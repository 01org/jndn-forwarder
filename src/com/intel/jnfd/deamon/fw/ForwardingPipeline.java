/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.fw;

import com.intel.jndn.forwarder.api.ContentStore;
import com.intel.jndn.forwarder.api.Face;
import com.intel.jndn.forwarder.api.Strategy;
import com.intel.jnfd.deamon.table.cs.SearchCsCallback;
import com.intel.jnfd.deamon.table.cs.SortedSetCs;
import com.intel.jnfd.deamon.table.deadnonce.DeadNonce;
import com.intel.jnfd.deamon.table.deadnonce.DeadNonceNaive;
import com.intel.jnfd.deamon.table.fib.Fib;
import com.intel.jnfd.deamon.table.fib.FibEntry;
import com.intel.jnfd.deamon.table.measurement.Measurement;
import com.intel.jnfd.deamon.table.pit.Pit;
import com.intel.jnfd.deamon.table.pit.PitEntry;
import com.intel.jnfd.deamon.table.pit.PitInRecord;
import com.intel.jnfd.deamon.table.pit.PitOutRecord;
import com.intel.jnfd.deamon.table.strategy.StrategyChoice;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.util.Blob;

/**
 *
 * @author zht
 */
public class ForwardingPipeline implements SearchCsCallback {

    public static final Name LOCALHOST_NAME = new Name("ndn:/localhost");

    public ForwardingPipeline() {
        faceTable = new FaceTable(this);
        pit = new Pit();
        fib = new Fib();
		cs = new SortedSetCs();
        measurement = new Measurement();
        strategyChoice = new StrategyChoice(new BestRouteStrategy2(this));
        // install more strategies into strategyChoice here
        deadNonceList = new DeadNonceNaive();

        //TODO: find a right way to initialize the shcheduler
        scheduler = null;
    }

    public FaceTable getFaceTable() {
        return faceTable;
    }

    public Face getFace(int faceId) {
        return faceTable.get(faceId);
    }

    public void addFace(Face face) {
        faceTable.add(face);
    }

    public void onInterest(Face face, Interest interest) throws Exception {
        onIncomingInterest(face, interest);
    }

    public void onData(Face face, Data data) {
        onIncomingData(face, data);
    }

    public Fib getFib() {
        return fib;
    }

    public Pit getPit() {
        return pit;
    }

    public ContentStore getCs() {
        return cs;
    }

    public Measurement getMeasurement() {
        return measurement;
    }

    public StrategyChoice getStrategyChoice() {
        return strategyChoice;
    }

    public DeadNonce getDeadNonceList() {
        return deadNonceList;
    }

    /**
     * incoming Interest pipeline
     *
     * @param inFace
     * @param interest
     */
    private void onIncomingInterest(Face inFace, Interest interest)
            throws Exception {
// TODO: in the c++ code, they set the incoming FaceId, but jndn does
// not provide similiar function. Need to find a solution
// interest.setIncomingFaceId();

        // /localhost scope control
        boolean isViolatingLocalhost = !inFace.isLocal()
                && LOCALHOST_NAME.match(interest.getName());
        if (isViolatingLocalhost) {
            return;
        }

        // PIT insert
        PitEntry pitEntry = pit.insert(interest).getFirst();

        // detect duplicate Nonce
        int dnw = pitEntry.findNonce(interest.getNonce(), inFace);
        boolean hasDuplicateNonce = (dnw != PitEntry.DUPLICATE_NONCE_NONE)
                || deadNonceList.find(interest.getName(), interest.getNonce());
        if (hasDuplicateNonce) {
            // goto Interest loop pipeline
            onInterestLoop(inFace, interest, pitEntry);
            return;
        }

        // cancel unsatisfy & straggler timer
        cancelUnsatisfyAndStragglerTimer(pitEntry);

        // is pending?
        List<PitInRecord> inRecords = pitEntry.getInRecords();
        if (inRecords == null || inRecords.isEmpty()) {
            cs.find(inFace, pitEntry, interest, this);
        } else {
            onContentStoreMiss(inFace, pitEntry, interest);
        }
    }

    /**
     *
     * Interest loop pipeline
     *
     * @param inFace
     * @param interest
     * @param pitEntry
     */
    private void onInterestLoop(Face inFace, Interest interest,
            PitEntry pitEntry) {
        // Do nothing

        // TODO: drop the interest. Since the c++ code hasn't implemented this 
        // method, we will also omit it here
    }

