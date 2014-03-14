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
    public static final class Pair<Key, Value>
    {
        public final Key key;
        public final Value value;

        public Pair(Key value, Value k)
        {
            this.key = value;
            this.value = k;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }

            if (getClass() != obj.getClass())
            {
                return false;
            }
            Pair<?, ?> o2 = (Pair<?, ?>)obj;

            return isEquals(key, o2.key) && isEquals(value, o2.value);
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }
    }

    public static boolean isEquals(Object o1, Object o2)
    {
        if (o1 == o2)
        {
            return true;
        }

        if (o1 == null || o2 == null)
        {
            return false;
        }

        return o1.equals(o2);
    }

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
