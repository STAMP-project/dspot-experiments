/**
 * Copyright 2002-2018 the original author or authors.
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


import java.util.concurrent.ConcurrentHashMap;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;


/**
 * Unit tests for
 * {@link org.springframework.messaging.simp.SimpAttributesContextHolder}.
 *
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class SimpAttributesContextHolderTests {
    private SimpAttributes simpAttributes;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void resetAttributes() {
        SimpAttributesContextHolder.setAttributes(this.simpAttributes);
        MatcherAssert.assertThat(SimpAttributesContextHolder.getAttributes(), sameInstance(this.simpAttributes));
        SimpAttributesContextHolder.resetAttributes();
        MatcherAssert.assertThat(SimpAttributesContextHolder.getAttributes(), nullValue());
    }

    @Test
    public void getAttributes() {
        MatcherAssert.assertThat(SimpAttributesContextHolder.getAttributes(), nullValue());
        SimpAttributesContextHolder.setAttributes(this.simpAttributes);
        MatcherAssert.assertThat(SimpAttributesContextHolder.getAttributes(), sameInstance(this.simpAttributes));
    }

    @Test
    public void setAttributes() {
        SimpAttributesContextHolder.setAttributes(this.simpAttributes);
        MatcherAssert.assertThat(SimpAttributesContextHolder.getAttributes(), sameInstance(this.simpAttributes));
        SimpAttributesContextHolder.setAttributes(null);
        MatcherAssert.assertThat(SimpAttributesContextHolder.getAttributes(), nullValue());
    }

    @Test
    public void setAttributesFromMessage() {
        String sessionId = "session1";
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setSessionAttributes(map);
        Message<?> message = MessageBuilder.createMessage("", headerAccessor.getMessageHeaders());
        SimpAttributesContextHolder.setAttributesFromMessage(message);
        SimpAttributes attrs = SimpAttributesContextHolder.getAttributes();
        MatcherAssert.assertThat(attrs, notNullValue());
        MatcherAssert.assertThat(attrs.getSessionId(), is(sessionId));
        attrs.setAttribute("name1", "value1");
        MatcherAssert.assertThat(map.get("name1"), is("value1"));
    }

    @Test
    public void setAttributesFromMessageWithMissingSessionId() {
        this.thrown.expect(IllegalStateException.class);
        this.thrown.expectMessage(startsWith("No session id in"));
        SimpAttributesContextHolder.setAttributesFromMessage(new org.springframework.messaging.support.GenericMessage<Object>(""));
    }

    @Test
    public void setAttributesFromMessageWithMissingSessionAttributes() {
        this.thrown.expect(IllegalStateException.class);
        this.thrown.expectMessage(startsWith("No session attributes in"));
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        headerAccessor.setSessionId("session1");
        Message<?> message = MessageBuilder.createMessage("", headerAccessor.getMessageHeaders());
        SimpAttributesContextHolder.setAttributesFromMessage(message);
    }

    @Test
    public void currentAttributes() {
        SimpAttributesContextHolder.setAttributes(this.simpAttributes);
        MatcherAssert.assertThat(SimpAttributesContextHolder.currentAttributes(), sameInstance(this.simpAttributes));
    }

    @Test
    public void currentAttributesNone() {
        this.thrown.expect(IllegalStateException.class);
        this.thrown.expectMessage(startsWith("No thread-bound SimpAttributes found"));
        SimpAttributesContextHolder.currentAttributes();
    }
}