    /**
     * outgoing Interest pipeline
     *
     * @param pitEntry
     * @param outFace
     * @param wantNewNonce
     */
    public void onOutgoingInterest(PitEntry pitEntry, Face outFace,
            boolean wantNewNonce) {
        if (outFace.getFaceId() == FaceTable.INVALID_FACEID) {
            return;
        }

        // scope control
        if (pitEntry.violatesScope(outFace)) {
            return;
        }

        // pick Interest
        List<PitInRecord> inRecords = pitEntry.getInRecords();
        if (inRecords == null || inRecords.isEmpty()) {
            return;
        }
        long smallestLastRenewed = Long.MAX_VALUE;
        boolean smallestIsOutFace = true;
        PitInRecord pickedInRecord = null;
        for (PitInRecord one : inRecords) {
            boolean currentIsOutFace = one.getFace().equals(outFace);
            if (!smallestIsOutFace && currentIsOutFace) {
                continue;
            }
            if (smallestIsOutFace && !currentIsOutFace) {
                smallestLastRenewed = one.getLastRenewed();
                smallestIsOutFace = currentIsOutFace;
                pickedInRecord = one;
                continue;
            }
            if (smallestLastRenewed > one.getLastRenewed()) {
                smallestLastRenewed = one.getLastRenewed();
                smallestIsOutFace = currentIsOutFace;
                pickedInRecord = one;
            }
        }
        if (pickedInRecord == null) {
            return;
        }
        Interest interest = pickedInRecord.getInterest();
        if (wantNewNonce) {
            //generate and set new nonce
            Random randomGenerator = new Random();
            byte bytes[] = new byte[4];
            randomGenerator.nextBytes(bytes);
            // notice that this is right, if we set the nonce first, the jndn
            // library will not change the nonce
            interest.setNonce(new Blob(bytes));
        }

        // insert OutRecord
        pitEntry.insertOrUpdateInRecord(outFace, interest);

		try {
			// send Interest
			outFace.sendInterest(interest);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
		}
    }

    /**
     * Interest reject pipeline
     *
     * @param pitEntry
     */
    public void onInterestReject(PitEntry pitEntry) {

        if (pitEntry.hasUnexpiredOutRecords()) {
            return;
        }

        // cancel unsatisfy & straggler timer
        cancelUnsatisfyAndStragglerTimer(pitEntry);

        // set PIT straggler timer
        setStragglerTimer(pitEntry, false, -1);
    }

    /**
     * Interest unsatisfied pipeline
     *
     * @param pitEntry
     */
    private void onInterestUnsatisfied(PitEntry pitEntry) {
        Strategy effectiveStrategy
                = strategyChoice.findEffectiveStrategy(pitEntry.getName());
        effectiveStrategy.beforeExpirePendingInterest(pitEntry);

        // goto Interest Finalize pipeline
        onInterestFinalize(pitEntry, false, -1);
    }

    /**
     * Interest finalize pipeline
     *
     * @param pitEntry
     * @param isSatisfied
     * @param dataFreshnessPeriod
     */
    private void onInterestFinalize(PitEntry pitEntry, boolean isSatisfied,
            long dataFreshnessPeriod) {
        // Dead Nonce List insert if necessary
        insertDeadNonceList(pitEntry, isSatisfied, dataFreshnessPeriod, null);

        // PIT delete
        cancelUnsatisfyAndStragglerTimer(pitEntry);
        pit.erase(pitEntry);
    }

    /**
     * Content Store miss pipeline
     *
     * @param inFace
     * @param pitEntry
     * @param interest
     */
    @Override
    public void onContentStoreMiss(Face inFace, PitEntry pitEntry,
            Interest interest) {
        // insert InRecord
        pitEntry.insertOrUpdateInRecord(inFace, interest);
        // set PIT unsatisfy timer
        setUnsatisfyTimer(pitEntry);
        // FIB lookup
        FibEntry fibEntry = fib.findLongestPrefixMatch(pitEntry.getName());
        // dispatch to strategy
        Strategy effectiveStrategy
                = strategyChoice.findEffectiveStrategy(pitEntry.getName());
        effectiveStrategy.afterReceiveInterest(inFace, interest, fibEntry,
                pitEntry);
    }

