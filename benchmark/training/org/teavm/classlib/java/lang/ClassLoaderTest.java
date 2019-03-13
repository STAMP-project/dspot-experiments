/**
 * Copyright 2015 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.lang;


import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class ClassLoaderTest {
    @Test
    public void loadsResources() {
        Assert.assertEquals("q", ClassLoaderTest.loadResource("1"));
        Assert.assertEquals("qw", ClassLoaderTest.loadResource("2"));
        Assert.assertEquals("qwe", ClassLoaderTest.loadResource("3"));
        Assert.assertEquals("qwer", ClassLoaderTest.loadResource("4"));
        Assert.assertEquals("qwert", ClassLoaderTest.loadResource("5"));
        Assert.assertEquals("qwerty", ClassLoaderTest.loadResource("6"));
        Assert.assertEquals("qwertyu", ClassLoaderTest.loadResource("7"));
        Assert.assertEquals("qwertyui", ClassLoaderTest.loadResource("8"));
        Assert.assertEquals("qwertyuiopasdfghjklzxcvbnm", ClassLoaderTest.loadResource("9"));
    }

    @Test
    public void returnsNullForNonExistentResource() {
        InputStream input = ClassLoader.getSystemClassLoader().getResourceAsStream("non-existent-resource.txt");
        Assert.assertNull(input);
    }
}

