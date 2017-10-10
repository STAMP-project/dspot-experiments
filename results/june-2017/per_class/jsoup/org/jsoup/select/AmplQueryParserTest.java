

package org.jsoup.select;


/**
 * Tests for the Selector Query Parser.
 *
 * @author Jonathan Hedley
 */
public class AmplQueryParserTest {
    @org.junit.Test
    public void testOrGetsCorrectPrecedence() {
        // tests that a selector "a b, c d, e f" evals to (a AND b) OR (c AND d) OR (e AND f)"
        // top level or, three child ands
        org.jsoup.select.Evaluator eval = org.jsoup.select.QueryParser.parse("a b, c d, e f");
        org.junit.Assert.assertTrue((eval instanceof org.jsoup.select.CombiningEvaluator.Or));
        org.jsoup.select.CombiningEvaluator.Or or = ((org.jsoup.select.CombiningEvaluator.Or) (eval));
        org.junit.Assert.assertEquals(3, or.evaluators.size());
        for (org.jsoup.select.Evaluator innerEval : or.evaluators) {
            org.junit.Assert.assertTrue((innerEval instanceof org.jsoup.select.CombiningEvaluator.And));
            org.jsoup.select.CombiningEvaluator.And and = ((org.jsoup.select.CombiningEvaluator.And) (innerEval));
            org.junit.Assert.assertEquals(2, and.evaluators.size());
            org.junit.Assert.assertTrue(((and.evaluators.get(0)) instanceof org.jsoup.select.Evaluator.Tag));
            org.junit.Assert.assertTrue(((and.evaluators.get(1)) instanceof org.jsoup.select.StructuralEvaluator.Parent));
        }
    }

    @org.junit.Test
    public void testParsesMultiCorrectly() {
        org.jsoup.select.Evaluator eval = org.jsoup.select.QueryParser.parse(".foo > ol, ol > li + li");
        org.junit.Assert.assertTrue((eval instanceof org.jsoup.select.CombiningEvaluator.Or));
        org.jsoup.select.CombiningEvaluator.Or or = ((org.jsoup.select.CombiningEvaluator.Or) (eval));
        org.junit.Assert.assertEquals(2, or.evaluators.size());
        org.jsoup.select.CombiningEvaluator.And andLeft = ((org.jsoup.select.CombiningEvaluator.And) (or.evaluators.get(0)));
        org.jsoup.select.CombiningEvaluator.And andRight = ((org.jsoup.select.CombiningEvaluator.And) (or.evaluators.get(1)));
        org.junit.Assert.assertEquals("ol :ImmediateParent.foo", andLeft.toString());
        org.junit.Assert.assertEquals(2, andLeft.evaluators.size());
        org.junit.Assert.assertEquals("li :prevli :ImmediateParentol", andRight.toString());
        org.junit.Assert.assertEquals(2, andLeft.evaluators.size());
    }

    @org.junit.Test(expected = org.jsoup.select.Selector.SelectorParseException.class)
    public void exceptionOnUncloseAttribute() {
        org.jsoup.select.Evaluator parse = org.jsoup.select.QueryParser.parse("section > a[href=\"]");
    }

    @org.junit.Test(expected = org.jsoup.select.Selector.SelectorParseException.class)
    public void testParsesSingleQuoteInContains() {
        org.jsoup.select.Evaluator parse = org.jsoup.select.QueryParser.parse("p:contains(One \" One)");
    }