    /**
     * Content Store hit pipeline
     *
     * @param inFace
     * @param pitEntry
     * @param interest
     * @param data
     */
    @Override
    public void onContentStoreHit(Face inFace, PitEntry pitEntry,
            Interest interest, Data data) {
// TODO: in the c++ code, they set the incoming FaceId, but jndn does
// not provide similiar function. Need to find a solution
// data.setIncomingFaceId();
        // set PIT straggler timer
        setStragglerTimer(pitEntry, true,
                (long) Math.round(data.getMetaInfo().getFreshnessPeriod()));
        // goto outgoing Data pipeline
        onOutgoingData(data, inFace);
    }

    /**
     * incoming Data pipeline
     *
     * @param inFace
     * @param data
     */
    private void onIncomingData(Face inFace, Data data) {

    }

    /**
     * Data unsolicited pipeline
     *
     * @param inFace
     * @param data
     */
    private void onDataUnsolicited(Face inFace, Data data) {

    }

    /**
     * outgoing Data pipeline
     *
     * @param data
     * @param outFace
     */
    private void onOutgoingData(Data data, Face outFace) {

    }

    private void setUnsatisfyTimer(final PitEntry pitEntry) {
        List<PitInRecord> inRecords = pitEntry.getInRecords();
        long lastExpiry = 0;
        for (PitInRecord one : inRecords) {
            if (lastExpiry < one.getExpiry()) {
                lastExpiry = one.getExpiry();
            }
        }
        long lastExpiryFromNow = lastExpiry - System.currentTimeMillis();
        if (lastExpiryFromNow < 0) {
            // TODO: this message is copied from c++ code
            // all InRecords are already expired; will this happen?
        }

        pitEntry.unsatisfyTimer.cancel(false);
        pitEntry.unsatisfyTimer = scheduler.schedule(new Runnable() {

            @Override
            public void run() {
                // TODO: make sure if we need to change the pointer pitEntry or 
                // not.
                // Since it is an inner class, we cannot change the pointer.
                onInterestUnsatisfied(pitEntry);
            }
        },
                lastExpiryFromNow, TimeUnit.MILLISECONDS);
    }

    private void setStragglerTimer(final PitEntry pitEntry,
            final boolean isSatisfied, final long dataFreshnessPeriod) {
        long stragglerTime = 100;
        pitEntry.stragglerTimer.cancel(false);
        pitEntry.stragglerTimer = scheduler.schedule(new Runnable() {

            @Override
            public void run() {
                onInterestFinalize(pitEntry, isSatisfied, dataFreshnessPeriod);
            }

        },
                stragglerTime, TimeUnit.MILLISECONDS);
    }

    private void cancelUnsatisfyAndStragglerTimer(PitEntry pitEntry) {
        pitEntry.unsatisfyTimer.cancel(false);
        pitEntry.stragglerTimer.cancel(false);
    }

    private void insertDeadNonceList(PitEntry pitEntry, boolean isSatisfied,
            long dataFreshnessPeriod, Face upstream) {
        // need Dead Nonce List insert?
        boolean needDnl = false;
        if (isSatisfied) {
            boolean hasFreshnessPeriod = dataFreshnessPeriod >= 0;
            // Data never becomes stale if it doesn't have FreshnessPeriod field
            needDnl = pitEntry.getInterest().getMustBeFresh()
                    && (hasFreshnessPeriod
                    && dataFreshnessPeriod < deadNonceList.getLifetime());
        } else {
            needDnl = true;
        }

        if (!needDnl) {
            return;
        }

        // Dead Nonce List insert
        if (upstream == null) {
            // insert all outgoing Nonces
            List<PitOutRecord> outRecords = pitEntry.getOutRecords();
            for (PitOutRecord one : outRecords) {
                deadNonceList.add(pitEntry.getName(), one.getLastNonce());
            }
        } else {
            // insert outgoing Nonce of a specific face
            PitOutRecord outRecord = pitEntry.getOutRecord(upstream);
            if (outRecord != null) {
                deadNonceList.add(pitEntry.getName(), outRecord.getLastNonce());
            }
        }
    }

    //    private void dispatchToStrategy(PitEntry pitEntry, Trigger trigger) {
    //        Strategy effectiveStrategy
    //                = strategyChoice.findEffectiveStrategy(pitEntry.getName());
    //        trigger.trigger(effectiveStrategy);
    //    }
	private static final Logger logger = Logger.getLogger(ForwardingPipeline.class.getName());
    private final FaceTable faceTable;
    private final Fib fib;
    private final Pit pit;
    private final ContentStore cs;
    private final Measurement measurement;
    private final StrategyChoice strategyChoice;
    private final DeadNonce deadNonceList;

    private final ScheduledExecutorService scheduler;
}
