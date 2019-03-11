/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.cache.query.internal;


import LocalRegion.NonTXEntry;
import RestrictedMethodInvocationAuthorizer.DEFAULT_ACCEPTLIST;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.geode.cache.query.internal.index.DummyQRegion;
import org.apache.geode.internal.cache.EntrySnapshot;
import org.apache.geode.internal.cache.PartitionedRegion;
import org.apache.geode.test.junit.categories.SecurityTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SecurityTest.class })
public class RestrictedMethodInvocationAuthorizerTest {
    RestrictedMethodInvocationAuthorizer methodInvocationAuthorizer = new RestrictedMethodInvocationAuthorizer(null);

    @Test
    public void getClassShouldFail() throws Exception {
        Method method = Integer.class.getMethod("getClass");
        Assert.assertFalse(methodInvocationAuthorizer.isAcceptlisted(method));
    }

    @Test
    public void toStringOnAnyObject() throws Exception {
        Method stringMethod = Integer.class.getMethod("toString");
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void equalsOnAnyObject() throws Exception {
        Method equalsMethod = Integer.class.getMethod("equals", Object.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(equalsMethod));
    }

    @Test
    public void booleanMethodsAreAcceptListed() throws Exception {
        Method booleanValue = Boolean.class.getMethod("booleanValue");
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(booleanValue));
    }

