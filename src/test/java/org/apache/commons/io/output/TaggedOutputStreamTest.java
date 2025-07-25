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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import org.apache.commons.io.TaggedIOException;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link TaggedOutputStream}.
 */
class TaggedOutputStreamTest  {

    @Test
    void testBrokenStream() {
        final IOException exception = new IOException("test exception");
        final TaggedOutputStream stream = new TaggedOutputStream(new BrokenOutputStream(exception));

        // Test the write() method
        try {
            stream.write('x');
            fail("Expected exception not thrown.");
        } catch (final IOException e) {
            assertTrue(stream.isCauseOf(e));
            try {
                stream.throwIfCauseOf(e);
                fail("Expected exception not thrown.");
            } catch (final IOException e2) {
                assertEquals(exception, e2);
            }
        }

        // Test the flush() method
        try {
            stream.flush();
            fail("Expected exception not thrown.");
        } catch (final IOException e) {
            assertTrue(stream.isCauseOf(e));
            try {
                stream.throwIfCauseOf(e);
                fail("Expected exception not thrown.");
            } catch (final IOException e2) {
                assertEquals(exception, e2);
            }
        }

        // Test the close() method
        try {
            stream.close();
            fail("Expected exception not thrown.");
        } catch (final IOException e) {
            assertTrue(stream.isCauseOf(e));
            try {
                stream.throwIfCauseOf(e);
                fail("Expected exception not thrown.");
            } catch (final IOException e2) {
                assertEquals(exception, e2);
            }
        }
    }

    @Test
    void testNormalStream() {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            try (OutputStream stream = new TaggedOutputStream(buffer)) {
                stream.write('a');
                stream.write(new byte[] { 'b' });
                stream.write(new byte[] { 'c' }, 0, 1);
                stream.flush();
            }
            assertEquals(3, buffer.size());
            assertEquals('a', buffer.toByteArray()[0]);
            assertEquals('b', buffer.toByteArray()[1]);
            assertEquals('c', buffer.toByteArray()[2]);
        } catch (final IOException e) {
            fail("Unexpected exception thrown");
        }
    }

    @Test
    void testOtherException() throws Exception {
        final IOException exception = new IOException("test exception");
        try (TaggedOutputStream stream = new TaggedOutputStream(ClosedOutputStream.INSTANCE)) {

            assertFalse(stream.isCauseOf(exception));
            assertFalse(stream.isCauseOf(new TaggedIOException(exception, UUID.randomUUID())));

            try {
                stream.throwIfCauseOf(exception);
            } catch (final IOException e) {
                fail("Unexpected exception thrown");
            }

            try {
                stream.throwIfCauseOf(new TaggedIOException(exception, UUID.randomUUID()));
            } catch (final IOException e) {
                fail("Unexpected exception thrown");
            }
        }
    }

}
