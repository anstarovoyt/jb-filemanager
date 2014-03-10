package ru.kkey;

import ru.kkey.core.FSSource;
import ru.kkey.core.Source;

/**
 * @author anstarovoyt
 */
public class FSSourceTest extends FileSourceTestBase
{
	@Override
	protected Source createSource()
	{
		return new FSSource(TestFSBuilder.DIR_FOR_TEST_TREE);
	}

	@Override
	protected TestFSBuilder createBuilder()
	{
		return new TestFSBuilder();
	}
}
