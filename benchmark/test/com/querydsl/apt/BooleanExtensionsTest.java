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
package com.querydsl.apt;


import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class BooleanExtensionsTest extends AbstractProcessorTest {
    private static final String packagePath = "src/test/apt/com/querydsl/";

    @Test
    public void process() throws IOException {
        List<String> sources = Arrays.asList(new File(BooleanExtensionsTest.packagePath, "BooleanExtensions.java").getPath(), new File(BooleanExtensionsTest.packagePath, "ExampleEntity.java").getPath());
        process(QuerydslAnnotationProcessor.class, sources, "booleanExtensions");
        String qtypeContent = Files.toString(new File("target/booleanExtensions/com/querydsl/QExampleEntity.java"), Charsets.UTF_8);
        Assert.assertTrue(qtypeContent.contains("ext.java.lang.QBoolean booleanProp"));
        Assert.assertTrue(qtypeContent.contains("ext.java.lang.QBoolean booleanProp2"));
    }

    @Test
    public void process2() throws IOException {
        List<String> sources = Arrays.asList(new File(BooleanExtensionsTest.packagePath, "BooleanExtensions2.java").getPath(), new File(BooleanExtensionsTest.packagePath, "ExampleEntity.java").getPath());
        process(QuerydslAnnotationProcessor.class, sources, "booleanExtensions2");
        String qtypeContent = Files.toString(new File("target/booleanExtensions2/com/querydsl/QExampleEntity.java"), Charsets.UTF_8);
        Assert.assertTrue(qtypeContent.contains("ext.java.lang.QBoolean booleanProp"));
        Assert.assertTrue(qtypeContent.contains("ext.java.lang.QBoolean booleanProp2"));
    }
}

