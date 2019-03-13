/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.maven;


import java.io.File;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class PackageHelperTest {
    @Test
    public void testFileToString() throws Exception {
        Assert.assertEquals("dk19i21)@+#(OR", PackageHelper.fileToString(ResourceUtils.getResourceAsFile("filecontent/a.txt")));
    }

    @Test
    public void testFindJsonFiles() throws Exception {
        Map<String, File> jsonFiles = PackageHelper.findJsonFiles(ResourceUtils.getResourceAsFile("json"));
        Assert.assertTrue("Files a.json must be found", jsonFiles.containsKey("a"));
        Assert.assertTrue("Files b.json must be found", jsonFiles.containsKey("b"));
        Assert.assertFalse("File c.txt must not be found", jsonFiles.containsKey("c"));
    }
}

