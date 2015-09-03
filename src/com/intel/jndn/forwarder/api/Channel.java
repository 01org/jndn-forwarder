/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder.api;

import com.intel.jnfd.deamon.face.AbstractFace;
import com.intel.jnfd.deamon.face.FaceUri;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * Channels are abstractions of local interfaces; they perform the necessary
 * functionality to connect a local interface to a remote interface (i.e. a
 * {@link AbstractFace}). When a channel is opened, it accepts incoming traffic
 * from remote faces; when it is closed, it rejects incoming traffic and closes
 * all faces connected to it.
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public interface Channel {

    /**
     * A channel may correspond to more than one local FaceUri, for example, a tcp
     * channel corresponds to a IPv4 and a IPv6 local FaceUri.
     * @return 
     */
    public Set<FaceUri> localUri();

    public void open() throws IOException;

    public void close() throws IOException;

    public Collection<? extends Face> listFaces();

    public void destroyFace(FaceUri faceUri);
}
