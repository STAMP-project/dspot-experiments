/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.dtable.shared.validation;


import GuidedDecisionTable52.HitPolicy;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;


@RunWith(Parameterized.class)
public class DecisionTableValidatorHitPolicyAttributeLimitationsTest {
    private final HitPolicy hitPolicy;

    private final String attributeName;

    private final boolean isAllowed;

    public DecisionTableValidatorHitPolicyAttributeLimitationsTest(final GuidedDecisionTable52.HitPolicy hitPolicy, final String attributeName, final boolean isAllowed) {
        this.hitPolicy = hitPolicy;
        this.attributeName = attributeName;
        this.isAllowed = isAllowed;
    }

    @Test
    public void addAttributeColumn() throws Exception {
        final GuidedDecisionTable52 table = Mockito.mock(GuidedDecisionTable52.class);
        Mockito.when(table.getHitPolicy()).thenReturn(hitPolicy);
        final DecisionTableValidator validator = new DecisionTableValidator(table);
        final AttributeCol52 attributeCol52 = new AttributeCol52();
        attributeCol52.setAttribute(attributeName);
        boolean wasAllowed = true;
        try {
            validator.isValidToAdd(attributeCol52);
        } catch (final Exception e) {
            wasAllowed = false;
        }
        Assert.assertEquals(wasAllowed, isAllowed);
    }
}

