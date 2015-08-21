/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face.tcp;

import com.intel.jnfd.deamon.face.Channel;
import com.intel.jnfd.deamon.face.Face;
import com.intel.jndn.forwarder.api.callbacks.OnFaceConnectionFailed;
import com.intel.jndn.forwarder.api.callbacks.OnFaceConnected;
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
public class TcpChannel extends Channel {

	TcpChannel(FaceUri uri, AsynchronousChannelGroup asynchronousChannelGroup)
			throws IOException {
		setUri(uri);
		mAddr = new InetSocketAddress(getUri().getInet(), getUri().getPort());
		this.asynchronousChannelGroup = asynchronousChannelGroup;
		asynchronousServerSocket
				= AsynchronousServerSocketChannel.open(asynchronousChannelGroup);
		asynchronousServerSocket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
	}

	public int size() {
		return faceMap.size();
	}

	public void connect(FaceUri faceUri, OnFaceConnected faceCreatedCallback,
			OnFaceConnectionFailed faceConnectFailedCallback)
			throws IOException, InterruptedException, ExecutionException {
		connect(faceUri, faceCreatedCallback, faceConnectFailedCallback,
				TimeUnit.SECONDS.toSeconds(4));
	}

	public void connect(FaceUri faceUri, OnFaceConnected faceCreatedCallback,
			OnFaceConnectionFailed faceConnectFailedCallback, long timeout)
			throws IOException, InterruptedException, ExecutionException {
		InetSocketAddress remoteAddr
				= new InetSocketAddress(faceUri.getInet(), faceUri.getPort());
		Face face = faceMap.get(remoteAddr);
		if (face != null) {
			faceCreatedCallback.onConnected(face);
			return;
		}
//        System.out.println("try to connect " + remoteAddr.toString());
		AsynchronousSocketChannel asynchronousSocketChannel
				= AsynchronousSocketChannel.open(asynchronousChannelGroup);
		ConnectAttachment connectAttachment = new ConnectAttachment();
		connectAttachment.asynchronousSocketChannel = asynchronousSocketChannel;
		asynchronousSocketChannel.connect(remoteAddr, connectAttachment,
				new ConnectHandler());
	}

	/**
	 * Open the AsynchronousServerSocket to prepare to accept incoming
	 * connections. This method only needs to be called once.
	 *
	 * @param faceCreatedCallback
	 * @param connectFailedCallback
	 * @throws IOException
	 */
	public void open(OnFaceConnected faceCreatedCallback,
			OnFaceConnectionFailed connectFailedCallback) throws IOException {
//        System.out.println("start to accept incomming connections");
		asynchronousServerSocket.bind(mAddr);
		AcceptAttachment attach = new AcceptAttachment();
		asynchronousServerSocket.accept(attach, new AcceptHandler());
//        System.out.println("Done: start to accept incomming connections");
	}

	public TcpFace getFace(String remoteIP, int remotePort) {
		InetSocketAddress remoteSocket = new InetSocketAddress(remoteIP, remotePort);
		return faceMap.get(remoteSocket);
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

	private class ConnectHandler
			implements CompletionHandler<Void, ConnectAttachment> {

		@Override
		public void failed(Throwable exc, ConnectAttachment attachment) {
			//TODO: fix this in the future;
		}

		@Override
		public void completed(Void result, ConnectAttachment attachment) {
//            if(result == null) {
//                System.out.println("no result");
//            }
			try {
				createFace(attachment.asynchronousSocketChannel);
			} catch (IOException ex) {
				Logger.getLogger(TcpChannel.class.getName()).log(Level.SEVERE, null, ex);
			} catch (ParseFaceUriException ex) {
				Logger.getLogger(TcpChannel.class.getName()).log(Level.SEVERE, null, ex);
			}
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
						asynchronousSocketChannel, true, false);
			} else {
				face = new TcpFace(new FaceUri(localSocket, "tcp"),
						new FaceUri(remoteSocket, "tcp"),
						asynchronousSocketChannel, false, false);
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
