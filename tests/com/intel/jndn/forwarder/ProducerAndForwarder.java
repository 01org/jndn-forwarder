/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder;

import com.intel.jndn.forwarder.api.callbacks.OnCompleted;
import com.intel.jnfd.deamon.face.FaceUri;
import static com.intel.jnfd.deamon.face.tcp.TcpFaceTest.configure;
import com.intel.jnfd.deamon.table.fib.FibEntry;
import net.named_data.jndn.Face;
import net.named_data.jndn.Name;
import net.named_data.jndn.security.KeyChain;
import org.junit.Before;

/**
 *
 * @author zht
 */
public class ProducerAndForwarder {

	public static final Name PREFIX = new Name("/test");
	private Face producer;
	private Forwarder forwarder;

	@Before
	public void setUp() throws Exception {
		forwarder = new Forwarder();
		forwarder.addNextHop(PREFIX, new FaceUri(""), 0, new OnCompleted<FibEntry>() {

			@Override
			public void onCompleted(FibEntry result) {

			}

		});
		producer = new Face();
		setupProducer(producer);

		Thread.sleep(500);
	}

	private void setupProducer(final Face faceB) throws net.named_data.jndn.security.SecurityException {
		KeyChain keyChain = configure(new Name("/producer"));
		faceB.setCommandSigningInfo(keyChain, keyChain.getDefaultCertificateName());
	}
}
