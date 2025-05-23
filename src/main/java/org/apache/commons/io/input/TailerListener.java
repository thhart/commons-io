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

/**
 * Listener for events from a {@link Tailer}.
 *
 * @since 2.0
 */
public interface TailerListener {

    /**
     * This method is called if the tailed file is not found.
     * <p>
     * <strong>Note:</strong> this is called from the tailer thread.
     * </p>
     */
    void fileNotFound();

    /**
     * Called if a file rotation is detected.
     *
     * This method is called before the file is reopened, and fileNotFound may
     * be called if the new file has not yet been created.
     * <p>
     * <strong>Note:</strong> this is called from the tailer thread.
     * </p>
     */
    void fileRotated();

    /**
     * Handles an Exception.
     * <p>
     * <strong>Note:</strong> this is called from the tailer thread.
     * </p>
     * @param ex the exception.
     */
    void handle(Exception ex);

    /**
     * Handles a line from a Tailer.
     * <p>
     * <strong>Note:</strong> this is called from the tailer thread.
     * </p>
     * @param line the line.
     */
    void handle(String line);

    /**
     * The tailer will call this method during construction,
     * giving the listener a method of stopping the tailer.
     * @param tailer the tailer.
     */
    void init(Tailer tailer);

}
