/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table;

/**
 *
 * @author Haitao Zhang <zhtaoxiang@gmail.com>
 */
public class Pair<V> {

	public Pair(V first, boolean second) {
		this.first = first;
		this.second = second;
	}

	public V getFirst() {
		return first;
	}

	public boolean isSecond() {
		return second;
	}

	private V first;
	private boolean second;
}
