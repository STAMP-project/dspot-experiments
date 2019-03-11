/**
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.functional_tests;


import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.vo.enumtest.MyBean;
import com.github.dozermapper.core.vo.enumtest.MyBeanPrime;
import com.github.dozermapper.core.vo.enumtest.MyBeanPrimeByte;
import com.github.dozermapper.core.vo.enumtest.MyBeanPrimeInteger;
import com.github.dozermapper.core.vo.enumtest.MyBeanPrimeLong;
import com.github.dozermapper.core.vo.enumtest.MyBeanPrimeShort;
import com.github.dozermapper.core.vo.enumtest.MyBeanPrimeString;
import com.github.dozermapper.core.vo.enumtest.SrcType;
import com.github.dozermapper.core.vo.enumtest.SrcTypeWithOverride;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Functional test for enum mapping as described
 * <a href=https://github.com/DozerMapper/dozer/blob/master/docs/asciidoc/documentation/enum.adoc>here</a>.
 * <p>
 * In this functional test, Enum is categorized into two types: Based Enum and Overridden Enum.
 * Based Enum refers to those enum without any overridden methods, including constructors.  A
 * typical Based Enum would look as below.
 * <code>
 * public enum SrcType {
 * FOO , BAR;
 * }
 * </code>
 * On the contrary, Overridden Enum refers to those enum with overridden methods, including
 * constructors. A typical Overridden Enum would look as below.
 * <code>
 * public enum SrcTypeWithOverride {
 * FOO { public String display() { return "Src.FOO"; } },
 * BAR { public String display() { return "Src.BAR"; } };
 * public abstract String display();
 * }
 * </code>
 */
public class EnumMappingTest extends AbstractFunctionalTest {
    private static Mapper enumMapping;

    private static Mapper enumMappingOverriedEnumToBasedEnum;

    @Rule
    public ExpectedException canByteMapsToEnumOutOfOrdinalRangeExpectedException = ExpectedException.none();

    @Rule
    public ExpectedException canShortMapsToEnumOutOfOrdinalRangeExpectedException = ExpectedException.none();

    @Rule
    public ExpectedException canIntegerMapsToEnumOutOfOrdinalRangeExpectedException = ExpectedException.none();

    @Rule
    public ExpectedException canLongMapsToEnumOutOfOrdinalRangeExpectedException = ExpectedException.none();

    @Rule
    public ExpectedException canStringMapsToEnumNonexistEnumValueExpectedException = ExpectedException.none();

    @Test
    public void canStringInMapMapsToEnum() {
        Map src = newInstance(HashMap.class);
        src.put("destType", "FOO");
        MyBeanPrime dest = EnumMappingTest.enumMappingOverriedEnumToBasedEnum.map(src, MyBeanPrime.class);
        Assert.assertEquals("FOO", dest.getDestType().toString());
    }

    @Test
    public void canOverriddenEnumMapsToBasedEnum() {
        MyBean src = newInstance(MyBean.class);
        src.setSrcTypeWithOverride(SrcTypeWithOverride.FOO);
        MyBeanPrime dest = EnumMappingTest.enumMappingOverriedEnumToBasedEnum.map(src, MyBeanPrime.class);
        Assert.assertEquals(src.getSrcTypeWithOverride().toString(), dest.getDestType().toString());
    }

    @Test
    public void canBasedEnumMapsToOverriddenEnum() {
        MyBean src = newInstance(MyBean.class);
        src.setSrcType(SrcType.FOO);
        MyBeanPrime dest = EnumMappingTest.enumMappingOverriedEnumToBasedEnum.map(src, MyBeanPrime.class);
        Assert.assertEquals(src.getSrcType().toString(), dest.getDestTypeWithOverride().toString());
    }

    @Test
    public void canBasedEnumMapsToBasedEnum() {
        MyBean src = newInstance(MyBean.class);
        src.setSrcType(SrcType.FOO);
        MyBeanPrime dest = EnumMappingTest.enumMapping.map(src, MyBeanPrime.class);
        Assert.assertEquals(src.getSrcType().toString(), dest.getDestType().toString());
    }

    @Test
    public void canOverriddenEnumMapsToOverriddenEnum() {
        MyBean src = newInstance(MyBean.class);
        src.setSrcTypeWithOverride(SrcTypeWithOverride.FOO);
        MyBeanPrime dest = EnumMappingTest.enumMapping.map(src, MyBeanPrime.class);
        Assert.assertEquals(src.getSrcTypeWithOverride().toString(), dest.getDestTypeWithOverride().toString());
    }

