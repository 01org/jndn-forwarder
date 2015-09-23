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
import com.intel.jnfd.deamon.face.tcp.Producer;
import com.intel.jnfd.deamon.table.fib.FibEntry;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.named_data.jndn.Data;
import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.OnData;
import net.named_data.jndn.encoding.WireFormat;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.identity.IdentityManager;
import net.named_data.jndn.security.identity.IdentityStorage;
import net.named_data.jndn.security.identity.MemoryIdentityStorage;
import net.named_data.jndn.security.identity.MemoryPrivateKeyStorage;
import net.named_data.jndn.security.identity.PrivateKeyStorage;
import net.named_data.jndn.security.policy.SelfVerifyPolicyManager;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author zht
 */
public class TestRemoteForwarder {

	private static final Logger logger = Logger.getLogger(com.intel.jnfd.deamon.face.tcp.TcpFaceTest.class.getName());
	public static final Name PREFIX = new Name("/haitao/test");
	private Face producer;
	private Face consumer;
	private Forwarder forwarder;

	@Before
	public void setUp() throws Exception {
		WireFormat defaultWireFormat = WireFormat.getDefaultWireFormat();

		forwarder = new Forwarder();
		forwarder.addNextHop(PREFIX, new FaceUri("tcp4://10.54.12.170:6363"),
				0, new OnCompleted<FibEntry>() {

					@Override
					public void onCompleted(FibEntry result) {
						System.out.println("add route successfully");
					}

				});

		consumer = new Face();
		setupConsumer(consumer);

		producer = new Face("ndn-lab2.jf.intel.com");
		setupProducer(producer);

		Thread.sleep(500);
	}

	@Test
	public void testSendInterest() throws Exception {

		producer.registerPrefix(new Name(PREFIX), new Producer(), null);

		int waitCount = 10;
		while (waitCount-- > 0) {
			producer.processEvents();
			Thread.sleep(50);
		}

		long totalInterests = 1000;
		long interestCount = 0;
		final AtomicLong dataCount = new AtomicLong(0);
		final Set<Name.Component> sent = new HashSet();
		while (interestCount++ < totalInterests) {
			Interest interest = new Interest(new Name(PREFIX).appendSegment(interestCount));
			interest.setInterestLifetimeMilliseconds(20000);
			sent.add(new Name().appendSegment(interestCount).get(0));
			System.out.println("Interest sent: " + interest.toUri());
			consumer.expressInterest(interest, new OnData() {
				@Override
				public void onData(Interest interest, Data data) {

					System.out.println("Data received: " + data.getName().toUri());
					sent.remove(data.getName().get(-2));
					dataCount.incrementAndGet();

				}
			});

			consumer.processEvents();
			producer.processEvents();
//			Thread.sleep(50);
		}

		waitCount = 1000;
		while (waitCount-- > 0) {
			consumer.processEvents();
			producer.processEvents();
			Thread.sleep(10);
		}

		logger.log(Level.INFO, "Datas received: {0}", dataCount.get());
		logger.log(Level.INFO, "Missing interests: {0}", componentsToUri(sent));
		assertEquals(totalInterests, dataCount.get());
		forwarder.stop();

	}

	private void setupConsumer(final Face faceA) {
	}

	private void setupProducer(final Face faceB) throws net.named_data.jndn.security.SecurityException {
		MemoryIdentityStorage identityStorage = new MemoryIdentityStorage();
		MemoryPrivateKeyStorage privateKeyStorage
				= new MemoryPrivateKeyStorage();
		KeyChain keyChain = new KeyChain(
				new IdentityManager(identityStorage, privateKeyStorage),
				new SelfVerifyPolicyManager(identityStorage));
		Name identityName = new Name("/haitao/test");
		Name keyName = keyChain.generateRSAKeyPairAsDefault(identityName);
		Name certificateName = keyName.getSubName(0, keyName.size() - 1)
				.append("KEY").append(keyName.get(-1)).append("ID-CERT")
				.append("0");

		faceB.setCommandSigningInfo(keyChain, certificateName);
	}

	private String componentsToUri(Set<Name.Component> sent) {
		StringBuilder sb = new StringBuilder("[");
		for (Name.Component c : sent) {
			sb.append(c.toEscapedString() + ", ");
		}
		sb.append("]");
		return sb.toString();
	}
}
