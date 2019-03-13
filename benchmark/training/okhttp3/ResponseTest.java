/**
 * Copyright (C) 2016 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okhttp3;


import org.junit.Assert;
import org.junit.Test;


public final class ResponseTest {
    @Test
    public void peekShorterThanResponse() throws Exception {
        Response response = newResponse(responseBody("abcdef"));
        ResponseBody peekedBody = response.peekBody(3);
        Assert.assertEquals("abc", peekedBody.string());
        Assert.assertEquals("abcdef", response.body().string());
    }

    @Test
    public void peekLongerThanResponse() throws Exception {
        Response response = newResponse(responseBody("abc"));
        ResponseBody peekedBody = response.peekBody(6);
        Assert.assertEquals("abc", peekedBody.string());
        Assert.assertEquals("abc", response.body().string());
    }

    @Test
    public void peekAfterReadingResponse() throws Exception {
        Response response = newResponse(responseBody("abc"));
        Assert.assertEquals("abc", response.body().string());
        try {
            response.peekBody(3);
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void eachPeakIsIndependent() throws Exception {
        Response response = newResponse(responseBody("abcdef"));
        ResponseBody p1 = response.peekBody(4);
        ResponseBody p2 = response.peekBody(2);
        Assert.assertEquals("abcdef", response.body().string());
        Assert.assertEquals("abcd", p1.string());
        Assert.assertEquals("ab", p2.string());
    }
}

