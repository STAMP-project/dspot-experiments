/**
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique.resource;


import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class ResourceFactoryTest {
    @Test
    public void testGetCanonicalFile() throws IOException {
        File file = new ResourceFactory("").getCanonicalFile("./src/test/resources/io/bootique/config/test1.yml");
        File expected = new File(((System.getProperty("user.dir")) + "/src/test/resources/io/bootique/config/test1.yml"));
        Assert.assertEquals(expected, file);
    }

    @Test
    public void testGetUrl_File() throws IOException {
        Assert.assertEquals("a: b", ResourceFactoryTest.resourceContents("src/test/resources/io/bootique/config/test1.yml"));
    }

    @Test
    public void testGetUrl_File_DotSlash() throws IOException {
        Assert.assertEquals("a: b", ResourceFactoryTest.resourceContents("./src/test/resources/io/bootique/config/test1.yml"));
    }

    @Test
    public void testGetUrl_FileUrl() throws IOException {
        String fileUrl = ResourceFactoryTest.fileUrl("src/test/resources/io/bootique/config/test2.yml");
        Assert.assertEquals("c: d", ResourceFactoryTest.resourceContents(fileUrl));
    }

    @Test
    public void testGetUrl_JarUrl() throws IOException {
        String jarUrl = ResourceFactoryTest.jarEntryUrl("src/test/resources/io/bootique/config/test3.jar", "com/foo/test3.yml");
        Assert.assertEquals("e: f", ResourceFactoryTest.resourceContents(jarUrl));
    }

    @Test
    public void testGetUrl_ClasspathUrl() throws IOException {
        String cpUrl = "classpath:io/bootique/config/test2.yml";
        Assert.assertEquals("c: d", ResourceFactoryTest.resourceContents(cpUrl));
    }

    @Test(expected = RuntimeException.class)
    public void testGetUrl_ClasspathUrlWithSlash() throws IOException {
        String cpUrl = "classpath:/io/bootique/config/test2.yml";
        ResourceFactoryTest.resourceContents(cpUrl);
    }
}

