/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.util;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Rob Harrop
 */
public class FileSystemUtilsTests {
    @Test
    public void deleteRecursively() throws Exception {
        File root = new File("./tmp/root");
        File child = new File(root, "child");
        File grandchild = new File(child, "grandchild");
        grandchild.mkdirs();
        File bar = new File(child, "bar.txt");
        bar.createNewFile();
        Assert.assertTrue(root.exists());
        Assert.assertTrue(child.exists());
        Assert.assertTrue(grandchild.exists());
        Assert.assertTrue(bar.exists());
        FileSystemUtils.deleteRecursively(root);
        Assert.assertFalse(root.exists());
        Assert.assertFalse(child.exists());
        Assert.assertFalse(grandchild.exists());
        Assert.assertFalse(bar.exists());
    }

    @Test
    public void copyRecursively() throws Exception {
        File src = new File("./tmp/src");
        File child = new File(src, "child");
        File grandchild = new File(child, "grandchild");
        grandchild.mkdirs();
        File bar = new File(child, "bar.txt");
        bar.createNewFile();
        Assert.assertTrue(src.exists());
        Assert.assertTrue(child.exists());
        Assert.assertTrue(grandchild.exists());
        Assert.assertTrue(bar.exists());
        File dest = new File("./dest");
        FileSystemUtils.copyRecursively(src, dest);
        Assert.assertTrue(dest.exists());
        Assert.assertTrue(new File(dest, child.getName()).exists());
        FileSystemUtils.deleteRecursively(src);
        Assert.assertFalse(src.exists());
    }
}

