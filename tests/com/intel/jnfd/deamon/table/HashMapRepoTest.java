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
package com.intel.jnfd.deamon.table;

import net.named_data.jndn.Name;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public class HashMapRepoTest {

	HashMapRepo instance = new HashMapRepo();

	@Test
	public void testFindLongestPrefixMatch_Name() {
		Object a = new Object();
		Object ab = new Object();
		Object abc = new Object();
		instance.insert(new Name("/a"), a);
		instance.insert(new Name("/a/b"), ab);
		instance.insert(new Name("/a/b/c"), abc);

		assertEquals(abc, instance.findLongestPrefixMatch(new Name("/a/b/c/d")));
		assertEquals(ab, instance.findExactMatch(new Name("/a/b")));
		assertNull(instance.findLongestPrefixMatch(new Name("/")));
	}

	@Test
	public void testInsertAndErase() {
		assertEquals(0, instance.size());
		instance.insert(new Name("/a"), new Object());
		assertEquals(1, instance.size());
		instance.erase(new Name("/a"));
		assertEquals(0, instance.size());
	}

	@Test
	public void testHasKey() {
		instance.insert(new Name("/a"), new Object());
		assertTrue(instance.hasKey(new Name("/a")));
		assertFalse(instance.hasKey(new Name()));
	}
}
