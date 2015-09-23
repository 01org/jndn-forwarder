/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table.measurement;

import net.named_data.jndn.Name;

/**
 *
 * @author Haitao Zhang <zhtaoxiang@gmail.com>
 */
public class MeasurementEntry {

	public MeasurementEntry() {

	}

	public MeasurementEntry(Name name) {
		this.name = name;
		expiry = -1;
	}

	public Name getName() {
		return name;
	}

	public long getExpiry() {
		return expiry;
	}

	public void setExpiry(long expiry) {
		this.expiry = expiry;
	}

	private Name name;
	private long expiry;

}
