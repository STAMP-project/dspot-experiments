/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.artifact.repository;


import junit.framework.TestCase;


public class MavenArtifactRepositoryTest extends TestCase {
    private static class MavenArtifactRepositorySubclass extends MavenArtifactRepository {
        String id;

        public MavenArtifactRepositorySubclass(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }
    }

    public void testHashCodeEquals() {
        MavenArtifactRepositoryTest.MavenArtifactRepositorySubclass r1 = new MavenArtifactRepositoryTest.MavenArtifactRepositorySubclass("foo");
        MavenArtifactRepositoryTest.MavenArtifactRepositorySubclass r2 = new MavenArtifactRepositoryTest.MavenArtifactRepositorySubclass("foo");
        MavenArtifactRepositoryTest.MavenArtifactRepositorySubclass r3 = new MavenArtifactRepositoryTest.MavenArtifactRepositorySubclass("bar");
        TestCase.assertTrue(((r1.hashCode()) == (r2.hashCode())));
        TestCase.assertFalse(((r1.hashCode()) == (r3.hashCode())));
        TestCase.assertTrue(r1.equals(r2));
        TestCase.assertTrue(r2.equals(r1));
        TestCase.assertFalse(r1.equals(r3));
        TestCase.assertFalse(r3.equals(r1));
    }
}

