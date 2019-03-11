/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.rules.objc;


import J2ObjcSource.SourceType.JAVA;
import J2ObjcSource.SourceType.PROTO;
import com.google.common.testing.EqualsTester;
import com.google.devtools.build.lib.actions.ArtifactRoot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit test for {@link J2ObjcSource}.
 */
@RunWith(JUnit4.class)
public class J2ObjcSourceTest {
    private ArtifactRoot rootDir;

    @Test
    public void testEqualsAndHashCode() throws Exception {
        new EqualsTester().addEqualityGroup(getJ2ObjcSource("//a/b:c", "sourceA", JAVA), getJ2ObjcSource("//a/b:c", "sourceA", JAVA)).addEqualityGroup(getJ2ObjcSource("//a/b:d", "sourceA", JAVA), getJ2ObjcSource("//a/b:d", "sourceA", JAVA)).addEqualityGroup(getJ2ObjcSource("//a/b:d", "sourceC", JAVA), getJ2ObjcSource("//a/b:d", "sourceC", JAVA)).addEqualityGroup(getJ2ObjcSource("//a/b:d", "sourceC", PROTO), getJ2ObjcSource("//a/b:d", "sourceC", PROTO)).testEquals();
    }
}

