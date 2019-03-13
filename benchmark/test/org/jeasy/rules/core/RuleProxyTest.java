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


import Rule.DEFAULT_PRIORITY;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.AnnotatedRuleWithMetaRuleAnnotation;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.api.Rule;
import org.junit.Assert;
import org.junit.Test;


public class RuleProxyTest {
    @Test
    public void proxyingHappensEvenWhenRuleIsAnnotatedWithMetaRuleAnnotation() {
        // Given
        AnnotatedRuleWithMetaRuleAnnotation rule = new AnnotatedRuleWithMetaRuleAnnotation();
        // When
        Rule proxy = RuleProxy.asRule(rule);
        // Then
        Assert.assertNotNull(proxy.getDescription());
        Assert.assertNotNull(proxy.getName());
    }

    @Test
    public void asRuleForObjectThatImplementsRule() {
        Object rule = new BasicRule();
        Rule proxy = RuleProxy.asRule(rule);
        Assert.assertNotNull(proxy.getDescription());
        Assert.assertNotNull(proxy.getName());
    }

    @Test
    public void asRuleForObjectThatHasProxied() {
        Object rule = new RuleProxyTest.DummyRule();
        Rule proxy1 = RuleProxy.asRule(rule);
        Rule proxy2 = RuleProxy.asRule(proxy1);
        Assert.assertEquals(proxy1.getDescription(), proxy2.getDescription());
        Assert.assertEquals(proxy1.getName(), proxy2.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void asRuleForPojo() {
        Object rule = new Object();
        Rule proxy = RuleProxy.asRule(rule);
    }

    @Test
    public void invokeEquals() {
        Object rule = new RuleProxyTest.DummyRule();
        Rule proxy1 = RuleProxy.asRule(rule);
        Rule proxy2 = RuleProxy.asRule(proxy1);
        Rule proxy3 = RuleProxy.asRule(proxy2);
        /**
         *
         *
         * @see Object#equals(Object) reflexive
         */
        Assert.assertEquals(rule, rule);
        Assert.assertEquals(proxy1, proxy1);
        Assert.assertEquals(proxy2, proxy2);
        Assert.assertEquals(proxy3, proxy3);
        /**
         *
         *
         * @see Object#equals(Object) symmetric
         */
        Assert.assertNotEquals(rule, proxy1);
        Assert.assertNotEquals(proxy1, rule);
        Assert.assertEquals(proxy1, proxy2);
        Assert.assertEquals(proxy2, proxy1);
        /**
         *
         *
         * @see Object#equals(Object) transitive consistent
         */
        Assert.assertEquals(proxy1, proxy2);
        Assert.assertEquals(proxy2, proxy3);
        Assert.assertEquals(proxy3, proxy1);
        /**
         *
         *
         * @see Object#equals(Object) non-null
         */
        Assert.assertNotEquals(rule, null);
        Assert.assertNotEquals(proxy1, null);
        Assert.assertNotEquals(proxy2, null);
        Assert.assertNotEquals(proxy3, null);
    }

    @Test
    public void invokeHashCode() {
        Object rule = new RuleProxyTest.DummyRule();
        Rule proxy1 = RuleProxy.asRule(rule);
        Rule proxy2 = RuleProxy.asRule(proxy1);
        /**
         *
         *
         * @see Object#hashCode rule1
         */
        Assert.assertEquals(proxy1.hashCode(), proxy1.hashCode());
        /**
         *
         *
         * @see Object#hashCode rule2
         */
        Assert.assertEquals(proxy1, proxy2);
        Assert.assertEquals(proxy1.hashCode(), proxy2.hashCode());
        /**
         *
         *
         * @see Object#hashCode rule3
         */
        Assert.assertNotEquals(rule, proxy1);
        Assert.assertNotEquals(rule.hashCode(), proxy1.hashCode());
    }

    @Test
    public void invokeToString() {
        Object rule = new RuleProxyTest.DummyRule();
        Rule proxy1 = RuleProxy.asRule(rule);
        Rule proxy2 = RuleProxy.asRule(proxy1);
        Assert.assertEquals(proxy1.toString(), proxy1.toString());
        Assert.assertEquals(proxy1.toString(), proxy2.toString());
        Assert.assertEquals(rule.toString(), proxy1.toString());
    }

    @Test
    public void testPriorityFromAnnotation() {
        @Rule(priority = 1)
        class MyRule {
            @Condition
            public boolean when() {
                return true;
            }

            @Action
            public void then() {
            }
        }
        Object rule = new MyRule();
        Rule proxy = RuleProxy.asRule(rule);
        Assert.assertEquals(1, proxy.getPriority());
    }

    @Test
    public void testPriorityFromMethod() {
        @Rule
        class MyRule {
            @Condition
            public boolean when() {
                return true;
            }

            @Action
            public void then() {
            }

            @Priority
            public int getPriority() {
                return 2;
            }
        }
        Object rule = new MyRule();
        Rule proxy = RuleProxy.asRule(rule);
        Assert.assertEquals(2, proxy.getPriority());
    }

    @Test
    public void testPriorityPrecedence() {
        @Rule(priority = 1)
        class MyRule {
            @Condition
            public boolean when() {
                return true;
            }

            @Action
            public void then() {
            }

            @Priority
            public int getPriority() {
                return 2;
            }
        }
        Object rule = new MyRule();
        Rule proxy = RuleProxy.asRule(rule);
        Assert.assertEquals(2, proxy.getPriority());
    }

    @Test
    public void testDefaultPriority() {
        @Rule
        class MyRule {
            @Condition
            public boolean when() {
                return true;
            }

            @Action
            public void then() {
            }
        }
        Object rule = new MyRule();
        Rule proxy = RuleProxy.asRule(rule);
        Assert.assertEquals(DEFAULT_PRIORITY, proxy.getPriority());
    }

    @Rule
    class DummyRule {
        @Condition
        public boolean when() {
            return true;
        }

        @Action
        public void then() {
        }

        @Override
        public String toString() {
            return "I am a Dummy rule";
        }
    }
}

