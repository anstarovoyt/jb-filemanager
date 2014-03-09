package ru.kkey.core;

import java.io.InputStream;
import java.util.List;

/**
 *
 * @author anstarovoyt
 */
public class FSSource implements FileSource
{


	@Override
	public List<FileItem> getFiles()
	{
		return null;
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
