/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.builder;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;


/**
 *
 */
public class ReflectionToStringBuilderExcludeTest {
    class TestFixture {
        @SuppressWarnings("unused")
        private final String secretField = ReflectionToStringBuilderExcludeTest.SECRET_VALUE;

        @SuppressWarnings("unused")
        private final String showField = ReflectionToStringBuilderExcludeTest.NOT_SECRET_VALUE;
    }

    private static final String NOT_SECRET_FIELD = "showField";

    private static final String NOT_SECRET_VALUE = "Hello World!";

    private static final String SECRET_FIELD = "secretField";

    private static final String SECRET_VALUE = "secret value";

    @Test
    public void test_toStringExclude() {
        final String toString = ReflectionToStringBuilder.toStringExclude(new ReflectionToStringBuilderExcludeTest.TestFixture(), ReflectionToStringBuilderExcludeTest.SECRET_FIELD);
        this.validateSecretFieldAbsent(toString);
    }

    @Test
    public void test_toStringExcludeArray() {
        final String toString = ReflectionToStringBuilder.toStringExclude(new ReflectionToStringBuilderExcludeTest.TestFixture(), ReflectionToStringBuilderExcludeTest.SECRET_FIELD);
        this.validateSecretFieldAbsent(toString);
    }

    @Test
    public void test_toStringExcludeArrayWithNull() {
        final String toString = ReflectionToStringBuilder.toStringExclude(new ReflectionToStringBuilderExcludeTest.TestFixture(), new String[]{ null });
        this.validateSecretFieldPresent(toString);
    }

    @Test
    public void test_toStringExcludeArrayWithNulls() {
        final String toString = ReflectionToStringBuilder.toStringExclude(new ReflectionToStringBuilderExcludeTest.TestFixture(), null, null);
        this.validateSecretFieldPresent(toString);
    }

    @Test
    public void test_toStringExcludeCollection() {
        final List<String> excludeList = new ArrayList<>();
        excludeList.add(ReflectionToStringBuilderExcludeTest.SECRET_FIELD);
        final String toString = ReflectionToStringBuilder.toStringExclude(new ReflectionToStringBuilderExcludeTest.TestFixture(), excludeList);
        this.validateSecretFieldAbsent(toString);
    }

    @Test
    public void test_toStringExcludeCollectionWithNull() {
        final List<String> excludeList = new ArrayList<>();
        excludeList.add(null);
        final String toString = ReflectionToStringBuilder.toStringExclude(new ReflectionToStringBuilderExcludeTest.TestFixture(), excludeList);
        this.validateSecretFieldPresent(toString);
    }

    @Test
    public void test_toStringExcludeCollectionWithNulls() {
        final List<String> excludeList = new ArrayList<>();
        excludeList.add(null);
        excludeList.add(null);
        final String toString = ReflectionToStringBuilder.toStringExclude(new ReflectionToStringBuilderExcludeTest.TestFixture(), excludeList);
        this.validateSecretFieldPresent(toString);
    }

    @Test
    public void test_toStringExcludeEmptyArray() {
        final String toString = ReflectionToStringBuilder.toStringExclude(new ReflectionToStringBuilderExcludeTest.TestFixture(), ArrayUtils.EMPTY_STRING_ARRAY);
        this.validateSecretFieldPresent(toString);
    }

    @Test
    public void test_toStringExcludeEmptyCollection() {
        final String toString = ReflectionToStringBuilder.toStringExclude(new ReflectionToStringBuilderExcludeTest.TestFixture(), new ArrayList<>());
        this.validateSecretFieldPresent(toString);
    }

    @Test
    public void test_toStringExcludeNullArray() {
        final String toString = ReflectionToStringBuilder.toStringExclude(new ReflectionToStringBuilderExcludeTest.TestFixture(), ((String[]) (null)));
        this.validateSecretFieldPresent(toString);
    }

    @Test
    public void test_toStringExcludeNullCollection() {
        final String toString = ReflectionToStringBuilder.toStringExclude(new ReflectionToStringBuilderExcludeTest.TestFixture(), ((Collection<String>) (null)));
        this.validateSecretFieldPresent(toString);
    }
}

