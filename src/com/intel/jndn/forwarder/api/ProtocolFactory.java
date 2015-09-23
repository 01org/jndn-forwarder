/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder.api;

import com.intel.jndn.forwarder.api.callbacks.OnCompleted;
import com.intel.jnfd.deamon.face.FaceUri;
import java.util.Collection;

/**
 *
 * @author Haitao Zhang <zhtaoxiang@gmail.com>
 */
public interface ProtocolFactory {

    /**
     * a factory may correspond to several different schemes
     *
     * @return
     */
    public String[] scheme();

    /**
     * For each schemes, it has a localUri.
     *
     * @return
     */
    public FaceUri[] defaultLocalUri();

    public Channel createChannel(FaceUri faceUri);

    public Collection<? extends Channel> listChannels();

    public void destroyChannel(FaceUri faceUri);

    public void createFace(FaceUri remoteFaceUri);
    
    public void createFace(FaceUri remoteFaceUri, OnCompleted<Face> onFaceCreated);

    public Collection<? extends Face> listFaces();

//    public void createFace(FaceUri localFaceUri, FaceUri remoteFaceUri, 
//            boolean newChannel);

    public void destroyFace(Face face);

    public void destroyFace(FaceUri localFaceUri, FaceUri remoteFaceUri);
}
