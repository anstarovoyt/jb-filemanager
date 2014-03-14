package ru.kkey.core;

import java.util.List;

/**
 * @author anstarovoyt
 */
public interface Source
{
    interface SourceFactory
    {
        Source create(String path);
    }

    /**
     * Method for closing opened resources of the data source
     */
    void destroy();

    /**
     * Here we doesn't use InputStream because
     * file will be loaded in memory anyway.
     *
     * The second reason is "unstable" behavior of InputStream.
     * For example, if we use ftp source connection can be reset
     *
     * @return byte file presentation
     *
     */
    byte[] getFile(FileItem item);

    /**
     * @return child data source
     * For example if we want go into zip archive
     */
    Source getSourceFor(FileItem item);

    /**
     * Change current directory to parent
     * @return old parent if currect path is changed otherwih
     */
    FileItem goBack();

    /**
     * Change current directory to nested item directory
     */
    void goInto(FileItem item);

    /**
     *
     * @return sorted list of directories and files
     * Order: {@link FileItem#compareTo(FileItem)}
     */
    List<FileItem> listFiles();
}
