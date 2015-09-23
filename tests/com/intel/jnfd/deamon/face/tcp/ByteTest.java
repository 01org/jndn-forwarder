/*
 * jndn-forwarder
 * Copyright (c) 2015, Intel Corporation.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms and conditions of the GNU Lesser General Public License,
 * version 3, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 */
package com.intel.jnfd.deamon.face.tcp;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public class ByteTest {

	public ByteTest() {
	}

	@Before
	public void setUp() {
	}

	// TODO add test methods here.
	// The methods must be annotated with annotation @Test. For example:
	//
	@Test
	public void hello() {
		byte a = 0x7F; // 
		System.out.println(a);
		byte b = (byte) 0x81; // -129 - -128
		System.out.println(b);
		int compare = Byte.compare(b, a);
		System.out.println(compare);

		int c = 127;
		int d = 128;
		System.out.println(Integer.compare(d, c));
	}
}
