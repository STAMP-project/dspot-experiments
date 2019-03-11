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
package com.taobao.metamorphosis.utils;


import com.taobao.metamorphosis.Message;
import org.junit.Assert;
import org.junit.Test;


public class MessageFlagUtilsUnitTest {
    @Test
    public void testFlagWithoutAttribute() {
        final Message message = new Message("test", "hello".getBytes());
        final int flag = MessageFlagUtils.getFlag(message);
        Assert.assertFalse(MessageFlagUtils.hasAttribute(flag));
    }

    @Test
    public void testFlagWithAttribute() {
        final Message message = new Message("test", "hello".getBytes(), "");
        final int flag = MessageFlagUtils.getFlag(message);
        Assert.assertTrue(MessageFlagUtils.hasAttribute(flag));
    }
}

