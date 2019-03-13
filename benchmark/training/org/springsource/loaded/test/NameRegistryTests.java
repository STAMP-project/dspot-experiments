/**
 * Copyright 2010-2012 VMware and contributors
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
package org.springsource.loaded.test;


import org.junit.Assert;
import org.junit.Test;
import org.springsource.loaded.NameRegistry;


/**
 * Test the NameRegistry behaves as expected.
 *
 * @author Andy Clement
 * @since 0.8.1
 */
public class NameRegistryTests extends SpringLoadedTests {
    @Test
    public void byTheNumbers() {
        Assert.assertEquals((-1), NameRegistry.getIdFor("a/b/C"));
        Assert.assertEquals(0, NameRegistry.getIdOrAllocateFor("a/b/C"));
        Assert.assertEquals(0, NameRegistry.getIdFor("a/b/C"));
        Assert.assertEquals(1, NameRegistry.getIdOrAllocateFor("d/e/F"));
        Assert.assertEquals(1, NameRegistry.getIdOrAllocateFor("d/e/F"));
        // test expansion
        for (int i = 0; i < 100; i++) {
            Assert.assertEquals((2 + i), NameRegistry.getIdOrAllocateFor(("a/b/C" + i)));
        }
        Assert.assertEquals("a/b/C", NameRegistry.getTypenameById(0));
        Assert.assertNull(NameRegistry.getTypenameById(1000));
    }
}

