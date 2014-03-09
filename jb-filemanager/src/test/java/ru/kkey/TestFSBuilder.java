package ru.kkey;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author anstarovoyt
 */
public class TestFSBuilder
{
	public static final String COMMON_PATH = "src/test";
	public static final String DIR_FOR_TEST_TREE = COMMON_PATH + "/testtree";

	public static void build(String fileTreeSource) throws IOException
	{
		List<String> strings = Files.readAllLines(Paths.get(fileTreeSource), Charset.defaultCharset());

		if (Files.exists(Paths.get(DIR_FOR_TEST_TREE)))
		{
			//external lib because it is very boring impl recursive deletion
			FileUtils.deleteDirectory(new File(DIR_FOR_TEST_TREE));
		}

		Files.createDirectory(Paths.get(DIR_FOR_TEST_TREE));

		List<String> path = new ArrayList<String>();
		for (String currentValue : strings)
		{
			int count = countWhiteSpaces(currentValue);

			path = path.subList(0, count);

			String currentPath = DIR_FOR_TEST_TREE + "/" + join(path) + "/" + currentValue.trim();
			if (isDir(currentValue))
			{
				Files.createDirectory(Paths.get(currentPath));
				path.add(currentValue.trim());
			} else
			{
				Files.createFile(Paths.get(currentPath));
			}
		}
	}

	private static int countWhiteSpaces(String str)
	{
		int i = -1;
		while (str.length() > ++i && Character.isWhitespace(str.charAt(i))) ;
		return i;
	}

	private static String join(List<String> str)
	{
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < str.size(); i++)
		{
			if (i > 0)
			{
				builder.append('/');
			}
			builder.append(str.get(i));
		}
		return builder.toString();
	}

	private static boolean isDir(String str)
	{
		return str.trim().startsWith("dir");
	}
}
