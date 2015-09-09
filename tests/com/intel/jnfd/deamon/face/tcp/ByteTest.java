/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
