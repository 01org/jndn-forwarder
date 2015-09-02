/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table.pit;

import com.intel.jnfd.deamon.table.Pair;
import java.util.List;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public class PitTest {
	
	Pit instance = new Pit();

	@Test
	public void testSize() {
		assertEquals(0, instance.size());
	}

	@Test
	public void testInsert() {
		insertInterest("/a");
		assertEquals(1, instance.size());
		
		insertInterest("/a");
		assertEquals(1, instance.size());
		
		insertInterest("/b");
		assertEquals(2, instance.size());
	}

	private void insertInterest(String name) {
		instance.insert(new Interest(new Name(name)));
	}

	@Test
	public void testFindAllMatches() {
		insertInterest("/a");
		insertInterest("/a/b");
		insertInterest("/a/b/c");
		insertInterest("/b");
		
		List<PitEntry> matches = instance.findAllMatches(new Data(new Name("/a/b")));
		assertEquals(2, matches.size());
	}

	@Test
	public void testErase() {
		PitEntry entry = instance.insert(new Interest(new Name("/a"))).getFirst();
		assertEquals(1, instance.size());
		
		instance.erase(entry);
		assertEquals(0, instance.size());
	}
	
}
