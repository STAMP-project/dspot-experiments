/**
 * Copyright 2011 gitblit.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gitblit.tests;


import com.gitblit.utils.FileUtils;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;


public class FileUtilsTest extends GitblitUnitTest {
    @Test
    public void testReadContent() throws Exception {
        File dir = new File(System.getProperty("user.dir"));
        String rawContent = FileUtils.readContent(new File(dir, "LICENSE"), "\n");
        Assert.assertTrue(rawContent.trim().startsWith("Apache License"));
    }

    @Test
    public void testWriteContent() throws Exception {
        String contentA = "this is a test";
        File tmp = File.createTempFile("gitblit-", ".test");
        FileUtils.writeContent(tmp, contentA);
        String contentB = FileUtils.readContent(tmp, "\n").trim();
        Assert.assertEquals(contentA, contentB);
    }

    @Test
    public void testFolderSize() throws Exception {
        Assert.assertEquals((-1), FileUtils.folderSize(null));
        Assert.assertEquals((-1), FileUtils.folderSize(new File(System.getProperty("user.dir"), "pretend")));
        File dir = new File(System.getProperty("user.dir"), "src/main/distrib");
        long size = FileUtils.folderSize(dir);
        Assert.assertTrue(("size is actually " + size), (size >= 470000L));
        File file = new File(System.getProperty("user.dir"), "LICENSE");
        size = FileUtils.folderSize(file);
        Assert.assertEquals(("size is actually " + size), 11556L, size);
    }

    @Test
    public void testStringSizes() throws Exception {
        Assert.assertEquals((50 * (FileUtils.KB)), FileUtils.convertSizeToInt("50k", 0));
        Assert.assertEquals((50 * (FileUtils.MB)), FileUtils.convertSizeToInt("50m", 0));
        Assert.assertEquals((2 * (FileUtils.GB)), FileUtils.convertSizeToInt("2g", 0));
        Assert.assertEquals((50 * (FileUtils.KB)), FileUtils.convertSizeToInt("50kb", 0));
        Assert.assertEquals((50 * (FileUtils.MB)), FileUtils.convertSizeToInt("50mb", 0));
        Assert.assertEquals((2 * (FileUtils.GB)), FileUtils.convertSizeToInt("2gb", 0));
        Assert.assertEquals((50L * (FileUtils.KB)), FileUtils.convertSizeToLong("50k", 0));
        Assert.assertEquals((50L * (FileUtils.MB)), FileUtils.convertSizeToLong("50m", 0));
        Assert.assertEquals((50L * (FileUtils.GB)), FileUtils.convertSizeToLong("50g", 0));
        Assert.assertEquals((50L * (FileUtils.KB)), FileUtils.convertSizeToLong("50kb", 0));
        Assert.assertEquals((50L * (FileUtils.MB)), FileUtils.convertSizeToLong("50mb", 0));
        Assert.assertEquals((50L * (FileUtils.GB)), FileUtils.convertSizeToLong("50gb", 0));
        Assert.assertEquals((50 * (FileUtils.KB)), FileUtils.convertSizeToInt("50 k", 0));
        Assert.assertEquals((50 * (FileUtils.MB)), FileUtils.convertSizeToInt("50 m", 0));
        Assert.assertEquals((2 * (FileUtils.GB)), FileUtils.convertSizeToInt("2 g", 0));
        Assert.assertEquals((50 * (FileUtils.KB)), FileUtils.convertSizeToInt("50 kb", 0));
        Assert.assertEquals((50 * (FileUtils.MB)), FileUtils.convertSizeToInt("50 mb", 0));
        Assert.assertEquals((2 * (FileUtils.GB)), FileUtils.convertSizeToInt("2 gb", 0));
    }
}

