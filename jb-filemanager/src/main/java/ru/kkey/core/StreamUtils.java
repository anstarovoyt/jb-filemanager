package ru.kkey.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author anstarovoyt
 *
 */
public class StreamUtils
{
    public static byte[] readInputSteamToByteArray(InputStream is)
    {
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
