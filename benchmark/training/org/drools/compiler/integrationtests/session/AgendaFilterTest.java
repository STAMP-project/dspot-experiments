/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests.session;


import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.base.RuleNameEndsWithAgendaFilter;
import org.drools.core.base.RuleNameEqualsAgendaFilter;
import org.drools.core.base.RuleNameMatchesAgendaFilter;
import org.drools.core.base.RuleNameStartsWithAgendaFilter;
import org.junit.Test;


public class AgendaFilterTest extends CommonTestMethodBase {
    @Test
    public void testAgendaFilterRuleNameStartsWith() {
        testAgendaFilter(new RuleNameStartsWithAgendaFilter("B"), "Bbb");
    }

    @Test
    public void testAgendaFilterRuleNameEndsWith() {
        testAgendaFilter(new RuleNameEndsWithAgendaFilter("a"), "Aaa");
    }

    @Test
    public void testAgendaFilterRuleNameMatches() {
        testAgendaFilter(new RuleNameMatchesAgendaFilter(".*b."), "Bbb");
    }

    @Test
    public void testAgendaFilterRuleNameEquals() {
        testAgendaFilter(new RuleNameEqualsAgendaFilter("Aaa"), "Aaa");
    }
}

