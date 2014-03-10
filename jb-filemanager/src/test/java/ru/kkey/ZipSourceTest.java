package ru.kkey;

import ru.kkey.core.Source;
import ru.kkey.core.ZipSource;

/**
 * @author anstarovoyt
 */
public class ZipSourceTest extends FileSourceTestBase
{
	@Override
	protected Source createSource()
	{
		return new ZipSource(TestZipBuilder.ZIP_NAME);
	}

	@Override
	protected TestFSBuilder createBuilder()
	{
		return new TestZipBuilder();
	}
}
