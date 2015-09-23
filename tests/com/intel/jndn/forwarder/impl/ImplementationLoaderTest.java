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
package com.intel.jndn.forwarder.impl;

import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public class ImplementationLoaderTest {

	@Before
	public void setup() {
		ImplementationLoader.clear();
	}

	@Test
	public void testLoadNone() {
		List<TestInterface> load = ImplementationLoader.load(TestInterface.class);
		assertEquals(0, load.size());
	}

	@Test
	public void testLoadManually() {
		ImplementationLoader.register(new TestClass());
		List<TestInterface> load = ImplementationLoader.load(TestInterface.class);
		assertEquals(1, load.size());
	}

	public interface TestInterface {

	}

	public class TestClass implements TestInterface {

	}
}
