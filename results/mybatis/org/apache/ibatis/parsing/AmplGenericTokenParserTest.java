/**
 * Copyright 2009-2016 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


package org.apache.ibatis.parsing;


public class AmplGenericTokenParserTest {
    public static class VariableTokenHandler implements org.apache.ibatis.parsing.TokenHandler {
        private java.util.Map<java.lang.String, java.lang.String> variables = new java.util.HashMap<java.lang.String, java.lang.String>();

        public VariableTokenHandler(java.util.Map<java.lang.String, java.lang.String> variables) {
            this.variables = variables;
        }

        @java.lang.Override
        public java.lang.String handleToken(java.lang.String content) {
            return variables.get(content);
        }
    }

    @org.junit.Test
    public void shouldDemonstrateGenericTokenReplacement() {
        org.apache.ibatis.parsing.GenericTokenParser parser = new org.apache.ibatis.parsing.GenericTokenParser("${", "}", new org.apache.ibatis.parsing.AmplGenericTokenParserTest.VariableTokenHandler(new java.util.HashMap<java.lang.String, java.lang.String>() {
            {
                put("first_name", "James");
                put("initial", "T");
                put("last_name", "Kirk");
                put("var{with}brace", "Hiya");
                put("", "");
            }
        }));
        org.junit.Assert.assertEquals("James T Kirk reporting.", parser.parse("${first_name} ${initial} ${last_name} reporting."));
        org.junit.Assert.assertEquals("Hello captain James T Kirk", parser.parse("Hello captain ${first_name} ${initial} ${last_name}"));
        org.junit.Assert.assertEquals("James T Kirk", parser.parse("${first_name} ${initial} ${last_name}"));
        org.junit.Assert.assertEquals("JamesTKirk", parser.parse("${first_name}${initial}${last_name}"));
        org.junit.Assert.assertEquals("{}JamesTKirk", parser.parse("{}${first_name}${initial}${last_name}"));
        org.junit.Assert.assertEquals("}JamesTKirk", parser.parse("}${first_name}${initial}${last_name}"));
        org.junit.Assert.assertEquals("}James{{T}}Kirk", parser.parse("}${first_name}{{${initial}}}${last_name}"));
        org.junit.Assert.assertEquals("}James}T{Kirk", parser.parse("}${first_name}}${initial}{${last_name}"));
        org.junit.Assert.assertEquals("}James}T{Kirk", parser.parse("}${first_name}}${initial}{${last_name}"));
        org.junit.Assert.assertEquals("}James}T{Kirk{{}}", parser.parse("}${first_name}}${initial}{${last_name}{{}}"));
        org.junit.Assert.assertEquals("}James}T{Kirk{{}}", parser.parse("}${first_name}}${initial}{${last_name}{{}}${}"));
        org.junit.Assert.assertEquals("{$$something}JamesTKirk", parser.parse("{$$something}${first_name}${initial}${last_name}"));
        org.junit.Assert.assertEquals("${", parser.parse("${"));
        org.junit.Assert.assertEquals("${\\}", parser.parse("${\\}"));
        org.junit.Assert.assertEquals("Hiya", parser.parse("${var{with\\}brace}"));
        org.junit.Assert.assertEquals("", parser.parse("${}"));
        org.junit.Assert.assertEquals("}", parser.parse("}"));
        org.junit.Assert.assertEquals("Hello ${ this is a test.", parser.parse("Hello ${ this is a test."));
        org.junit.Assert.assertEquals("Hello } this is a test.", parser.parse("Hello } this is a test."));
        org.junit.Assert.assertEquals("Hello } ${ this is a test.", parser.parse("Hello } ${ this is a test."));
    }

    @org.junit.Test
    public void shallNotInterpolateSkippedVaiables() {
        org.apache.ibatis.parsing.GenericTokenParser parser = new org.apache.ibatis.parsing.GenericTokenParser("${", "}", new org.apache.ibatis.parsing.AmplGenericTokenParserTest.VariableTokenHandler(new java.util.HashMap<java.lang.String, java.lang.String>()));
        org.junit.Assert.assertEquals("${skipped} variable", parser.parse("\\${skipped} variable"));
        org.junit.Assert.assertEquals("This is a ${skipped} variable", parser.parse("This is a \\${skipped} variable"));
        org.junit.Assert.assertEquals("null ${skipped} variable", parser.parse("${skipped} \\${skipped} variable"));
        org.junit.Assert.assertEquals("The null is ${skipped} variable", parser.parse("The ${skipped} is \\${skipped} variable"));
    }

    @org.junit.Ignore(value = "Because it randomly fails on Travis CI. It could be useful during development.")
    @org.junit.Test(timeout = 1000)
    public void shouldParseFastOnJdk7u6() {
        // issue #760
        org.apache.ibatis.parsing.GenericTokenParser parser = new org.apache.ibatis.parsing.GenericTokenParser("${", "}", new org.apache.ibatis.parsing.AmplGenericTokenParserTest.VariableTokenHandler(new java.util.HashMap<java.lang.String, java.lang.String>() {
            {
                put("first_name", "James");
                put("initial", "T");
                put("last_name", "Kirk");
                put("", "");
            }
        }));
        java.lang.StringBuilder input = new java.lang.StringBuilder();
        for (int i = 0; i < 10000; i++) {
            input.append("${first_name} ${initial} ${last_name} reporting. ");
        }
        java.lang.StringBuilder expected = new java.lang.StringBuilder();
        for (int i = 0; i < 10000; i++) {
            expected.append("James T Kirk reporting. ");
        }
        org.junit.Assert.assertEquals(expected.toString(), parser.parse(input.toString()));
    }
}

