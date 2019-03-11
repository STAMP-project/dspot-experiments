package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import org.junit.Test;


public class Issue627 extends v4Test {
    @Test
    public void eq() throws Exception {
        shouldCompileTo("{{#eq 1 1}}yes{{/eq}}", v4Test.$, "yes");
        shouldCompileTo("{{#eq 1 2}}yes{{else}}no{{/eq}}", v4Test.$, "no");
        shouldCompileTo("{{#eq a b}}yes{{/eq}}", v4Test.$("hash", v4Test.$("a", "a", "b", "a")), "yes");
        // as expression
        shouldCompileTo("{{eq 1 1}}", v4Test.$, "true");
        shouldCompileTo("{{eq 1 0}}", v4Test.$, "false");
        shouldCompileTo("{{eq 1 1 yes='yes' no='no'}}", v4Test.$, "yes");
        shouldCompileTo("{{eq 1 0 yes='yes' no='no'}}", v4Test.$, "no");
        // as subexpression
        shouldCompileTo("{{#if (eq 1 1)}}yes{{/if}}", v4Test.$, "yes");
    }

    @Test
    public void neq() throws Exception {
        shouldCompileTo("{{#neq 1 1}}yes{{/neq}}", v4Test.$, "");
        shouldCompileTo("{{#neq 1 2}}yes{{else}}no{{/neq}}", v4Test.$, "yes");
        shouldCompileTo("{{#neq a b}}yes{{/neq}}", v4Test.$("hash", v4Test.$("a", "a", "b", "b")), "yes");
        // as expression
        shouldCompileTo("{{neq 1 1}}", v4Test.$, "false");
        shouldCompileTo("{{neq 1 0}}", v4Test.$, "true");
        shouldCompileTo("{{neq 1 1 yes='yes' no='no'}}", v4Test.$, "no");
        shouldCompileTo("{{neq 1 0 yes='yes' no='no'}}", v4Test.$, "yes");
        // as subexpression
        shouldCompileTo("{{#if (neq 1 2)}}yes{{/if}}", v4Test.$, "yes");
    }

    @Test
    public void gt() throws Exception {
        shouldCompileTo("{{#gt 2 1}}yes{{else}}no{{/gt}}", v4Test.$, "yes");
        shouldCompileTo("{{#gt 2 3}}yes{{else}}no{{/gt}}", v4Test.$, "no");
        shouldCompileTo("{{#gt 2 2}}yes{{else}}no{{/gt}}", v4Test.$, "no");
        shouldCompileTo("{{#gte 2 2}}yes{{else}}no{{/gte}}", v4Test.$, "yes");
        // as expression
        shouldCompileTo("{{gt 2 1}}", v4Test.$, "true");
        shouldCompileTo("{{gt 1 12}}", v4Test.$, "false");
        shouldCompileTo("{{gt 2 1 yes='y' no='n'}}", v4Test.$, "y");
        shouldCompileTo("{{gt 1 12 yes='y' no='n'}}", v4Test.$, "n");
        shouldCompileTo("{{gte 2 1 yes='y' no='n'}}", v4Test.$, "y");
        shouldCompileTo("{{gte 2 2 yes='y' no='n'}}", v4Test.$, "y");
        // as subexpression
        shouldCompileTo("{{#if (gte 2 1)}}yes{{/if}}", v4Test.$, "yes");
        shouldCompileTo("{{#if (gt 2 2)}}yes{{/if}}", v4Test.$, "");
        shouldCompileTo("{{#if (gte 2 2)}}yes{{/if}}", v4Test.$, "yes");
    }

    @Test
    public void lt() throws Exception {
        shouldCompileTo("{{#lt 2 1}}yes{{else}}no{{/lt}}", v4Test.$, "no");
        shouldCompileTo("{{#lt 2 3}}yes{{else}}no{{/lt}}", v4Test.$, "yes");
        shouldCompileTo("{{#lt 2 2}}yes{{else}}no{{/lt}}", v4Test.$, "no");
        shouldCompileTo("{{#lte 2 2}}yes{{else}}no{{/lte}}", v4Test.$, "yes");
        // as expression
        shouldCompileTo("{{lt 2 1}}", v4Test.$, "false");
        shouldCompileTo("{{lt 1 12}}", v4Test.$, "true");
        shouldCompileTo("{{lt 1 12 yes='y' no='n'}}", v4Test.$, "y");
        shouldCompileTo("{{lt 14 12 yes='y' no='n'}}", v4Test.$, "n");
        shouldCompileTo("{{lte 14 14 yes='y' no='n'}}", v4Test.$, "y");
        shouldCompileTo("{{lte 14 12 yes='y' no='n'}}", v4Test.$, "n");
        // as subexpression
        shouldCompileTo("{{#if (lte 2 1)}}yes{{/if}}", v4Test.$, "");
        shouldCompileTo("{{#if (lt 2 2)}}yes{{/if}}", v4Test.$, "");
        shouldCompileTo("{{#if (lte 2 2)}}yes{{/if}}", v4Test.$, "yes");
    }

