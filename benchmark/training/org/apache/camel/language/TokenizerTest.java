/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.language;


import java.util.List;
import org.apache.camel.ExchangeTestSupport;
import org.apache.camel.Expression;
import org.apache.camel.language.tokenizer.TokenizeLanguage;
import org.junit.Assert;
import org.junit.Test;


public class TokenizerTest extends ExchangeTestSupport {
    @Test
    public void testTokenizeHeader() throws Exception {
        Expression exp = TokenizeLanguage.tokenize("names", ",");
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(3, names.size());
        Assert.assertEquals("Claus", names.get(0));
        Assert.assertEquals("James", names.get(1));
        Assert.assertEquals("Willem", names.get(2));
    }

    @Test
    public void testTokenizeBody() throws Exception {
        Expression exp = TokenizeLanguage.tokenize(",");
        exchange.getIn().setBody("Hadrian,Charles");
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(2, names.size());
        Assert.assertEquals("Hadrian", names.get(0));
        Assert.assertEquals("Charles", names.get(1));
    }

    @Test
    public void testTokenizeBodyRegEx() throws Exception {
        Expression exp = TokenizeLanguage.tokenize("(\\W+)\\s*", true);
        exchange.getIn().setBody("The little fox");
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(3, names.size());
        Assert.assertEquals("The", names.get(0));
        Assert.assertEquals("little", names.get(1));
        Assert.assertEquals("fox", names.get(2));
    }

    @Test
    public void testTokenizeHeaderRegEx() throws Exception {
        Expression exp = TokenizeLanguage.tokenize("quote", "(\\W+)\\s*", true);
        exchange.getIn().setHeader("quote", "Camel rocks");
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(2, names.size());
        Assert.assertEquals("Camel", names.get(0));
        Assert.assertEquals("rocks", names.get(1));
    }

    @Test
    public void testTokenizeManualConfiguration() throws Exception {
        TokenizeLanguage lan = new TokenizeLanguage();
        lan.setHeaderName("names");
        lan.setRegex(false);
        lan.setToken(",");
        Expression exp = lan.createExpression();
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(3, names.size());
        Assert.assertEquals("Claus", names.get(0));
        Assert.assertEquals("James", names.get(1));
        Assert.assertEquals("Willem", names.get(2));
        Assert.assertEquals("names", lan.getHeaderName());
        Assert.assertEquals(",", lan.getToken());
        Assert.assertEquals(false, lan.isRegex());
        Assert.assertEquals(false, lan.isSingleton());
    }

    @Test
    public void testTokenizePairSpecial() throws Exception {
        Expression exp = TokenizeLanguage.tokenizePair("!", "@", false);
        exchange.getIn().setBody("2011-11-11\n!James@!Claus@\n2 records");
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(2, names.size());
        Assert.assertEquals("James", names.get(0));
        Assert.assertEquals("Claus", names.get(1));
    }

    @Test
    public void testTokenizePair() throws Exception {
        Expression exp = TokenizeLanguage.tokenizePair("[START]", "[END]", false);
        exchange.getIn().setBody("2011-11-11\n[START]James[END]\n[START]Claus[END]\n2 records");
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(2, names.size());
        Assert.assertEquals("James", names.get(0));
        Assert.assertEquals("Claus", names.get(1));
    }

    @Test
    public void testTokenizePairSimple() throws Exception {
        Expression exp = TokenizeLanguage.tokenizePair("${header.foo}", "${header.bar}", false);
        exchange.getIn().setHeader("foo", "[START]");
        exchange.getIn().setHeader("bar", "[END]");
        exchange.getIn().setBody("2011-11-11\n[START]James[END]\n[START]Claus[END]\n2 records");
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(2, names.size());
        Assert.assertEquals("James", names.get(0));
        Assert.assertEquals("Claus", names.get(1));
    }

    @Test
    public void testTokenizePairIncludeTokens() throws Exception {
        Expression exp = TokenizeLanguage.tokenizePair("[START]", "[END]", true);
        exchange.getIn().setBody("2011-11-11\n[START]James[END]\n[START]Claus[END]\n2 records");
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(2, names.size());
        Assert.assertEquals("[START]James[END]", names.get(0));
        Assert.assertEquals("[START]Claus[END]", names.get(1));
    }