    /* amplification of org.jsoup.select.QueryParserTest#testOrGetsCorrectPrecedence */
    @org.junit.Test(timeout = 10000)
    public void testOrGetsCorrectPrecedence_cf133_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // tests that a selector "a b, c d, e f" evals to (a AND b) OR (c AND d) OR (e AND f)"
            // top level or, three child ands
            org.jsoup.select.Evaluator eval = org.jsoup.select.QueryParser.parse("a b, c d, e f");
            // MethodAssertGenerator build local variable
            Object o_5_0 = eval instanceof org.jsoup.select.CombiningEvaluator.Or;
            org.jsoup.select.CombiningEvaluator.Or or = ((org.jsoup.select.CombiningEvaluator.Or) (eval));
            // MethodAssertGenerator build local variable
            Object o_7_0 = or.evaluators.size();
            for (org.jsoup.select.Evaluator innerEval : or.evaluators) {
                // MethodAssertGenerator build local variable
                Object o_12_0 = innerEval instanceof org.jsoup.select.CombiningEvaluator.And;
                org.jsoup.select.CombiningEvaluator.And and = ((org.jsoup.select.CombiningEvaluator.And) (innerEval));
                // MethodAssertGenerator build local variable
                Object o_14_0 = and.evaluators.size();
                // MethodAssertGenerator build local variable
                Object o_16_0 = (and.evaluators.get(0)) instanceof org.jsoup.select.Evaluator.Tag;
                // StatementAdderOnAssert create null value
                java.lang.String vc_4 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                org.jsoup.select.QueryParser vc_2 = (org.jsoup.select.QueryParser)null;
                // StatementAdderMethod cloned existing statement
                vc_2.parse(vc_4);
                // MethodAssertGenerator build local variable
                Object o_24_0 = (and.evaluators.get(1)) instanceof org.jsoup.select.StructuralEvaluator.Parent;
            }
            org.junit.Assert.fail("testOrGetsCorrectPrecedence_cf133 should have thrown SelectorParseException");
        } catch (org.jsoup.select.Selector.SelectorParseException eee) {
        }
    }

    /* amplification of org.jsoup.select.QueryParserTest#testOrGetsCorrectPrecedence */
    @org.junit.Test
    public void testOrGetsCorrectPrecedence_literalMutation120_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // tests that a selector "a b, c d, e f" evals to (a AND b) OR (c AND d) OR (e AND f)"
            // top level or, three child ands
            org.jsoup.select.Evaluator eval = org.jsoup.select.QueryParser.parse("");
            // MethodAssertGenerator build local variable
            Object o_5_0 = eval instanceof org.jsoup.select.CombiningEvaluator.Or;
            org.jsoup.select.CombiningEvaluator.Or or = ((org.jsoup.select.CombiningEvaluator.Or) (eval));
            // MethodAssertGenerator build local variable
            Object o_7_0 = or.evaluators.size();
            for (org.jsoup.select.Evaluator innerEval : or.evaluators) {
                // MethodAssertGenerator build local variable
                Object o_12_0 = innerEval instanceof org.jsoup.select.CombiningEvaluator.And;
                org.jsoup.select.CombiningEvaluator.And and = ((org.jsoup.select.CombiningEvaluator.And) (innerEval));
                // MethodAssertGenerator build local variable
                Object o_14_0 = and.evaluators.size();
                // MethodAssertGenerator build local variable
                Object o_16_0 = (and.evaluators.get(0)) instanceof org.jsoup.select.Evaluator.Tag;
                // MethodAssertGenerator build local variable
                Object o_18_0 = (and.evaluators.get(1)) instanceof org.jsoup.select.StructuralEvaluator.Parent;
            }
            org.junit.Assert.fail("testOrGetsCorrectPrecedence_literalMutation120 should have thrown SelectorParseException");
        } catch (org.jsoup.select.Selector.SelectorParseException eee) {
        }
    }

    /* amplification of org.jsoup.select.QueryParserTest#testOrGetsCorrectPrecedence */
    @org.junit.Test
    public void testOrGetsCorrectPrecedence_literalMutation126_failAssert4_literalMutation190_failAssert20() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // tests that a selector "a b, c d, e f" evals to (a AND b) OR (c AND d) OR (e AND f)"
                // top level or, three child ands
                org.jsoup.select.Evaluator eval = org.jsoup.select.QueryParser.parse("");
                // MethodAssertGenerator build local variable
                Object o_5_0 = eval instanceof org.jsoup.select.CombiningEvaluator.Or;
                org.jsoup.select.CombiningEvaluator.Or or = ((org.jsoup.select.CombiningEvaluator.Or) (eval));
                // MethodAssertGenerator build local variable
                Object o_7_0 = or.evaluators.size();
                for (org.jsoup.select.Evaluator innerEval : or.evaluators) {
                    // MethodAssertGenerator build local variable
                    Object o_12_0 = innerEval instanceof org.jsoup.select.CombiningEvaluator.And;
                    org.jsoup.select.CombiningEvaluator.And and = ((org.jsoup.select.CombiningEvaluator.And) (innerEval));
                    // MethodAssertGenerator build local variable
                    Object o_14_0 = and.evaluators.size();
                    // MethodAssertGenerator build local variable
                    Object o_16_0 = (and.evaluators.get(-1)) instanceof org.jsoup.select.Evaluator.Tag;
                    // MethodAssertGenerator build local variable
                    Object o_19_0 = (and.evaluators.get(1)) instanceof org.jsoup.select.StructuralEvaluator.Parent;
                }
                org.junit.Assert.fail("testOrGetsCorrectPrecedence_literalMutation126 should have thrown ArrayIndexOutOfBoundsException");
            } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("testOrGetsCorrectPrecedence_literalMutation126_failAssert4_literalMutation190 should have thrown SelectorParseException");
        } catch (org.jsoup.select.Selector.SelectorParseException eee) {
        }
    }

    /* amplification of org.jsoup.select.QueryParserTest#testOrGetsCorrectPrecedence */
    @org.junit.Test(timeout = 10000)
    public void testOrGetsCorrectPrecedence_cf134_cf159_failAssert15() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // tests that a selector "a b, c d, e f" evals to (a AND b) OR (c AND d) OR (e AND f)"
            // top level or, three child ands
            org.jsoup.select.Evaluator eval = org.jsoup.select.QueryParser.parse("a b, c d, e f");
            // MethodAssertGenerator build local variable
            Object o_5_0 = eval instanceof org.jsoup.select.CombiningEvaluator.Or;
            org.jsoup.select.CombiningEvaluator.Or or = ((org.jsoup.select.CombiningEvaluator.Or) (eval));
            // MethodAssertGenerator build local variable
            Object o_7_0 = or.evaluators.size();
            for (org.jsoup.select.Evaluator innerEval : or.evaluators) {
                // MethodAssertGenerator build local variable
                Object o_12_0 = innerEval instanceof org.jsoup.select.CombiningEvaluator.And;
                org.jsoup.select.CombiningEvaluator.And and = ((org.jsoup.select.CombiningEvaluator.And) (innerEval));
                // MethodAssertGenerator build local variable
                Object o_14_0 = and.evaluators.size();
                // MethodAssertGenerator build local variable
                Object o_16_0 = (and.evaluators.get(0)) instanceof org.jsoup.select.Evaluator.Tag;
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_0 = "a b, c d, e f";
                // MethodAssertGenerator build local variable
                Object o_20_0 = String_vc_0;
                // StatementAdderOnAssert create null value
                org.jsoup.select.QueryParser vc_2 = (org.jsoup.select.QueryParser)null;
                // MethodAssertGenerator build local variable
                Object o_24_0 = vc_2;
                // StatementAdderMethod cloned existing statement
                vc_2.parse(String_vc_0);
                // StatementAdderOnAssert create null value
                java.lang.String vc_10 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                org.jsoup.select.QueryParser vc_8 = (org.jsoup.select.QueryParser)null;
                // StatementAdderMethod cloned existing statement
                vc_8.parse(vc_10);
                // MethodAssertGenerator build local variable
                Object o_34_0 = (and.evaluators.get(1)) instanceof org.jsoup.select.StructuralEvaluator.Parent;
            }
            org.junit.Assert.fail("testOrGetsCorrectPrecedence_cf134_cf159 should have thrown SelectorParseException");
        } catch (org.jsoup.select.Selector.SelectorParseException eee) {
        }
    }

    /* amplification of org.jsoup.select.QueryParserTest#testOrGetsCorrectPrecedence */
    @org.junit.Test
    public void testOrGetsCorrectPrecedence_literalMutation126_failAssert4_literalMutation193_failAssert23() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // tests that a selector "a b, c d, e f" evals to (a AND b) OR (c AND d) OR (e AND f)"
                // top level or, three child ands
                org.jsoup.select.Evaluator eval = org.jsoup.select.QueryParser.parse("A!L#vK5WR{oE1");
                // MethodAssertGenerator build local variable
                Object o_5_0 = eval instanceof org.jsoup.select.CombiningEvaluator.Or;
                org.jsoup.select.CombiningEvaluator.Or or = ((org.jsoup.select.CombiningEvaluator.Or) (eval));
                // MethodAssertGenerator build local variable
                Object o_7_0 = or.evaluators.size();
                for (org.jsoup.select.Evaluator innerEval : or.evaluators) {
                    // MethodAssertGenerator build local variable
                    Object o_12_0 = innerEval instanceof org.jsoup.select.CombiningEvaluator.And;
                    org.jsoup.select.CombiningEvaluator.And and = ((org.jsoup.select.CombiningEvaluator.And) (innerEval));
                    // MethodAssertGenerator build local variable
                    Object o_14_0 = and.evaluators.size();
                    // MethodAssertGenerator build local variable
                    Object o_16_0 = (and.evaluators.get(-1)) instanceof org.jsoup.select.Evaluator.Tag;
                    // MethodAssertGenerator build local variable
                    Object o_19_0 = (and.evaluators.get(1)) instanceof org.jsoup.select.StructuralEvaluator.Parent;
                }
                org.junit.Assert.fail("testOrGetsCorrectPrecedence_literalMutation126 should have thrown ArrayIndexOutOfBoundsException");
            } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("testOrGetsCorrectPrecedence_literalMutation126_failAssert4_literalMutation193 should have thrown SelectorParseException");
        } catch (org.jsoup.select.Selector.SelectorParseException eee) {
        }
    }

    /* amplification of org.jsoup.select.QueryParserTest#testOrGetsCorrectPrecedence */
    @org.junit.Test(timeout = 10000)
    public void testOrGetsCorrectPrecedence_cf134_literalMutation153_failAssert10_literalMutation366_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // tests that a selector "a b, c d, e f" evals to (a AND b) OR (c AND d) OR (e AND f)"
                // top level or, three child ands
                org.jsoup.select.Evaluator eval = org.jsoup.select.QueryParser.parse("uV!aV(v6Iw9b>");
                // MethodAssertGenerator build local variable
                Object o_5_0 = eval instanceof org.jsoup.select.CombiningEvaluator.Or;
                org.jsoup.select.CombiningEvaluator.Or or = ((org.jsoup.select.CombiningEvaluator.Or) (eval));
                // MethodAssertGenerator build local variable
                Object o_7_0 = or.evaluators.size();
                for (org.jsoup.select.Evaluator innerEval : or.evaluators) {
                    // MethodAssertGenerator build local variable
                    Object o_12_0 = innerEval instanceof org.jsoup.select.CombiningEvaluator.And;
                    org.jsoup.select.CombiningEvaluator.And and = ((org.jsoup.select.CombiningEvaluator.And) (innerEval));
                    // MethodAssertGenerator build local variable
                    Object o_14_0 = and.evaluators.size();
                    // MethodAssertGenerator build local variable
                    Object o_16_0 = (and.evaluators.get(0)) instanceof org.jsoup.select.Evaluator.Tag;
                    // StatementAdderOnAssert create literal from method
                    java.lang.String String_vc_0 = "a b, c d, e f";
                    // MethodAssertGenerator build local variable
                    Object o_20_0 = String_vc_0;
                    // StatementAdderOnAssert create null value
                    org.jsoup.select.QueryParser vc_2 = (org.jsoup.select.QueryParser)null;
                    // MethodAssertGenerator build local variable
                    Object o_24_0 = vc_2;
                    // StatementAdderMethod cloned existing statement
                    vc_2.parse(String_vc_0);
                    // MethodAssertGenerator build local variable
                    Object o_28_0 = (and.evaluators.get(2)) instanceof org.jsoup.select.StructuralEvaluator.Parent;
                }
                org.junit.Assert.fail("testOrGetsCorrectPrecedence_cf134_literalMutation153 should have thrown IndexOutOfBoundsException");
            } catch (java.lang.IndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("testOrGetsCorrectPrecedence_cf134_literalMutation153_failAssert10_literalMutation366 should have thrown SelectorParseException");
        } catch (org.jsoup.select.Selector.SelectorParseException eee) {
        }
    }

    /* amplification of org.jsoup.select.QueryParserTest#testOrGetsCorrectPrecedence */
    @org.junit.Test(timeout = 10000)
    public void testOrGetsCorrectPrecedence_cf134_literalMutation153_failAssert10_literalMutation362_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // tests that a selector "a b, c d, e f" evals to (a AND b) OR (c AND d) OR (e AND f)"
                // top level or, three child ands
                org.jsoup.select.Evaluator eval = org.jsoup.select.QueryParser.parse("");
                // MethodAssertGenerator build local variable
                Object o_5_0 = eval instanceof org.jsoup.select.CombiningEvaluator.Or;
                org.jsoup.select.CombiningEvaluator.Or or = ((org.jsoup.select.CombiningEvaluator.Or) (eval));
                // MethodAssertGenerator build local variable
                Object o_7_0 = or.evaluators.size();
                for (org.jsoup.select.Evaluator innerEval : or.evaluators) {
                    // MethodAssertGenerator build local variable
                    Object o_12_0 = innerEval instanceof org.jsoup.select.CombiningEvaluator.And;
                    org.jsoup.select.CombiningEvaluator.And and = ((org.jsoup.select.CombiningEvaluator.And) (innerEval));
                    // MethodAssertGenerator build local variable
                    Object o_14_0 = and.evaluators.size();
                    // MethodAssertGenerator build local variable
                    Object o_16_0 = (and.evaluators.get(0)) instanceof org.jsoup.select.Evaluator.Tag;
                    // StatementAdderOnAssert create literal from method
                    java.lang.String String_vc_0 = "a b, c d, e f";
                    // MethodAssertGenerator build local variable
                    Object o_20_0 = String_vc_0;
                    // StatementAdderOnAssert create null value
                    org.jsoup.select.QueryParser vc_2 = (org.jsoup.select.QueryParser)null;
                    // MethodAssertGenerator build local variable
                    Object o_24_0 = vc_2;
                    // StatementAdderMethod cloned existing statement
                    vc_2.parse(String_vc_0);
                    // MethodAssertGenerator build local variable
                    Object o_28_0 = (and.evaluators.get(2)) instanceof org.jsoup.select.StructuralEvaluator.Parent;
                }
                org.junit.Assert.fail("testOrGetsCorrectPrecedence_cf134_literalMutation153 should have thrown IndexOutOfBoundsException");
            } catch (java.lang.IndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("testOrGetsCorrectPrecedence_cf134_literalMutation153_failAssert10_literalMutation362 should have thrown SelectorParseException");
        } catch (org.jsoup.select.Selector.SelectorParseException eee) {
        }
    }

    /* amplification of org.jsoup.select.QueryParserTest#testParsesMultiCorrectly */
    @org.junit.Test
    public void testParsesMultiCorrectly_literalMutation625_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.select.Evaluator eval = org.jsoup.select.QueryParser.parse(".foo > ol, ol > li; + li");
            // MethodAssertGenerator build local variable
            Object o_3_0 = eval instanceof org.jsoup.select.CombiningEvaluator.Or;
            org.jsoup.select.CombiningEvaluator.Or or = ((org.jsoup.select.CombiningEvaluator.Or) (eval));
            // MethodAssertGenerator build local variable
            Object o_5_0 = or.evaluators.size();
            org.jsoup.select.CombiningEvaluator.And andLeft = ((org.jsoup.select.CombiningEvaluator.And) (or.evaluators.get(0)));
            org.jsoup.select.CombiningEvaluator.And andRight = ((org.jsoup.select.CombiningEvaluator.And) (or.evaluators.get(1)));
            // MethodAssertGenerator build local variable
            Object o_11_0 = andLeft.toString();
            // MethodAssertGenerator build local variable
            Object o_13_0 = andLeft.evaluators.size();
            // MethodAssertGenerator build local variable
            Object o_15_0 = andRight.toString();
            // MethodAssertGenerator build local variable
            Object o_17_0 = andLeft.evaluators.size();
            org.junit.Assert.fail("testParsesMultiCorrectly_literalMutation625 should have thrown SelectorParseException");
        } catch (org.jsoup.select.Selector.SelectorParseException eee) {
        }
    }

    /* amplification of org.jsoup.select.QueryParserTest#testParsesMultiCorrectly */
    @org.junit.Test(timeout = 10000)
    public void testParsesMultiCorrectly_cf635_failAssert11() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.select.Evaluator eval = org.jsoup.select.QueryParser.parse(".foo > ol, ol > li + li");
            // MethodAssertGenerator build local variable
            Object o_3_0 = eval instanceof org.jsoup.select.CombiningEvaluator.Or;
            org.jsoup.select.CombiningEvaluator.Or or = ((org.jsoup.select.CombiningEvaluator.Or) (eval));
            // MethodAssertGenerator build local variable
            Object o_5_0 = or.evaluators.size();
            org.jsoup.select.CombiningEvaluator.And andLeft = ((org.jsoup.select.CombiningEvaluator.And) (or.evaluators.get(0)));
            org.jsoup.select.CombiningEvaluator.And andRight = ((org.jsoup.select.CombiningEvaluator.And) (or.evaluators.get(1)));
            // MethodAssertGenerator build local variable
            Object o_11_0 = andLeft.toString();
            // MethodAssertGenerator build local variable
            Object o_13_0 = andLeft.evaluators.size();
            // MethodAssertGenerator build local variable
            Object o_15_0 = andRight.toString();
            // StatementAdderOnAssert create null value
            java.lang.String vc_16 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.select.QueryParser vc_14 = (org.jsoup.select.QueryParser)null;
            // StatementAdderMethod cloned existing statement
            vc_14.parse(vc_16);
            // MethodAssertGenerator build local variable
            Object o_23_0 = andLeft.evaluators.size();
            org.junit.Assert.fail("testParsesMultiCorrectly_cf635 should have thrown SelectorParseException");
        } catch (org.jsoup.select.Selector.SelectorParseException eee) {
        }
    }

    /* amplification of org.jsoup.select.QueryParserTest#testParsesMultiCorrectly */
    @org.junit.Test
    public void testParsesMultiCorrectly_literalMutation622_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.select.Evaluator eval = org.jsoup.select.QueryParser.parse("");
            // MethodAssertGenerator build local variable
            Object o_3_0 = eval instanceof org.jsoup.select.CombiningEvaluator.Or;
            org.jsoup.select.CombiningEvaluator.Or or = ((org.jsoup.select.CombiningEvaluator.Or) (eval));
            // MethodAssertGenerator build local variable
            Object o_5_0 = or.evaluators.size();
            org.jsoup.select.CombiningEvaluator.And andLeft = ((org.jsoup.select.CombiningEvaluator.And) (or.evaluators.get(0)));
            org.jsoup.select.CombiningEvaluator.And andRight = ((org.jsoup.select.CombiningEvaluator.And) (or.evaluators.get(1)));
            // MethodAssertGenerator build local variable
            Object o_11_0 = andLeft.toString();
            // MethodAssertGenerator build local variable
            Object o_13_0 = andLeft.evaluators.size();
            // MethodAssertGenerator build local variable
            Object o_15_0 = andRight.toString();
            // MethodAssertGenerator build local variable
            Object o_17_0 = andLeft.evaluators.size();
            org.junit.Assert.fail("testParsesMultiCorrectly_literalMutation622 should have thrown SelectorParseException");
        } catch (org.jsoup.select.Selector.SelectorParseException eee) {
        }
    }

    /* amplification of org.jsoup.select.QueryParserTest#testParsesMultiCorrectly */
    @org.junit.Test
    public void testParsesMultiCorrectly_literalMutation630_failAssert7_literalMutation693_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.select.Evaluator eval = org.jsoup.select.QueryParser.parse("");
                // MethodAssertGenerator build local variable
                Object o_3_0 = eval instanceof org.jsoup.select.CombiningEvaluator.Or;
                org.jsoup.select.CombiningEvaluator.Or or = ((org.jsoup.select.CombiningEvaluator.Or) (eval));
                // MethodAssertGenerator build local variable
                Object o_5_0 = or.evaluators.size();
                org.jsoup.select.CombiningEvaluator.And andLeft = ((org.jsoup.select.CombiningEvaluator.And) (or.evaluators.get(0)));
                org.jsoup.select.CombiningEvaluator.And andRight = ((org.jsoup.select.CombiningEvaluator.And) (or.evaluators.get(2)));
                // MethodAssertGenerator build local variable
                Object o_12_0 = andLeft.toString();
                // MethodAssertGenerator build local variable
                Object o_14_0 = andLeft.evaluators.size();
                // MethodAssertGenerator build local variable
                Object o_16_0 = andRight.toString();
                // MethodAssertGenerator build local variable
                Object o_18_0 = andLeft.evaluators.size();
                org.junit.Assert.fail("testParsesMultiCorrectly_literalMutation630 should have thrown IndexOutOfBoundsException");
            } catch (java.lang.IndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("testParsesMultiCorrectly_literalMutation630_failAssert7_literalMutation693 should have thrown SelectorParseException");
        } catch (org.jsoup.select.Selector.SelectorParseException eee) {
        }
    }

    /* amplification of org.jsoup.select.QueryParserTest#testParsesMultiCorrectly */
    @org.junit.Test
    public void testParsesMultiCorrectly_literalMutation628_failAssert6_literalMutation686_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.select.Evaluator eval = org.jsoup.select.QueryParser.parse("F]or`woVZX>^L,%i.9>C9lx");
                // MethodAssertGenerator build local variable
                Object o_3_0 = eval instanceof org.jsoup.select.CombiningEvaluator.Or;
                org.jsoup.select.CombiningEvaluator.Or or = ((org.jsoup.select.CombiningEvaluator.Or) (eval));
                // MethodAssertGenerator build local variable
                Object o_5_0 = or.evaluators.size();
                org.jsoup.select.CombiningEvaluator.And andLeft = ((org.jsoup.select.CombiningEvaluator.And) (or.evaluators.get(-1)));
                org.jsoup.select.CombiningEvaluator.And andRight = ((org.jsoup.select.CombiningEvaluator.And) (or.evaluators.get(1)));
                // MethodAssertGenerator build local variable
                Object o_12_0 = andLeft.toString();
                // MethodAssertGenerator build local variable
                Object o_14_0 = andLeft.evaluators.size();
                // MethodAssertGenerator build local variable
                Object o_16_0 = andRight.toString();
                // MethodAssertGenerator build local variable
                Object o_18_0 = andLeft.evaluators.size();
                org.junit.Assert.fail("testParsesMultiCorrectly_literalMutation628 should have thrown ArrayIndexOutOfBoundsException");
            } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("testParsesMultiCorrectly_literalMutation628_failAssert6_literalMutation686 should have thrown SelectorParseException");
        } catch (org.jsoup.select.Selector.SelectorParseException eee) {
        }
    }

    /* amplification of org.jsoup.select.QueryParserTest#testParsesMultiCorrectly */
    @org.junit.Test(timeout = 10000)
    public void testParsesMultiCorrectly_cf633_failAssert10_literalMutation713_failAssert11_literalMutation838_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // AssertGenerator generate try/catch block with fail statement
                try {
                    org.jsoup.select.Evaluator eval = org.jsoup.select.QueryParser.parse("");
                    // MethodAssertGenerator build local variable
                    Object o_3_0 = eval instanceof org.jsoup.select.CombiningEvaluator.Or;
                    org.jsoup.select.CombiningEvaluator.Or or = ((org.jsoup.select.CombiningEvaluator.Or) (eval));
                    // MethodAssertGenerator build local variable
                    Object o_5_0 = or.evaluators.size();
                    org.jsoup.select.CombiningEvaluator.And andLeft = ((org.jsoup.select.CombiningEvaluator.And) (or.evaluators.get(0)));
                    org.jsoup.select.CombiningEvaluator.And andRight = ((org.jsoup.select.CombiningEvaluator.And) (or.evaluators.get(2)));
                    // MethodAssertGenerator build local variable
                    Object o_11_0 = andLeft.toString();
                    // MethodAssertGenerator build local variable
                    Object o_13_0 = andLeft.evaluators.size();
                    // MethodAssertGenerator build local variable
                    Object o_15_0 = andRight.toString();
                    // StatementAdderOnAssert create null value
                    org.jsoup.select.QueryParser vc_12 = (org.jsoup.select.QueryParser)null;
                    // StatementAdderMethod cloned existing statement
                    vc_12.parse();
                    // MethodAssertGenerator build local variable
                    Object o_21_0 = andLeft.evaluators.size();
                    org.junit.Assert.fail("testParsesMultiCorrectly_cf633 should have thrown NullPointerException");
                } catch (java.lang.NullPointerException eee) {
                }
                org.junit.Assert.fail("testParsesMultiCorrectly_cf633_failAssert10_literalMutation713 should have thrown IndexOutOfBoundsException");
            } catch (java.lang.IndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("testParsesMultiCorrectly_cf633_failAssert10_literalMutation713_failAssert11_literalMutation838 should have thrown SelectorParseException");
        } catch (org.jsoup.select.Selector.SelectorParseException eee) {
        }
    }
}

