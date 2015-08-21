/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.fw;

import com.intel.jndn.forwarder.api.Strategy;

/**
 *
 * @author zht
 */
public class BestRouteStrategy extends Strategy {

	public class BestRouteStrategyInfo extends StrategyInfo {

		private static final int typeId = 1;

		@Override
		public int getTypeId() {
			return typeId;
		}
	}
}
