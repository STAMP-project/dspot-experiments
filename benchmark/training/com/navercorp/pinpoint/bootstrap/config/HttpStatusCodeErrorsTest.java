/**
 * Copyright 2018 NAVER Corp.
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
package com.navercorp.pinpoint.bootstrap.config;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class HttpStatusCodeErrorsTest {
    @Test
    public void isErrorCode() throws Exception {
        // default 5xx
        HttpStatusCodeErrors defaultHttpStatusCodeErrors = new HttpStatusCodeErrors();
        Assert.assertTrue(defaultHttpStatusCodeErrors.isErrorCode(500));
        Assert.assertTrue(defaultHttpStatusCodeErrors.isErrorCode(501));
        Assert.assertFalse(defaultHttpStatusCodeErrors.isErrorCode(200));
        Assert.assertFalse(defaultHttpStatusCodeErrors.isErrorCode(999));
        Assert.assertFalse(defaultHttpStatusCodeErrors.isErrorCode(0));
        Assert.assertFalse(defaultHttpStatusCodeErrors.isErrorCode((-1)));
        HttpStatusCodeErrors customHttpStatusCodeErrors = new HttpStatusCodeErrors(Arrays.asList("5xx", "401", "402"));
        Assert.assertTrue(customHttpStatusCodeErrors.isErrorCode(500));
        Assert.assertTrue(customHttpStatusCodeErrors.isErrorCode(501));
        Assert.assertTrue(customHttpStatusCodeErrors.isErrorCode(401));
        Assert.assertTrue(customHttpStatusCodeErrors.isErrorCode(402));
        Assert.assertFalse(customHttpStatusCodeErrors.isErrorCode(100));
        Assert.assertFalse(customHttpStatusCodeErrors.isErrorCode(200));
        Assert.assertFalse(customHttpStatusCodeErrors.isErrorCode(201));
        Assert.assertFalse(customHttpStatusCodeErrors.isErrorCode(300));
        Assert.assertFalse(customHttpStatusCodeErrors.isErrorCode(400));
        Assert.assertFalse(customHttpStatusCodeErrors.isErrorCode(404));
    }

    @Test
    public void isHttpStatusCode() throws Exception {
        HttpStatusCodeErrors httpStatusCodeErrors = new HttpStatusCodeErrors();
        Assert.assertTrue(httpStatusCodeErrors.isHttpStatusCode(200));
        Assert.assertTrue(httpStatusCodeErrors.isHttpStatusCode(300));
        Assert.assertTrue(httpStatusCodeErrors.isHttpStatusCode(500));
        Assert.assertFalse(httpStatusCodeErrors.isHttpStatusCode(0));
        Assert.assertFalse(httpStatusCodeErrors.isHttpStatusCode(600));
    }
}

