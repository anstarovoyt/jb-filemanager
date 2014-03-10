package ru.kkey.core;

import java.io.IOException;
import java.nio.file.*;

/**
 * @author anstarovoyt
 */
public class ZipSource extends FSSource
{
	private static Path getZipInnerPath(String pathToFile)
	{
		try
		{
			Path zipFile = Paths.get(pathToFile).toAbsolutePath();

			if (!Files.exists(zipFile))
			{
				throw new RuntimeException("File doesn't exist");
			}

			FileSystem fs = FileSystems.newFileSystem(zipFile, null);
			return fs.getPath("/");
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public ZipSource(String pathToFile)
	{
		super(getZipInnerPath(pathToFile));
	}

	@Override
	public void destroy()
	{
		try
		{
			if (null != currentPath)
			{
				currentPath.getFileSystem().close();
			}
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
