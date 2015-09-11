/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder;

import com.intel.jndn.forwarder.impl.ImplementationLoader;
import com.intel.jndn.forwarder.api.ProtocolFactory;
import com.intel.jndn.forwarder.api.Strategy;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import net.named_data.jndn.encoding.EncodingException;

/**
 *
 * @author zht
 */
public class Main {

	public static void main(String[] args) throws IOException, EncodingException {
//		ScheduledExecutorService pool = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
//		List<ProtocolFactory> protocols = ImplementationLoader.load(ProtocolFactory.class);
//		List<Strategy> strategies = ImplementationLoader.load(Strategy.class);
		// TODO pass these in to the forwarder
		
		Forwarder forwarder = new Forwarder();
		forwarder.run();
                
	}
}
