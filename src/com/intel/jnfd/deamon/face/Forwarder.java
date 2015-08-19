/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face;

import net.named_data.jndn.Name;

/**
 *
 * @author zht
 */
public interface Forwarder {
//    public Forwarder(ExecutorService pool, ContentStore cs, PendingInterestTable pit, FaceInformationBase fib);
    public void createFace(FaceUri uri, FaceCreatedCallback cb, FaceConnectFailedCallback cb2);
    public void destroyFace(FaceUri uri);
    public void register(Name name, FaceUri uri);
    public void unregister(Name name, FaceUri uri);
    
}
