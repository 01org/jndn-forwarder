/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face;

import com.intel.jndn.forwarder.api.Face;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;

/**
 *
 * @author zht
 */
public abstract class AbstractFace implements Face {

    /**
     *
     * @param localUri
     * @param remoteUri
     * @param isLocal
     * @param isMultiAccess
     */
    public AbstractFace(FaceUri localUri, FaceUri remoteUri, boolean isLocal, boolean isMultiAccess) {
        this.localUri = localUri;
        this.remoteUri = remoteUri;
        this.isLocal = isLocal;
        this.isMultiAccess = isMultiAccess;
    }
    
    public abstract void sendInterest(Interest interest);

    public abstract void sendData(Data data);
    
    public abstract void close();
    
    public FaceUri getLocalUri() {
        return localUri;
    }

    public void setLocalUri(FaceUri localUri) {
        this.localUri = localUri;
    }

    public FaceUri getRemoteUri() {
        return remoteUri;
    }

    public void setRemoteUri(FaceUri remoteUri) {
        this.remoteUri = remoteUri;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setIsLocal(boolean isLocal) {
        this.isLocal = isLocal;
    }

    public boolean isMultiAccess() {
        return isMultiAccess;
    }

    public void setIsMultiAccess(boolean isMultiAccess) {
        this.isMultiAccess = isMultiAccess;
    }

    public int getFaceId() {
        return faceId;
    }

    public void setFaceId(int faceId) {
        this.faceId = faceId;
    }
    
    @Override
    public boolean equals(Object o) {
        AbstractFace other = (AbstractFace) o;
        if(localUri.equals(other.getLocalUri())
                && remoteUri.equals(other.getRemoteUri())
                && other.isLocal() == isLocal
                && other.isMultiAccess() == isMultiAccess)
            return true;
        return false;
    }
    
    private FaceUri localUri;
    private FaceUri remoteUri;
    private boolean isLocal;
    private boolean isMultiAccess;
    private int faceId;
}
