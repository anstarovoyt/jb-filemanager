package ru.kkey;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.kkey.core.FTPSource;
import ru.kkey.core.Source;

/**
 * Testing {@link FTPSource}
 *
 * @author anstarovoyt
 */
public class FTPSourceTest extends FileSourceTestBase
{
    private static FtpServer server;

    @AfterClass
    public static void downFTPServer()
    {
        if (null != server)
        {
            server.stop();
        }
    }

    @BeforeClass
    public static void setupFTPServer() throws Exception
    {
        FtpServerFactory serverFactory = new FtpServerFactory();

        ConnectionConfigFactory connectionConfigFactory = new ConnectionConfigFactory();
        connectionConfigFactory.setAnonymousLoginEnabled(true);

        serverFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig());

        BaseUser user = new BaseUser();
        user.setHomeDirectory(Paths.get(FSBuilder.DIR_FOR_TEST_TREE).toAbsolutePath().toString());
        user.setName("anonymous");
        serverFactory.getUserManager().save(user);

        ListenerFactory factory = new ListenerFactory();
        factory.setPort(2221);
        serverFactory.addListener("default", factory.createListener());
        server = serverFactory.createServer();
        server.start();
    }

    @Test
    public void testFTPReconnect() throws Exception
    {
        List<String> paths = Arrays.asList("dir", " dir1", "  dir2", "   file1");
        createBuilder().build(paths);

        fsSource = FTPSource.FACTORY.create("ftp://localhost:2221/dir/dir1");

        fsSource.goInto(item("dir2", true));

        server.suspend();
        server.resume();

        List<String> actual = toNames(fsSource.listFiles());
        List<String> expected = Arrays.asList("file1");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFTPWithPath()
    {
        List<String> paths = Arrays.asList("dir", " dir1", "  dir2", "   file1");
        createBuilder().build(paths);

        fsSource = FTPSource.FACTORY.create("ftp://localhost:2221/dir/dir1");

        fsSource.goInto(item("dir2", true));

        List<String> actual = toNames(fsSource.listFiles());
        List<String> expected = Arrays.asList("file1");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testUrlParsing() throws MalformedURLException
    {
        URL url = new URL("ftp://user:pass@localhost:2221");

        Assert.assertEquals("localhost", url.getHost());
        Assert.assertEquals(2221, url.getPort());
        Assert.assertEquals("user:pass", url.getUserInfo());
    }

    @Override
    protected FSBuilder createBuilder()
    {
        return new FSBuilder();
    }

    @Override
    protected Source createSource()
    {
        return FTPSource.FACTORY.create("ftp://localhost:2221/");
    }
}
