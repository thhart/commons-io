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
package org.apache.commons.io.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;

/**
 * Compare the file name <strong>extensions</strong> for order
 * (see {@link FilenameUtils#getExtension(String)}).
 * <p>
 * This comparator can be used to sort lists or arrays of files
 * by their file extension either in a case-sensitive, case-insensitive or
 * system dependent case-sensitive way. A number of singleton instances
 * are provided for the various case sensitivity options (using {@link IOCase})
 * and the reverse of those options.
 * </p>
 * <p>
 * Example of a <em>case-sensitive</em> file extension sort using the
 * {@link #EXTENSION_COMPARATOR} singleton instance:
 * </p>
 * <pre>
 *       List&lt;File&gt; list = ...
 *       ((AbstractFileComparator) ExtensionFileComparator.EXTENSION_COMPARATOR).sort(list);
 * </pre>
 * <p>
 * Example of a <em>reverse case-insensitive</em> file extension sort using the
 * {@link #EXTENSION_INSENSITIVE_REVERSE} singleton instance:
 * </p>
 * <pre>
 *       File[] array = ...
 *       ((AbstractFileComparator) ExtensionFileComparator.EXTENSION_INSENSITIVE_REVERSE).sort(array);
 * </pre>
 * <h2>Deprecating Serialization</h2>
 * <p>
 * <em>Serialization is deprecated and will be removed in 3.0.</em>
 * </p>
 *
 * @since 1.4
 */
public class ExtensionFileComparator extends AbstractFileComparator implements Serializable {

    private static final long serialVersionUID = 1928235200184222815L;

    /** Case-sensitive extension comparator instance (see {@link IOCase#SENSITIVE}) */
    public static final Comparator<File> EXTENSION_COMPARATOR = new ExtensionFileComparator();

    /** Reverse case-sensitive extension comparator instance (see {@link IOCase#SENSITIVE}) */
    public static final Comparator<File> EXTENSION_REVERSE = new ReverseFileComparator(EXTENSION_COMPARATOR);

    /** Case-insensitive extension comparator instance (see {@link IOCase#INSENSITIVE}) */
    public static final Comparator<File> EXTENSION_INSENSITIVE_COMPARATOR
                                                = new ExtensionFileComparator(IOCase.INSENSITIVE);

    /** Reverse case-insensitive extension comparator instance (see {@link IOCase#INSENSITIVE}) */
    public static final Comparator<File> EXTENSION_INSENSITIVE_REVERSE
                                                = new ReverseFileComparator(EXTENSION_INSENSITIVE_COMPARATOR);

    /** System sensitive extension comparator instance (see {@link IOCase#SYSTEM}) */
    public static final Comparator<File> EXTENSION_SYSTEM_COMPARATOR = new ExtensionFileComparator(IOCase.SYSTEM);

    /** Reverse system sensitive path comparator instance (see {@link IOCase#SYSTEM}) */
    public static final Comparator<File> EXTENSION_SYSTEM_REVERSE = new ReverseFileComparator(EXTENSION_SYSTEM_COMPARATOR);

    /** Whether the comparison is case-sensitive. */
    private final IOCase ioCase;

    /**
     * Constructs a case-sensitive file extension comparator instance.
     */
    public ExtensionFileComparator() {
        this.ioCase = IOCase.SENSITIVE;
    }

    /**
     * Constructs a file extension comparator instance with the specified case-sensitivity.
     *
     * @param ioCase how to handle case sensitivity, null means case-sensitive
     */
    public ExtensionFileComparator(final IOCase ioCase) {
        this.ioCase = IOCase.value(ioCase, IOCase.SENSITIVE);
    }

    /**
     * Compares the extensions of two files the specified case sensitivity.
     *
     * @param file1 The first file to compare
     * @param file2 The second file to compare
     * @return a negative value if the first file's extension
     * is less than the second, zero if the extensions are the
     * same and a positive value if the first files extension
     * is greater than the second file.
     */
    @Override
    public int compare(final File file1, final File file2) {
        final String suffix1 = FilenameUtils.getExtension(file1.getName());
        final String suffix2 = FilenameUtils.getExtension(file2.getName());
        return ioCase.checkCompareTo(suffix1, suffix2);
    }

    /**
     * String representation of this file comparator.
     *
     * @return String representation of this file comparator
     */
    @Override
    public String toString() {
        return super.toString() + "[ioCase=" + ioCase + "]";
    }
}
