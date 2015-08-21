/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face.tcp;

import com.intel.jndn.forwarder.api.Channel;
import com.intel.jndn.forwarder.api.Face;
import com.intel.jnfd.deamon.face.AbstractChannel;
import com.intel.jnfd.deamon.face.FaceUri;
import com.intel.jnfd.deamon.face.ParseFaceUriException;
import com.intel.jndn.forwarder.api.ProtocolFactory;
import com.intel.jndn.forwarder.api.callbacks.OnCompleted;
import com.intel.jndn.forwarder.api.callbacks.OnFailed;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zht
 */
public class TcpFactory implements ProtocolFactory {

	public TcpFactory(AsynchronousChannelGroup asynchronousChannelGroup) throws IOException {

//    private Set<Node> prohibitedNodes = new HashSet<Node>();
//    TODO: if the prohibition function is necessary, we can implement this funtion
//    in the future;
		this.asynchronousChannelGroup = asynchronousChannelGroup;
	}

	public TcpFactory(AsynchronousChannelGroup asynchronousChannelGroup, String port) throws IOException {
		//    private Set<Node> prohibitedNodes = new HashSet<Node>();
		//    TODO: if the prohibition function is necessary, we can implement this funtion
		//    in the future;
		this.asynchronousChannelGroup = asynchronousChannelGroup;
		defaultPort = port;
	}

	@Override
	public String scheme() {
		return SCHEME_NAME;
	}

	public TcpChannel CreateChannel(FaceUri uri) throws IOException {
		TcpChannel channel = channelMap.get(uri);
		if (channel != null) {
			return channel;
		}
		channel = new TcpChannel(uri, asynchronousChannelGroup);
		channelMap.put(uri, channel);
		return channel;
	}

	public TcpChannel CreateChannel(String localName, int localPort)
			throws ParseFaceUriException, UnknownHostException, IOException {
		FaceUri uri = new FaceUri("tcp", localName, localPort);
		return CreateChannel(uri);
	}

	public TcpChannel findChannels(FaceUri uri) {
		return channelMap.get(uri);
	}

	public List<? extends AbstractChannel> getChannels() {
		List<TcpChannel> result = new ArrayList<>();
		for (Map.Entry<FaceUri, TcpChannel> entry : channelMap.entrySet()) {
			result.add(entry.getValue());
		}
		return result;
	}

	public static final String SCHEME_NAME = "tcp";
	private final Map<FaceUri, TcpChannel> channelMap = new HashMap<>();
	private String defaultPort = "6363";
	private AsynchronousChannelGroup asynchronousChannelGroup = null;

//    private Set<Node> prohibitedNodes = new HashSet<Node>();
//    TODO: if the prohibition function is necessary, we can implement this funtion 
//    in the future;

	@Override
	public FaceUri defaultLocalUri() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void createChannel(FaceUri faceUri, OnCompleted<Channel> onChannelCreated, OnFailed onChannelCreationFailed) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void destroyChannel(FaceUri faceUri, OnCompleted<Channel> onChannelDestroyed, OnFailed onChannelDestructionFailure) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void createFace(FaceUri faceUri, OnCompleted<Face> onFaceCreated, OnFailed onFaceCreationFailed) {
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
}
