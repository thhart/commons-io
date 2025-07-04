/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.io.input;

import static org.apache.commons.io.IOUtils.EOF;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;

/**
 * A functional, lightweight {@link Reader} that emulates
 * a reader of a specified size.
 * <p>
 * This implementation provides a lightweight
 * object for testing with an {@link Reader}
 * where the contents don't matter.
 * </p>
 * <p>
 * One use case would be for testing the handling of
 * large {@link Reader} as it can emulate that
 * scenario without the overhead of actually processing
 * large numbers of characters - significantly speeding up
 * test execution times.
 * </p>
 * <p>
 * This implementation returns a space from the method that
 * reads a character and leaves the array unchanged in the read
 * methods that are passed a character array.
 * If alternative data is required the {@code processChar()} and
 * {@code processChars()} methods can be implemented to generate
 * data, for example:
 * </p>
 *
 * <pre>
 *  public class TestReader extends NullReader {
 *      public TestReader(int size) {
 *          super(size);
 *      }
 *      protected char processChar() {
 *          return ... // return required value here
 *      }
 *      protected void processChars(char[] chars, int offset, int length) {
 *          for (int i = offset; i &lt; length; i++) {
 *              chars[i] = ... // set array value here
 *          }
 *      }
 *  }
 * </pre>
 * <p>
 * This class is not thread-safe.
 * </p>
 *
 * @since 1.3
 */
public class NullReader extends Reader {

    /**
     * The singleton instance.
     *
     * @since 2.12.0
     */
    public static final NullReader INSTANCE = new NullReader();

    private final long size;
    private final boolean throwEofException;
    private final boolean markSupported;

    private long position;
    private long mark = -1;
    private long readLimit;
    private boolean eof;

    /**
     * Constructs a {@link Reader} that emulates a size 0 reader
     * which supports marking and does not throw EOFException.
     *
     * @since 2.7
     */
    public NullReader() {
       this(0, true, false);
    }

    /**
     * Constructs a {@link Reader} that emulates a specified size
     * which supports marking and does not throw EOFException.
     *
     * @param size The size of the reader to emulate.
     */
    public NullReader(final long size) {
       this(size, true, false);
    }

    /**
     * Constructs a {@link Reader} that emulates a specified
     * size with option settings.
     *
     * @param size The size of the reader to emulate.
     * @param markSupported Whether this instance will support
     * the {@code mark()} functionality.
     * @param throwEofException Whether this implementation
     * will throw an {@link EOFException} or return -1 when the
     * end of file is reached.
     */
    public NullReader(final long size, final boolean markSupported, final boolean throwEofException) {
       this.size = size;
       this.markSupported = markSupported;
       this.throwEofException = throwEofException;
    }

    /**
     * Closes this Reader. Resets the internal state to
     * the initial values.
     *
     * @throws IOException If an error occurs.
     */
    @Override
    public void close() throws IOException {
        eof = false;
        position = 0;
        mark = -1;
    }

    /**
     * Handles End of File.
     *
     * @return {@code -1} if {@code throwEofException} is
     * set to {@code false}
     * @throws EOFException if {@code throwEofException} is set
     * to {@code true}.
     */
    private int doEndOfFile() throws EOFException {
        eof = true;
        if (throwEofException) {
            throw new EOFException();
        }
        return EOF;
    }

    /**
     * Gets the current position.
     *
     * @return the current position.
     */
    public long getPosition() {
        return position;
    }

    /**
     * Gets the size this {@link Reader} emulates.
     *
     * @return The size of the reader to emulate.
     */
    public long getSize() {
        return size;
    }

    /**
     * Marks the current position.
     *
     * @param readLimit The number of characters before this marked position
     * is invalid.
     * @throws UnsupportedOperationException if mark is not supported.
     */
    @Override
    public synchronized void mark(final int readLimit) {
        if (!markSupported) {
            throw UnsupportedOperationExceptions.mark();
        }
        mark = position;
        this.readLimit = readLimit;
    }

    /**
     * Tests whether <em>mark</em> is supported.
     *
     * @return Whether <em>mark</em> is supported or not.
     */
    @Override
    public boolean markSupported() {
        return markSupported;
    }

