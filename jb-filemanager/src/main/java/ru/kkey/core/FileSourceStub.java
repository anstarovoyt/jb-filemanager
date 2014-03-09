package ru.kkey.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author anstarovoyt
 */
public class FileSourceStub implements FileSource
{
	@Override
	public List<FileItem> getFiles()
	{
		List<FileItem> result = new ArrayList<FileItem>();
		result.add(new FileItem("dir 1", true));
		result.add(new FileItem("dir 2", true));
		result.add(new FileItem("dir 3", true));
		result.add(new FileItem("dir 4", true));
		result.add(new FileItem("file 1", false));
		result.add(new FileItem("file 2", false));
		result.add(new FileItem("file 3", false));
		return result;
	}

	@Override
	public void goInto(FileItem item)
	{

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
