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
import org.springsource.loaded.AnyTypePattern;
import org.springsource.loaded.ExactTypePattern;
import org.springsource.loaded.PrefixTypePattern;


/**
 * Tests for the TypePattern handling.
 *
 * @author Andy Clement
 * @since 1.0
 */
public class TypePatternTests extends SpringLoadedTests {
    @Test
    public void prefix() {
        PrefixTypePattern tp = new PrefixTypePattern("com.foo..*");
        Assert.assertEquals("text:com.foo..*", tp.toString());
        Assert.assertTrue(tp.matches("com.foo.Bar"));
        Assert.assertFalse(tp.matches("com.food.Bar"));
    }

    @Test
    public void exact() {
        ExactTypePattern tp = new ExactTypePattern("com.foo.Bark");
        Assert.assertTrue(tp.matches("com.foo.Bark"));
        Assert.assertFalse(tp.matches("com.food.Bar"));
    }

    @Test
    public void any() {
        AnyTypePattern tp = new AnyTypePattern();
        Assert.assertTrue(tp.matches("com.foo.Bar"));
        Assert.assertTrue(tp.matches("com.food.Bar"));
    }
}

