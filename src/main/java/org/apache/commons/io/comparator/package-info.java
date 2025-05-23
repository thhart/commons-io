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

/**
 * Provides various {@link java.util.Comparator} implementations
 * for {@link java.io.File}s and {@link java.nio.file.Path}.
 * <h2>Sorting</h2>
 * <p>
 * All the comparators include <em>convenience</em> utility <code>sort(File...)</code> and
 * <code>sort(List)</code> methods.
 * </p>
 * <p>
 * For example, to sort the files in a directory by name:
 * </p>
 * <pre>
 * File[] files = dir.listFiles();
 * NameFileComparator.NAME_COMPARATOR.sort(files);
 * </pre>
 * <p>
 * ...alternatively you can do this in one line:
 * </p>
 * <pre>
 * File[] files = NameFileComparator.NAME_COMPARATOR.sort(dir.listFiles());
 * </pre>
 * <h2>Composite Comparator</h2>
 * <p>
 * The <a href="CompositeFileComparator.html">CompositeFileComparator</a> can be used
 * to compare (and sort lists or arrays of files) by combining a number of other comparators.
 * </p>
 * <p>
 * For example, to sort an array of files by type (directory or file)
 * and then by name:
 * </p>
 * <pre>
 * CompositeFileComparator comparator =
 * new CompositeFileComparator(
 * DirectoryFileComparator.DIRECTORY_COMPARATOR,
 * NameFileComparator.NAME_COMPARATOR);
 * File[] files = dir.listFiles();
 * comparator.sort(files);
 * </pre>
 * <h2>Singleton Instances (thread-safe)</h2>
 * <p>
 * The {@link java.util.Comparator} implementations have some <em>convenience</em>
 * singleton(<em>thread-safe</em>) instances ready to use:
 * </p>
 * <ul>
 * <li><a href="DefaultFileComparator.html">DefaultFileComparator</a> - default file compare:
 * <ul>
 * <li><a href="DefaultFileComparator.html#DEFAULT_COMPARATOR">DEFAULT_COMPARATOR</a>
 * - Compare using <code>File.compareTo(File)</code> method.
 * </li>
 * <li><a href="DefaultFileComparator.html#DEFAULT_REVERSE">DEFAULT_REVERSE</a>
 * - Reverse compare of <code>File.compareTo(File)</code> method.
 * </li>
 * </ul>
 * </li>
 * <li><a href="DirectoryFileComparator.html">DirectoryFileComparator</a> - compare by type (directory or file):
 * <ul>
 * <li><a href="DirectoryFileComparator.html#DIRECTORY_COMPARATOR">DIRECTORY_COMPARATOR</a>
 * - Compare using <code>File.isDirectory()</code> method (directories &lt; files).
 * </li>
 * <li><a href="DirectoryFileComparator.html#DIRECTORY_REVERSE">DIRECTORY_REVERSE</a>
 * - Reverse compare of <code>File.isDirectory()</code> method  (directories &gt;files).
 * </li>
 * </ul>
 * </li>
 * <li><a href="ExtensionFileComparator.html">ExtensionFileComparator</a> - compare file extensions:
 * <ul>
 * <li><a href="ExtensionFileComparator.html#EXTENSION_COMPARATOR">EXTENSION_COMPARATOR</a>
 * - Compare using <code>FilenameUtils.getExtension(String)</code> method.
 * </li>
 * <li><a href="ExtensionFileComparator.html#EXTENSION_REVERSE">EXTENSION_REVERSE</a>
 * - Reverse compare of <code>FilenameUtils.getExtension(String)</code> method.
 * </li>
 * <li><a href="ExtensionFileComparator.html#EXTENSION_INSENSITIVE_COMPARATOR">EXTENSION_INSENSITIVE_COMPARATOR</a>
 * - Case-insensitive compare using <code>FilenameUtils.getExtension(String)</code> method.
 * </li>
 * <li><a href="ExtensionFileComparator.html#EXTENSION_INSENSITIVE_REVERSE">EXTENSION_INSENSITIVE_REVERSE</a>
 * - Reverse case-insensitive compare of <code>FilenameUtils.getExtension(String)</code> method.
 * </li>
 * <li><a href="ExtensionFileComparator.html#EXTENSION_SYSTEM_COMPARATOR">EXTENSION_SYSTEM_COMPARATOR</a>
 * -  System sensitive compare using <code>FilenameUtils.getExtension(String)</code> method.
 * </li>
 * <li><a href="ExtensionFileComparator.html#EXTENSION_SYSTEM_REVERSE">EXTENSION_SYSTEM_REVERSE</a>
 * - Reverse system sensitive compare of <code>FilenameUtils.getExtension(String)</code> method.
 * </li>
 * </ul>
 * </li>
 * <li><a href="LastModifiedFileComparator.html">LastModifiedFileComparator</a>
 * - compare the file's last modified date/time:
 * <ul>
 * <li><a href="LastModifiedFileComparator.html#LASTMODIFIED_COMPARATOR">LASTMODIFIED_COMPARATOR</a>
 * - Compare using <code>File.lastModified()</code> method.
 * </li>
 * <li><a href="LastModifiedFileComparator.html#LASTMODIFIED_REVERSE">LASTMODIFIED_REVERSE</a>
 * - Reverse compare of <code>File.lastModified()</code> method.
 * </li>
 * </ul>
 * </li>
 * <li><a href="NameFileComparator.html">NameFileComparator</a> - compare file names:
 * <ul>
 * <li><a href="NameFileComparator.html#NAME_COMPARATOR">NAME_COMPARATOR</a>
 * - Compare using <code>File.getName()</code> method.
 * </li>
 * <li><a href="NameFileComparator.html#NAME_REVERSE">NAME_REVERSE</a>
 * - Reverse compare of <code>File.getName()</code> method.
 * </li>
 * <li><a href="NameFileComparator.html#NAME_INSENSITIVE_COMPARATOR">NAME_INSENSITIVE_COMPARATOR</a>
 * - Case-insensitive compare using <code>File.getName()</code> method.
 * </li>
 * <li><a href="NameFileComparator.html#NAME_INSENSITIVE_REVERSE">NAME_INSENSITIVE_REVERSE</a>
 * - Reverse case-insensitive compare of <code>File.getName()</code> method.
 * </li>
 * <li><a href="NameFileComparator.html#NAME_SYSTEM_COMPARATOR">NAME_SYSTEM_COMPARATOR</a>
 * -  System sensitive compare using <code>File.getName()</code> method.
 * </li>
 * <li><a href="NameFileComparator.html#NAME_SYSTEM_REVERSE">NAME_SYSTEM_REVERSE</a>
 * - Reverse system sensitive compare of <code>File.getName()</code> method.
 * </li>
 * </ul>
 * </li>
 * <li><a href="PathFileComparator.html">PathFileComparator</a> - compare file paths:
 * <ul>
 * <li><a href="PathFileComparator.html#PATH_COMPARATOR">PATH_COMPARATOR</a>
 * - Compare using <code>File.getPath()</code> method.
 * </li>
 * <li><a href="PathFileComparator.html#PATH_REVERSE">PATH_REVERSE</a>
 * - Reverse compare of <code>File.getPath()</code> method.
 * </li>
 * <li><a href="PathFileComparator.html#PATH_INSENSITIVE_COMPARATOR">PATH_INSENSITIVE_COMPARATOR</a>
 * - Case-insensitive compare using <code>File.getPath()</code> method.
 * </li>
 * <li><a href="PathFileComparator.html#PATH_INSENSITIVE_REVERSE">PATH_INSENSITIVE_REVERSE</a>
 * - Reverse case-insensitive compare of <code>File.getPath()</code> method.
 * </li>
 * <li><a href="PathFileComparator.html#PATH_SYSTEM_COMPARATOR">PATH_SYSTEM_COMPARATOR</a>
 * -  System sensitive compare using <code>File.getPath()</code> method.
 * </li>
 * <li><a href="PathFileComparator.html#PATH_SYSTEM_REVERSE">PATH_SYSTEM_REVERSE</a>
 * - Reverse system sensitive compare of <code>File.getPath()</code> method.
 * </li>
 * </ul>
 * </li>
 * <li><a href="SizeFileComparator.html">SizeFileComparator</a> - compare the file's size:
 * <ul>
 * <li><a href="SizeFileComparator.html#SIZE_COMPARATOR">SIZE_COMPARATOR</a>
 * - Compare using <code>File.length()</code> method (directories treated as zero length).
 * </li>
 * <li><a href="SizeFileComparator.html#SIZE_REVERSE">LASTMODIFIED_REVERSE</a>
 * - Reverse compare of <code>File.length()</code> method (directories treated as zero length).
 * </li>
 * <li><a href="SizeFileComparator.html#SIZE_SUMDIR_COMPARATOR">SIZE_SUMDIR_COMPARATOR</a>
 * - Compare using <code>FileUtils.sizeOfDirectory(File)</code> method
 * (sums the size of a directory's contents).
 * </li>
 * <li><a href="SizeFileComparator.html#SIZE_SUMDIR_REVERSE">SIZE_SUMDIR_REVERSE</a>
 * - Reverse compare of <code>FileUtils.sizeOfDirectory(File)</code> method
 * (sums the size of a directory's contents).
 * </li>
 * </ul>
 * </li>
 * </ul>
 */
package org.apache.commons.io.comparator;
