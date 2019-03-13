/**
 * Copyright 2007 Red Hat, Inc. and/or its affiliates.
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
 *
 * Created on Dec 14, 2007
 */
package org.drools.compiler.integrationtests;


import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.StockTick;
import org.junit.Test;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests related to the pseudo session clock
 */
public class PseudoClockEventsTest extends CommonTestMethodBase {
    private static final String evalFirePseudoClockDeclaration = (((((("package org.drools.compiler.integrationtests\n" + "import ") + (StockTick.class.getCanonicalName())) + "\n") + "\n") + "declare StockTick\n") + "    @role( event )\n") + // "    @expires( 1m )\n" +
    "end\n\n";

    private static final String evalFirePseudoClockRuleA = "rule A\n" + (((((("when\n" + "\t$a: StockTick( $priceA: price )\n") + "\t$b: StockTick( $priceA < price )\n") + "then \n") + "    System.out.println(\"Rule A fired by thread \" + Thread.currentThread().getName() + \": \" + $a + \", \" + $b);\n") + "end\n") + "");

    private static final String evalFirePseudoClockRuleB = "rule B\n" + (((((("when\n" + "\t$a: StockTick()\n") + "\tnot( StockTick( this after[1,10s] $a ) )\n") + "then \n") + "    System.out.println(\"Rule B fired by thread \" + Thread.currentThread().getName());\n") + "end\n") + "");

    int evalFirePseudoClockStockCount = 5;

    @Test(timeout = 10000)
    public void testEvenFirePseudoClockRuleA() throws Exception {
        AgendaEventListener ael = Mockito.mock(AgendaEventListener.class);
        processStocks(evalFirePseudoClockStockCount, ael, ((PseudoClockEventsTest.evalFirePseudoClockDeclaration) + (PseudoClockEventsTest.evalFirePseudoClockRuleA)));
        Mockito.verify(ael, Mockito.times((((evalFirePseudoClockStockCount) * ((evalFirePseudoClockStockCount) - 1)) / 2))).afterMatchFired(ArgumentMatchers.any(AfterMatchFiredEvent.class));
    }

    @Test(timeout = 10000)
    public void testEvenFirePseudoClockRuleB() throws Exception {
        AgendaEventListener ael = Mockito.mock(AgendaEventListener.class);
        processStocks(evalFirePseudoClockStockCount, ael, ((PseudoClockEventsTest.evalFirePseudoClockDeclaration) + (PseudoClockEventsTest.evalFirePseudoClockRuleB)));
        Mockito.verify(ael, Mockito.times(((evalFirePseudoClockStockCount) - 1))).afterMatchFired(ArgumentMatchers.any(AfterMatchFiredEvent.class));
    }

    @Test(timeout = 60000)
    public void testEvenFirePseudoClockRulesAB() throws Exception {
        AgendaEventListener ael = Mockito.mock(AgendaEventListener.class);
        processStocks(evalFirePseudoClockStockCount, ael, (((PseudoClockEventsTest.evalFirePseudoClockDeclaration) + (PseudoClockEventsTest.evalFirePseudoClockRuleA)) + (PseudoClockEventsTest.evalFirePseudoClockRuleB)));
        final int expectedActivationCount = ((((evalFirePseudoClockStockCount) * ((evalFirePseudoClockStockCount) - 1)) / 2) + (evalFirePseudoClockStockCount)) - 1;
        Mockito.verify(ael, Mockito.times(expectedActivationCount)).afterMatchFired(ArgumentMatchers.any(AfterMatchFiredEvent.class));
    }
}

