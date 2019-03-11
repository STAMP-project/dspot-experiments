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
package org.apache.geode.test.junit.rules;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.geode.test.junit.runners.TestRunner;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Result;

import static ShouldRestoreLocaleInAfterWithConsumer.locales;


/**
 * Unit tests for {@link RestoreLocaleRule}.
 */
public class RestoreLocaleRuleTest {
    private static Locale notDefaultLocale;

    @Test
    public void shouldRestoreLocaleInAfter() throws Throwable {
        Locale originalLocale = Locale.getDefault();
        Result result = TestRunner.runTest(RestoreLocaleRuleTest.ShouldRestoreLocaleInAfter.class);
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertThat(Locale.getDefault(), CoreMatchers.is(originalLocale));
        Assert.assertThat(RestoreLocaleRuleTest.ShouldRestoreLocaleInAfter.localeInTest, CoreMatchers.is(RestoreLocaleRuleTest.notDefaultLocale));
    }

    @Test
    public void setInitLocaleShouldRestoreLocaleInAfter() throws Throwable {
        Locale originalLocale = Locale.getDefault();
        Result result = TestRunner.runTest(RestoreLocaleRuleTest.SetInitLocaleShouldRestoreLocaleInAfter.class);
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertThat(Locale.getDefault(), CoreMatchers.is(originalLocale));
        Assert.assertThat(RestoreLocaleRuleTest.SetInitLocaleShouldRestoreLocaleInAfter.localeInTest, CoreMatchers.is(Locale.ENGLISH));
    }

    @Test
    public void shouldRestoreLocaleInAfterWithConsumer() throws Throwable {
        Locale originalLocale = Locale.getDefault();
        Result result = TestRunner.runTest(RestoreLocaleRuleTest.ShouldRestoreLocaleInAfterWithConsumer.class);
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertThat(Locale.getDefault(), CoreMatchers.is(originalLocale));
        Assert.assertThat(RestoreLocaleRuleTest.ShouldRestoreLocaleInAfterWithConsumer.localeInTest, CoreMatchers.is(RestoreLocaleRuleTest.notDefaultLocale));
        Assert.assertThat(RestoreLocaleRuleTest.ShouldRestoreLocaleInAfterWithConsumer.locales.size(), CoreMatchers.is(2));
        Assert.assertThat(RestoreLocaleRuleTest.ShouldRestoreLocaleInAfterWithConsumer.locales, Matchers.contains(originalLocale, originalLocale));
    }

    @Test
    public void setInitLocaleShouldRestoreLocaleInAfterWithConsumer() throws Throwable {
        Locale originalLocale = Locale.getDefault();
        Result result = TestRunner.runTest(RestoreLocaleRuleTest.SetInitLocaleShouldRestoreLocaleInAfterWithConsumer.class);
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertThat(Locale.getDefault(), CoreMatchers.is(originalLocale));
        Assert.assertThat(RestoreLocaleRuleTest.SetInitLocaleShouldRestoreLocaleInAfterWithConsumer.localeInTest, CoreMatchers.is(Locale.CHINESE));
        Assert.assertThat(RestoreLocaleRuleTest.SetInitLocaleShouldRestoreLocaleInAfterWithConsumer.locales.size(), CoreMatchers.is(2));
        Assert.assertThat(RestoreLocaleRuleTest.SetInitLocaleShouldRestoreLocaleInAfterWithConsumer.locales, Matchers.contains(Locale.CHINESE, originalLocale));
    }

    /**
     * Used by test {@link #shouldRestoreLocaleInAfter()}
     */
    public static class ShouldRestoreLocaleInAfter {
        static Locale localeInTest = RestoreLocaleRuleTest.notDefaultLocale;

        @Rule
        public final RestoreLocaleRule restoreLocale = new RestoreLocaleRule();

        @Test
        public void doTest() throws Exception {
            Locale.setDefault(RestoreLocaleRuleTest.ShouldRestoreLocaleInAfter.localeInTest);
            Assert.assertThat(Locale.getDefault(), CoreMatchers.is(RestoreLocaleRuleTest.ShouldRestoreLocaleInAfter.localeInTest));
        }
    }

    /**
     * Used by test {@link #setInitLocaleShouldRestoreLocaleInAfter()}
     */
    public static class SetInitLocaleShouldRestoreLocaleInAfter {
        static Locale localeInTest;

        @Rule
        public final RestoreLocaleRule restoreLocale = new RestoreLocaleRule(Locale.ENGLISH);

        @Test
        public void doTest() throws Exception {
            RestoreLocaleRuleTest.SetInitLocaleShouldRestoreLocaleInAfter.localeInTest = Locale.getDefault();
            Assert.assertThat(Locale.getDefault(), CoreMatchers.is(Locale.ENGLISH));
        }
    }

    /**
     * Used by test {@link #shouldRestoreLocaleInAfterWithConsumer()}
     */
    public static class ShouldRestoreLocaleInAfterWithConsumer {
        static List<Locale> locales = new ArrayList<>();

        static Locale localeInTest = RestoreLocaleRuleTest.notDefaultLocale;

        @Rule
        public final RestoreLocaleRule restoreLocale = new RestoreLocaleRule(( l) -> ShouldRestoreLocaleInAfterWithConsumer.locales.add(l));

        @Test
        public void doTest() throws Exception {
            Locale originalLocale = Locale.getDefault();
            Locale.setDefault(RestoreLocaleRuleTest.ShouldRestoreLocaleInAfterWithConsumer.localeInTest);
            Assert.assertThat(Locale.getDefault(), CoreMatchers.is(RestoreLocaleRuleTest.ShouldRestoreLocaleInAfterWithConsumer.localeInTest));
            Assert.assertThat(RestoreLocaleRuleTest.ShouldRestoreLocaleInAfterWithConsumer.locales.size(), CoreMatchers.is(1));
            Assert.assertThat(RestoreLocaleRuleTest.ShouldRestoreLocaleInAfterWithConsumer.locales, Matchers.contains(originalLocale));
        }
    }

    /**
     * Used by test {@link #setInitLocaleShouldRestoreLocaleInAfterWithConsumer()}
     */
    public static class SetInitLocaleShouldRestoreLocaleInAfterWithConsumer {
        static List<Locale> locales = new ArrayList<>();

        static Locale localeInTest;

        @Rule
        public final RestoreLocaleRule restoreLocale = new RestoreLocaleRule(Locale.CHINESE, ( l) -> SetInitLocaleShouldRestoreLocaleInAfterWithConsumer.locales.add(l));

        @Test
        public void doTest() throws Exception {
            RestoreLocaleRuleTest.SetInitLocaleShouldRestoreLocaleInAfterWithConsumer.localeInTest = Locale.getDefault();
            Assert.assertThat(Locale.getDefault(), CoreMatchers.is(Locale.CHINESE));
            Assert.assertThat(RestoreLocaleRuleTest.SetInitLocaleShouldRestoreLocaleInAfterWithConsumer.locales.size(), CoreMatchers.is(1));
            Assert.assertThat(RestoreLocaleRuleTest.SetInitLocaleShouldRestoreLocaleInAfterWithConsumer.locales, Matchers.contains(Locale.CHINESE));
        }
    }
}

