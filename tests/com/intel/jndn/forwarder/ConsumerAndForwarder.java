/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
