package ru.kkey.core;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

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

			URI uri = URI.create("jar:file:" + zipFile.toString());

			//FileSystem fs = FileSystems.newFileSystem(zipFile, null);
			Map<String, ?> env = new HashMap<>();
			FileSystem fs = FileSystems.newFileSystem(uri, env);
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

	public void destroy()
	{
		try
		{
			currentPath.getFileSystem().close();
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
