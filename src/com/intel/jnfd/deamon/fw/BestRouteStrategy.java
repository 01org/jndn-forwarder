/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.fw;

import com.intel.jndn.forwarder.Forwarder;
import com.intel.jndn.forwarder.api.Face;
import com.intel.jndn.forwarder.api.Strategy;
import com.intel.jndn.forwarder.api.callbacks.OnInterestReceived;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

/**
 *
 * @author zht
 */
public class BestRouteStrategy extends Strategy {

	public BestRouteStrategy(OnInterestReceived onInterestReceived, Name prefix) {
		super(onInterestReceived, prefix);
	}

	@Override
	public Face[] determineOutgoingFaces(Interest interest, Forwarder forwarder) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public class BestRouteStrategyInfo extends StrategyInfo {

		private static final int typeId = 1;

		@Override
		public int getTypeId() {
			return typeId;
		}
	}
}
