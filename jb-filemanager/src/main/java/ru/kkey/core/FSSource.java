package ru.kkey.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Data source for file system navigation
 *
 * @author anstarovoyt
 */
public class FSSource implements FileSource
{
	private volatile Path currentPath;

	public FSSource(String defaultPath)
	{
		this.currentPath = Paths.get(defaultPath).toAbsolutePath();
	}

	@Override
	public List<FileItem> getFiles()
	{
		Map<FileItem, Path> fileItemFileMap = geFileMap();
		ArrayList<FileItem> list = new ArrayList<>(fileItemFileMap.keySet());
		Collections.sort(list);
		return list;
	}

	@Override
	public void goInto(FileItem item)
	{
		Map<FileItem, Path> fileItemFileMap = geFileMap();
		Path file = fileItemFileMap.get(item);
		if (Files.isDirectory(file))
		{
			currentPath = file;
		}
	}

	@Override
	public InputStream getFileStream(FileItem item)
	{
		try
		{

			return Files.newInputStream(geFileMap().get(item));

		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void goBack()
	{
		Path parent = currentPath.getParent();
		if (null != parent)
		{
			currentPath = parent;
		}
	}

	private Map<FileItem, Path> geFileMap()
	{
		if (!Files.exists(currentPath))
		{
			throw new RuntimeException("Path doesn't exist");
		}

		try
		{
			List<Path> files = new ArrayList<>();

			//file.listFiles returns incorrect name
			DirectoryStream<Path> paths = Files.newDirectoryStream(currentPath);

			for (Path path : paths)
			{
				files.add(path);
			}


			Map<FileItem, Path> result = new HashMap<>();

			for (Path file : files)
			{
				result.put(new FileItem(file.getFileName().toString(), Files.isDirectory(file)), file);
			}

			return result;

		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
