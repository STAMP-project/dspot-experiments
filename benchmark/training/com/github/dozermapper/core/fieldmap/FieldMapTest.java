/**
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.fieldmap;


import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.classmap.ClassMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class FieldMapTest extends AbstractDozerTest {
    private ClassMap classMap;

    private FieldMap fieldMap;

    private DozerField field;

    @Test
    public void shouldSurviveConcurrentAccess() throws InterruptedException {
        DozerField dozerField = Mockito.mock(DozerField.class);
        Mockito.when(dozerField.getName()).thenReturn("");
        fieldMap.setSrcField(dozerField);
        fieldMap.setDestField(dozerField);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Callable<Object>> callables = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            callables.add(new Callable<Object>() {
                public Object call() {
                    return fieldMap.getSrcPropertyDescriptor(String.class);
                }
            });
        }
        executorService.invokeAll(callables);
        Thread.sleep(1000);
        executorService.shutdown();
    }

    @Test
    public void shouldBeAccessible_ClassLevel() {
        Mockito.when(classMap.getDestClass().isAccessible()).thenReturn(Boolean.TRUE);
        Mockito.when(field.isAccessible()).thenReturn(Boolean.FALSE);
        Assert.assertFalse(fieldMap.isDestFieldAccessible());
    }

    @Test
    public void shouldBeAccessible_Both() {
        Mockito.when(classMap.getDestClass().isAccessible()).thenReturn(Boolean.TRUE);
        Mockito.when(field.isAccessible()).thenReturn(Boolean.TRUE);
        Assert.assertTrue(fieldMap.isDestFieldAccessible());
    }

    @Test
    public void shouldBeAccessible_FieldLevel() {
        Mockito.when(classMap.getDestClass().isAccessible()).thenReturn(Boolean.FALSE);
        Mockito.when(field.isAccessible()).thenReturn(Boolean.TRUE);
        Assert.assertTrue(fieldMap.isDestFieldAccessible());
    }

    @Test
    public void shouldBeAccessible_Override() {
        Mockito.when(classMap.getDestClass().isAccessible()).thenReturn(Boolean.TRUE);
        Mockito.when(field.isAccessible()).thenReturn(Boolean.FALSE);
        Assert.assertFalse(fieldMap.isDestFieldAccessible());
    }

    @Test
    public void shouldBeAccessible_Null() {
        Mockito.when(classMap.getDestClass().isAccessible()).thenReturn(false);
        Mockito.when(field.isAccessible()).thenReturn(Boolean.TRUE);
        Assert.assertTrue(fieldMap.isDestFieldAccessible());
    }

    @Test
    public void shouldBeAccessible_FieldLevelNull() {
        Mockito.when(classMap.getDestClass().isAccessible()).thenReturn(Boolean.TRUE);
        Mockito.when(field.isAccessible()).thenReturn(null);
        Assert.assertTrue(fieldMap.isDestFieldAccessible());
    }

    @Test
    public void shouldBeAccessible_Default() {
        Mockito.when(classMap.getDestClass().isAccessible()).thenReturn(false);
        Mockito.when(field.isAccessible()).thenReturn(null);
        Assert.assertFalse(fieldMap.isDestFieldAccessible());
    }
}

