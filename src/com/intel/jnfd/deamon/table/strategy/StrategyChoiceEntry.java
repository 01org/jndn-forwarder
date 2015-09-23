/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table.strategy;

import com.intel.jndn.forwarder.api.Strategy;
import net.named_data.jndn.Name;

/**
 *
 * @author Haitao Zhang <zhtaoxiang@gmail.com>
 */
public class StrategyChoiceEntry {

    public StrategyChoiceEntry(Name prefix) {
        this.prefix = prefix;
        strategy = null;
    }

    public StrategyChoiceEntry(Name prefix, Strategy strategy) {
        this.prefix = prefix;
        this.strategy = strategy;
    }

    public Name getPrefix() {
        return prefix;
    }

    public void setPrefix(Name prefix) {
        this.prefix = prefix;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public Name getStrategyName() {
        return strategy.getName();
    }

    private Name prefix;
    private Strategy strategy;
}
