/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * Combines SPI-loaded implementations with a manual registration process
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public class ImplementationLoader {

	/**
	 *
	 * @param <T>
	 * @param type
	 * @return
	 */
	public static <T> List<T> load(Class<T> type) {
		java.util.ServiceLoader<T> load = java.util.ServiceLoader.load(type);
		List<T> implementations = new ArrayList<>();

		// add SPI-loaded implementations
		for (T implementation : load) {
			implementations.add(implementation);
		}

		// add manually registered implementations
		for (Object implementation : manuallyRegisteredImplementations) {
			if (type.isInstance(implementation)) {
				implementations.add((T) implementation);
			}
		}

		return implementations;
	}

	/**
	 *
	 * @param implementation
	 */
	public static void register(Object implementation) {
		manuallyRegisteredImplementations.add(implementation);
	}

	/**
	 *
	 */
	public static void clear() {
		manuallyRegisteredImplementations.clear();
	}

	private static final List manuallyRegisteredImplementations = new ArrayList();
}
