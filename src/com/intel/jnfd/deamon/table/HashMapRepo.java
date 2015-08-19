/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.named_data.jndn.Name;

/**
 *
 * @author zht
 */
public class HashMapRepo<V> {

    public HashMapRepo() {
        repo = new ConcurrentHashMap<>();
    }

    public int size() {
        return repo.size();
    }

    public V findLongestPrefixMatch(Name prefix) {
        V result = null;
        for (int i = prefix.size(); i >= 0; i--) {
            Name p = prefix.getPrefix(i);
            V r = findExactMatch(p);
            if (r != null) {
                return result;
            }
        }
        return null;
    }
    
    public V findLongestPrefixMatch(Name prefix, EntryFilter filter) {
        V result = null;
        for (int i = prefix.size(); i >= 0; i--) {
            Name p = prefix.getPrefix(i);
            V r = findExactMatch(p);
            if (r != null && filter.filt(r)) {
                return result;
            }
        }
        return null;
    }

    public V findExactMatch(Name prefix) {
        return repo.get(prefix);
    }
    
    public void insert(Name prefix, V value) {
        repo.put(prefix, value);
    }

    public void erase(Name prefix) {
        repo.remove(prefix);
    }

    public boolean hasKey(Name key) {
        return repo.containsKey(key);
    }
    
    public Set<Entry<Name, V>> EntrySet() {
        return repo.entrySet();
    }

    private Map<Name, V> repo;
}
