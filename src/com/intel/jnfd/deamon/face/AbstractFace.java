/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face;

import com.intel.jndn.forwarder.api.Face;
import java.util.Objects;
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

	@Override
	public abstract void sendInterest(Interest interest);

	@Override
	public abstract void sendData(Data data);

	@Override
	public FaceUri getLocalUri() {
		return localUri;
	}

	public void setLocalUri(FaceUri localUri) {
		this.localUri = localUri;
	}

	@Override
	public FaceUri getRemoteUri() {
		return remoteUri;
	}

	public void setRemoteUri(FaceUri remoteUri) {
		this.remoteUri = remoteUri;
	}

	@Override
	public boolean isLocal() {
		return isLocal;
	}

	public void setIsLocal(boolean isLocal) {
		this.isLocal = isLocal;
	}

	@Override
	public boolean isMultiAccess() {
		return isMultiAccess;
	}

	public void setIsMultiAccess(boolean isMultiAccess) {
		this.isMultiAccess = isMultiAccess;
	}

	@Override
	public int getFaceId() {
		return faceId;
	}

	@Override
	public void setFaceId(int faceId) {
		this.faceId = faceId;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AbstractFace)) {
			return false;
		}
		AbstractFace other = (AbstractFace) o;
		return localUri.equals(other.getLocalUri())
				&& remoteUri.equals(other.getRemoteUri())
				&& other.isLocal() == isLocal
				&& other.isMultiAccess() == isMultiAccess;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 13 * hash + Objects.hashCode(this.localUri);
		hash = 13 * hash + Objects.hashCode(this.remoteUri);
		hash = 13 * hash + (this.isLocal ? 1 : 0);
		hash = 13 * hash + (this.isMultiAccess ? 1 : 0);
		hash = 13 * hash + this.faceId;
		return hash;
	}

	@Override
	public String toString() {
		return "faceId: " + faceId + "; localUri: " + localUri + "; remoteUri: " + remoteUri;
	}

	private FaceUri localUri;
	private FaceUri remoteUri;
	private boolean isLocal;
	private boolean isMultiAccess;
	private int faceId;
}
