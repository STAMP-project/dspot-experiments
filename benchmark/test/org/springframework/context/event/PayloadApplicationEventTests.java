/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.context.event;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;


/**
 *
 *
 * @author Juergen Hoeller
 */
public class PayloadApplicationEventTests {
    @Test
    public void testEventClassWithInterface() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(PayloadApplicationEventTests.AuditableListener.class);
        PayloadApplicationEventTests.AuditablePayloadEvent event = new PayloadApplicationEventTests.AuditablePayloadEvent<>(this, "xyz");
        ac.publishEvent(event);
        Assert.assertTrue(ac.getBean(PayloadApplicationEventTests.AuditableListener.class).events.contains(event));
    }

    public interface Auditable {}

    @SuppressWarnings("serial")
    public static class AuditablePayloadEvent<T> extends PayloadApplicationEvent<T> implements PayloadApplicationEventTests.Auditable {
        public AuditablePayloadEvent(Object source, T payload) {
            super(source, payload);
        }
    }

    @Component
    public static class AuditableListener {
        public final List<PayloadApplicationEventTests.Auditable> events = new ArrayList<>();

        @EventListener
        public void onEvent(PayloadApplicationEventTests.Auditable event) {
            events.add(event);
        }
    }
}

