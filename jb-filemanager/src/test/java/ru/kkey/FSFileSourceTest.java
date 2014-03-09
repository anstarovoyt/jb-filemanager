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
	@BeforeClass
	public static void before() throws IOException
	{
		TestFSBuilder.build();
	}

	@Test
	public void testFSFileSource()
	{
		assertTrue(true);
	}

}
