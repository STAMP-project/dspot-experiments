/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.util;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class FileSizeUtilsTest {
    @Test
    public void shouldConvertBytes() {
        Assert.assertThat(FileSizeUtils.byteCountToDisplaySize(((long) (1023))), Matchers.is("1023 bytes"));
    }

    @Test
    public void shouldConvertBytesToKilo() {
        Assert.assertThat(FileSizeUtils.byteCountToDisplaySize(((long) (1024 + 512))), Matchers.is("1.5 KB"));
    }

    @Test
    public void shouldOnlyKeep() {
        Assert.assertThat(FileSizeUtils.byteCountToDisplaySize(((long) ((1024 + 512) + 256))), Matchers.is("1.8 KB"));
    }

    @Test
    public void shouldConvertBytesToMega() {
        Assert.assertThat(FileSizeUtils.byteCountToDisplaySize(((long) (1024 * 1024))), Matchers.is("1.0 MB"));
    }

    @Test
    public void shouldConvertBytesToMegaForFloat() {
        Assert.assertThat(FileSizeUtils.byteCountToDisplaySize(((long) (((1 * 1024) * 1024) + (512 * 1024)))), Matchers.is("1.5 MB"));
    }

    @Test
    public void shouldConvertBytesToGiga() {
        long twoGiga = (((2L * 1024) * 1024) * 1024) + ((512 * 1024) * 1024);
        Assert.assertThat(FileSizeUtils.byteCountToDisplaySize(twoGiga), Matchers.is("2.5 GB"));
    }

    @Test
    public void shouldConvertBytesToTB() {
        long twoGiga = ((((2L * 1024) * 1024) * 1024) * 1024) + (((512L * 1024) * 1024) * 1024);
        Assert.assertThat(FileSizeUtils.byteCountToDisplaySize(twoGiga), Matchers.is("2.5 TB"));
    }

    @Test
    public void shouldConvertBytesToPB() {
        long twoGiga = (((((2L * 1024) * 1024) * 1024) * 1024) * 1024) + ((((512L * 1024) * 1024) * 1024) * 1024);
        Assert.assertThat(FileSizeUtils.byteCountToDisplaySize(twoGiga), Matchers.is("2.5 PB"));
    }
}

