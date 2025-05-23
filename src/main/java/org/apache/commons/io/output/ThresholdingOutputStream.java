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
package org.apache.commons.io.output;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.function.IOConsumer;
import org.apache.commons.io.function.IOFunction;

/**
 * An output stream which triggers an event on the first write that causes
 * the total number of bytes written to the stream to exceed a configured threshold,
 * and every subsequent write. The event
 * can be used, for example, to throw an exception if a maximum has been reached,
 * or to switch the underlying stream when the threshold is exceeded.
 *
 * <p>
 * This class overrides all {@link OutputStream} methods. However, these overrides ultimately call the corresponding
 * methods in the underlying output stream implementation.
 * </p>
 * <p>
 * NOTE: This implementation may trigger the event <em>before</em> the threshold is actually reached, since it triggers
 * when a pending write operation would cause the threshold to be exceeded.
 * </p>
 * <p>
 * See also the subclass {@link DeferredFileOutputStream}.
 * </p>
 *
 * @see DeferredFileOutputStream
 */
public class ThresholdingOutputStream extends OutputStream {

    /**
     * Noop output stream getter function.
     */
    private static final IOFunction<ThresholdingOutputStream, OutputStream> NOOP_OS_GETTER = os -> NullOutputStream.INSTANCE;

    /**
     * The threshold at which the event will be triggered.
     */
    private final int threshold;

    /**
     * Accepts reaching the threshold.
     */
    private final IOConsumer<ThresholdingOutputStream> thresholdConsumer;

    /**
     * Gets the output stream.
     */
    private final IOFunction<ThresholdingOutputStream, OutputStream> outputStreamGetter;

    /**
     * The number of bytes written to the output stream.
     */
    private long written;

    /**
     * Whether or not the configured threshold has been exceeded.
     */
    private boolean thresholdExceeded;

    /**
     * Constructs an instance of this class which will trigger an event at the specified threshold.
     *
     * @param threshold The number of bytes at which to trigger an event.
     */
    public ThresholdingOutputStream(final int threshold) {
        this(threshold, IOConsumer.noop(), NOOP_OS_GETTER);
    }

    /**
     * Constructs an instance of this class which will trigger an event at the specified threshold.
     * A negative threshold has no meaning and will be treated as 0
     *
     * @param threshold The number of bytes at which to trigger an event.
     * @param thresholdConsumer Accepts reaching the threshold.
     * @param outputStreamGetter Gets the output stream.
     * @since 2.9.0
     */
    public ThresholdingOutputStream(final int threshold, final IOConsumer<ThresholdingOutputStream> thresholdConsumer,
        final IOFunction<ThresholdingOutputStream, OutputStream> outputStreamGetter) {
        this.threshold = threshold < 0 ? 0 : threshold;
        this.thresholdConsumer = thresholdConsumer == null ? IOConsumer.noop() : thresholdConsumer;
        this.outputStreamGetter = outputStreamGetter == null ? NOOP_OS_GETTER : outputStreamGetter;
    }

    /**
     * Checks to see if writing the specified number of bytes would cause the configured threshold to be exceeded. If
     * so, triggers an event to allow a concrete implementation to take action on this.
     *
     * @param count The number of bytes about to be written to the underlying output stream.
     * @throws IOException if an error occurs.
     */
    protected void checkThreshold(final int count) throws IOException {
        if (!thresholdExceeded && written + count > threshold) {
            thresholdExceeded = true;
            thresholdReached();
        }
    }

    /**
     * Closes this output stream and releases any system resources associated with this stream.
     *
     * @throws IOException if an error occurs.
     */
    @Override
    public void close() throws IOException {
        try {
            flush();
        } catch (final IOException ignored) {
            // ignore
        }
        // TODO for 4.0: Replace with getOutputStream()
        getStream().close();
    }

