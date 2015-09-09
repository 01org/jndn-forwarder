/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face.tcp;

import com.intel.jndn.forwarder.Forwarder;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.named_data.jndn.Data;
import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.OnData;
import net.named_data.jndn.encoding.EncodingException;
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

		Thread.sleep(500);
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
		while (interestCount++ < totalInterests) {
			Interest interest = new Interest(new Name(PREFIX).appendSegment(interestCount));
			System.out.println("Interest sent: " + interest.toUri());
			consumer.expressInterest(interest, new OnData() {
				@Override
				public void onData(Interest interest, Data data) {
					System.out.println("Data received: " + data.getName().toUri());
					dataCount.incrementAndGet();
				}
			});
			
			consumer.processEvents();
			
			producer.processEvents();
			
			Thread.sleep(20);
		}

//		while (dataCount.get() < totalInterests) {
//			logger.info("Looping at: " + dataCount.get());
//		}

		assertEquals(totalInterests, dataCount.get());
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
}
