/**
 * The MIT License
 *
 *  Copyright (c) 2019, Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package org.jeasy.rules.core;


import org.jeasy.rules.api.RulesEngineListener;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;


public class RulesEngineListenerTest extends AbstractTest {
    @Mock
    private RulesEngineListener rulesEngineListener1;

    @Mock
    private RulesEngineListener rulesEngineListener2;

    @Test
    public void rulesEngineListenersShouldBeCalledInOrderWhenFiringRules() throws Exception {
        // Given
        Mockito.when(rule1.evaluate(facts)).thenReturn(true);
        rules.register(rule1);
        // When
        rulesEngine.fire(rules, facts);
        // Then
        InOrder inOrder = Mockito.inOrder(rule1, fact1, fact2, rulesEngineListener1, rulesEngineListener2);
        inOrder.verify(rulesEngineListener1).beforeEvaluate(rules, facts);
        inOrder.verify(rulesEngineListener2).beforeEvaluate(rules, facts);
        inOrder.verify(rule1).evaluate(facts);
        inOrder.verify(rule1).execute(facts);
        inOrder.verify(rulesEngineListener1).afterExecute(rules, facts);
        inOrder.verify(rulesEngineListener2).afterExecute(rules, facts);
    }

    @Test
    public void rulesEngineListenersShouldBeCalledInOrderWhenCheckingRules() throws Exception {
        // Given
        Mockito.when(rule1.evaluate(facts)).thenReturn(true);
        rules.register(rule1);
        // When
        rulesEngine.check(rules, facts);
        // Then
        InOrder inOrder = Mockito.inOrder(rule1, fact1, fact2, rulesEngineListener1, rulesEngineListener2);
        inOrder.verify(rulesEngineListener1).beforeEvaluate(rules, facts);
        inOrder.verify(rulesEngineListener2).beforeEvaluate(rules, facts);
        inOrder.verify(rule1).evaluate(facts);
        inOrder.verify(rulesEngineListener1).afterExecute(rules, facts);
        inOrder.verify(rulesEngineListener2).afterExecute(rules, facts);
    }
}

