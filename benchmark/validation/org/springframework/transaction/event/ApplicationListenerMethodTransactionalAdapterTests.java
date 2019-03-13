/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.transaction.event;


import TransactionPhase.AFTER_COMMIT;
import TransactionPhase.AFTER_COMPLETION;
import TransactionPhase.AFTER_ROLLBACK;
import java.lang.reflect.Method;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.util.ReflectionUtils;

import static TransactionPhase.AFTER_COMPLETION;
import static TransactionPhase.AFTER_ROLLBACK;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class ApplicationListenerMethodTransactionalAdapterTests {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void defaultPhase() {
        Method m = ReflectionUtils.findMethod(ApplicationListenerMethodTransactionalAdapterTests.SampleEvents.class, "defaultPhase", String.class);
        assertPhase(m, AFTER_COMMIT);
    }

    @Test
    public void phaseSet() {
        Method m = ReflectionUtils.findMethod(ApplicationListenerMethodTransactionalAdapterTests.SampleEvents.class, "phaseSet", String.class);
        assertPhase(m, AFTER_ROLLBACK);
    }

    @Test
    public void phaseAndClassesSet() {
        Method m = ReflectionUtils.findMethod(ApplicationListenerMethodTransactionalAdapterTests.SampleEvents.class, "phaseAndClassesSet");
        assertPhase(m, AFTER_COMPLETION);
        supportsEventType(true, m, createGenericEventType(String.class));
        supportsEventType(true, m, createGenericEventType(Integer.class));
        supportsEventType(false, m, createGenericEventType(Double.class));
    }

    @Test
    public void valueSet() {
        Method m = ReflectionUtils.findMethod(ApplicationListenerMethodTransactionalAdapterTests.SampleEvents.class, "valueSet");
        assertPhase(m, AFTER_COMMIT);
        supportsEventType(true, m, createGenericEventType(String.class));
        supportsEventType(false, m, createGenericEventType(Double.class));
    }

    static class SampleEvents {
        @TransactionalEventListener
        public void defaultPhase(String data) {
        }

        @TransactionalEventListener(phase = AFTER_ROLLBACK)
        public void phaseSet(String data) {
        }

        @TransactionalEventListener(classes = { String.class, Integer.class }, phase = AFTER_COMPLETION)
        public void phaseAndClassesSet() {
        }

        @TransactionalEventListener(String.class)
        public void valueSet() {
        }
    }
}

