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

package org.apache.commons.io.file;

import static org.apache.commons.io.file.CounterAssertions.assertCounts;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link DeletingPathVisitor}.
 */
class PathUtilsDeleteDirectoryTest extends AbstractTempDirTest {

    @Test
    void testDeleteAbsentDirectory() throws IOException {
        final Path absent = tempDirPath.resolve("ThisDirectoryDoesNotExist");
        assertFalse(Files.exists(absent));
        final Class<NoSuchFileException> expectedType = NoSuchFileException.class;
        assertThrows(expectedType, () -> PathUtils.deleteDirectory(absent));
        assertThrows(expectedType, () -> PathUtils.deleteDirectory(absent, StandardDeleteOption.OVERRIDE_READ_ONLY));
        assertThrows(expectedType, () -> PathUtils.deleteDirectory(absent, PathUtils.EMPTY_DELETE_OPTION_ARRAY));
        // This will throw if not empty.
        Files.deleteIfExists(tempDirPath);
    }

    /**
     * Tests a directory with one file of size 0.
     */
    @Test
    void testDeleteDirectory1FileSize0() throws IOException {
        PathUtils.copyDirectory(Paths.get("src/test/resources/org/apache/commons/io/dirs-1-file-size-0"), tempDirPath);
        assertCounts(1, 1, 0, PathUtils.deleteDirectory(tempDirPath));
        // This will throw if not empty.
        Files.deleteIfExists(tempDirPath);
    }

    /**
     * Tests a directory with one file of size 0.
     */
    private void testDeleteDirectory1FileSize0(final DeleteOption... options) throws IOException {
        // TODO Setup the test to use FileVisitOption.
        PathUtils.copyDirectory(Paths.get("src/test/resources/org/apache/commons/io/dirs-1-file-size-0"), tempDirPath);
        assertCounts(1, 1, 0, PathUtils.deleteDirectory(tempDirPath, options));
        // This will throw if not empty.
        Files.deleteIfExists(tempDirPath);
    }

    @Test
    void testDeleteDirectory1FileSize0NoOptions() throws IOException {
        testDeleteDirectory1FileSize0(PathUtils.EMPTY_DELETE_OPTION_ARRAY);
    }

    @Test
    void testDeleteDirectory1FileSize0OverrideReadOnly() throws IOException {
        testDeleteDirectory1FileSize0(StandardDeleteOption.OVERRIDE_READ_ONLY);
    }

    /**
     * Tests a directory with one file of size 1.
     */
    @Test
    void testDeleteDirectory1FileSize1() throws IOException {
        PathUtils.copyDirectory(Paths.get("src/test/resources/org/apache/commons/io/dirs-1-file-size-1"), tempDirPath);
        assertCounts(1, 1, 1, PathUtils.deleteDirectory(tempDirPath));
        // This will throw if not empty.
        Files.deleteIfExists(tempDirPath);
    }

    /**
     * Tests a directory with two subdirectories, each containing one file of size 1.
     */
    @Test
    void testDeleteDirectory2FileSize2() throws IOException {
        PathUtils.copyDirectory(Paths.get("src/test/resources/org/apache/commons/io/dirs-2-file-size-2"), tempDirPath);
        assertCounts(3, 2, 2, PathUtils.deleteDirectory(tempDirPath));
        // This will throw if not empty.
        Files.deleteIfExists(tempDirPath);
    }

    /**
     * Tests an empty folder.
     */
    @Test
    void testDeleteEmptyDirectory() throws IOException {
        assertCounts(1, 0, 0, PathUtils.deleteDirectory(tempDirPath));
        // This will throw if not empty.
        Files.deleteIfExists(tempDirPath);
    }
}
