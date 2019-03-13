/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.verifier.core.index;


import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.Conditions;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.ObjectField;
import org.drools.verifier.core.index.select.AllListener;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ConditionsListenerTest {
    private Conditions conditions;

    private AllListener allListener;

    private AnalyzerConfigurationMock configuration;

    @Test
    public void testListen() throws Exception {
        conditions.add(new org.drools.verifier.core.index.model.FieldCondition(new Field(Mockito.mock(ObjectField.class), "Person", "String", "name", configuration), new Column(1, configuration), "==", new org.drools.verifier.core.index.keys.Values(10), configuration));
        Mockito.verify(allListener).onAllChanged(ArgumentMatchers.anyCollection());
    }

    @Test
    public void testUpdate() throws Exception {
        final Condition condition = new org.drools.verifier.core.index.model.FieldCondition(new Field(Mockito.mock(ObjectField.class), "Person", "String", "name", configuration), new Column(1, configuration), "==", new org.drools.verifier.core.index.keys.Values(10), configuration);
        conditions.add(condition);
        Mockito.reset(allListener);
        condition.setValue(new org.drools.verifier.core.index.keys.Values(20));
        Mockito.verify(allListener).onAllChanged(ArgumentMatchers.anyCollection());
    }
}

