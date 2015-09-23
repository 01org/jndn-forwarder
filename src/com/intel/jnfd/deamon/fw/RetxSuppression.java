/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.fw;

import com.intel.jndn.forwarder.api.Face;
import com.intel.jnfd.deamon.table.pit.PitEntry;
import com.intel.jnfd.deamon.table.pit.PitOutRecord;
import java.util.List;
import net.named_data.jndn.Interest;

/**
 *
 * @author Haitao Zhang <zhtaoxiang@gmail.com>
 */
public abstract class RetxSuppression {

	public enum Result {

		/**
		 * Interest is new (not a retransmission)
		 */
		NEW,
		/**
		 * Interest is retransmission and should be forwarded
		 */
		FORWARD,
		/**
		 * Interest is retransmission and should be suppressed
		 */
		SUPPRESS
	}

	public abstract Result decide(Face inFace, Interest interest,
			PitEntry pitEntry);

	protected long getLastOutgoing(PitEntry pitEntry) {
		List<PitOutRecord> outRecords = pitEntry.getOutRecords();
		long result = 0;
		for (PitOutRecord one : outRecords) {
			if (result < one.getLastRenewed()) {
				result = one.getLastRenewed();
			}
		}
		return result;
	}
}
