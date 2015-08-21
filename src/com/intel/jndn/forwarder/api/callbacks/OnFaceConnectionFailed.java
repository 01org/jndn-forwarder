/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder.api.callbacks;

import com.intel.jnfd.deamon.face.FaceUri;

/**
 *
 * @author zht
 */
public interface OnFaceConnectionFailed {

    public void onConnectionFailure(FaceUri uri, Throwable throwable);
}
