/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder.api;

import com.intel.jndn.forwarder.Forwarder;
import com.intel.jndn.forwarder.api.callbacks.OnInterestReceived;
import com.intel.jnfd.deamon.table.measurement.MeasurementAccessor;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

/**
 *
 * @author zht
 */
public abstract class Strategy {
	private final Name prefix;
    
    public Strategy(OnInterestReceived onInterestReceived, Name prefix) {
		this.onInterestReceived = onInterestReceived;
		this.prefix = prefix;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }
	
	public abstract Face[] determineOutgoingFaces(Interest interest, Forwarder forwarder);
    
    // TODO: implement this
    @Override
    public boolean equals(Object o) {
        return false;
    }
    
    private Name name;
    private OnInterestReceived onInterestReceived;
    private MeasurementAccessor measurementAccessor;
}
