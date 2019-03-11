/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.analyze;


import io.crate.expression.symbol.ParameterSymbol;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import io.crate.testing.SymbolMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;


public class DecommissionNodeAnalyzerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    @Test
    public void testDecommissionNodeUsingStringLiteral() throws Exception {
        AnalyzedDecommissionNodeStatement statement = e.analyze("alter cluster decommission 'aNodeIdOrName'");
        assertThat(statement.nodeIdOrName(), SymbolMatchers.isLiteral("aNodeIdOrName"));
    }

    @Test
    public void testDecommissionNodeUsingParameter() throws Exception {
        AnalyzedDecommissionNodeStatement statement = e.analyze("alter cluster decommission ?");
        assertThat(statement.nodeIdOrName(), Matchers.instanceOf(ParameterSymbol.class));
    }

    @Test
    public void testDecommissionNodeFailsIfNodeIsMissing() throws Exception {
        expectedException.expectMessage("no viable alternative at input '<EOF>'");
        AnalyzedDecommissionNodeStatement statement = e.analyze("alter cluster decommission");
    }
}

