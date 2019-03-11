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


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
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
public class CircularReferenceTest {
    @Parcel
    public static class One {
        CircularReferenceTest.Two two;

        int four;

        @ParcelConstructor
        public One(int four, CircularReferenceTest.Two two) {
            this.two = two;
            this.four = four;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof CircularReferenceTest.One))
                return false;

            CircularReferenceTest.One one = ((CircularReferenceTest.One) (o));
            return EqualsBuilder.reflectionEquals(this, one);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }

    @Parcel
    public static class Two {
        CircularReferenceTest.One one;

        String three;

        @ParcelConstructor
        public Two(String three, CircularReferenceTest.One one) {
            this.one = one;
            this.three = three;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof CircularReferenceTest.Two))
                return false;

            CircularReferenceTest.Two two = ((CircularReferenceTest.Two) (o));
            return EqualsBuilder.reflectionEquals(this, two);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }

    @Test
    public void testCircularReferences() {
        CircularReferenceTest.One target = new CircularReferenceTest.One(1, new CircularReferenceTest.Two("two", new CircularReferenceTest.One(3, new CircularReferenceTest.Two("four", new CircularReferenceTest.One(5, new CircularReferenceTest.Two("six", null))))));
        CircularReferenceTest.One unwrap = Parcels.unwrap(ParcelsTestUtil.wrap(target));
        Assert.assertEquals(target, unwrap);
    }

    @Parcel
    static class Three {
        CircularReferenceTest.Four four;

        String name;

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof CircularReferenceTest.Three))
                return false;

            CircularReferenceTest.Three three = ((CircularReferenceTest.Three) (o));
            return EqualsBuilder.reflectionEquals(this, three, new String[]{ "four" });
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, new String[]{ "four" });
        }
    }

    @Parcel
    static class Four {
        CircularReferenceTest.Three three;

        String name;

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof CircularReferenceTest.Four))
                return false;

            CircularReferenceTest.Four four = ((CircularReferenceTest.Four) (o));
            return EqualsBuilder.reflectionEquals(this, four, new String[]{ "three" });
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, new String[]{ "three" });
        }
    }

    @Test
    public void testCircularInstances() {
        CircularReferenceTest.Three three = new CircularReferenceTest.Three();
        CircularReferenceTest.Four four = new CircularReferenceTest.Four();
        three.name = "3";
        four.name = "4";
        three.four = four;
        four.three = three;
        CircularReferenceTest.Three unwrap = Parcels.unwrap(ParcelsTestUtil.wrap(three));
        Assert.assertEquals(three, unwrap);
        Assert.assertEquals(four, three.four);
    }

    @Parcel
    static class Five {
        CircularReferenceTest.Six a;

        CircularReferenceTest.Six b;

        CircularReferenceTest.Six c;

        String name;

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof CircularReferenceTest.Five))
                return false;

            CircularReferenceTest.Five five = ((CircularReferenceTest.Five) (o));
            return EqualsBuilder.reflectionEquals(this, five, new String[]{ "a", "b", "c" });
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, new String[]{ "a", "b", "c" });
        }
    }

    @Parcel
    static class Six {
        String name;

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof CircularReferenceTest.Six))
                return false;

            CircularReferenceTest.Six six = ((CircularReferenceTest.Six) (o));
            return EqualsBuilder.reflectionEquals(this, six);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }

    @Test
    public void testMultipleReferences() {
        CircularReferenceTest.Five five = new CircularReferenceTest.Five();
        five.name = "5";
        CircularReferenceTest.Six six = new CircularReferenceTest.Six();
        five.a = six;
        five.b = six;
        five.c = six;
        six.name = "6";
        CircularReferenceTest.Five unwrap = Parcels.unwrap(ParcelsTestUtil.wrap(five));
        Assert.assertEquals(five, unwrap);
        Assert.assertEquals(five.a, unwrap.a);
        Assert.assertEquals(five.b, unwrap.b);
        Assert.assertEquals(five.c, unwrap.c);
        Assert.assertEquals(unwrap.a, unwrap.b);
        Assert.assertEquals(unwrap.a, unwrap.c);
    }

    @Parcel
    static class Seven {
        CircularReferenceTest.Eight eight;

        String name;

        @ParcelConstructor
        Seven(CircularReferenceTest.Eight eight, String name) {
            this.eight = eight;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof CircularReferenceTest.Seven))
                return false;

            CircularReferenceTest.Seven seven = ((CircularReferenceTest.Seven) (o));
            return EqualsBuilder.reflectionEquals(this, seven);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }

    @Parcel
    static class Eight {
        CircularReferenceTest.Seven seven;

        String name;

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof CircularReferenceTest.Eight))
                return false;

            CircularReferenceTest.Eight eight = ((CircularReferenceTest.Eight) (o));
            return EqualsBuilder.reflectionEquals(this, eight, new String[]{ "seven" });
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, new String[]{ "seven" });
        }
    }

    @Test
    public void testCircularConstructor() {
        CircularReferenceTest.Eight eight = new CircularReferenceTest.Eight();
        eight.name = "8";
        CircularReferenceTest.Seven seven = new CircularReferenceTest.Seven(eight, "7");
        eight.seven = seven;
        CircularReferenceTest.Eight unwrappedEight = Parcels.unwrap(ParcelsTestUtil.wrap(eight));
        Assert.assertEquals(eight, unwrappedEight);
        Assert.assertEquals(seven, unwrappedEight.seven);
        Assert.assertEquals(eight, unwrappedEight.seven.eight);
        try {
            Parcels.unwrap(ParcelsTestUtil.wrap(seven));
            Assert.assertTrue("Parcels.unwrap did not throw an exception", false);
        } catch (ParcelerRuntimeException e) {
        }
    }

    @Parcel
    static class Nine {
        CircularReferenceTest.Ten ten;

        String name;

        public Nine(CircularReferenceTest.Ten ten, String name) {
            this.ten = ten;
            this.name = name;
        }

        @ParcelFactory
        public static CircularReferenceTest.Nine build(CircularReferenceTest.Ten ten, String name) {
            return new CircularReferenceTest.Nine(ten, name);
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof CircularReferenceTest.Nine))
                return false;

            CircularReferenceTest.Nine nine = ((CircularReferenceTest.Nine) (o));
            return EqualsBuilder.reflectionEquals(this, nine, new String[]{ "ten" });
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, new String[]{ "ten" });
        }
    }

    @Parcel
    static class Ten {
        CircularReferenceTest.Nine nine;

        String name;

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof CircularReferenceTest.Ten))
                return false;

            CircularReferenceTest.Ten ten = ((CircularReferenceTest.Ten) (o));
            return EqualsBuilder.reflectionEquals(this, ten, new String[]{ "nine" });
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, new String[]{ "nine" });
        }
    }

    @Test
    public void testCircularFactory() {
        CircularReferenceTest.Ten ten = new CircularReferenceTest.Ten();
        ten.name = "10";
        CircularReferenceTest.Nine nine = CircularReferenceTest.Nine.build(ten, "9");
        ten.nine = nine;
        CircularReferenceTest.Ten unwrappedTen = Parcels.unwrap(ParcelsTestUtil.wrap(ten));
        Assert.assertEquals(ten, unwrappedTen);
        Assert.assertEquals(ten, unwrappedTen.nine.ten);
        Assert.assertEquals(nine, unwrappedTen.nine);
        try {
            Parcels.unwrap(ParcelsTestUtil.wrap(nine));
            Assert.assertTrue("Parcels.unwrap did not throw an exception", false);
        } catch (ParcelerRuntimeException e) {
        }
    }
}

