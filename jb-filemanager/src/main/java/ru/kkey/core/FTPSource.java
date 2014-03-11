package ru.kkey.core;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data source for ftp server navigation
 *
 * @author anstarovoyt
 */
public class FTPSource implements Source
{
	public static int RETRY_COUNT = 1;

	public static final SourceFactory FACTORY = new SourceFactory()
	{
		@Override
		public Source create(String path)
		{
			return FTPSource.create(path);
		}
	};

	private static final Logger logger = Logger.getAnonymousLogger();

	private static FTPSource create(String fullPath)
	{
		try
		{
			String prefix = "";
			if (!fullPath.startsWith("ftp://"))
			{
				prefix = "ftp://";
			}
			return new FTPSource(new URL(prefix + fullPath));
		} catch (RuntimeException e)
		{
			throw e;
		} catch (Exception e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private volatile FTPClient client;
	private final URL url;

	private final List<String> paths = new ArrayList<>();

	public FTPSource(URL url)
	{
		this.url = url;

		try
		{
			connect();

			String path = url.getPath();
			if (null != path && !path.isEmpty())
			{
				client.changeWorkingDirectory(path);
				addToPath(path);
			}

		} catch (IOException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void destroy()
	{
		try
		{
			client.disconnect();
		} catch (Exception e)
		{
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}

	@Override
	public synchronized byte[] getFile(FileItem item)
	{
		return getFile(item, 0);
	}

	@Override
	public Source getSourceFor(FileItem item)
	{
		return null;
	}

	@Override
	public synchronized boolean goBack()
	{
		return goBack(0);
	}

	@Override
	public synchronized void goInto(FileItem item)
	{
		goInto(item, 0);
	}

	@Override
	public synchronized List<FileItem> listFiles()
	{
		return listFiles(0);
	}

	private void addToPath(String path)
	{
		if (path == null || path.isEmpty())
		{
			return;
		}

		String[] subPaths = path.split("/");
		for (String sub : subPaths)
		{
			if (null != sub && !sub.isEmpty())
			{
				paths.add(sub);
			}
		}
	}

	private void checkConnection()
	{
		boolean isAvailable = false;
		try
		{
			isAvailable = client.isAvailable();
		} catch (Exception e)
		{
			logger.log(Level.FINE, e.getMessage(), e);
		}

		if (!isAvailable)
		{
			reconnect();
		}
	}

	private void connect()
	{
		try
		{
			client = new FTPClient();
			String userWithPass = url.getUserInfo();
			String login = null == userWithPass ? "anonymous" : userWithPass;
			String pass = "";

			int port = url.getPort() == -1 ? 21 : url.getPort();

			if (null != userWithPass && userWithPass.contains(":"))
			{
				String[] splitUserPass = userWithPass.split(":");
				login = splitUserPass[0];
				pass = splitUserPass[1];
			}

			client.connect(url.getHost(), port);
			client.login(login, pass);

			if (!paths.isEmpty())
			{
				client.changeWorkingDirectory(Utils.joinPath(paths, "/"));
			}
		} catch (RuntimeException e)
		{
			throw e;
		} catch (Exception e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private byte[] getFile(FileItem item, int retry)
	{
		try
		{
			checkConnection();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			if (client.retrieveFile(item.getName(), buffer))
			{
				buffer.flush();
				return buffer.toByteArray();

			}
			if (retry < RETRY_COUNT)
			{
				reconnect();
				return getFile(item, ++retry);
			}
			throw new RuntimeException("Cannot loading file");
		} catch (IOException e)
		{
			if (retry < RETRY_COUNT)
			{
				logger.log(Level.WARNING, e.getMessage(), e);
				reconnect();
				return getFile(item, ++retry);
			}
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private boolean goBack(int retry)
	{
		try
		{
			checkConnection();
			boolean result = client.changeToParentDirectory();
			if (result && paths.size() > 0)
			{
				paths.remove(paths.size() - 1);
				return true;
			}

			if (!result)
			{
				//some problems. Try reconnect
				reconnect();
			}

			return result;
		} catch (IOException e)
		{
			if (retry < RETRY_COUNT)
			{
				logger.log(Level.WARNING, e.getMessage(), e);
				reconnect();
				return goBack(++retry);
			}
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private void goInto(FileItem item, int retry)
	{
		try
		{
			checkConnection();
			if (client.changeWorkingDirectory(item.getName()))
			{
				paths.add(item.getName());
			} else
			{
				//some problems. Try reconnect
				reconnect();
			}
		} catch (Exception e)
		{
			if (retry < RETRY_COUNT)
			{
				logger.log(Level.WARNING, e.getMessage(), e);
				reconnect();
				goInto(item, ++retry);
			}
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private List<FileItem> listFiles(int retry)
	{
		List<FileItem> result = new ArrayList<>();
		try
		{
			checkConnection();
			result.addAll(toFileItems(client.listFiles()));
			return result;
		} catch (IOException e)
		{
			if (retry < RETRY_COUNT)
			{
				logger.log(Level.WARNING, e.getMessage(), e);
				reconnect();
				return listFiles(++retry);
			}
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private void reconnect()
	{
		try
		{
			client.disconnect();
		} catch (Exception e)
		{
		}

		connect();
	}

	private List<FileItem> toFileItems(FTPFile[] files)
	{
		List<FileItem> result = new ArrayList<>();

		for (FTPFile file : files)
		{
			result.add(new FileItem(file.getName(), file.isDirectory()));
		}
		Collections.sort(result);
		return result;
	}
}
