/**
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.facebook;


import FacebookRequestError.Category.LOGIN_RECOVERABLE;
import FacebookRequestError.Category.TRANSIENT;
import FacebookRequestErrorClassification.EC_APP_NOT_INSTALLED;
import FacebookRequestErrorClassification.EC_APP_TOO_MANY_CALLS;
import FacebookRequestErrorClassification.EC_INVALID_SESSION;
import FacebookRequestErrorClassification.EC_INVALID_TOKEN;
import FacebookRequestErrorClassification.EC_RATE;
import FacebookRequestErrorClassification.EC_SERVICE_UNAVAILABLE;
import FacebookRequestErrorClassification.EC_TOO_MANY_USER_ACTION_CALLS;
import FacebookRequestErrorClassification.EC_USER_TOO_MANY_CALLS;
import com.facebook.internal.FacebookRequestErrorClassification;
import junit.framework.Assert;
import org.junit.Test;


public class ErrorClassificationTest extends FacebookTestCase {
    @Test
    public void testDefaultErrorClassification() {
        FacebookRequestErrorClassification errorClassification = FacebookRequestErrorClassification.getDefaultErrorClassification();
        // Test transient takes precedence
        Assert.assertEquals(TRANSIENT, errorClassification.classify(EC_INVALID_TOKEN, 0, true));
        Assert.assertEquals(TRANSIENT, errorClassification.classify(EC_APP_NOT_INSTALLED, 0, true));
        Assert.assertEquals(LOGIN_RECOVERABLE, errorClassification.classify(EC_INVALID_SESSION, 0, false));
        Assert.assertEquals(LOGIN_RECOVERABLE, errorClassification.classify(EC_INVALID_TOKEN, 0, false));
        Assert.assertEquals(LOGIN_RECOVERABLE, errorClassification.classify(EC_APP_NOT_INSTALLED, 0, false));
        Assert.assertEquals(TRANSIENT, errorClassification.classify(EC_SERVICE_UNAVAILABLE, 0, false));
        Assert.assertEquals(TRANSIENT, errorClassification.classify(EC_APP_TOO_MANY_CALLS, 0, false));
        Assert.assertEquals(TRANSIENT, errorClassification.classify(EC_RATE, 0, false));
        Assert.assertEquals(TRANSIENT, errorClassification.classify(EC_USER_TOO_MANY_CALLS, 0, false));
        Assert.assertEquals(TRANSIENT, errorClassification.classify(EC_TOO_MANY_USER_ACTION_CALLS, 0, false));
    }
}