    @Test
    public void canEnumMapsToItself() {
        MyBean src = newInstance(MyBean.class);
        src.setSrcType(SrcType.FOO);
        MyBean dest = EnumMappingTest.enumMapping.map(src, MyBean.class);
        Assert.assertEquals(src.getSrcType(), dest.getSrcType());
        Assert.assertEquals(src.getSrcTypeWithOverride(), dest.getSrcTypeWithOverride());
    }

    @Test
    public void canEnumMapsToString() {
        MyBean src = new MyBean();
        src.setSrcType(SrcType.FOO);
        MyBeanPrimeString dest = EnumMappingTest.enumMapping.map(src, MyBeanPrimeString.class);
        Assert.assertEquals("FOO", dest.getDestType());
    }

    @Test
    public void canStringMapsToEnum() {
        MyBeanPrimeString src = new MyBeanPrimeString();
        src.setDestType("FOO");
        src.setDestTypeWithOverride("BAR");
        MyBean dest = EnumMappingTest.enumMapping.map(src, MyBean.class);
        Assert.assertEquals(SrcType.FOO, dest.getSrcType());
        Assert.assertEquals(SrcTypeWithOverride.BAR, dest.getSrcTypeWithOverride());
    }

    @Test
    public void canByteMapsToEnum() {
        MyBeanPrimeByte src = new MyBeanPrimeByte();
        src.setFirst(((byte) (0)));
        src.setSecond(((byte) (1)));
        MyBean dest = EnumMappingTest.enumMapping.map(src, MyBean.class);
        Assert.assertEquals(SrcType.FOO, dest.getSrcType());
        Assert.assertEquals(SrcTypeWithOverride.BAR, dest.getSrcTypeWithOverride());
    }

    @Test
    public void canShortMapsToEnum() {
        MyBeanPrimeShort src = new MyBeanPrimeShort();
        src.setFirst(((short) (0)));
        src.setSecond(((short) (1)));
        MyBean dest = EnumMappingTest.enumMapping.map(src, MyBean.class);
        Assert.assertEquals(SrcType.FOO, dest.getSrcType());
        Assert.assertEquals(SrcTypeWithOverride.BAR, dest.getSrcTypeWithOverride());
    }

    @Test
    public void canIntegerMapsToEnum() {
        MyBeanPrimeInteger src = new MyBeanPrimeInteger();
        src.setFirst(0);
        src.setSecond(1);
        MyBean dest = EnumMappingTest.enumMapping.map(src, MyBean.class);
        Assert.assertEquals(SrcType.FOO, dest.getSrcType());
        Assert.assertEquals(SrcTypeWithOverride.BAR, dest.getSrcTypeWithOverride());
    }

    @Test
    public void canLongMapsToEnum() {
        MyBeanPrimeLong src = new MyBeanPrimeLong();
        src.setFirst(0L);
        src.setSecond(1L);
        MyBean dest = EnumMappingTest.enumMapping.map(src, MyBean.class);
        Assert.assertEquals(SrcType.FOO, dest.getSrcType());
        Assert.assertEquals(SrcTypeWithOverride.BAR, dest.getSrcTypeWithOverride());
    }

    @Test
    public void canEnumMapsToByte() {
        MyBean src = new MyBean();
        src.setSrcType(SrcType.FOO);
        src.setSrcTypeWithOverride(SrcTypeWithOverride.BAR);
        MyBeanPrimeByte dest = EnumMappingTest.enumMapping.map(src, MyBeanPrimeByte.class);
        Assert.assertEquals(0, dest.getFirst());
        Assert.assertEquals(Byte.valueOf(((byte) (1))), dest.getSecond());
    }

    @Test
    public void canEnumMapsToShort() {
        MyBean src = new MyBean();
        src.setSrcType(SrcType.FOO);
        src.setSrcTypeWithOverride(SrcTypeWithOverride.BAR);
        MyBeanPrimeShort dest = EnumMappingTest.enumMapping.map(src, MyBeanPrimeShort.class);
        Assert.assertEquals(0, dest.getFirst());
        Assert.assertEquals(Short.valueOf(((short) (1))), dest.getSecond());
    }

    @Test
    public void canEnumMapsToInteger() {
        MyBean src = new MyBean();
        src.setSrcType(SrcType.FOO);
        src.setSrcTypeWithOverride(SrcTypeWithOverride.BAR);
        MyBeanPrimeInteger dest = EnumMappingTest.enumMapping.map(src, MyBeanPrimeInteger.class);
        Assert.assertEquals(0, dest.getFirst());
        Assert.assertEquals(Integer.valueOf(1), dest.getSecond());
    }

