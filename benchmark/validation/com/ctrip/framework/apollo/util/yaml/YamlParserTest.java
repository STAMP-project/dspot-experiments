package com.ctrip.framework.apollo.util.yaml;


import org.junit.Test;
import org.yaml.snakeyaml.parser.ParserException;


public class YamlParserTest {
    private YamlParser parser = new YamlParser();

    @Test
    public void testValidCases() throws Exception {
        test("case1.yaml");
        test("case3.yaml");
        test("case4.yaml");
        test("case5.yaml");
        test("case6.yaml");
        test("case7.yaml");
    }

    @Test(expected = ParserException.class)
    public void testcase2() throws Exception {
        testInvalid("case2.yaml");
    }

    @Test(expected = ParserException.class)
    public void testcase8() throws Exception {
        testInvalid("case8.yaml");
    }
}

