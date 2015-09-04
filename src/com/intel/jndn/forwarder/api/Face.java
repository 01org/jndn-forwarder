/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder.api;

import com.intel.jnfd.deamon.face.FaceUri;
import java.io.IOException;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;

/**
 * Faces are abstractions of remote interfaces (see {@link Channel}).
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public interface Face {

	public void sendInterest(Interest interest);

	public void sendData(Data data);

	public void close() throws IOException;

	public int getFaceId();
	
	public void setFaceId(int id);

	public FaceUri getLocalUri();

	public FaceUri getRemoteUri();

	public boolean isLocal();

	public boolean isMultiAccess();
        
        @Override
        public String toString();
}
