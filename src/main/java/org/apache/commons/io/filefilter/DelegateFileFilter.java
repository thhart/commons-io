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
package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.Objects;

/**
 * This class turns a Java FileFilter or FilenameFilter into an IO FileFilter.
 * <h2>Deprecating Serialization</h2>
 * <p>
 * <em>Serialization is deprecated and will be removed in 3.0.</em>
 * </p>
 *
 * @since 1.0
 * @see FileFilterUtils#asFileFilter(FileFilter)
 * @see FileFilterUtils#asFileFilter(FilenameFilter)
 */
public class DelegateFileFilter extends AbstractFileFilter implements Serializable {

    private static final long serialVersionUID = -8723373124984771318L;

    /** The File filter */
    private final transient FileFilter fileFilter;
    /** The Filename filter */
    private final transient FilenameFilter fileNameFilter;

    /**
     * Constructs a delegate file filter around an existing FileFilter.
     *
     * @param fileFilter  the filter to decorate
     */
    public DelegateFileFilter(final FileFilter fileFilter) {
        Objects.requireNonNull(fileFilter, "filter");
        this.fileFilter = fileFilter;
        this.fileNameFilter = null;
    }

    /**
     * Constructs a delegate file filter around an existing FilenameFilter.
     *
     * @param fileNameFilter  the filter to decorate
     */
    public DelegateFileFilter(final FilenameFilter fileNameFilter) {
        Objects.requireNonNull(fileNameFilter, "filter");
        this.fileNameFilter = fileNameFilter;
        this.fileFilter = null;
    }

    /**
     * Tests the filter.
     *
     * @param file  the file to check
     * @return true if the filter matches
     */
    @Override
    public boolean accept(final File file) {
        if (fileFilter != null) {
            return fileFilter.accept(file);
        }
        return super.accept(file);
    }

    /**
     * Tests the filter.
     *
     * @param dir  the directory
     * @param name  the file name in the directory
     * @return true if the filter matches
     */
    @Override
    public boolean accept(final File dir, final String name) {
        if (fileNameFilter != null) {
            return fileNameFilter.accept(dir, name);
        }
        return super.accept(dir, name);
    }

    /**
     * Provide a String representation of this file filter.
     *
     * @return a String representation
     */
    @Override
    public String toString() {
        final String delegate = Objects.toString(fileFilter, Objects.toString(fileNameFilter, null));
        return super.toString() + "(" + delegate + ")";
    }

}
