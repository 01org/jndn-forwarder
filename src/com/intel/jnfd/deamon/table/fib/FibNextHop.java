/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table.fib;

import com.intel.jndn.forwarder.api.Face;

/**
 *
 * @author zht
 */
public class FibNextHop implements Comparable {

	public FibNextHop(Face face) {
		this.face = face;
	}

	public FibNextHop(Face face, long cost) {
		this.face = face;
		this.cost = cost;
	}

	public Face getFace() {
		return face;
	}

	public long getCost() {
		return cost;
	}

	public void setCost(long cost) {
		this.cost = cost;
	}

	private Face face;
	private long cost;

	@Override
	public int compareTo(Object o) {
		if (cost < ((FibNextHop) o).getCost()) {
			return -1;
		}
		if (cost > ((FibNextHop) o).getCost()) {
			return 1;
		}
		return 0;
	}
}
