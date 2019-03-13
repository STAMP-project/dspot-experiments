/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.guided.dtable.shared.model;


import DataType.TYPE_STRING;
import java.util.List;
import java.util.function.Supplier;
import org.junit.Test;


public class BRLRuleModelTest extends BaseBRLTest {
    private BRLRuleModel rm;

    @Test
    public void getLHSBoundFactsWithNoDefinition() {
        assertThereAreNoBindings();
    }

    @Test
    public void getLHSBoundFactsWithPattern() {
        whenThereIsAPattern("Applicant", "$a");
        assertThereIsLHSBindingFor("$a");
    }

    @Test
    public void getLHSBoundFactsWithFactPattern() {
        whenThereIsABRLFactPattern("Applicant", "$a");
        assertThereIsLHSBindingFor("$a");
    }

    @Test
    public void getLHSBoundFactsWithFromCompositeFactPattern() {
        whenThereIsABRLFromCompositeFactPattern("Applicant", "$a");
        assertThereIsLHSBindingFor("$a");
    }

    @Test
    public void getLHSBoundFactWithPattern() {
        whenThereIsAPattern("Applicant", "$a");
        assertThereIsABoundFactFor("$a");
    }

    @Test
    public void getLHSBoundFactWithFactPattern() {
        whenThereIsABRLFactPattern("Applicant", "$a");
        assertThereIsABoundFactFor("$a");
    }

    @Test
    public void getLHSBoundFactWithFromCompositeFactPattern() {
        whenThereIsABRLFromCompositeFactPattern("Applicant", "$a");
        assertThereIsABoundFactFor("$a");
    }

    @Test
    public void getLHSBindingTypeWithPattern() {
        whenThereIsAPattern("Applicant", "$a");
        assertLHSBindingTypeFor("Applicant", "$a");
    }

    @Test
    public void getLHSBindingTypeWithFactPattern() {
        whenThereIsABRLFactPattern("Applicant", "$a");
        assertLHSBindingTypeFor("Applicant", "$a");
    }

    @Test
    public void getLHSBindingTypeWithFromCompositeFactPattern() {
        whenThereIsABRLFromCompositeFactPattern("Applicant", "$a");
        assertLHSBindingTypeFor("Applicant", "$a");
    }

    @Test
    public void getLHSBoundFieldWithPatternField() {
        final Pattern52 p = whenThereIsAPattern("Applicant", "$a");
        whenPatternHasAField(p, "field1", TYPE_STRING, "$f");
        assertThereIsAFieldBindingFor("$f");
    }

    @Test
    public void getLHSBoundFieldWithFactPatternField() {
        final BRLConditionColumn brl = whenThereIsABRLFactPattern("Applicant", "$a");
        whenBRLFactPatternHasAField(brl, "field1", TYPE_STRING, "$f");
        assertThereIsAFieldBindingFor("$f");
    }

    @Test
    public void getLHSBoundFieldWithFromCompositeFactPatternField() {
        final BRLConditionColumn brl = whenThereIsABRLFromCompositeFactPattern("Applicant", "$a");
        whenBRLFromCompositeFactPatternHasAField(brl, "field1", TYPE_STRING, "$f");
        assertThereIsAFieldBindingFor("$f");
    }

    @Test
    public void getLHSBindingTypeWithPatternField() {
        final Pattern52 p = whenThereIsAPattern("Applicant", "$a");
        whenPatternHasAField(p, "field1", TYPE_STRING, "$f");
        assertLHSBindingTypeFor(TYPE_STRING, "$f");
    }

    @Test
    public void getLHSBindingTypeWithFactPatternField() {
        final BRLConditionColumn brl = whenThereIsABRLFactPattern("Applicant", "$a");
        whenBRLFactPatternHasAField(brl, "field1", TYPE_STRING, "$f");
        assertLHSBindingTypeFor(TYPE_STRING, "$f");
    }

    @Test
    public void getLHSBindingTypeWithFromCompositeFactPatternField() {
        final BRLConditionColumn brl = whenThereIsABRLFromCompositeFactPattern("Applicant", "$a");
        whenBRLFromCompositeFactPatternHasAField(brl, "field1", TYPE_STRING, "$f");
        assertLHSBindingTypeFor(TYPE_STRING, "$f");
    }

    @Test
    public void getLHSParentFactPatternForBindingWithPatternField() {
        final Pattern52 p = whenThereIsAPattern("Applicant", "$a");
        whenPatternHasAField(p, "field1", TYPE_STRING, "$f");
        assertLHSParentFactPatternFor("$a", "$f");
    }

    @Test
    public void getLHSParentFactPatternForBindingWithFactPatternField() {
        final BRLConditionColumn brl = whenThereIsABRLFactPattern("Applicant", "$a");
        whenBRLFactPatternHasAField(brl, "field1", TYPE_STRING, "$f");
        assertLHSParentFactPatternFor("$a", "$f");
    }

    @Test
    public void getLHSParentFactPatternForBindingWithFromCompositeFactPatternField() {
        final BRLConditionColumn brl = whenThereIsABRLFromCompositeFactPattern("Applicant", "$a");
        whenBRLFromCompositeFactPatternHasAField(brl, "field1", TYPE_STRING, "$f");
        assertLHSParentFactPatternFor("$a", "$f");
    }

    @Test
    public void getAllLHSVariables() {
        final Pattern52 p = whenThereIsAPattern("Applicant", "$a1");
        whenPatternHasAField(p, "field1", TYPE_STRING, "$f1");
        final BRLConditionColumn brl1 = whenThereIsABRLFactPattern("Applicant", "$a2");
        whenBRLFactPatternHasAField(brl1, "field1", TYPE_STRING, "$f2");
        final BRLConditionColumn brl2 = whenThereIsABRLFromCompositeFactPattern("Applicant", "$a3");
        whenBRLFromCompositeFactPatternHasAField(brl2, "field1", TYPE_STRING, "$f3");
        assertLHSBindings("$a1", "$a2", "$a3", "$f1", "$f2", "$f3");
    }

    @Test
    public void checkGetAllLHSVariables() {
        whenThereIsADecisionTableWithPatternsAndBRLCondition();
        assertThereAreBindings(() -> rm.getAllLHSVariables(), "$p1", "$p2", "$p3", "$p4", "$f1", "$f2");
    }

    @Test
    public void checkGetLHSPatternVariables() {
        whenThereIsADecisionTableWithPatternsAndBRLCondition();
        assertThereAreBindings(() -> rm.getLHSPatternVariables(), "$p1", "$p2", "$p3", "$p4");
    }

    @Test
    public void checkGetLHSFieldVariables() {
        whenThereIsADecisionTableWithPatternsAndBRLCondition();
        assertThereAreBindings(() -> rm.getLHSVariables(false, true), "$f1", "$f2");
    }
}

