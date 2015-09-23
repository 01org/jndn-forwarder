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

import net.named_data.jndn.Face;
import net.named_data.jndn.Name;
import org.junit.Before;

/**
 *
 * @author Haitao Zhang <zhtaoxiang@gmail.com>
 */
public class ConsumerAndForwarder {

	public static final Name PREFIX = new Name("/test");
	private Face consumer;
	private Forwarder forwarder;

	@Before
	public void setUp() throws Exception {
		forwarder = new Forwarder();
		consumer = new Face();
		setupConsumer(consumer);

		Thread.sleep(500);
	}

	private void setupConsumer(final Face faceA) {

	}
}