    @Test
    public void testTokenizeXMLPair() throws Exception {
        Expression exp = TokenizeLanguage.tokenizeXML("<person>", null);
        exchange.getIn().setBody("<persons><person>James</person><person>Claus</person><person>Jonathan</person><person>Hadrian</person></persons>");
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(4, names.size());
        Assert.assertEquals("<person>James</person>", names.get(0));
        Assert.assertEquals("<person>Claus</person>", names.get(1));
        Assert.assertEquals("<person>Jonathan</person>", names.get(2));
        Assert.assertEquals("<person>Hadrian</person>", names.get(3));
    }

    @Test
    public void testTokenizeXMLPairSimple() throws Exception {
        Expression exp = TokenizeLanguage.tokenizeXML("${header.foo}", null);
        exchange.getIn().setHeader("foo", "<person>");
        exchange.getIn().setBody("<persons><person>James</person><person>Claus</person><person>Jonathan</person><person>Hadrian</person></persons>");
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(4, names.size());
        Assert.assertEquals("<person>James</person>", names.get(0));
        Assert.assertEquals("<person>Claus</person>", names.get(1));
        Assert.assertEquals("<person>Jonathan</person>", names.get(2));
        Assert.assertEquals("<person>Hadrian</person>", names.get(3));
    }

    @Test
    public void testTokenizeXMLPairNoXMLTag() throws Exception {
        Expression exp = TokenizeLanguage.tokenizeXML("person", null);
        exchange.getIn().setBody("<persons><person>James</person><person>Claus</person><person>Jonathan</person><person>Hadrian</person></persons>");
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(4, names.size());
        Assert.assertEquals("<person>James</person>", names.get(0));
        Assert.assertEquals("<person>Claus</person>", names.get(1));
        Assert.assertEquals("<person>Jonathan</person>", names.get(2));
        Assert.assertEquals("<person>Hadrian</person>", names.get(3));
    }

    @Test
    public void testTokenizeXMLPairWithNoise() throws Exception {
        Expression exp = TokenizeLanguage.tokenizeXML("<person>", null);
        exchange.getIn().setBody(("<?xml version=\"1.0\"?><!-- bla bla --><persons>\n<person>James</person>\n<person>Claus</person>\n" + "<!-- more bla bla --><person>Jonathan</person>\n<person>Hadrian</person>\n</persons>   "));
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(4, names.size());
        Assert.assertEquals("<person>James</person>", names.get(0));
        Assert.assertEquals("<person>Claus</person>", names.get(1));
        Assert.assertEquals("<person>Jonathan</person>", names.get(2));
        Assert.assertEquals("<person>Hadrian</person>", names.get(3));
    }

    @Test
    public void testTokenizeXMLPairEmpty() throws Exception {
        Expression exp = TokenizeLanguage.tokenizeXML("<person>", null);
        exchange.getIn().setBody("<?xml version=\"1.0\"?><!-- bla bla --><persons></persons>   ");
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(0, names.size());
    }

    @Test
    public void testTokenizeXMLPairNoData() throws Exception {
        Expression exp = TokenizeLanguage.tokenizeXML("<person>", null);
        exchange.getIn().setBody("");
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(0, names.size());
    }

    @Test
    public void testTokenizeXMLPairNullData() throws Exception {
        Expression exp = TokenizeLanguage.tokenizeXML("<person>", null);
        exchange.getIn().setBody(null);
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertNull(names);
    }

    @Test
    public void testTokenizeXMLPairWithSimilarChildNames() throws Exception {
        Expression exp = TokenizeLanguage.tokenizeXML("Trip", "Trips");
        exchange.getIn().setBody("<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n<Trips>\n<Trip>\n<TripType>\n</TripType>\n</Trip>\n</Trips>");
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(1, names.size());
    }

