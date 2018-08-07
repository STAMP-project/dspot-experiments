package org.jsoup.parser;


import org.jsoup.Jsoup;
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
            Assert.fail("chompBalanced_mg6283 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalanced_mg6259_failAssert8() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            tq.consume();
            org.junit.Assert.fail("chompBalanced_mg6259 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 31", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalanced_mg6259_failAssert8litChar6639_failAssert11() throws Exception {
        try {
            try {
                TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
                String pre = tq.consumeTo("(");
                String guts = tq.chompBalanced('(', '*');
                String remainder = tq.remainder();
                tq.consume();
                org.junit.Assert.fail("chompBalanced_mg6259 should have thrown StringIndexOutOfBoundsException");
            } catch (StringIndexOutOfBoundsException expected) {
            }
            org.junit.Assert.fail("chompBalanced_mg6259_failAssert8litChar6639 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Did not find balanced marker at \'one (two) three) four\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalanced_mg6260_failAssert9() throws Exception {
        try {
            String __DSPOT_seq_1659 = "go$1Z&L!Q;zP,DDw*5:2";
            TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            tq.consume(__DSPOT_seq_1659);
            org.junit.Assert.fail("chompBalanced_mg6260 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedlitChar6244_failAssert3() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', '\u0000');
            String remainder = tq.remainder();
            org.junit.Assert.fail("chompBalancedlitChar6244 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced marker at \'one (two) three) four\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalanced_mg18777_failAssert28() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            TokenQueue.unescape(guts);
            tq.consume();
            org.junit.Assert.fail("chompEscapedBalanced_mg18777 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 40", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalanced_mg18777_failAssert28litChar19098_failAssert34() throws Exception {
        try {
            try {
                TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
                String pre = tq.consumeTo("(");
                String guts = tq.chompBalanced('(', '\n');
                String remainder = tq.remainder();
                TokenQueue.unescape(guts);
                tq.consume();
                org.junit.Assert.fail("chompEscapedBalanced_mg18777 should have thrown StringIndexOutOfBoundsException");
            } catch (StringIndexOutOfBoundsException expected) {
            }
            org.junit.Assert.fail("chompEscapedBalanced_mg18777_failAssert28litChar19098 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Did not find balanced marker at \'one (two) \\( \\) \\) three) four\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalanced_mg18777_failAssert28litString18975_failAssert30() throws Exception {
        try {
            try {
                TokenQueue tq = new TokenQueue("one \\( \\) \\\\");
                String pre = tq.consumeTo("(");
                String guts = tq.chompBalanced('(', ')');
                String remainder = tq.remainder();
                TokenQueue.unescape(guts);
                tq.consume();
                org.junit.Assert.fail("chompEscapedBalanced_mg18777 should have thrown StringIndexOutOfBoundsException");
            } catch (StringIndexOutOfBoundsException expected) {
            }
            org.junit.Assert.fail("chompEscapedBalanced_mg18777_failAssert28litString18975 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Did not find balanced marker at \' \\) \\\\\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalanced_mg18778_failAssert29() throws Exception {
        try {
            String __DSPOT_seq_4953 = "H;l:fHA>cTR|&#7ckTE*";
            TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            TokenQueue.unescape(guts);
            tq.consume(__DSPOT_seq_4953);
            org.junit.Assert.fail("chompEscapedBalanced_mg18778 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalancedlitChar18766_failAssert27() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', '\n');
            String remainder = tq.remainder();
            TokenQueue.unescape(guts);
            org.junit.Assert.fail("chompEscapedBalancedlitChar18766 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced marker at \'one (two) \\( \\) \\) three) four\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalancedlitString18746_failAssert22() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(";Or2UFO!`>8;>=?Sc7D?YWD(k=K.lNjTVEw2(N_H");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            TokenQueue.unescape(guts);
            org.junit.Assert.fail("chompEscapedBalancedlitString18746 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced marker at \'k=K.lNjTVEw2(N_H\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedlitString6248_failAssert2() throws Exception {
        try {
            TokenQueue tq = new TokenQueue("(");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            Assert.fail("chompBalancedlitString6248 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced maker at ", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedMatchesAsMuchAsPossible_mg8244_failAssert21() throws Exception {
        try {
            String __DSPOT_seq_1864 = "NV5:Zl@mz|DEwGD}[>9G";
            TokenQueue tq = new TokenQueue("unbalanced(something(or another)) else");
            tq.consumeTo("(");
            String match = tq.chompBalanced('(', ')');
            tq.consume(__DSPOT_seq_1864);
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossible_mg8244 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedMatchesAsMuchAsPossiblelitChar8232_failAssert19() throws Exception {
        try {
            TokenQueue tq = new TokenQueue("unbalanced(something(or another)) else");
            tq.consumeTo("(");
            String match = tq.chompBalanced('(', '(');
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossiblelitChar8232 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced marker at \'something(or another)) else\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedMatchesAsMuchAsPossiblelitString8213_failAssert15() throws Exception {
        try {
            TokenQueue tq = new TokenQueue("WjS5R$S -#V9-(z-(sJ@VGi=O$Y8HSYDjKXkXE");
            tq.consumeTo("(");
            String match = tq.chompBalanced('(', ')');
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossiblelitString8213 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced marker at \'z-(sJ@VGi=O$Y8HSYDjKXkXE\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void unescape_add33457_add33501litString33557() throws Exception {
        String o_unescape_add33457__1 = TokenQueue.unescape("P!z7)P]MH.Eq");
        Assert.assertEquals("P!z7)P]MH.Eq", o_unescape_add33457__1);
    }

    @Test(timeout = 10000)
    public void unescape_add33457_add33501litString33558() throws Exception {
        String o_unescape_add33457__1 = TokenQueue.unescape("");
        Assert.assertEquals("", o_unescape_add33457__1);
    }

    @Test(timeout = 10000)
    public void unescape_add33457litString33487_add33561() throws Exception {
        String o_unescape_add33457__1 = TokenQueue.unescape("one \\(- \\) \\\\");
        Assert.assertEquals("one (- ) \\", o_unescape_add33457__1);
    }

    @Test(timeout = 10000)
    public void unescape_add33457litString33490() throws Exception {
        String o_unescape_add33457__1 = TokenQueue.unescape("");
        Assert.assertEquals("", o_unescape_add33457__1);
    }

    @Test(timeout = 10000)
    public void unescape_add33457litString33491() throws Exception {
        String o_unescape_add33457__1 = TokenQueue.unescape("\n");
        Assert.assertEquals("\n", o_unescape_add33457__1);
    }

    @Test(timeout = 10000)
    public void unescape_add33457litString33492() throws Exception {
        String o_unescape_add33457__1 = TokenQueue.unescape(":");
        Assert.assertEquals(":", o_unescape_add33457__1);
    }

    @Test(timeout = 10000)
    public void unescapelitString33449() throws Exception {
        String o_unescapelitString33449__1 = TokenQueue.unescape("<html><body><a id=\"identifier\" onclick=\'func(\"arg\")\' /></body></html>");
        Assert.assertEquals("<html><body><a id=\"identifier\" onclick=\'func(\"arg\")\' /></body></html>", o_unescapelitString33449__1);
    }

    @Test(timeout = 10000)
    public void unescapelitString33450() throws Exception {
        String o_unescapelitString33450__1 = TokenQueue.unescape("one \\(p\\) \\\\");
        Assert.assertEquals("one (p) \\", o_unescapelitString33450__1);
    }

    @Test(timeout = 10000)
    public void unescapelitString33451_add33493() throws Exception {
        String o_unescapelitString33451__1 = TokenQueue.unescape("one \\( Z\\) \\\\");
        Assert.assertEquals("one ( Z) \\", o_unescapelitString33451__1);
    }

    @Test(timeout = 10000)
    public void unescapelitString33455() throws Exception {
        String o_unescapelitString33455__1 = TokenQueue.unescape("\n");
        Assert.assertEquals("\n", o_unescapelitString33455__1);
    }

    @Test(timeout = 10000)
    public void chompBalancedlitChar6267_failAssert3() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', '\u0000');
            String remainder = tq.remainder();
            Assert.fail("chompBalancedlitChar6267 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced maker at one (two) three) four", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCase_mg20820_failAssert38() throws Exception {
        try {
            String t = "<textarea>one < two </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("</textarea");
            tq = new TokenQueue("<textarea> one two < three </oops>");
            data = tq.chompToIgnoreCase("</textarea");
            tq.consume();
            org.junit.Assert.fail("chompToIgnoreCase_mg20820 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 34", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCase_mg20821_failAssert39() throws Exception {
        try {
            String __DSPOT_seq_5178 = "*Kd1dr6@9u=bCX#aCw<#";
            String t = "<textarea>one < two </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("</textarea");
            tq = new TokenQueue("<textarea> one two < three </oops>");
            data = tq.chompToIgnoreCase("</textarea");
            tq.consume(__DSPOT_seq_5178);
            org.junit.Assert.fail("chompToIgnoreCase_mg20821 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCaselitString20793_failAssert36() throws Exception {
        try {
            String t = "<textarea>one < two </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("");
            tq = new TokenQueue("<textarea> one two < three </oops>");
            data = tq.chompToIgnoreCase("</textarea");
            org.junit.Assert.fail("chompToIgnoreCaselitString20793 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 1", expected.getMessage());
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
            Assert.fail("chompBalanced_mg6282 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 31", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToIgnoreSecondCallTest_mg32197_failAssert42() throws Exception {
        try {
            String t = "<textarea>one < two </TEXTarea> third </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("</textarea>");
            data = tq.chompToIgnoreCase("</textarea>");
            tq.consume();
            org.junit.Assert.fail("consumeToIgnoreSecondCallTest_mg32197 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 49", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToIgnoreSecondCallTest_mg32198_failAssert43() throws Exception {
        try {
            String __DSPOT_seq_8226 = "#@,#H+KigT_Uqy:TxRZ<";
            String t = "<textarea>one < two </TEXTarea> third </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("</textarea>");
            data = tq.chompToIgnoreCase("</textarea>");
            tq.consume(__DSPOT_seq_8226);
            org.junit.Assert.fail("consumeToIgnoreSecondCallTest_mg32198 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToIgnoreSecondCallTest_mg32198_failAssert43litString32442_failAssert45() throws Exception {
        try {
            try {
                String __DSPOT_seq_8226 = "#@,#H+KigT_Uqy:TxRZ<";
                String t = "<textarea>one < two </TEXTarea> third </TEXTarea>";
                TokenQueue tq = new TokenQueue(t);
                String data = tq.chompToIgnoreCase("</textarea>");
                data = tq.chompToIgnoreCase("");
                tq.consume(__DSPOT_seq_8226);
                org.junit.Assert.fail("consumeToIgnoreSecondCallTest_mg32198 should have thrown IllegalStateException");
            } catch (IllegalStateException expected) {
            }
            org.junit.Assert.fail("consumeToIgnoreSecondCallTest_mg32198_failAssert43litString32442 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected_1) {
            Assert.assertEquals("String index out of range: 1", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToIgnoreSecondCallTestlitString32178_failAssert40() throws Exception {
        try {
            String t = "<textarea>one < two </TEXTarea> third </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("");
            data = tq.chompToIgnoreCase("</textarea>");
            org.junit.Assert.fail("consumeToIgnoreSecondCallTestlitString32178 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 1", expected.getMessage());
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
                Assert.fail("chompBalanced_mg6282 should have thrown StringIndexOutOfBoundsException");
            } catch (StringIndexOutOfBoundsException expected) {
            }
            Assert.fail("chompBalanced_mg6282_failAssert8litString6526 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Did not find balanced maker at :D?n", expected_1.getMessage());
        }
    }

    private static void validateNestedQuotes(String html, String selector) {
        Assert.assertEquals("#identifier", Jsoup.parse(html).select(selector).first().cssSelector());
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
            Assert.fail("chompEscapedBalanced_mg18784 should have thrown StringIndexOutOfBoundsException");
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
            Assert.fail("chompEscapedBalanced_mg18785 should have thrown IllegalStateException");
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
            Assert.fail("chompEscapedBalancedlitChar18768 should have thrown IllegalArgumentException");
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
                Assert.fail("chompEscapedBalanced_mg18784 should have thrown StringIndexOutOfBoundsException");
            } catch (StringIndexOutOfBoundsException expected) {
            }
            Assert.fail("chompEscapedBalanced_mg18784_failAssert25litChar19090 should have thrown IllegalArgumentException");
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
            Assert.fail("chompBalancedMatchesAsMuchAsPossible_mg8195 should have thrown IllegalStateException");
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
            Assert.fail("chompBalancedMatchesAsMuchAsPossiblelitChar8181 should have thrown IllegalArgumentException");
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
            Assert.fail("chompToIgnoreCase_mg20734 should have thrown IllegalStateException");
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
            Assert.fail("chompToIgnoreCase_mg20733 should have thrown StringIndexOutOfBoundsException");
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
            Assert.fail("chompToIgnoreCaselitString20706 should have thrown StringIndexOutOfBoundsException");
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
            Assert.fail("addFirst_mg28 should have thrown StringIndexOutOfBoundsException");
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
            Assert.fail("addFirst_mg29 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }
}

