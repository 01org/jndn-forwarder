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

import com.intel.jndn.forwarder.api.callbacks.OnCompleted;
import com.intel.jnfd.deamon.face.FaceUri;
import static com.intel.jnfd.deamon.face.tcp.TcpFaceTest.configure;
import com.intel.jnfd.deamon.table.fib.FibEntry;
import net.named_data.jndn.Face;
import net.named_data.jndn.Name;
import net.named_data.jndn.security.KeyChain;
import org.junit.Before;

/**
 *
 * @author Haitao Zhang <zhtaoxiang@gmail.com>
 */
public class ProducerAndForwarder {

	public static final Name PREFIX = new Name("/test");
	private Face producer;
	private Forwarder forwarder;

	@Before
	public void setUp() throws Exception {
		forwarder = new Forwarder();
		forwarder.addNextHop(PREFIX, new FaceUri(""), 0, new OnCompleted<FibEntry>() {

			@Override
			public void onCompleted(FibEntry result) {

			}

		});
		producer = new Face();
		setupProducer(producer);

		Thread.sleep(500);
	}

	private void setupProducer(final Face faceB) throws net.named_data.jndn.security.SecurityException {
		KeyChain keyChain = configure(new Name("/producer"));
		faceB.setCommandSigningInfo(keyChain, keyChain.getDefaultCertificateName());
	}
}
