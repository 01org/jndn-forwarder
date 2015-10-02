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
package com.intel.jndn.forwarder.impl;

import com.intel.jndn.forwarder.TestCounter;
import com.intel.jndn.forwarder.api.Face;
import com.intel.jndn.forwarder.api.callbacks.OnDataReceived;
import com.intel.jndn.forwarder.api.callbacks.OnInterestReceived;
import com.intel.jnfd.deamon.face.tcp.TcpFace2;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.util.Blob;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public class OrderedPacketReaderTest {

	private static final Logger logger = Logger.getLogger(OrderedPacketReaderTest.class.getName());

	@Test
	public void testAddingUnalignedBuffers() throws InterruptedException {
		final TestCounter dataCounter = new TestCounter();
		final TestCounter interestCounter = new TestCounter();
		OrderedPacketReader reader = new OrderedPacketReader(new OnDataReceived() {
			@Override
			public void onData(Data data, Face incomingFace) {
				logger.log(Level.INFO, "Received data: {0}", data.getName().toUri());
				dataCounter.count++;
			}
		}, new OnInterestReceived() {
			@Override
			public void onInterest(Interest interest, Face face) {
				logger.log(Level.INFO, "Received interest: {0}", interest.toUri());
				interestCounter.count++;
			}
		});

		byte[] encodedInterest1 = encodeInterest(1000);
		byte[] encodedData1 = encodeData(1000);
		byte[] encodedInterest2 = encodeInterest(1000);

		ByteBuffer buffer1 = ByteBuffer.allocate(5000);
		buffer1.put(encodedInterest1, 0, 500);

		ByteBuffer buffer2 = ByteBuffer.allocate(5000);
		buffer2.put(encodedInterest1, 500, encodedInterest1.length - 500);
		buffer2.put(encodedData1, 0, 500);

		ByteBuffer buffer3 = ByteBuffer.allocate(5000);
		buffer3.put(encodedData1, 500, encodedData1.length - 500);
		buffer3.put(encodedInterest2);

		reader.add(OrderedPacket.fromFilledBuffer(buffer1));
		reader.add(OrderedPacket.fromFilledBuffer(buffer2));
		reader.add(OrderedPacket.fromFilledBuffer(buffer3));

		// wait for the background thread reader to finish; TODO fix
		Thread.sleep(250);

		assertEquals(1, dataCounter.count);
		assertEquals(2, interestCounter.count);
	}

	private byte[] encodeInterest(int approximateSize) {
		Interest interest = new Interest(new Name("/interest"));
		interest.getName().append(buildRandomBytes(approximateSize));
		return interest.wireEncode().getImmutableArray();
	}

	private byte[] encodeData(int approximateSize) {
		Data data = new Data(new Name("/data"));
		data.setContent(new Blob(buildRandomBytes(approximateSize)));
		return data.wireEncode().getImmutableArray();
	}

	private byte[] buildRandomBytes(int approximateSize) {
		Random random = new Random();
		byte[] bytes = new byte[approximateSize];
		random.nextBytes(bytes);
		return bytes;
	}

}
