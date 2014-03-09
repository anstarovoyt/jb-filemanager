package ru.kkey.core;

/**
 * @author anstarovoyt
 */
public class FileItem
{
	private String name;
	private boolean isFolder;

	private int id;

	public FileItem(String name, int id, boolean isFolder)
	{
		this.id = id;
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

	public int getId()
	{
		return id;
	}


	@Override
	public String toString()
	{
		String prefix = isFolder ? " /" : "  ";
		return prefix + name;
	}
}
