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
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class DateExtensionsTest extends AbstractProcessorTest {
    private static final String packagePath = "src/test/java/com/querydsl/apt/";

    @Test
    public void handles_date_extensions_correctly() throws IOException, InterruptedException {
        File source = new File(DateExtensionsTest.packagePath, "EntityWithExtensions.java");
        File source2 = new File(DateExtensionsTest.packagePath, "DateExtensions.java");
        List<String> sources = Arrays.asList(source.getPath(), source2.getPath());
        File qType = new File("target/overwrite3/com/querydsl/apt/QEntityWithExtensions.java");
        // QEntityWithExtensions is generated
        process(QuerydslAnnotationProcessor.class, sources, "overwrite3");
        Assert.assertTrue(qType.exists());
        long modified = qType.lastModified();
        Thread.sleep(1000);
        Assert.assertTrue(Files.toString(qType, Charsets.UTF_8).contains("QDate"));
        // EntityWithExtensions has not changed, QEntityWithExtensions is not overwritten
        compile(QuerydslAnnotationProcessor.class, sources, "overwrite3");
        Assert.assertEquals(modified, qType.lastModified());
        // EntityWithExtensions is updated, QEntityWithExtensions is overwritten
        Files.touch(source);
        compile(QuerydslAnnotationProcessor.class, sources, "overwrite3");
        Assert.assertTrue(((("" + modified) + " >= ") + (qType.lastModified())), (modified < (qType.lastModified())));
        Assert.assertTrue(Files.toString(qType, Charsets.UTF_8).contains("QDate"));
        // QEntityWithExtensions is deleted and regenerated
        Assert.assertTrue(qType.delete());
        compile(QuerydslAnnotationProcessor.class, sources, "overwrite3");
        Assert.assertTrue(qType.exists());
        Assert.assertTrue(Files.toString(qType, Charsets.UTF_8).contains("QDate"));
    }
}

