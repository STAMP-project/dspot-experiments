/**
 * (C) 2007-2012 Alibaba Group Holding Limited.
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
 * Authors:
 *   wuhua <wq163@163.com> , boyan <killme2008@gmail.com>
 */
package com.taobao.metamorphosis.client.consumer;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author ???
 * @since 2011-11-14 ????2:29:14
 */
public class ConsumerConfigUnitTest {
    private ConsumerConfig consumerConfig;

    private final String group = "test-group";

    @Test
    public void testSetConsumeFromMaxOffset() {
        Assert.assertEquals(0, this.consumerConfig.getOffset());
        this.consumerConfig.setConsumeFromMaxOffset();
        Assert.assertEquals(Long.MAX_VALUE, this.consumerConfig.getOffset());
        Assert.assertFalse(this.consumerConfig.isAlwaysConsumeFromMaxOffset());
    }

    @Test
    public void testSetAlwaysConsumeFromMaxOffset() {
        Assert.assertEquals(0, this.consumerConfig.getOffset());
        this.consumerConfig.setConsumeFromMaxOffset(true);
        Assert.assertEquals(Long.MAX_VALUE, this.consumerConfig.getOffset());
        Assert.assertTrue(this.consumerConfig.isAlwaysConsumeFromMaxOffset());
    }
}

