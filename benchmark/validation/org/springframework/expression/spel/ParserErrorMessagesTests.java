/**
 * Copyright 2002-2012 the original author or authors.
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
 */
package org.springframework.expression.spel;


import SpelMessage.MORE_INPUT;
import SpelMessage.NOT_AN_INTEGER;
import SpelMessage.NOT_A_LONG;
import SpelMessage.NOT_EXPECTED_TOKEN;
import SpelMessage.RIGHT_OPERAND_PROBLEM;
import org.junit.Test;


/**
 * Tests the messages and exceptions that come out for badly formed expressions
 *
 * @author Andy Clement
 */
public class ParserErrorMessagesTests extends AbstractExpressionTests {
    @Test
    public void testBrokenExpression01() {
        // will not fit into an int, needs L suffix
        parseAndCheckError("0xCAFEBABE", NOT_AN_INTEGER);
        evaluate("0xCAFEBABEL", 3405691582L, Long.class);
        parseAndCheckError("0xCAFEBABECAFEBABEL", NOT_A_LONG);
    }

    @Test
    public void testBrokenExpression02() {
        // rogue 'G' on the end
        parseAndCheckError("0xB0BG", MORE_INPUT, 5, "G");
    }

    @Test
    public void testBrokenExpression04() {
        // missing right operand
        parseAndCheckError("true or ", RIGHT_OPERAND_PROBLEM, 5);
    }

    @Test
    public void testBrokenExpression05() {
        // missing right operand
        parseAndCheckError("1 + ", RIGHT_OPERAND_PROBLEM, 2);
    }

    @Test
    public void testBrokenExpression07() {
        // T() can only take an identifier (possibly qualified), not a literal
        // message ought to say identifier rather than ID
        parseAndCheckError("null instanceof T('a')", NOT_EXPECTED_TOKEN, 18, "qualified ID", "literal_string");
    }
}

