package ru.kkey;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * @author anstarovoyt
 */
public class FSFileSourceTest
{
	public static final String SRC_TEST_STRUCTURE_TXT = TestFSBuilder.COMMON_PATH + "/struct.txt";

	@BeforeClass
	public static void before() throws IOException
	{
		TestFSBuilder.build(SRC_TEST_STRUCTURE_TXT);
	}

	@Test
	public void testFSFileSource()
	{
		assertTrue(true);
	}

}
