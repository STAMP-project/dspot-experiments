/**
 * Copyright (c) 2012-2018 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package io.moquette.broker.subscriptions;


import Token.EMPTY;
import Token.MULTI;
import Token.SINGLE;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;


public class TopicTest {
    @Test
    public void testParseTopic() {
        TopicTest.assertThat(new Topic("finance/stock/ibm")).containsToken("finance", "stock", "ibm");
        TopicTest.assertThat(new Topic("/finance/stock/ibm")).containsToken(EMPTY, "finance", "stock", "ibm");
        TopicTest.assertThat(new Topic("/")).containsToken(EMPTY, EMPTY);
    }

    @Test
    public void testParseTopicMultiValid() {
        TopicTest.assertThat(new Topic("finance/stock/#")).containsToken("finance", "stock", MULTI);
        TopicTest.assertThat(new Topic("#")).containsToken(MULTI);
    }

    @Test
    public void testValidationProcess() {
        // TopicMultiInTheMiddle
        TopicTest.assertThat(new Topic("finance/#/closingprice")).isInValid();
        // MultiNotAfterSeparator
        TopicTest.assertThat(new Topic("finance#")).isInValid();
        // TopicMultiNotAlone
        TopicTest.assertThat(new Topic("/finance/#closingprice")).isInValid();
        // SingleNotAferSeparator
        TopicTest.assertThat(new Topic("finance+")).isInValid();
        TopicTest.assertThat(new Topic("finance/+")).isValid();
    }

    @Test
    public void testParseTopicSingleValid() {
        TopicTest.assertThat(new Topic("finance/stock/+")).containsToken("finance", "stock", SINGLE);
        TopicTest.assertThat(new Topic("+")).containsToken(SINGLE);
        TopicTest.assertThat(new Topic("finance/+/ibm")).containsToken("finance", SINGLE, "ibm");
    }

    @Test
    public void testMatchTopics_simple() {
        TopicTest.assertThat(new Topic("/")).matches("/");
        TopicTest.assertThat(new Topic("/finance")).matches("/finance");
    }

    @Test
    public void testMatchTopics_multi() {
        TopicTest.assertThat(new Topic("finance")).matches("#");
        TopicTest.assertThat(new Topic("finance")).matches("finance/#");
        TopicTest.assertThat(new Topic("finance/stock")).matches("finance/#");
        TopicTest.assertThat(new Topic("finance/stock/ibm")).matches("finance/#");
    }

    @Test
    public void testMatchTopics_single() {
        TopicTest.assertThat(new Topic("finance")).matches("+");
        TopicTest.assertThat(new Topic("finance/stock")).matches("finance/+");
        TopicTest.assertThat(new Topic("finance")).doesNotMatch("finance/+");
        TopicTest.assertThat(new Topic("/finance")).matches("/+");
        TopicTest.assertThat(new Topic("/finance")).doesNotMatch("+");
        TopicTest.assertThat(new Topic("/finance")).matches("+/+");
        TopicTest.assertThat(new Topic("/finance/stock/ibm")).matches("/finance/+/ibm");
        TopicTest.assertThat(new Topic("/")).matches("+/+");
        TopicTest.assertThat(new Topic("sport/")).matches("sport/+");
        TopicTest.assertThat(new Topic("/finance/stock")).doesNotMatch("+");
    }

    @Test
    public void rogerLightMatchTopics() {
        TopicTest.assertThat(new Topic("foo/bar")).matches("foo/bar");
        TopicTest.assertThat(new Topic("foo/bar")).matches("foo/+");
        TopicTest.assertThat(new Topic("foo/bar/baz")).matches("foo/+/baz");
        TopicTest.assertThat(new Topic("foo/bar/baz")).matches("foo/+/#");
        TopicTest.assertThat(new Topic("foo/bar/baz")).matches("#");
        TopicTest.assertThat(new Topic("foo")).doesNotMatch("foo/bar");
        TopicTest.assertThat(new Topic("foo/bar/baz")).doesNotMatch("foo/+");
        TopicTest.assertThat(new Topic("foo/bar/bar")).doesNotMatch("foo/+/baz");
        TopicTest.assertThat(new Topic("fo2/bar/baz")).doesNotMatch("foo/+/#");
        TopicTest.assertThat(new Topic("/foo/bar")).matches("#");
        TopicTest.assertThat(new Topic("/foo/bar")).matches("/#");
        TopicTest.assertThat(new Topic("foo/bar")).doesNotMatch("/#");
        TopicTest.assertThat(new Topic("foo//bar")).matches("foo//bar");
        TopicTest.assertThat(new Topic("foo//bar")).matches("foo//+");
        TopicTest.assertThat(new Topic("foo///baz")).matches("foo/+/+/baz");
        TopicTest.assertThat(new Topic("foo/bar/")).matches("foo/bar/+");
    }

    @Test
    public void exceptHeadToken() {
        Assert.assertEquals(Topic.asTopic("token"), Topic.asTopic("/token").exceptHeadToken());
        Assert.assertEquals(Topic.asTopic("a/b"), Topic.asTopic("/a/b").exceptHeadToken());
    }

    static class TopicAssert extends AbstractAssert<TopicTest.TopicAssert, Topic> {
        TopicAssert(Topic actual) {
            super(actual, TopicTest.TopicAssert.class);
        }

        public TopicTest.TopicAssert matches(String topic) {
            Assertions.assertThat(actual.match(new Topic(topic))).isTrue();
            return myself;
        }

        public TopicTest.TopicAssert doesNotMatch(String topic) {
            Assertions.assertThat(actual.match(new Topic(topic))).isFalse();
            return myself;
        }

        public TopicTest.TopicAssert containsToken(Object... tokens) {
            Assertions.assertThat(actual.getTokens()).containsExactly(asArray(tokens));
            return myself;
        }

        private Token[] asArray(Object... l) {
            Token[] tokens = new Token[l.length];
            for (int i = 0; i < (l.length); i++) {
                Object o = l[i];
                if (o instanceof Token) {
                    tokens[i] = ((Token) (o));
                } else {
                    tokens[i] = new Token(o.toString());
                }
            }
            return tokens;
        }

        public TopicTest.TopicAssert isValid() {
            Assertions.assertThat(actual.isValid()).isTrue();
            return myself;
        }

        public TopicTest.TopicAssert isInValid() {
            Assertions.assertThat(actual.isValid()).isFalse();
            return myself;
        }
    }
}

