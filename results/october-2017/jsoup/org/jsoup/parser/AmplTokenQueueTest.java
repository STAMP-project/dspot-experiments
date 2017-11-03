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

    /* amplification of org.jsoup.parser.TokenQueueTest#addFirst */
    @org.junit.Test(timeout = 10000)
    public void addFirst_sd28_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue("One Two");
            tq.consumeWord();
            tq.addFirst("Three");
            tq.remainder();
            // StatementAdd: add invocation of a method
            tq.consume();
            org.junit.Assert.fail("addFirst_sd28 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#addFirst */
    @org.junit.Test(timeout = 10000)
    public void addFirst_sd46_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_seq_14 = "lKC*+{5@T5!^MYU(dM7K";
            org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue("One Two");
            tq.consumeWord();
            tq.addFirst("Three");
            tq.remainder();
            // StatementAdd: add invocation of a method
            tq.consume(__DSPOT_seq_14);
            org.junit.Assert.fail("addFirst_sd46 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#addFirst */
    @org.junit.Test(timeout = 10000)
    public void addFirst_sd85_failAssert10() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_arg0_64 = 2094448682;
            org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue("One Two");
            // StatementAdd: generate variable from return value
            java.lang.String __DSPOT_invoc_3 = tq.consumeWord();
            tq.addFirst("Three");
            tq.remainder();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_3.substring(__DSPOT_arg0_64);
            org.junit.Assert.fail("addFirst_sd85 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompBalanced */
    @org.junit.Test(timeout = 10000)
    public void chompBalanced_sd3346_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_seq_2024 = "Pz4a&n3P:c5ptUfimdUw";
            org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(":contains(one (two) three) four");
            java.lang.String pre = tq.consumeTo("(");
            java.lang.String guts = tq.chompBalanced('(', ')');
            java.lang.String remainder = tq.remainder();
            // StatementAdd: add invocation of a method
            tq.consume(__DSPOT_seq_2024);
            org.junit.Assert.fail("chompBalanced_sd3346 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompBalanced */
    @org.junit.Test(timeout = 10000)
    public void chompBalanced_sd3328_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(":contains(one (two) three) four");
            java.lang.String pre = tq.consumeTo("(");
            java.lang.String guts = tq.chompBalanced('(', ')');
            java.lang.String remainder = tq.remainder();
            // StatementAdd: add invocation of a method
            tq.consume();
            org.junit.Assert.fail("chompBalanced_sd3328 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompBalanced */
    @org.junit.Test(timeout = 10000)
    public void chompBalanced_literalMutationChar3312_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(":contains(one (two) three) four");
            java.lang.String pre = tq.consumeTo("(");
            java.lang.String guts = tq.chompBalanced('(', '*');
            java.lang.String remainder = tq.remainder();
            org.junit.Assert.fail("chompBalanced_literalMutationChar3312 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompBalanced */
    /* amplification of org.jsoup.parser.TokenQueueTest#chompBalanced_sd3346 */
    @org.junit.Test(timeout = 10000)
    public void chompBalanced_sd3346_failAssert6_literalMutationChar3796_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String __DSPOT_seq_2024 = "Pz4a&n3P:c5ptUfimdUw";
                org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(":contains(one (two) three) four");
                java.lang.String pre = tq.consumeTo("(");
                java.lang.String guts = tq.chompBalanced('(', '*');
                java.lang.String remainder = tq.remainder();
                // StatementAdd: add invocation of a method
                tq.consume(__DSPOT_seq_2024);
                org.junit.Assert.fail("chompBalanced_sd3346 should have thrown IllegalStateException");
            } catch (java.lang.IllegalStateException eee) {
            }
            org.junit.Assert.fail("chompBalanced_sd3346_failAssert6_literalMutationChar3796 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompBalancedMatchesAsMuchAsPossible */
    @org.junit.Test(timeout = 10000)
    public void chompBalancedMatchesAsMuchAsPossible_literalMutationString4738_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue("dOzifF v!nx3BEC*m>$UMz?*]TE3um.I(3]wc,");
            tq.consumeTo("(");
            java.lang.String match = tq.chompBalanced('(', ')');
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossible_literalMutationString4738 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompBalancedMatchesAsMuchAsPossible */
    @org.junit.Test(timeout = 10000)
    public void chompBalancedMatchesAsMuchAsPossible_sd4786_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_seq_2189 = "rTN}^}=4)/68Sy=-Sdtg";
            org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue("unbalanced(something(or another)) else");
            tq.consumeTo("(");
            java.lang.String match = tq.chompBalanced('(', ')');
            // StatementAdd: add invocation of a method
            tq.consume(__DSPOT_seq_2189);
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossible_sd4786 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompBalancedMatchesAsMuchAsPossible */
    /* amplification of org.jsoup.parser.TokenQueueTest#chompBalancedMatchesAsMuchAsPossible_sd4800 */
    @org.junit.Test(timeout = 10000)
    public void chompBalancedMatchesAsMuchAsPossible_sd4800_failAssert10_literalMutationChar6052_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                int __DSPOT_arg0_2208 = -550646558;
                org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue("unbalanced(something(or another)) else");
                // StatementAdd: generate variable from return value
                java.lang.String __DSPOT_invoc_3 = tq.consumeTo("(");
                java.lang.String match = tq.chompBalanced('(', '(');
                // StatementAdd: add invocation of a method
                __DSPOT_invoc_3.charAt(__DSPOT_arg0_2208);
                org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossible_sd4800 should have thrown StringIndexOutOfBoundsException");
            } catch (java.lang.StringIndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossible_sd4800_failAssert10_literalMutationChar6052 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompBalancedMatchesAsMuchAsPossible */
    /* amplification of org.jsoup.parser.TokenQueueTest#chompBalancedMatchesAsMuchAsPossible_sd4786 */
    @org.junit.Test(timeout = 10000)
    public void chompBalancedMatchesAsMuchAsPossible_sd4786_failAssert7_literalMutationString5762_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String __DSPOT_seq_2189 = "rTN}^}=4)/68Sy=-Sdtg";
                org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue("(E3yJ3[SOKyetFYCDOR]<.$/D.G;=N/s8uuu[g");
                tq.consumeTo("(");
                java.lang.String match = tq.chompBalanced('(', ')');
                // StatementAdd: add invocation of a method
                tq.consume(__DSPOT_seq_2189);
                org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossible_sd4786 should have thrown IllegalStateException");
            } catch (java.lang.IllegalStateException eee) {
            }
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossible_sd4786_failAssert7_literalMutationString5762 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompEscapedBalanced */
    @org.junit.Test(timeout = 10000)
    public void chompEscapedBalanced_sd8743_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_seq_3984 = "3[(I[UHNI)_Oyg(b1iLM";
            org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(":contains(one (two) \\( \\) \\) three) four");
            java.lang.String pre = tq.consumeTo("(");
            java.lang.String guts = tq.chompBalanced('(', ')');
            java.lang.String remainder = tq.remainder();
            org.jsoup.parser.TokenQueue.unescape(guts);
            // StatementAdd: add invocation of a method
            tq.consume(__DSPOT_seq_3984);
            org.junit.Assert.fail("chompEscapedBalanced_sd8743 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompEscapedBalanced */
    @org.junit.Test(timeout = 10000)
    public void chompEscapedBalanced_sd8725_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(":contains(one (two) \\( \\) \\) three) four");
            java.lang.String pre = tq.consumeTo("(");
            java.lang.String guts = tq.chompBalanced('(', ')');
            java.lang.String remainder = tq.remainder();
            org.jsoup.parser.TokenQueue.unescape(guts);
            // StatementAdd: add invocation of a method
            tq.consume();
            org.junit.Assert.fail("chompEscapedBalanced_sd8725 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompEscapedBalanced */
    @org.junit.Test(timeout = 10000)
    public void chompEscapedBalanced_literalMutationChar8709_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(":contains(one (two) \\( \\) \\) three) four");
            java.lang.String pre = tq.consumeTo("(");
            java.lang.String guts = tq.chompBalanced('(', '(');
            java.lang.String remainder = tq.remainder();
            org.jsoup.parser.TokenQueue.unescape(guts);
            org.junit.Assert.fail("chompEscapedBalanced_literalMutationChar8709 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompEscapedBalanced */
    /* amplification of org.jsoup.parser.TokenQueueTest#chompEscapedBalanced_sd8725 */
    @org.junit.Test(timeout = 10000)
    public void chompEscapedBalanced_sd8725_failAssert6_literalMutationChar9495_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(":contains(one (two) \\( \\) \\) three) four");
                java.lang.String pre = tq.consumeTo("(");
                java.lang.String guts = tq.chompBalanced('(', '(');
                java.lang.String remainder = tq.remainder();
                org.jsoup.parser.TokenQueue.unescape(guts);
                // StatementAdd: add invocation of a method
                tq.consume();
                org.junit.Assert.fail("chompEscapedBalanced_sd8725 should have thrown StringIndexOutOfBoundsException");
            } catch (java.lang.StringIndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("chompEscapedBalanced_sd8725_failAssert6_literalMutationChar9495 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompToIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void chompToIgnoreCase_literalMutationString10594_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String t = "<textarea>one < two </TEXTarea>";
            org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(t);
            java.lang.String data = tq.chompToIgnoreCase("</textarea");
            tq = new org.jsoup.parser.TokenQueue("<textarea> one two < three </oops>");
            data = tq.chompToIgnoreCase("");
            org.junit.Assert.fail("chompToIgnoreCase_literalMutationString10594 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompToIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void chompToIgnoreCase_sd10630_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_seq_4734 = "[N*ZGm |E>CC[0,@K(VV";
            java.lang.String t = "<textarea>one < two </TEXTarea>";
            org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(t);
            java.lang.String data = tq.chompToIgnoreCase("</textarea");
            tq = new org.jsoup.parser.TokenQueue("<textarea> one two < three </oops>");
            data = tq.chompToIgnoreCase("</textarea");
            // StatementAdd: add invocation of a method
            tq.consume(__DSPOT_seq_4734);
            org.junit.Assert.fail("chompToIgnoreCase_sd10630 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompToIgnoreCase */
    /* amplification of org.jsoup.parser.TokenQueueTest#chompToIgnoreCase_sd10630 */
    @org.junit.Test(timeout = 10000)
    public void chompToIgnoreCase_sd10630_failAssert3_literalMutationString10925_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String __DSPOT_seq_4734 = "[N*ZGm |E>CC[0,@K(VV";
                java.lang.String t = "<textarea>one < two </TEXTarea>";
                org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(t);
                java.lang.String data = tq.chompToIgnoreCase("</textarea");
                tq = new org.jsoup.parser.TokenQueue("<textarea> one two < three </oops>");
                data = tq.chompToIgnoreCase("");
                // StatementAdd: add invocation of a method
                tq.consume(__DSPOT_seq_4734);
                org.junit.Assert.fail("chompToIgnoreCase_sd10630 should have thrown IllegalStateException");
            } catch (java.lang.IllegalStateException eee) {
            }
            org.junit.Assert.fail("chompToIgnoreCase_sd10630_failAssert3_literalMutationString10925 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }
}

