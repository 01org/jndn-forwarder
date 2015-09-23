/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder.api;

import com.intel.jnfd.deamon.table.cs.SearchCsCallback;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public interface ContentStore {

	/**
	 * @param data the data to insert
	 * @param isUnsolicited true if the data was not solicited by an interest;
	 * content stores may use this information in their eviction policies
	 * @return true if the data was added to the content store, false otherwise;
	 * this is necessary because of different caching policies
	 */
	public boolean insert(Data data, boolean isUnsolicited);

	/**
	 * @param exactName the exact name of the data to erase
	 * @return the data erased
	 */
	public Data erase(Name exactName);

	/**
	 * Find a data packet asynchronously; necessary due to future disk-based
	 * content stores
	 *
	 * @param interest the interest to search with
	 * @param searchCsCallback the class called when the operation completes
	 */
	public void find(Interest interest, SearchCsCallback searchCsCallback);

	/**
	 * @return the number of data packets in the content store
	 */
	public int size();

	/**
	 * @return the maximum number of data packets allowed in the content store
	 */
	public int limit();

	/**
	 * Change the maximum number of data packets allowed
	 *
	 * @param maxNumberOfDataPackets the new maximum limit
	 */
	public void limit(int maxNumberOfDataPackets);
}
