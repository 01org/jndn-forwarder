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
import java.util.List;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public interface FaceManager {

	public void registerProtocol(ProtocolFactory protocolFactory);

	public List<ProtocolFactory> listProtocols();

	public Channel createChannel(FaceUri localUri);

	public List<Channel> listChannels();

	public Channel destroyChannel(FaceUri localUri);

	public void createFace(FaceUri uri, OnCompleted<Face> onFaceCreated, OnFailed onFaceCreationFailed, OnDataReceived onDataReceived, OnInterestReceived onInterestReceived);

	public List<Face> listFaces();

	public void destroyFace(FaceUri uri, OnCompleted<Face> onFaceDestroyed, OnFailed onFaceDestructionFailed);

}
