/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder.api;

import com.intel.jndn.forwarder.api.callbacks.OnCompleted;
import com.intel.jndn.forwarder.api.callbacks.OnFailed;
import com.intel.jnfd.deamon.face.FaceUri;

/**
 *
 * @author zht
 */
public interface ProtocolFactory {

	public String scheme();

	public FaceUri defaultLocalUri();

	public void createChannel(FaceUri faceUri, OnCompleted<Channel> onChannelCreated, OnFailed onChannelCreationFailed);

	public void destroyChannel(FaceUri faceUri, OnCompleted<Channel> onChannelDestroyed, OnFailed onChannelDestructionFailure);

	public void createFace(FaceUri faceUri, OnCompleted<Face> onFaceCreated, OnFailed onFaceCreationFailed);

	public void destroyFace(FaceUri faceUri, OnCompleted<Face> onFaceDestroyed, OnFailed onFaceDestructionFailed);
}
