/**
 * *****************************************************************************
 * Copyright (c) 2017
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package smile.symbolic;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ernest DeFoy
 */
public class ExpressionParserTest {
    private ExpressionParser parser = new ExpressionParser();

    @Test
    public void testParse() throws InvalidExpressionException {
        final String QUERY = "-5 + 7((x^2) + ((sin(x))";
        final String EXPECTED = "$5+7*((x^2)+((sin(x))))";
        parser.parse(QUERY);
        String actual = parser.getExpression();
        Assert.assertEquals(EXPECTED, actual);
    }

    @Test
    public void testTokenize() throws InvalidExpressionException {
        final String QUERY = "(5+x) / sin(x)";
        final String[] EXPECTED = new String[]{ "5", "x", "+", "x", "sin", "/" };
        parser.parse(QUERY);
        final String[] actual = parser.getTokens().toArray(new String[parser.getTokens().size()]);
        Assert.assertArrayEquals(EXPECTED, actual);
    }

    @Test
    public void testFormat() throws InvalidExpressionException {
        final String QUERY = "5x + 7*x^2";
        final String EXPECTED = "5x + 7x^2";
        parser.parse(QUERY);
        String actual = parser.format(QUERY);
        Assert.assertEquals(EXPECTED, actual);
    }
}

