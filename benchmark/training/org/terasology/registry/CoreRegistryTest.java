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
package org.terasology.registry;


import com.google.common.collect.Maps;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.terasology.context.Context;


public class CoreRegistryTest {
    private Context context;

    /**
     * Check if the context is changed with setContext method.
     */
    @Test
    public void testContextChange() {
        CoreRegistry.setContext(new CoreRegistryTest.ContextImplementation());
        Assert.assertNotEquals(CoreRegistry.get(Context.class), context);
    }

    /**
     * Check if CoreRegistry returns null on its methods when the context is not defined.
     */
    @Test
    public void testNullReturnOnMissingContext() {
        CoreRegistry.setContext(null);
        Assert.assertEquals(CoreRegistry.put(Integer.class, 10), null);
        Assert.assertEquals(CoreRegistry.get(Integer.class), null);
    }

    /**
     * Test if the CoreRegistry context is being returned by the get method when the argument is Context.class
     * independently of the Context implementation.
     */
    @Test
    public void testContextGetIndependenceFromContextInterfaceImplementation() {
        Assert.assertEquals(CoreRegistry.get(Context.class), context);
        Assert.assertEquals(context.get(Context.class), null);
    }

    /**
     * Check if the CoreRegistry is calling the methods of its Context
     */
    @Test
    public void testContextMethodsCalled() {
        // Load value in context
        Integer value = 10;
        CoreRegistry.put(Integer.class, value);
        Assert.assertEquals(value, context.get(Integer.class));
        Assert.assertEquals(context.get(Integer.class), CoreRegistry.get(Integer.class));
        // Change context
        CoreRegistry.setContext(new CoreRegistryTest.ContextImplementation());
        Assert.assertNotEquals(CoreRegistry.get(Context.class), context);
        Assert.assertEquals(CoreRegistry.get(Integer.class), null);
        // Restore first context
        CoreRegistry.setContext(context);
        Assert.assertEquals(CoreRegistry.get(Integer.class), value);
    }

    private static class ContextImplementation implements Context {
        private final Map<Class<?>, Object> map = Maps.newConcurrentMap();

        @Override
        public <T> T get(Class<? extends T> type) {
            T result = type.cast(map.get(type));
            if (result != null) {
                return result;
            }
            return null;
        }

        @Override
        public <T, U extends T> void put(Class<T> type, U object) {
            map.put(type, object);
        }
    }
}

