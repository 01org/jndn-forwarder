/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder.api;

import com.intel.jndn.forwarder.api.Strategy;
import com.intel.jnfd.deamon.table.Pair;
import com.intel.jnfd.deamon.table.strategy.StrategyChoiceEntry;
import java.util.Collection;
import net.named_data.jndn.Name;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public interface StrategyChoiceTable {

	public boolean install(Strategy strategy);

	public boolean insert(Name prefix, Name strategyName);

	public boolean hasStrategy(Name strategyName, boolean isExact);

	public void erase(Name prefix);

	public Strategy findEffectiveStrategy(Name prefix);

	public Collection<StrategyChoiceEntry> list();

	public int size();

}
