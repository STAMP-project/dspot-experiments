/**
 * Copyright ? 2010-2017 Nokia
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
package org.jsonschema2pojo.integration.config;


import SourceSortOrder.FILES_FIRST;
import SourceSortOrder.SUBDIRS_FIRST;
import java.lang.reflect.Method;
import org.jsonschema2pojo.integration.util.CodeGenerationHelper;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Rule;
import org.junit.Test;


public class SourceSortOrderIT {
    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void generatedClassesInCorrectPackageForFilesFirstSort() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/sourceSortOrder/", "com.example", CodeGenerationHelper.config("sourceSortOrder", FILES_FIRST.toString()));
        Class generatedTypeA = resultsClassLoader.loadClass("com.example.A");
        Class generatedTypeZ = resultsClassLoader.loadClass("com.example.Z");
        Method getterTypeA = generatedTypeA.getMethod("getRefToA");
        final Class<?> returnTypeA = getterTypeA.getReturnType();
        Method getterTypeZ = generatedTypeZ.getMethod("getRefToZ");
        final Class<?> returnTypeZ = getterTypeZ.getReturnType();
        assertInPackage("com.example", generatedTypeA);
        assertInPackage("com.example", generatedTypeZ);
        assertInPackage("com.example", returnTypeA);
        assertInPackage("com.example", returnTypeZ);
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void generatedClassesInCorrectPackageForDirectoriesFirstSort() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/sourceSortOrder/", "com.example", CodeGenerationHelper.config("sourceSortOrder", SUBDIRS_FIRST.toString()));
        Class generatedTypeA = resultsClassLoader.loadClass("com.example.A");
        Class generatedTypeZ = resultsClassLoader.loadClass("com.example.Z");
        Method getterTypeA = generatedTypeA.getMethod("getRefToA");
        final Class<?> returnTypeA = getterTypeA.getReturnType();
        Method getterTypeZ = generatedTypeZ.getMethod("getRefToZ");
        final Class<?> returnTypeZ = getterTypeZ.getReturnType();
        assertInPackage("com.example", generatedTypeA);
        assertInPackage("com.example", generatedTypeZ);
        assertInPackage("com.example.includes", returnTypeA);
        assertInPackage("com.example.includes", returnTypeZ);
    }
}

