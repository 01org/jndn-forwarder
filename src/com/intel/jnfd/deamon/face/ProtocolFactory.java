/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face;

import com.intel.jndn.forwarder.api.callbacks.OnFaceConnected;
import com.intel.jndn.forwarder.api.callbacks.OnFaceConnectionFailed;
import java.util.List;

/**
 *
 * @author zht
 */
public abstract class ProtocolFactory {

	public abstract String scheme();

	public abstract void createFace(FaceUri faceUri, OnFaceConnected faceCreatedCallback,
			OnFaceConnectionFailed faceConnectFailedCallback);

	public abstract List<? extends Channel> getChannels();
}