    /**
     * Returns a character value for the  {@code read()} method.
     * <p>
     * This implementation returns zero.
     * </p>
     *
     * @return This implementation always returns zero.
     */
    protected int processChar() {
        // do nothing - overridable by subclass
        return 0;
    }

    /**
     * Process the characters for the {@code read(char[], offset, length)}
     * method.
     * <p>
     * This implementation leaves the character array unchanged.
     * </p>
     *
     * @param chars The character array
     * @param offset The offset to start at.
     * @param length The number of characters.
     */
    protected void processChars(final char[] chars, final int offset, final int length) {
        // do nothing - overridable by subclass
    }

    /**
     * Reads a character.
     *
     * @return Either The character value returned by {@code processChar()}
     * or {@code -1} if the end of file has been reached and
     * {@code throwEofException} is set to {@code false}.
     * @throws EOFException if the end of file is reached and
     * {@code throwEofException} is set to {@code true}.
     * @throws IOException if trying to read past the end of file.
     */
    @Override
    public int read() throws IOException {
        if (eof) {
            throw new IOException("Read after end of file");
        }
        if (position == size) {
            return doEndOfFile();
        }
        position++;
        return processChar();
    }

    /**
     * Reads some characters into the specified array.
     *
     * @param chars The character array to read into
     * @return The number of characters read or {@code -1}
     * if the end of file has been reached and
     * {@code throwEofException} is set to {@code false}.
     * @throws EOFException if the end of file is reached and
     * {@code throwEofException} is set to {@code true}.
     * @throws IOException if trying to read past the end of file.
     */
    @Override
    public int read(final char[] chars) throws IOException {
        return read(chars, 0, chars.length);
    }

    /**
     * Reads the specified number characters into an array.
     *
     * @param chars The character array to read into.
     * @param offset The offset to start reading characters into.
     * @param length The number of characters to read.
     * @return The number of characters read or {@code -1}
     * if the end of file has been reached and
     * {@code throwEofException} is set to {@code false}.
     * @throws EOFException if the end of file is reached and
     * {@code throwEofException} is set to {@code true}.
     * @throws IOException if trying to read past the end of file.
     */
    @Override
    public int read(final char[] chars, final int offset, final int length) throws IOException {
        if (eof) {
            throw new IOException("Read after end of file");
        }
        if (position == size) {
            return doEndOfFile();
        }
        position += length;
        int returnLength = length;
        if (position > size) {
            returnLength = length - (int) (position - size);
            position = size;
        }
        processChars(chars, offset, returnLength);
        return returnLength;
    }

    /**
     * Resets the stream to the point when mark was last called.
     *
     * @throws UnsupportedOperationException if mark is not supported.
     * @throws IOException If no position has been marked
     * or the read limit has been exceeded since the last position was
     * marked.
     */
    @Override
    public synchronized void reset() throws IOException {
        if (!markSupported) {
            throw UnsupportedOperationExceptions.reset();
        }
        if (mark < 0) {
            throw new IOException("No position has been marked");
        }
        if (position > mark + readLimit) {
            throw new IOException("Marked position [" + mark +
                    "] is no longer valid - passed the read limit [" +
                    readLimit + "]");
        }
        position = mark;
        eof = false;
    }

    /**
     * Skips a specified number of characters.
     *
     * @param numberOfChars The number of characters to skip.
     * @return The number of characters skipped or {@code -1}
     * if the end of file has been reached and
     * {@code throwEofException} is set to {@code false}.
     * @throws EOFException if the end of file is reached and
     * {@code throwEofException} is set to {@code true}.
     * @throws IOException if trying to read past the end of file.
     */
    @Override
    public long skip(final long numberOfChars) throws IOException {
        if (eof) {
            throw new IOException("Skip after end of file");
        }
        if (position == size) {
            return doEndOfFile();
        }
        position += numberOfChars;
        long returnLength = numberOfChars;
        if (position > size) {
            returnLength = numberOfChars - (position - size);
            position = size;
        }
        return returnLength;
    }

}
