/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder.api;

import com.intel.jndn.forwarder.api.callbacks.OnCompleted;
import com.intel.jndn.forwarder.api.callbacks.OnDataReceived;
import com.intel.jndn.forwarder.api.callbacks.OnFailed;
import com.intel.jndn.forwarder.api.callbacks.OnInterestReceived;
import com.intel.jnfd.deamon.face.FaceUri;
import java.util.Collection;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public interface FaceManager {

	public void registerProtocol(ProtocolFactory protocolFactory);

	public Collection<ProtocolFactory> listProtocols();
        
        public Collection<String> listProtocolNames();

	public void createChannelAndListen(FaceUri localUri, 
                OnCompleted<Channel> onChannelCreated, 
                OnFailed onChannelCreationFailed, 
                OnDataReceived onDataReceived, 
                OnInterestReceived onInterestReceived);

	public Collection<? extends Channel> listChannels();
        
        public Collection<? extends Channel> listChannels(String scheme);

	public void destroyChannel(FaceUri localUri, 
                OnCompleted<Channel> onChannelCreated, 
                OnFailed onChannelCreationFailed);
        
        public void createFaceAndConnect(FaceUri remoteUri, 
                OnCompleted<Face> onFaceCreated, OnFailed onFaceCreationFailed, 
                OnDataReceived onDataReceived, 
                OnInterestReceived onInterestReceived);
        
	public void createFaceAndConnect(FaceUri localUri, FaceUri remoteUri, 
                OnCompleted<Face> onFaceCreated, OnFailed onFaceCreationFailed, 
                OnDataReceived onDataReceived, 
                OnInterestReceived onInterestReceived);

	public Collection<? extends Face> listFaces();
        
        public Collection<? extends Face> listFaces(String scheme);

	public void destroyFace(Face face, OnCompleted<Face> onFaceDestroyed, OnFailed onFaceDestructionFailed);
        
        public void destroyFace(FaceUri localFaceUri, FaceUri remoteFaceUri, 
                OnCompleted<Face> onFaceDestroyed, OnFailed onFaceDestructionFailed);

}
