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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link CloseShieldOutputStream}.
 */
class CloseShieldOutputStreamTest {

    private ByteArrayOutputStream original;

    private OutputStream shielded;

    private boolean closed;

    @BeforeEach
    public void setUp() {
        original = new ByteArrayOutputStream() {
            @Override
            public void close() {
                closed = true;
            }
        };
        shielded = CloseShieldOutputStream.wrap(original);
        closed = false;
    }

    @Test
    void testClose() throws IOException {
        shielded.close();
        assertFalse(closed, "closed");
        assertThrows(IOException.class, () -> shielded.write('x'), "write(b)");
        original.write('y');
        assertEquals(1, original.size());
        assertEquals('y', original.toByteArray()[0]);
    }

}
