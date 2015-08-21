/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder.api.callbacks;

import com.intel.jnfd.deamon.face.Face;
import net.named_data.jndn.Data;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public interface OnDataReceived {
	public void onData(Data data, Face incomingFace);
}
