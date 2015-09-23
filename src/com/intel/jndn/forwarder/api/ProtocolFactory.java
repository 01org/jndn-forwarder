/*
 * jndn-forwarder
 * Copyright (c) 2015, Intel Corporation.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms and conditions of the GNU Lesser General Public License,
 * version 3, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 */
package com.intel.jndn.forwarder.api;

import com.intel.jndn.forwarder.api.callbacks.OnCompleted;
import com.intel.jnfd.deamon.face.FaceUri;
import java.util.Collection;

/**
 *
 * @author zht
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
