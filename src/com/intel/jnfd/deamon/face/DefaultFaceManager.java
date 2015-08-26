/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face;

import com.intel.jndn.forwarder.api.Channel;
import com.intel.jndn.forwarder.api.Face;
import com.intel.jndn.forwarder.api.FaceManager;
import com.intel.jndn.forwarder.api.ProtocolFactory;
import com.intel.jndn.forwarder.api.callbacks.OnCompleted;
import com.intel.jndn.forwarder.api.callbacks.OnDataReceived;
import com.intel.jndn.forwarder.api.callbacks.OnFailed;
import com.intel.jndn.forwarder.api.callbacks.OnInterestReceived;
import com.intel.jnfd.deamon.face.tcp.TcpChannel;
import com.intel.jnfd.deamon.face.tcp.TcpFactory;
import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author zht
 */
public class DefaultFaceManager implements FaceManager {

	public DefaultFaceManager(ExecutorService pool) {
		this.pool = pool;
	}

	public DefaultFaceManager() {
		this(Executors.newCachedThreadPool());
	}

	@Override
	public void registerProtocol(ProtocolFactory protocolFactory) {
		protocols.put(protocolFactory.scheme(), protocolFactory);
	}

	@Override
	public Collection<ProtocolFactory> listProtocols() {
		return protocols.values();
	}

	public ProtocolFactory findProtocol(String scheme) {
		if (!protocols.containsKey(scheme)) {
			throw new IllegalArgumentException("Unknown protocol scheme: " + scheme);
		} else {
			return protocols.get(scheme);
		}
	}

	@Override
	public void createChannel(FaceUri localUri, OnCompleted<Channel> onChannelCreated, OnFailed onChannelCreationFailed, OnDataReceived onDataReceived, OnInterestReceived onInterestReceived) {
		findProtocol(localUri.getScheme()).createChannel(localUri, new OnCompleted<Channel>() {
			@Override
			public void onCompleted(Channel result) {
				// TODO 
			}
		}, onChannelCreationFailed, onDataReceived, onInterestReceived);
	}

	@Override
	public void destroyChannel(FaceUri localUri, OnCompleted<Channel> onChannelCreated, OnFailed onChannelCreationFailed) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<Channel> listChannels() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void createFace(FaceUri localUri, FaceUri remoteUri, OnCompleted<Face> onFaceCreated, OnFailed onFaceCreationFailed, OnDataReceived onDataReceived, OnInterestReceived onInterestReceived) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void destroyFace(FaceUri localUri, FaceUri remoteUri, OnCompleted<Face> onFaceDestroyed, OnFailed onFaceDestructionFailed) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<Face> listFaces() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private Map<String, ProtocolFactory> protocols = new HashMap<>();
	private ExecutorService pool;

}
