package ru.kkey.core;

import java.io.InputStream;
import java.util.List;

/**
 * @author anstarovoyt
 */
public interface FileSource
{
	List<FileItem> getFiles();

	void goInto(FileItem item);

	InputStream getStream(FileItem item);

	void goBack();
}
