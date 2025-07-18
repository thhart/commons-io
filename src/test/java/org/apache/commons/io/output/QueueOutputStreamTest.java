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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.InterruptedIOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.QueueInputStream;
import org.apache.commons.io.input.QueueInputStreamTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

/**
 * Test {@link QueueOutputStream} and {@link QueueInputStream}
 *
 * @see QueueInputStreamTest
 */
public class QueueOutputStreamTest {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @AfterAll
    public static void afterAll() {
        executorService.shutdown();
    }

    private static <T> T callInThrowAwayThread(final Callable<T> callable) throws Exception {
        final Exchanger<T> exchanger = new Exchanger<>();
        executorService.submit(() -> {
            final T value = callable.call();
            exchanger.exchange(value);
            return null;
        });
        return exchanger.exchange(null);
    }

    @Test
    void testNullArgument() {
        assertThrows(NullPointerException.class, () -> new QueueOutputStream(null), "queue is required");
    }

    @Test
    void testWriteInterrupted() throws Exception {
        try (QueueOutputStream outputStream = new QueueOutputStream(new LinkedBlockingQueue<>(1));
                QueueInputStream inputStream = outputStream.newQueueInputStream()) {

            final int timeout = 1;
            final Exchanger<Thread> writerThreadExchanger = new Exchanger<>();
            final Exchanger<Exception> exceptionExchanger = new Exchanger<>();
            executorService.submit(() -> {
                final Thread writerThread = writerThreadExchanger.exchange(null, timeout, SECONDS);
                writerThread.interrupt();
                return null;
            });

            executorService.submit(() -> {
                try {
                    writerThreadExchanger.exchange(Thread.currentThread(), timeout, SECONDS);
                    outputStream.write("ABC".getBytes(StandardCharsets.UTF_8));
                } catch (final Exception e) {
                    Thread.interrupted(); //clear interrupt
                    exceptionExchanger.exchange(e, timeout, SECONDS);
                }
                return null;
            });

            final Exception exception = exceptionExchanger.exchange(null, timeout, SECONDS);
            assertNotNull(exception);
            assertEquals(exception.getClass(), InterruptedIOException.class);
        }
    }

    @Test
    void testWriteString() throws Exception {
        try (QueueOutputStream outputStream = new QueueOutputStream();
                QueueInputStream inputStream = outputStream.newQueueInputStream()) {
            outputStream.write("ABC".getBytes(UTF_8));
            final String value = IOUtils.toString(inputStream, UTF_8);
            assertEquals("ABC", value);
        }
    }

    @Test
    void testWriteStringMultiThread() throws Exception {
        try (QueueOutputStream outputStream = callInThrowAwayThread(QueueOutputStream::new);
                QueueInputStream inputStream = callInThrowAwayThread(outputStream::newQueueInputStream)) {
            callInThrowAwayThread(() -> {
                outputStream.write("ABC".getBytes(UTF_8));
                return null;
            });

            final String value = callInThrowAwayThread(() -> IOUtils.toString(inputStream, UTF_8));
            assertEquals("ABC", value);
        }
    }
}
