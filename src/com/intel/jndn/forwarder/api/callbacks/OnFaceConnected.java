/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder.api.callbacks;

import com.intel.jnfd.deamon.face.Face;

/**
 *
 * @author zht
 */
public interface OnFaceConnected {

    public void onConnected(Face face);
}
