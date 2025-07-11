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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.UUID;

import org.apache.commons.io.TaggedIOException;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link TaggedReader}.
 */
class TaggedReaderTest {

    @Test
    void testBrokenReader() {
        final IOException exception = new IOException("test exception");
        final TaggedReader reader = new TaggedReader(new BrokenReader(exception));

        // Test the ready() method
        final IOException readyException = assertThrows(IOException.class, reader::ready);
        assertTrue(reader.isCauseOf(readyException));
        final IOException rethrownReadyException = assertThrows(IOException.class, () -> reader.throwIfCauseOf(readyException));
        assertEquals(exception, rethrownReadyException);

        // Test the read() method
        final IOException readException = assertThrows(IOException.class, reader::read);
        assertTrue(reader.isCauseOf(readException));
        final IOException rethrownReadException = assertThrows(IOException.class, () -> reader.throwIfCauseOf(readException));
        assertEquals(exception, rethrownReadException);

        // Test the close() method
        final IOException closeException = assertThrows(IOException.class, reader::close);
        assertTrue(reader.isCauseOf(closeException));
        final IOException rethrownCloseException = assertThrows(IOException.class, () -> reader.throwIfCauseOf(closeException));
        assertEquals(exception, rethrownCloseException);
    }

    @Test
    void testEmptyReader() throws IOException {
        try (Reader reader = new TaggedReader(ClosedReader.INSTANCE)) {
            assertFalse(reader.ready());
            assertEquals(-1, reader.read());
            assertEquals(-1, reader.read(new char[1]));
            assertEquals(-1, reader.read(new char[1], 0, 1));
        }
    }

    @Test
    void testNormalReader() throws IOException {
        try (Reader reader = new TaggedReader(new StringReader("abc"))) {
            assertTrue(reader.ready());
            assertEquals('a', reader.read());
            final char[] buffer = new char[1];
            assertEquals(1, reader.read(buffer));
            assertEquals('b', buffer[0]);
            assertEquals(1, reader.read(buffer, 0, 1));
            assertEquals('c', buffer[0]);
            assertEquals(-1, reader.read());
        }
    }

    @Test
    void testOtherException() throws Exception {
        final IOException exception = new IOException("test exception");
        try (TaggedReader reader = new TaggedReader(ClosedReader.INSTANCE)) {

            assertFalse(reader.isCauseOf(exception));
            assertFalse(reader.isCauseOf(new TaggedIOException(exception, UUID.randomUUID())));

            reader.throwIfCauseOf(exception);

            reader.throwIfCauseOf(new TaggedIOException(exception, UUID.randomUUID()));
        }
    }

}
