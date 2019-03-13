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
public class MultipleNullTest {
    @Parcel
    public static class MultipleNullTarget {
        MultipleNullTest.SubPlaceholder a;

        MultipleNullTest.SubPlaceholder b;

        String c;
    }

    @Parcel
    public static class SubPlaceholder {}

    @Test
    public void testMultipleNullSubParcels() {
        MultipleNullTest.MultipleNullTarget target = new MultipleNullTest.MultipleNullTarget();
        MultipleNullTest.MultipleNullTarget output = Parcels.unwrap(ParcelsTestUtil.wrap(target));
    }
}

