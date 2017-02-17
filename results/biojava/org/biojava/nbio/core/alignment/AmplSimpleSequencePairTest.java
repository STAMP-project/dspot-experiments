

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

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundInQueryAt_cf17_failAssert16() {
        try {
            java.lang.Object o_1_0 = global.getCompoundInQueryAt(1).getShortName();
            java.lang.Object o_4_0 = global.getCompoundInQueryAt(2).getShortName();
            java.lang.Object o_7_0 = global.getCompoundInQueryAt(3).getShortName();
            java.lang.Object o_10_0 = global.getCompoundInQueryAt(4).getShortName();
            java.lang.Object o_13_0 = global.getCompoundInQueryAt(5).getShortName();
            java.lang.Object o_16_0 = local.getCompoundInQueryAt(1).getShortName();
            java.lang.Object o_19_0 = local.getCompoundInQueryAt(2).getShortName();
            int int_vc_0 = 1;
            org.biojava.nbio.core.sequence.template.Sequence vc_0 = ((org.biojava.nbio.core.sequence.template.Sequence) (null));
            vc_0.getCompoundAt(int_vc_0);
            java.lang.Object o_28_0 = local.getCompoundInQueryAt(3).getShortName();
            org.junit.Assert.fail("testGetCompoundInQueryAt_cf17 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundInQueryAtOutOfBounds_add1_failAssert0() {
        try {
            global.getCompoundInQueryAt(0);
            global.getCompoundInQueryAt(0);
            org.junit.Assert.fail("testGetCompoundInQueryAtOutOfBounds_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundInQueryAtOutOfBounds2_add1_failAssert0() {
        try {
            global.getCompoundInQueryAt(6);
            global.getCompoundInQueryAt(6);
            org.junit.Assert.fail("testGetCompoundInQueryAtOutOfBounds2_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundInQueryAtOutOfBounds3_add1_failAssert0() {
        try {
            local.getCompoundInQueryAt(0);
            local.getCompoundInQueryAt(0);
            org.junit.Assert.fail("testGetCompoundInQueryAtOutOfBounds3_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundInQueryAtOutOfBounds4_add1_failAssert0() {
        try {
            local.getCompoundInQueryAt(4);
            local.getCompoundInQueryAt(4);
            org.junit.Assert.fail("testGetCompoundInQueryAtOutOfBounds4_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundInTargetAt_cf17_failAssert15() {
        try {
            java.lang.Object o_1_0 = global.getCompoundInTargetAt(1).getShortName();
            java.lang.Object o_4_0 = global.getCompoundInTargetAt(2).getShortName();
            java.lang.Object o_7_0 = global.getCompoundInTargetAt(3).getShortName();
            java.lang.Object o_10_0 = global.getCompoundInTargetAt(4).getShortName();
            java.lang.Object o_13_0 = global.getCompoundInTargetAt(5).getShortName();
            java.lang.Object o_16_0 = local.getCompoundInTargetAt(1).getShortName();
            java.lang.Object o_19_0 = local.getCompoundInTargetAt(2).getShortName();
            int int_vc_0 = 1;
            org.biojava.nbio.core.sequence.template.Sequence vc_0 = ((org.biojava.nbio.core.sequence.template.Sequence) (null));
            vc_0.getCompoundAt(int_vc_0);
            java.lang.Object o_28_0 = local.getCompoundInTargetAt(3).getShortName();
            org.junit.Assert.fail("testGetCompoundInTargetAt_cf17 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundInTargetAtOutOfBounds_add1_failAssert0() {
        try {
            global.getCompoundInTargetAt(0);
            global.getCompoundInTargetAt(0);
            org.junit.Assert.fail("testGetCompoundInTargetAtOutOfBounds_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundInTargetAtOutOfBounds2_add1_failAssert0() {
        try {
            global.getCompoundInTargetAt(6);
            global.getCompoundInTargetAt(6);
            org.junit.Assert.fail("testGetCompoundInTargetAtOutOfBounds2_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundInTargetAtOutOfBounds3_add1_failAssert0() {
        try {
            local.getCompoundInTargetAt(0);
            local.getCompoundInTargetAt(0);
            org.junit.Assert.fail("testGetCompoundInTargetAtOutOfBounds3_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundInTargetAtOutOfBounds4_add1_failAssert0() {
        try {
            local.getCompoundInTargetAt(4);
            local.getCompoundInTargetAt(4);
            org.junit.Assert.fail("testGetCompoundInTargetAtOutOfBounds4_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetIndexInQueryAt_cf38_failAssert37() {
        try {
            java.lang.Object o_1_0 = global.getIndexInQueryAt(1);
            java.lang.Object o_3_0 = global.getIndexInQueryAt(2);
            java.lang.Object o_5_0 = global.getIndexInQueryAt(3);
            java.lang.Object o_7_0 = global.getIndexInQueryAt(4);
            java.lang.Object o_9_0 = global.getIndexInQueryAt(5);
            java.lang.Object o_11_0 = local.getIndexInQueryAt(1);
            java.lang.Object o_13_0 = local.getIndexInQueryAt(2);
            int int_vc_0 = 2;
            org.biojava.nbio.core.sequence.template.Sequence vc_0 = ((org.biojava.nbio.core.sequence.template.Sequence) (null));
            vc_0.getCompoundAt(int_vc_0);
            java.lang.Object o_21_0 = local.getIndexInQueryAt(3);
            org.junit.Assert.fail("testGetIndexInQueryAt_cf38 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetIndexInQueryAtOutOfBounds_add1_failAssert0() {
        try {
            global.getIndexInQueryAt(0);
            global.getIndexInQueryAt(0);
            org.junit.Assert.fail("testGetIndexInQueryAtOutOfBounds_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetIndexInQueryForTargetAt_cf29_failAssert28() {
        try {
            java.lang.Object o_1_0 = global.getIndexInQueryForTargetAt(1);
            java.lang.Object o_3_0 = global.getIndexInQueryForTargetAt(2);
            java.lang.Object o_5_0 = global.getIndexInQueryForTargetAt(3);
            java.lang.Object o_7_0 = local.getIndexInQueryForTargetAt(1);
            java.lang.Object o_9_0 = local.getIndexInQueryForTargetAt(2);
            int int_vc_0 = 2;
            org.biojava.nbio.core.sequence.template.Sequence vc_0 = ((org.biojava.nbio.core.sequence.template.Sequence) (null));
            vc_0.getCompoundAt(int_vc_0);
            java.lang.Object o_17_0 = local.getIndexInQueryForTargetAt(3);
            org.junit.Assert.fail("testGetIndexInQueryForTargetAt_cf29 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetIndexInQueryForTargetAtOutOfBounds_add1_failAssert0() {
        try {
            global.getIndexInQueryForTargetAt(0);
            global.getIndexInQueryForTargetAt(0);
            org.junit.Assert.fail("testGetIndexInQueryForTargetAtOutOfBounds_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetIndexInTargetAt_cf34_failAssert33() {
        try {
            java.lang.Object o_1_0 = global.getIndexInTargetAt(1);
            java.lang.Object o_3_0 = global.getIndexInTargetAt(2);
            java.lang.Object o_5_0 = global.getIndexInTargetAt(3);
            java.lang.Object o_7_0 = global.getIndexInTargetAt(4);
            java.lang.Object o_9_0 = global.getIndexInTargetAt(5);
            java.lang.Object o_11_0 = local.getIndexInTargetAt(1);
            java.lang.Object o_13_0 = local.getIndexInTargetAt(2);
            int int_vc_0 = 2;
            org.biojava.nbio.core.sequence.template.Sequence vc_0 = ((org.biojava.nbio.core.sequence.template.Sequence) (null));
            vc_0.getCompoundAt(int_vc_0);
            java.lang.Object o_21_0 = local.getIndexInTargetAt(3);
            org.junit.Assert.fail("testGetIndexInTargetAt_cf34 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetIndexInTargetAtOutOfBounds_add1_failAssert0() {
        try {
            global.getIndexInTargetAt(0);
            global.getIndexInTargetAt(0);
            org.junit.Assert.fail("testGetIndexInTargetAtOutOfBounds_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetIndexInTargetForQueryAt_cf33_failAssert32() {
        try {
            java.lang.Object o_1_0 = global.getIndexInTargetForQueryAt(1);
            java.lang.Object o_3_0 = global.getIndexInTargetForQueryAt(2);
            java.lang.Object o_5_0 = global.getIndexInTargetForQueryAt(3);
            java.lang.Object o_7_0 = global.getIndexInTargetForQueryAt(4);
            java.lang.Object o_9_0 = local.getIndexInTargetForQueryAt(1);
            java.lang.Object o_11_0 = local.getIndexInTargetForQueryAt(2);
            java.lang.Object o_13_0 = local.getIndexInTargetForQueryAt(3);
            int int_vc_0 = 3;
            org.biojava.nbio.core.sequence.template.Sequence vc_0 = ((org.biojava.nbio.core.sequence.template.Sequence) (null));
            vc_0.getCompoundAt(int_vc_0);
            java.lang.Object o_21_0 = local.getIndexInTargetForQueryAt(4);
            org.junit.Assert.fail("testGetIndexInTargetForQueryAt_cf33 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetIndexInTargetForQueryAtOutOfBounds_add1_failAssert0() {
        try {
            global.getIndexInTargetForQueryAt(0);
            global.getIndexInTargetForQueryAt(0);
            org.junit.Assert.fail("testGetIndexInTargetForQueryAtOutOfBounds_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetNumIdenticals_cf10_failAssert9() {
        try {
            java.lang.Object o_1_0 = global.getNumIdenticals();
            int vc_2 = 362573722;
            org.biojava.nbio.core.sequence.template.Sequence vc_0 = ((org.biojava.nbio.core.sequence.template.Sequence) (null));
            vc_0.getCompoundAt(vc_2);
            java.lang.Object o_9_0 = local.getNumIdenticals();
            org.junit.Assert.fail("testGetNumIdenticals_cf10 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetPercentageOfIdentity_cf19_failAssert8() {
        try {
            java.lang.Object o_1_0 = global.getPercentageOfIdentity();
            int vc_2 = 1517406316;
            org.biojava.nbio.core.sequence.template.Sequence vc_0 = ((org.biojava.nbio.core.sequence.template.Sequence) (null));
            vc_0.getCompoundAt(vc_2);
            java.lang.Object o_9_0 = local.getPercentageOfIdentity();
            org.junit.Assert.fail("testGetPercentageOfIdentity_cf19 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetNumSimilars_cf10_failAssert9() {
        try {
            java.lang.Object o_1_0 = global.getNumSimilars();
            int vc_2 = 362573722;
            org.biojava.nbio.core.sequence.template.Sequence vc_0 = ((org.biojava.nbio.core.sequence.template.Sequence) (null));
            vc_0.getCompoundAt(vc_2);
            java.lang.Object o_9_0 = local.getNumSimilars();
            org.junit.Assert.fail("testGetNumSimilars_cf10 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetQuery_cf15_failAssert1() {
        try {
            java.lang.Object o_1_0 = global.getQuery().getOriginalSequence();
            org.biojava.nbio.core.sequence.template.Sequence vc_15 = ((org.biojava.nbio.core.sequence.template.Sequence) (null));
            vc_15.getLength();
            java.lang.Object o_8_0 = local.getQuery().getOriginalSequence();
            org.junit.Assert.fail("testGetQuery_cf15 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleSequencePairTest#testGetTarget */
    @org.junit.Test(timeout = 10000)
    public void testGetTarget_cf15_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = global.getTarget().getOriginalSequence();
            // StatementAdderOnAssert create null value
            org.biojava.nbio.core.sequence.template.Sequence vc_15 = (org.biojava.nbio.core.sequence.template.Sequence)null;
            // StatementAdderMethod cloned existing statement
            vc_15.getLength();
            // MethodAssertGenerator build local variable
            Object o_8_0 = local.getTarget().getOriginalSequence();
            org.junit.Assert.fail("testGetTarget_cf15 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

