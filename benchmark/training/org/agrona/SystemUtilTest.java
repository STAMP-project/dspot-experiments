/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.agrona;


import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class SystemUtilTest {
    @Test
    public void shouldParseSizesWithSuffix() {
        Assert.assertThat(SystemUtil.parseSize("", "1"), Is.is(1L));
        Assert.assertThat(SystemUtil.parseSize("", "1k"), Is.is(1024L));
        Assert.assertThat(SystemUtil.parseSize("", "1K"), Is.is(1024L));
        Assert.assertThat(SystemUtil.parseSize("", "1m"), Is.is((1024L * 1024)));
        Assert.assertThat(SystemUtil.parseSize("", "1M"), Is.is((1024L * 1024)));
        Assert.assertThat(SystemUtil.parseSize("", "1g"), Is.is(((1024L * 1024) * 1024)));
        Assert.assertThat(SystemUtil.parseSize("", "1G"), Is.is(((1024L * 1024) * 1024)));
    }

    @Test
    public void shouldParseTimesWithSuffix() {
        Assert.assertThat(SystemUtil.parseDuration("", "1"), Is.is(1L));
        Assert.assertThat(SystemUtil.parseDuration("", "1ns"), Is.is(1L));
        Assert.assertThat(SystemUtil.parseDuration("", "1NS"), Is.is(1L));
        Assert.assertThat(SystemUtil.parseDuration("", "1us"), Is.is(1000L));
        Assert.assertThat(SystemUtil.parseDuration("", "1US"), Is.is(1000L));
        Assert.assertThat(SystemUtil.parseDuration("", "1ms"), Is.is((1000L * 1000)));
        Assert.assertThat(SystemUtil.parseDuration("", "1MS"), Is.is((1000L * 1000)));
        Assert.assertThat(SystemUtil.parseDuration("", "1s"), Is.is(((1000L * 1000) * 1000)));
        Assert.assertThat(SystemUtil.parseDuration("", "1S"), Is.is(((1000L * 1000) * 1000)));
        Assert.assertThat(SystemUtil.parseDuration("", "12s"), Is.is((((12L * 1000) * 1000) * 1000)));
    }

    @Test(expected = NumberFormatException.class)
    public void shouldThrowWhenParseTimeHasBadSuffix() {
        SystemUtil.parseDuration("", "1g");
    }

    @Test(expected = NumberFormatException.class)
    public void shouldThrowWhenParseTimeHasBadTwoLetterSuffix() {
        SystemUtil.parseDuration("", "1zs");
    }

    @Test(expected = NumberFormatException.class)
    public void shouldThrowWhenParseSizeOverflows() {
        SystemUtil.parseSize("", (8589934592L + "g"));
    }

    @Test
    public void shouldDoNothingToSystemPropsWhenLoadingFileWhichDoesNotExist() {
        final int originalSystemPropSize = System.getProperties().size();
        SystemUtil.loadPropertiesFile("$unknown-file$");
        Assert.assertThat(originalSystemPropSize, Is.is(System.getProperties().size()));
    }

    @Test
    public void shouldMergeMultiplePropFilesTogether() {
        Assert.assertThat(System.getProperty("TestFileA.foo"), Matchers.isEmptyOrNullString());
        Assert.assertThat(System.getProperty("TestFileB.foo"), Matchers.isEmptyOrNullString());
        SystemUtil.loadPropertiesFiles(new String[]{ "TestFileA.properties", "TestFileB.properties" });
        Assert.assertThat(System.getProperty("TestFileA.foo"), Is.is("AAA"));
        Assert.assertThat(System.getProperty("TestFileB.foo"), Is.is("BBB"));
    }

    @Test
    public void shouldOverrideSystemPropertiesWithConfigFromPropFile() {
        System.setProperty("TestFileA.foo", "ToBeOverridden");
        Assert.assertThat(System.getProperty("TestFileA.foo"), Is.is("ToBeOverridden"));
        SystemUtil.loadPropertiesFile("TestFileA.properties");
        Assert.assertThat(System.getProperty("TestFileA.foo"), Is.is("AAA"));
        System.clearProperty("TestFileA.foo");
    }
}

