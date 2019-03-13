/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.kproject.memory;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.drools.compiler.compiler.io.File;
import org.drools.compiler.compiler.io.FileSystem;
import org.drools.compiler.compiler.io.Folder;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.core.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;


public class MemoryFileTest {
    @Test
    public void testFileCreation() throws IOException {
        FileSystem fs = new MemoryFileSystem();
        Folder mres = fs.getFolder("src/main/java/org/domain");
        File f1 = mres.getFile("MyClass.java");
        f1.create(new ByteArrayInputStream("ABC".getBytes()));
        mres.create();
        f1 = mres.getFile("MyClass.java");
        Assert.assertTrue(f1.exists());
        f1.create(new ByteArrayInputStream("ABC".getBytes()));
        f1 = mres.getFile("MyClass.java");
        Assert.assertTrue(f1.exists());
        Assert.assertEquals("ABC", StringUtils.toString(f1.getContents()));
        f1.create(new ByteArrayInputStream("ABC".getBytes()));
        f1.setContents(new ByteArrayInputStream("DEF".getBytes()));
        Assert.assertEquals("DEF", StringUtils.toString(f1.getContents()));
    }

    @Test
    public void testFileRemoval() throws IOException {
        FileSystem fs = new MemoryFileSystem();
        Folder mres = fs.getFolder("src/main/java/org/domain");
        mres.create();
        File f1 = mres.getFile("MyClass.java");
        f1.create(new ByteArrayInputStream("ABC".getBytes()));
        Assert.assertTrue(f1.exists());
        Assert.assertEquals("ABC", StringUtils.toString(f1.getContents()));
        fs.remove(f1);
        f1 = mres.getFile("MyClass.java");
        Assert.assertFalse(f1.exists());
        try {
            f1.getContents();
            Assert.fail("Should throw IOException");
        } catch (IOException e) {
        }
    }

    @Test
    public void testFilePath() {
        FileSystem fs = new MemoryFileSystem();
        Folder mres = fs.getFolder("src/main/java/org/domain");
        File f1 = mres.getFile("MyClass.java");
        Assert.assertEquals("src/main/java/org/domain/MyClass.java", f1.getPath().toPortableString());
    }

    @Test
    public void testRelativeToParentFilePath() {
        FileSystem fs = new MemoryFileSystem();
        Folder mres = fs.getFolder("src/main/java/org/domain");
        Folder f2 = fs.getFolder("src/main/java/org/domain/f1/f2/");
        File f1 = mres.getFile("MyClass.java");
        Assert.assertEquals("../../MyClass.java", f1.getPath().toRelativePortableString(f2.getPath()));
    }

    @Test
    public void testRelativeToBranchFilePath() {
        FileSystem fs = new MemoryFileSystem();
        Folder mres = fs.getFolder("src/main/java/org/domain");
        Folder f2 = fs.getFolder("src/main/resources/org/domain/");
        File f1 = mres.getFile("MyClass.java");
        Assert.assertEquals("../../../java/org/domain/MyClass.java", f1.getPath().toRelativePortableString(f2.getPath()));
    }
}

