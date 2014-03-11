package ru.kkey.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * @author anstarovoyt
 *
 */
public class Utils
{
    public static String joinPath(Collection<String> items, String separator)
    {
        if (null == items)
        {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean isFirst = false;
        for (String path : items)
        {
            if (!isFirst)
            {
                result.append(separator);
            }
            else
            {
                isFirst = true;
            }
            result.append(path);
        }
        return result.toString();
    }

    public static byte[] readInputSteamToByteArray(InputStream is)
    {
        if (null == is)
        {
            return new byte[0];
        }
        try
        {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] byteBuffer = new byte[16384];
            while ((nRead = is.read(byteBuffer, 0, byteBuffer.length)) != -1)
            {
                buffer.write(byteBuffer, 0, nRead);
            }
            buffer.flush();

            return buffer.toByteArray();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
