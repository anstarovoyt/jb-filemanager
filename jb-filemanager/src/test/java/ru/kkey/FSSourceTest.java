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
		return new FSSource(FSBuilder.DIR_FOR_TEST_TREE);
	}

	@Override
	protected FSBuilder createBuilder()
	{
		return new FSBuilder();
	}
}
