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


import org.jeasy.rules.api.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CustomRuleOrderingTest extends AbstractTest {
    @Mock
    private CustomRuleOrderingTest.MyRule rule1;

    @Mock
    private CustomRuleOrderingTest.MyRule rule2;

    @Test
    public void whenCompareToIsOverridden_thenShouldExecuteRulesInTheCustomOrder() throws Exception {
        // Given
        Mockito.when(getName()).thenReturn("a");
        Mockito.when(getPriority()).thenReturn(1);
        Mockito.when(rule1.evaluate(facts)).thenReturn(true);
        Mockito.when(getName()).thenReturn("b");
        Mockito.when(getPriority()).thenReturn(0);
        Mockito.when(rule2.evaluate(facts)).thenReturn(true);
        Mockito.when(rule2.compareTo(rule1)).thenCallRealMethod();
        rules.register(rule1);
        rules.register(rule2);
        // When
        rulesEngine.fire(rules, facts);
        // Then
        /* By default, if compareTo is not overridden, then rule2 should be executed first (priority 0 < 1).
        But in this case, the compareTo method order rules by their name, so rule1 should be executed first ("a" < "b")
         */
        InOrder inOrder = Mockito.inOrder(rule1, rule2);
        inOrder.verify(rule1).execute(facts);
        inOrder.verify(rule2).execute(facts);
    }

    class MyRule extends BasicRule {
        @Override
        public int compareTo(Rule rule) {
            return getName().compareTo(rule.getName());
        }
    }
}

