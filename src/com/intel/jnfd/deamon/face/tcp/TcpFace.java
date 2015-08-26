/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face.tcp;

import com.intel.jndn.forwarder.api.callbacks.OnDataReceived;
import com.intel.jndn.forwarder.api.callbacks.OnInterestReceived;
import com.intel.jnfd.deamon.face.AbstractFace;
import com.intel.jnfd.deamon.face.FaceUri;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.encoding.ElementListener;
import net.named_data.jndn.encoding.ElementReader;
import net.named_data.jndn.encoding.EncodingException;
import net.named_data.jndn.encoding.TlvWireFormat;
import net.named_data.jndn.encoding.tlv.Tlv;
import net.named_data.jndn.encoding.tlv.TlvDecoder;
import net.named_data.jndn.util.Blob;
import net.named_data.jndn.util.Common;

/**
 *
 * @author zht
 */
public class TcpFace extends AbstractFace {

	public TcpFace(FaceUri localUri, FaceUri remoteUri, AsynchronousSocketChannel asynchronousSocketChannel, boolean isLocal, boolean isMultiAccess, OnDataReceived onDataReceived, OnInterestReceived onInterestReceived) {
		super(localUri, remoteUri, isLocal, isMultiAccess);
		this.asynchronousSocketChannel = asynchronousSocketChannel;
		ReceiveHandler receiveHandler = new ReceiveHandler();
		this.asynchronousSocketChannel.read(inputBuffer, null, receiveHandler);
		this.onInterestReceived = onInterestReceived;
		this.onDataReceived = onDataReceived;
	}

	@Override
	public void sendInterest(Interest interest) {
		boolean wasQueueEmpty = sendQueue.isEmpty();
		sendQueue.add(interest.wireEncode());
		if (wasQueueEmpty) {
			sendFromQueue();
		}
	}

	/**
	 * This method is created for test purpose. It should not be invoked by the
	 * formal method.
	 *
	 * @param str
	 */
	public void send(String str) {
		boolean wasQueueEmpty = sendQueue.isEmpty();
		sendQueue.add(new Blob(str));
		if (wasQueueEmpty) {
			sendFromQueue();
		}
	}

	@Override
	public void sendData(Data data) {
		boolean wasQueueEmpty = sendQueue.isEmpty();
		sendQueue.add(data.wireEncode());
		if (wasQueueEmpty) {
			sendFromQueue();
		}
	}

	@Override
	public void close() {
		if (!asynchronousSocketChannel.isOpen()) {
			return;
		}

		try {
			asynchronousSocketChannel.close();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Failed to close the face.", ex);
		}

		sendQueue.clear();
	}

	/**
	 * Check the sendQueue and send data out.
	 */
	protected void sendFromQueue() {
		asynchronousSocketChannel.write(sendQueue.poll().buf(), null, new SendHandler());
	}

	private static final Logger logger = Logger.getLogger(TcpFace.class.getName());
	protected AsynchronousSocketChannel asynchronousSocketChannel;
	private ByteBuffer inputBuffer = ByteBuffer.allocate(Common.MAX_NDN_PACKET_SIZE);
	private Queue<Blob> sendQueue = new ConcurrentLinkedQueue<>();
	private ElementReader elementReader = new ElementReader(new Deserializer(null, null));
	private final OnInterestReceived onInterestReceived;
	private final OnDataReceived onDataReceived;

	/**
	 * Receive data and split it into elements (e.g. Data, Interest) using the
	 * ElementReader
	 */
	private class ReceiveHandler implements CompletionHandler<Integer, Void> {

		@Override
		public void completed(Integer result, Void attachment) {
			if (result != -1) {
				asynchronousSocketChannel.read(inputBuffer, attachment, this);
				try {
					elementReader.onReceivedData(inputBuffer);
				} catch (EncodingException ex) {
					logger.log(Level.WARNING, "Failed to decode bytes on face.", ex);
				}
			}
		}

		@Override
		public void failed(Throwable exc, Void attachment) {
			logger.log(Level.WARNING, "Failed to receive bytes on face.");
		}
	}

	/**
	 * Parse bytes into Interest and Data packets
	 */
	private class Deserializer implements ElementListener {
		
		private final OnDataReceived onDataReceived;
		private final OnInterestReceived onInterestReceived;

		public Deserializer(OnDataReceived onDataReceived, OnInterestReceived onInterestReceived) {
			this.onDataReceived = onDataReceived;
			this.onInterestReceived = onInterestReceived;
		}

		@Override
		public final void onReceivedElement(ByteBuffer element) throws EncodingException {
			if (element.get(0) == Tlv.Interest || element.get(0) == Tlv.Data) {
				TlvDecoder decoder = new TlvDecoder(element);
				if (decoder.peekType(Tlv.Interest, element.remaining())) {
					Interest interest = new Interest();
					interest.wireDecode(element, TlvWireFormat.get());
					onInterestReceived.onInterest(interest, TcpFace.this);
				} else if (decoder.peekType(Tlv.Data, element.remaining())) {
					Data data = new Data();
					data.wireDecode(element, TlvWireFormat.get());
					onDataReceived.onData(data, TcpFace.this);
				}
			}
		}

	}

	/**
	 * This class is used to handle the send data. The Void is used to
	 * pass the parameters.
	 */
	private class SendHandler implements CompletionHandler<Integer, Void> {

		@Override
		public void completed(Integer result, Void attachment) {
			if (!sendQueue.isEmpty()) {
				asynchronousSocketChannel.write(inputBuffer, attachment, this);
			}
		}

		@Override
		public void failed(Throwable exc, Void attachment) {
			//TODO: add actions in the future;
		}

	}
}