    @Test
    public void canEnumMapsToLong() {
        MyBean src = new MyBean();
        src.setSrcType(SrcType.FOO);
        src.setSrcTypeWithOverride(SrcTypeWithOverride.BAR);
        MyBeanPrimeLong dest = EnumMappingTest.enumMapping.map(src, MyBeanPrimeLong.class);
        Assert.assertEquals(0, dest.getFirst());
        Assert.assertEquals(Long.valueOf(1L), dest.getSecond());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void canBasedEnumMapsToMap() {
        MyBean src = newInstance(MyBean.class);
        src.setSrcType(SrcType.FOO);
        Map<String, ?> mappedBean = EnumMappingTest.enumMapping.map(src, Map.class);
        Assert.assertEquals(SrcType.FOO, mappedBean.get("srcType"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void canOverriddenEnumMapsToMap() {
        MyBean src = newInstance(MyBean.class);
        src.setSrcTypeWithOverride(SrcTypeWithOverride.FOO);
        Map<String, ?> mappedBean = EnumMappingTest.enumMapping.map(src, Map.class);
        Assert.assertEquals(SrcTypeWithOverride.FOO, mappedBean.get("srcTypeWithOverride"));
    }

    @Test
    public void canByteMapsToEnumOutOfOrdinalRange() {
        canByteMapsToEnumOutOfOrdinalRangeExpectedException.expect(MappingException.class);
        canByteMapsToEnumOutOfOrdinalRangeExpectedException.expectMessage(("Cannot convert [3] to enum of type " + "class com.github.dozermapper.core.vo.enumtest.SrcTypeWithOverride"));
        canByteMapsToEnumOutOfOrdinalRangeExpectedException.expectCause(IsInstanceOf.instanceOf(IndexOutOfBoundsException.class));
        MyBeanPrimeByte src = new MyBeanPrimeByte();
        src.setFirst(((byte) (0)));
        src.setSecond(((byte) (3)));
        EnumMappingTest.enumMapping.map(src, MyBean.class);
    }

    @Test
    public void canShortMapsToEnumOutOfOrdinalRange() {
        canShortMapsToEnumOutOfOrdinalRangeExpectedException.expect(MappingException.class);
        canShortMapsToEnumOutOfOrdinalRangeExpectedException.expectMessage(("Cannot convert [3] to enum of type " + "class com.github.dozermapper.core.vo.enumtest.SrcTypeWithOverride"));
        canShortMapsToEnumOutOfOrdinalRangeExpectedException.expectCause(IsInstanceOf.instanceOf(IndexOutOfBoundsException.class));
        MyBeanPrimeShort src = new MyBeanPrimeShort();
        src.setFirst(((short) (0)));
        src.setSecond(((short) (3)));
        EnumMappingTest.enumMapping.map(src, MyBean.class);
    }

    @Test
    public void canIntegerMapsToEnumOutOfOrdinalRange() {
        canIntegerMapsToEnumOutOfOrdinalRangeExpectedException.expect(MappingException.class);
        canIntegerMapsToEnumOutOfOrdinalRangeExpectedException.expectMessage(("Cannot convert [3] to enum of type " + "class com.github.dozermapper.core.vo.enumtest.SrcTypeWithOverride"));
        canIntegerMapsToEnumOutOfOrdinalRangeExpectedException.expectCause(IsInstanceOf.instanceOf(IndexOutOfBoundsException.class));
        MyBeanPrimeInteger src = new MyBeanPrimeInteger();
        src.setFirst(0);
        src.setSecond(3);
        EnumMappingTest.enumMapping.map(src, MyBean.class);
    }

    @Test
    public void canLongMapsToEnumOutOfOrdinalRange() {
        canLongMapsToEnumOutOfOrdinalRangeExpectedException.expect(MappingException.class);
        canLongMapsToEnumOutOfOrdinalRangeExpectedException.expectMessage(("Cannot convert [3] to enum of type " + "class com.github.dozermapper.core.vo.enumtest.SrcTypeWithOverride"));
        canLongMapsToEnumOutOfOrdinalRangeExpectedException.expectCause(IsInstanceOf.instanceOf(IndexOutOfBoundsException.class));
        MyBeanPrimeLong src = new MyBeanPrimeLong();
        src.setFirst(0L);
        src.setSecond(3L);
        EnumMappingTest.enumMapping.map(src, MyBean.class);
    }

    @Test
    public void canStringMapsToEnumNonexistEnumValue() {
        canStringMapsToEnumNonexistEnumValueExpectedException.expect(MappingException.class);
        canStringMapsToEnumNonexistEnumValueExpectedException.expectMessage("Cannot convert [BAZ] to enum of type class com.github.dozermapper.core.vo.enumtest.SrcType");
        canStringMapsToEnumNonexistEnumValueExpectedException.expectCause(IsInstanceOf.instanceOf(IllegalArgumentException.class));
        MyBeanPrimeString src = new MyBeanPrimeString();
        src.setDestType("BAZ");
        EnumMappingTest.enumMapping.map(src, MyBean.class);
    }
}

