/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face.tcp;

import com.intel.jndn.forwarder.api.Face;
import com.intel.jndn.forwarder.api.callbacks.OnCompleted;
import com.intel.jndn.forwarder.api.callbacks.OnDataReceived;
import com.intel.jndn.forwarder.api.callbacks.OnInterestReceived;
import com.intel.jnfd.deamon.face.AbstractFace;
import com.intel.jnfd.deamon.face.FaceUri;
import com.intel.jnfd.util.NfdCommon;
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
import net.named_data.jndn.encoding.WireFormat;
import net.named_data.jndn.encoding.tlv.Tlv;
import net.named_data.jndn.encoding.tlv.TlvDecoder;
import net.named_data.jndn.util.Blob;
import net.named_data.jndn.util.Common;

/**
 *
 * @author Haitao Zhang <zhtaoxiang@gmail.com>
 */
public class TcpFace extends AbstractFace {

	public TcpFace(FaceUri localUri, FaceUri remoteUri,
			AsynchronousSocketChannel asynchronousSocketChannel,
			boolean isLocal, boolean isMultiAccess,
			OnCompleted<Face> onFaceDestroyedByPeer,
			OnDataReceived onDataReceived,
			OnInterestReceived onInterestReceived) {
		super(localUri, remoteUri, isLocal, isMultiAccess);
		this.asynchronousSocketChannel = asynchronousSocketChannel;

		// callbacks
		this.onFaceDestroyedByPeer = onFaceDestroyedByPeer;
		this.onInterestReceived = onInterestReceived;
		this.onDataReceived = onDataReceived;
		this.elementReader = new ElementReader(new Deserializer(onDataReceived, onInterestReceived));

		ReceiveHandler receiveHandler = new ReceiveHandler();

		ReceiveAttachment newAttachment = new ReceiveAttachment();
		receiveQueue.add(newAttachment);
		this.asynchronousSocketChannel.read(newAttachment.inputBuffer, newAttachment, receiveHandler);
		logger.setLevel(NfdCommon.LOG_LEVEL);
	}

	@Override
	public void sendInterest(Interest interest) {
		boolean wasQueueEmpty = sendQueue.isEmpty();
		sendQueue.add(interest.wireEncode(wireFormat));
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
		sendQueue.add(data.wireEncode(wireFormat));
		if (wasQueueEmpty) {
			sendFromQueue();
		}
	}

	/**
	 * Check the sendQueue and send data out.
	 */
	protected synchronized void sendFromQueue() {
		if (!sendQueue.isEmpty()) {
			asynchronousSocketChannel.write(sendQueue.poll().buf(), null, sendHandler);
		}
	}

	protected void receiveFromQueue() {
		while (!receiveQueue.isEmpty()) {
			logger.info("try to process the incoming queue");
			ReceiveAttachment head = receiveQueue.peek();
			if (head.haveReadFromChannle) {
				receiveQueue.poll();
				logger.info("decode packet");
				try {
					elementReader.onReceivedData(head.inputBuffer);
				} catch (EncodingException ex) {
					logger.log(Level.WARNING, "Failed to decode bytes on face.", ex);
				}
			} else {
				return;
			}
		}
	}

	@Override
	public void close() throws IOException {
		if (!asynchronousSocketChannel.isOpen()) {
			return;
		}
		asynchronousSocketChannel.close();
		sendQueue.clear();
		receiveQueue.clear();
	}

	private class ReceiveAttachment {

		public ByteBuffer inputBuffer = ByteBuffer.allocate(Common.MAX_NDN_PACKET_SIZE);
		public boolean haveReadFromChannle = false;
	}

	/**
	 * Receive data and split it into elements (e.g. Data, Interest) using the
	 * ElementReader
	 */
	private class ReceiveHandler implements CompletionHandler<Integer, ReceiveAttachment> {

		@Override
		public void completed(Integer result, ReceiveAttachment attachment) {
			if (result != -1) {
				logger.info("receive something");
				attachment.inputBuffer.flip();
				attachment.haveReadFromChannle = true;
				receiveFromQueue();
				ReceiveAttachment newAttachment = new ReceiveAttachment();
				receiveQueue.add(newAttachment);
				asynchronousSocketChannel.read(newAttachment.inputBuffer, newAttachment, this);
			} else {
				logger.info("NO DATA RECEIVED...");
				onFaceDestroyedByPeer.onCompleted((Face) TcpFace.this);
			}
		}

		@Override
		public void failed(Throwable exc, ReceiveAttachment attachment) {
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
			logger.info("onReceivedElement is called");
			if (element.get(0) == Tlv.Interest || element.get(0) == Tlv.Data) {
				logger.info("receive Data or Interest packet");
				TlvDecoder decoder = new TlvDecoder(element);
				if (decoder.peekType(Tlv.Interest, element.remaining())) {
					logger.info("receive Interest packet");
					Interest interest = new Interest();
					interest.wireDecode(element, TlvWireFormat.get());
					onInterestReceived.onInterest(interest, TcpFace.this);

				} else if (decoder.peekType(Tlv.Data, element.remaining())) {
					logger.info("receive Data packet");
					Data data = new Data();
					data.wireDecode(element, TlvWireFormat.get());
					onDataReceived.onData(data, TcpFace.this);
				}
			}
		}

	}

	/**
	 * This class is used to handle the send data. The Void is used to pass the
	 * parameters.
	 */
	private class SendHandler implements CompletionHandler<Integer, Void> {

		@Override
		public void completed(Integer result, Void attachment) {
			if (result != -1) {
				logger.log(Level.INFO, "bytes sent: {0}", result);
				if (!sendQueue.isEmpty()) {
					sendFromQueue();
				}
			} else {
				logger.info("NO DATA SENT...");
				onFaceDestroyedByPeer.onCompleted((Face) this);
			}
		}

		@Override
		public void failed(Throwable exc, Void attachment) {
			//TODO: add actions in the future;
			logger.log(Level.WARNING, "Failed to send bytes on face.");
		}

	}

	private static final Logger logger = Logger.getLogger(TcpFace.class.getName());
	protected AsynchronousSocketChannel asynchronousSocketChannel;
//    private final ByteBuffer inputBuffer = ByteBuffer.allocate(Common.MAX_NDN_PACKET_SIZE);
	private final Queue<Blob> sendQueue = new ConcurrentLinkedQueue<>();
	private final Queue<ReceiveAttachment> receiveQueue = new ConcurrentLinkedQueue<>();
	private final ElementReader elementReader;
	private final OnCompleted<Face> onFaceDestroyedByPeer;
	private final OnInterestReceived onInterestReceived;
	private final OnDataReceived onDataReceived;
	private final WireFormat wireFormat = new TlvWireFormat();
	private final CompletionHandler<Integer, Void> sendHandler = new SendHandler();
}