    @Test
    public void and() throws Exception {
        shouldCompileTo("{{#and 2 1}}yes{{else}}no{{/and}}", v4Test.$, "yes");
        shouldCompileTo("{{#and 0 1}}yes{{else}}no{{/and}}", v4Test.$, "no");
        shouldCompileTo("{{#and '' ''}}yes{{else}}no{{/and}}", v4Test.$, "no");
        shouldCompileTo("{{#and 'a' 'b'}}yes{{else}}no{{/and}}", v4Test.$, "yes");
        shouldCompileTo("{{#and 'a' ''}}yes{{else}}no{{/and}}", v4Test.$, "no");
        // N args
        shouldCompileTo("{{#and 2 1 4 5 6}}yes{{else}}no{{/and}}", v4Test.$, "yes");
        shouldCompileTo("{{#and 0 1 4 5 6}}yes{{else}}no{{/and}}", v4Test.$, "no");
        shouldCompileTo("{{#and 1 0 4 5 6}}yes{{else}}no{{/and}}", v4Test.$, "no");
        // as expression
        shouldCompileTo("{{and 2 1}}", v4Test.$, "true");
        shouldCompileTo("{{and 2 1 yes='y'}}", v4Test.$, "y");
        shouldCompileTo("{{and 0 1 no='n'}}", v4Test.$, "n");
        // as subexpression
        shouldCompileTo("{{#if (and 2 1)}}yes{{/if}}", v4Test.$, "yes");
    }

    @Test
    public void or() throws Exception {
        shouldCompileTo("{{#or 2 1}}yes{{else}}no{{/or}}", v4Test.$, "yes");
        shouldCompileTo("{{#or 0 1}}yes{{else}}no{{/or}}", v4Test.$, "yes");
        shouldCompileTo("{{#or '' ''}}yes{{else}}no{{/or}}", v4Test.$, "no");
        shouldCompileTo("{{#or 'a' 'b'}}yes{{else}}no{{/or}}", v4Test.$, "yes");
        shouldCompileTo("{{#or 'a' ''}}yes{{else}}no{{/or}}", v4Test.$, "yes");
        // N args
        shouldCompileTo("{{#or 0 1 0}}yes{{else}}no{{/or}}", v4Test.$, "yes");
        shouldCompileTo("{{#or 0 0 1}}yes{{else}}no{{/or}}", v4Test.$, "yes");
        shouldCompileTo("{{#or 0 0 0 1}}yes{{else}}no{{/or}}", v4Test.$, "yes");
        shouldCompileTo("{{#or 0 0 0 0}}yes{{else}}no{{/or}}", v4Test.$, "no");
        // as expression
        shouldCompileTo("{{or 2 1}}", v4Test.$, "true");
        shouldCompileTo("{{or 0 1}}", v4Test.$, "true");
        shouldCompileTo("{{or 2 1 yes='y'}}", v4Test.$, "y");
        shouldCompileTo("{{or 0 0 no='n'}}", v4Test.$, "n");
        // as subexpression
        shouldCompileTo("{{#if (or 2 1)}}yes{{/if}}", v4Test.$, "yes");
    }

    @Test
    public void not() throws Exception {
        shouldCompileTo("{{#not true}}yes{{else}}no{{/not}}", v4Test.$, "no");
        shouldCompileTo("{{#not 1}}yes{{else}}no{{/not}}", v4Test.$, "no");
        shouldCompileTo("{{#not false}}yes{{else}}no{{/not}}", v4Test.$, "yes");
        shouldCompileTo("{{#not 0}}yes{{else}}no{{/not}}", v4Test.$, "yes");
        shouldCompileTo("{{#not list}}yes{{else}}no{{/not}}", v4Test.$("hash", v4Test.$("list", new Object[0])), "yes");
        // as expression
        shouldCompileTo("{{not false}}", v4Test.$, "true");
        shouldCompileTo("{{not false yes='y'}}", v4Test.$, "y");
        shouldCompileTo("{{not true no='n'}}", v4Test.$, "n");
        // as subexpression
        shouldCompileTo("{{#if (not false)}}yes{{/if}}", v4Test.$, "yes");
    }

    @Test
    public void complex() throws Exception {
        shouldCompileTo("{{#if (and a (or b c))}}yes{{else}}no{{/if}}", v4Test.$("hash", v4Test.$("a", true, "b", false, "c", true)), "yes");
        shouldCompileTo("{{#if (and a (or b c))}}yes{{else}}no{{/if}}", v4Test.$("hash", v4Test.$("a", true, "b", true, "c", false)), "yes");
        shouldCompileTo("{{#if (and a (or b c))}}yes{{else}}no{{/if}}", v4Test.$("hash", v4Test.$("a", true, "b", true, "c", true)), "yes");
        shouldCompileTo("{{#if (and a (or b c))}}yes{{else}}no{{/if}}", v4Test.$("hash", v4Test.$("a", true, "b", false, "c", false)), "no");
        shouldCompileTo("{{#if (and a (or b c))}}yes{{else}}no{{/if}}", v4Test.$("hash", v4Test.$("a", false, "b", false, "c", true)), "no");
    }
}

