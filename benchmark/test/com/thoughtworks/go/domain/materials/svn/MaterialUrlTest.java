/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
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
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.domain.materials.svn;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class MaterialUrlTest {
    @Test
    public void shouldIgnoreTrailingSlash() {
        Assert.assertThat(new MaterialUrl("http://somehost/"), Matchers.is(new MaterialUrl("http://somehost")));
        Assert.assertThat(new MaterialUrl("http://somehost"), Matchers.is(new MaterialUrl("http://somehost/")));
    }

    @Test
    public void shouldDecodeSpecialCharacters() {
        Assert.assertThat(new MaterialUrl("http://somehost/program files"), Matchers.is(new MaterialUrl("http://somehost/program%20files")));
        Assert.assertThat(new MaterialUrl("http://somehost/program%20files"), Matchers.is(new MaterialUrl("http://somehost/program files")));
        Assert.assertThat(new MaterialUrl("http://somehost/sv@n/"), Matchers.is(new MaterialUrl("http://somehost/sv%40n")));
    }

    @Test
    public void shouldDecodeAndRemoveTrailingSlash() {
        Assert.assertThat(new MaterialUrl("http://somehost/program files/"), Matchers.is(new MaterialUrl("http://somehost/program%20files")));
    }

    @Test
    public void shouldIgnoreTheFileProtocol() {
        Assert.assertThat(new MaterialUrl("file:///somefile/Program files/"), Matchers.is(new MaterialUrl("/somefile/Program files/")));
        Assert.assertThat(new MaterialUrl("FilE:///somefile/Program files/"), Matchers.is(new MaterialUrl("/somefile/Program files/")));
    }
}

