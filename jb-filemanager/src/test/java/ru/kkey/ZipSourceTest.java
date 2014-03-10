package ru.kkey;

import ru.kkey.core.FileSource;
import ru.kkey.core.ZipSource;

/**
 * @author anstarovoyt
 */
public class ZipSourceTest extends FileSourceTestBase
{
	@Override
	protected FileSource createSource()
	{
		return new ZipSource(TestZipBuilder.ZIP_NAME);
	}

	@Override
	protected TestFSBuilder createBuilder()
	{
		return new TestZipBuilder();
	}
}
