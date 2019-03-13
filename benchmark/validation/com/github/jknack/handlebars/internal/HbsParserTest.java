package com.github.jknack.handlebars.internal;


import org.junit.Test;


public class HbsParserTest {
    private boolean printTokens = true;

    @Test
    public void hello() {
        parse("Hello {{who}}\n!");
    }

    @Test
    public void subExpr() {
        // below expression has unicode char with value: \u0001
        parse("{{datawithCtrlChar}}");
    }

    @Test
    public void rawblock() {
        parse("{{{{raw}}}} {{test}} {{{{/raw}}}}");
    }

    @Test
    public void dynamicPartial() {
        parse("{{> (partial)}}");
    }

    @Test
    public void text() {
        parse("Hello world!");
    }

    @Test
    public void newlines() {
        parse("Alan\'s\rTest");
    }

    @Test
    public void var() {
        parse("{{variable 678}}");
        parse("{{array.[10]}}");
        parse("{{array.[1foo]}}");
        parse("{{array.['foo']}}");
        parse("{{array.['foo or bar}}]}}");
        parse("{{variable \"string\"}}");
        parse("{{variable \"true\"}}");
        parse("{{variable \"string\" 78}}");
    }

    @Test
    public void comments() {
        parse("12345{{! Comment Block! }}67890");
    }

    @Test
    public void partial() {
        parse("[ {{>include}} ]");
    }

    @Test
    public void ampvar() {
        parse("{{&variable 678}}");
        parse("{{&variable \"string\"}}");
        parse("{{&variable \"true\"}}");
        parse("{{&variable \"string\" 78}}");
    }

    @Test
    public void tvar() {
        parse("{{{variable 678}}}");
        parse("{{{variable \"string\"}}}");
        parse("{{{variable \"true\"}}}");
        parse("{{{variable \"string\" 78}}}");
    }

    @Test
    public void block() {
        parse("{{#block 678}}{{var}}{{/block}}");
        parse("{{#block 678}}then{{^}}else{{/block}}");
        parse("{{#block 678}}then{{^}}else{{/block}}");
    }

    @Test
    public void unless() {
        parse("{{^block}}{{var}}{{/block}}");
    }

    @Test
    public void hash() {
        parse("{{variable int=678}}");
        parse("{{variable string='string'}}");
    }

    @Test
    public void setDelim() {
        parse("{{=<% %>=}}<%hello%><%={{ }}=%>{{reset}}");
        parse("{{= | | =}}<|#lambda|-|/lambda|>");
        parse("{{=+-+ +-+=}}+-+test+-+");
    }
}

