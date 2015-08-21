/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder.api.callbacks;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 * @param <T>
 */
public interface OnFailed<T extends Throwable> {

	public void onFailed(T error);
}