    @Test
    public void testTokenizeXMLPairWithDefaultNamespace() throws Exception {
        Expression exp = TokenizeLanguage.tokenizeXML("<person>", "<persons>");
        exchange.getIn().setBody(("<?xml version=\"1.0\"?><persons xmlns=\"http:acme.com/persons\">\n<person>James</person>\n<person>Claus</person>\n" + "<person>Jonathan</person>\n<person>Hadrian</person>\n</persons>\n"));
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(4, names.size());
        Assert.assertEquals("<person xmlns=\"http:acme.com/persons\">James</person>", names.get(0));
        Assert.assertEquals("<person xmlns=\"http:acme.com/persons\">Claus</person>", names.get(1));
        Assert.assertEquals("<person xmlns=\"http:acme.com/persons\">Jonathan</person>", names.get(2));
        Assert.assertEquals("<person xmlns=\"http:acme.com/persons\">Hadrian</person>", names.get(3));
    }

    @Test
    public void testTokenizeXMLPairWithDefaultNamespaceNotInherit() throws Exception {
        Expression exp = TokenizeLanguage.tokenizeXML("<person>", null);
        exchange.getIn().setBody(("<?xml version=\"1.0\"?><persons xmlns=\"http:acme.com/persons\">\n<person>James</person>\n<person>Claus</person>\n" + "<person>Jonathan</person>\n<person>Hadrian</person>\n</persons>\n"));
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(4, names.size());
        Assert.assertEquals("<person>James</person>", names.get(0));
        Assert.assertEquals("<person>Claus</person>", names.get(1));
        Assert.assertEquals("<person>Jonathan</person>", names.get(2));
        Assert.assertEquals("<person>Hadrian</person>", names.get(3));
    }

    @Test
    public void testTokenizeXMLPairWithDefaultAndFooNamespace() throws Exception {
        Expression exp = TokenizeLanguage.tokenizeXML("<person>", "<persons>");
        exchange.getIn().setBody(("<?xml version=\"1.0\"?><persons xmlns=\"http:acme.com/persons\" xmlns:foo=\"http:foo.com\">\n<person>James</person>\n<person>Claus</person>\n" + "<person>Jonathan</person>\n<person>Hadrian</person>\n</persons>\n"));
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(4, names.size());
        Assert.assertEquals("<person xmlns=\"http:acme.com/persons\" xmlns:foo=\"http:foo.com\">James</person>", names.get(0));
        Assert.assertEquals("<person xmlns=\"http:acme.com/persons\" xmlns:foo=\"http:foo.com\">Claus</person>", names.get(1));
        Assert.assertEquals("<person xmlns=\"http:acme.com/persons\" xmlns:foo=\"http:foo.com\">Jonathan</person>", names.get(2));
        Assert.assertEquals("<person xmlns=\"http:acme.com/persons\" xmlns:foo=\"http:foo.com\">Hadrian</person>", names.get(3));
    }

    @Test
    public void testTokenizeXMLPairWithLocalNamespace() throws Exception {
        Expression exp = TokenizeLanguage.tokenizeXML("<person>", null);
        exchange.getIn().setBody(("<?xml version=\"1.0\"?><persons>\n<person xmlns=\"http:acme.com/persons\">James</person>\n<person xmlns=\"http:acme.com/persons\">Claus</person>\n" + "<person xmlns=\"http:acme.com/persons\">Jonathan</person>\n<person xmlns=\"http:acme.com/persons\">Hadrian</person>\n</persons>\n"));
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(4, names.size());
        Assert.assertEquals("<person xmlns=\"http:acme.com/persons\">James</person>", names.get(0));
        Assert.assertEquals("<person xmlns=\"http:acme.com/persons\">Claus</person>", names.get(1));
        Assert.assertEquals("<person xmlns=\"http:acme.com/persons\">Jonathan</person>", names.get(2));
        Assert.assertEquals("<person xmlns=\"http:acme.com/persons\">Hadrian</person>", names.get(3));
    }

    @Test
    public void testTokenizeXMLPairWithLocalAndInheritedNamespace() throws Exception {
        Expression exp = TokenizeLanguage.tokenizeXML("<person>", "<persons>");
        exchange.getIn().setBody(("<?xml version=\"1.0\"?><persons xmlns=\"http:acme.com/persons\">\n<person xmlns:foo=\"http:foo.com\">James</person>\n<person>Claus</person>\n" + "<person>Jonathan</person>\n<person xmlns:bar=\"http:bar.com\">Hadrian</person>\n</persons>\n"));
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(4, names.size());
        Assert.assertEquals("<person xmlns:foo=\"http:foo.com\" xmlns=\"http:acme.com/persons\">James</person>", names.get(0));
        Assert.assertEquals("<person xmlns=\"http:acme.com/persons\">Claus</person>", names.get(1));
        Assert.assertEquals("<person xmlns=\"http:acme.com/persons\">Jonathan</person>", names.get(2));
        Assert.assertEquals("<person xmlns:bar=\"http:bar.com\" xmlns=\"http:acme.com/persons\">Hadrian</person>", names.get(3));
    }

