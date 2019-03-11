/**
 * Copyright 2011-2015 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parceler;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 *
 *
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EnumsTest {
    @Test
    public void testEnumsColleciton() {
        List<Enums.Values> values = new ArrayList<Enums.Values>();
        values.add(A);
        values.add(C);
        values.add(null);
        List<Enums.Values> unwrapped = Parcels.unwrap(ParcelsTestUtil.wrap(values));
        Assert.assertEquals(3, unwrapped.size());
        Assert.assertEquals(A, unwrapped.get(0));
        Assert.assertEquals(C, unwrapped.get(1));
        Assert.assertNull(unwrapped.get(2));
    }

    @Test
    public void testSubEnums() {
        Enums enums = new Enums();
        enums.one = A;
        enums.two = null;
        enums.three = B;
        enums.enumArray = new Enums.Values[]{ A, C };
        Enums unwrapped = Parcels.unwrap(ParcelsTestUtil.wrap(enums));
        Assert.assertEquals(A, unwrapped.one);
        Assert.assertNull(unwrapped.two);
        Assert.assertEquals(B, unwrapped.three);
        Assert.assertArrayEquals(enums.enumArray, unwrapped.enumArray);
    }
}

