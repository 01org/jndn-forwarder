/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder.api;

import com.intel.jndn.forwarder.api.callbacks.OnCompleted;
import com.intel.jndn.forwarder.api.callbacks.OnFailed;
import com.intel.jnfd.deamon.face.AbstractFace;
import com.intel.jnfd.deamon.face.FaceUri;

/**
 * Channels are abstractions of local interfaces; they perform the necessary
 * functionality to connect a local interface to a remote interface (i.e. a
 * {@link AbstractFace}). When a channel is opened, it accepts incoming traffic from
 * remote faces; when it is closed, it rejects incoming traffic and closes all
 * faces connected to it.
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public interface Channel {

	public FaceUri localUri();

	public void open(OnCompleted<Channel> onChannelOpened, OnFailed onFailed);

	public void close(OnCompleted<Channel> onChannelClosed, OnFailed onFailed);
}
