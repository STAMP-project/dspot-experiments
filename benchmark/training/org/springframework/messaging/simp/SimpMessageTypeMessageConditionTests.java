/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.messaging.simp;


import SimpMessageType.MESSAGE;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.messaging.Message;

import static SimpMessageType.MESSAGE;
import static SimpMessageType.SUBSCRIBE;


/**
 * Unit tests for SimpMessageTypeMessageCondition.
 *
 * @author Rossen Stoyanchev
 */
public class SimpMessageTypeMessageConditionTests {
    @Test
    public void combine() {
        SimpMessageType messageType = MESSAGE;
        SimpMessageType subscribeType = SUBSCRIBE;
        SimpMessageType actual = condition(messageType).combine(condition(subscribeType)).getMessageType();
        Assert.assertEquals(subscribeType, actual);
        actual = condition(messageType).combine(condition(messageType)).getMessageType();
        Assert.assertEquals(messageType, actual);
        actual = condition(subscribeType).combine(condition(subscribeType)).getMessageType();
        Assert.assertEquals(subscribeType, actual);
    }

    @Test
    public void getMatchingCondition() {
        Message<?> message = message(MESSAGE);
        SimpMessageTypeMessageCondition condition = condition(MESSAGE);
        SimpMessageTypeMessageCondition actual = condition.getMatchingCondition(message);
        Assert.assertNotNull(actual);
        Assert.assertEquals(MESSAGE, actual.getMessageType());
    }

    @Test
    public void getMatchingConditionNoMessageType() {
        Message<?> message = message(null);
        SimpMessageTypeMessageCondition condition = condition(MESSAGE);
        Assert.assertNull(condition.getMatchingCondition(message));
    }

    @Test
    public void compareTo() {
        Message<byte[]> message = message(null);
        Assert.assertEquals(0, condition(MESSAGE).compareTo(condition(MESSAGE), message));
        Assert.assertEquals(0, condition(MESSAGE).compareTo(condition(SimpMessageType.SUBSCRIBE), message));
    }
}

