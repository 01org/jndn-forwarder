/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table.fib;

import com.intel.jndn.forwarder.api.Face;
import com.intel.jnfd.deamon.face.AbstractFace;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.named_data.jndn.Name;

/**
 *
 * @author Haitao Zhang <zhtaoxiang@gmail.com>
 */
public class FibEntry {

	public FibEntry(Name prefix) {
		this.prefix = prefix;
	}

	public Name getPrefix() {
		return prefix;
	}

	public List<FibNextHop> getNextHopList() {
		return nextHopList;
	}

	public boolean hasNextHop() {
		return nextHopList.isEmpty();
	}

	public boolean hasNextHop(Face face) {
		return findNextHop(face) != null;
	}

	public void addNextHop(Face face, long cost) {
		FibNextHop nextHop = findNextHop(face);
		if (nextHop == null) {
			nextHopList.add(new FibNextHop(face, cost));
		} else {
			nextHop.setCost(cost);
		}
		sortNextHops();
	}

	public void removeNextHop(Face face) {
		FibNextHop nextHop = findNextHop(face);
		if (nextHop != null) {
			nextHopList.remove(nextHop);
		}
	}

	private void sortNextHops() {
		Collections.sort(nextHopList);
	}

	public FibNextHop findNextHop(Face face) {
		for (FibNextHop one : nextHopList) {
			if (one.getFace() == face || one.getFace().equals(face)) {
				return one;
			}
		}
		return null;
	}

	private Name prefix;
	private List<FibNextHop> nextHopList = new ArrayList<>();
}
