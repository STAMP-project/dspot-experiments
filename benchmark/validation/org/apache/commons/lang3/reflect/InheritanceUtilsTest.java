/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.reflect;


import org.apache.commons.lang3.reflect.testbed.AnotherChild;
import org.apache.commons.lang3.reflect.testbed.AnotherParent;
import org.apache.commons.lang3.reflect.testbed.Grandchild;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unit tests InheritanceUtils
 */
public class InheritanceUtilsTest {
    @Test
    public void testConstructor() throws Exception {
        Assertions.assertNotNull(InheritanceUtils.class.newInstance());
    }

    @Test
    public void testDistanceGreaterThanZero() {
        Assertions.assertEquals(1, InheritanceUtils.distance(AnotherChild.class, AnotherParent.class));
        Assertions.assertEquals(1, InheritanceUtils.distance(Grandchild.class, AnotherChild.class));
        Assertions.assertEquals(2, InheritanceUtils.distance(Grandchild.class, AnotherParent.class));
        Assertions.assertEquals(3, InheritanceUtils.distance(Grandchild.class, Object.class));
    }

    @Test
    public void testDistanceEqual() {
        Assertions.assertEquals(0, InheritanceUtils.distance(AnotherChild.class, AnotherChild.class));
    }

    @Test
    public void testDistanceEqualObject() {
        Assertions.assertEquals(0, InheritanceUtils.distance(Object.class, Object.class));
    }

    @Test
    public void testDistanceNullChild() {
        Assertions.assertEquals((-1), InheritanceUtils.distance(null, Object.class));
    }

    @Test
    public void testDistanceNullParent() {
        Assertions.assertEquals((-1), InheritanceUtils.distance(Object.class, null));
    }

    @Test
    public void testDistanceNullParentNullChild() {
        Assertions.assertEquals((-1), InheritanceUtils.distance(null, null));
    }

    @Test
    public void testDistanceDisjoint() {
        Assertions.assertEquals((-1), InheritanceUtils.distance(Boolean.class, String.class));
    }

    @Test
    public void testDistanceReverseParentChild() {
        Assertions.assertEquals((-1), InheritanceUtils.distance(Object.class, Grandchild.class));
    }
}

