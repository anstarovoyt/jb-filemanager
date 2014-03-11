package ru.kkey.core;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data source for zip file navigation (without direct unpack)
 * <p/>
 * Implementation used virtual zip file system
 *
 * @author anstarovoyt
 * @see FileSystem
 */
public class ZipSource extends FSSource
{
	public static final SourceFactory FACTORY = new SourceFactory()
	{
		@Override
		public Source create(String path)
		{
			return new ZipSource(path);
		}
	};

	private static final int ZIP_MAGIC_CONST = 0x504b0304;
	private static final Logger logger = Logger.getAnonymousLogger();

	/**
	 * Try define zip archive without extension info (can be zip, jar, war, etc)
	 */
	public static boolean isZip(Path path)
	{
		if (!Files.isDirectory(path))
		{
			try (InputStream newInputStream = Files.newInputStream(path);
				 DataInputStream fileInputStream = new DataInputStream(newInputStream))
			{
				int test = fileInputStream.readInt();

				if (test == ZIP_MAGIC_CONST)
				{
					return true;
				}
			} catch (Exception e)
			{
				logger.log(Level.FINE, e.getMessage(), e);
			}
		}

		return false;
	}

	private static Path getZipFileSystemPath(String pathToFile)
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
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public ZipSource(String pathToFile)
	{
		super(getZipFileSystemPath(pathToFile));
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
		} catch (Exception e)
		{
			logger.log(Level.FINE, e.getMessage(), e);
		}
	}

	@Override
	public Source getSourceFor(FileItem item)
	{
		return null;
	}
}
