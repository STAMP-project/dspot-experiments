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


import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class IncludedPackagesTest extends AbstractProcessorTest {
    private static final String packagePath = "src/test/java/com/querydsl/";

    @Test
    public void process() throws IOException {
        List<String> classes = AbstractProcessorTest.getFiles(IncludedPackagesTest.packagePath);
        process(QuerydslAnnotationProcessor.class, classes, "includedPackages");
        Assert.assertFalse(new File("target/includedPackages/com/querydsl/apt/domain/p1").exists());
        Assert.assertTrue(new File("target/includedPackages/com/querydsl/apt/domain/p2").exists());
    }
}

