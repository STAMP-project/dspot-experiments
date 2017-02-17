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

    // TODO SimpleAlignedSequence.getSubSequence(Integer, Integer)
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

    /* amplification of org.biojava.nbio.core.alignment.SimpleAlignedSequenceTest#testCountCompounds */
    @org.junit.Test(timeout = 10000)
    public void testCountCompounds_cf14_failAssert13() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = global.countCompounds(cs.getCompoundForString("A"), cs.getCompoundForString("N"), cs.getCompoundForString("A"), cs.getCompoundForString("E"), cs.getCompoundForString("D"));
            // MethodAssertGenerator build local variable
            Object o_8_0 = local.countCompounds(cs.getCompoundForString("A"), cs.getCompoundForString("N"), cs.getCompoundForString("A"), cs.getCompoundForString("E"), cs.getCompoundForString("D"));
            // StatementAdderOnAssert create literal from method
            int int_vc_0 = 2;
            // StatementAdderOnAssert create null value
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_0 = (org.biojava.nbio.core.alignment.SimpleAlignedSequence)null;
            // StatementAdderMethod cloned existing statement
            vc_0.getCompoundAt(int_vc_0);
            // MethodAssertGenerator build local variable
            Object o_21_0 = local2.countCompounds(cs.getCompoundForString("A"), cs.getCompoundForString("N"), cs.getCompoundForString("A"), cs.getCompoundForString("E"), cs.getCompoundForString("D"));
            org.junit.Assert.fail("testCountCompounds_cf14 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleAlignedSequenceTest#testGetAccession */
    @org.junit.Test(timeout = 10000)
    public void testGetAccession_cf434_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.junit.Assert.assertNull(global.getAccession());
            org.junit.Assert.assertNull(local.getAccession());
            // StatementAdderOnAssert create random local variable
            int vc_66 = -1477321960;
            // StatementAdderOnAssert create null value
            org.biojava.nbio.core.alignment.SimpleAlignedSequence vc_64 = (org.biojava.nbio.core.alignment.SimpleAlignedSequence)null;
            // StatementAdderMethod cloned existing statement
            vc_64.getCompoundAt(vc_66);
            org.junit.Assert.assertNull(local2.getAccession());
            org.junit.Assert.fail("testGetAccession_cf434 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