    @Test
    public void testTokenizeXMLPairWithLocalAndNotInheritedNamespace() throws Exception {
        Expression exp = TokenizeLanguage.tokenizeXML("<person>", null);
        exchange.getIn().setBody(("<?xml version=\"1.0\"?><persons xmlns=\"http:acme.com/persons\">\n<person xmlns:foo=\"http:foo.com\">James</person>\n" + "<person>Claus</person>\n<person>Jonathan</person>\n<person xmlns:bar=\"http:bar.com\">Hadrian</person>\n</persons>\n"));
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(4, names.size());
        Assert.assertEquals("<person xmlns:foo=\"http:foo.com\">James</person>", names.get(0));
        Assert.assertEquals("<person>Claus</person>", names.get(1));
        Assert.assertEquals("<person>Jonathan</person>", names.get(2));
        Assert.assertEquals("<person xmlns:bar=\"http:bar.com\">Hadrian</person>", names.get(3));
    }

    @Test
    public void testTokenizeXMLPairWithAttributes() throws Exception {
        Expression exp = TokenizeLanguage.tokenizeXML("<person>", null);
        exchange.getIn().setBody(("<persons><person id=\"1\">James</person><person id=\"2\">Claus</person><person id=\"3\">Jonathan</person>" + "<person id=\"4\">Hadrian</person></persons>"));
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(4, names.size());
        Assert.assertEquals("<person id=\"1\">James</person>", names.get(0));
        Assert.assertEquals("<person id=\"2\">Claus</person>", names.get(1));
        Assert.assertEquals("<person id=\"3\">Jonathan</person>", names.get(2));
        Assert.assertEquals("<person id=\"4\">Hadrian</person>", names.get(3));
    }

    @Test
    public void testTokenizeXMLPairWithAttributesInheritNamespace() throws Exception {
        Expression exp = TokenizeLanguage.tokenizeXML("<person>", "<persons>");
        exchange.getIn().setBody(("<persons xmlns=\"http:acme.com/persons\"><person id=\"1\">James</person><person id=\"2\">Claus</person>" + "<person id=\"3\">Jonathan</person><person id=\"4\">Hadrian</person></persons>"));
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(4, names.size());
        Assert.assertEquals("<person id=\"1\" xmlns=\"http:acme.com/persons\">James</person>", names.get(0));
        Assert.assertEquals("<person id=\"2\" xmlns=\"http:acme.com/persons\">Claus</person>", names.get(1));
        Assert.assertEquals("<person id=\"3\" xmlns=\"http:acme.com/persons\">Jonathan</person>", names.get(2));
        Assert.assertEquals("<person id=\"4\" xmlns=\"http:acme.com/persons\">Hadrian</person>", names.get(3));
    }

    @Test
    public void testTokenizeXMLPairWithAttributes2InheritNamespace() throws Exception {
        Expression exp = TokenizeLanguage.tokenizeXML("<person>", "<persons>");
        exchange.getIn().setBody(("<persons riders=\"true\" xmlns=\"http:acme.com/persons\"><person id=\"1\">James</person><person id=\"2\">Claus</person>" + "<person id=\"3\">Jonathan</person><person id=\"4\">Hadrian</person></persons>"));
        List<?> names = exp.evaluate(exchange, List.class);
        Assert.assertEquals(4, names.size());
        Assert.assertEquals("<person id=\"1\" xmlns=\"http:acme.com/persons\">James</person>", names.get(0));
        Assert.assertEquals("<person id=\"2\" xmlns=\"http:acme.com/persons\">Claus</person>", names.get(1));
        Assert.assertEquals("<person id=\"3\" xmlns=\"http:acme.com/persons\">Jonathan</person>", names.get(2));
        Assert.assertEquals("<person id=\"4\" xmlns=\"http:acme.com/persons\">Hadrian</person>", names.get(3));
    }
}

