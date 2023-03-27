package com.qfi.huffman;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IterableBitArray implements Iterable<Boolean>
{
    private byte[] array = null;

    public IterableBitArray(byte[] array)
    {
        this.array = array;
    }

    public Iterator<Boolean> iterator()
    {
        return new Iterator<>()
        {
            private int bitIndex = 0;
            private int arrayIndex = 0;

            public boolean hasNext()
            {
                return (arrayIndex < array.length) && (bitIndex < 8);
            }

            public Boolean next()
            {
                if (!hasNext())
                {
                    throw new NoSuchElementException();
                }

                Boolean val = (array[arrayIndex] >> (7 - bitIndex) & 1) == 1;
                bitIndex++;

                if (bitIndex == 8)
                {
                    bitIndex = 0;
                    arrayIndex++;
                }

                return val;
            }

            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };
    }
}
