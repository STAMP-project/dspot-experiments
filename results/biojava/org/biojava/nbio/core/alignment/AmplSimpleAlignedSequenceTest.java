

package org.biojava.nbio.core.alignment;


public class AmplSimpleAlignedSequenceTest {
    private org.biojava.nbio.core.sequence.ProteinSequence go;

    private org.biojava.nbio.core.sequence.ProteinSequence lo;

    private org.biojava.nbio.core.alignment.template.AlignedSequence<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound> global;

    private org.biojava.nbio.core.alignment.template.AlignedSequence<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound> local;

    private org.biojava.nbio.core.alignment.template.AlignedSequence<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound> local2;

    private org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet cs;

    @org.junit.Before
    public void setup() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        go = new org.biojava.nbio.core.sequence.ProteinSequence("ARND");
        lo = new org.biojava.nbio.core.sequence.ProteinSequence("CEQGHILKM");
        global = new org.biojava.nbio.core.alignment.SimpleAlignedSequence<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound>(go, java.util.Arrays.asList(new org.biojava.nbio.core.alignment.template.AlignedSequence.Step[]{ org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP }));
        local = new org.biojava.nbio.core.alignment.SimpleAlignedSequence<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound>(lo, java.util.Arrays.asList(new org.biojava.nbio.core.alignment.template.AlignedSequence.Step[]{ org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND }), 1, 3);
        local2 = new org.biojava.nbio.core.alignment.SimpleAlignedSequence<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound>(go, java.util.Arrays.asList(new org.biojava.nbio.core.alignment.template.AlignedSequence.Step[]{ org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND }), 1, 0);
        cs = org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet.getAminoAcidCompoundSet();
    }

    @org.junit.Test(expected = java.lang.IllegalArgumentException.class)
    public void testSimpleAlignedSequenceLocal() {
        new org.biojava.nbio.core.alignment.SimpleAlignedSequence<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound>(lo, java.util.Arrays.asList(new org.biojava.nbio.core.alignment.template.AlignedSequence.Step[]{ org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND }));
    }

    @org.junit.Test(expected = java.lang.IllegalArgumentException.class)
    public void testSimpleAlignedSequenceLong() {
        new org.biojava.nbio.core.alignment.SimpleAlignedSequence<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound>(go, java.util.Arrays.asList(new org.biojava.nbio.core.alignment.template.AlignedSequence.Step[]{ org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP }));
    }

    @org.junit.Test(expected = java.lang.IllegalArgumentException.class)
    public void testSimpleAlignedSequenceShort() {
        new org.biojava.nbio.core.alignment.SimpleAlignedSequence<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound>(go, java.util.Arrays.asList(new org.biojava.nbio.core.alignment.template.AlignedSequence.Step[]{ org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP }));
    }

    @org.junit.Test
    public void testGetAlignmentIndexAt() {
        org.junit.Assert.assertEquals(global.getAlignmentIndexAt(1), 2);
        org.junit.Assert.assertEquals(global.getAlignmentIndexAt(2), 3);
        org.junit.Assert.assertEquals(global.getAlignmentIndexAt(3), 5);
        org.junit.Assert.assertEquals(global.getAlignmentIndexAt(4), 6);
        org.junit.Assert.assertEquals(local.getAlignmentIndexAt(1), 1);
        org.junit.Assert.assertEquals(local.getAlignmentIndexAt(2), 1);
        org.junit.Assert.assertEquals(local.getAlignmentIndexAt(3), 2);
        org.junit.Assert.assertEquals(local.getAlignmentIndexAt(4), 5);
        org.junit.Assert.assertEquals(local.getAlignmentIndexAt(5), 7);
        org.junit.Assert.assertEquals(local.getAlignmentIndexAt(6), 8);
        org.junit.Assert.assertEquals(local.getAlignmentIndexAt(7), 8);
        org.junit.Assert.assertEquals(local.getAlignmentIndexAt(8), 8);
        org.junit.Assert.assertEquals(local.getAlignmentIndexAt(9), 8);
        org.junit.Assert.assertEquals(local2.getAlignmentIndexAt(1), 1);
        org.junit.Assert.assertEquals(local2.getAlignmentIndexAt(2), 1);
        org.junit.Assert.assertEquals(local2.getAlignmentIndexAt(3), 2);
        org.junit.Assert.assertEquals(local2.getAlignmentIndexAt(4), 3);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetAlignmentIndexAtOutOfBounds() {
        global.getAlignmentIndexAt(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetAlignmentIndexAtOutOfBounds2() {
        global.getAlignmentIndexAt(5);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetAlignmentIndexAtOutOfBounds3() {
        local.getAlignmentIndexAt(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetAlignmentIndexAtOutOfBounds4() {
        local.getAlignmentIndexAt(10);
    }

    @org.junit.Test
    public void testGetEnd() {
        org.junit.Assert.assertEquals(global.getEnd().getPosition(), java.lang.Integer.valueOf(6));
        org.junit.Assert.assertEquals(local.getEnd().getPosition(), java.lang.Integer.valueOf(8));
        org.junit.Assert.assertEquals(local2.getEnd().getPosition(), java.lang.Integer.valueOf(3));
    }

    @org.junit.Test
    public void testGetLocationInAlignment() {
        org.junit.Assert.assertEquals(global.getLocationInAlignment(), new org.biojava.nbio.core.sequence.location.SimpleLocation(2, 6, org.biojava.nbio.core.sequence.Strand.UNDEFINED, new org.biojava.nbio.core.sequence.location.SimpleLocation(2, 3, org.biojava.nbio.core.sequence.Strand.UNDEFINED), new org.biojava.nbio.core.sequence.location.SimpleLocation(5, 6, org.biojava.nbio.core.sequence.Strand.UNDEFINED)));
        org.junit.Assert.assertEquals(local.getLocationInAlignment(), new org.biojava.nbio.core.sequence.location.SimpleLocation(1, 8, org.biojava.nbio.core.sequence.Strand.UNDEFINED, new org.biojava.nbio.core.sequence.location.SimpleLocation(1, 2, org.biojava.nbio.core.sequence.Strand.UNDEFINED), new org.biojava.nbio.core.sequence.location.SimpleLocation(5, 5, org.biojava.nbio.core.sequence.Strand.UNDEFINED), new org.biojava.nbio.core.sequence.location.SimpleLocation(7, 8, org.biojava.nbio.core.sequence.Strand.UNDEFINED)));
        org.junit.Assert.assertEquals(local2.getLocationInAlignment(), new org.biojava.nbio.core.sequence.location.SimpleLocation(1, 3, org.biojava.nbio.core.sequence.Strand.UNDEFINED));
    }

    @org.junit.Test
    public void testGetNumGaps() {
        org.junit.Assert.assertEquals(global.getNumGaps(), 3);
        org.junit.Assert.assertEquals(local.getNumGaps(), 2);
        org.junit.Assert.assertEquals(local2.getNumGaps(), 0);
    }

    @org.junit.Test
    public void testGetNumGapPositions() {
        org.junit.Assert.assertEquals(global.getNumGapPositions(), 3);
        org.junit.Assert.assertEquals(local.getNumGapPositions(), 3);
        org.junit.Assert.assertEquals(local2.getNumGapPositions(), 0);
    }

    @org.junit.Test
    public void testGetCoverage() {
        org.junit.Assert.assertEquals(global.getCoverage(), 1.0, 0.01);
        org.junit.Assert.assertEquals(local.getCoverage(), 0.556, 0.01);
        org.junit.Assert.assertEquals(local2.getCoverage(), 0.75, 0.01);
    }

    @org.junit.Test
    public void testGetOriginalSequence() {
        org.junit.Assert.assertEquals(global.getOriginalSequence(), go);
        org.junit.Assert.assertEquals(local.getOriginalSequence(), lo);
        org.junit.Assert.assertEquals(local2.getOriginalSequence(), go);
    }

    @org.junit.Test
    public void testGetOverlapCount() {
        org.junit.Assert.assertEquals(global.getOverlapCount(), 1);
        org.junit.Assert.assertEquals(local.getOverlapCount(), 1);
        org.junit.Assert.assertEquals(local2.getOverlapCount(), 1);
    }

    @org.junit.Test
    public void testGetSequenceIndexAt() {
        org.junit.Assert.assertEquals(global.getSequenceIndexAt(1), 1);
        org.junit.Assert.assertEquals(global.getSequenceIndexAt(2), 1);
        org.junit.Assert.assertEquals(global.getSequenceIndexAt(3), 2);
        org.junit.Assert.assertEquals(global.getSequenceIndexAt(4), 2);
        org.junit.Assert.assertEquals(global.getSequenceIndexAt(5), 3);
        org.junit.Assert.assertEquals(global.getSequenceIndexAt(6), 4);
        org.junit.Assert.assertEquals(global.getSequenceIndexAt(7), 4);
        org.junit.Assert.assertEquals(local.getSequenceIndexAt(1), 2);
        org.junit.Assert.assertEquals(local.getSequenceIndexAt(2), 3);
        org.junit.Assert.assertEquals(local.getSequenceIndexAt(3), 3);
        org.junit.Assert.assertEquals(local.getSequenceIndexAt(4), 3);
        org.junit.Assert.assertEquals(local.getSequenceIndexAt(5), 4);
        org.junit.Assert.assertEquals(local.getSequenceIndexAt(6), 4);
        org.junit.Assert.assertEquals(local.getSequenceIndexAt(7), 5);
        org.junit.Assert.assertEquals(local.getSequenceIndexAt(8), 6);
        org.junit.Assert.assertEquals(local2.getSequenceIndexAt(1), 2);
        org.junit.Assert.assertEquals(local2.getSequenceIndexAt(2), 3);
        org.junit.Assert.assertEquals(local2.getSequenceIndexAt(3), 4);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetSequenceIndexAtOutOfBounds() {
        global.getSequenceIndexAt(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetSequenceIndexAtOutOfBounds2() {
        global.getSequenceIndexAt(8);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetSequenceIndexAtOutOfBounds3() {
        local.getSequenceIndexAt(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetSequenceIndexAtOutOfBounds4() {
        local.getSequenceIndexAt(9);
    }

    @org.junit.Test
    public void testGetStart() {
        org.junit.Assert.assertEquals(global.getStart().getPosition(), java.lang.Integer.valueOf(2));
        org.junit.Assert.assertEquals(local.getStart().getPosition(), java.lang.Integer.valueOf(1));
        org.junit.Assert.assertEquals(local2.getStart().getPosition(), java.lang.Integer.valueOf(1));
    }

    @org.junit.Test
    public void testIsCircular() {
        org.junit.Assert.assertFalse(global.isCircular());
        org.junit.Assert.assertFalse(local.isCircular());
        org.junit.Assert.assertFalse(local2.isCircular());
    }

    @org.junit.Test
    public void testCountCompounds() {
        org.junit.Assert.assertEquals(global.countCompounds(cs.getCompoundForString("A"), cs.getCompoundForString("N"), cs.getCompoundForString("A"), cs.getCompoundForString("E"), cs.getCompoundForString("D")), 3);
        org.junit.Assert.assertEquals(local.countCompounds(cs.getCompoundForString("A"), cs.getCompoundForString("N"), cs.getCompoundForString("A"), cs.getCompoundForString("E"), cs.getCompoundForString("D")), 1);
        org.junit.Assert.assertEquals(local2.countCompounds(cs.getCompoundForString("A"), cs.getCompoundForString("N"), cs.getCompoundForString("A"), cs.getCompoundForString("E"), cs.getCompoundForString("D")), 2);
    }

    @org.junit.Test
    public void testGetAccession() {
        org.junit.Assert.assertNull(global.getAccession());
        org.junit.Assert.assertNull(local.getAccession());
        org.junit.Assert.assertNull(local2.getAccession());
    }

    @org.junit.Test
    public void testGetAsList() {
        org.junit.Assert.assertArrayEquals(global.getAsList().toArray(new org.biojava.nbio.core.sequence.compound.AminoAcidCompound[7]), new org.biojava.nbio.core.sequence.compound.AminoAcidCompound[]{ cs.getCompoundForString("-") , cs.getCompoundForString("A") , cs.getCompoundForString("R") , cs.getCompoundForString("-") , cs.getCompoundForString("N") , cs.getCompoundForString("D") , cs.getCompoundForString("-") });
        org.junit.Assert.assertArrayEquals(local.getAsList().toArray(new org.biojava.nbio.core.sequence.compound.AminoAcidCompound[8]), new org.biojava.nbio.core.sequence.compound.AminoAcidCompound[]{ cs.getCompoundForString("E") , cs.getCompoundForString("Q") , cs.getCompoundForString("-") , cs.getCompoundForString("-") , cs.getCompoundForString("G") , cs.getCompoundForString("-") , cs.getCompoundForString("H") , cs.getCompoundForString("I") });
        org.junit.Assert.assertArrayEquals(local2.getAsList().toArray(new org.biojava.nbio.core.sequence.compound.AminoAcidCompound[3]), new org.biojava.nbio.core.sequence.compound.AminoAcidCompound[]{ cs.getCompoundForString("R") , cs.getCompoundForString("N") , cs.getCompoundForString("D") });
    }

    @org.junit.Test
    public void testGetCompoundAt() {
        org.junit.Assert.assertEquals(global.getCompoundAt(1), cs.getCompoundForString("-"));
        org.junit.Assert.assertEquals(global.getCompoundAt(2), cs.getCompoundForString("A"));
        org.junit.Assert.assertEquals(global.getCompoundAt(3), cs.getCompoundForString("R"));
        org.junit.Assert.assertEquals(global.getCompoundAt(4), cs.getCompoundForString("-"));
        org.junit.Assert.assertEquals(global.getCompoundAt(5), cs.getCompoundForString("N"));
        org.junit.Assert.assertEquals(global.getCompoundAt(6), cs.getCompoundForString("D"));
        org.junit.Assert.assertEquals(global.getCompoundAt(7), cs.getCompoundForString("-"));
        org.junit.Assert.assertEquals(global.getCompoundAt(1), cs.getCompoundForString("-"));
        org.junit.Assert.assertEquals(local.getCompoundAt(1), cs.getCompoundForString("E"));
        org.junit.Assert.assertEquals(local.getCompoundAt(2), cs.getCompoundForString("Q"));
        org.junit.Assert.assertEquals(local.getCompoundAt(3), cs.getCompoundForString("-"));
        org.junit.Assert.assertEquals(local.getCompoundAt(4), cs.getCompoundForString("-"));
        org.junit.Assert.assertEquals(local.getCompoundAt(5), cs.getCompoundForString("G"));
        org.junit.Assert.assertEquals(local.getCompoundAt(6), cs.getCompoundForString("-"));
        org.junit.Assert.assertEquals(local.getCompoundAt(7), cs.getCompoundForString("H"));
        org.junit.Assert.assertEquals(local.getCompoundAt(8), cs.getCompoundForString("I"));
        org.junit.Assert.assertEquals(local2.getCompoundAt(1), cs.getCompoundForString("R"));
        org.junit.Assert.assertEquals(local2.getCompoundAt(2), cs.getCompoundForString("N"));
        org.junit.Assert.assertEquals(local2.getCompoundAt(3), cs.getCompoundForString("D"));
    }

    @org.junit.Test
    public void testGetCompoundSet() {
        org.junit.Assert.assertEquals(global.getCompoundSet(), cs);
        org.junit.Assert.assertEquals(local.getCompoundSet(), cs);
        org.junit.Assert.assertEquals(local2.getCompoundSet(), cs);
    }

    @org.junit.Test
    public void testGetIndexOf() {
        org.junit.Assert.assertEquals(global.getIndexOf(cs.getCompoundForString("R")), 3);
        org.junit.Assert.assertEquals(global.getIndexOf(cs.getCompoundForString("-")), 1);
        org.junit.Assert.assertEquals(local.getIndexOf(cs.getCompoundForString("G")), 5);
        org.junit.Assert.assertEquals(local.getIndexOf(cs.getCompoundForString("-")), 3);
        org.junit.Assert.assertEquals(local2.getIndexOf(cs.getCompoundForString("N")), 2);
        org.junit.Assert.assertEquals(local2.getIndexOf(cs.getCompoundForString("-")), (-1));
    }

    @org.junit.Test
    public void testGetLastIndexOf() {
        org.junit.Assert.assertEquals(global.getLastIndexOf(cs.getCompoundForString("R")), 3);
        org.junit.Assert.assertEquals(global.getLastIndexOf(cs.getCompoundForString("-")), 7);
        org.junit.Assert.assertEquals(local.getLastIndexOf(cs.getCompoundForString("G")), 5);
        org.junit.Assert.assertEquals(local.getLastIndexOf(cs.getCompoundForString("-")), 6);
        org.junit.Assert.assertEquals(local2.getLastIndexOf(cs.getCompoundForString("N")), 2);
        org.junit.Assert.assertEquals(local2.getLastIndexOf(cs.getCompoundForString("-")), (-1));
    }

    @org.junit.Test
    public void testGetLength() {
        org.junit.Assert.assertEquals(global.getLength(), 7);
        org.junit.Assert.assertEquals(local.getLength(), 8);
        org.junit.Assert.assertEquals(local2.getLength(), 3);
    }

    @org.junit.Test
    public void testGetSequenceAsString() {
        org.junit.Assert.assertEquals(global.getSequenceAsString(), "-AR-ND-");
        org.junit.Assert.assertEquals(local.getSequenceAsString(), "EQ--G-HI");
        org.junit.Assert.assertEquals(local2.getSequenceAsString(), "RND");
    }

    @org.junit.Test
    public void testGetSequenceAsStringIntegerIntegerStrand() {
        org.junit.Assert.assertEquals(global.getSubSequence(2, 5).getSequenceAsString(), "AR-N");
        org.junit.Assert.assertEquals(local.getSubSequence(2, 6).getSequenceAsString(), "Q--G-");
        org.junit.Assert.assertEquals(local2.getSubSequence(2, 3).getSequenceAsString(), "ND");
    }

    @org.junit.Ignore
    @org.junit.Test
    public void testGetSubSequence() {
        org.junit.Assert.fail("Not yet implemented");
    }

    @org.junit.Test
    public void testIterator() {
        for (org.biojava.nbio.core.sequence.compound.AminoAcidCompound c : global) {
            org.junit.Assert.assertNotNull(cs.getStringForCompound(c));
        }
        for (org.biojava.nbio.core.sequence.compound.AminoAcidCompound c : local) {
            org.junit.Assert.assertNotNull(cs.getStringForCompound(c));
        }
        for (org.biojava.nbio.core.sequence.compound.AminoAcidCompound c : local2) {
            org.junit.Assert.assertNotNull(cs.getStringForCompound(c));
        }
    }

    @org.junit.Test
    public void testToString() {
        org.junit.Assert.assertEquals(global.toString(), "-AR-ND-");
        org.junit.Assert.assertEquals(local.toString(), "EQ--G-HI");
        org.junit.Assert.assertEquals(local2.toString(), "RND");
    }

    @org.junit.Test(timeout = 10000)
    public void testGetAlignmentIndexAt_cf109_failAssert87() {
        try {
            java.lang.Object o_1_0 = global.getAlignmentIndexAt(1);
            java.lang.Object o_3_0 = global.getAlignmentIndexAt(2);
            java.lang.Object o_5_0 = global.getAlignmentIndexAt(3);
            java.lang.Object o_7_0 = global.getAlignmentIndexAt(4);
            java.lang.Object o_9_0 = local.getAlignmentIndexAt(1);
            java.lang.Object o_11_0 = local.getAlignmentIndexAt(2);
            java.lang.Object o_13_0 = local.getAlignmentIndexAt(3);
            java.lang.Object o_15_0 = local.getAlignmentIndexAt(4);
            java.lang.Object o_17_0 = local.getAlignmentIndexAt(5);
            java.lang.Object o_19_0 = local.getAlignmentIndexAt(6);
            java.lang.Object o_21_0 = local.getAlignmentIndexAt(7);
            java.lang.Object o_23_0 = local.getAlignmentIndexAt(8);
            java.lang.Object o_25_0 = local.getAlignmentIndexAt(9);
            java.lang.Object o_27_0 = local2.getAlignmentIndexAt(1);
            java.lang.Object o_29_0 = local2.getAlignmentIndexAt(2);
            java.lang.Object o_31_0 = local2.getAlignmentIndexAt(3);
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_27 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
            vc_27.getLength();
            java.lang.Object o_37_0 = local2.getAlignmentIndexAt(4);
            org.junit.Assert.fail("testGetAlignmentIndexAt_cf109 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetAlignmentIndexAt_cf113_failAssert89_add188_add434() {
        try {
            java.lang.Object o_1_0 = global.getAlignmentIndexAt(1);
            org.junit.Assert.assertEquals(o_1_0, 2);
            org.junit.Assert.assertEquals(o_1_0, 2);
            java.lang.Object o_3_0 = global.getAlignmentIndexAt(2);
            org.junit.Assert.assertEquals(o_3_0, 3);
            org.junit.Assert.assertEquals(o_3_0, 3);
            java.lang.Object o_5_0 = global.getAlignmentIndexAt(3);
            org.junit.Assert.assertEquals(o_5_0, 5);
            org.junit.Assert.assertEquals(o_5_0, 5);
            java.lang.Object o_7_0 = global.getAlignmentIndexAt(4);
            org.junit.Assert.assertEquals(o_7_0, 6);
            org.junit.Assert.assertEquals(o_7_0, 6);
            java.lang.Object o_9_0 = local.getAlignmentIndexAt(1);
            org.junit.Assert.assertEquals(o_9_0, 1);
            org.junit.Assert.assertEquals(o_9_0, 1);
            java.lang.Object o_11_0 = local.getAlignmentIndexAt(2);
            org.junit.Assert.assertEquals(o_11_0, 1);
            org.junit.Assert.assertEquals(o_11_0, 1);
            java.lang.Object o_13_0 = local.getAlignmentIndexAt(3);
            org.junit.Assert.assertEquals(o_13_0, 2);
            org.junit.Assert.assertEquals(o_13_0, 2);
            java.lang.Object o_15_0 = local.getAlignmentIndexAt(4);
            org.junit.Assert.assertEquals(o_15_0, 5);
            org.junit.Assert.assertEquals(o_15_0, 5);
            java.lang.Object o_17_0 = local.getAlignmentIndexAt(5);
            org.junit.Assert.assertEquals(o_17_0, 7);
            org.junit.Assert.assertEquals(o_17_0, 7);
            java.lang.Object o_19_0 = local.getAlignmentIndexAt(6);
            org.junit.Assert.assertEquals(o_19_0, 8);
            org.junit.Assert.assertEquals(o_19_0, 8);
            java.lang.Object o_21_0 = local.getAlignmentIndexAt(7);
            org.junit.Assert.assertEquals(o_21_0, 8);
            org.junit.Assert.assertEquals(o_21_0, 8);
            java.lang.Object o_23_0 = local.getAlignmentIndexAt(8);
            org.junit.Assert.assertEquals(o_23_0, 8);
            org.junit.Assert.assertEquals(o_23_0, 8);
            java.lang.Object o_25_0 = local.getAlignmentIndexAt(9);
            org.junit.Assert.assertEquals(o_25_0, 8);
            org.junit.Assert.assertEquals(o_25_0, 8);
            java.lang.Object o_27_0 = local2.getAlignmentIndexAt(1);
            org.junit.Assert.assertEquals(o_27_0, 1);
            org.junit.Assert.assertEquals(o_27_0, 1);
            java.lang.Object o_29_0 = local2.getAlignmentIndexAt(2);
            org.junit.Assert.assertEquals(o_29_0, 1);
            org.junit.Assert.assertEquals(o_29_0, 1);
            java.lang.Object o_31_0 = local2.getAlignmentIndexAt(3);
            org.junit.Assert.assertEquals(o_31_0, 2);
            org.junit.Assert.assertEquals(o_31_0, 2);
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_31 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
            org.junit.Assert.assertNull(vc_31);
            org.junit.Assert.assertNull(vc_31);
            vc_31.getNumGaps();
            vc_31.getNumGaps();
            vc_31.getNumGaps();
            java.lang.Object o_37_0 = local2.getAlignmentIndexAt(4);
            org.junit.Assert.fail("testGetAlignmentIndexAt_cf113 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetAlignmentIndexAtOutOfBounds_add1_failAssert0() {
        try {
            global.getAlignmentIndexAt(0);
            global.getAlignmentIndexAt(0);
            org.junit.Assert.fail("testGetAlignmentIndexAtOutOfBounds_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetAlignmentIndexAtOutOfBounds2_add1_failAssert0() {
        try {
            global.getAlignmentIndexAt(5);
            global.getAlignmentIndexAt(5);
            org.junit.Assert.fail("testGetAlignmentIndexAtOutOfBounds2_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetAlignmentIndexAtOutOfBounds3_add1_failAssert0() {
        try {
            local.getAlignmentIndexAt(0);
            local.getAlignmentIndexAt(0);
            org.junit.Assert.fail("testGetAlignmentIndexAtOutOfBounds3_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetAlignmentIndexAtOutOfBounds4_add1_failAssert0() {
        try {
            local.getAlignmentIndexAt(10);
            local.getAlignmentIndexAt(10);
            org.junit.Assert.fail("testGetAlignmentIndexAtOutOfBounds4_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetEnd_cf10_failAssert5() {
        try {
            java.lang.Object o_15_1 = java.lang.Integer.valueOf(3);
            java.lang.Object o_5_1 = java.lang.Integer.valueOf(8);
            java.lang.Object o_1_1 = java.lang.Integer.valueOf(6);
            java.lang.Object o_1_0 = global.getEnd().getPosition();
            java.lang.Object o_5_0 = local.getEnd().getPosition();
            int vc_9 = -720162583;
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_7 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
            vc_7.isGap(vc_9);
            java.lang.Object o_15_0 = local2.getEnd().getPosition();
            org.junit.Assert.fail("testGetEnd_cf10 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetLocationInAlignment_cf105_failAssert83() {
        try {
            java.lang.Object o_16_1 = new org.biojava.nbio.core.sequence.location.SimpleLocation(1, 3, org.biojava.nbio.core.sequence.Strand.UNDEFINED);
            java.lang.Object o_6_1 = new org.biojava.nbio.core.sequence.location.SimpleLocation(1, 8, org.biojava.nbio.core.sequence.Strand.UNDEFINED, new org.biojava.nbio.core.sequence.location.SimpleLocation(1, 2, org.biojava.nbio.core.sequence.Strand.UNDEFINED), new org.biojava.nbio.core.sequence.location.SimpleLocation(5, 5, org.biojava.nbio.core.sequence.Strand.UNDEFINED), new org.biojava.nbio.core.sequence.location.SimpleLocation(7, 8, org.biojava.nbio.core.sequence.Strand.UNDEFINED));
            java.lang.Object o_1_1 = new org.biojava.nbio.core.sequence.location.SimpleLocation(2, 6, org.biojava.nbio.core.sequence.Strand.UNDEFINED, new org.biojava.nbio.core.sequence.location.SimpleLocation(2, 3, org.biojava.nbio.core.sequence.Strand.UNDEFINED), new org.biojava.nbio.core.sequence.location.SimpleLocation(5, 6, org.biojava.nbio.core.sequence.Strand.UNDEFINED));
            java.lang.Object o_1_0 = global.getLocationInAlignment();
            java.lang.Object o_6_0 = local.getLocationInAlignment();
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_27 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
            vc_27.getLength();
            java.lang.Object o_16_0 = local2.getLocationInAlignment();
            org.junit.Assert.fail("testGetLocationInAlignment_cf105 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetLocationInAlignment_cf107_failAssert84_literalMutation6432_failAssert12() {
        try {
            try {
                java.lang.Object o_16_1 = new org.biojava.nbio.core.sequence.location.SimpleLocation(1, 3, org.biojava.nbio.core.sequence.Strand.UNDEFINED);
                java.lang.Object o_6_1 = new org.biojava.nbio.core.sequence.location.SimpleLocation(1, 8, org.biojava.nbio.core.sequence.Strand.UNDEFINED, new org.biojava.nbio.core.sequence.location.SimpleLocation(1, 2, org.biojava.nbio.core.sequence.Strand.UNDEFINED), new org.biojava.nbio.core.sequence.location.SimpleLocation(5, 5, org.biojava.nbio.core.sequence.Strand.UNDEFINED), new org.biojava.nbio.core.sequence.location.SimpleLocation(7, 0, org.biojava.nbio.core.sequence.Strand.UNDEFINED));
                java.lang.Object o_1_1 = new org.biojava.nbio.core.sequence.location.SimpleLocation(2, 6, org.biojava.nbio.core.sequence.Strand.UNDEFINED, new org.biojava.nbio.core.sequence.location.SimpleLocation(2, 3, org.biojava.nbio.core.sequence.Strand.UNDEFINED), new org.biojava.nbio.core.sequence.location.SimpleLocation(5, 6, org.biojava.nbio.core.sequence.Strand.UNDEFINED));
                java.lang.Object o_1_0 = global.getLocationInAlignment();
                java.lang.Object o_6_0 = local.getLocationInAlignment();
                org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_29 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
                vc_29.getNumGapPositions();
                java.lang.Object o_16_0 = local2.getLocationInAlignment();
                org.junit.Assert.fail("testGetLocationInAlignment_cf107 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testGetLocationInAlignment_cf107_failAssert84_literalMutation6432 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetLocationInAlignment_cf109_failAssert85_literalMutation6479_failAssert11_literalMutation8438_failAssert4() {
        try {
            try {
                try {
                    java.lang.Object o_16_1 = new org.biojava.nbio.core.sequence.location.SimpleLocation(1, 3, org.biojava.nbio.core.sequence.Strand.UNDEFINED);
                    java.lang.Object o_6_1 = new org.biojava.nbio.core.sequence.location.SimpleLocation(0, 0, org.biojava.nbio.core.sequence.Strand.UNDEFINED, new org.biojava.nbio.core.sequence.location.SimpleLocation(1, 2, org.biojava.nbio.core.sequence.Strand.UNDEFINED), new org.biojava.nbio.core.sequence.location.SimpleLocation(5, 5, org.biojava.nbio.core.sequence.Strand.UNDEFINED), new org.biojava.nbio.core.sequence.location.SimpleLocation(7, 8, org.biojava.nbio.core.sequence.Strand.UNDEFINED));
                    java.lang.Object o_1_1 = new org.biojava.nbio.core.sequence.location.SimpleLocation(2, 6, org.biojava.nbio.core.sequence.Strand.UNDEFINED, new org.biojava.nbio.core.sequence.location.SimpleLocation(2, 3, org.biojava.nbio.core.sequence.Strand.UNDEFINED), new org.biojava.nbio.core.sequence.location.SimpleLocation(5, 6, org.biojava.nbio.core.sequence.Strand.UNDEFINED));
                    java.lang.Object o_1_0 = global.getLocationInAlignment();
                    java.lang.Object o_6_0 = local.getLocationInAlignment();
                    org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_31 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
                    vc_31.getNumGaps();
                    java.lang.Object o_16_0 = local2.getLocationInAlignment();
                    org.junit.Assert.fail("testGetLocationInAlignment_cf109 should have thrown NullPointerException");
                } catch (java.lang.NullPointerException eee) {
                }
                org.junit.Assert.fail("testGetLocationInAlignment_cf109_failAssert85_literalMutation6479 should have thrown IllegalStateException");
            } catch (java.lang.IllegalStateException eee) {
            }
            org.junit.Assert.fail("testGetLocationInAlignment_cf109_failAssert85_literalMutation6479_failAssert11_literalMutation8438 should have thrown Throwable");
        } catch (java.lang.Throwable eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetNumGaps_cf14_failAssert11() {
        try {
            java.lang.Object o_1_0 = global.getNumGaps();
            java.lang.Object o_3_0 = local.getNumGaps();
            int int_vc_0 = 0;
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_0 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
            vc_0.getCompoundAt(int_vc_0);
            java.lang.Object o_11_0 = local2.getNumGaps();
            org.junit.Assert.fail("testGetNumGaps_cf14 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetNumGapPositions_cf15_failAssert12() {
        try {
            java.lang.Object o_1_0 = global.getNumGapPositions();
            java.lang.Object o_3_0 = local.getNumGapPositions();
            int int_vc_0 = 0;
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_0 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
            vc_0.getCompoundAt(int_vc_0);
            java.lang.Object o_11_0 = local2.getNumGapPositions();
            org.junit.Assert.fail("testGetNumGapPositions_cf15 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCoverage_cf30_failAssert15() {
        try {
            java.lang.Object o_1_0 = global.getCoverage();
            java.lang.Object o_3_0 = local.getCoverage();
            int vc_2 = 1517406316;
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_0 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
            vc_0.getCompoundAt(vc_2);
            java.lang.Object o_11_0 = local2.getCoverage();
            org.junit.Assert.fail("testGetCoverage_cf30 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetOriginalSequence_cf15_failAssert5() {
        try {
            java.lang.Object o_1_0 = global.getOriginalSequence();
            java.lang.Object o_3_0 = local.getOriginalSequence();
            int vc_18 = 965497076;
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_16 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
            vc_16.getAlignmentIndexAt(vc_18);
            java.lang.Object o_11_0 = local2.getOriginalSequence();
            org.junit.Assert.fail("testGetOriginalSequence_cf15 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetOverlapCount_cf13_failAssert12() {
        try {
            java.lang.Object o_1_0 = global.getOverlapCount();
            java.lang.Object o_3_0 = local.getOverlapCount();
            int int_vc_0 = 1;
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_0 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
            vc_0.getCompoundAt(int_vc_0);
            java.lang.Object o_11_0 = local2.getOverlapCount();
            org.junit.Assert.fail("testGetOverlapCount_cf13 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetSequenceIndexAt_cf103_failAssert91() {
        try {
            java.lang.Object o_1_0 = global.getSequenceIndexAt(1);
            java.lang.Object o_3_0 = global.getSequenceIndexAt(2);
            java.lang.Object o_5_0 = global.getSequenceIndexAt(3);
            java.lang.Object o_7_0 = global.getSequenceIndexAt(4);
            java.lang.Object o_9_0 = global.getSequenceIndexAt(5);
            java.lang.Object o_11_0 = global.getSequenceIndexAt(6);
            java.lang.Object o_13_0 = global.getSequenceIndexAt(7);
            java.lang.Object o_15_0 = local.getSequenceIndexAt(1);
            java.lang.Object o_17_0 = local.getSequenceIndexAt(2);
            java.lang.Object o_19_0 = local.getSequenceIndexAt(3);
            java.lang.Object o_21_0 = local.getSequenceIndexAt(4);
            java.lang.Object o_23_0 = local.getSequenceIndexAt(5);
            java.lang.Object o_25_0 = local.getSequenceIndexAt(6);
            java.lang.Object o_27_0 = local.getSequenceIndexAt(7);
            java.lang.Object o_29_0 = local.getSequenceIndexAt(8);
            java.lang.Object o_31_0 = local2.getSequenceIndexAt(1);
            java.lang.Object o_33_0 = local2.getSequenceIndexAt(2);
            int int_vc_2 = 2;
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_16 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
            vc_16.getAlignmentIndexAt(int_vc_2);
            java.lang.Object o_41_0 = local2.getSequenceIndexAt(3);
            org.junit.Assert.fail("testGetSequenceIndexAt_cf103 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetSequenceIndexAtOutOfBounds_add1_failAssert0() {
        try {
            global.getSequenceIndexAt(0);
            global.getSequenceIndexAt(0);
            org.junit.Assert.fail("testGetSequenceIndexAtOutOfBounds_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetSequenceIndexAtOutOfBounds2_add1_failAssert0() {
        try {
            global.getSequenceIndexAt(8);
            global.getSequenceIndexAt(8);
            org.junit.Assert.fail("testGetSequenceIndexAtOutOfBounds2_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetSequenceIndexAtOutOfBounds3_add1_failAssert0() {
        try {
            local.getSequenceIndexAt(0);
            local.getSequenceIndexAt(0);
            org.junit.Assert.fail("testGetSequenceIndexAtOutOfBounds3_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetSequenceIndexAtOutOfBounds4_add1_failAssert0() {
        try {
            local.getSequenceIndexAt(9);
            local.getSequenceIndexAt(9);
            org.junit.Assert.fail("testGetSequenceIndexAtOutOfBounds4_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetStart_cf10_failAssert5() {
        try {
            java.lang.Object o_15_1 = java.lang.Integer.valueOf(1);
            java.lang.Object o_5_1 = java.lang.Integer.valueOf(1);
            java.lang.Object o_1_1 = java.lang.Integer.valueOf(2);
            java.lang.Object o_1_0 = global.getStart().getPosition();
            java.lang.Object o_5_0 = local.getStart().getPosition();
            int vc_9 = -720162583;
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_7 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
            vc_7.isGap(vc_9);
            java.lang.Object o_15_0 = local2.getStart().getPosition();
            org.junit.Assert.fail("testGetStart_cf10 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testIsCircular_cf15_failAssert5() {
        try {
            java.lang.Object o_1_0 = global.isCircular();
            java.lang.Object o_3_0 = local.isCircular();
            int vc_18 = 965497076;
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_16 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
            vc_16.getAlignmentIndexAt(vc_18);
            java.lang.Object o_11_0 = local2.isCircular();
            org.junit.Assert.fail("testIsCircular_cf15 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testCountCompounds_cf14_failAssert13() {
        try {
            java.lang.Object o_1_0 = global.countCompounds(cs.getCompoundForString("A"), cs.getCompoundForString("N"), cs.getCompoundForString("A"), cs.getCompoundForString("E"), cs.getCompoundForString("D"));
            java.lang.Object o_8_0 = local.countCompounds(cs.getCompoundForString("A"), cs.getCompoundForString("N"), cs.getCompoundForString("A"), cs.getCompoundForString("E"), cs.getCompoundForString("D"));
            int int_vc_0 = 2;
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_0 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
            vc_0.getCompoundAt(int_vc_0);
            java.lang.Object o_21_0 = local2.countCompounds(cs.getCompoundForString("A"), cs.getCompoundForString("N"), cs.getCompoundForString("A"), cs.getCompoundForString("E"), cs.getCompoundForString("D"));
            org.junit.Assert.fail("testCountCompounds_cf14 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetAccession_cf15_failAssert5() {
        try {
            org.junit.Assert.assertNull(global.getAccession());
            org.junit.Assert.assertNull(local.getAccession());
            int vc_18 = 965497076;
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_16 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
            vc_16.getAlignmentIndexAt(vc_18);
            org.junit.Assert.assertNull(local2.getAccession());
            org.junit.Assert.fail("testGetAccession_cf15 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetAsList_cf16_failAssert6() {
        try {
            org.junit.Assert.assertArrayEquals(global.getAsList().toArray(new org.biojava.nbio.core.sequence.compound.AminoAcidCompound[7]), new org.biojava.nbio.core.sequence.compound.AminoAcidCompound[]{ cs.getCompoundForString("-") , cs.getCompoundForString("A") , cs.getCompoundForString("R") , cs.getCompoundForString("-") , cs.getCompoundForString("N") , cs.getCompoundForString("D") , cs.getCompoundForString("-") });
            org.junit.Assert.assertArrayEquals(local.getAsList().toArray(new org.biojava.nbio.core.sequence.compound.AminoAcidCompound[8]), new org.biojava.nbio.core.sequence.compound.AminoAcidCompound[]{ cs.getCompoundForString("E") , cs.getCompoundForString("Q") , cs.getCompoundForString("-") , cs.getCompoundForString("-") , cs.getCompoundForString("G") , cs.getCompoundForString("-") , cs.getCompoundForString("H") , cs.getCompoundForString("I") });
            int int_vc_0 = 3;
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_0 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
            vc_0.getCompoundAt(int_vc_0);
            org.junit.Assert.assertArrayEquals(local2.getAsList().toArray(new org.biojava.nbio.core.sequence.compound.AminoAcidCompound[3]), new org.biojava.nbio.core.sequence.compound.AminoAcidCompound[]{ cs.getCompoundForString("R") , cs.getCompoundForString("N") , cs.getCompoundForString("D") });
            org.junit.Assert.fail("testGetAsList_cf16 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundAt_cf10_failAssert5() {
        try {
            java.lang.Object o_61_1 = cs.getCompoundForString("D");
            java.lang.Object o_52_1 = cs.getCompoundForString("N");
            java.lang.Object o_49_1 = cs.getCompoundForString("R");
            java.lang.Object o_46_1 = cs.getCompoundForString("I");
            java.lang.Object o_43_1 = cs.getCompoundForString("H");
            java.lang.Object o_40_1 = cs.getCompoundForString("-");
            java.lang.Object o_37_1 = cs.getCompoundForString("G");
            java.lang.Object o_34_1 = cs.getCompoundForString("-");
            java.lang.Object o_31_1 = cs.getCompoundForString("-");
            java.lang.Object o_28_1 = cs.getCompoundForString("Q");
            java.lang.Object o_25_1 = cs.getCompoundForString("E");
            java.lang.Object o_22_1 = cs.getCompoundForString("-");
            java.lang.Object o_19_1 = cs.getCompoundForString("-");
            java.lang.Object o_16_1 = cs.getCompoundForString("D");
            java.lang.Object o_13_1 = cs.getCompoundForString("N");
            java.lang.Object o_10_1 = cs.getCompoundForString("-");
            java.lang.Object o_7_1 = cs.getCompoundForString("R");
            java.lang.Object o_4_1 = cs.getCompoundForString("A");
            java.lang.Object o_1_1 = cs.getCompoundForString("-");
            java.lang.Object o_1_0 = global.getCompoundAt(1);
            java.lang.Object o_4_0 = global.getCompoundAt(2);
            java.lang.Object o_7_0 = global.getCompoundAt(3);
            java.lang.Object o_10_0 = global.getCompoundAt(4);
            java.lang.Object o_13_0 = global.getCompoundAt(5);
            java.lang.Object o_16_0 = global.getCompoundAt(6);
            java.lang.Object o_19_0 = global.getCompoundAt(7);
            java.lang.Object o_22_0 = global.getCompoundAt(1);
            java.lang.Object o_25_0 = local.getCompoundAt(1);
            java.lang.Object o_28_0 = local.getCompoundAt(2);
            java.lang.Object o_31_0 = local.getCompoundAt(3);
            java.lang.Object o_34_0 = local.getCompoundAt(4);
            java.lang.Object o_37_0 = local.getCompoundAt(5);
            java.lang.Object o_40_0 = local.getCompoundAt(6);
            java.lang.Object o_43_0 = local.getCompoundAt(7);
            java.lang.Object o_46_0 = local.getCompoundAt(8);
            java.lang.Object o_49_0 = local2.getCompoundAt(1);
            java.lang.Object o_52_0 = local2.getCompoundAt(2);
            int vc_9 = -720162583;
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_7 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
            vc_7.isGap(vc_9);
            java.lang.Object o_61_0 = local2.getCompoundAt(3);
            org.junit.Assert.fail("testGetCompoundAt_cf10 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundSet_cf15_failAssert5() {
        try {
            java.lang.Object o_1_0 = global.getCompoundSet();
            java.lang.Object o_3_0 = local.getCompoundSet();
            int vc_18 = 965497076;
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_16 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
            vc_16.getAlignmentIndexAt(vc_18);
            java.lang.Object o_11_0 = local2.getCompoundSet();
            org.junit.Assert.fail("testGetCompoundSet_cf15 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetIndexOf_cf28_failAssert27() {
        try {
            java.lang.Object o_22_1 = -1;
            java.lang.Object o_1_0 = global.getIndexOf(cs.getCompoundForString("R"));
            java.lang.Object o_4_0 = global.getIndexOf(cs.getCompoundForString("-"));
            java.lang.Object o_7_0 = local.getIndexOf(cs.getCompoundForString("G"));
            java.lang.Object o_10_0 = local.getIndexOf(cs.getCompoundForString("-"));
            java.lang.Object o_13_0 = local2.getIndexOf(cs.getCompoundForString("N"));
            int int_vc_0 = 5;
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_0 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
            vc_0.getCompoundAt(int_vc_0);
            java.lang.Object o_22_0 = local2.getIndexOf(cs.getCompoundForString("-"));
            org.junit.Assert.fail("testGetIndexOf_cf28 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetLastIndexOf_cf29_failAssert28() {
        try {
            java.lang.Object o_22_1 = -1;
            java.lang.Object o_1_0 = global.getLastIndexOf(cs.getCompoundForString("R"));
            java.lang.Object o_4_0 = global.getLastIndexOf(cs.getCompoundForString("-"));
            java.lang.Object o_7_0 = local.getLastIndexOf(cs.getCompoundForString("G"));
            java.lang.Object o_10_0 = local.getLastIndexOf(cs.getCompoundForString("-"));
            java.lang.Object o_13_0 = local2.getLastIndexOf(cs.getCompoundForString("N"));
            int int_vc_0 = 5;
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_0 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
            vc_0.getCompoundAt(int_vc_0);
            java.lang.Object o_22_0 = local2.getLastIndexOf(cs.getCompoundForString("-"));
            org.junit.Assert.fail("testGetLastIndexOf_cf29 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetLength_cf16_failAssert15() {
        try {
            java.lang.Object o_1_0 = global.getLength();
            java.lang.Object o_3_0 = local.getLength();
            int int_vc_0 = 3;
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_0 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
            vc_0.getCompoundAt(int_vc_0);
            java.lang.Object o_11_0 = local2.getLength();
            org.junit.Assert.fail("testGetLength_cf16 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetSequenceAsString_cf16_failAssert15() {
        try {
            java.lang.Object o_1_0 = global.getSequenceAsString();
            java.lang.Object o_3_0 = local.getSequenceAsString();
            int vc_2 = -1110079674;
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_0 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
            vc_0.getCompoundAt(vc_2);
            java.lang.Object o_11_0 = local2.getSequenceAsString();
            org.junit.Assert.fail("testGetSequenceAsString_cf16 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetSequenceAsStringIntegerIntegerStrand_cf13_failAssert12() {
        try {
            java.lang.Object o_1_0 = global.getSubSequence(2, 5).getSequenceAsString();
            java.lang.Object o_4_0 = local.getSubSequence(2, 6).getSequenceAsString();
            int int_vc_0 = 2;
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_0 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
            vc_0.getCompoundAt(int_vc_0);
            java.lang.Object o_13_0 = local2.getSubSequence(2, 3).getSequenceAsString();
            org.junit.Assert.fail("testGetSequenceAsStringIntegerIntegerStrand_cf13 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testIterator_cf15_failAssert5() {
        try {
            for (org.biojava.nbio.core.sequence.compound.AminoAcidCompound c : global) {
                org.junit.Assert.assertNotNull(cs.getStringForCompound(c));
            }
            for (org.biojava.nbio.core.sequence.compound.AminoAcidCompound c : local) {
                org.junit.Assert.assertNotNull(cs.getStringForCompound(c));
            }
            for (org.biojava.nbio.core.sequence.compound.AminoAcidCompound c : local2) {
                int vc_18 = 965497076;
                org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_16 = ((org.biojava.nbio.core.alignment.SimpleAlignedSequence) (null));
                vc_16.getAlignmentIndexAt(vc_18);
                org.junit.Assert.assertNotNull(cs.getStringForCompound(c));
            }
            org.junit.Assert.fail("testIterator_cf15 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleAlignedSequenceTest#testToString */
    @org.junit.Test(timeout = 10000)
    public void testToString_cf16_failAssert15() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = global.toString();
            // MethodAssertGenerator build local variable
            Object o_3_0 = local.toString();
            // StatementAdderOnAssert create random local variable
            int vc_2 = -1110079674;
            // StatementAdderOnAssert create null value
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_0 = (org.biojava.nbio.core.alignment.SimpleAlignedSequence)null;
            // StatementAdderMethod cloned existing statement
            vc_0.getCompoundAt(vc_2);
            // MethodAssertGenerator build local variable
            Object o_11_0 = local2.toString();
            org.junit.Assert.fail("testToString_cf16 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

