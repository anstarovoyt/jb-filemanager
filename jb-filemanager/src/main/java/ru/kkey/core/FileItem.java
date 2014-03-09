package ru.kkey.core;

/**
 * @author anstarovoyt
 */
public class FileItem implements Comparable<FileItem>
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
		String prefix = isFolder ? " /" : "  ";
		return prefix + name;
	}

	@Override
	public int compareTo(FileItem o)
	{
		if (null == o)
		{
			return -1;
		}
		if (isFolder() ^ o.isFolder())
		{
			return isFolder() ? -1 : 1;
		}

		return name.compareTo(o.getName());
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FileItem fileItem = (FileItem) o;

		if (isFolder != fileItem.isFolder) return false;
		return name.equals(fileItem.name);
	}

	@Override
	public int hashCode()
	{
		int result = name.hashCode();
		result = 31 * result + (isFolder ? 1 : 0);
		return result;
	}
}
