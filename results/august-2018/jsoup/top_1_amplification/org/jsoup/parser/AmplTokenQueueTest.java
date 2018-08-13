package org.jsoup.parser;


import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Test;


public class AmplTokenQueueTest {
    @Test(timeout = 10000)
    public void chompBalanced_mg1717_failAssert13() throws Exception {
        try {
            String __DSPOT_seq_126 = "+bBYGG!0gGi?=}tR?SY{";
            TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            tq.consume(__DSPOT_seq_126);
            org.junit.Assert.fail("chompBalanced_mg1717 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedlitChar1704_failAssert9() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', '*');
            String remainder = tq.remainder();
            org.junit.Assert.fail("chompBalancedlitChar1704 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced marker at \'one (two) three) four\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalanced_mg1716_failAssert12() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            tq.consume();
            org.junit.Assert.fail("chompBalanced_mg1716 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 31", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalanced_mg16456_failAssert32() throws Exception {
        try {
            String __DSPOT_seq_3416 = "  DC2o[vAK1v]swuA9t-";
            TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            TokenQueue.unescape(guts);
            tq.consume(__DSPOT_seq_3416);
            org.junit.Assert.fail("chompEscapedBalanced_mg16456 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalancedlitChar16441_failAssert27() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', 'k');
            String remainder = tq.remainder();
            TokenQueue.unescape(guts);
            org.junit.Assert.fail("chompEscapedBalancedlitChar16441 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced marker at \'one (two) \\( \\) \\) three) four\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalancednull16498_failAssert36() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            TokenQueue.unescape(null);
            org.junit.Assert.fail("chompEscapedBalancednull16498 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalanced_mg16455_failAssert31() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            TokenQueue.unescape(guts);
            tq.consume();
            org.junit.Assert.fail("chompEscapedBalanced_mg16455 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 40", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedMatchesAsMuchAsPossiblelitChar3346_failAssert18() throws Exception {
        try {
            TokenQueue tq = new TokenQueue("unbalanced(something(or another)) else");
            tq.consumeTo("(");
            String match = tq.chompBalanced('(', 'I');
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossiblelitChar3346 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced marker at \'something(or another)) else\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedMatchesAsMuchAsPossible_mg3360_failAssert22() throws Exception {
        try {
            String __DSPOT_seq_290 = "S^{}YexJ,;oHv!Lb^R/C";
            TokenQueue tq = new TokenQueue("unbalanced(something(or another)) else");
            tq.consumeTo("(");
            String match = tq.chompBalanced('(', ')');
            tq.consume(__DSPOT_seq_290);
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossible_mg3360 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void unescapelitString30409() throws Exception {
        String o_unescapelitString30409__1 = TokenQueue.unescape("[+1`4[0.,]]>");
        Assert.assertEquals("[+1`4[0.,]]>", o_unescapelitString30409__1);
    }

    @Test(timeout = 10000)
    public void unescape_add30413null30545_failAssert61() throws Exception {
        try {
            String o_unescape_add30413__1 = TokenQueue.unescape(null);
            String o_unescape_add30413__2 = TokenQueue.unescape("one \\( \\) \\\\");
            org.junit.Assert.fail("unescape_add30413null30545 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void unescapenull30415null30509null31020_failAssert71() throws Exception {
        try {
            String o_unescapenull30415__1 = TokenQueue.unescape(null);
            org.junit.Assert.fail("unescapenull30415null30509null31020 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void unescape_add30413_add30542null31946() throws Exception {
        String o_unescape_add30413__1 = TokenQueue.unescape("one \\( \\) \\\\");
        Assert.assertEquals("one ( ) \\", o_unescape_add30413__1);
        String o_unescape_add30413_add30542__4 = TokenQueue.unescape("one \\( \\) \\\\");
        Assert.assertEquals("one ( ) \\", o_unescape_add30413_add30542__4);
        String o_unescape_add30413__2 = TokenQueue.unescape("one \\( \\) \\\\");
        Assert.assertEquals("one ( ) \\", o_unescape_add30413__2);
        Assert.assertEquals("one ( ) \\", o_unescape_add30413__1);
        Assert.assertEquals("one ( ) \\", o_unescape_add30413_add30542__4);
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCasenull18344_failAssert42() throws Exception {
        try {
            String t = null;
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("</textarea");
            tq = new TokenQueue("<textarea> one two < three </oops>");
            data = tq.chompToIgnoreCase("</textarea");
            org.junit.Assert.fail("chompToIgnoreCasenull18344 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCase_mg18322_failAssert41() throws Exception {
        try {
            String __DSPOT_seq_3625 = "`|w7b+j;20cIP$F;5GDv";
            String t = "<textarea>one < two </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("</textarea");
            tq = new TokenQueue("<textarea> one two < three </oops>");
            data = tq.chompToIgnoreCase("</textarea");
            tq.consume(__DSPOT_seq_3625);
            org.junit.Assert.fail("chompToIgnoreCase_mg18322 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCaselitString18294_failAssert38() throws Exception {
        try {
            String t = "<textarea>one < two </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("");
            tq = new TokenQueue("<textarea> one two < three </oops>");
            data = tq.chompToIgnoreCase("</textarea");
            org.junit.Assert.fail("chompToIgnoreCaselitString18294 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCasenull18353_failAssert45null18921_failAssert46() throws Exception {
        try {
            try {
                String t = null;
                TokenQueue tq = new TokenQueue(t);
                String data = tq.chompToIgnoreCase("</textarea");
                tq = new TokenQueue("<textarea> one two < three </oops>");
                data = tq.chompToIgnoreCase(null);
                org.junit.Assert.fail("chompToIgnoreCasenull18353 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("chompToIgnoreCasenull18353_failAssert45null18921 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Object must not be null", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void addFirst_mg28_failAssert0() throws Exception {
        try {
            TokenQueue tq = new TokenQueue("One Two");
            tq.consumeWord();
            tq.addFirst("Three");
            tq.remainder();
            tq.consume();
            org.junit.Assert.fail("addFirst_mg28 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 9", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void addFirst_mg29_failAssert1() throws Exception {
        try {
            String __DSPOT_seq_6 = "SO/woO!OKS@Rl&{ha!&B";
            TokenQueue tq = new TokenQueue("One Two");
            tq.consumeWord();
            tq.addFirst("Three");
            tq.remainder();
            tq.consume(__DSPOT_seq_6);
            org.junit.Assert.fail("addFirst_mg29 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void addFirst_mg29_failAssert1null395_failAssert6() throws Exception {
        try {
            try {
                String __DSPOT_seq_6 = "SO/woO!OKS@Rl&{ha!&B";
                TokenQueue tq = new TokenQueue("One Two");
                tq.consumeWord();
                tq.addFirst("Three");
                tq.remainder();
                tq.consume(null);
                org.junit.Assert.fail("addFirst_mg29 should have thrown IllegalStateException");
            } catch (IllegalStateException expected) {
            }
            org.junit.Assert.fail("addFirst_mg29_failAssert1null395 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals(null, expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToIgnoreSecondCallTestnull23483_failAssert53() throws Exception {
        try {
            String t = null;
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("</textarea>");
            data = tq.chompToIgnoreCase("</textarea>");
            org.junit.Assert.fail("consumeToIgnoreSecondCallTestnull23483 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToIgnoreSecondCallTestlitString23441_failAssert49() throws Exception {
        try {
            String t = "<textarea>one < two </TEXTarea> third </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("");
            data = tq.chompToIgnoreCase("</textarea>");
            org.junit.Assert.fail("consumeToIgnoreSecondCallTestlitString23441 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToIgnoreSecondCallTest_mg23461_failAssert52() throws Exception {
        try {
            String __DSPOT_seq_4608 = "SyXTXTJn8zWs.d&U=6mR";
            String t = "<textarea>one < two </TEXTarea> third </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("</textarea>");
            data = tq.chompToIgnoreCase("</textarea>");
            tq.consume(__DSPOT_seq_4608);
            org.junit.Assert.fail("consumeToIgnoreSecondCallTest_mg23461 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToIgnoreSecondCallTestnull23492_failAssert56null23984_failAssert58() throws Exception {
        try {
            try {
                String t = null;
                TokenQueue tq = new TokenQueue(t);
                String data = tq.chompToIgnoreCase("</textarea>");
                data = tq.chompToIgnoreCase(null);
                org.junit.Assert.fail("consumeToIgnoreSecondCallTestnull23492 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("consumeToIgnoreSecondCallTestnull23492_failAssert56null23984 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Object must not be null", expected_1.getMessage());
        }
    }

    private static void validateNestedQuotes(String html, String selector) {
        Assert.assertEquals("#identifier", Jsoup.parse(html).select(selector).first().cssSelector());
    }
}

