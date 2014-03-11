package ru.kkey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import ru.kkey.core.FileItem;
import ru.kkey.core.Source;

/**
 * Tests use framework {@link TestFSBuilder}
 *
 *
 * @author anstarovoyt
 */
public abstract class FileSourceTestBase
{
    Source fsSource;

    @After
    public void after()
    {
        if (fsSource != null)
        {
            fsSource.destroy();
        }
    }

    @Test
    public void testSimpleBack()
    {
        List<String> paths = Arrays.asList("dir", " dir", "  file", " file 1", " file 2");
        createBuilder().build(paths);

        fsSource = createSource();

        fsSource.goInto(item("dir", true));
        fsSource.goInto(item("dir", true));
        fsSource.listFiles(); //

        fsSource.goBack();

        List<String> actual = toNames(fsSource.listFiles());
        List<String> expected = Arrays.asList("dir", "file 1", "file 2");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSimpleInto()
    {
        List<String> paths = Arrays.asList("dir", " dir 11", " file 11");
        createBuilder().build(paths);

        fsSource = createSource();

        fsSource.goInto(item("dir", true));

        List<String> actual = toNames(fsSource.listFiles());
        List<String> expected = Arrays.asList("dir 11", "file 11");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSimpleList()
    {
        List<String> paths = Arrays.asList("dir 1", "dir 2", "dir 3", "file 1");
        createBuilder().build(paths);

        fsSource = createSource();

        List<String> actual = toNames(fsSource.listFiles());

        Assert.assertEquals(paths, actual);
    }

    @Test
    public void testSimpleOrder()
    {
        List<String> paths = Arrays.asList("dir 2", "dir 1", "dir 3", "file 2", "file 1", "file 3");
        createBuilder().build(paths);

        fsSource = createSource();

        List<String> actual = toNames(fsSource.listFiles());
        List<String> expected = Arrays.asList("dir 1", "dir 2", "dir 3", "file 1", "file 2", "file 3");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSimpleReadFile()
    {
        List<String> paths = Arrays.asList("dir", " file 11", " file 12");
        createBuilder().build(paths);

        fsSource = createSource();

        fsSource.goInto(item("dir", true));
        byte[] file = fsSource.getFile(item("file 11", false));

        Arrays.equals("file 11".getBytes(), file);
    }

    @Test
    public void testTreeInto()
    {
        List<String> paths = Arrays.asList("dir 1", " dir 11", " file 11", "dir 2", " dir 21", "  dir 211",
                "   dir 2111", "  file 211", "  file 212");
        createBuilder().build(paths);

        fsSource = createSource();

        fsSource.goInto(item("dir 2", true));
        fsSource.goInto(item("dir 21", true));

        List<String> actual = toNames(fsSource.listFiles());
        List<String> expected = Arrays.asList("dir 211", "file 211", "file 212");

        Assert.assertEquals(expected, actual);
    }

    protected abstract TestFSBuilder createBuilder();

    protected abstract Source createSource();

    protected FileItem item(String name, boolean isDir)
    {
        return new FileItem(name, isDir);
    }

    protected List<String> toNames(List<FileItem> items)
    {
        List<String> result = new ArrayList<>();
        for (FileItem item : items)
        {
            result.add(item.getName());
        }

        return result;
    }
}
