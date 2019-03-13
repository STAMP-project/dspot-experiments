/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.common.scanner;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.common.scanner.persistence.AnnotatedClassDescriptor;
import org.apache.drill.common.scanner.persistence.AnnotationDescriptor;
import org.apache.drill.common.scanner.persistence.FieldDescriptor;
import org.apache.drill.common.scanner.persistence.ScanResult;
import org.apache.drill.exec.expr.DrillFunc;
import org.apache.drill.exec.expr.annotations.FunctionTemplate;
import org.apache.drill.exec.fn.impl.testing.GeneratorFunctions;
import org.apache.drill.exec.physical.base.PhysicalOperator;
import org.apache.drill.exec.store.SystemPlugin;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SlowTest.class })
public class TestClassPathScanner {
    private static ScanResult result;

    @Test
    public void testFunctionTemplates() throws Exception {
        List<AnnotatedClassDescriptor> functions = TestClassPathScanner.result.getAnnotatedClasses(FunctionTemplate.class.getName());
        Set<String> scanned = new HashSet<>();
        AnnotatedClassDescriptor functionRandomBigIntGauss = null;
        for (AnnotatedClassDescriptor function : functions) {
            Assert.assertTrue(((function.getClassName()) + " scanned twice"), scanned.add(function.getClassName()));
            if (function.getClassName().equals(GeneratorFunctions.RandomBigIntGauss.class.getName())) {
                functionRandomBigIntGauss = function;
            }
        }
        if (functionRandomBigIntGauss == null) {
            Assert.fail("functionRandomBigIntGauss not found");
        }
        // TODO: use Andrew's randomized test framework to verify a subset of the functions
        for (AnnotatedClassDescriptor function : functions) {
            Class<?> c = Class.forName(function.getClassName(), false, this.getClass().getClassLoader());
            Field[] fields = c.getDeclaredFields();
            Assert.assertEquals(("fields count for " + function), fields.length, function.getFields().size());
            for (int i = 0; i < (fields.length); i++) {
                FieldDescriptor fieldDescriptor = function.getFields().get(i);
                Field field = fields[i];
                Assert.assertEquals(((("Class fields:\n" + (Arrays.toString(fields))) + "\n != \nDescriptor fields:\n") + (function.getFields())), field.getName(), fieldDescriptor.getName());
                verifyAnnotations(field.getDeclaredAnnotations(), fieldDescriptor.getAnnotations());
                Assert.assertEquals(field.getType(), fieldDescriptor.getFieldClass());
            }
            Annotation[] annotations = c.getDeclaredAnnotations();
            List<AnnotationDescriptor> scannedAnnotations = function.getAnnotations();
            verifyAnnotations(annotations, scannedAnnotations);
            FunctionTemplate bytecodeAnnotation = function.getAnnotationProxy(FunctionTemplate.class);
            Assert.assertNotNull(bytecodeAnnotation);
            FunctionTemplate reflectionAnnotation = c.getAnnotation(FunctionTemplate.class);
            Assert.assertEquals(reflectionAnnotation.name(), bytecodeAnnotation.name());
            Assert.assertArrayEquals(reflectionAnnotation.names(), bytecodeAnnotation.names());
            Assert.assertEquals(reflectionAnnotation.scope(), bytecodeAnnotation.scope());
            Assert.assertEquals(reflectionAnnotation.nulls(), bytecodeAnnotation.nulls());
            Assert.assertEquals(reflectionAnnotation.isBinaryCommutative(), bytecodeAnnotation.isBinaryCommutative());
            Assert.assertEquals(reflectionAnnotation.desc(), bytecodeAnnotation.desc());
            Assert.assertEquals(reflectionAnnotation.costCategory(), bytecodeAnnotation.costCategory());
        }
        for (String baseType : TestClassPathScanner.result.getScannedClasses()) {
            validateType(TestClassPathScanner.result, baseType);
        }
        Assert.assertTrue(((TestClassPathScanner.result.getImplementations(PhysicalOperator.class).size()) > 0));
        Assert.assertTrue(((TestClassPathScanner.result.getImplementations(DrillFunc.class).size()) > 0));
    }

    @Test
    public void testSystemPlugins() {
        List<AnnotatedClassDescriptor> annotatedClasses = TestClassPathScanner.result.getAnnotatedClasses(SystemPlugin.class.getName());
        List<AnnotatedClassDescriptor> foundPlugins = annotatedClasses.stream().filter(( a) -> (.class.getName().equals(a.getClassName())) || (.class.getName().equals(a.getClassName()))).collect(Collectors.toList());
        Assert.assertEquals(2, foundPlugins.size());
    }
}

