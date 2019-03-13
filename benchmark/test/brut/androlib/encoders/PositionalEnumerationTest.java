/**
 * Copyright (C) 2018 Ryszard Wi?niewski <brut.alll@gmail.com>
 *  Copyright (C) 2018 Connor Tumbleson <connor.tumbleson@gmail.com>
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
package brut.androlib.encoders;


import brut.androlib.BaseTest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Connor Tumbleson <connor.tumbleson@gmail.com>
 */
public class PositionalEnumerationTest extends BaseTest {
    @Test
    public void noArgumentsTest() {
        Assert.assertEquals("test", enumerateArguments("test"));
    }

    @Test
    public void twoArgumentsTest() {
        Assert.assertEquals("%1$s, %2$s, and 1 other.", enumerateArguments("%s, %s, and 1 other."));
    }

    @Test
    public void twoPositionalArgumentsTest() {
        Assert.assertEquals("%1$s, %2$s and 1 other", enumerateArguments("%1$s, %2$s and 1 other"));
    }

    @Test
    public void threeArgumentsTest() {
        Assert.assertEquals("%1$s, %2$s, and %3$d other.", enumerateArguments("%s, %s, and %d other."));
    }

    @Test
    public void threePositionalArgumentsTest() {
        Assert.assertEquals(" %1$s, %2$s and %3$d other", enumerateArguments(" %1$s, %2$s and %3$d other"));
    }

    @Test
    public void fourArgumentsTest() {
        Assert.assertEquals("%1$s, %2$s, and %3$d other and %4$d.", enumerateArguments("%s, %s, and %d other and %d."));
    }

    @Test
    public void fourPositionalArgumentsTest() {
        Assert.assertEquals(" %1$s, %2$s and %3$d other and %4$d.", enumerateArguments(" %1$s, %2$s and %3$d other and %4$d."));
    }
}

