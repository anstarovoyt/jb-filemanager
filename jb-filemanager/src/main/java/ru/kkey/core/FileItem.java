package ru.kkey.core;

/**
 * @author anstarovoyt
 */
public class FileItem
{
	private String name;
	private boolean isFolder;

	public FileItem(String name, boolean isFolder)
	{
		this.name = name;
		this.isFolder = isFolder;
	}

	public String getName()
	{
		return name;
	}

	public boolean isFolder()
	{
		return isFolder;
	}

	@Override
	public String toString()
	{
		String prefix = isFolder ? " /": "  ";
		return prefix + name;
	}
}
