

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
    public void addFirst_add1() {
        org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue("One Two");
        // AssertGenerator replace invocation
        java.lang.String o_addFirst_add1__3 = // MethodCallAdder
tq.consumeWord();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_addFirst_add1__3, "One");
        tq.consumeWord();
        tq.addFirst("Three");
        org.junit.Assert.assertEquals("Three Two", tq.remainder());
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#addFirst */
    @org.junit.Test(timeout = 10000)
    public void addFirst_cf37() {
        org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue("One Two");
        tq.consumeWord();
        tq.addFirst("Three");
        // StatementAdderOnAssert create null value
        java.lang.StringBuilder vc_26 = (java.lang.StringBuilder)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_26);
        // StatementAdderOnAssert create null value
        org.jsoup.parser.Token vc_24 = (org.jsoup.parser.Token)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_24);
        // StatementAdderMethod cloned existing statement
        vc_24.reset(vc_26);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_24);
        org.junit.Assert.assertEquals("Three Two", tq.remainder());
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#addFirst */
    @org.junit.Test(timeout = 10000)
    public void addFirst_cf38() {
        org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue("One Two");
        tq.consumeWord();
        tq.addFirst("Three");
        // StatementAdderOnAssert create random local variable
        java.lang.StringBuilder vc_27 = new java.lang.StringBuilder();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)((java.lang.StringBuilder)((java.lang.StringBuilder)vc_27).reverse()).reverse()).capacity(), 16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.StringBuilder)vc_27).reverse().equals(vc_27));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)((java.lang.StringBuilder)vc_27).reverse()).length(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)((java.lang.StringBuilder)((java.lang.StringBuilder)vc_27).reverse()).reverse()).length(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)((java.lang.StringBuilder)vc_27).reverse()).capacity(), 16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)vc_27).capacity(), 16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.StringBuilder)((java.lang.StringBuilder)((java.lang.StringBuilder)vc_27).reverse()).reverse()).reverse().equals(vc_27));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)vc_27).length(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.StringBuilder)((java.lang.StringBuilder)vc_27).reverse()).reverse().equals(vc_27));
        // StatementAdderOnAssert create null value
        org.jsoup.parser.Token vc_24 = (org.jsoup.parser.Token)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_24);
        // StatementAdderMethod cloned existing statement
        vc_24.reset(vc_27);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_24);
        org.junit.Assert.assertEquals("Three Two", tq.remainder());
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompBalanced */
    @org.junit.Test(timeout = 10000)
    public void chompBalanced_cf924() {
        org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(":contains(one (two) three) four");
        java.lang.String pre = tq.consumeTo("(");
        java.lang.String guts = tq.chompBalanced('(', ')');
        java.lang.String remainder = tq.remainder();
        org.junit.Assert.assertEquals(":contains", pre);
        org.junit.Assert.assertEquals("one (two) three", guts);
        // StatementAdderOnAssert create random local variable
        java.lang.StringBuilder vc_139 = new java.lang.StringBuilder();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)((java.lang.StringBuilder)vc_139).reverse()).capacity(), 16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)((java.lang.StringBuilder)vc_139).reverse()).length(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)vc_139).capacity(), 16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.StringBuilder)((java.lang.StringBuilder)vc_139).reverse()).reverse().equals(vc_139));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)vc_139).length(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)((java.lang.StringBuilder)((java.lang.StringBuilder)vc_139).reverse()).reverse()).length(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.StringBuilder)((java.lang.StringBuilder)((java.lang.StringBuilder)vc_139).reverse()).reverse()).reverse().equals(vc_139));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)((java.lang.StringBuilder)((java.lang.StringBuilder)vc_139).reverse()).reverse()).capacity(), 16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.StringBuilder)vc_139).reverse().equals(vc_139));
        // StatementAdderOnAssert create null value
        org.jsoup.parser.Token vc_136 = (org.jsoup.parser.Token)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_136);
        // StatementAdderMethod cloned existing statement
        vc_136.reset(vc_139);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_136);
        org.junit.Assert.assertEquals(" four", remainder);
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompBalanced */
    @org.junit.Test(timeout = 10000)
    public void chompBalanced_cf923() {
        org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(":contains(one (two) three) four");
        java.lang.String pre = tq.consumeTo("(");
        java.lang.String guts = tq.chompBalanced('(', ')');
        java.lang.String remainder = tq.remainder();
        org.junit.Assert.assertEquals(":contains", pre);
        org.junit.Assert.assertEquals("one (two) three", guts);
        // StatementAdderOnAssert create null value
        java.lang.StringBuilder vc_138 = (java.lang.StringBuilder)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_138);
        // StatementAdderOnAssert create null value
        org.jsoup.parser.Token vc_136 = (org.jsoup.parser.Token)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_136);
        // StatementAdderMethod cloned existing statement
        vc_136.reset(vc_138);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_136);
        org.junit.Assert.assertEquals(" four", remainder);
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompBalanced */
    @org.junit.Test
    public void chompBalanced_literalMutation894_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue("!b(f2kvd|>op3F[UUbDcpS ]x;K>tB$");
            java.lang.String pre = tq.consumeTo("(");
            java.lang.String guts = tq.chompBalanced('(', ')');
            java.lang.String remainder = tq.remainder();
            org.junit.Assert.fail("chompBalanced_literalMutation894 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompBalanced */
    @org.junit.Test(timeout = 10000)
    public void chompBalanced_cf921_failAssert18_literalMutation1132_failAssert66() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue("@H(x2>iy;hPVv>/f/5!<Y=dgY,;s4m>");
                java.lang.String pre = tq.consumeTo("(");
                java.lang.String guts = tq.chompBalanced('(', ')');
                java.lang.String remainder = tq.remainder();
                // StatementAdderOnAssert create null value
                org.jsoup.parser.Token vc_134 = (org.jsoup.parser.Token)null;
                // StatementAdderMethod cloned existing statement
                vc_134.asStartTag();
                org.junit.Assert.fail("chompBalanced_cf921 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("chompBalanced_cf921_failAssert18_literalMutation1132 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompBalanced */
    @org.junit.Test(timeout = 10000)
    public void chompBalanced_cf924_cf1010_failAssert67_literalMutation1593_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_27_1 = 16;
                // MethodAssertGenerator build local variable
                Object o_23_1 = 0;
                // MethodAssertGenerator build local variable
                Object o_21_1 = 0;
                // MethodAssertGenerator build local variable
                Object o_17_1 = 16;
                // MethodAssertGenerator build local variable
                Object o_15_1 = 0;
                // MethodAssertGenerator build local variable
                Object o_13_1 = 16;
                org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue("sgVPn5_N]Rma)Zp`f_]Wh$_8K%!{;g(");
                java.lang.String pre = tq.consumeTo("(");
                java.lang.String guts = tq.chompBalanced('(', ')');
                java.lang.String remainder = tq.remainder();
                // StatementAdderOnAssert create random local variable
                java.lang.StringBuilder vc_139 = new java.lang.StringBuilder();
                // MethodAssertGenerator build local variable
                Object o_13_0 = ((java.lang.StringBuilder)((java.lang.StringBuilder)vc_139).reverse()).capacity();
                // MethodAssertGenerator build local variable
                Object o_15_0 = ((java.lang.StringBuilder)((java.lang.StringBuilder)vc_139).reverse()).length();
                // MethodAssertGenerator build local variable
                Object o_17_0 = ((java.lang.StringBuilder)vc_139).capacity();
                // MethodAssertGenerator build local variable
                Object o_19_0 = ((java.lang.StringBuilder)((java.lang.StringBuilder)vc_139).reverse()).reverse().equals(vc_139);
                // MethodAssertGenerator build local variable
                Object o_21_0 = ((java.lang.StringBuilder)vc_139).length();
                // MethodAssertGenerator build local variable
                Object o_23_0 = ((java.lang.StringBuilder)((java.lang.StringBuilder)((java.lang.StringBuilder)vc_139).reverse()).reverse()).length();
                // MethodAssertGenerator build local variable
                Object o_25_0 = ((java.lang.StringBuilder)((java.lang.StringBuilder)((java.lang.StringBuilder)vc_139).reverse()).reverse()).reverse().equals(vc_139);
                // MethodAssertGenerator build local variable
                Object o_27_0 = ((java.lang.StringBuilder)((java.lang.StringBuilder)((java.lang.StringBuilder)vc_139).reverse()).reverse()).capacity();
                // MethodAssertGenerator build local variable
                Object o_29_0 = ((java.lang.StringBuilder)vc_139).reverse().equals(vc_139);
                // StatementAdderOnAssert create null value
                org.jsoup.parser.Token vc_136 = (org.jsoup.parser.Token)null;
                // MethodAssertGenerator build local variable
                Object o_33_0 = vc_136;
                // StatementAdderMethod cloned existing statement
                vc_136.reset(vc_139);
                // MethodAssertGenerator build local variable
                Object o_37_0 = vc_136;
                // StatementAdderMethod cloned existing statement
                vc_136.asCharacter();
                org.junit.Assert.fail("chompBalanced_cf924_cf1010 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("chompBalanced_cf924_cf1010_failAssert67_literalMutation1593 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompBalanced */
    @org.junit.Test(timeout = 10000)
    public void chompBalanced_cf924_cf1000_failAssert44_literalMutation1426_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_27_1 = 16;
                // MethodAssertGenerator build local variable
                Object o_23_1 = 0;
                // MethodAssertGenerator build local variable
                Object o_21_1 = 0;
                // MethodAssertGenerator build local variable
                Object o_17_1 = 16;
                // MethodAssertGenerator build local variable
                Object o_15_1 = 0;
                // MethodAssertGenerator build local variable
                Object o_13_1 = 16;
                org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(":contains(one (two_ three) four");
                java.lang.String pre = tq.consumeTo("(");
                java.lang.String guts = tq.chompBalanced('(', ')');
                java.lang.String remainder = tq.remainder();
                // StatementAdderOnAssert create random local variable
                java.lang.StringBuilder vc_139 = new java.lang.StringBuilder();
                // MethodAssertGenerator build local variable
                Object o_13_0 = ((java.lang.StringBuilder)((java.lang.StringBuilder)vc_139).reverse()).capacity();
                // MethodAssertGenerator build local variable
                Object o_15_0 = ((java.lang.StringBuilder)((java.lang.StringBuilder)vc_139).reverse()).length();
                // MethodAssertGenerator build local variable
                Object o_17_0 = ((java.lang.StringBuilder)vc_139).capacity();
                // MethodAssertGenerator build local variable
                Object o_19_0 = ((java.lang.StringBuilder)((java.lang.StringBuilder)vc_139).reverse()).reverse().equals(vc_139);
                // MethodAssertGenerator build local variable
                Object o_21_0 = ((java.lang.StringBuilder)vc_139).length();
                // MethodAssertGenerator build local variable
                Object o_23_0 = ((java.lang.StringBuilder)((java.lang.StringBuilder)((java.lang.StringBuilder)vc_139).reverse()).reverse()).length();
                // MethodAssertGenerator build local variable
                Object o_25_0 = ((java.lang.StringBuilder)((java.lang.StringBuilder)((java.lang.StringBuilder)vc_139).reverse()).reverse()).reverse().equals(vc_139);
                // MethodAssertGenerator build local variable
                Object o_27_0 = ((java.lang.StringBuilder)((java.lang.StringBuilder)((java.lang.StringBuilder)vc_139).reverse()).reverse()).capacity();
                // MethodAssertGenerator build local variable
                Object o_29_0 = ((java.lang.StringBuilder)vc_139).reverse().equals(vc_139);
                // StatementAdderOnAssert create null value
                org.jsoup.parser.Token vc_136 = (org.jsoup.parser.Token)null;
                // MethodAssertGenerator build local variable
                Object o_33_0 = vc_136;
                // StatementAdderMethod cloned existing statement
                vc_136.reset(vc_139);
                // MethodAssertGenerator build local variable
                Object o_37_0 = vc_136;
                // StatementAdderOnAssert create null value
                org.jsoup.parser.Token vc_176 = (org.jsoup.parser.Token)null;
                // StatementAdderMethod cloned existing statement
                vc_176.isEndTag();
                org.junit.Assert.fail("chompBalanced_cf924_cf1000 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("chompBalanced_cf924_cf1000_failAssert44_literalMutation1426 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompBalancedMatchesAsMuchAsPossible */
    @org.junit.Test
    public void chompBalancedMatchesAsMuchAsPossible_literalMutation1604_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(")K54x0P`g!FE6hSw&uM#lT(R@N-/+Q;*6-*};E");
            tq.consumeTo("(");
            java.lang.String match = tq.chompBalanced('(', ')');
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossible_literalMutation1604 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompBalancedMatchesAsMuchAsPossible */
    @org.junit.Test(timeout = 10000)
    public void chompBalancedMatchesAsMuchAsPossible_cf1613_failAssert9_literalMutation1674_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(")s(Bqv|9>B^t$vWL?9&8ga`ZDT5#5gGp%c!j#q");
                tq.consumeTo("(");
                java.lang.String match = tq.chompBalanced('(', ')');
                // StatementAdderOnAssert create null value
                org.jsoup.parser.Token vc_202 = (org.jsoup.parser.Token)null;
                // StatementAdderMethod cloned existing statement
                vc_202.isEOF();
                org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossible_cf1613 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("chompBalancedMatchesAsMuchAsPossible_cf1613_failAssert9_literalMutation1674 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompEscapedBalanced */
    @org.junit.Test(timeout = 10000)
    public void chompEscapedBalanced_cf1818() {
        org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(":contains(one (two) \\( \\) \\) three) four");
        java.lang.String pre = tq.consumeTo("(");
        java.lang.String guts = tq.chompBalanced('(', ')');
        java.lang.String remainder = tq.remainder();
        org.junit.Assert.assertEquals(":contains", pre);
        org.junit.Assert.assertEquals("one (two) \\( \\) \\) three", guts);
        org.junit.Assert.assertEquals("one (two) ( ) ) three", org.jsoup.parser.TokenQueue.unescape(guts));
        // StatementAdderOnAssert create null value
        java.lang.StringBuilder vc_250 = (java.lang.StringBuilder)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_250);
        // StatementAdderOnAssert create null value
        org.jsoup.parser.Token vc_248 = (org.jsoup.parser.Token)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_248);
        // StatementAdderMethod cloned existing statement
        vc_248.reset(vc_250);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_248);
        org.junit.Assert.assertEquals(" four", remainder);
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompEscapedBalanced */
    @org.junit.Test(timeout = 10000)
    public void chompEscapedBalanced_cf1819() {
        org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(":contains(one (two) \\( \\) \\) three) four");
        java.lang.String pre = tq.consumeTo("(");
        java.lang.String guts = tq.chompBalanced('(', ')');
        java.lang.String remainder = tq.remainder();
        org.junit.Assert.assertEquals(":contains", pre);
        org.junit.Assert.assertEquals("one (two) \\( \\) \\) three", guts);
        org.junit.Assert.assertEquals("one (two) ( ) ) three", org.jsoup.parser.TokenQueue.unescape(guts));
        // StatementAdderOnAssert create random local variable
        java.lang.StringBuilder vc_251 = new java.lang.StringBuilder();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)vc_251).capacity(), 16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.StringBuilder)vc_251).reverse().equals(vc_251));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)((java.lang.StringBuilder)((java.lang.StringBuilder)vc_251).reverse()).reverse()).capacity(), 16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)((java.lang.StringBuilder)((java.lang.StringBuilder)vc_251).reverse()).reverse()).length(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)vc_251).length(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)((java.lang.StringBuilder)vc_251).reverse()).capacity(), 16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.StringBuilder)((java.lang.StringBuilder)((java.lang.StringBuilder)vc_251).reverse()).reverse()).reverse().equals(vc_251));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.StringBuilder)((java.lang.StringBuilder)vc_251).reverse()).reverse().equals(vc_251));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)((java.lang.StringBuilder)vc_251).reverse()).length(), 0);
        // StatementAdderOnAssert create null value
        org.jsoup.parser.Token vc_248 = (org.jsoup.parser.Token)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_248);
        // StatementAdderMethod cloned existing statement
        vc_248.reset(vc_251);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_248);
        org.junit.Assert.assertEquals(" four", remainder);
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompEscapedBalanced */
    @org.junit.Test(timeout = 10000)
    public void chompEscapedBalanced_cf1804_failAssert12_literalMutation1971_failAssert24() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue("F]JZNr 8m{vtctU`+]ls({S&n-6[>6Jncm%^0X<!");
                java.lang.String pre = tq.consumeTo("(");
                java.lang.String guts = tq.chompBalanced('(', ')');
                java.lang.String remainder = tq.remainder();
                // MethodAssertGenerator build local variable
                Object o_11_0 = org.jsoup.parser.TokenQueue.unescape(guts);
                // StatementAdderOnAssert create null value
                org.jsoup.parser.Token vc_234 = (org.jsoup.parser.Token)null;
                // StatementAdderMethod cloned existing statement
                vc_234.isStartTag();
                org.junit.Assert.fail("chompEscapedBalanced_cf1804 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("chompEscapedBalanced_cf1804_failAssert12_literalMutation1971 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompEscapedBalanced */
    @org.junit.Test(timeout = 10000)
    public void chompEscapedBalanced_cf1818_cf1858_failAssert35_literalMutation2248_failAssert18() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue("13S(GMZ?1&*1vG5s7#B!yTS8}c6H9eV&5`LpC`Fb");
                java.lang.String pre = tq.consumeTo("(");
                java.lang.String guts = tq.chompBalanced('(', ')');
                java.lang.String remainder = tq.remainder();
                // MethodAssertGenerator build local variable
                Object o_11_0 = org.jsoup.parser.TokenQueue.unescape(guts);
                // StatementAdderOnAssert create null value
                java.lang.StringBuilder vc_250 = (java.lang.StringBuilder)null;
                // MethodAssertGenerator build local variable
                Object o_15_0 = vc_250;
                // StatementAdderOnAssert create null value
                org.jsoup.parser.Token vc_248 = (org.jsoup.parser.Token)null;
                // MethodAssertGenerator build local variable
                Object o_19_0 = vc_248;
                // StatementAdderMethod cloned existing statement
                vc_248.reset(vc_250);
                // MethodAssertGenerator build local variable
                Object o_23_0 = vc_248;
                // StatementAdderMethod cloned existing statement
                vc_248.asDoctype();
                org.junit.Assert.fail("chompEscapedBalanced_cf1818_cf1858 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("chompEscapedBalanced_cf1818_cf1858_failAssert35_literalMutation2248 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompToIgnoreCase */
    @org.junit.Test
    public void chompToIgnoreCase_literalMutation2541_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String t = "<textarea>one < two </TEXTarea>";
            org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(t);
            java.lang.String data = tq.chompToIgnoreCase("");
            tq = new org.jsoup.parser.TokenQueue("<textarea> one two < three </oops>");
            data = tq.chompToIgnoreCase("</textarea");
            org.junit.Assert.fail("chompToIgnoreCase_literalMutation2541 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompToIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void chompToIgnoreCase_cf2581() {
        java.lang.String t = "<textarea>one < two </TEXTarea>";
        org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(t);
        java.lang.String data = tq.chompToIgnoreCase("</textarea");
        org.junit.Assert.assertEquals("<textarea>one < two ", data);
        tq = new org.jsoup.parser.TokenQueue("<textarea> one two < three </oops>");
        data = tq.chompToIgnoreCase("</textarea");
        // StatementAdderOnAssert create random local variable
        java.lang.StringBuilder vc_335 = new java.lang.StringBuilder();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)((java.lang.StringBuilder)vc_335).reverse()).capacity(), 16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)((java.lang.StringBuilder)vc_335).reverse()).length(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)vc_335).capacity(), 16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.StringBuilder)((java.lang.StringBuilder)vc_335).reverse()).reverse().equals(vc_335));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)((java.lang.StringBuilder)((java.lang.StringBuilder)vc_335).reverse()).reverse()).length(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)((java.lang.StringBuilder)((java.lang.StringBuilder)vc_335).reverse()).reverse()).capacity(), 16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)vc_335).length(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.StringBuilder)((java.lang.StringBuilder)((java.lang.StringBuilder)vc_335).reverse()).reverse()).reverse().equals(vc_335));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.StringBuilder)vc_335).reverse().equals(vc_335));
        // StatementAdderOnAssert create null value
        org.jsoup.parser.Token vc_332 = (org.jsoup.parser.Token)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_332);
        // StatementAdderMethod cloned existing statement
        vc_332.reset(vc_335);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_332);
        org.junit.Assert.assertEquals("<textarea> one two < three </oops>", data);
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompToIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void chompToIgnoreCase_cf2580() {
        java.lang.String t = "<textarea>one < two </TEXTarea>";
        org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(t);
        java.lang.String data = tq.chompToIgnoreCase("</textarea");
        org.junit.Assert.assertEquals("<textarea>one < two ", data);
        tq = new org.jsoup.parser.TokenQueue("<textarea> one two < three </oops>");
        data = tq.chompToIgnoreCase("</textarea");
        // StatementAdderOnAssert create null value
        java.lang.StringBuilder vc_334 = (java.lang.StringBuilder)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_334);
        // StatementAdderOnAssert create null value
        org.jsoup.parser.Token vc_332 = (org.jsoup.parser.Token)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_332);
        // StatementAdderMethod cloned existing statement
        vc_332.reset(vc_334);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_332);
        org.junit.Assert.assertEquals("<textarea> one two < three </oops>", data);
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompToIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void chompToIgnoreCase_cf2564_failAssert19_literalMutation2985_failAssert73() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String t = "<textarea>one < two </TEXTarea>";
                org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(t);
                java.lang.String data = tq.chompToIgnoreCase("");
                tq = new org.jsoup.parser.TokenQueue("<textarea> one two < three </oops>");
                data = tq.chompToIgnoreCase("</textarea");
                // StatementAdderOnAssert create null value
                org.jsoup.parser.Token vc_316 = (org.jsoup.parser.Token)null;
                // StatementAdderMethod cloned existing statement
                vc_316.isEndTag();
                org.junit.Assert.fail("chompToIgnoreCase_cf2564 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("chompToIgnoreCase_cf2564_failAssert19_literalMutation2985 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#chompToIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void chompToIgnoreCase_literalMutation2552_cf2604_failAssert46_literalMutation3766_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String t = "<textarea>one < two </TEXTarea>";
                org.jsoup.parser.TokenQueue tq = new org.jsoup.parser.TokenQueue(t);
                java.lang.String data = tq.chompToIgnoreCase("");
                tq = new org.jsoup.parser.TokenQueue("<textarea> one two < three </oops>");
                data = tq.chompToIgnoreCase("</textavea");
                // MethodAssertGenerator build local variable
                Object o_11_0 = data;
                // StatementAdderOnAssert create null value
                org.jsoup.parser.Token vc_336 = (org.jsoup.parser.Token)null;
                // StatementAdderMethod cloned existing statement
                vc_336.isCharacter();
                org.junit.Assert.fail("chompToIgnoreCase_literalMutation2552_cf2604 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("chompToIgnoreCase_literalMutation2552_cf2604_failAssert46_literalMutation3766 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#unescape */
    @org.junit.Test(timeout = 10000)
    public void unescape_cf4186() {
        // StatementAdderOnAssert create random local variable
        java.lang.StringBuilder vc_503 = new java.lang.StringBuilder();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)((java.lang.StringBuilder)vc_503).reverse()).length(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)((java.lang.StringBuilder)vc_503).reverse()).capacity(), 16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)vc_503).capacity(), 16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.StringBuilder)((java.lang.StringBuilder)((java.lang.StringBuilder)vc_503).reverse()).reverse()).reverse().equals(vc_503));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)((java.lang.StringBuilder)((java.lang.StringBuilder)vc_503).reverse()).reverse()).length(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.StringBuilder)vc_503).reverse().equals(vc_503));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)vc_503).length(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuilder)((java.lang.StringBuilder)((java.lang.StringBuilder)vc_503).reverse()).reverse()).capacity(), 16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.StringBuilder)((java.lang.StringBuilder)vc_503).reverse()).reverse().equals(vc_503));
        // StatementAdderOnAssert create null value
        org.jsoup.parser.Token vc_500 = (org.jsoup.parser.Token)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_500);
        // StatementAdderMethod cloned existing statement
        vc_500.reset(vc_503);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_500);
        org.junit.Assert.assertEquals("one ( ) \\", org.jsoup.parser.TokenQueue.unescape("one \\( \\) \\\\"));
    }

    /* amplification of org.jsoup.parser.TokenQueueTest#unescape */
    @org.junit.Test(timeout = 10000)
    public void unescape_cf4185() {
        // StatementAdderOnAssert create null value
        java.lang.StringBuilder vc_502 = (java.lang.StringBuilder)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_502);
        // StatementAdderOnAssert create null value
        org.jsoup.parser.Token vc_500 = (org.jsoup.parser.Token)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_500);
        // StatementAdderMethod cloned existing statement
        vc_500.reset(vc_502);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_500);
        org.junit.Assert.assertEquals("one ( ) \\", org.jsoup.parser.TokenQueue.unescape("one \\( \\) \\\\"));
    }
}

