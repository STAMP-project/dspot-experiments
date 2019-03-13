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


import org.junit.Test;
import org.mockito.Mockito;


public class SkipOnFirstFailedRuleTest extends AbstractTest {
    @Test
    public void testSkipOnFirstFailedRule() throws Exception {
        // Given
        Mockito.when(rule1.evaluate(facts)).thenReturn(true);
        Mockito.when(rule2.compareTo(rule1)).thenReturn(1);
        final Exception exception = new Exception("fatal error!");
        Mockito.doThrow(exception).when(rule1).execute(facts);
        rules.register(rule1);
        rules.register(rule2);
        // When
        rulesEngine.fire(rules, facts);
        // Then
        // Rule 1 should be executed
        Mockito.verify(rule1).execute(facts);
        // Rule 2 should be skipped since Rule 1 has failed
        Mockito.verify(rule2, Mockito.never()).execute(facts);
    }
}

