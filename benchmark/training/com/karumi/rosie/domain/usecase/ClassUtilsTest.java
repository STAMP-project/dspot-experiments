/**
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.karumi.rosie.domain.usecase;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ClassUtilsTest {
    @Test
    public void testCanAssignTwoAnyObjects() throws Exception {
        Assert.assertTrue(ClassUtils.canAssign(String.class, String.class));
    }

    @Test
    public void testCanAssignTwoAnyObjectsWithHierarchy() throws Exception {
        Assert.assertTrue(ClassUtils.canAssign(ClassUtilsTest.AnyClass.class, ClassUtilsTest.SonOfAnyClass.class));
    }

    @Test
    public void testCanAssignTwoObjectsWithAPrimitiveObject() throws Exception {
        Assert.assertTrue(ClassUtils.canAssign(int.class, Integer.class));
    }

    @Test
    public void testCanAssignTwoObjectsWithAPrimitiveObjectBase() throws Exception {
        Assert.assertTrue(ClassUtils.canAssign(Integer.class, int.class));
    }

    private class AnyClass {}

    private class SonOfAnyClass extends ClassUtilsTest.AnyClass {}
}

