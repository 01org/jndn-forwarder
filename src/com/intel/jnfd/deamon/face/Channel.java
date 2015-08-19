/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face;

/**
 *
 * @author zht
 */
public class Channel {

    public FaceUri getUri() {
        return m_faceUri;
    }
    
    protected void setUri(FaceUri faceUri) {
        m_faceUri = faceUri;
    }
    
    private FaceUri m_faceUri;
}
