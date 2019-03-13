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


import Parcel.Serialization;
import android.content.Intent;
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
public class ParcelTest {
    @Test
    public void testParcel() {
        String one = "one";
        int two = 2;
        long three = 3;
        String name = "name";
        SubParcel sub = new SubParcel(name);
        ExampleParcel example = new ExampleParcel(one, two, three, sub);
        ExampleParcel exampleParcel = Parcels.unwrap(ParcelsTestUtil.wrap(example));
        Assert.assertEquals(one, exampleParcel.getOne());
        Assert.assertEquals(two, exampleParcel.getTwo());
        Assert.assertEquals(three, exampleParcel.getThree());
        Assert.assertEquals(sub, exampleParcel.getFour());
        Assert.assertEquals(name, exampleParcel.getFour().getName());
    }

    @Test
    public void testManualSerialization() {
        String value = "test";
        Manual input = new Manual();
        input.setValue(value);
        Manual output = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        Assert.assertEquals(input.getValue(), output.getValue());
    }

    @Test
    public void testManuallyRegistered() {
        String value = "test";
        ExternalParcel input = new ExternalParcel();
        input.setValue(value);
        ExternalParcel output = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        Assert.assertEquals(input.getValue(), output.getValue());
    }

    @Test
    public void testManuallyRegisteredSerialization() {
        String value = "test";
        ManuallyRegistered input = new ManuallyRegistered();
        input.setValue(value);
        ManuallyRegistered output = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        Assert.assertEquals(input.getValue(), output.getValue());
    }

    @Test
    public void testIntArrayConstructorOrder() {
        ParcelTest.IntArrayClass prewrap = new ParcelTest.IntArrayClass();
        prewrap.setI(10);
        prewrap.setArr(new int[]{ 1, 2, 3, 4, 5 });
        ParcelTest.IntArrayClass unwrapped = Parcels.unwrap(ParcelsTestUtil.wrap(prewrap));
        Assert.assertEquals(10, unwrapped.i);
        Assert.assertArrayEquals(new int[]{ 1, 2, 3, 4, 5 }, unwrapped.getArr());
    }

    @Parcel(Serialization.BEAN)
    public static class IntArrayClass {
        int[] arr;

        int i;

        @ParcelConstructor
        public IntArrayClass(int i, int[] arr) {
            this.arr = arr;
            this.i = i;
        }

        public IntArrayClass() {
        }

        public int[] getArr() {
            return arr;
        }

        public void setArr(int[] arr) {
            this.arr = arr;
        }

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }
    }

    @Test
    public void testIntArrayFactoryOrder() {
        ParcelTest.IntArrayFactory prewrap = ParcelTest.IntArrayFactory.build(10, new int[]{ 1, 2, 3, 4, 5 });
        ParcelTest.IntArrayFactory unwrapped = Parcels.unwrap(ParcelsTestUtil.wrap(prewrap));
        Assert.assertEquals(10, unwrapped.i);
        Assert.assertArrayEquals(new int[]{ 1, 2, 3, 4, 5 }, unwrapped.getArr());
    }

    @Test
    public void testParcelableClassWithoutAnnotation() {
        Intent expected = new Intent("someAction");
        Intent actual = Parcels.unwrap(ParcelsTestUtil.wrap(expected));
        Assert.assertEquals(expected, actual);
    }

    @Parcel(Serialization.BEAN)
    public static class IntArrayFactory {
        int[] arr;

        int i;

        @ParcelFactory
        public static ParcelTest.IntArrayFactory build(int i, int[] arr) {
            return new ParcelTest.IntArrayFactory(i, arr);
        }

        public IntArrayFactory(int i, int[] arr) {
            this.arr = arr;
            this.i = i;
        }

        public IntArrayFactory() {
        }

        public int[] getArr() {
            return arr;
        }

        public void setArr(int[] arr) {
            this.arr = arr;
        }

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }
    }
}

