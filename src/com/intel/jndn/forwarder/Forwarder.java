/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder;

import com.intel.jndn.forwarder.api.FaceInformationBase;
import com.intel.jndn.forwarder.api.PendingInterestTable;
import com.intel.jndn.forwarder.api.ContentStore;
import com.intel.jndn.forwarder.api.Face;
import com.intel.jndn.forwarder.api.FaceManager;
import com.intel.jnfd.deamon.face.FaceUri;
import com.intel.jndn.forwarder.api.ProtocolFactory;
import com.intel.jndn.forwarder.api.Strategy;
import com.intel.jndn.forwarder.api.StrategyChoiceTable;
import com.intel.jndn.forwarder.api.callbacks.OnCompleted;
import com.intel.jndn.forwarder.api.callbacks.OnDataReceived;
import com.intel.jndn.forwarder.api.callbacks.OnFailed;
import com.intel.jndn.forwarder.api.callbacks.OnInterestReceived;
import com.intel.jnfd.deamon.face.DefaultFaceManager;
import com.intel.jnfd.deamon.table.fib.FibEntry;
import com.intel.jnfd.deamon.table.pit.PitEntry;
import com.intel.jnfd.deamon.table.strategy.StrategyChoice;
import com.intel.jnfd.deamon.table.strategy.StrategyChoiceEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public class Forwarder implements Runnable, OnDataReceived, OnInterestReceived {

	private final ScheduledExecutorService pool;
	private final PendingInterestTable pit;
	private final FaceInformationBase fib;
	private final ContentStore cs;
	private final FaceManager fm;
	private final StrategyChoiceTable sct;

	public Forwarder() {
		this(Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()), null, null, null, new ArrayList(), new ArrayList());
	}

	public Forwarder(ScheduledExecutorService pool, PendingInterestTable pit, FaceInformationBase fib, ContentStore cs, List<ProtocolFactory> protocols, List<Strategy> strategies) {
		this.pool = pool;
		this.pit = pit;
		this.fib = fib;
		this.cs = cs;

		this.fm = new DefaultFaceManager(pool);
		for (ProtocolFactory protocolFactory : protocols) {
			fm.registerProtocol(protocolFactory);
		}

		this.sct = null; // new StrategyChoice(new BestRouteStrategy2(this, new Name("/")));
		for (Strategy strategy : strategies) {
			sct.install(strategy);
		}
	}

	@Override
	public void run() {
		while (true) {

		}
	}

	public void addNextHop(final Name prefix, FaceUri uri, final int cost, final OnCompleted<FibEntry> onCompleted, OnFailed onFailed) {
		createFace(uri, new OnCompleted<Face>() {
			@Override
			public void onCompleted(Face face) {
				FibEntry entry = fib.insert(prefix, face, cost);
				onCompleted.onCompleted(entry);
			}
		}, onFailed);
	}

	public void removeNextHop(Name name, FaceUri uri) {
		fib.remove(name);
	}

	public FibEntry[] listNextHops() {
		return fib.list();
	}

	public void setStrategy(Name prefix, Name strategy) {
		// TODO change strategies to accept Strategy, not Name
		sct.insert(prefix, strategy);
	}

	public void unsetStrategy(Name prefix) {
		sct.erase(prefix);
	}

	public Collection<StrategyChoiceEntry> listStrategies() {
		return sct.list();
	}

	public void createFace(FaceUri remoteUri, OnCompleted<Face> onFaceCreated, OnFailed onFaceCreationFailed) {
		FaceUri localUri = findApplicableChannelUri(remoteUri);
		fm.createFaceAndConnect(localUri, remoteUri, onFaceCreated, onFaceCreationFailed, this, this);
	}

	public void destroyFace(FaceUri remoteUri, OnCompleted<Face> onFaceDestroyed, OnFailed onFaceDestructionFailed) {
		FaceUri localUri = findApplicableChannelUri(remoteUri);
		fm.destroyFace(localUri, remoteUri, onFaceDestroyed, onFaceDestructionFailed);
	}

	protected FaceUri findApplicableChannelUri(FaceUri remoteUri) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public Collection<? extends Face> listFaces() {
		return fm.listFaces();
	}

	@Override
	public void onData(Data data, Face incomingFace) {
		List<PitEntry> matches = pit.findAllMatches(data);
		for (PitEntry entry : matches) {
			// TODO: satisfy interests here
		}

		cs.insert(data, matches.isEmpty());
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
