package org.jsoup.parser;


/**
 * Token queue tests.
 */
public class AmplTokenQueueTest {
    @org.junit.Test
    public void chompBalanced() {
        org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(":contains(one (two) three) four");
        java.lang.String pre = tq.consumeTo("(");
        java.lang.String guts = tq.chompBalanced('(', ')');
        java.lang.String remainder = tq.remainder();
        org.junit.Assert.assertEquals(":contains", pre);
        org.junit.Assert.assertEquals("one (two) three", guts);
        org.junit.Assert.assertEquals(" four", remainder);
    }

    @org.junit.Test
    public void chompEscapedBalanced() {
        org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(":contains(one (two) \\( \\) \\) three) four");
        java.lang.String pre = tq.consumeTo("(");
        java.lang.String guts = tq.chompBalanced('(', ')');
        java.lang.String remainder = tq.remainder();
        org.junit.Assert.assertEquals(":contains", pre);
        org.junit.Assert.assertEquals("one (two) \\( \\) \\) three", guts);
        org.junit.Assert.assertEquals("one (two) ( ) ) three", org.jsoup.parser.TokenQueue.unescape(guts));
        org.junit.Assert.assertEquals(" four", remainder);
    }

    @org.junit.Test
    public void chompBalancedMatchesAsMuchAsPossible() {
        org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue("unbalanced(something(or another)) else");
        tq.consumeTo("(");
        java.lang.String match = tq.chompBalanced('(', ')');
        org.junit.Assert.assertEquals("something(or another)", match);
    }

    @org.junit.Test
    public void unescape() {
        org.junit.Assert.assertEquals("one ( ) \\", org.jsoup.parser.TokenQueue.unescape("one \\( \\) \\\\"));
    }

    @org.junit.Test
    public void chompToIgnoreCase() {
        java.lang.String t = "<textarea>one < two </TEXTarea>";
        org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(t);
        java.lang.String data = tq.chompToIgnoreCase("</textarea");
        org.junit.Assert.assertEquals("<textarea>one < two ", data);
        tq = new org.jsoup.parser.TokenQueue("<textarea> one two < three </oops>");
        data = tq.chompToIgnoreCase("</textarea");
        org.junit.Assert.assertEquals("<textarea> one two < three </oops>", data);
    }

    @org.junit.Test
    public void addFirst() {
        org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue("One Two");
        tq.consumeWord();
        tq.addFirst("Three");
        org.junit.Assert.assertEquals("Three Two", tq.remainder());
    }
}

