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
package org.drools.verifier.core.cache.inspectors;


import org.drools.verifier.core.checks.AnalyzerConfigurationMock;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.FieldAction;
import org.drools.verifier.core.index.model.FieldCondition;
import org.drools.verifier.core.index.model.ObjectField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class FieldInspectorUpdateTest {
    @Mock
    ObjectField objectField;

    @Mock
    RuleInspectorUpdater ruleInspectorUpdater;

    private FieldCondition fieldCondition;

    private FieldAction fieldAction;

    private AnalyzerConfigurationMock configurationMock;

    @Test
    public void updateAction() throws Exception {
        fieldAction.setValue(new Values(20));
        Mockito.verify(ruleInspectorUpdater).resetActionsInspectors();
    }

    @Test
    public void updateCondition() throws Exception {
        fieldCondition.setValue(new Values(20));
        Mockito.verify(ruleInspectorUpdater).resetConditionsInspectors();
    }
}

