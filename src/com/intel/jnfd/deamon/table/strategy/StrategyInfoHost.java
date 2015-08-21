/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table.strategy;

import com.intel.jnfd.deamon.fw.StrategyInfo;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author zht
 */
public class StrategyInfoHost {

    public StrategyInfo getStrategyInfo(int typeId) {
        return items.get(typeId);
    }

    public void setStrategyInfo(StrategyInfo item) {
        if (item == null) {
            items.remove(item.getTypeId());
        } else {
            items.put(item.getTypeId(), item);
        }
    }

    public <T extends StrategyInfo> StrategyInfo getOrCreateStrategyInfo(Class<T> clazz)
            throws InstantiationException, IllegalAccessException {
        StrategyInfo item = clazz.newInstance();
        StrategyInfo oldItem = items.get(item.getTypeId());
        if (oldItem != null) {
            return oldItem;
        }
        items.put(item.getTypeId(), item);
        return item;
    }

    public void clearStrategyInfo() {
        items.clear();
    }

    private Map<Integer, StrategyInfo> items = new ConcurrentHashMap<>();
}
