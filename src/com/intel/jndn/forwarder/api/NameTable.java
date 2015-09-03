/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.named_data.jndn.Name;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 * @param <V>
 */
public interface NameTable<V> {

	public Set<Map.Entry<Name, V>> EntrySet();
	
	public Collection<V> values();

	public void insert(Name prefix, V value);

	public V erase(Name prefix);

	public V findExactMatch(Name prefix);

	public V findLongestPrefixMatch(Name prefix);
        
        public List<V> findAllMatch(Name prefix);

	public boolean hasKey(Name key);

	public int size();
}
