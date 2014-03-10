package ru.kkey.core;

import java.io.InputStream;
import java.util.List;

/**
 * @author anstarovoyt
 */
public interface Source
{
	List<FileItem> listFiles();

	void goInto(FileItem item);

	InputStream getFileStream(FileItem item);

	boolean goBack();

	void destroy();
}
