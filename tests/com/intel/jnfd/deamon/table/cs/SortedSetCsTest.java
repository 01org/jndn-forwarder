/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table.cs;

import com.intel.jndn.forwarder.api.ContentStore;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public class SortedSetCsTest {

	ContentStore cs = new SortedSetCs();

	@Before
	public void setUp() {
		cs.insert(createData("/a"), true);
		cs.insert(createData("/a/b"), true);
		cs.insert(createData("/a/b/c"), true);
		cs.insert(createData("/a/b/b"), true);
	}

	@Test
	public void testRetrieval() {
		assertDataExists("/a/b/c");
		assertDataExists("/a");
		assertDataDoesNotExist("/");
		assertDataDoesNotExist("/a/b/c/d");
	}

	@Test
	public void testErase() {
		cs.erase(new Name("/a/b/c/d"));

		assertDataExists("/a");
		assertDataExists("/a/b");
		assertDataExists("/a/b/c");
		assertDataDoesNotExist("/a/b/c/d");
	}

	@Test
	public void testLimits() {
		ContentStore limitedCs = new SortedSetCs(3);
		assertEquals(3, limitedCs.limit());

		limitedCs.insert(createData("/a"), true);
		limitedCs.insert(createData("/b"), true);
		limitedCs.insert(createData("/c"), true);
		assertEquals(3, limitedCs.size());

		limitedCs.insert(createData("/d"), true);
		assertEquals(3, limitedCs.size());

		assertDataExists("/d", limitedCs);
		assertDataDoesNotExist("/a", limitedCs);
	}

	private Data createData(String name) {
		return new Data(new Name(name));
	}

	private void assertDataExists(String name) {
		assertDataExists(name, this.cs);
	}

	private void assertDataExists(String name, ContentStore cs) {
		assertNotNull(retrieve(name, cs));
	}

	private void assertDataDoesNotExist(String name) {
		assertDataDoesNotExist(name, this.cs);
	}

	private void assertDataDoesNotExist(String name, ContentStore cs) {
		assertNull(retrieve(name, cs));
	}

	private Data retrieve(String name, ContentStore contentStore) {
		FoundReference found = new FoundReference();
		FoundCallback callback = new FoundCallback(found);
		contentStore.find(new Interest(new Name(name)), callback);
		return found.data;
	}

	private class FoundReference {

		public Data data;
	}

	private class FoundCallback implements SearchCsCallback {

		final FoundReference found;

		public FoundCallback(FoundReference found) {
			this.found = found;
		}

		@Override
		public void onContentStoreHit(Interest interest, Data data) {
			found.data = data;
		}

		@Override
		public void onContentStoreMiss(Interest interest) {
			found.data = null;
		}
	}

}
