/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.function;


import io.confluent.ksql.parser.SqlBaseParser;
import org.antlr.v4.runtime.Vocabulary;
import org.junit.Assert;
import org.junit.Test;


public class FunctionNameValidatorTest {
    private final FunctionNameValidator validator = new FunctionNameValidator();

    @Test
    public void shouldNotAllowJavaReservedWords() {
        // not exhaustive..
        Assert.assertFalse(validator.test("enum"));
        Assert.assertFalse(validator.test("static"));
        Assert.assertFalse(validator.test("final"));
        Assert.assertFalse(validator.test("do"));
        Assert.assertFalse(validator.test("while"));
        Assert.assertFalse(validator.test("double"));
        Assert.assertFalse(validator.test("float"));
        Assert.assertFalse(validator.test("private"));
        Assert.assertFalse(validator.test("public"));
        Assert.assertFalse(validator.test("goto"));
        Assert.assertFalse(validator.test("default"));
    }

    @Test
    public void shouldNotAllowKsqlReservedWordsExceptSubstringAndConcat() {
        final Vocabulary vocabulary = SqlBaseParser.VOCABULARY;
        final int maxTokenType = vocabulary.getMaxTokenType();
        for (int i = 0; i < maxTokenType; i++) {
            final String symbolicName = vocabulary.getSymbolicName(i);
            if (symbolicName != null) {
                if ((symbolicName.equalsIgnoreCase("substring")) || (symbolicName.equalsIgnoreCase("concat"))) {
                    Assert.assertTrue(validator.test(symbolicName));
                } else {
                    Assert.assertFalse(validator.test(symbolicName));
                }
            }
        }
    }

    @Test
    public void shouldNotAllowInvalidJavaIdentifiers() {
        Assert.assertFalse(validator.test("@foo"));
        Assert.assertFalse(validator.test("1foo"));
        Assert.assertFalse(validator.test("^foo"));
        Assert.assertFalse(validator.test("&foo"));
        Assert.assertFalse(validator.test("%foo"));
        Assert.assertFalse(validator.test("+foo"));
        Assert.assertFalse(validator.test("-foo"));
        Assert.assertFalse(validator.test("#foo"));
        Assert.assertFalse(validator.test("f1@%$"));
    }

    @Test
    public void shouldAllowValidJavaIdentifiers() {
        Assert.assertTrue(validator.test("foo"));
        Assert.assertTrue(validator.test("$foo"));
        Assert.assertTrue(validator.test("f1_b"));
        Assert.assertTrue(validator.test("__blah"));
    }
}

