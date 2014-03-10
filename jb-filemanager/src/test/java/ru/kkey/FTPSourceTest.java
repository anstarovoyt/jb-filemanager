package ru.kkey;

import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import ru.kkey.core.FTPSource;
import ru.kkey.core.FileSource;

import java.nio.file.Paths;

/**
 * @author anstarovoyt
 */
public class FTPSourceTest extends FileSourceTestBase
{
	private static FtpServer server;

	@BeforeClass
	public static void setupFTPServer() throws FtpException
	{
		FtpServerFactory serverFactory = new FtpServerFactory();

		ConnectionConfigFactory connectionConfigFactory = new ConnectionConfigFactory();
		connectionConfigFactory.setAnonymousLoginEnabled(true);

		serverFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig());

		BaseUser user = new BaseUser();
		user.setHomeDirectory(Paths.get(TestFSBuilder.DIR_FOR_TEST_TREE).toAbsolutePath().toString());
		user.setName("anonymous");
		serverFactory.getUserManager().save(user);


		ListenerFactory factory = new ListenerFactory();
		factory.setPort(2221);
		serverFactory.addListener("default", factory.createListener());
		server = serverFactory.createServer();
		server.start();
	}

	@AfterClass
	public static void downFTPServer()
	{
		if (null != server)
		{
			server.stop();
		}
	}

	@Override
	protected FileSource createSource()
	{
		return new FTPSource("localhost", 2221, "anonymous", "");
	}

	@Override
	protected TestFSBuilder createBuilder()
	{
		return new TestFSBuilder();
	}
}
