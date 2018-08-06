package org.jsoup.parser;


import org.junit.Assert;
import org.junit.Test;


public class AmplTokenQueueTest {
    @Test(timeout = 10000)
    public void chompBalanced_mg1718_failAssert13() throws Exception {
        try {
            String __DSPOT_seq_126 = ";+kVD6&G)ynZ< gd.usM";
            TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            tq.consume(__DSPOT_seq_126);
            org.junit.Assert.fail("chompBalanced_mg1718 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalanced_mg1717_failAssert12() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            tq.consume();
            org.junit.Assert.fail("chompBalanced_mg1717 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 31", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedlitChar1706_failAssert10() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', '(');
            String remainder = tq.remainder();
            org.junit.Assert.fail("chompBalancedlitChar1706 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced maker at one (two) three) four", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalancedlitChar16514_failAssert30() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', '(');
            String remainder = tq.remainder();
            TokenQueue.unescape(guts);
            org.junit.Assert.fail("chompEscapedBalancedlitChar16514 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced maker at one (two) \\( \\) \\) three) four", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalanced_mg16526_failAssert32() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            TokenQueue.unescape(guts);
            tq.consume();
            org.junit.Assert.fail("chompEscapedBalanced_mg16526 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 40", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalanced_mg16527_failAssert33() throws Exception {
        try {
            String __DSPOT_seq_3408 = "0&(eX*_]^ekXtaZH(e@a";
            TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            TokenQueue.unescape(guts);
            tq.consume(__DSPOT_seq_3408);
            org.junit.Assert.fail("chompEscapedBalanced_mg16527 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalancedlitString16495_failAssert26() throws Exception {
        try {
            TokenQueue tq = new TokenQueue("PI:6<p5CuChQcidUqJ6bS]gJ$!(Lq#9=1{gt o.i");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            TokenQueue.unescape(guts);
            org.junit.Assert.fail("chompEscapedBalancedlitString16495 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced maker at Lq#9=1{gt o.i", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedMatchesAsMuchAsPossiblelitChar3345_failAssert17() throws Exception {
        try {
            TokenQueue tq = new TokenQueue("unbalanced(something(or another)) else");
            tq.consumeTo("(");
            String match = tq.chompBalanced('(', '\u0000');
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossiblelitChar3345 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced maker at something(or another)) else", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedMatchesAsMuchAsPossible_mg3361_failAssert22() throws Exception {
        try {
            String __DSPOT_seq_291 = "sEocWj+V2ku8d[w[zx>w";
            TokenQueue tq = new TokenQueue("unbalanced(something(or another)) else");
            tq.consumeTo("(");
            String match = tq.chompBalanced('(', ')');
            tq.consume(__DSPOT_seq_291);
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossible_mg3361 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void unescape() throws Exception {
        String o_unescape__1 = TokenQueue.unescape("one \\( \\) \\\\");
        Assert.assertEquals("one ( ) \\", o_unescape__1);
    }

    @Test(timeout = 10000)
    public void unescapelitString23817() throws Exception {
        String o_unescapelitString23817__1 = TokenQueue.unescape("&f]#,;mG8:Zf");
        Assert.assertEquals("&f]#,;mG8:Zf", o_unescapelitString23817__1);
    }

    @Test(timeout = 10000)
    public void unescapelitString23817null23919() throws Exception {
        String o_unescapelitString23817__1 = TokenQueue.unescape("&f]#,;mG8:Zf");
        Assert.assertEquals("&f]#,;mG8:Zf", o_unescapelitString23817__1);
    }

    @Test(timeout = 10000)
    public void unescapelitString23817_add23911_add24270() throws Exception {
        String o_unescapelitString23817__1 = TokenQueue.unescape("&f]#,;mG8:Zf");
        Assert.assertEquals("&f]#,;mG8:Zf", o_unescapelitString23817__1);
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCase_mg18701_failAssert42() throws Exception {
        try {
            String t = "<textarea>one < two </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("</textarea");
            tq = new TokenQueue("<textarea> one two < three </oops>");
            data = tq.chompToIgnoreCase("</textarea");
            tq.consume();
            org.junit.Assert.fail("chompToIgnoreCase_mg18701 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 34", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCasenull18724_failAssert44() throws Exception {
        try {
            String t = null;
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("</textarea");
            tq = new TokenQueue("<textarea> one two < three </oops>");
            data = tq.chompToIgnoreCase("</textarea");
            org.junit.Assert.fail("chompToIgnoreCasenull18724 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCase_mg18702_failAssert43() throws Exception {
        try {
            String __DSPOT_seq_3638 = "CxL=hfZ_#|4GBFnLWrkh";
            String t = "<textarea>one < two </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("</textarea");
            tq = new TokenQueue("<textarea> one two < three </oops>");
            data = tq.chompToIgnoreCase("</textarea");
            tq.consume(__DSPOT_seq_3638);
            org.junit.Assert.fail("chompToIgnoreCase_mg18702 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCaselitString18674_failAssert40() throws Exception {
        try {
            String t = "<textarea>one < two </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("");
            tq = new TokenQueue("<textarea> one two < three </oops>");
            data = tq.chompToIgnoreCase("</textarea");
            org.junit.Assert.fail("chompToIgnoreCaselitString18674 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCasenull18733_failAssert47null19403_failAssert48() throws Exception {
        try {
            try {
                String t = null;
                TokenQueue tq = new TokenQueue(t);
                String data = tq.chompToIgnoreCase("</textarea");
                tq = new TokenQueue("<textarea> one two < three </oops>");
                data = tq.chompToIgnoreCase(null);
                org.junit.Assert.fail("chompToIgnoreCasenull18733 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("chompToIgnoreCasenull18733_failAssert47null19403 should have thrown IllegalArgumentException");
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
}

