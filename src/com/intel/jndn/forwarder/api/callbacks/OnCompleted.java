/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder.api.callbacks;

/**
 * Similar interface to {@link java.nio.channels.CompletionHandler} to allow
 * consistent pattern for completing actions;
 * {@link java.nio.channels.CompletionHandler} is not used in case a
 * lower/different version of Java is used (CompletionHandler is published in
 * 1.7).
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 * @param <R> the result type of a successful action
 */
public interface OnCompleted<R> {

	void onCompleted(R result);
}
