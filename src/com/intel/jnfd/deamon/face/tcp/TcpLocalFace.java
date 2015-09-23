/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face.tcp;

import com.intel.jndn.forwarder.api.Face;
import com.intel.jndn.forwarder.api.callbacks.OnCompleted;
import com.intel.jndn.forwarder.api.callbacks.OnDataReceived;
import com.intel.jndn.forwarder.api.callbacks.OnInterestReceived;
import com.intel.jnfd.deamon.face.FaceUri;
import java.nio.channels.AsynchronousSocketChannel;

/**
 *
 * @author Haitao Zhang <zhtaoxiang@gmail.com>
 */
public class TcpLocalFace extends TcpFace {

	public TcpLocalFace(FaceUri localUri, FaceUri remoteUri,
			AsynchronousSocketChannel asynchronousSocketChannel, boolean isLocal,
			boolean isMultiAccess, OnCompleted<Face> onFaceDestroyedByPeer,
			OnDataReceived onDataReceived, OnInterestReceived onInterestReceived) {
		super(localUri, remoteUri, asynchronousSocketChannel, isLocal, isMultiAccess,
				onFaceDestroyedByPeer, onDataReceived, onInterestReceived);
	}

}
