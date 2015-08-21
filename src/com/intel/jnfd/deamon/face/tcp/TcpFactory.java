/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face.tcp;

import com.intel.jnfd.deamon.face.Channel;
import com.intel.jndn.forwarder.api.callbacks.OnFaceConnectionFailed;
import com.intel.jndn.forwarder.api.callbacks.OnFaceConnected;
import com.intel.jnfd.deamon.face.FaceUri;
import com.intel.jnfd.deamon.face.ParseFaceUriException;
import com.intel.jnfd.deamon.face.ProtocolFactory;
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
public class TcpFactory extends ProtocolFactory {

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

	@Override
	public void createFace(FaceUri faceUri, OnFaceConnected faceCreatedCallback,
			OnFaceConnectionFailed faceConnectFailedCallback) {
		for (Map.Entry<FaceUri, TcpChannel> entry : channelMap.entrySet()) {
			if ((!entry.getKey().getIsV6()) && (!faceUri.getIsV6())
					|| entry.getKey().getIsV6() && faceUri.getIsV6()) {
				try {
					entry.getValue().connect(faceUri, faceCreatedCallback,
							faceConnectFailedCallback);
				} catch (IOException | InterruptedException | ExecutionException ex) {
					Logger.getLogger(TcpFactory.class.getName()).log(Level.SEVERE, null, ex);
				}
				return;
			}
		}
		faceConnectFailedCallback.onConnectionFailure(faceUri, new IOException("No channels available to connect to for " + faceUri));
	}

	@Override
	public List<? extends Channel> getChannels() {
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
}
