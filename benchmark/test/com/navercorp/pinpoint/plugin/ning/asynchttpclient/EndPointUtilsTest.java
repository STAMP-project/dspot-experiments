/**
 * Copyright 2017 NAVER Corp.
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
package com.navercorp.pinpoint.plugin.ning.asynchttpclient;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jaehong.kim
 */
public class EndPointUtilsTest {
    @Test
    public void getEndPoint() throws Exception {
        Assert.assertEquals("127.0.0.1:80", EndPointUtils.getEndPoint("http://127.0.0.1:80", null));
        Assert.assertEquals("127.0.0.1:80", EndPointUtils.getEndPoint("http://127.0.0.1:80/path", null));
        Assert.assertEquals("127.0.0.1:80", EndPointUtils.getEndPoint("http://127.0.0.1:80?query=foo", null));
        Assert.assertEquals("127.0.0.1:80", EndPointUtils.getEndPoint("http://127.0.0.1:80/path?query=foo", null));
        Assert.assertEquals("127.0.0.1", EndPointUtils.getEndPoint("http://127.0.0.1", null));
        Assert.assertEquals("127.0.0.1", EndPointUtils.getEndPoint("http://127.0.0.1/path", null));
        Assert.assertEquals("127.0.0.1", EndPointUtils.getEndPoint("http://127.0.0.1?query=foo", null));
        Assert.assertEquals("127.0.0.1", EndPointUtils.getEndPoint("http://127.0.0.1/path?query=foo", null));
        Assert.assertEquals("127.0.0.1:443", EndPointUtils.getEndPoint("https://127.0.0.1:443", null));
        Assert.assertEquals("127.0.0.1", EndPointUtils.getEndPoint("https://127.0.0.1", null));
        Assert.assertEquals("127.0.0.1:99999", EndPointUtils.getEndPoint("http://127.0.0.1:99999", null));
        Assert.assertEquals("111111", EndPointUtils.getEndPoint("http://111111", null));
        Assert.assertEquals(null, EndPointUtils.getEndPoint(null, null));
        Assert.assertEquals(null, EndPointUtils.getEndPoint("", null));
        Assert.assertEquals(null, EndPointUtils.getEndPoint(" ", null));
        Assert.assertEquals("default", EndPointUtils.getEndPoint(null, "default"));
        Assert.assertEquals("default", EndPointUtils.getEndPoint("", "default"));
        Assert.assertEquals("default", EndPointUtils.getEndPoint(" ", "default"));
        Assert.assertEquals("127.0.0.1:number", EndPointUtils.getEndPoint("http://127.0.0.1:number", null));
        Assert.assertEquals("ftp:foo:bar@127.0.0.1", EndPointUtils.getEndPoint("ftp:foo:bar@127.0.0.1", null));
        Assert.assertEquals("mailto:foo@bar.com", EndPointUtils.getEndPoint("mailto:foo@bar.com", null));
    }
}

