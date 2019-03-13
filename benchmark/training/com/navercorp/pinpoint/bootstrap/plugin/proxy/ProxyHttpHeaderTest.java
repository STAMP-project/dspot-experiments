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
package com.navercorp.pinpoint.bootstrap.plugin.proxy;


import ProxyHttpHeader.TYPE_APACHE;
import org.junit.Assert;
import org.junit.Test;

import static ProxyHttpHeader.TYPE_APACHE;


/**
 *
 *
 * @author jaehong.kim
 */
public class ProxyHttpHeaderTest {
    @Test
    public void operations() throws Exception {
        ProxyHttpHeader header = new ProxyHttpHeader(TYPE_APACHE);
        Assert.assertFalse(header.isValid());
        final long currentTimeMillis = System.currentTimeMillis();
        header.setApp("testapp");
        header.setBusyPercent(((byte) (99)));
        header.setIdlePercent(((byte) (1)));
        header.setDurationTimeMicroseconds(12345);
        header.setReceivedTimeMillis(currentTimeMillis);
        header.setValid(true);
        Assert.assertEquals(TYPE_APACHE, header.getType());
        Assert.assertEquals("testapp", header.getApp());
        Assert.assertEquals(99, header.getBusyPercent());
        Assert.assertEquals(1, header.getIdlePercent());
        Assert.assertEquals(12345, header.getDurationTimeMicroseconds());
        Assert.assertEquals(currentTimeMillis, header.getReceivedTimeMillis());
        Assert.assertTrue(header.isValid());
    }
}

