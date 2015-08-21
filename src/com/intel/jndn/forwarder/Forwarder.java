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
import com.intel.jnfd.deamon.fw.BestRouteStrategy;
import com.intel.jndn.forwarder.api.Strategy;
import com.intel.jndn.forwarder.api.callbacks.OnCompleted;
import com.intel.jndn.forwarder.api.callbacks.OnFailed;
import com.intel.jnfd.deamon.face.DefaultFaceManager;
import com.intel.jnfd.deamon.table.fib.FibEntry;
import com.intel.jnfd.deamon.table.strategy.StrategyChoice;
import com.intel.jnfd.deamon.table.strategy.StrategyChoiceEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import net.named_data.jndn.Name;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public class Forwarder implements Runnable {

	private final ScheduledExecutorService pool;
	private final PendingInterestTable pit;
	private final FaceInformationBase fib;
	private final ContentStore cs;
	private final FaceManager fm;
	private final StrategyChoice strategies;

	public Forwarder() {
		this(Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()), null, null, null, new ArrayList(), new StrategyChoice(new BestRouteStrategy(null, new Name())));
	}

	public Forwarder(ScheduledExecutorService pool, PendingInterestTable pit, FaceInformationBase fib, ContentStore cs, List<ProtocolFactory> protocols, StrategyChoice strategies) {
		this.pool = pool;
		this.pit = pit;
		this.fib = fib;
		this.cs = cs;
		this.fm = new DefaultFaceManager(pool);
		this.strategies = strategies;
	}

	@Override
	public void run() {
		while (true) {

		}
	}

	public void addNextHop(final Name prefix, FaceUri uri, final int cost, final OnCompleted<FibEntry> onCompleted, OnFailed onFailed) {
		fm.createFace(uri, new OnCompleted<Face>() {
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

	public void setStrategy(Name name, Strategy strategy) {
		// TODO change strategies to accept Strategy, not Name
		strategies.insert(name, strategy.getName());
	}

	public void unsetStrategy(Name name) {
		strategies.erase(name);
	}

	public Collection<StrategyChoiceEntry> listStrategies() {
		return strategies.list();
	}

	public void createFace(FaceUri uri, OnCompleted<Face> onFaceCreated, OnFailed onFaceCreationFailed) {
		fm.createFace(uri, onFaceCreated, onFaceCreationFailed);
	}

	public void destroyFace(FaceUri uri, OnCompleted<Face> onFaceDestroyed, OnFailed onFaceDestructionFailed) {
		fm.destroyFace(uri, onFaceDestroyed, onFaceDestructionFailed);
	}

	public List<Face> listFaces() {
		return fm.listFaces();
	}
}