    @Test
    public void toCharAtOnStringObject() throws Exception {
        Method stringMethod = String.class.getMethod("charAt", int.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void codePointAtStringObject() throws Exception {
        Method stringMethod = String.class.getMethod("codePointAt", int.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void codePointBeforeStringObject() throws Exception {
        Method stringMethod = String.class.getMethod("codePointBefore", int.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void codePointCountStringObject() throws Exception {
        Method stringMethod = String.class.getMethod("codePointCount", int.class, int.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void compareToStringObject() throws Exception {
        Method stringMethod = String.class.getMethod("compareTo", String.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void compareToIgnoreCaseStringObject() throws Exception {
        Method stringMethod = String.class.getMethod("compareToIgnoreCase", String.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void concatStringObject() throws Exception {
        Method stringMethod = String.class.getMethod("compareTo", String.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void containsStringObject() throws Exception {
        Method stringMethod = String.class.getMethod("contains", CharSequence.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void contentEqualsStringObject() throws Exception {
        Method stringMethod = String.class.getMethod("contentEquals", CharSequence.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void contentEqualsWithStringBufferStringObject() throws Exception {
        Method stringMethod = String.class.getMethod("contentEquals", StringBuffer.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void endsWithOnStringObject() throws Exception {
        Method stringMethod = String.class.getMethod("endsWith", String.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void equalsIgnoreCase() throws Exception {
        Method stringMethod = String.class.getMethod("equalsIgnoreCase", String.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void getBytesOnString() throws Exception {
        Method stringMethod = String.class.getMethod("getBytes");
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void getBytesWithCharsetOnString() throws Exception {
        Method stringMethod = String.class.getMethod("getBytes", Charset.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void hashCodeOnStringObject() throws Exception {
        Method stringMethod = String.class.getMethod("hashCode");
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void indexOfOnStringObject() throws Exception {
        Method stringMethod = String.class.getMethod("indexOf", int.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void indexOfWithStringOnStringObject() throws Exception {
        Method stringMethod = String.class.getMethod("indexOf", String.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void indexOfWithStringAndIntOnStringObject() throws Exception {
        Method stringMethod = String.class.getMethod("indexOf", String.class, int.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void internOnStringObject() throws Exception {
        Method stringMethod = String.class.getMethod("intern");
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void isEmpty() throws Exception {
        Method stringMethod = String.class.getMethod("isEmpty");
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void lastIndexOfWithIntOnString() throws Exception {
        Method stringMethod = String.class.getMethod("lastIndexOf", int.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void lastIndexOfWithIntAndFronIndexOnString() throws Exception {
        Method stringMethod = String.class.getMethod("lastIndexOf", int.class, int.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void lastIndexOfWithStringOnString() throws Exception {
        Method stringMethod = String.class.getMethod("lastIndexOf", String.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void lastIndexOfWithStringAndFromIndexOnString() throws Exception {
        Method stringMethod = String.class.getMethod("lastIndexOf", String.class, int.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void lengthOnString() throws Exception {
        Method stringMethod = String.class.getMethod("length");
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void matchesOnString() throws Exception {
        Method stringMethod = String.class.getMethod("matches", String.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void offsetByCodePointsOnString() throws Exception {
        Method stringMethod = String.class.getMethod("offsetByCodePoints", int.class, int.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void regionMatchesWith5ParamsOnString() throws Exception {
        Method stringMethod = String.class.getMethod("regionMatches", boolean.class, int.class, String.class, int.class, int.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void regionMatchesWith4ParamsOnString() throws Exception {
        Method stringMethod = String.class.getMethod("regionMatches", int.class, String.class, int.class, int.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void replaceOnString() throws Exception {
        Method stringMethod = String.class.getMethod("replace", char.class, char.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void replaceWithCharSequenceOnString() throws Exception {
        Method stringMethod = String.class.getMethod("replace", CharSequence.class, CharSequence.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void replaceAllOnString() throws Exception {
        Method stringMethod = String.class.getMethod("replaceAll", String.class, String.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void replaceFirstOnString() throws Exception {
        Method stringMethod = String.class.getMethod("replaceFirst", String.class, String.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void splitOnString() throws Exception {
        Method stringMethod = String.class.getMethod("split", String.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void splitWithLimitOnString() throws Exception {
        Method stringMethod = String.class.getMethod("split", String.class, int.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void startsOnString() throws Exception {
        Method stringMethod = String.class.getMethod("startsWith", String.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void startsWithOffsetOnString() throws Exception {
        Method stringMethod = String.class.getMethod("startsWith", String.class, int.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void substringOnString() throws Exception {
        Method stringMethod = String.class.getMethod("substring", int.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void substringWithEndIndexOnString() throws Exception {
        Method stringMethod = String.class.getMethod("substring", int.class, int.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void toCharArrayOnString() throws Exception {
        Method stringMethod = String.class.getMethod("toCharArray");
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void toLowerCaseOnStringObject() throws Exception {
        Method stringMethod = String.class.getMethod("toLowerCase");
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void toUpperCaseOnStringObject() throws Exception {
        Method stringMethod = String.class.getMethod("toUpperCase");
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void trimOnString() throws Exception {
        Method stringMethod = String.class.getMethod("trim");
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(stringMethod));
    }

    @Test
    public void utilDateAfterMethodIsAcceptListed() throws Exception {
        Method method = Date.class.getMethod("after", Date.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(method));
    }

    @Test
    public void sqlDateAfterMethodIsAcceptListed() throws Exception {
        Method method = java.sql.Date.class.getMethod("after", Date.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(method));
    }

    @Test
    public void utilDateBeforeMethodIsAcceptListed() throws Exception {
        Method method = Date.class.getMethod("before", Date.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(method));
    }

    @Test
    public void sqlDateBeforeMethodIsAcceptListed() throws Exception {
        Method method = java.sql.Date.class.getMethod("before", Date.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(method));
    }

    @Test
    public void timestampAfterMethodIsAcceptListed() throws Exception {
        Method method = Timestamp.class.getMethod("after", Timestamp.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(method));
    }

    @Test
    public void sqlTimestampBeforeMethodIsAcceptListed() throws Exception {
        Method method = Timestamp.class.getMethod("before", Timestamp.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(method));
    }

    @Test
    public void sqlTimestampGetNanosIsAcceptListed() throws Exception {
        Method method = Timestamp.class.getMethod("getNanos");
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(method));
    }

    @Test
    public void sqlTimestampGetTimeIsAcceptListed() throws Exception {
        Method method = Timestamp.class.getMethod("getTime");
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(method));
    }

    @Test
    public void getKeyForMapEntryIsAcceptListed() throws Exception {
        Method getKeyMethod = Map.Entry.class.getMethod("getKey");
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(getKeyMethod));
    }

    @Test
    public void getValueForMapEntryIsAcceptListed() throws Exception {
        Method getValueMethod = Map.Entry.class.getMethod("getValue");
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(getValueMethod));
    }

    @Test
    public void getKeyForMapEntrySnapShotIsAcceptListed() throws Exception {
        Method getKeyMethod = EntrySnapshot.class.getMethod("getKey");
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(getKeyMethod));
    }

    @Test
    public void getValueForMapEntrySnapShotIsAcceptListed() throws Exception {
        Method getValueMethod = EntrySnapshot.class.getMethod("getValue");
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(getValueMethod));
    }

    @Test
    public void getKeyForNonTXEntryIsAcceptListed() throws Exception {
        Method getKeyMethod = NonTXEntry.class.getMethod("getKey");
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(getKeyMethod));
    }

    @Test
    public void getValueForNonTXEntryIsAcceptListed() throws Exception {
        Method getValueMethod = NonTXEntry.class.getMethod("getValue");
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(getValueMethod));
    }

    @Test
    public void mapMethodsForQRegionAreAcceptListed() throws Exception {
        testMapMethods(QRegion.class);
    }

    @Test
    public void mapMethodsForDummyQRegionAreAcceptListed() throws Exception {
        testMapMethods(DummyQRegion.class);
    }

    @Test
    public void mapMethodsForPartitionedRegionAreAcceptListed() throws Exception {
        Class<PartitionedRegion> clazz = PartitionedRegion.class;
        Method get = clazz.getMethod("get", Object.class);
        Method entrySet = clazz.getMethod("entrySet");
        Method keySet = clazz.getMethod("keySet");
        Method values = clazz.getMethod("values");
        Method containsKey = clazz.getMethod("containsKey", Object.class);
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(get));
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(entrySet));
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(keySet));
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(values));
        Assert.assertTrue(methodInvocationAuthorizer.isAcceptlisted(containsKey));
    }

    @Test
    public void numberMethodsForByteAreAcceptListed() throws Exception {
        testNumberMethods(Byte.class);
    }

    @Test
    public void numberMethodsForDoubleAreAcceptListed() throws Exception {
        testNumberMethods(Double.class);
    }

    @Test
    public void numberMethodsForFloatAreAcceptListed() throws Exception {
        testNumberMethods(Float.class);
    }

    @Test
    public void numberMethodsForIntegerAreAcceptListed() throws Exception {
        testNumberMethods(Integer.class);
    }

    @Test
    public void numberMethodsForShortAreAcceptListed() throws Exception {
        testNumberMethods(Short.class);
    }

    @Test
    public void numberMethodsForBigDecimalAreAcceptListed() throws Exception {
        testNumberMethods(BigDecimal.class);
    }

    @Test
    public void numberMethodsForNumberAreAcceptListed() throws Exception {
        testNumberMethods(BigInteger.class);
    }

    @Test
    public void numberMethodsForAtomicIntegerAreAcceptListed() throws Exception {
        testNumberMethods(AtomicInteger.class);
    }

    @Test
    public void numberMethodsForAtomicLongAreAcceptListed() throws Exception {
        testNumberMethods(AtomicLong.class);
    }

    @Test
    public void verifyAuthorizersUseDefaultAcceptList() {
        RestrictedMethodInvocationAuthorizer authorizer1 = new RestrictedMethodInvocationAuthorizer(null);
        RestrictedMethodInvocationAuthorizer authorizer2 = new RestrictedMethodInvocationAuthorizer(null);
        assertThat(authorizer1.getAcceptList()).isSameAs(authorizer2.getAcceptList());
        assertThat(authorizer1.getAcceptList()).isSameAs(DEFAULT_ACCEPTLIST);
        assertThat(authorizer2.getAcceptList()).isSameAs(DEFAULT_ACCEPTLIST);
    }
}

