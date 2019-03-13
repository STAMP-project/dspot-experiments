/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.core.util;


import com.querydsl.core.types.ProjectionExample;
import java.lang.reflect.Constructor;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Shredder121
 */
public class ConstructorUtilsTest {
    @Test
    public void getDefaultConstructor() {
        Class<?>[] args = new Class<?>[]{  };
        Constructor<?> emptyDefaultConstructor = getConstructor(ProjectionExample.class, args);
        Constructor<?> nullDefaultConstructor = getConstructor(ProjectionExample.class, null);
        Assert.assertNotNull(emptyDefaultConstructor);
        Assert.assertNotNull(nullDefaultConstructor);
        Assert.assertTrue(((ArrayUtils.isEmpty(emptyDefaultConstructor.getParameterTypes())) && (ArrayUtils.isEmpty(nullDefaultConstructor.getParameterTypes()))));
    }

    @Test
    public void getSimpleConstructor() {
        Class<?>[] args = new Class<?>[]{ Long.class };
        Constructor<?> constructor = getConstructor(ProjectionExample.class, args);
        Assert.assertNotNull(constructor);
        Assert.assertArrayEquals(args, constructor.getParameterTypes());
    }

    @Test
    public void getDefaultConstructorParameters() {
        Class<?>[] args = new Class<?>[]{ Long.class, String.class };
        Class<?>[] expected = new Class<?>[]{ Long.TYPE, String.class };
        Class<?>[] constructorParameters = ConstructorUtils.getConstructorParameters(ProjectionExample.class, args);
        Assert.assertArrayEquals("Constructorparameters not equal", expected, constructorParameters);
    }
}

