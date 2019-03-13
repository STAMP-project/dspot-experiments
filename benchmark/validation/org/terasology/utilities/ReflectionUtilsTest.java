/**
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.utilities;


import org.junit.Assert;
import org.junit.Test;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.location.LocationComponent;
import org.terasology.reflection.copy.CopyStrategy;


/**
 *
 */
public class ReflectionUtilsTest {
    @Test
    public void testGetParameterForField() throws Exception {
        Assert.assertEquals(EntityRef.class, ReflectionUtil.getTypeParameter(LocationComponent.class.getDeclaredField("children").getGenericType(), 0));
    }

    @Test
    public void testGetParameterForGenericInterface() throws Exception {
        Assert.assertEquals(Integer.class, ReflectionUtil.getTypeParameterForSuper(ReflectionUtilsTest.ParameterisedInterfaceImplementor.class, CopyStrategy.class, 0));
    }

    @Test
    public void testGetParameterForBuriedGenericInterface() throws Exception {
        Assert.assertEquals(Integer.class, ReflectionUtil.getTypeParameterForSuper(ReflectionUtilsTest.Subclass.class, CopyStrategy.class, 0));
    }

    @Test
    public void testGetParameterForUnboundGenericInterface() throws Exception {
        Assert.assertEquals(null, ReflectionUtil.getTypeParameterForSuper(ReflectionUtilsTest.UnboundInterfaceImplementor.class, CopyStrategy.class, 0));
    }

    public static class ParameterisedInterfaceImplementor implements CopyStrategy<Integer> {
        @Override
        public Integer copy(Integer value) {
            return null;
        }
    }

    public static class Subclass extends ReflectionUtilsTest.ParameterisedInterfaceImplementor {}

    public static class UnboundInterfaceImplementor<T> implements CopyStrategy<T> {
        @Override
        public T copy(T value) {
            return null;
        }
    }
}

