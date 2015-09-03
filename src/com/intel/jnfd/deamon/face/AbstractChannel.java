/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face;

import com.intel.jndn.forwarder.api.Channel;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author zht
 */
public abstract class AbstractChannel implements Channel {

    @Override
    public Set<FaceUri> localUri() {
        return localUris;
    }

    public void addLocalUri(FaceUri localUri) {
        localUris.add(localUri);
    }
    
    public void removeLocalUri(FaceUri localUri) {
        localUris.remove(localUri);
    }

    private final Set<FaceUri> localUris = new HashSet<>();
}