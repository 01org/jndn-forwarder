/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder;

import com.intel.jndn.forwarder.api.FaceInformationBase;
import com.intel.jndn.forwarder.api.PendingInterestTable;
import com.intel.jndn.forwarder.api.ContentStore;
import com.intel.jndn.forwarder.api.callbacks.OnFaceConnected;
import com.intel.jndn.forwarder.api.callbacks.OnFaceConnectionFailed;
import com.intel.jnfd.deamon.face.Face;
import com.intel.jnfd.deamon.face.FaceUri;
import com.intel.jnfd.deamon.face.ProtocolFactory;
import com.intel.jnfd.deamon.fw.BestRouteStrategy;
import com.intel.jndn.forwarder.api.Strategy;
import com.intel.jnfd.deamon.table.fib.FibEntry;
import com.intel.jnfd.deamon.table.strategy.StrategyChoice;
import com.intel.jnfd.deamon.table.strategy.StrategyChoiceEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
	private final List<ProtocolFactory> protocols;
	private final List<Face> faces;
	private final StrategyChoice strategies;

	public Forwarder() {
		this(Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()), null, null, null, new ArrayList(), new StrategyChoice(new BestRouteStrategy()));
	}

	public Forwarder(ScheduledExecutorService pool, PendingInterestTable pit, FaceInformationBase fib, ContentStore cs, List<ProtocolFactory> protocols, StrategyChoice strategies) {
		this.pool = pool;
		this.pit = pit;
		this.fib = fib;
		this.cs = cs;
		this.protocols = protocols;
		this.faces = new ArrayList();
		this.strategies = strategies;
	}

	@Override
	public void run() {
		while (true) {

		}
	}

	public FibEntry addNextHop(Name name, FaceUri uri, int cost) {
		return fib.insert(name, uri, cost);
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

	public Face createFace(FaceUri uri, OnFaceConnected onFaceConnected, OnFaceConnectionFailed onFaceConnectionFailed) {
		for (ProtocolFactory factory : protocols) {
			if (factory.scheme().equals(uri.getScheme())) {
				return factory.createFace(uri, onFaceConnected, onFaceConnectionFailed);
			}
		}
		throw new IllegalArgumentException("No protocol found for uri: " + uri);
	}

	public Face destroyFace(FaceUri uri) {
		protocols.
	}

	public List<Face> listFaces() {
		return Collections.unmodifiableList(faces);
	}
}
