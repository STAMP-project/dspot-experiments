package org.jsoup.parser;


import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Test;


public class AmplTokenQueueTest {
    @Test(timeout = 10000)
    public void chompBalancedlitChar6287_failAssert5() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', '(');
            String remainder = tq.remainder();
            org.junit.Assert.fail("chompBalancedlitChar6287 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced marker at \'one (two) three) four\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalanced_mg6299_failAssert8() throws Exception {
        try {
            String __DSPOT_seq_1667 = "_|&!vkH10LS+C(uj(}P`";
            TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            tq.consume(__DSPOT_seq_1667);
            org.junit.Assert.fail("chompBalanced_mg6299 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalanced_mg6298_failAssert7() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            tq.consume();
            org.junit.Assert.fail("chompBalanced_mg6298 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 31", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalanced_mg6298_failAssert7litChar6594_failAssert9() throws Exception {
        try {
            try {
                TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
                String pre = tq.consumeTo("(");
                String guts = tq.chompBalanced('(', '(');
                String remainder = tq.remainder();
                tq.consume();
                org.junit.Assert.fail("chompBalanced_mg6298 should have thrown StringIndexOutOfBoundsException");
            } catch (StringIndexOutOfBoundsException expected) {
            }
            org.junit.Assert.fail("chompBalanced_mg6298_failAssert7litChar6594 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Did not find balanced marker at \'one (two) three) four\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalanced_mg18936_failAssert28() throws Exception {
        try {
            String __DSPOT_seq_4965 = "o&9i!6)iM[F&dyeNpXuA";
            TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            TokenQueue.unescape(guts);
            tq.consume(__DSPOT_seq_4965);
            org.junit.Assert.fail("chompEscapedBalanced_mg18936 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalanced_mg18935_failAssert27() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            TokenQueue.unescape(guts);
            tq.consume();
            org.junit.Assert.fail("chompEscapedBalanced_mg18935 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 40", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalancedlitChar18921_failAssert23() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', '@');
            String remainder = tq.remainder();
            TokenQueue.unescape(guts);
            org.junit.Assert.fail("chompEscapedBalancedlitChar18921 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced marker at \'one (two) \\( \\) \\) three) four\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalanced_mg18935_failAssert27litChar19290_failAssert32() throws Exception {
        try {
            try {
                TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
                String pre = tq.consumeTo("(");
                String guts = tq.chompBalanced('(', 'w');
                String remainder = tq.remainder();
                TokenQueue.unescape(guts);
                tq.consume();
                org.junit.Assert.fail("chompEscapedBalanced_mg18935 should have thrown StringIndexOutOfBoundsException");
            } catch (StringIndexOutOfBoundsException expected) {
            }
            org.junit.Assert.fail("chompEscapedBalanced_mg18935_failAssert27litChar19290 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Did not find balanced marker at \'one (two) \\( \\) \\) three) four\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedMatchesAsMuchAsPossiblelitChar8207_failAssert16() throws Exception {
        try {
            TokenQueue tq = new TokenQueue("unbalanced(something(or another)) else");
            tq.consumeTo("(");
            String match = tq.chompBalanced('(', '+');
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossiblelitChar8207 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced marker at \'something(or another)) else\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedMatchesAsMuchAsPossiblelitString8186_failAssert14() throws Exception {
        try {
            TokenQueue tq = new TokenQueue("a[onclick*=\"(\'arg\"]");
            tq.consumeTo("(");
            String match = tq.chompBalanced('(', ')');
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossiblelitString8186 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced marker at \'\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedMatchesAsMuchAsPossible_mg8221_failAssert20() throws Exception {
        try {
            String __DSPOT_seq_1862 = "miz1<F(O>U|/e{f(mako";
            TokenQueue tq = new TokenQueue("unbalanced(something(or another)) else");
            tq.consumeTo("(");
            String match = tq.chompBalanced('(', ')');
            tq.consume(__DSPOT_seq_1862);
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossible_mg8221 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCaselitString20950_failAssert35() throws Exception {
        try {
            String t = "<textarea>one < two </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("");
            tq = new TokenQueue("<textarea> one two < three </oops>");
            data = tq.chompToIgnoreCase("</textarea");
            org.junit.Assert.fail("chompToIgnoreCaselitString20950 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCase_mg20978_failAssert38() throws Exception {
        try {
            String __DSPOT_seq_5190 = "0:r8jm#McTPH.1n/NMnm";
            String t = "<textarea>one < two </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("</textarea");
            tq = new TokenQueue("<textarea> one two < three </oops>");
            data = tq.chompToIgnoreCase("</textarea");
            tq.consume(__DSPOT_seq_5190);
            org.junit.Assert.fail("chompToIgnoreCase_mg20978 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
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
    public void consumeToIgnoreSecondCallTest_mg32456_failAssert42() throws Exception {
        try {
            String __DSPOT_seq_8235 = ".uI``GUGFz%GUHYlu32B";
            String t = "<textarea>one < two </TEXTarea> third </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("</textarea>");
            data = tq.chompToIgnoreCase("</textarea>");
            tq.consume(__DSPOT_seq_8235);
            org.junit.Assert.fail("consumeToIgnoreSecondCallTest_mg32456 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToIgnoreSecondCallTestlitString32436_failAssert39() throws Exception {
        try {
            String t = "<textarea>one < two </TEXTarea> third </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("");
            data = tq.chompToIgnoreCase("</textarea>");
            org.junit.Assert.fail("consumeToIgnoreSecondCallTestlitString32436 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToIgnoreSecondCallTest_mg32456_failAssert42litString32700_failAssert44() throws Exception {
        try {
            try {
                String __DSPOT_seq_8235 = ".uI``GUGFz%GUHYlu32B";
                String t = "<textarea>one < two </TEXTarea> third </TEXTarea>";
                TokenQueue tq = new TokenQueue(t);
                String data = tq.chompToIgnoreCase("</textarea>");
                data = tq.chompToIgnoreCase("");
                tq.consume(__DSPOT_seq_8235);
                org.junit.Assert.fail("consumeToIgnoreSecondCallTest_mg32456 should have thrown IllegalStateException");
            } catch (IllegalStateException expected) {
            }
            org.junit.Assert.fail("consumeToIgnoreSecondCallTest_mg32456_failAssert42litString32700 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected_1) {
            Assert.assertEquals("String index out of range: 1", expected_1.getMessage());
        }
    }

    private static void validateNestedQuotes(String html, String selector) {
        Assert.assertEquals("#identifier", Jsoup.parse(html).select(selector).first().cssSelector());
    }
}

