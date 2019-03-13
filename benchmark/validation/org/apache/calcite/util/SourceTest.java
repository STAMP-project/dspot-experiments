/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.util;


import java.io.File;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link Source}.
 */
public class SourceTest {
    private static final String ROOT_PREFIX = SourceTest.getRootPrefix();

    @Test
    public void testAppendWithSpaces() {
        String fooRelative = "fo o+";
        String fooAbsolute = (SourceTest.ROOT_PREFIX) + "fo o+";
        String barRelative = "b ar+";
        String barAbsolute = (SourceTest.ROOT_PREFIX) + "b ar+";
        assertAppend(Sources.file(null, fooRelative), Sources.file(null, barRelative), "fo o+/b ar+");
        assertAppend(Sources.file(null, fooRelative), Sources.file(null, barAbsolute), barAbsolute);
        assertAppend(Sources.file(null, fooAbsolute), Sources.file(null, barRelative), ((SourceTest.ROOT_PREFIX) + "fo o+/b ar+"));
        assertAppend(Sources.file(null, fooAbsolute), Sources.file(null, barAbsolute), barAbsolute);
        String urlFooRelative = "file:fo%20o+";
        String urlFooAbsolute = ("file:" + (SourceTest.ROOT_PREFIX)) + "fo%20o+";
        String urlBarRelative = "file:b%20ar+";
        String urlBarAbsolute = ("file:" + (SourceTest.ROOT_PREFIX)) + "b%20ar+";
        assertAppend(Sources.url(urlFooRelative), Sources.url(urlBarRelative), "fo o+/b ar+");
        assertAppend(Sources.url(urlFooRelative), Sources.url(urlBarAbsolute), barAbsolute);
        assertAppend(Sources.url(urlFooAbsolute), Sources.url(urlBarRelative), ((SourceTest.ROOT_PREFIX) + "fo o+/b ar+"));
        assertAppend(Sources.url(urlFooAbsolute), Sources.url(urlBarAbsolute), barAbsolute);
        assertAppend(Sources.file(null, fooRelative), Sources.url(urlBarRelative), "fo o+/b ar+");
        assertAppend(Sources.file(null, fooRelative), Sources.url(urlBarAbsolute), barAbsolute);
        assertAppend(Sources.file(null, fooAbsolute), Sources.url(urlBarRelative), ((SourceTest.ROOT_PREFIX) + "fo o+/b ar+"));
        assertAppend(Sources.file(null, fooAbsolute), Sources.url(urlBarAbsolute), barAbsolute);
        assertAppend(Sources.url(urlFooRelative), Sources.file(null, barRelative), "fo o+/b ar+");
        assertAppend(Sources.url(urlFooRelative), Sources.file(null, barAbsolute), barAbsolute);
        assertAppend(Sources.url(urlFooAbsolute), Sources.file(null, barRelative), ((SourceTest.ROOT_PREFIX) + "fo o+/b ar+"));
        assertAppend(Sources.url(urlFooAbsolute), Sources.file(null, barAbsolute), barAbsolute);
    }

    @Test
    public void testAppendHttp() {
        // I've truly no idea what append of two URLs should be, yet it does something
        assertAppendUrl(Sources.url("http://fo%20o+/ba%20r+"), Sources.file(null, "no idea what I am doing+"), "http://fo%20o+/ba%20r+/no%20idea%20what%20I%20am%20doing+");
        assertAppendUrl(Sources.url("http://fo%20o+"), Sources.file(null, "no idea what I am doing+"), "http://fo%20o+/no%20idea%20what%20I%20am%20doing+");
        assertAppendUrl(Sources.url("http://fo%20o+/ba%20r+"), Sources.url("file:no%20idea%20what%20I%20am%20doing+"), "http://fo%20o+/ba%20r+/no%20idea%20what%20I%20am%20doing+");
        assertAppendUrl(Sources.url("http://fo%20o+"), Sources.url("file:no%20idea%20what%20I%20am%20doing+"), "http://fo%20o+/no%20idea%20what%20I%20am%20doing+");
    }

    @Test
    public void testSpaceInUrl() {
        String url = ("file:" + (SourceTest.ROOT_PREFIX)) + "dir%20name/test%20file.json";
        final Source foo = Sources.url(url);
        Assert.assertEquals((url + " .file().getAbsolutePath()"), new File(((SourceTest.ROOT_PREFIX) + "dir name/test file.json")).getAbsolutePath(), foo.file().getAbsolutePath());
    }

    @Test
    public void testSpaceInRelativeUrl() {
        String url = "file:dir%20name/test%20file.json";
        final Source foo = Sources.url(url);
        Assert.assertEquals((url + " .file().getAbsolutePath()"), "dir name/test file.json", foo.file().getPath().replace('\\', '/'));
    }

    @Test
    public void testRelative() {
        final Source fooBar = Sources.file(null, ((SourceTest.ROOT_PREFIX) + "foo/bar"));
        final Source foo = Sources.file(null, ((SourceTest.ROOT_PREFIX) + "foo"));
        final Source baz = Sources.file(null, ((SourceTest.ROOT_PREFIX) + "baz"));
        final Source bar = fooBar.relative(foo);
        Assert.assertThat(bar.file().toString(), Is.is("bar"));
        Assert.assertThat(fooBar.relative(baz), Is.is(fooBar));
    }
}

/**
 * End SourceTest.java
 */
