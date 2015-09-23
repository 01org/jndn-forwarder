/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table.measurement;

import com.intel.jndn.forwarder.api.Strategy;
import com.intel.jnfd.deamon.table.EntryFilter;
import com.intel.jnfd.deamon.table.strategy.StrategyChoice;
import net.named_data.jndn.Name;

/**
 *
 * @author zht
 */
public class MeasurementAccessor {

	public MeasurementAccessor(Measurement measurement,
			StrategyChoice strategyChoice, Strategy strategy) {
		this.measurement = measurement;
		this.strategyChoice = strategyChoice;
		this.strategy = strategy;
	}

	public MeasurementEntry get(Name name) {
		return filter(measurement.get(name));
	}

	/**
	 * find or insert a Measurements entry for child's parent
	 *
	 * @param child
	 * @return null if child is the root entry
	 */
	public MeasurementEntry getParentMeasurementEntry(MeasurementEntry child) {
		return filter(measurement.getParentMeasurementEntry(child));
	}

	public MeasurementEntry findLongestPrefixMatch(Name name, EntryFilter filter) {
		return filter(measurement.findLongestPrefixMatch(name, filter));
	}

	public MeasurementEntry findLongestPrefixMatch(Name name) {
		return filter(measurement.findLongestPrefixMatch(name));
	}

	public MeasurementEntry findExactMatch(Name name) {
		return filter(measurement.findExactMatch(name));
	}

	public void extendLifetime(MeasurementEntry entry, long lifetime) {
		measurement.extendLifetime(entry, lifetime);
	}

	private MeasurementEntry filter(MeasurementEntry entry) {
		if (entry == null) {
			return null;
		}
		Strategy effectiveStrategy
				= strategyChoice.findEffectiveStrategy(entry.getName());
		// FIX: how to compare the strategy in a proper way
		if (effectiveStrategy.equals(strategy)) {
			return entry;
		}
		return new MeasurementEntry();
	}

	private Measurement measurement;
	private StrategyChoice strategyChoice;
	private Strategy strategy;
}
