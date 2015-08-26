/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face.tcp;

import com.intel.jndn.forwarder.api.Channel;
import com.intel.jndn.forwarder.api.Face;
import com.intel.jnfd.deamon.face.FaceUri;
import com.intel.jnfd.deamon.face.ParseFaceUriException;
import com.intel.jndn.forwarder.api.ProtocolFactory;
import com.intel.jndn.forwarder.api.callbacks.OnCompleted;
import com.intel.jndn.forwarder.api.callbacks.OnDataReceived;
import com.intel.jndn.forwarder.api.callbacks.OnFailed;
import com.intel.jndn.forwarder.api.callbacks.OnInterestReceived;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zht
 */
public class TcpFactory implements ProtocolFactory {

	public TcpFactory(ExecutorService pool) {
		try {
			this.asynchronousChannelGroup = AsynchronousChannelGroup.withThreadPool(pool);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public String scheme() {
		return SCHEME_NAME;
	}

	@Override
	public FaceUri defaultLocalUri() {
		if (defaultUri == null) {
			try {
				defaultUri = new FaceUri(SCHEME_NAME, DEFAULT_HOST, DEFAULT_PORT);
			} catch (ParseFaceUriException | UnknownHostException ex) {
				throw new RuntimeException(ex);
			}
		}
		return defaultUri;
	}

	@Override
	public void createChannel(FaceUri faceUri, OnCompleted<Channel> onChannelCreated, OnFailed onChannelCreationFailed, OnDataReceived onDataReceived, OnInterestReceived onInterestReceived) {
		if (channelMap.containsKey(faceUri)) {
			onChannelCreated.onCompleted(channelMap.get(faceUri));
		} else {
			try {
				TcpChannel channel = new TcpChannel(faceUri, asynchronousChannelGroup, onInterestReceived, onDataReceived);
				channelMap.put(faceUri, channel);
				onChannelCreated.onCompleted(channel);
			} catch (IOException ex) {
				onChannelCreationFailed.onFailed(ex);
			}
		}
	}

	@Override
	public void destroyChannel(FaceUri faceUri, OnCompleted<Channel> onChannelDestroyed, OnFailed onChannelDestructionFailure) {
		Channel channel = channelMap.remove(faceUri);
		channel.close(onChannelDestroyed, onChannelDestructionFailure);
	}

	@Override
	public void createFace(FaceUri faceUri, OnCompleted<Face> onFaceCreated, OnFailed onFaceCreationFailed, OnDataReceived onDataReceived, OnInterestReceived onInterestReceived) {
		for (Map.Entry<FaceUri, TcpChannel> entry : channelMap.entrySet()) {
			if ((!entry.getKey().getIsV6()) && (!faceUri.getIsV6())
					|| entry.getKey().getIsV6() && faceUri.getIsV6()) {
				try {
					entry.getValue().connect(faceUri, onFaceCreated,
							onFaceCreationFailed);
				} catch (IOException | InterruptedException | ExecutionException ex) {
					Logger.getLogger(TcpFactory.class.getName()).log(Level.SEVERE, null, ex);
				}
				return;
			}
		}
		onFaceCreationFailed.onFailed(new IOException("No channels available to connect to for " + faceUri));
	}

	@Override
	public void destroyFace(FaceUri faceUri, OnCompleted<Face> onFaceDestroyed, OnFailed onFaceDestructionFailed) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	protected Channel findChannels(FaceUri uri) {
		return channelMap.get(uri);
	}

	protected Collection<? extends Channel> getChannels() {
		return channelMap.values();
	}

	public static final String SCHEME_NAME = "tcp";
	public static final String DEFAULT_HOST = "0.0.0.0";
	public static final int DEFAULT_PORT = 6363;

	private static final Logger logger = Logger.getLogger(TcpFactory.class.getName());
	private static FaceUri defaultUri;
	private final Map<FaceUri, TcpChannel> channelMap = new HashMap<>();
	private AsynchronousChannelGroup asynchronousChannelGroup = null;

	//    private Set<Node> prohibitedNodes = new HashSet<Node>();
	//    TODO: if the prohibition function is necessary, we can implement this funtion 
	//    in the future;
}
