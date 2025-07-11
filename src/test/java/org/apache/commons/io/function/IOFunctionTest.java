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

package org.apache.commons.io.function;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link IOFunction}.
 */
class IOFunctionTest {

    private static final class Holder<T> {
        T value;
    }

    @Test
    void testAndThenConsumer() throws IOException {
        final Holder<Integer> holder = new Holder<>();
        final IOFunction<InputStream, Integer> readByte = InputStream::read;
        final Consumer<Integer> sinkInteger = i -> {
            holder.value = i * i;
        };
        final IOConsumer<InputStream> productFunction = readByte.andThen(sinkInteger);

        final InputStream is = new ByteArrayInputStream(new byte[] {2, 3});
        productFunction.accept(is);
        assertEquals(4, holder.value);
        productFunction.accept(is);
        assertEquals(9, holder.value);
    }

    @Test
    void testAndThenFunction() throws IOException {
        final IOFunction<InputStream, Integer> readByte = InputStream::read;
        final Function<Integer, Integer> squareInteger = i -> i * i;
        final IOFunction<InputStream, Integer> productFunction = readByte.andThen(squareInteger);

        final InputStream is = new ByteArrayInputStream(new byte[] {2, 3});
        assertEquals(4, productFunction.apply(is));
        assertEquals(9, productFunction.apply(is));
    }

    @Test
    void testAndThenIOConsumer() throws IOException {
        final Holder<Integer> holder = new Holder<>();
        final IOFunction<InputStream, Integer> readByte = InputStream::read;
        final IOConsumer<Integer> sinkInteger = i -> {
            holder.value = i * i;
        };
        final IOConsumer<InputStream> productFunction = readByte.andThen(sinkInteger);

        final InputStream is = new ByteArrayInputStream(new byte[] {2, 3});
        productFunction.accept(is);
        assertEquals(4, holder.value);
        productFunction.accept(is);
        assertEquals(9, holder.value);
    }

    @Test
    void testAndThenIOFunction() throws IOException {
        final IOFunction<InputStream, Integer> readByte = InputStream::read;
        final IOFunction<Integer, Integer> squareInteger = i -> i * i;
        final IOFunction<InputStream, Integer> productFunction = readByte.andThen(squareInteger);

        final InputStream is = new ByteArrayInputStream(new byte[] {2, 3});
        assertEquals(4, productFunction.apply(is));
        assertEquals(9, productFunction.apply(is));
    }

    @Test
    void testApply() throws IOException {
        final IOFunction<InputStream, Integer> readByte = InputStream::read;
        final InputStream is = new ByteArrayInputStream(new byte[] {(byte) 0xa, (byte) 0xb, (byte) 0xc});
        assertEquals(0xa, readByte.apply(is));
        assertEquals(0xb, readByte.apply(is));
        assertEquals(0xc, readByte.apply(is));
        assertEquals(-1, readByte.apply(is));
    }

    @Test
    void testApplyThrowsException() {
        final IOFunction<InputStream, Integer> throwException = function -> {
            throw new IOException("Boom!");
        };
        assertThrows(IOException.class, () -> throwException.apply(new ByteArrayInputStream(ArrayUtils.EMPTY_BYTE_ARRAY)));
    }

    @Test
    void testAsFunction() {
        assertThrows(UncheckedIOException.class, () -> Optional.of("a").map(TestConstants.THROWING_IO_FUNCTION.asFunction()).get());
        assertEquals("a", Optional.of("a").map(IOFunction.identity().asFunction()).get());
    }

    @Test
    void testComposeFunction() throws IOException {
        final Function<InputStream, Integer> alwaysSeven = is -> 7;
        final IOFunction<Integer, Integer> squareInteger = i -> i * i;
        final IOFunction<InputStream, Integer> productFunction = squareInteger.compose(alwaysSeven);

        final InputStream is = new ByteArrayInputStream(new byte[] {2, 3});
        assertEquals(49, productFunction.apply(is));
        assertEquals(49, productFunction.apply(is));
    }

    @Test
    void testComposeIOFunction() throws IOException {
        final IOFunction<InputStream, Integer> readByte = InputStream::read;
        final IOFunction<Integer, Integer> squareInteger = i -> i * i;
        final IOFunction<InputStream, Integer> productFunction = squareInteger.compose(readByte);

        final InputStream is = new ByteArrayInputStream(new byte[] {2, 3});
        assertEquals(4, productFunction.apply(is));
        assertEquals(9, productFunction.apply(is));
    }

    @Test
    void testComposeIOSupplier() throws IOException {
        final InputStream is = new ByteArrayInputStream(new byte[] {2, 3});

        final IOSupplier<Integer> readByte = is::read;
        final IOFunction<Integer, Integer> squareInteger = i -> i * i;
        final IOSupplier<Integer> productFunction = squareInteger.compose(readByte);

        assertEquals(4, productFunction.get());
        assertEquals(9, productFunction.get());
    }

    @Test
    void testComposeSupplier() throws IOException {
        final Supplier<Integer> alwaysNine = () -> 9;
        final IOFunction<Integer, Integer> squareInteger = i -> i * i;
        final IOSupplier<Integer> productFunction = squareInteger.compose(alwaysNine);

        assertEquals(81, productFunction.get());
        assertEquals(81, productFunction.get());
    }

    @Test
    void testIdentity() throws IOException {
        assertEquals(IOFunction.identity(), IOFunction.identity());
        final IOFunction<byte[], byte[]> identityFunction = IOFunction.identity();
        final byte[] buf = {(byte) 0xa, (byte) 0xb, (byte) 0xc};
        assertEquals(buf, identityFunction.apply(buf));
        assertArrayEquals(buf, identityFunction.apply(buf));
    }
}
