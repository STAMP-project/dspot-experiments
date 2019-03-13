/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.util;


import io.undertow.testutils.category.UnitTest;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 *
 * @author Oleksandr Radchykov
 * @author Andre Schaefer
 */
@RunWith(Parameterized.class)
@Category(UnitTest.class)
public class URLUtilsTestCase {
    @Parameterized.Parameter
    public String spaceCode = "%2f";

    @Test
    public void testDecodingWithEncodedAndDecodedSlashAndSlashDecodingDisabled() throws Exception {
        String url = ("http://localhost:3001/by-path/wild%20card/wild%28west%29/wild" + (spaceCode)) + "wolf";
        final String result = URLUtils.decode(url, Charset.defaultCharset().name(), false, new StringBuilder());
        Assert.assertEquals((("http://localhost:3001/by-path/wild card/wild(west)/wild" + (spaceCode)) + "wolf"), result);
    }

    @Test
    public void testDecodingURLMustNotMutateSpaceSymbolsCaseIfSpaceDecodingDisabled() throws Exception {
        final String url = ("http://localhost:3001/wild" + (spaceCode)) + "west";
        final String result = URLUtils.decode(url, Charset.defaultCharset().name(), false, new StringBuilder());
        Assert.assertEquals(url, result);
    }

    @Test
    public void testIsAbsoluteUrlRecognizingAbsolutUrls() {
        Assert.assertTrue(URLUtils.isAbsoluteUrl("https://some.valid.url:8080/path?query=val"));
        Assert.assertTrue(URLUtils.isAbsoluteUrl("http://some.valid.url:8080/path?query=val"));
        Assert.assertTrue(URLUtils.isAbsoluteUrl("http://some.valid.url"));
    }

    @Test
    public void testIsAbsoluteUrlRecognizingAppUrls() {
        Assert.assertTrue(URLUtils.isAbsoluteUrl("com.example.app:/oauth2redirect/example-provider"));
        Assert.assertTrue(URLUtils.isAbsoluteUrl("com.example.app:/oauth2redirect/example-provider?query=val"));
    }

    @Test
    public void testIsAbsoluteUrlRecognizingRelativeUrls() {
        Assert.assertFalse(URLUtils.isAbsoluteUrl("relative"));
        Assert.assertFalse(URLUtils.isAbsoluteUrl("relative/path"));
        Assert.assertFalse(URLUtils.isAbsoluteUrl("relative/path?query=val"));
        Assert.assertFalse(URLUtils.isAbsoluteUrl("/root/relative/path"));
    }

    @Test
    public void testIsAbsoluteUrlRecognizingEmptyOrNullAsRelative() {
        Assert.assertFalse(URLUtils.isAbsoluteUrl(null));
        Assert.assertFalse(URLUtils.isAbsoluteUrl(""));
    }

    @Test
    public void testIsAbsoluteUrlIgnoresSyntaxErrorsAreNotAbsolute() {
        Assert.assertFalse(URLUtils.isAbsoluteUrl(":"));
    }
}

