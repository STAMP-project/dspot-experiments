package org.jsoup.parser;


import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Test;


public class AmplTokenQueueTest {
    @Test(timeout = 10000)
    public void chompBalancedlitChar972_failAssert11() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', '*');
            String remainder = tq.remainder();
            org.junit.Assert.fail("chompBalancedlitChar972 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced marker at \'one (two) three) four\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalanced_mg985_failAssert7() throws Exception {
        try {
            String __DSPOT_seq_81 = "&sg2R8>3aX.)v8-E+,N[";
            TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            tq.consume(__DSPOT_seq_81);
            org.junit.Assert.fail("chompBalanced_mg985 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalanced_mg984_failAssert6() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            tq.consume();
            org.junit.Assert.fail("chompBalanced_mg984 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 31", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalanced_mg985_failAssert7null1600_failAssert16() throws Exception {
        try {
            try {
                String __DSPOT_seq_81 = "&sg2R8>3aX.)v8-E+,N[";
                TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
                String pre = tq.consumeTo("(");
                String guts = tq.chompBalanced('(', ')');
                String remainder = tq.remainder();
                tq.consume(null);
                org.junit.Assert.fail("chompBalanced_mg985 should have thrown IllegalStateException");
            } catch (IllegalStateException expected) {
            }
            org.junit.Assert.fail("chompBalanced_mg985_failAssert7null1600 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals(null, expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalanced_mg984_failAssert6litChar1303_failAssert23() throws Exception {
        try {
            try {
                TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
                String pre = tq.consumeTo("(");
                String guts = tq.chompBalanced('(', '\n');
                String remainder = tq.remainder();
                tq.consume();
                org.junit.Assert.fail("chompBalanced_mg984 should have thrown StringIndexOutOfBoundsException");
            } catch (StringIndexOutOfBoundsException expected) {
            }
            org.junit.Assert.fail("chompBalanced_mg984_failAssert6litChar1303 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Did not find balanced marker at \'one (two) three) four\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalanced_mg985_failAssert7null1598_failAssert25litChar2392_failAssert44() throws Exception {
        try {
            try {
                try {
                    String __DSPOT_seq_81 = null;
                    TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
                    String pre = tq.consumeTo("(");
                    String guts = tq.chompBalanced('(', '(');
                    String remainder = tq.remainder();
                    tq.consume(__DSPOT_seq_81);
                    org.junit.Assert.fail("chompBalanced_mg985 should have thrown IllegalStateException");
                } catch (IllegalStateException expected) {
                }
                org.junit.Assert.fail("chompBalanced_mg985_failAssert7null1598 should have thrown NullPointerException");
            } catch (NullPointerException expected_1) {
            }
            org.junit.Assert.fail("chompBalanced_mg985_failAssert7null1598_failAssert25litChar2392 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_2) {
            Assert.assertEquals("Did not find balanced marker at \'one (two) three) four\'", expected_2.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalancedlitChar5872_failAssert92() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', '\u0000');
            String remainder = tq.remainder();
            TokenQueue.unescape(guts);
            org.junit.Assert.fail("chompEscapedBalancedlitChar5872 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced marker at \'one (two) \\( \\) \\) three) four\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalanced_mg5889_failAssert86() throws Exception {
        try {
            String __DSPOT_seq_846 = "fQO)drdP!oCN,sn2Zk@A";
            TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            TokenQueue.unescape(guts);
            tq.consume(__DSPOT_seq_846);
            org.junit.Assert.fail("chompEscapedBalanced_mg5889 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalanced_mg5888_failAssert87() throws Exception {
        try {
            TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
            String pre = tq.consumeTo("(");
            String guts = tq.chompBalanced('(', ')');
            String remainder = tq.remainder();
            TokenQueue.unescape(guts);
            tq.consume();
            org.junit.Assert.fail("chompEscapedBalanced_mg5888 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 40", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalancednull5910_failAssert84litChar6199_failAssert104() throws Exception {
        try {
            try {
                TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
                String pre = tq.consumeTo("(");
                String guts = tq.chompBalanced('(', '*');
                String remainder = tq.remainder();
                TokenQueue.unescape(null);
                org.junit.Assert.fail("chompEscapedBalancednull5910 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("chompEscapedBalancednull5910_failAssert84litChar6199 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Did not find balanced marker at \'one (two) \\( \\) \\) three) four\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalanced_mg5889_failAssert86null6566_failAssert97() throws Exception {
        try {
            try {
                String __DSPOT_seq_846 = "fQO)drdP!oCN,sn2Zk@A";
                TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
                String pre = tq.consumeTo("(");
                String guts = tq.chompBalanced('(', ')');
                String remainder = tq.remainder();
                TokenQueue.unescape(null);
                tq.consume(__DSPOT_seq_846);
                org.junit.Assert.fail("chompEscapedBalanced_mg5889 should have thrown IllegalStateException");
            } catch (IllegalStateException expected) {
            }
            org.junit.Assert.fail("chompEscapedBalanced_mg5889_failAssert86null6566 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals(null, expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalanced_mg5889_failAssert86null6564_failAssert98() throws Exception {
        try {
            try {
                String __DSPOT_seq_846 = null;
                TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
                String pre = tq.consumeTo("(");
                String guts = tq.chompBalanced('(', ')');
                String remainder = tq.remainder();
                TokenQueue.unescape(guts);
                tq.consume(__DSPOT_seq_846);
                org.junit.Assert.fail("chompEscapedBalanced_mg5889 should have thrown IllegalStateException");
            } catch (IllegalStateException expected) {
            }
            org.junit.Assert.fail("chompEscapedBalanced_mg5889_failAssert86null6564 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals(null, expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompEscapedBalanced_mg5889_failAssert86null6566_failAssert97litChar7265_failAssert125() throws Exception {
        try {
            try {
                try {
                    String __DSPOT_seq_846 = "fQO)drdP!oCN,sn2Zk@A";
                    TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
                    String pre = tq.consumeTo("(");
                    String guts = tq.chompBalanced('(', '(');
                    String remainder = tq.remainder();
                    TokenQueue.unescape(null);
                    tq.consume(__DSPOT_seq_846);
                    org.junit.Assert.fail("chompEscapedBalanced_mg5889 should have thrown IllegalStateException");
                } catch (IllegalStateException expected) {
                }
                org.junit.Assert.fail("chompEscapedBalanced_mg5889_failAssert86null6566 should have thrown NullPointerException");
            } catch (NullPointerException expected_1) {
            }
            org.junit.Assert.fail("chompEscapedBalanced_mg5889_failAssert86null6566_failAssert97litChar7265 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_2) {
            Assert.assertEquals("Did not find balanced marker at \'one (two) \\( \\) \\) three) four\'", expected_2.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedMatchesAsMuchAsPossible_mg3656_failAssert54() throws Exception {
        try {
            String __DSPOT_seq_531 = "!F5w|qM/m67_uZ>BTwOD";
            TokenQueue tq = new TokenQueue("unbalanced(something(or another)) else");
            tq.consumeTo("(");
            String match = tq.chompBalanced('(', ')');
            tq.consume(__DSPOT_seq_531);
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossible_mg3656 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedMatchesAsMuchAsPossiblelitChar3642_failAssert49() throws Exception {
        try {
            TokenQueue tq = new TokenQueue("unbalanced(something(or another)) else");
            tq.consumeTo("(");
            String match = tq.chompBalanced('(', '9');
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossiblelitChar3642 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Did not find balanced marker at \'something(or another)) else\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedMatchesAsMuchAsPossible_mg3656_failAssert54litChar3944_failAssert66() throws Exception {
        try {
            try {
                String __DSPOT_seq_531 = "!F5w|qM/m67_uZ>BTwOD";
                TokenQueue tq = new TokenQueue("unbalanced(something(or another)) else");
                tq.consumeTo("(");
                String match = tq.chompBalanced('(', '[');
                tq.consume(__DSPOT_seq_531);
                org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossible_mg3656 should have thrown IllegalStateException");
            } catch (IllegalStateException expected) {
            }
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossible_mg3656_failAssert54litChar3944 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Did not find balanced marker at \'something(or another)) else\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedMatchesAsMuchAsPossible_mg3656_failAssert54null4164_failAssert57() throws Exception {
        try {
            try {
                String __DSPOT_seq_531 = null;
                TokenQueue tq = new TokenQueue("unbalanced(something(or another)) else");
                tq.consumeTo("(");
                String match = tq.chompBalanced('(', ')');
                tq.consume(__DSPOT_seq_531);
                org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossible_mg3656 should have thrown IllegalStateException");
            } catch (IllegalStateException expected) {
            }
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossible_mg3656_failAssert54null4164 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals(null, expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompBalancedMatchesAsMuchAsPossible_mg3656_failAssert54null4164_failAssert57litChar4860_failAssert80() throws Exception {
        try {
            try {
                try {
                    String __DSPOT_seq_531 = null;
                    TokenQueue tq = new TokenQueue("unbalanced(something(or another)) else");
                    tq.consumeTo("(");
                    String match = tq.chompBalanced('(', '\u0000');
                    tq.consume(__DSPOT_seq_531);
                    org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossible_mg3656 should have thrown IllegalStateException");
                } catch (IllegalStateException expected) {
                }
                org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossible_mg3656_failAssert54null4164 should have thrown NullPointerException");
            } catch (NullPointerException expected_1) {
            }
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossible_mg3656_failAssert54null4164_failAssert57litChar4860 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_2) {
            Assert.assertEquals("Did not find balanced marker at \'something(or another)) else\'", expected_2.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void unescapelitString13183() throws Exception {
        String o_unescapelitString13183__1 = TokenQueue.unescape("");
        Assert.assertEquals("", o_unescapelitString13183__1);
    }

    @Test(timeout = 10000)
    public void unescapelitString13182_add13235() throws Exception {
        String o_unescapelitString13182_add13235__1 = TokenQueue.unescape("!M1hlc%&:Q;*");
        Assert.assertEquals("!M1hlc%&:Q;*", o_unescapelitString13182_add13235__1);
        String o_unescapelitString13182__1 = TokenQueue.unescape("!M1hlc%&:Q;*");
        Assert.assertEquals("!M1hlc%&:Q;*", o_unescapelitString13182__1);
        Assert.assertEquals("!M1hlc%&:Q;*", o_unescapelitString13182_add13235__1);
    }

    @Test(timeout = 10000)
    public void unescape_add13186litString13221_add13495() throws Exception {
        String o_unescape_add13186litString13221_add13495__1 = TokenQueue.unescape("J=f1_[-!n}n8");
        Assert.assertEquals("J=f1_[-!n}n8", o_unescape_add13186litString13221_add13495__1);
        String o_unescape_add13186__1 = TokenQueue.unescape("J=f1_[-!n}n8");
        Assert.assertEquals("J=f1_[-!n}n8", o_unescape_add13186__1);
        String o_unescape_add13186__2 = TokenQueue.unescape("one \\( \\) \\\\");
        Assert.assertEquals("one ( ) \\", o_unescape_add13186__2);
        Assert.assertEquals("J=f1_[-!n}n8", o_unescape_add13186litString13221_add13495__1);
        Assert.assertEquals("J=f1_[-!n}n8", o_unescape_add13186__1);
    }

    @Test(timeout = 10000)
    public void unescape_add13186litString13221litString13419() throws Exception {
        String o_unescape_add13186__1 = TokenQueue.unescape("J=f1_[-!n}n8");
        Assert.assertEquals("J=f1_[-!n}n8", o_unescape_add13186__1);
        String o_unescape_add13186__2 = TokenQueue.unescape("a.&ZxhHz#G[W");
        Assert.assertEquals("a.&ZxhHz#G[W", o_unescape_add13186__2);
        Assert.assertEquals("J=f1_[-!n}n8", o_unescape_add13186__1);
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCasenull8713_failAssert128() throws Exception {
        try {
            String t = null;
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("</textarea");
            tq = new TokenQueue("<textarea> one two < three </oops>");
            data = tq.chompToIgnoreCase("</textarea");
            org.junit.Assert.fail("chompToIgnoreCasenull8713 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCaselitString8665_failAssert132() throws Exception {
        try {
            String t = "<textarea>one < two </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("");
            tq = new TokenQueue("<textarea> one two < three </oops>");
            data = tq.chompToIgnoreCase("</textarea");
            org.junit.Assert.fail("chompToIgnoreCaselitString8665 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCase_mg8693_failAssert131() throws Exception {
        try {
            String __DSPOT_seq_1341 = ">MI>cwz#1JS:qqix@V|q";
            String t = "<textarea>one < two </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("</textarea");
            tq = new TokenQueue("<textarea> one two < three </oops>");
            data = tq.chompToIgnoreCase("</textarea");
            tq.consume(__DSPOT_seq_1341);
            org.junit.Assert.fail("chompToIgnoreCase_mg8693 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCase_mg8693_failAssert131null9247_failAssert144() throws Exception {
        try {
            try {
                String __DSPOT_seq_1341 = null;
                String t = "<textarea>one < two </TEXTarea>";
                TokenQueue tq = new TokenQueue(t);
                String data = tq.chompToIgnoreCase("</textarea");
                tq = new TokenQueue("<textarea> one two < three </oops>");
                data = tq.chompToIgnoreCase("</textarea");
                tq.consume(__DSPOT_seq_1341);
                org.junit.Assert.fail("chompToIgnoreCase_mg8693 should have thrown IllegalStateException");
            } catch (IllegalStateException expected) {
            }
            org.junit.Assert.fail("chompToIgnoreCase_mg8693_failAssert131null9247 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals(null, expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCase_mg8693_failAssert131null9248_failAssert142() throws Exception {
        try {
            try {
                String __DSPOT_seq_1341 = ">MI>cwz#1JS:qqix@V|q";
                String t = null;
                TokenQueue tq = new TokenQueue(t);
                String data = tq.chompToIgnoreCase("</textarea");
                tq = new TokenQueue("<textarea> one two < three </oops>");
                data = tq.chompToIgnoreCase("</textarea");
                tq.consume(__DSPOT_seq_1341);
                org.junit.Assert.fail("chompToIgnoreCase_mg8693 should have thrown IllegalStateException");
            } catch (IllegalStateException expected) {
            }
            org.junit.Assert.fail("chompToIgnoreCase_mg8693_failAssert131null9248 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Object must not be null", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCase_mg8693_failAssert131litString8962_failAssert143() throws Exception {
        try {
            try {
                String __DSPOT_seq_1341 = ">MI>cwz#1JS:qqix@V|q";
                String t = "<textarea>one < two </TEXTarea>";
                TokenQueue tq = new TokenQueue(t);
                String data = tq.chompToIgnoreCase("");
                tq = new TokenQueue("<textarea> one two < three </oops>");
                data = tq.chompToIgnoreCase("</textarea");
                tq.consume(__DSPOT_seq_1341);
                org.junit.Assert.fail("chompToIgnoreCase_mg8693 should have thrown IllegalStateException");
            } catch (IllegalStateException expected) {
            }
            org.junit.Assert.fail("chompToIgnoreCase_mg8693_failAssert131litString8962 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected_1) {
            Assert.assertEquals("String index out of range: 1", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void chompToIgnoreCase_mg8693_failAssert131null9251_failAssert141null10357_failAssert150() throws Exception {
        try {
            try {
                try {
                    String __DSPOT_seq_1341 = ">MI>cwz#1JS:qqix@V|q";
                    String t = null;
                    TokenQueue tq = new TokenQueue(t);
                    String data = tq.chompToIgnoreCase("</textarea");
                    tq = new TokenQueue("<textarea> one two < three </oops>");
                    data = tq.chompToIgnoreCase("</textarea");
                    tq.consume(null);
                    org.junit.Assert.fail("chompToIgnoreCase_mg8693 should have thrown IllegalStateException");
                } catch (IllegalStateException expected) {
                }
                org.junit.Assert.fail("chompToIgnoreCase_mg8693_failAssert131null9251 should have thrown NullPointerException");
            } catch (NullPointerException expected_1) {
            }
            org.junit.Assert.fail("chompToIgnoreCase_mg8693_failAssert131null9251_failAssert141null10357 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_2) {
            Assert.assertEquals("Object must not be null", expected_2.getMessage());
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
            String __DSPOT_seq_6 = "!&Bcvg[?i!rb0/|]6^FT";
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
    public void addFirst_mg29_failAssert1null300_failAssert3() throws Exception {
        try {
            try {
                String __DSPOT_seq_6 = "!&Bcvg[?i!rb0/|]6^FT";
                TokenQueue tq = new TokenQueue("One Two");
                tq.consumeWord();
                tq.addFirst("Three");
                tq.remainder();
                tq.consume(null);
                org.junit.Assert.fail("addFirst_mg29 should have thrown IllegalStateException");
            } catch (IllegalStateException expected) {
            }
            org.junit.Assert.fail("addFirst_mg29_failAssert1null300 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals(null, expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToIgnoreSecondCallTestlitString10981_failAssert156() throws Exception {
        try {
            String t = "<textarea>one < two </TEXTarea> third </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("");
            data = tq.chompToIgnoreCase("</textarea>");
            org.junit.Assert.fail("consumeToIgnoreSecondCallTestlitString10981 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToIgnoreSecondCallTest_mg11001_failAssert157() throws Exception {
        try {
            String __DSPOT_seq_1626 = "B$IFUa[)%uJ #8w(_t[L";
            String t = "<textarea>one < two </TEXTarea> third </TEXTarea>";
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("</textarea>");
            data = tq.chompToIgnoreCase("</textarea>");
            tq.consume(__DSPOT_seq_1626);
            org.junit.Assert.fail("consumeToIgnoreSecondCallTest_mg11001 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Queue did not match expected sequence", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToIgnoreSecondCallTestnull11021_failAssert154() throws Exception {
        try {
            String t = null;
            TokenQueue tq = new TokenQueue(t);
            String data = tq.chompToIgnoreCase("</textarea>");
            data = tq.chompToIgnoreCase("</textarea>");
            org.junit.Assert.fail("consumeToIgnoreSecondCallTestnull11021 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToIgnoreSecondCallTestnull11022_failAssert152null11485_failAssert166() throws Exception {
        try {
            try {
                String t = null;
                TokenQueue tq = new TokenQueue(t);
                String data = tq.chompToIgnoreCase(null);
                data = tq.chompToIgnoreCase("</textarea>");
                org.junit.Assert.fail("consumeToIgnoreSecondCallTestnull11022 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("consumeToIgnoreSecondCallTestnull11022_failAssert152null11485 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Object must not be null", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToIgnoreSecondCallTest_mg11001_failAssert157null11492_failAssert168() throws Exception {
        try {
            try {
                String __DSPOT_seq_1626 = null;
                String t = "<textarea>one < two </TEXTarea> third </TEXTarea>";
                TokenQueue tq = new TokenQueue(t);
                String data = tq.chompToIgnoreCase("</textarea>");
                data = tq.chompToIgnoreCase("</textarea>");
                tq.consume(__DSPOT_seq_1626);
                org.junit.Assert.fail("consumeToIgnoreSecondCallTest_mg11001 should have thrown IllegalStateException");
            } catch (IllegalStateException expected) {
            }
            org.junit.Assert.fail("consumeToIgnoreSecondCallTest_mg11001_failAssert157null11492 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals(null, expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToIgnoreSecondCallTest_mg11001_failAssert157litString11238_failAssert170() throws Exception {
        try {
            try {
                String __DSPOT_seq_1626 = "B$IFUa[)%uJ #8w(_t[L";
                String t = "<textarea>one < two </TEXTarea> third </TEXTarea>";
                TokenQueue tq = new TokenQueue(t);
                String data = tq.chompToIgnoreCase("");
                data = tq.chompToIgnoreCase("</textarea>");
                tq.consume(__DSPOT_seq_1626);
                org.junit.Assert.fail("consumeToIgnoreSecondCallTest_mg11001 should have thrown IllegalStateException");
            } catch (IllegalStateException expected) {
            }
            org.junit.Assert.fail("consumeToIgnoreSecondCallTest_mg11001_failAssert157litString11238 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected_1) {
            Assert.assertEquals("String index out of range: 1", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToIgnoreSecondCallTest_mg11000_failAssert158null11499_failAssert160null12554_failAssert171() throws Exception {
        try {
            try {
                try {
                    String t = null;
                    TokenQueue tq = new TokenQueue(t);
                    String data = tq.chompToIgnoreCase("</textarea>");
                    data = tq.chompToIgnoreCase(null);
                    tq.consume();
                    org.junit.Assert.fail("consumeToIgnoreSecondCallTest_mg11000 should have thrown StringIndexOutOfBoundsException");
                } catch (StringIndexOutOfBoundsException expected) {
                }
                org.junit.Assert.fail("consumeToIgnoreSecondCallTest_mg11000_failAssert158null11499 should have thrown NullPointerException");
            } catch (NullPointerException expected_1) {
            }
            org.junit.Assert.fail("consumeToIgnoreSecondCallTest_mg11000_failAssert158null11499_failAssert160null12554 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_2) {
            Assert.assertEquals("Object must not be null", expected_2.getMessage());
        }
    }

    private static void validateNestedQuotes(String html, String selector) {
        Assert.assertEquals("#identifier", Jsoup.parse(html).select(selector).first().cssSelector());
    }
}

