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
 * @author zht
 */
public interface ProtocolFactory {

	public String scheme();

	public FaceUri defaultLocalUri();

	public Channel createChannelAndListen(FaceUri faceUri, 
                OnCompleted<Channel> onChannelCreated, 
                OnFailed onChannelCreationFailed, 
                OnDataReceived onDataReceived, 
                OnInterestReceived onInterestReceived);
        
        public Collection<? extends Channel> listChannels();

	public void destroyChannel(FaceUri faceUri, 
                OnCompleted<Channel> onChannelDestroyed, 
                OnFailed onChannelDestructionFailure);

	public void createFace(FaceUri remoteFaceUri, 
                OnCompleted<Face> onFaceCreated, 
                OnFailed onFaceCreationFailed, 
                OnDataReceived onDataReceived, 
                OnInterestReceived onInterestReceived);
        
        public Collection<? extends Face> listFaces();

        public void createFace(FaceUri localFaceUri, 
                FaceUri remoteFaceUri,
                OnCompleted<Face> onFaceCreated, 
                OnFailed onFaceCreationFailed, 
                OnDataReceived onDataReceived, 
                OnInterestReceived onInterestReceived);
        
	public void destroyFace(Face face, OnCompleted<Face> onFaceDestroyed, 
                OnFailed onFaceDestructionFailed);
        
        public void destroyFace(FaceUri localFaceUri, FaceUri remoteFaceUri,
            OnCompleted<Face> onFaceDestroyed,
            OnFailed onFaceDestructionFailed);
}