    /**
     * Flushes this output stream and forces any buffered output bytes to be written out.
     *
     * @throws IOException if an error occurs.
     */
    @SuppressWarnings("resource") // the underlying stream is managed by a subclass.
    @Override
    public void flush() throws IOException {
        // TODO for 4.0: Replace with getOutputStream()
        getStream().flush();
    }

    /**
     * Gets the number of bytes that have been written to this output stream.
     *
     * @return The number of bytes written.
     */
    public long getByteCount() {
        return written;
    }

    /**
     * Gets the underlying output stream, to which the corresponding {@link OutputStream} methods in this class will
     * ultimately delegate.
     *
     * @return The underlying output stream.
     * @throws IOException if an error occurs.
     * @since 2.14.0
     */
    protected OutputStream getOutputStream() throws IOException {
        return outputStreamGetter.apply(this);
    }

    /**
     * Gets the underlying output stream, to which the corresponding {@link OutputStream} methods in this class will
     * ultimately delegate.
     *
     * @return The underlying output stream.
     * @throws IOException if an error occurs.
     * @deprecated Use {@link #getOutputStream()}.
     */
    @Deprecated
    protected OutputStream getStream() throws IOException {
        return getOutputStream();
    }

    /**
     * Gets the threshold, in bytes, at which an event will be triggered.
     *
     * @return The threshold point, in bytes.
     */
    public int getThreshold() {
        return threshold;
    }

    /**
     * Tests whether or not the configured threshold has been exceeded for this output stream.
     *
     * @return {@code true} if the threshold has been reached; {@code false} otherwise.
     */
    public boolean isThresholdExceeded() {
        return written > threshold;
    }

    /**
     * Resets the byteCount to zero. You can call this from {@link #thresholdReached()} if you want the event to be
     * triggered again.
     */
    protected void resetByteCount() {
        this.thresholdExceeded = false;
        this.written = 0;
    }

    /**
     * Sets the byteCount to count. Useful for re-opening an output stream that has previously been written to.
     *
     * @param count The number of bytes that have already been written to the output stream
     * @since 2.5
     */
    protected void setByteCount(final long count) {
        this.written = count;
    }

    /**
     * Indicates that the configured threshold has been reached, and that a subclass should take whatever action
     * necessary on this event. This may include changing the underlying output stream.
     *
     * @throws IOException if an error occurs.
     */
    protected void thresholdReached() throws IOException {
        thresholdConsumer.accept(this);
    }

    /**
     * Writes {@code b.length} bytes from the specified byte array to this output stream.
     *
     * @param b The array of bytes to be written.
     * @throws IOException if an error occurs.
     */
    @SuppressWarnings("resource") // the underlying stream is managed by a subclass.
    @Override
    public void write(final byte[] b) throws IOException {
        checkThreshold(b.length);
        // TODO for 4.0: Replace with getOutputStream()
        getStream().write(b);
        written += b.length;
    }

    /**
     * Writes {@code len} bytes from the specified byte array starting at offset {@code off} to this output stream.
     *
     * @param b The byte array from which the data will be written.
     * @param off The start offset in the byte array.
     * @param len The number of bytes to write.
     * @throws IOException if an error occurs.
     */
    @SuppressWarnings("resource") // the underlying stream is managed by a subclass.
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        // TODO we could write the sub-array up the threshold, fire the event,
        // and then write the rest so the event is always fired at the precise point.
        checkThreshold(len);
        // TODO for 4.0: Replace with getOutputStream()
        getStream().write(b, off, len);
        written += len;
    }

    /**
     * Writes the specified byte to this output stream.
     *
     * @param b The byte to be written.
     * @throws IOException if an error occurs.
     */
    @SuppressWarnings("resource") // the underlying stream is managed by a subclass.
    @Override
    public void write(final int b) throws IOException {
        checkThreshold(1);
        // TODO for 4.0: Replace with getOutputStream()
        getStream().write(b);
        written++;
    }
}
