/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.commons.io.serialization;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link WildcardClassNameMatcher}.
 */
class WildcardClassNameMatcherTest {

    @Test
    void testNoPattern() {
        final ClassNameMatcher ca = new WildcardClassNameMatcher("org.foo");
        assertTrue(ca.matches("org.foo"));
        assertFalse(ca.matches("org.foo.and.more"));
        assertFalse(ca.matches("org_foo"));
    }

    @Test
    void testStar() {
        final ClassNameMatcher ca = new WildcardClassNameMatcher("org*");
        assertTrue(ca.matches("org.foo.should.match"));
        assertFalse(ca.matches("bar.should.not.match"));
    }

    @Test
    void testStarAndQuestionMark() {
        final ClassNameMatcher ca = new WildcardClassNameMatcher("org?apache?something*");
        assertTrue(ca.matches("org.apache_something.more"));
        assertFalse(ca.matches("org..apache_something.more"));
    }

}