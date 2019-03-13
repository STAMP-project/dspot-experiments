/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import okhttp3.OkHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class ParseHttpClientTest {
    // We can not use ParameterizedRobolectricTestRunner right now since Robolectric use
    // default java classloader when we construct the parameters. However
    // SSLCertificateSocketFactory is only mocked under Robolectric classloader.
    @Test
    public void testParseOkHttpClientExecuteWithSuccessResponse() throws Exception {
        doSingleParseHttpClientExecuteWithResponse(200, "OK", "Success", ParseHttpClient.createClient(new OkHttpClient.Builder()));
    }

    @Test
    public void testParseOkHttpClientExecuteWithErrorResponse() throws Exception {
        doSingleParseHttpClientExecuteWithResponse(404, "NOT FOUND", "Error", ParseHttpClient.createClient(new OkHttpClient.Builder()));
    }

    // TODO(mengyan): Add testParseURLConnectionHttpClientExecuteWithGzipResponse, right now we can
    // not do that since in unit test env, URLConnection does not use OKHttp internally, so there is
    // no transparent ungzip
    @Test
    public void testParseOkHttpClientExecuteWithGzipResponse() throws Exception {
        doSingleParseHttpClientExecuteWithGzipResponse(200, "OK", "Success", ParseHttpClient.createClient(new OkHttpClient.Builder()));
    }
}

