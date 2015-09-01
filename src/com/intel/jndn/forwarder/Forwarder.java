/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder;

import com.intel.jndn.forwarder.api.Channel;
import com.intel.jndn.forwarder.api.FaceInformationBase;
import com.intel.jndn.forwarder.api.PendingInterestTable;
import com.intel.jndn.forwarder.api.ContentStore;
import com.intel.jndn.forwarder.api.Face;
import com.intel.jndn.forwarder.api.FaceManager;
import com.intel.jnfd.deamon.face.FaceUri;
import com.intel.jndn.forwarder.api.ProtocolFactory;
import com.intel.jndn.forwarder.api.Strategy;
import com.intel.jndn.forwarder.api.callbacks.OnCompleted;
import com.intel.jndn.forwarder.api.callbacks.OnDataReceived;
import com.intel.jndn.forwarder.api.callbacks.OnFailed;
import com.intel.jndn.forwarder.api.callbacks.OnInterestReceived;
import com.intel.jnfd.deamon.face.DefaultFaceManager;
import com.intel.jnfd.deamon.fw.ForwardingPipeline;
import com.intel.jnfd.deamon.table.Pair;
import com.intel.jnfd.deamon.table.fib.FibEntry;
import com.intel.jnfd.deamon.table.pit.PitEntry;
import com.intel.jnfd.deamon.table.strategy.StrategyChoiceEntry;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public class Forwarder implements Runnable, OnDataReceived, OnInterestReceived {

    private final ScheduledExecutorService pool;
    private final ForwardingPipeline pipeline;
    private final FaceManager faceManager;
    private final Logger logger = Logger.getLogger(Forwarder.class.getName());

    private OnCompleted<Channel> onChannelCreated;
    private OnFailed onChannelCreationFailed;
    private OnDataReceived onDataReceived;
    private OnInterestReceived onInterestReceived;

    private void initialize() {
        onChannelCreated = new OnCompleted<Channel>() {

            @Override
            public void onCompleted(Channel result) {

            }

        };

        onChannelCreationFailed = new OnFailed() {

            @Override
            public void onFailed(Throwable error) {
                logger.log(Level.SEVERE, null, error);
            }

        };

        onDataReceived = new OnDataReceived() {

            @Override
            public void onData(Data data, Face incomingFace) {
                pipeline.onData(incomingFace, data);
            }

        };

        onInterestReceived = new OnInterestReceived() {

            @Override
            public void onInterest(Interest interest, Face face) {
                try {
                    pipeline.onInterest(face, interest);
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }

        };
    }

    public Forwarder() {

        pool = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        pipeline = new ForwardingPipeline(pool);
        initialize();

        faceManager = new DefaultFaceManager(pool, onChannelCreated,
                onChannelCreationFailed, onDataReceived, onInterestReceived);
    }

    /**
     * If new {@link ScheduledExecutorService}, {@link PendingInterestTable},
     * {@link FaceInformationBase} and {@link ContentStore} need to be tested,
     * use this constructor.
     *
     * @param pool
     * @param pit
     * @param fib
     * @param cs
     */
    public Forwarder(ScheduledExecutorService pool, PendingInterestTable pit,
            FaceInformationBase fib, ContentStore cs) {
        this.pool = pool;
        pipeline = new ForwardingPipeline(pool);
        initialize();
        faceManager = new DefaultFaceManager(pool, onChannelCreated,
                onChannelCreationFailed, onDataReceived, onInterestReceived);
        pipeline.setPit(pit);
        pipeline.setFib(fib);
        pipeline.setCs(cs);
    }

    /**
     * New {@link protocolFactory} can be dynamically registered using this
     * method.
     *
     * @param protocolFactory
     */
    public void registerProtocol(ProtocolFactory protocolFactory) {
        faceManager.registerProtocol(protocolFactory);
    }

    /**
     * New {@link Strategy} can be dynamically registered using this method.
     *
     * @param strategy
     */
    public void installStrategies(Strategy strategy) {
        pipeline.getStrategyChoice().install(strategy);
    }

    @Override
    public void run() {
        while (true) {

        }
    }

    public void addNextHop(final Name prefix, FaceUri uri, final int cost,
            final OnCompleted<FibEntry> onCompleted, OnFailed onFailed) {
        // in the createFace method, the Face should be checked first, if exists,
        // just use that one, if not, create a new one.
        createFace(uri, new OnCompleted<Face>() {
            @Override
            public void onCompleted(Face face) {
                Pair<FibEntry> entry = pipeline.getFib().insert(prefix, face, cost);
                onCompleted.onCompleted(entry.getFirst());
            }
        }, onFailed);
    }

    public void removeNextHop(Name name, FaceUri uri) {
        pipeline.getFib().remove(name);
    }

    public Collection<FibEntry> listNextHops() {
        return pipeline.getFib().list();
    }

    /**
     * set the Strategy used by a specific name prefix. Notice, the Strategy
     * should be installed first.
     *
     * @param prefix
     * @param strategy
     */
    public void setStrategy(Name prefix, Name strategy) {
        pipeline.getStrategyChoice().insert(prefix, strategy);
    }

    public void unsetStrategy(Name prefix) {
        pipeline.getStrategyChoice().erase(prefix);
    }

    public Collection<StrategyChoiceEntry> listStrategies() {
        return pipeline.getStrategyChoice().list();
    }

    public void createFace(FaceUri remoteUri, OnCompleted<Face> onFaceCreated, OnFailed onFaceCreationFailed) {
        faceManager.createFaceAndConnect(remoteUri, onFaceCreated, onFaceCreationFailed, this, this);
    }

    public void destroyFace(Face face, OnCompleted<Face> onFaceDestroyed, OnFailed onFaceDestructionFailed) {
        faceManager.destroyFace(face, onFaceDestroyed, onFaceDestructionFailed);
    }

    public Collection<? extends Face> listFaces() {
        return faceManager.listFaces();
    }

    @Override
    public void onData(Data data, Face incomingFace) {
        List<PitEntry> matches = pipeline.getPit().findAllMatches(data);
        for (PitEntry entry : matches) {
            // TODO: satisfy interests here
        }

        pipeline.getCs().insert(data, matches.isEmpty());
    }

    @Override
    public void onInterest(Interest interest, final Face face) {
//		try {
//			cs.find(interest, new SearchCsCallback() {
//
//				@Override
//				public void hitCallback(Interest interest, Data data) {
//					try {
//						// TODO erase PIT entries
//						face.sendData(data);
//					} catch (IOException ex) {
//						// TODO push exception up appropriately
//						throw new RuntimeException(ex);
//					}
//				}
//
//				@Override
//				public void missCallback(Interest interest) {
//					pit.insert(interest);
//
//					for (Face face : sct.findEffectiveStrategy(interest.getName()).determineOutgoingFaces(interest, Forwarder.this)) {
//						// TODO check nonces for loops
//						try {
//							face.sendInterest(interest);
//						} catch (IOException ex) {
//							// TODO push exception up appropriately
//							throw new RuntimeException(ex);
//						}
//					}
//				}
//			});
//		} catch (Exception ex) {
//			// TODO push exception up appropriately
//			throw new RuntimeException(ex);
//		}
    }
}
