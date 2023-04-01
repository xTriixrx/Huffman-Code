package com.qfi.huffman;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The IterableBitArray object wraps a byte array in order to iterate through each bit within the byte array.
 */
public class IterableBitArray implements Iterable<Boolean>
{
    private final byte[] m_array;

    /**
     * IterableBitArray constructor.
     *
     * @param array - The byte array to use for the IterableBitArray object.
     */
    public IterableBitArray(byte[] array)
    {
        m_array = array;
    }

    /**
     * The iterator interface returns an iterator which wil iterate through each bit within the byte array until
     * all the individual bits have been iterated through.
     *
     * @return {@code Iterator<Boolean>} - An iterator to iterate through each bit within the byte array.
     */
    @Override
    public Iterator<Boolean> iterator()
    {
        return new Iterator<>()
        {
            private int bitIndex = 0;
            private int arrayIndex = 0;

            /**
             * A boolish method to determine if their is another bit within the iterator.
             *
             * @return boolean
             */
            public boolean hasNext()
            {
                return (arrayIndex < m_array.length) && (bitIndex < 8);
            }

            /**
             * Returns the next available bit as a Boolean object if it is available.
             *
             * @return - Boolean
             */
            public Boolean next()
            {
                if (!hasNext())
                {
                    throw new NoSuchElementException();
                }

                Boolean val = (m_array[arrayIndex] >> (7 - bitIndex) & 1) == 1;
                bitIndex++;

                if (bitIndex == 8)
                {
                    bitIndex = 0;
                    arrayIndex++;
                }

                return val;
            }

            /**
             * Overridden unused remove function for this iterator.
             */
            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };
    }
}
