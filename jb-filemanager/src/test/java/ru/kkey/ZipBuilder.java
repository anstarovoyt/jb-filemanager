package ru.kkey;

import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.util.List;

/**
 * @author anstarovoyt
 */
public class ZipBuilder extends FSBuilder
{
	public static final String ZIP_NAME = COMMON_PATH + "/ziptest.zip";

	@Override
	public void build(List<String> strings)
	{
		super.build(strings);

        //external lib because it is very boring impl recursive zipping
		ZipUtil.pack(new File(DIR_FOR_TEST_TREE), new File(ZIP_NAME));
	}

}
