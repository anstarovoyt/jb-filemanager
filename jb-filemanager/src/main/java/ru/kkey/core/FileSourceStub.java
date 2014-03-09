package ru.kkey.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author anstarovoyt
 */
public class FileSourceStub implements FileSource
{
	int dirIndex = 0;

	@Override
	public List<FileItem> getFiles()
	{
		List<FileItem> result = new ArrayList<FileItem>();
		result.add(new FileItem("dir " + dirIndex + "1", 1, true));
		result.add(new FileItem("dir " + dirIndex + "2", 2, true));
		result.add(new FileItem("dir " + dirIndex + "3", 3, true));
		result.add(new FileItem("dir " + dirIndex + "4", 4, true));
		result.add(new FileItem("file " + dirIndex + "1", 5, false));
		result.add(new FileItem("file " + dirIndex + "2", 6, false));
		result.add(new FileItem("file " + dirIndex + "3", 7, false));
		return result;
	}

	@Override
	public void goInto(FileItem item)
	{
		dirIndex++;
	}

	@Override
	public InputStream getStream(FileItem item)
	{
		return null;
	}

	@Override
	public void goBack()
	{

	}
}
