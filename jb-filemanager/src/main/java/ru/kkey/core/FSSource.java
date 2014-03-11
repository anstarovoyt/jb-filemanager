package ru.kkey.core;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Data source for file system navigation
 *
 * @author anstarovoyt
 */
public class FSSource implements Source
{
	public static final SourceFactory FACTORY = new SourceFactory()
	{
		@Override
		public Source create(String path)
		{
			return new FSSource(path);
		}
	};

	volatile Path currentPath;

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	public FSSource(Path path)
	{
		if (!Files.exists(path) || !Files.isDirectory(path))
		{
			throw new RuntimeException("Incorrect path");
		}
		this.currentPath = path;
	}

	public FSSource(String defaultPath)
	{
		this(Paths.get(defaultPath).toAbsolutePath());

	}

	@Override
	public void destroy()
	{

	}

	@Override
	public byte[] getFile(FileItem item)
	{
		try
		{
			return Utils.readInputSteamToByteArray(Files.newInputStream(geFileMap().get(item)));
		} catch (AccessDeniedException e)
		{
			throw new RuntimeException("Access denied error");
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public Source getSourceFor(FileItem item)
	{
		Path path = geFileMap().get(item);

		if (ZipSource.isZip(path))
		{
			return new ZipSource(path.toString());
		}

		return null;
	}

	@Override
	public boolean goBack()
	{
		lock.writeLock().lock();
		try
		{
			Path parent = currentPath.getParent();
			Path prev = currentPath;
			if (null != parent)
			{
				currentPath = parent;
				return !parent.equals(prev);
			}
			return false;
		} finally
		{
			lock.writeLock().unlock();
		}
	}

	@Override
	public void goInto(FileItem item)
	{
		lock.writeLock().lock();
		try
		{
			Map<FileItem, Path> fileItemFileMap = geFileMap();
			Path file = fileItemFileMap.get(item);
			if (Files.isDirectory(file))
			{
				currentPath = file;
			}
		} finally
		{
			lock.writeLock().unlock();
		}
	}

	@Override
	public List<FileItem> listFiles()
	{
		Map<FileItem, Path> fileItemFileMap = geFileMap();
		ArrayList<FileItem> list = new ArrayList<>(fileItemFileMap.keySet());
		Collections.sort(list);
		return list;
	}

	private Map<FileItem, Path> geFileMap()
	{
		lock.readLock().lock();

		try
		{
			if (!Files.exists(currentPath))
			{
				throw new RuntimeException("Path doesn't exist");
			}
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
				Path fileName = file.getFileName();
				String stringName = fileName == null ? "" : fileName.toString();
				result.put(new FileItem(stringName, Files.isDirectory(file)), file);
			}

			return result;

		} catch (AccessDeniedException e)
		{
			throw new RuntimeException("Access denied error");
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		} finally
		{
			lock.readLock().unlock();
		}
	}

}
