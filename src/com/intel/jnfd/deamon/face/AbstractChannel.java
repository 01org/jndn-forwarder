/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face;

import com.intel.jndn.forwarder.api.Channel;

/**
 *
 * @author zht
 */
public abstract class AbstractChannel implements Channel {

    @Override
    public FaceUri localUri() {
        return localUri;
    }

    protected void localUri(FaceUri localUri) {
        this.localUri = localUri;
    }

    private FaceUri localUri;
}
