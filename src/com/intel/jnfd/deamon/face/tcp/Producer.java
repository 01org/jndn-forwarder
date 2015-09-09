/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face.tcp;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.named_data.jndn.Data;
import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.InterestFilter;
import net.named_data.jndn.Name;
import net.named_data.jndn.OnInterestCallback;
import net.named_data.jndn.util.Blob;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public class Producer implements OnInterestCallback {

	private static final Logger logger = Logger.getLogger(Producer.class.getName());
	private Integer count = 0;
	private Blob blob = buildRandom(100);

	@Override
	public void onInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
		// synchronized (count) {
			try {
				System.out.println("Interest received: " + interest.toUri());
				Data data = new Data(new Name(interest.getName()).appendSegment(count));
				data.setContent(blob);
				data.getMetaInfo().setFreshnessPeriod(1000);
				face.putData(data);
				//count++;
			} catch (IOException ex) {
				logger.log(Level.SEVERE, "Data write failed.", ex);
			}
		// }
	}

	private static Blob buildRandom(int size) {
		byte[] bytes = new byte[size];
		Random random = new Random();
		random.nextBytes(bytes);
		return new Blob(bytes);
	}
}
