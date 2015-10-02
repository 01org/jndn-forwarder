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

import com.intel.jndn.forwarder.api.Face2;
import com.intel.jndn.forwarder.api.callbacks.OnCompleted;
import com.intel.jndn.forwarder.api.callbacks.OnDataReceived;
import com.intel.jndn.forwarder.api.callbacks.OnInterestReceived;
import com.intel.jndn.forwarder.impl.OrderedPacketReader;
import com.intel.jnfd.deamon.face.FaceUri;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.encoding.ElementReader;
import net.named_data.jndn.encoding.EncodingException;
import net.named_data.jndn.util.Common;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public class TcpFace2 implements Face2 {

	private static final Logger logger = Logger.getLogger(TcpFace.class.getName());
	private final FaceUri remoteUri;
	private final FaceUri localUri;
	private AsynchronousSocketChannel socket;
	private final ExecutorService pool;
	private final OrderedPacketReader packetReader;

	/**
	 * Build a TCP face F
	 *
	 * @param localUri
	 * @param remoteUri
	 * @param socket
	 * @param onDataReceived
	 * @param onInterestReceived
	 */
	public TcpFace2(FaceUri localUri, FaceUri remoteUri,
			ExecutorService pool,
			AsynchronousSocketChannel socket,
			OnDataReceived onDataReceived,
			OnInterestReceived onInterestReceived) {

		this.localUri = localUri;
		this.remoteUri = remoteUri;
		this.socket = socket;
		this.packetReader = new OrderedPacketReader(onDataReceived, onInterestReceived);
		this.pool = pool;

		BufferAttachment attachment = new BufferAttachment();
		socket.read(attachment.buffer, attachment, new ReceiveHandler());
		packetReader.add(attachment);
	}

	public static class BufferAttachment {

		public final ByteBuffer buffer;
		public boolean filled = false;

		public BufferAttachment(ByteBuffer buffer) {
			this.buffer = buffer;
			this.filled = true;
		}

		public BufferAttachment() {
			this.buffer = ByteBuffer.allocateDirect(Common.MAX_NDN_PACKET_SIZE);
		}

	}

	/**
	 * Receive data and split it into elements (e.g. Data, Interest) using the a
	 * packet reader
	 */
	private class ReceiveHandler implements CompletionHandler<Integer, BufferAttachment> {

		@Override
		public void completed(Integer result, BufferAttachment attachment) {
			if (result != -1) {
				logger.log(Level.INFO, "Received {0} bytes from: {1}", new Object[]{result, remoteUri});
				attachment.filled = true;

				BufferAttachment nextAttachment = new BufferAttachment();
				socket.read(nextAttachment.buffer, nextAttachment, this);
				packetReader.add(nextAttachment);
			} else {
				logger.log(Level.INFO, "Socket closed from remote, closing locally: {0}", remoteUri);
				close(DO_NOTHING_HANDLER, DO_NOTHING_HANDLER);
			}
		}

		@Override
		public void failed(Throwable exc, BufferAttachment attachment) {
			logger.log(Level.WARNING, "Failed to receive bytes on face: {0}", remoteUri);
		}
	}

	public static OnCompleted DO_NOTHING_HANDLER = new OnCompleted() {
		@Override
		public void onCompleted(Object result) {
			// do nothing
		}
	};

	@Override
	public void close(OnCompleted<Void> onClosed, OnCompleted<Throwable> onFailed) {
		if (socket != null && socket.isOpen()) {
			try {
				socket.close();
			} catch (IOException ex) {
				logger.log(Level.SEVERE, "Failed to close socket on: {0}", remoteUri);
				onFailed.onCompleted(ex);
			}
		}
		packetReader.stopGracefully();
	}

	@Override
	public void sendInterest(Interest interest) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void sendData(Data data) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public FaceUri localUri() {
		return localUri;
	}

	@Override
	public FaceUri remoteUri() {
		return remoteUri;
	}

	@Override
	public boolean isLocal() {
		return false; // TODO fix
	}

	@Override
	public boolean isMultiAccess() {
		return false;
	}

	//
//	@Override
//	public void open(OnCompleted<Void> onOpened, OnCompleted<Throwable> onFailed) {
//		if (socket != null) {
//			throw new IllegalStateException("The face is already open.");
//		}
//
////		try {
////			InetSocketAddress remoteAddr = new InetSocketAddress(remoteUri.getInet(), remoteUri.getPort());
////			socket = AsynchronousSocketChannel.open(group);
////			socket.connect(remoteAddr, remoteUri, new ConnectHandler(onOpened, onFailed));
////		} catch (IOException ex) {
////			logger.log(Level.SEVERE, "Failed to create socket to: {0}", remoteUri);
////			onFailed.onCompleted(ex);
////		}
//		
//		try {
//			InetSocketAddress localAddress = new InetSocketAddress(remoteUri.getInet(), remoteUri.getPort());
//			socket = AsynchronousSocketChannel.open(group);
//			socket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
//			socket.bind(localAddress);
//			socket.accept(null, new TcpChannel.AcceptHandler());
//			socket.connect(localAddress, remoteUri, new ConnectHandler(onOpened, onFailed));
//		} catch (IOException ex) {
//			logger.log(Level.SEVERE, "Failed to create socket to: {0}", remoteUri);
//			onFailed.onCompleted(ex);
//		}			
//	}
//
//	public class ConnectHandler implements CompletionHandler<Void, FaceUri> {
//
//		private final OnCompleted<Void> onOpened;
//		private final OnCompleted<Throwable> onFailed;
//
//		private ConnectHandler(OnCompleted<Void> onOpened, OnCompleted<Throwable> onFailure) {
//			this.onOpened = onOpened;
//			this.onFailed = onFailure;
//		}
//
//		@Override
//		public void completed(Void result, FaceUri attachment) {
//			logger.log(Level.SEVERE, "Connected to: {0}", attachment);
//			onOpened.onCompleted(null);
//
//			ByteBuffer buffer = ByteBuffer.allocate(Common.MAX_NDN_PACKET_SIZE);
//			socket.read(buffer, buffer, new ReceiveHandler());
//		}
//
//		@Override
//		public void failed(Throwable error, FaceUri attachment) {
//			logger.log(Level.SEVERE, "Failed to connect to: {0}", attachment);
//			onFailed.onCompleted(error);
//		}
//	}
}
