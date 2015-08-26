/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face.tcp;

import com.intel.jndn.forwarder.api.Channel;
import com.intel.jndn.forwarder.api.Face;
import com.intel.jndn.forwarder.api.callbacks.OnCompleted;
import com.intel.jndn.forwarder.api.callbacks.OnDataReceived;
import com.intel.jnfd.deamon.face.AbstractChannel;
import com.intel.jnfd.deamon.face.AbstractFace;
import com.intel.jndn.forwarder.api.callbacks.OnFailed;
import com.intel.jndn.forwarder.api.callbacks.OnInterestReceived;
import com.intel.jnfd.deamon.face.FaceUri;
import com.intel.jnfd.deamon.face.ParseFaceUriException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zht
 */
public class TcpChannel extends AbstractChannel {

	private static final Logger logger = Logger.getLogger(TcpChannel.class.getName());
	private final OnInterestReceived onInterestReceived;
	private final OnDataReceived onDataReceived;

	TcpChannel(FaceUri uri, AsynchronousChannelGroup asynchronousChannelGroup, OnInterestReceived onInterestReceived, OnDataReceived onDataReceived)
			throws IOException {
		localUri(uri);
		mAddr = new InetSocketAddress(localUri().getInet(), localUri().getPort());
		this.asynchronousChannelGroup = asynchronousChannelGroup;
		asynchronousServerSocket
				= AsynchronousServerSocketChannel.open(asynchronousChannelGroup);
		asynchronousServerSocket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		this.onInterestReceived = onInterestReceived;
		this.onDataReceived = onDataReceived;
	}

	public int size() {
		return faceMap.size();
	}

	public void connect(FaceUri faceUri, OnCompleted<Face> onFaceConnected,
			OnFailed onFailure)
			throws IOException, InterruptedException, ExecutionException {
		connect(faceUri, onFaceConnected, onFailure,
				TimeUnit.SECONDS.toSeconds(4));
	}

	public void connect(FaceUri faceUri, OnCompleted<Face> onFaceConnected,
			OnFailed onFailure, long timeout)
			throws IOException, InterruptedException, ExecutionException {
		InetSocketAddress remoteAddr = new InetSocketAddress(faceUri.getInet(), faceUri.getPort());
		AbstractFace face = faceMap.get(remoteAddr);
		if (face != null) {
			onFaceConnected.onCompleted(face);
			return;
		}

		AsynchronousSocketChannel asynchronousSocketChannel = AsynchronousSocketChannel.open(asynchronousChannelGroup);
		ConnectAttachment connectAttachment = new ConnectAttachment();
		connectAttachment.asynchronousSocketChannel = asynchronousSocketChannel;
		asynchronousSocketChannel.connect(remoteAddr, connectAttachment, new ConnectHandler());
	}

	/**
	 * Open the AsynchronousServerSocket to prepare to accept incoming
	 * connections. This method only needs to be called once.
	 *
	 * @param onChannelCreated
	 * @param onFailure
	 */
	@Override
	public void open(OnCompleted<Channel> onChannelCreated,
			OnFailed onFailure) {
		try {
			asynchronousServerSocket.bind(mAddr);
			asynchronousServerSocket.accept(null, new AcceptHandler());
		} catch (IOException ex) {
			onFailure.onFailed(ex);
		}
	}

	public TcpFace getFace(String remoteIP, int remotePort) {
		InetSocketAddress remoteSocket = new InetSocketAddress(remoteIP, remotePort);
		return faceMap.get(remoteSocket);
	}

	@Override
	public void close(OnCompleted<Channel> onChannelClosed, OnFailed onFailed) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	/**
	 * This AcceptAttachment class is used to pass the parameters needed by the
	 * AcceptHandler.
	 */
	private class AcceptAttachment {

	}

	/**
	 * This is the AcceptHandler used to accept incoming connections.
	 */
	private class AcceptHandler implements
			CompletionHandler<AsynchronousSocketChannel, AcceptAttachment> {

		/**
		 *
		 * @param result it is the AsynchronousSocketChannel created by the
		 * AsynchronousSereverSocketChannel.
		 * @param attachment it is the parameter need by the
		 * AsynchronousSocketChannel.
		 */
		@Override
		public void completed(AsynchronousSocketChannel result,
				AcceptAttachment attachment) {
			// accept the next connection
			asynchronousServerSocket.accept(attachment, this);
			try {
				// handle this connection
				createFace(result);
			} catch (IOException ex) {
				Logger.getLogger(TcpChannel.class.getName()).log(Level.SEVERE, null, ex);
			} catch (ParseFaceUriException ex) {
				Logger.getLogger(TcpChannel.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		@Override
		public void failed(Throwable exc, AcceptAttachment attachment) {
			//TODO: fix this in the future;
		}

	}

	private class ConnectAttachment {

		public AsynchronousSocketChannel asynchronousSocketChannel;
	}

	private class ConnectHandler implements CompletionHandler<Void, ConnectAttachment> {

		@Override
		public void completed(Void result, ConnectAttachment attachment) {
			try {
				createFace(attachment.asynchronousSocketChannel);
			} catch (IOException ex) {
				Logger.getLogger(TcpChannel.class.getName()).log(Level.SEVERE, null, ex);
			} catch (ParseFaceUriException ex) {
				Logger.getLogger(TcpChannel.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		@Override
		public void failed(Throwable exc, ConnectAttachment attachment) {
			//TODO: fix this in the future;
		}
	}

	/**
	 * Create face for data sending and receiving.
	 *
	 * @param asynchronousSocketChannel
	 * @throws IOException
	 */
	public void createFace(AsynchronousSocketChannel asynchronousSocketChannel)
			throws IOException, ParseFaceUriException {
		InetSocketAddress remoteSocket
				= (InetSocketAddress) (asynchronousSocketChannel.getRemoteAddress());
		TcpFace face = null;
		if ((face = faceMap.get(remoteSocket)) == null) {
			InetSocketAddress localSocket
					= (InetSocketAddress) (asynchronousSocketChannel.getLocalAddress());
			if (remoteSocket.getAddress().isLoopbackAddress()
					&& localSocket.getAddress().isLoopbackAddress()) {
				face = new TcpLocalFace(new FaceUri(localSocket, "tcp"),
						new FaceUri(remoteSocket, "tcp"),
						asynchronousSocketChannel, true, false, onDataReceived, onInterestReceived);
			} else {
				face = new TcpFace(new FaceUri(localSocket, "tcp"),
						new FaceUri(remoteSocket, "tcp"),
						asynchronousSocketChannel, false, false, onDataReceived, onInterestReceived);
			}
			faceMap.put(remoteSocket, face);
		} else {
			// we already have a face for this endpoint, just reuse it
			asynchronousSocketChannel.close();
		}
	}

	private InetSocketAddress mAddr = null;
	private final Map<InetSocketAddress, TcpFace> faceMap = new HashMap<>();
	private AsynchronousServerSocketChannel asynchronousServerSocket = null;
	private AsynchronousChannelGroup asynchronousChannelGroup = null;
}
