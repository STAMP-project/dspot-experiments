/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.model.path;


import junit.framework.TestCase;


/**
 *
 *
 * @author Benjamin Bentmann
 */
public class DefaultUrlNormalizerTest extends TestCase {
    private UrlNormalizer normalizer;

    public void testNullSafe() {
        TestCase.assertNull(normalize(null));
    }

    public void testTrailingSlash() {
        TestCase.assertEquals("", normalize(""));
        TestCase.assertEquals("http://server.org/dir", normalize("http://server.org/dir"));
        TestCase.assertEquals("http://server.org/dir/", normalize("http://server.org/dir/"));
    }

    public void testRemovalOfParentRefs() {
        TestCase.assertEquals("http://server.org/child", normalize("http://server.org/parent/../child"));
        TestCase.assertEquals("http://server.org/child", normalize("http://server.org/grand/parent/../../child"));
        TestCase.assertEquals("http://server.org//child", normalize("http://server.org/parent/..//child"));
        TestCase.assertEquals("http://server.org/child", normalize("http://server.org/parent//../child"));
    }

    public void testPreservationOfDoubleSlashes() {
        TestCase.assertEquals("scm:hg:ssh://localhost//home/user", normalize("scm:hg:ssh://localhost//home/user"));
        TestCase.assertEquals("file:////UNC/server", normalize("file:////UNC/server"));
        TestCase.assertEquals("[fetch=]http://server.org/[push=]ssh://server.org/", normalize("[fetch=]http://server.org/[push=]ssh://server.org/"));
    }
}

