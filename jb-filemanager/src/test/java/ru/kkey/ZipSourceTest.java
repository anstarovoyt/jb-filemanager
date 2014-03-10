package ru.kkey;

import org.junit.After;
import org.junit.Test;
import ru.kkey.core.FileSource;
import ru.kkey.core.ZipSource;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author anstarovoyt
 */
public class ZipSourceTest extends FileSourceTestBase
{
	ZipSource zipSource;

	@After
	public void after()
	{
		if (null != zipSource)
		{
			zipSource.destroy();
		}
	}

	@Override
	protected FileSource createSource()
	{
		zipSource = new ZipSource(TestZipBuilder.ZIP_NAME);
		return zipSource;
	}

	@Override
	protected TestFSBuilder createBuilder()
	{
		return new TestZipBuilder();
	}

	@Test
	public void zipTest() throws IOException
	{
		List<String> paths = Arrays.asList(
				"dir 1",
				"dir 2",
				"dir 3",
				"file 1"
		);
		createBuilder().build(paths);

		Path zipFile = Paths.get(TestZipBuilder.ZIP_NAME).toAbsolutePath();

		assertTrue(Files.exists(zipFile));

		try (FileSystem fileSystem = FileSystems.newFileSystem(zipFile, null))
		{
			Path root = fileSystem.getPath("/");

			Files.walkFileTree(root, new FileVisitor<Path>()
			{
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
				{
					System.out.println(dir);
					if (dir.equals(Paths.get("/")))
					{
						return FileVisitResult.CONTINUE;
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
				{
					System.out.println(file.getFileName().toString());
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
				{
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
				{
					return FileVisitResult.CONTINUE;
				}
			});
		}
	}
}
