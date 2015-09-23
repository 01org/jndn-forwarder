/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table.deadnonce;

import net.named_data.jndn.Name;
import net.named_data.jndn.util.Blob;

/**
 *
 * @author zht
 */
public interface DeadNonce {

	public void add(Name name, Blob nonce);

	public void evictStaleEntries();

	public boolean find(Name name, Blob nonce);

	public int size();

	public long getLifetime();
}
