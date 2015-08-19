/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face.tcp;

import com.intel.jnfd.deamon.face.FaceUri;
import java.nio.channels.AsynchronousSocketChannel;

/**
 *
 * @author zht
 */
public class TcpLocalFace extends TcpFace {

    public TcpLocalFace(FaceUri localUri, FaceUri remoteUri,
            AsynchronousSocketChannel asynchronousSocketChannel, boolean isLocal,
            boolean isMultiAccess) {
        super(localUri, remoteUri, asynchronousSocketChannel, isLocal, isMultiAccess);
    }

}
