package ru.kkey;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Framework create file system tree for list of strings
 * <p/>
 * Spaces before dir/file names is used for define nested files/dirs.
 * <p/>
 * Example:
 * <pre>
 * dir
 *  dir1
 *  file1
 * </pre>
 * Here "dir1" is subdirectory for "dir".
 * Directory "dir" contains nested file with name (and content) "file1"
 *
 * @author anstarovoyt
 */
public class FSBuilder
{
	public static final String COMMON_PATH = "src/test";
	public static final String DIR_FOR_TEST_TREE = COMMON_PATH + "/testtree";

	private static int countWhiteSpaces(String str)
	{
		int i = -1;
		while (str.length() > ++i && Character.isWhitespace(str.charAt(i)))
		{
		}
		return i;
	}

	private static boolean isDir(String str)
	{
		return str.trim().startsWith("dir");
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

	public void build(List<String> strings)
	{
		try
		{
			if (Files.exists(Paths.get(DIR_FOR_TEST_TREE)))
			{
				//external lib because it is very boring impl recursive deletion
				FileUtils.deleteDirectory(new File(DIR_FOR_TEST_TREE));
			}

			Files.createDirectory(Paths.get(DIR_FOR_TEST_TREE));

			List<String> path = new ArrayList<>();
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
					OutputStream out = Files.newOutputStream(Paths.get(currentPath));
					out.write(Charset.forName("UTF-8").encode(currentValue.trim()).array());
					out.flush();
					out.close();
				}
			}
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
