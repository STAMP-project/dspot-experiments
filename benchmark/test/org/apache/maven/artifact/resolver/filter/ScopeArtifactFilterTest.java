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
package org.apache.maven.artifact.resolver.filter;


import Artifact.SCOPE_COMPILE;
import Artifact.SCOPE_PROVIDED;
import Artifact.SCOPE_RUNTIME;
import Artifact.SCOPE_SYSTEM;
import Artifact.SCOPE_TEST;
import junit.framework.TestCase;
import org.apache.maven.artifact.Artifact;


/**
 * Tests {@link ScopeArtifactFilter}.
 *
 * @author Benjamin Bentmann
 */
public class ScopeArtifactFilterTest extends TestCase {
    public void testInclude_Compile() {
        ScopeArtifactFilter filter = new ScopeArtifactFilter(Artifact.SCOPE_COMPILE);
        TestCase.assertTrue(filter.include(newArtifact(SCOPE_COMPILE)));
        TestCase.assertTrue(filter.include(newArtifact(SCOPE_SYSTEM)));
        TestCase.assertTrue(filter.include(newArtifact(SCOPE_PROVIDED)));
        TestCase.assertFalse(filter.include(newArtifact(SCOPE_RUNTIME)));
        TestCase.assertFalse(filter.include(newArtifact(SCOPE_TEST)));
    }

    public void testInclude_CompilePlusRuntime() {
        ScopeArtifactFilter filter = new ScopeArtifactFilter(Artifact.SCOPE_COMPILE_PLUS_RUNTIME);
        TestCase.assertTrue(filter.include(newArtifact(SCOPE_COMPILE)));
        TestCase.assertTrue(filter.include(newArtifact(SCOPE_SYSTEM)));
        TestCase.assertTrue(filter.include(newArtifact(SCOPE_PROVIDED)));
        TestCase.assertTrue(filter.include(newArtifact(SCOPE_RUNTIME)));
        TestCase.assertFalse(filter.include(newArtifact(SCOPE_TEST)));
    }

    public void testInclude_Runtime() {
        ScopeArtifactFilter filter = new ScopeArtifactFilter(Artifact.SCOPE_RUNTIME);
        TestCase.assertTrue(filter.include(newArtifact(SCOPE_COMPILE)));
        TestCase.assertFalse(filter.include(newArtifact(SCOPE_SYSTEM)));
        TestCase.assertFalse(filter.include(newArtifact(SCOPE_PROVIDED)));
        TestCase.assertTrue(filter.include(newArtifact(SCOPE_RUNTIME)));
        TestCase.assertFalse(filter.include(newArtifact(SCOPE_TEST)));
    }

    public void testInclude_RuntimePlusSystem() {
        ScopeArtifactFilter filter = new ScopeArtifactFilter(Artifact.SCOPE_RUNTIME_PLUS_SYSTEM);
        TestCase.assertTrue(filter.include(newArtifact(SCOPE_COMPILE)));
        TestCase.assertTrue(filter.include(newArtifact(SCOPE_SYSTEM)));
        TestCase.assertFalse(filter.include(newArtifact(SCOPE_PROVIDED)));
        TestCase.assertTrue(filter.include(newArtifact(SCOPE_RUNTIME)));
        TestCase.assertFalse(filter.include(newArtifact(SCOPE_TEST)));
    }

    public void testInclude_Test() {
        ScopeArtifactFilter filter = new ScopeArtifactFilter(Artifact.SCOPE_TEST);
        TestCase.assertTrue(filter.include(newArtifact(SCOPE_COMPILE)));
        TestCase.assertTrue(filter.include(newArtifact(SCOPE_SYSTEM)));
        TestCase.assertTrue(filter.include(newArtifact(SCOPE_PROVIDED)));
        TestCase.assertTrue(filter.include(newArtifact(SCOPE_RUNTIME)));
        TestCase.assertTrue(filter.include(newArtifact(SCOPE_TEST)));
    }
}

