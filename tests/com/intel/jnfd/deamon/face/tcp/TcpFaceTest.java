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
package com.intel.jnfd.deamon.face.tcp;

import com.intel.jndn.forwarder.Forwarder;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.named_data.jndn.Data;
import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.Name.Component;
import net.named_data.jndn.OnData;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SecurityException;
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
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public class TcpFaceTest {

	private static final Logger logger = Logger.getLogger(TcpFaceTest.class.getName());
	public static final Name PREFIX = new Name("/test");
	private Face producer;
	private Face consumer;
	private Forwarder forwarder;

	@Before
	public void setUp() throws Exception {
		forwarder = new Forwarder();
		consumer = new Face();
		setupConsumer(consumer);
		producer = new Face();
		setupProducer(producer);

		Thread.sleep(2000);
	}

	@Test
	public void testSendInterest() throws Exception {

		producer.registerPrefix(PREFIX, new Producer(), null);

		int waitCount = 10;
		while (waitCount-- > 0) {
			producer.processEvents();
			Thread.sleep(50);
		}

		long totalInterests = 1000;
		long interestCount = 0;
		final AtomicLong dataCount = new AtomicLong(0);
		final Set<Component> sent = new HashSet();
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
//		Thread thread = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					while (true) {
//						faceA.processEvents();
//					}
//				} catch (IOException | EncodingException ex) {
//					logger.log(Level.SEVERE, "Failed to process events", ex);
//				}
//			}
//		});
//		thread.setName("consumer");
//		thread.start();
	}

	private void setupProducer(final Face faceB) throws SecurityException {
		KeyChain keyChain = configure(new Name("/producer"));
		faceB.setCommandSigningInfo(keyChain, keyChain.getDefaultCertificateName());

//		Thread thread = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					while (true) {
//						faceB.processEvents();
//					}
//				} catch (IOException | EncodingException ex) {
//					logger.log(Level.SEVERE, "Failed to process events", ex);
//				}
//			}
//		});
//		thread.setName("producer");
//		thread.start();
	}

	public static KeyChain configure(Name name) throws net.named_data.jndn.security.SecurityException {
		// access key chain in ~/.ndn; create if necessary 
		PrivateKeyStorage keyStorage = new MemoryPrivateKeyStorage();
		IdentityStorage identityStorage = new MemoryIdentityStorage();
		KeyChain keyChain = new KeyChain(new IdentityManager(identityStorage, keyStorage),
				new SelfVerifyPolicyManager(identityStorage));

		// create keys, certs if necessary
		if (!identityStorage.doesIdentityExist(name)) {
			Name keyName = keyChain.createIdentity(name);
			keyChain.setDefaultKeyForIdentity(keyName, name);
		}

		// set default identity
		keyChain.getIdentityManager().setDefaultIdentity(name);

		return keyChain;
	}

	private String componentsToUri(Set<Component> sent) {
		StringBuilder sb = new StringBuilder("[");
		for (Component c : sent) {
			sb.append(c.toEscapedString() + ", ");
		}
		sb.append("]");
		return sb.toString();
	}
}
