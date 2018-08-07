package org.jsoup.parser;


import org.junit.Assert;
import org.junit.Test;


public class AmplTokenQueueTest {
    @Test(timeout = 10000)
    public void chompBalanced_mg6283_failAssert9() throws Exception {
        try {
            String __DSPOT_seq_1656 = "z9t?,u=Bgyu[8:3eAn!b";
            TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            tq.consume(__DSPOT_seq_1656);
            org.junit.Assert.fail("chompBalanced_mg6283 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedlitString6248_failAssert2() throws Exception {
        try {
            TokenQueue tq = new TokenQueue("(");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            org.junit.Assert.fail("chompBalancedlitString6248 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced maker at ", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedlitChar6267_failAssert3() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', '\u0000');
            String remainder = tq.remainder();
            org.junit.Assert.fail("chompBalancedlitChar6267 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced maker at one (two) three) four", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalanced_mg6282_failAssert8() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            tq.consume();
            org.junit.Assert.fail("chompBalanced_mg6282 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 31", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalanced_mg6282_failAssert8litString6526_failAssert10() throws Exception {
        try {
            try {
                TokenQueue tq = new TokenQueue("@s3,t.):!E9|IXa:KEcA5MSxI](:D?n");
                String pre = tq.consumeTo("(");
                String guts = tq.chompBalanced('(', ')');
                String remainder = tq.remainder();
                tq.consume();
                org.junit.Assert.fail("chompBalanced_mg6282 should have thrown StringIndexOutOfBoundsException");
            } catch (StringIndexOutOfBoundsException expected) {
            }
            org.junit.Assert.fail("chompBalanced_mg6282_failAssert8litString6526 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Did not find balanced maker at :D?n", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalanced_mg18784_failAssert25() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            TokenQueue.unescape(guts);
            tq.consume();
            org.junit.Assert.fail("chompEscapedBalanced_mg18784 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 40", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalanced_mg18785_failAssert26() throws Exception {
        try {
            String __DSPOT_seq_4925 = ",vc$#</-+L/_a&<! C)i";
            TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            TokenQueue.unescape(guts);
            tq.consume(__DSPOT_seq_4925);
            org.junit.Assert.fail("chompEscapedBalanced_mg18785 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalancedlitChar18768_failAssert20() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', '\u0000');
            String remainder = tq.remainder();
            TokenQueue.unescape(guts);
            org.junit.Assert.fail("chompEscapedBalancedlitChar18768 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced maker at one (two) \\( \\) \\) three) four", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalanced_mg18784_failAssert25litChar19090_failAssert27() throws Exception {
        try {
            try {
                TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
                String pre = tq.consumeTo("(");
                String guts = tq.chompBalanced('(', '(');
                String remainder = tq.remainder();
                TokenQueue.unescape(guts);
                tq.consume();
                org.junit.Assert.fail("chompEscapedBalanced_mg18784 should have thrown StringIndexOutOfBoundsException");
            } catch (StringIndexOutOfBoundsException expected) {
            }
            org.junit.Assert.fail("chompEscapedBalanced_mg18784_failAssert25litChar19090 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Did not find balanced maker at one (two) \\( \\) \\) three) four", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedMatchesAsMuchAsPossible_mg8195_failAssert19() throws Exception {
        try {
            String __DSPOT_seq_1846 = "bv4BRyS_e]-)clqbM@:b";
            TokenQueue tq = new TokenQueue("unbalanced(something(or another)) else");
            tq.consumeTo("(");
            String match = tq.chompBalanced('(', ')');
            tq.consume(__DSPOT_seq_1846);
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossible_mg8195 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedMatchesAsMuchAsPossiblelitChar8181_failAssert15() throws Exception {
        try {
            TokenQueue tq = new TokenQueue("unbalanced(something(or another)) else");
            tq.consumeTo("(");
            String match = tq.chompBalanced('(', '6');
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossiblelitChar8181 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced maker at something(or another)) else", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void unescape() throws Exception {
        String o_unescape__1 = TokenQueue.unescape("one \\( \\) \\\\");
        Assert.assertEquals("one ( ) \\", o_unescape__1);
    }

    @Test(timeout = 10000)
    public void unescapelitString32103() throws Exception {
        String o_unescapelitString32103__1 = TokenQueue.unescape(":");
        Assert.assertEquals(":", o_unescapelitString32103__1);
    }

    @Test(timeout = 10000)
    public void unescape_add32104litString32140() throws Exception {
        String o_unescape_add32104__1 = TokenQueue.unescape("\n");
        Assert.assertEquals("\n", o_unescape_add32104__1);
    }

    @Test(timeout = 10000)
    public void unescape_add32104litString32139() throws Exception {
        String o_unescape_add32104__1 = TokenQueue.unescape("");
        Assert.assertEquals("", o_unescape_add32104__1);
    }

    @Test(timeout = 10000)
    public void unescape_add32104_add32148litString32206() throws Exception {
        String o_unescape_add32104__1 = TokenQueue.unescape("");
        Assert.assertEquals("", o_unescape_add32104__1);
    }

    @Test(timeout = 10000)
    public void unescape_add32104_add32148litString32207() throws Exception {
        String o_unescape_add32104__1 = TokenQueue.unescape("\n");
        Assert.assertEquals("\n", o_unescape_add32104__1);
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCase_mg20734_failAssert35() throws Exception {
        try {
            String __DSPOT_seq_5120 = "B);HWR0R,rTc$>L`@]a{";
            String t = "<textarea>one < two </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("</textarea");
            tq = new TokenQueue("<textarea> one two < three </oops>");
            data = tq.chompToIgnoreCase("</textarea");
            tq.consume(__DSPOT_seq_5120);
            org.junit.Assert.fail("chompToIgnoreCase_mg20734 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCase_mg20733_failAssert34() throws Exception {
        try {
            String t = "<textarea>one < two </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("</textarea");
            tq = new TokenQueue("<textarea> one two < three </oops>");
            data = tq.chompToIgnoreCase("</textarea");
            tq.consume();
            org.junit.Assert.fail("chompToIgnoreCase_mg20733 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 34", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCaselitString20706_failAssert32() throws Exception {
        try {
            String t = "<textarea>one < two </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("");
            tq = new TokenQueue("<textarea> one two < three </oops>");
            data = tq.chompToIgnoreCase("</textarea");
            org.junit.Assert.fail("chompToIgnoreCaselitString20706 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 1", expected.getMessage());
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
}

