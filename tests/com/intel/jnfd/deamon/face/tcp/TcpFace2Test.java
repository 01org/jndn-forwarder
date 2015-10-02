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

import com.intel.jndn.forwarder.TestCounter;
import com.intel.jndn.forwarder.api.Face;
import com.intel.jndn.forwarder.api.callbacks.OnDataReceived;
import com.intel.jndn.forwarder.api.callbacks.OnInterestReceived;
import com.intel.jnfd.deamon.face.FaceUri;
import com.intel.jnfd.deamon.face.ParseFaceUriException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public class TcpFace2Test {

	private static final Logger logger = Logger.getLogger(TcpFace2Test.class.getName());
	private static final int NUM_PACKETS = 10000;

	@Test
	public void testOrderingOfManyInterests() throws IOException, ParseFaceUriException, InterruptedException {
		final FaceUri local = new FaceUri("tcp4://0.0.0.0:6363");
		final TestCounter counter = new TestCounter();
		final OnDataReceived onDataReceived = new OnDataReceived() {
			@Override
			public void onData(Data data, Face incomingFace) {
				logger.log(Level.INFO, "Received data: {0}", data.getName().toUri());
			}
		};
		final OnInterestReceived onInterestReceived = new OnInterestReceived() {
			@Override
			public void onInterest(Interest interest, Face face) {
				logger.log(Level.INFO, "Received interest: {0}", interest.toUri());
				counter.count++;
			}
		};

		listenAndCreateFace(local, onDataReceived, onInterestReceived);

		Socket socket = new Socket();
		socket.connect(new InetSocketAddress("localhost", 6363));
		for (int i = 0; i < NUM_PACKETS; i++) {
			Interest interest = new Interest(new Name("/a/b/c").appendSegment(i));
			byte[] bytes = interest.wireEncode().getImmutableArray();
			socket.getOutputStream().write(bytes);
			logger.log(Level.INFO, "Wrote bytes, packet #{0}", new Object[]{i});
		}
		socket.close();

		waitAtLeast(counter, NUM_PACKETS, NUM_PACKETS * 10);
		assertEquals(NUM_PACKETS, counter.count);
	}

	private void listenAndCreateFace(final FaceUri local, final OnDataReceived onDataReceived, final OnInterestReceived onInterestReceived) throws ParseFaceUriException, IOException, UnknownHostException, InterruptedException {
		final ExecutorService pool = Executors.newFixedThreadPool(4);
		AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(pool);
		final FaceUri remote = new FaceUri("tcp4://0.0.0.0:6363");
		InetSocketAddress localAddress = new InetSocketAddress(local.getInet(), local.getPort());
		AsynchronousServerSocketChannel channel = AsynchronousServerSocketChannel.open(group);
		channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		channel.bind(localAddress);
		channel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
			@Override
			public void completed(AsynchronousSocketChannel result, Void attachment) {
				logger.log(Level.INFO, "Bound to new socket: " + local);
				TcpFace2 face = new TcpFace2(local, remote, pool, result, onDataReceived, onInterestReceived);
			}

			@Override
			public void failed(Throwable exc, Void attachment) {
				Assert.fail("Failed to bind to local socket: " + local);
			}
		});
		Thread.sleep(2000);
	}
	
	private void waitAtLeast(TestCounter counter, int expectedCount, long maxWaitMilliseconds) throws InterruptedException{
		logger.info("Waiting for count to reach: " + expectedCount);
		long end = System.currentTimeMillis() + maxWaitMilliseconds;
		while(counter.count < expectedCount && System.currentTimeMillis() < end){
			Thread.sleep(20);
		}
	}
}
