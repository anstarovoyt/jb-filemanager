package ru.kkey;

import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.util.List;

/**
 * @author anstarovoyt
 */
public class TestZipBuilder extends TestFSBuilder
{
	public static final String ZIP_NAME = COMMON_PATH + "/ziptest.zip";

	@Override
	public void build(List<String> strings)
	{
		super.build(strings);

		addToZip();
	}

	public void addToZip()
	{

		String zipFile = ZIP_NAME;

		String srcDir = DIR_FOR_TEST_TREE;

		try
		{
			ZipUtil.pack(new File(srcDir), new File(zipFile));
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
