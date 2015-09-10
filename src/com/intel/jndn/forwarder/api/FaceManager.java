/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder.api;

import com.intel.jnfd.deamon.face.FaceUri;
import java.util.Collection;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public interface FaceManager {

	public void registerProtocol(ProtocolFactory protocolFactory);

	public Collection<ProtocolFactory> listProtocols();
        
        public Collection<String> listProtocolNames();

	public void createChannelAndListen(FaceUri localUri);

	public Collection<? extends Channel> listChannels();
        
        public Collection<? extends Channel> listChannels(String scheme);

	public void destroyChannel(FaceUri localUri);
        
        public void createFaceAndConnect(FaceUri remoteUri);
        
	public void createFaceAndConnect(FaceUri localUri, FaceUri remoteUri);

	public Collection<? extends Face> listFaces();
        
        public Collection<? extends Face> listFaces(String scheme);

	public void destroyFace(Face face);
        
        public void destroyFace(FaceUri localFaceUri, FaceUri remoteFaceUri);

}
