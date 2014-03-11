package ru.kkey.core;


/**
 * @author anstarovoyt
 */
public class FileItem implements Comparable<FileItem>
{
	private String name;
	private boolean isDirectory;


	public FileItem(String name, boolean isDirectory)
	{
		this.name = name.endsWith("/") && isDirectory ? name.substring(0, name.length() - 1) : name;
		this.isDirectory = isDirectory;
	}

	public String getName()
	{
		return name;
	}

	public boolean isDirectory()
	{
		return isDirectory;
	}

	@Override
	public String toString()
	{
		String prefix = isDirectory ? " /" : "  ";
		return prefix + name;
	}

	@Override
	public int compareTo(FileItem o)
	{
		if (isDirectory() ^ o.isDirectory())
		{
			return isDirectory() ? -1 : 1;
		}

		return name.compareTo(o.getName());
	}

	public String getFileExtension()
	{
		return name.lastIndexOf('.') >= 0 ? name.substring(name.lastIndexOf('.') + 1).toLowerCase() : "";
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FileItem fileItem = (FileItem) o;

		if (isDirectory != fileItem.isDirectory) return false;
		return name.equals(fileItem.name);
	}

	@Override
	public int hashCode()
	{
		int result = name.hashCode();
		result = 31 * result + (isDirectory ? 1 : 0);
		return result;
	}
}
