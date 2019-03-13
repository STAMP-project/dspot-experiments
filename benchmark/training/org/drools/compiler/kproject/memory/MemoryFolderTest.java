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
import org.drools.compiler.compiler.io.memory.MemoryFolder;
import org.junit.Assert;
import org.junit.Test;


public class MemoryFolderTest {
    @Test
    public void testGetParentWithLeadingAndTrailingSlash() {
        MemoryFileSystem mfs = new MemoryFileSystem();
        Assert.assertEquals("", getParent().getPath().toPortableString());
        Assert.assertEquals("", getParent().getPath().toPortableString());
        Assert.assertEquals("", getParent().getPath().toPortableString());
        Assert.assertEquals("src", getParent().getPath().toPortableString());
        Assert.assertEquals("src", getParent().getPath().toPortableString());
        Assert.assertEquals("src", getParent().getPath().toPortableString());
        Assert.assertEquals("src/main", getParent().getPath().toPortableString());
        Assert.assertEquals("src/main", getParent().getPath().toPortableString());
        Assert.assertEquals("src/main", getParent().getPath().toPortableString());
    }

    @Test
    public void testRecursiveFolderCreation() {
        FileSystem fs = new MemoryFileSystem();
        Folder mres = fs.getFolder("src/main/resources");
        Assert.assertFalse(mres.exists());
        mres.create();
        Assert.assertTrue(mres.exists());
        Folder fld = fs.getFolder("src/main");
        Assert.assertTrue(fld.exists());
        Folder src = fs.getFolder("src");
        Assert.assertTrue(src.exists());
    }

    @Test
    public void testFolderGetParent() {
        FileSystem fs = new MemoryFileSystem();
        Folder mres = fs.getFolder("src/main/resources");
        mres.create();
        Assert.assertEquals("src/main", mres.getParent().getPath().toPortableString());
        Assert.assertEquals("src", getParent().getPath().toPortableString());
    }

    @Test
    public void testNestedRelativePath() {
        FileSystem fs = new MemoryFileSystem();
        Folder f1 = fs.getFolder("src/main/java");
        Folder f2 = fs.getFolder("src/main/java/org");
        f1.create();
        f2.create();
        Assert.assertEquals("org", f2.getPath().toRelativePortableString(f1.getPath()));
        fs = new MemoryFileSystem();
        f1 = fs.getFolder("src/main/java");
        f2 = fs.getFolder("src/main/java/org/drools/reteoo");
        f1.create();
        f2.create();
        Assert.assertEquals("org/drools/reteoo", f2.getPath().toRelativePortableString(f1.getPath()));
    }

    @Test
    public void testNestedRelativePathReverseArguments() {
        FileSystem fs = new MemoryFileSystem();
        Folder f1 = fs.getFolder("src/main/java/org");
        Folder f2 = fs.getFolder("src/main/java/");
        f1.create();
        f2.create();
        Assert.assertEquals("..", f2.getPath().toRelativePortableString(f1.getPath()));
        fs = new MemoryFileSystem();
        f1 = fs.getFolder("src/main/java/org/drools/reteoo");
        f2 = fs.getFolder("src/main/java");
        f1.create();
        f2.create();
        Assert.assertEquals("../../..", f2.getPath().toRelativePortableString(f1.getPath()));
    }

    @Test
    public void testNestedRelativeDifferentPath() {
        FileSystem fs = new MemoryFileSystem();
        Folder f1 = fs.getFolder("src/main/java");
        Folder f2 = fs.getFolder("src/main/resources");
        f1.create();
        f2.create();
        Assert.assertEquals("../resources", f2.getPath().toRelativePortableString(f1.getPath()));
        fs = new MemoryFileSystem();
        f1 = fs.getFolder("src/main/java/org/drools");
        f2 = fs.getFolder("src/main/resources/org/drools/reteoo");
        f1.create();
        f2.create();
        Assert.assertEquals("../../../resources/org/drools/reteoo", f2.getPath().toRelativePortableString(f1.getPath()));
    }

    @Test
    public void testFolderRemoval() throws IOException {
        FileSystem fs = new MemoryFileSystem();
        Folder fld = fs.getFolder("src/main/resources/org/domain");
        fld.create();
        fld = fs.getFolder("src/main");
        File file = fld.getFile("MyClass1.java");
        file.create(new ByteArrayInputStream("ABC1".getBytes()));
        file = fld.getFile("MyClass2.java");
        file.create(new ByteArrayInputStream("ABC2".getBytes()));
        fld = fs.getFolder("src/main/resources/org");
        file = fld.getFile("MyClass3.java");
        file.create(new ByteArrayInputStream("ABC3".getBytes()));
        file = fld.getFile("MyClass4.java");
        file.create(new ByteArrayInputStream("ABC4".getBytes()));
        fld = fs.getFolder("src/main/resources/org/domain");
        file = fld.getFile("MyClass4.java");
        file.create(new ByteArrayInputStream("ABC5".getBytes()));
        Assert.assertTrue(fs.getFolder("src/main").exists());
        Assert.assertTrue(fs.getFile("src/main/MyClass1.java").exists());
        Assert.assertTrue(fs.getFile("src/main/MyClass2.java").exists());
        Assert.assertTrue(fs.getFile("src/main/resources/org/MyClass3.java").exists());
        Assert.assertTrue(fs.getFile("src/main/resources/org/MyClass4.java").exists());
        Assert.assertTrue(fs.getFile("src/main/resources/org/domain/MyClass4.java").exists());
        fs.remove(fs.getFolder("src/main"));
        Assert.assertFalse(fs.getFolder("src/main").exists());
        Assert.assertFalse(fs.getFile("src/main/MyClass1.java").exists());
        Assert.assertFalse(fs.getFile("src/main/MyClass2.java").exists());
        Assert.assertFalse(fs.getFile("src/main/resources/org/MyClass3.java").exists());
        Assert.assertFalse(fs.getFile("src/main/resources/org/MyClass4.java").exists());
        Assert.assertFalse(fs.getFile("src/main/resources/org/domain/MyClass4.java").exists());
    }

    @Test
    public void trimLeadingAndTrailing() {
        Assert.assertEquals("", MemoryFolder.trimLeadingAndTrailing(""));
        Assert.assertEquals("src/main", MemoryFolder.trimLeadingAndTrailing("/src/main"));
        Assert.assertEquals("src/main", MemoryFolder.trimLeadingAndTrailing("src/main/"));
        Assert.assertEquals("src/main", MemoryFolder.trimLeadingAndTrailing("/src/main/"));
    }

    @Test
    public void testCreateAndCopyFolder() {
        MemoryFileSystem memoryFileSystem = new MemoryFileSystem();
        // this also creates a folder if it doesn't exist
        final Folder emptyFolder = memoryFileSystem.getFolder("emptyfolder");
        final MemoryFolder destinationFolder = new MemoryFolder(memoryFileSystem, "destinationfolder");
        memoryFileSystem.createFolder(destinationFolder);
        memoryFileSystem.copyFolder(emptyFolder, memoryFileSystem, destinationFolder);
    }
}

