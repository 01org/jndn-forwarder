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
import net.named_data.jndn.encoding.WireFormat;
import net.named_data.jndn.encoding.tlv.Tlv;
import net.named_data.jndn.encoding.tlv.TlvDecoder;
import net.named_data.jndn.util.Blob;
import net.named_data.jndn.util.Common;

/**
 *
 * @author zht
 */
public class TcpFace extends AbstractFace {

	public TcpFace(FaceUri localUri, FaceUri remoteUri,
			AsynchronousSocketChannel asynchronousSocketChannel,
			boolean isLocal, boolean isMultiAccess,
			OnDataReceived onDataReceived,
			OnInterestReceived onInterestReceived) {
		super(localUri, remoteUri, isLocal, isMultiAccess);
		this.asynchronousSocketChannel = asynchronousSocketChannel;

		// callbacks
		this.onInterestReceived = onInterestReceived;
		this.onDataReceived = onDataReceived;
		this.elementReader = new ElementReader(new Deserializer(onDataReceived, onInterestReceived));

		ReceiveHandler receiveHandler = new ReceiveHandler();

		ReceiveAttachment attachment = new ReceiveAttachment();
		attachment.byteBuffer.limit(attachment.byteBuffer.capacity());
		attachment.byteBuffer.position(0);
		this.asynchronousSocketChannel.read(attachment.byteBuffer, attachment, receiveHandler);
	}

	@Override
	public void sendInterest(Interest interest) {
		logger.info("send interest: " + interest.toUri());
		Blob blob = interest.wireEncode(wireFormat);
		asynchronousSocketChannel.write(blob.buf(), blob.buf(), sendHandler);
	}

	@Override
	public void sendData(Data data) {
		logger.info("send data: " + data.getName().toUri());
		Blob blob = data.wireEncode(wireFormat);
		asynchronousSocketChannel.write(blob.buf(), blob.buf(), sendHandler);
	}

	@Override
	public void close() throws IOException {
		if (!asynchronousSocketChannel.isOpen()) {
			return;
		}
		asynchronousSocketChannel.close();
		sendQueue.clear();
	}

	private class ReceiveAttachment {
		public ByteBuffer byteBuffer = ByteBuffer.allocate(Common.MAX_NDN_PACKET_SIZE);
	}

	/**
	 * Receive data and split it into elements (e.g. Data, Interest) using the
	 * ElementReader
	 */
	private class ReceiveHandler implements CompletionHandler<Integer, ReceiveAttachment> {

		@Override
		public void completed(Integer result, ReceiveAttachment attachment) {
			if (result != -1) {
				// continue reading
				ReceiveAttachment newAttachment = new ReceiveAttachment();
//				newAttachment.byteBuffer.limit(newAttachment.byteBuffer.capacity());
//				newAttachment.byteBuffer.position(0);
				asynchronousSocketChannel.read(newAttachment.byteBuffer, newAttachment, this);

				// parse current
				attachment.byteBuffer.flip();
				try {
					logger.info("decode packet");
					elementReader.onReceivedData(attachment.byteBuffer);
				} catch (Exception ex) {
					logger.log(Level.WARNING, "Failed to decode bytes on face.", ex);
				}
			}
			else{
				logger.info("NO DATA RECEIVED...");
			}
		}

		@Override
		public void failed(Throwable exc, ReceiveAttachment attachment) {
			logger.log(Level.INFO, "Failed to receive bytes on face.");
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
	private class SendHandler implements CompletionHandler<Integer, ByteBuffer> {

		@Override
		public void completed(Integer result, ByteBuffer attachment) {
			logger.info("bytes sent: " + result);
			if(attachment.hasRemaining()){
				logger.info("writing more bytes");
				asynchronousSocketChannel.write(attachment, attachment, sendHandler);
			}
		}

		@Override
		public void failed(Throwable exc, ByteBuffer attachment) {
			//TODO: add actions in the future;
			logger.log(Level.INFO, "Failed to send bytes on face.");
		}

	}

	private static final Logger logger = Logger.getLogger(TcpFace.class.getName());
	protected AsynchronousSocketChannel asynchronousSocketChannel;
//    private final ByteBuffer inputBuffer = ByteBuffer.allocate(Common.MAX_NDN_PACKET_SIZE);
	private final Queue<Blob> sendQueue = new ConcurrentLinkedQueue<>();
	private final ElementReader elementReader;
	private final OnInterestReceived onInterestReceived;
	private final OnDataReceived onDataReceived;
	private final WireFormat wireFormat = new TlvWireFormat();
	private final CompletionHandler<Integer, ByteBuffer> sendHandler = new SendHandler();
}
