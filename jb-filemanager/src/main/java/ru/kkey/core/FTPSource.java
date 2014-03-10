package ru.kkey.core;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author anstarovoyt
 */
public class FTPSource implements Source
{
	private final FTPClient client;

	public FTPSource(String server, int port, String login, String pass)
	{
		client = new FTPClient();
		try
		{
			client.connect(server, port);
			client.login(login, pass);
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}


	@Override
	public List<FileItem> listFiles()
	{
		List<FileItem> result = new ArrayList<>();
		try
		{
			result.addAll(toFileItems(client.listFiles()));

			return result;
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void goInto(FileItem item)
	{
		try
		{
			client.changeWorkingDirectory(item.getName());
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public InputStream getFileStream(FileItem item)
	{
		try
		{
			return client.retrieveFileStream(item.getName());
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean goBack()
	{
		try
		{
			return client.changeToParentDirectory();
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}


	List<FileItem> toFileItems(FTPFile[] files)
	{
		List<FileItem> result = new ArrayList<>();

		for (FTPFile file : files)
		{
			result.add(new FileItem(file.getName(), file.isDirectory(), null));
		}
		Collections.sort(result);
		return result;
	}

	@Override
	public void destroy()
	{
		try
		{
			client.disconnect();
		} catch (IOException e)
		{
			System.err.println(e);
		}
	}
}
