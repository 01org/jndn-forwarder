/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table;

import com.intel.jndn.forwarder.api.NameTable;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.named_data.jndn.Name;

/**
 *
 * @author zht
 * @param <V>
 */
public class HashMapRepo<V> implements NameTable<V> {

    public HashMapRepo() {
        repo = new ConcurrentHashMap<>();
    }

    @Override
    public int size() {
        return repo.size();
    }

    @Override
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

    @Override
    public V findExactMatch(Name prefix) {
        return repo.get(prefix);
    }

    @Override
    public void insert(Name prefix, V value) {
        repo.put(prefix, value);
    }

    @Override
    public V erase(Name prefix) {
        return repo.remove(prefix);
    }

    @Override
    public boolean hasKey(Name key) {
        return repo.containsKey(key);
    }

    @Override
    public Set<Entry<Name, V>> EntrySet() {
        return repo.entrySet();
    }

    @Override
    public Collection<V> values() {
        return repo.values();
    }

    private final Map<Name, V> repo;
}
