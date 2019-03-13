/**
 * This file is part of dependency-check-core.
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
 *
 * Copyright (c) 2012 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.utils;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Jeremy Long
 */
public class FileUtilsTest extends BaseTest {
    /**
     * Test of getFileExtension method, of class FileUtils.
     */
    @Test
    public void testGetFileExtension() {
        String[] fileName = new String[]{ "something-0.9.5.jar", "lib2-1.1.js", "dir.tmp/noext" };
        String[] expResult = new String[]{ "jar", "js", null };
        for (int i = 0; i < (fileName.length); i++) {
            String result = FileUtils.getFileExtension(fileName[i]);
            Assert.assertEquals((("Failed extraction on \"" + (fileName[i])) + "\"."), expResult[i], result);
        }
    }

    /**
     * Test of delete method, of class FileUtils.
     */
    @Test
    public void testDelete() throws Exception {
        File file = File.createTempFile("tmp", "deleteme", getSettings().getTempDirectory());
        if (!(file.exists())) {
            Assert.fail("Unable to create a temporary file.");
        }
        boolean status = FileUtils.delete(file);
        Assert.assertTrue("delete returned a failed status", status);
        Assert.assertFalse("Temporary file exists after attempting deletion", file.exists());
    }
}

