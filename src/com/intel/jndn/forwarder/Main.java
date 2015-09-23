/*
 * jndn-forwarder
 * Copyright (c) 2015, Intel Corporation.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms and conditions of the GNU Lesser General Public License,
 * version 3, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
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
 * @author Haitao Zhang <zhtaoxiang@gmail.com>
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
