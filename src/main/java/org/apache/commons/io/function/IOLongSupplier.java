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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * Like {@link LongSupplier} but throws {@link IOException}.
 *
 * @since 2.14.0
 */
@FunctionalInterface
public interface IOLongSupplier {

    /**
     * Creates a {@link Supplier} for this instance that throws {@link UncheckedIOException} instead of {@link IOException}.
     *
     * @return an UncheckedIOException Supplier.
     */
    default LongSupplier asSupplier() {
        return () -> Uncheck.getAsLong(this);
    }

    /**
     * Gets a result.
     *
     * @return a result
     * @throws IOException if an I/O error occurs.
     */
    long getAsLong() throws IOException;
}
