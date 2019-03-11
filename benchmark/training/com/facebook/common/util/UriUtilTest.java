/**
 * Copyright (c) 2017-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.common.util;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;


/**
 * Unit test for {@link UriUtilTest}.
 */
@RunWith(ParameterizedRobolectricTestRunner.class)
public class UriUtilTest {
    private static final String FB_COM = "www.facebook.com";

    private static final List<String> PATHS_GIVEN = Arrays.asList("a", "b", "c");

    private static final List<String> KEYS_GIVEN = Arrays.asList("key1", "key2", "key3");

    private static final List<String> VALS_GIVEN = Arrays.asList("val1", "val2", "val3");

    private static final List<String> EMPTY_LIST = Collections.<String>emptyList();

    private static final List<String> NO_PATHS = UriUtilTest.EMPTY_LIST;

    private static final List<String> NO_KEYS = UriUtilTest.EMPTY_LIST;

    private static final List<String> NO_VALS = UriUtilTest.EMPTY_LIST;

    private String scheme;

    public UriUtilTest(String scheme) {
        this.scheme = scheme;
    }

    @Test
    public void testWithParams() {
        assertConversionFromUriToUrl(UriUtilTest.FB_COM, UriUtilTest.PATHS_GIVEN, UriUtilTest.KEYS_GIVEN, UriUtilTest.VALS_GIVEN);
        assertConversionFromUriToUrl(UriUtilTest.FB_COM, UriUtilTest.NO_PATHS, UriUtilTest.KEYS_GIVEN, UriUtilTest.VALS_GIVEN);
    }

    @Test
    public void testWithoutParams() {
        assertConversionFromUriToUrl(UriUtilTest.FB_COM, UriUtilTest.PATHS_GIVEN, UriUtilTest.NO_KEYS, UriUtilTest.NO_VALS);
        assertConversionFromUriToUrl(UriUtilTest.FB_COM, UriUtilTest.NO_PATHS, UriUtilTest.NO_KEYS, UriUtilTest.NO_VALS);
    }

    @Test
    public void testNull() {
        Assert.assertNull(UriUtil.uriToUrl(null));
    }

    @Test
    public void testBadHostname() {
        assertConversionFromUriToUrl("www", UriUtilTest.NO_PATHS, UriUtilTest.NO_KEYS, UriUtilTest.NO_VALS);
        assertConversionFromUriToUrl(".www", UriUtilTest.NO_PATHS, UriUtilTest.NO_KEYS, UriUtilTest.NO_VALS);
        assertConversionFromUriToUrl("ww.w", UriUtilTest.NO_PATHS, UriUtilTest.NO_KEYS, UriUtilTest.NO_VALS);
        assertConversionFromUriToUrl("www.", UriUtilTest.NO_PATHS, UriUtilTest.NO_KEYS, UriUtilTest.NO_VALS);
        assertConversionFromUriToUrl("?k=v", UriUtilTest.NO_PATHS, UriUtilTest.NO_KEYS, UriUtilTest.NO_VALS);
    }

    @Test
    public void testListParameters() {
        assertConversionFromUriToUrl(UriUtilTest.FB_COM, UriUtilTest.PATHS_GIVEN, UriUtilTest.KEYS_GIVEN, Arrays.asList("[val11, val12]", "[val21, val22]"));
    }

    @Test
    public void testBadPaths() {
        assertConversionFromUriToUrl(UriUtilTest.FB_COM, Arrays.asList("a.b", "b//c"), UriUtilTest.NO_KEYS, UriUtilTest.NO_VALS);
        assertConversionFromUriToUrl(UriUtilTest.FB_COM, Arrays.asList("a?b", "b\\?c"), UriUtilTest.NO_KEYS, UriUtilTest.NO_VALS);
        assertConversionFromUriToUrl(UriUtilTest.FB_COM, Arrays.asList("{", "}"), UriUtilTest.NO_KEYS, UriUtilTest.NO_VALS);
    }
}

