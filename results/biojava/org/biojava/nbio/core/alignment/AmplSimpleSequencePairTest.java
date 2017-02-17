/**
 * BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors.  These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 *      http://www.biojava.org/
 *
 * Created on June 15, 2010
 * Author: Mark Chapman
 */


package org.biojava.nbio.core.alignment;


public class AmplSimpleSequencePairTest {
    private org.biojava.nbio.core.sequence.ProteinSequence query;

    private org.biojava.nbio.core.sequence.ProteinSequence target;

    private org.biojava.nbio.core.alignment.template.SequencePair<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound> global;

    private org.biojava.nbio.core.alignment.template.SequencePair<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound> local;

    @org.junit.Before
    public void setup() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        query = new org.biojava.nbio.core.sequence.ProteinSequence("ARND");
        target = new org.biojava.nbio.core.sequence.ProteinSequence("RDG");
        global = new org.biojava.nbio.core.alignment.SimpleSequencePair<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound>(query, target, java.util.Arrays.asList(new org.biojava.nbio.core.alignment.template.AlignedSequence.Step[]{ org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP }), java.util.Arrays.asList(new org.biojava.nbio.core.alignment.template.AlignedSequence.Step[]{ org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND }));
        local = new org.biojava.nbio.core.alignment.SimpleSequencePair<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound>(query, target, java.util.Arrays.asList(new org.biojava.nbio.core.alignment.template.AlignedSequence.Step[]{ org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND }), 1, 0, java.util.Arrays.asList(new org.biojava.nbio.core.alignment.template.AlignedSequence.Step[]{ org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND }), 0, 1);
    }

    @org.junit.Test
    public void testGetCompoundInQueryAt() {
        org.junit.Assert.assertEquals(global.getCompoundInQueryAt(1).getShortName(), "A");
        org.junit.Assert.assertEquals(global.getCompoundInQueryAt(2).getShortName(), "R");
        org.junit.Assert.assertEquals(global.getCompoundInQueryAt(3).getShortName(), "N");
        org.junit.Assert.assertEquals(global.getCompoundInQueryAt(4).getShortName(), "D");
        org.junit.Assert.assertEquals(global.getCompoundInQueryAt(5).getShortName(), "-");
        org.junit.Assert.assertEquals(local.getCompoundInQueryAt(1).getShortName(), "R");
        org.junit.Assert.assertEquals(local.getCompoundInQueryAt(2).getShortName(), "N");
        org.junit.Assert.assertEquals(local.getCompoundInQueryAt(3).getShortName(), "D");
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundInQueryAtOutOfBounds() {
        global.getCompoundInQueryAt(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundInQueryAtOutOfBounds2() {
        global.getCompoundInQueryAt(6);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundInQueryAtOutOfBounds3() {
        local.getCompoundInQueryAt(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundInQueryAtOutOfBounds4() {
        local.getCompoundInQueryAt(4);
    }

    @org.junit.Test
    public void testGetCompoundInTargetAt() {
        org.junit.Assert.assertEquals(global.getCompoundInTargetAt(1).getShortName(), "-");
        org.junit.Assert.assertEquals(global.getCompoundInTargetAt(2).getShortName(), "R");
        org.junit.Assert.assertEquals(global.getCompoundInTargetAt(3).getShortName(), "-");
        org.junit.Assert.assertEquals(global.getCompoundInTargetAt(4).getShortName(), "D");
        org.junit.Assert.assertEquals(global.getCompoundInTargetAt(5).getShortName(), "G");
        org.junit.Assert.assertEquals(local.getCompoundInTargetAt(1).getShortName(), "R");
        org.junit.Assert.assertEquals(local.getCompoundInTargetAt(2).getShortName(), "-");
        org.junit.Assert.assertEquals(local.getCompoundInTargetAt(3).getShortName(), "D");
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundInTargetAtOutOfBounds() {
        global.getCompoundInTargetAt(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundInTargetAtOutOfBounds2() {
        global.getCompoundInTargetAt(6);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundInTargetAtOutOfBounds3() {
        local.getCompoundInTargetAt(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundInTargetAtOutOfBounds4() {
        local.getCompoundInTargetAt(4);
    }

    @org.junit.Test
    public void testGetIndexInQueryAt() {
        org.junit.Assert.assertEquals(global.getIndexInQueryAt(1), 1);
        org.junit.Assert.assertEquals(global.getIndexInQueryAt(2), 2);
        org.junit.Assert.assertEquals(global.getIndexInQueryAt(3), 3);
        org.junit.Assert.assertEquals(global.getIndexInQueryAt(4), 4);
        org.junit.Assert.assertEquals(global.getIndexInQueryAt(5), 4);
        org.junit.Assert.assertEquals(local.getIndexInQueryAt(1), 2);
        org.junit.Assert.assertEquals(local.getIndexInQueryAt(2), 3);
        org.junit.Assert.assertEquals(local.getIndexInQueryAt(3), 4);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetIndexInQueryAtOutOfBounds() {
        global.getIndexInQueryAt(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetIndexInQueryAtOutOfBounds2() {
        global.getIndexInQueryAt(6);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetIndexInQueryAtOutOfBounds3() {
        local.getIndexInQueryAt(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetIndexInQueryAtOutOfBounds4() {
        local.getIndexInQueryAt(4);
    }

    @org.junit.Test
    public void testGetIndexInQueryForTargetAt() {
        org.junit.Assert.assertEquals(global.getIndexInQueryForTargetAt(1), 2);
        org.junit.Assert.assertEquals(global.getIndexInQueryForTargetAt(2), 4);
        org.junit.Assert.assertEquals(global.getIndexInQueryForTargetAt(3), 4);
        org.junit.Assert.assertEquals(local.getIndexInQueryForTargetAt(1), 2);
        org.junit.Assert.assertEquals(local.getIndexInQueryForTargetAt(2), 4);
        org.junit.Assert.assertEquals(local.getIndexInQueryForTargetAt(3), 4);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetIndexInQueryForTargetAtOutOfBounds() {
        global.getIndexInQueryForTargetAt(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetIndexInQueryForTargetAtOutOfBounds2() {
        global.getIndexInQueryForTargetAt(4);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetIndexInQueryForTargetAtOutOfBounds3() {
        local.getIndexInQueryForTargetAt(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetIndexInQueryForTargetAtOutOfBounds4() {
        local.getIndexInQueryForTargetAt(4);
    }

    @org.junit.Test
    public void testGetIndexInTargetAt() {
        org.junit.Assert.assertEquals(global.getIndexInTargetAt(1), 1);
        org.junit.Assert.assertEquals(global.getIndexInTargetAt(2), 1);
        org.junit.Assert.assertEquals(global.getIndexInTargetAt(3), 1);
        org.junit.Assert.assertEquals(global.getIndexInTargetAt(4), 2);
        org.junit.Assert.assertEquals(global.getIndexInTargetAt(5), 3);
        org.junit.Assert.assertEquals(local.getIndexInTargetAt(1), 1);
        org.junit.Assert.assertEquals(local.getIndexInTargetAt(2), 1);
        org.junit.Assert.assertEquals(local.getIndexInTargetAt(3), 2);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetIndexInTargetAtOutOfBounds() {
        global.getIndexInTargetAt(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetIndexInTargetAtOutOfBounds2() {
        global.getIndexInTargetAt(6);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetIndexInTargetAtOutOfBounds3() {
        local.getIndexInTargetAt(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetIndexInTargetAtOutOfBounds4() {
        local.getIndexInTargetAt(4);
    }

    @org.junit.Test
    public void testGetIndexInTargetForQueryAt() {
        org.junit.Assert.assertEquals(global.getIndexInTargetForQueryAt(1), 1);
        org.junit.Assert.assertEquals(global.getIndexInTargetForQueryAt(2), 1);
        org.junit.Assert.assertEquals(global.getIndexInTargetForQueryAt(3), 1);
        org.junit.Assert.assertEquals(global.getIndexInTargetForQueryAt(4), 2);
        org.junit.Assert.assertEquals(local.getIndexInTargetForQueryAt(1), 1);
        org.junit.Assert.assertEquals(local.getIndexInTargetForQueryAt(2), 1);
        org.junit.Assert.assertEquals(local.getIndexInTargetForQueryAt(3), 1);
        org.junit.Assert.assertEquals(local.getIndexInTargetForQueryAt(4), 2);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetIndexInTargetForQueryAtOutOfBounds() {
        global.getIndexInTargetForQueryAt(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetIndexInTargetForQueryAtOutOfBounds2() {
        global.getIndexInTargetForQueryAt(5);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetIndexInTargetForQueryAtOutOfBounds3() {
        local.getIndexInTargetForQueryAt(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetIndexInTargetForQueryAtOutOfBounds4() {
        local.getIndexInTargetForQueryAt(5);
    }

    @org.junit.Test
    public void testGetNumIdenticals() {
        org.junit.Assert.assertEquals(global.getNumIdenticals(), 2);
        org.junit.Assert.assertEquals(local.getNumIdenticals(), 2);
    }

    @org.junit.Test
    public void testGetPercentageOfIdentity() {
        org.junit.Assert.assertEquals(global.getPercentageOfIdentity(), 1.0, 0.01);
        org.junit.Assert.assertEquals(local.getPercentageOfIdentity(), 1.0, 0.01);
    }

    @org.junit.Test
    public void testGetNumSimilars() {
        org.junit.Assert.assertEquals(global.getNumSimilars(), 2);
        org.junit.Assert.assertEquals(local.getNumSimilars(), 2);
    }

    @org.junit.Test
    public void testGetQuery() {
        org.junit.Assert.assertEquals(global.getQuery().getOriginalSequence(), query);
        org.junit.Assert.assertEquals(local.getQuery().getOriginalSequence(), query);
    }

    @org.junit.Test
    public void testGetTarget() {
        org.junit.Assert.assertEquals(global.getTarget().getOriginalSequence(), target);
        org.junit.Assert.assertEquals(local.getTarget().getOriginalSequence(), target);
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleSequencePairTest#testGetCompoundInQueryAt */
    @org.junit.Test(timeout = 10000)
    public void testGetCompoundInQueryAt_cf17_failAssert16() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = global.getCompoundInQueryAt(1).getShortName();
            // MethodAssertGenerator build local variable
            Object o_4_0 = global.getCompoundInQueryAt(2).getShortName();
            // MethodAssertGenerator build local variable
            Object o_7_0 = global.getCompoundInQueryAt(3).getShortName();
            // MethodAssertGenerator build local variable
            Object o_10_0 = global.getCompoundInQueryAt(4).getShortName();
            // MethodAssertGenerator build local variable
            Object o_13_0 = global.getCompoundInQueryAt(5).getShortName();
            // MethodAssertGenerator build local variable
            Object o_16_0 = local.getCompoundInQueryAt(1).getShortName();
            // MethodAssertGenerator build local variable
            Object o_19_0 = local.getCompoundInQueryAt(2).getShortName();
            // StatementAdderOnAssert create literal from method
            int int_vc_0 = 1;
            // StatementAdderOnAssert create null value
            org.biojava.nbio.core.sequence.template.Sequence vc_0 = (org.biojava.nbio.core.sequence.template.Sequence)null;
            // StatementAdderMethod cloned existing statement
            vc_0.getCompoundAt(int_vc_0);
            // MethodAssertGenerator build local variable
            Object o_28_0 = local.getCompoundInQueryAt(3).getShortName();
            org.junit.Assert.fail("testGetCompoundInQueryAt_cf17 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleSequencePairTest#testGetCompoundInQueryAtOutOfBounds */
    @org.junit.Test(timeout = 10000)
    public void testGetCompoundInQueryAtOutOfBounds_add135_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            global.getCompoundInQueryAt(0);
            global.getCompoundInQueryAt(0);
            org.junit.Assert.fail("testGetCompoundInQueryAtOutOfBounds_add135 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleSequencePairTest#testGetCompoundInQueryAtOutOfBounds2 */
    @org.junit.Test(timeout = 10000)
    public void testGetCompoundInQueryAtOutOfBounds2_add138_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            global.getCompoundInQueryAt(6);
            global.getCompoundInQueryAt(6);
            org.junit.Assert.fail("testGetCompoundInQueryAtOutOfBounds2_add138 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleSequencePairTest#testGetCompoundInQueryAtOutOfBounds3 */
    @org.junit.Test(timeout = 10000)
    public void testGetCompoundInQueryAtOutOfBounds3_add141_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            local.getCompoundInQueryAt(0);
            local.getCompoundInQueryAt(0);
            org.junit.Assert.fail("testGetCompoundInQueryAtOutOfBounds3_add141 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleSequencePairTest#testGetCompoundInQueryAtOutOfBounds4 */
    @org.junit.Test(timeout = 10000)
    public void testGetCompoundInQueryAtOutOfBounds4_add144_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            local.getCompoundInQueryAt(4);
            local.getCompoundInQueryAt(4);
            org.junit.Assert.fail("testGetCompoundInQueryAtOutOfBounds4_add144 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleSequencePairTest#testGetCompoundInTargetAt */
    @org.junit.Test(timeout = 10000)
    public void testGetCompoundInTargetAt_cf163_failAssert16() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = global.getCompoundInTargetAt(1).getShortName();
            // MethodAssertGenerator build local variable
            Object o_4_0 = global.getCompoundInTargetAt(2).getShortName();
            // MethodAssertGenerator build local variable
            Object o_7_0 = global.getCompoundInTargetAt(3).getShortName();
            // MethodAssertGenerator build local variable
            Object o_10_0 = global.getCompoundInTargetAt(4).getShortName();
            // MethodAssertGenerator build local variable
            Object o_13_0 = global.getCompoundInTargetAt(5).getShortName();
            // MethodAssertGenerator build local variable
            Object o_16_0 = local.getCompoundInTargetAt(1).getShortName();
            // MethodAssertGenerator build local variable
            Object o_19_0 = local.getCompoundInTargetAt(2).getShortName();
            // StatementAdderOnAssert create literal from method
            int int_vc_1 = 3;
            // StatementAdderOnAssert create null value
            org.biojava.nbio.core.sequence.template.Sequence vc_29 = (org.biojava.nbio.core.sequence.template.Sequence)null;
            // StatementAdderMethod cloned existing statement
            vc_29.getCompoundAt(int_vc_1);
            // MethodAssertGenerator build local variable
            Object o_28_0 = local.getCompoundInTargetAt(3).getShortName();
            org.junit.Assert.fail("testGetCompoundInTargetAt_cf163 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleSequencePairTest#testGetCompoundInTargetAtOutOfBounds */
    @org.junit.Test(timeout = 10000)
    public void testGetCompoundInTargetAtOutOfBounds_add290_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            global.getCompoundInTargetAt(0);
            global.getCompoundInTargetAt(0);
            org.junit.Assert.fail("testGetCompoundInTargetAtOutOfBounds_add290 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleSequencePairTest#testGetCompoundInTargetAtOutOfBounds2 */
    @org.junit.Test(timeout = 10000)
    public void testGetCompoundInTargetAtOutOfBounds2_add293_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            global.getCompoundInTargetAt(6);
            global.getCompoundInTargetAt(6);
            org.junit.Assert.fail("testGetCompoundInTargetAtOutOfBounds2_add293 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleSequencePairTest#testGetCompoundInTargetAtOutOfBounds3 */
    @org.junit.Test(timeout = 10000)
    public void testGetCompoundInTargetAtOutOfBounds3_add296_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            local.getCompoundInTargetAt(0);
            local.getCompoundInTargetAt(0);
            org.junit.Assert.fail("testGetCompoundInTargetAtOutOfBounds3_add296 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleSequencePairTest#testGetCompoundInTargetAtOutOfBounds4 */
    @org.junit.Test(timeout = 10000)
    public void testGetCompoundInTargetAtOutOfBounds4_add299_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            local.getCompoundInTargetAt(4);
            local.getCompoundInTargetAt(4);
            org.junit.Assert.fail("testGetCompoundInTargetAtOutOfBounds4_add299 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleSequencePairTest#testGetIndexInQueryAt */
    @org.junit.Test(timeout = 10000)
    public void testGetIndexInQueryAt_cf339_failAssert37() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = global.getIndexInQueryAt(1);
            // MethodAssertGenerator build local variable
            Object o_3_0 = global.getIndexInQueryAt(2);
            // MethodAssertGenerator build local variable
            Object o_5_0 = global.getIndexInQueryAt(3);
            // MethodAssertGenerator build local variable
            Object o_7_0 = global.getIndexInQueryAt(4);
            // MethodAssertGenerator build local variable
            Object o_9_0 = global.getIndexInQueryAt(5);
            // MethodAssertGenerator build local variable
            Object o_11_0 = local.getIndexInQueryAt(1);
            // MethodAssertGenerator build local variable
            Object o_13_0 = local.getIndexInQueryAt(2);
            // StatementAdderOnAssert create literal from method
            int int_vc_2 = 1;
            // StatementAdderOnAssert create null value
            org.biojava.nbio.core.sequence.template.Sequence vc_58 = (org.biojava.nbio.core.sequence.template.Sequence)null;
            // StatementAdderMethod cloned existing statement
            vc_58.getCompoundAt(int_vc_2);
            // MethodAssertGenerator build local variable
            Object o_21_0 = local.getIndexInQueryAt(3);
            org.junit.Assert.fail("testGetIndexInQueryAt_cf339 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleSequencePairTest#testGetIndexInQueryAtOutOfBounds */
    @org.junit.Test(timeout = 10000)
    public void testGetIndexInQueryAtOutOfBounds_add457_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            global.getIndexInQueryAt(0);
            global.getIndexInQueryAt(0);
            org.junit.Assert.fail("testGetIndexInQueryAtOutOfBounds_add457 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleSequencePairTest#testGetIndexInQueryAtOutOfBounds2 */
    @org.junit.Test(timeout = 10000)
    public void testGetIndexInQueryAtOutOfBounds2_add460_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            global.getIndexInQueryAt(6);
            global.getIndexInQueryAt(6);
            org.junit.Assert.fail("testGetIndexInQueryAtOutOfBounds2_add460 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleSequencePairTest#testGetIndexInQueryAtOutOfBounds3 */
    @org.junit.Test(timeout = 10000)
    public void testGetIndexInQueryAtOutOfBounds3_add463_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            local.getIndexInQueryAt(0);
            local.getIndexInQueryAt(0);
            org.junit.Assert.fail("testGetIndexInQueryAtOutOfBounds3_add463 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleSequencePairTest#testGetIndexInQueryAtOutOfBounds4 */
    @org.junit.Test(timeout = 10000)
    public void testGetIndexInQueryAtOutOfBounds4_add466_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            local.getIndexInQueryAt(4);
            local.getIndexInQueryAt(4);
            org.junit.Assert.fail("testGetIndexInQueryAtOutOfBounds4_add466 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleSequencePairTest#testGetIndexInQueryForTargetAt */
    @org.junit.Test(timeout = 10000)
    public void testGetIndexInQueryForTargetAt_cf497_failAssert28() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = global.getIndexInQueryForTargetAt(1);
            // MethodAssertGenerator build local variable
            Object o_3_0 = global.getIndexInQueryForTargetAt(2);
            // MethodAssertGenerator build local variable
            Object o_5_0 = global.getIndexInQueryForTargetAt(3);
            // MethodAssertGenerator build local variable
            Object o_7_0 = local.getIndexInQueryForTargetAt(1);
            // MethodAssertGenerator build local variable
            Object o_9_0 = local.getIndexInQueryForTargetAt(2);
            // StatementAdderOnAssert create literal from method
            int int_vc_3 = 4;
            // StatementAdderOnAssert create null value
            org.biojava.nbio.core.sequence.template.Sequence vc_87 = (org.biojava.nbio.core.sequence.template.Sequence)null;
            // StatementAdderMethod cloned existing statement
            vc_87.getCompoundAt(int_vc_3);
            // MethodAssertGenerator build local variable
            Object o_17_0 = local.getIndexInQueryForTargetAt(3);
            org.junit.Assert.fail("testGetIndexInQueryForTargetAt_cf497 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleSequencePairTest#testGetIndexInQueryForTargetAtOutOfBounds */
    @org.junit.Test(timeout = 10000)
    public void testGetIndexInQueryForTargetAtOutOfBounds_add625_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            global.getIndexInQueryForTargetAt(0);
            global.getIndexInQueryForTargetAt(0);
            org.junit.Assert.fail("testGetIndexInQueryForTargetAtOutOfBounds_add625 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleSequencePairTest#testGetIndexInQueryForTargetAtOutOfBounds2 */
    @org.junit.Test(timeout = 10000)
    public void testGetIndexInQueryForTargetAtOutOfBounds2_add628_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            global.getIndexInQueryForTargetAt(4);
            global.getIndexInQueryForTargetAt(4);
            org.junit.Assert.fail("testGetIndexInQueryForTargetAtOutOfBounds2_add628 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleSequencePairTest#testGetIndexInQueryForTargetAtOutOfBounds3 */
    @org.junit.Test(timeout = 10000)
    public void testGetIndexInQueryForTargetAtOutOfBounds3_add631_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            local.getIndexInQueryForTargetAt(0);
            local.getIndexInQueryForTargetAt(0);
            org.junit.Assert.fail("testGetIndexInQueryForTargetAtOutOfBounds3_add631 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleSequencePairTest#testGetIndexInQueryForTargetAtOutOfBounds4 */
    @org.junit.Test(timeout = 10000)
    public void testGetIndexInQueryForTargetAtOutOfBounds4_add634_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            local.getIndexInQueryForTargetAt(4);
            local.getIndexInQueryForTargetAt(4);
            org.junit.Assert.fail("testGetIndexInQueryForTargetAtOutOfBounds4_add634 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleSequencePairTest#testGetIndexInTargetAt */
    @org.junit.Test(timeout = 10000)
    public void testGetIndexInTargetAt_cf670_failAssert33() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = global.getIndexInTargetAt(1);
            // MethodAssertGenerator build local variable
            Object o_3_0 = global.getIndexInTargetAt(2);
            // MethodAssertGenerator build local variable
            Object o_5_0 = global.getIndexInTargetAt(3);
            // MethodAssertGenerator build local variable
            Object o_7_0 = global.getIndexInTargetAt(4);
            // MethodAssertGenerator build local variable
            Object o_9_0 = global.getIndexInTargetAt(5);
            // MethodAssertGenerator build local variable
            Object o_11_0 = local.getIndexInTargetAt(1);
            // MethodAssertGenerator build local variable
            Object o_13_0 = local.getIndexInTargetAt(2);
            // StatementAdderOnAssert create literal from method
            int int_vc_4 = 5;
            // StatementAdderOnAssert create null value
            org.biojava.nbio.core.sequence.template.Sequence vc_116 = (org.biojava.nbio.core.sequence.template.Sequence)null;
            // StatementAdderMethod cloned existing statement
            vc_116.getCompoundAt(int_vc_4);
            // MethodAssertGenerator build local variable
            Object o_21_0 = local.getIndexInTargetAt(3);
            org.junit.Assert.fail("testGetIndexInTargetAt_cf670 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

