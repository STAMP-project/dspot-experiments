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
package com.thoughtworks.go.config;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public abstract class IdFileServiceTestBase {
    protected IdFileService idFileService;

    protected String DATA = "data";

    @Test
    public void shouldLoadDataFromFile() throws Exception {
        Assert.assertThat(idFileService.load(), Matchers.is(DATA));
    }

    @Test
    public void shouldStoreDataToFile() throws Exception {
        idFileService.store("some-id");
        Assert.assertThat(idFileService.load(), Matchers.is("some-id"));
    }

    @Test
    public void shouldCheckIfDataPresent() throws Exception {
        Assert.assertTrue(idFileService.dataPresent());
        idFileService.delete();
        Assert.assertFalse(idFileService.dataPresent());
        idFileService.store("");
        Assert.assertFalse(idFileService.dataPresent());
    }

    @Test
    public void shouldDeleteFile() throws Exception {
        Assert.assertTrue(idFileService.file.exists());
        idFileService.delete();
        Assert.assertFalse(idFileService.file.exists());
    }
}

