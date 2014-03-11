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

		ZipUtil.pack(new File(DIR_FOR_TEST_TREE), new File(ZIP_NAME));
	}

}