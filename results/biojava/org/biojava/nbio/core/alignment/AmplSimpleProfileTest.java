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


public class AmplSimpleProfileTest {
    private org.biojava.nbio.core.sequence.ProteinSequence query;

    private org.biojava.nbio.core.sequence.ProteinSequence target;

    private org.biojava.nbio.core.alignment.template.Profile<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound> global;

    private org.biojava.nbio.core.alignment.template.Profile<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound> local;

    private org.biojava.nbio.core.alignment.template.Profile<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound> single;

    @org.junit.Before
    public void setup() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        query = new org.biojava.nbio.core.sequence.ProteinSequence("ARND");
        target = new org.biojava.nbio.core.sequence.ProteinSequence("RDG");
        query.setAccession(new org.biojava.nbio.core.sequence.AccessionID("Query"));
        target.setAccession(new org.biojava.nbio.core.sequence.AccessionID("Target"));
        global = new org.biojava.nbio.core.alignment.SimpleProfile<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound>(query, target, java.util.Arrays.asList(new org.biojava.nbio.core.alignment.template.AlignedSequence.Step[]{ org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP }), 0, 0, java.util.Arrays.asList(new org.biojava.nbio.core.alignment.template.AlignedSequence.Step[]{ org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND }), 0, 0);
        local = new org.biojava.nbio.core.alignment.SimpleProfile<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound>(query, target, java.util.Arrays.asList(new org.biojava.nbio.core.alignment.template.AlignedSequence.Step[]{ org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND }), 1, 0, java.util.Arrays.asList(new org.biojava.nbio.core.alignment.template.AlignedSequence.Step[]{ org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND }), 0, 1);
        single = new org.biojava.nbio.core.alignment.SimpleProfile<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound>(query);
    }

    @org.junit.Test(expected = java.lang.IllegalArgumentException.class)
    public void testSimpleProfile() {
        new org.biojava.nbio.core.alignment.SimpleProfile<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound>(query, target, java.util.Arrays.asList(new org.biojava.nbio.core.alignment.template.AlignedSequence.Step[]{ org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP }), 0, 0, java.util.Arrays.asList(new org.biojava.nbio.core.alignment.template.AlignedSequence.Step[]{ org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.GAP , org.biojava.nbio.core.alignment.template.AlignedSequence.Step.COMPOUND }), 0, 0);
    }

    @org.junit.Test
    public void testGetAlignedSequenceInt() {
        org.junit.Assert.assertEquals(global.getAlignedSequence(1).toString(), "ARND-");
        org.junit.Assert.assertEquals(global.getAlignedSequence(2).toString(), "-R-DG");
        org.junit.Assert.assertEquals(local.getAlignedSequence(1).toString(), "RND");
        org.junit.Assert.assertEquals(local.getAlignedSequence(2).toString(), "R-D");
        org.junit.Assert.assertEquals(single.getAlignedSequence(1).toString(), "ARND");
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetAlignedSequenceIntOutOfBounds() {
        global.getAlignedSequence(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetAlignedSequenceIntOutOfBounds2() {
        global.getAlignedSequence(3);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetAlignedSequenceIntOutOfBounds3() {
        local.getAlignedSequence(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetAlignedSequenceIntOutOfBounds4() {
        local.getAlignedSequence(3);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetAlignedSequenceIntOutOfBounds5() {
        single.getAlignedSequence(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetAlignedSequenceIntOutOfBounds6() {
        single.getAlignedSequence(2);
    }

    @org.junit.Test
    public void testGetAlignedSequenceS() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        org.junit.Assert.assertEquals(global.getAlignedSequence(query).toString(), "ARND-");
        org.junit.Assert.assertEquals(global.getAlignedSequence(target).toString(), "-R-DG");
        org.junit.Assert.assertNull(global.getAlignedSequence(new org.biojava.nbio.core.sequence.ProteinSequence("AR")));
        org.junit.Assert.assertEquals(local.getAlignedSequence(query).toString(), "RND");
        org.junit.Assert.assertEquals(local.getAlignedSequence(target).toString(), "R-D");
        org.junit.Assert.assertNull(local.getAlignedSequence(new org.biojava.nbio.core.sequence.ProteinSequence("AR")));
        org.junit.Assert.assertEquals(single.getAlignedSequence(query).toString(), "ARND");
        org.junit.Assert.assertNull(single.getAlignedSequence(target));
    }

    @org.junit.Test
    public void testGetAlignedSequences() {
        java.util.List<org.biojava.nbio.core.alignment.template.AlignedSequence<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound>> list = global.getAlignedSequences();
        org.junit.Assert.assertEquals(list.size(), 2);
        org.junit.Assert.assertEquals(list.get(0).toString(), "ARND-");
        org.junit.Assert.assertEquals(list.get(1).toString(), "-R-DG");
        list = local.getAlignedSequences();
        org.junit.Assert.assertEquals(list.size(), 2);
        org.junit.Assert.assertEquals(list.get(0).toString(), "RND");
        org.junit.Assert.assertEquals(list.get(1).toString(), "R-D");
        list = single.getAlignedSequences();
        org.junit.Assert.assertEquals(list.size(), 1);
        org.junit.Assert.assertEquals(list.get(0).toString(), "ARND");
    }

    @org.junit.Test
    public void testGetAlignedSequencesIntArray() {
        java.util.List<org.biojava.nbio.core.alignment.template.AlignedSequence<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound>> list = global.getAlignedSequences(2, 1, 2);
        org.junit.Assert.assertEquals(list.size(), 3);
        org.junit.Assert.assertEquals(list.get(0).toString(), "-R-DG");
        org.junit.Assert.assertEquals(list.get(1).toString(), "ARND-");
        org.junit.Assert.assertEquals(list.get(2).toString(), "-R-DG");
        list = local.getAlignedSequences(2, 2, 1);
        org.junit.Assert.assertEquals(list.size(), 3);
        org.junit.Assert.assertEquals(list.get(0).toString(), "R-D");
        org.junit.Assert.assertEquals(list.get(1).toString(), "R-D");
        org.junit.Assert.assertEquals(list.get(2).toString(), "RND");
        list = single.getAlignedSequences(1, 1);
        org.junit.Assert.assertEquals(list.size(), 2);
        org.junit.Assert.assertEquals(list.get(0).toString(), "ARND");
        org.junit.Assert.assertEquals(list.get(1).toString(), "ARND");
    }

    @org.junit.Test
    public void testGetAlignedSequencesSArray() {
        java.util.List<org.biojava.nbio.core.alignment.template.AlignedSequence<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound>> list = global.getAlignedSequences(query, query, target);
        org.junit.Assert.assertEquals(list.size(), 3);
        org.junit.Assert.assertEquals(list.get(0).toString(), "ARND-");
        org.junit.Assert.assertEquals(list.get(1).toString(), "ARND-");
        org.junit.Assert.assertEquals(list.get(2).toString(), "-R-DG");
        list = local.getAlignedSequences(target, query, target);
        org.junit.Assert.assertEquals(list.size(), 3);
        org.junit.Assert.assertEquals(list.get(0).toString(), "R-D");
        org.junit.Assert.assertEquals(list.get(1).toString(), "RND");
        org.junit.Assert.assertEquals(list.get(2).toString(), "R-D");
        list = single.getAlignedSequences(query, query);
        org.junit.Assert.assertEquals(list.size(), 2);
        org.junit.Assert.assertEquals(list.get(0).toString(), "ARND");
        org.junit.Assert.assertEquals(list.get(1).toString(), "ARND");
    }

    @org.junit.Test
    public void testGetCompoundAtIntInt() {
        org.junit.Assert.assertEquals(global.getCompoundAt(1, 4).getShortName(), "D");
        org.junit.Assert.assertEquals(global.getCompoundAt(2, 3).getShortName(), "-");
        org.junit.Assert.assertEquals(local.getCompoundAt(1, 1).getShortName(), "R");
        org.junit.Assert.assertEquals(local.getCompoundAt(2, 2).getShortName(), "-");
        org.junit.Assert.assertEquals(single.getCompoundAt(1, 3).getShortName(), "N");
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundAtIntIntOutOfBounds() {
        global.getCompoundAt(0, 4);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundAtIntIntOutOfBounds2() {
        global.getCompoundAt(3, 4);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundAtIntIntOutOfBounds3() {
        global.getCompoundAt(1, 0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundAtIntIntOutOfBounds4() {
        global.getCompoundAt(2, 6);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundAtIntIntOutOfBounds5() {
        local.getCompoundAt(0, 2);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundAtIntIntOutOfBounds6() {
        local.getCompoundAt(3, 2);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundAtIntIntOutOfBounds7() {
        local.getCompoundAt(1, 0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundAtIntIntOutOfBounds8() {
        local.getCompoundAt(2, 4);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundAtIntIntOutOfBounds9() {
        single.getCompoundAt(1, 0);
    }

    @org.junit.Test
    public void testGetCompoundAtSInt() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        org.junit.Assert.assertEquals(global.getCompoundAt(query, 2).getShortName(), "R");
        org.junit.Assert.assertEquals(global.getCompoundAt(target, 5).getShortName(), "G");
        org.junit.Assert.assertNull(global.getCompoundAt(new org.biojava.nbio.core.sequence.ProteinSequence("AR"), 3));
        org.junit.Assert.assertEquals(local.getCompoundAt(query, 2).getShortName(), "N");
        org.junit.Assert.assertEquals(local.getCompoundAt(target, 3).getShortName(), "D");
        org.junit.Assert.assertNull(local.getCompoundAt(new org.biojava.nbio.core.sequence.ProteinSequence("AR"), 3));
        org.junit.Assert.assertEquals(single.getCompoundAt(query, 2).getShortName(), "R");
        org.junit.Assert.assertNull(single.getCompoundAt(target, 3));
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundAtSIntOutOfBounds() {
        global.getCompoundAt(query, 0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundAtSIntOutOfBounds2() {
        global.getCompoundAt(target, 6);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundAtSIntOutOfBounds3() {
        local.getCompoundAt(target, 0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundAtSIntOutOfBounds4() {
        local.getCompoundAt(query, 4);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundAtSIntOutOfBounds5() {
        single.getCompoundAt(query, 0);
    }

    @org.junit.Test
    public void testGetCompoundSet() {
        org.junit.Assert.assertEquals(global.getCompoundSet(), org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet.getAminoAcidCompoundSet());
        org.junit.Assert.assertEquals(local.getCompoundSet(), org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet.getAminoAcidCompoundSet());
        org.junit.Assert.assertEquals(single.getCompoundSet(), org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet.getAminoAcidCompoundSet());
    }

    @org.junit.Test
    public void testGetCompoundsAt() {
        java.util.List<org.biojava.nbio.core.sequence.compound.AminoAcidCompound> column = global.getCompoundsAt(5);
        org.junit.Assert.assertEquals(column.size(), 2);
        org.junit.Assert.assertEquals(column.get(0).getShortName(), "-");
        org.junit.Assert.assertEquals(column.get(1).getShortName(), "G");
        column = local.getCompoundsAt(2);
        org.junit.Assert.assertEquals(column.size(), 2);
        org.junit.Assert.assertEquals(column.get(0).getShortName(), "N");
        org.junit.Assert.assertEquals(column.get(1).getShortName(), "-");
        column = single.getCompoundsAt(2);
        org.junit.Assert.assertEquals(column.size(), 1);
        org.junit.Assert.assertEquals(column.get(0).getShortName(), "R");
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundsAtOutOfBounds() {
        global.getCompoundsAt(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundsAtOutOfBounds2() {
        global.getCompoundsAt(6);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundsAtOutOfBounds3() {
        local.getCompoundsAt(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundsAtOutOfBounds4() {
        local.getCompoundsAt(4);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundsAtOutOfBounds5() {
        single.getCompoundsAt(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetCompoundsAtOutOfBounds6() {
        single.getCompoundsAt(5);
    }

    @org.junit.Test
    public void testGetIndexOf() {
        org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet cs = org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet.getAminoAcidCompoundSet();
        org.junit.Assert.assertEquals(global.getIndexOf(cs.getCompoundForString("A")), 1);
        org.junit.Assert.assertEquals(global.getIndexOf(cs.getCompoundForString("R")), 2);
        org.junit.Assert.assertEquals(global.getIndexOf(cs.getCompoundForString("N")), 3);
        org.junit.Assert.assertEquals(global.getIndexOf(cs.getCompoundForString("D")), 4);
        org.junit.Assert.assertEquals(global.getIndexOf(cs.getCompoundForString("G")), 5);
        org.junit.Assert.assertEquals(global.getIndexOf(cs.getCompoundForString("-")), 1);
        org.junit.Assert.assertEquals(global.getIndexOf(cs.getCompoundForString("E")), (-1));
        org.junit.Assert.assertEquals(local.getIndexOf(cs.getCompoundForString("R")), 1);
        org.junit.Assert.assertEquals(local.getIndexOf(cs.getCompoundForString("N")), 2);
        org.junit.Assert.assertEquals(local.getIndexOf(cs.getCompoundForString("D")), 3);
        org.junit.Assert.assertEquals(local.getIndexOf(cs.getCompoundForString("-")), 2);
        org.junit.Assert.assertEquals(local.getIndexOf(cs.getCompoundForString("K")), (-1));
        org.junit.Assert.assertEquals(single.getIndexOf(cs.getCompoundForString("A")), 1);
        org.junit.Assert.assertEquals(single.getIndexOf(cs.getCompoundForString("R")), 2);
        org.junit.Assert.assertEquals(single.getIndexOf(cs.getCompoundForString("N")), 3);
        org.junit.Assert.assertEquals(single.getIndexOf(cs.getCompoundForString("D")), 4);
        org.junit.Assert.assertEquals(single.getIndexOf(cs.getCompoundForString("G")), (-1));
    }

    @org.junit.Test
    public void testGetIndicesAt() {
        org.junit.Assert.assertArrayEquals(global.getIndicesAt(1), new int[]{ 1 , 1 });
        org.junit.Assert.assertArrayEquals(global.getIndicesAt(2), new int[]{ 2 , 1 });
        org.junit.Assert.assertArrayEquals(global.getIndicesAt(3), new int[]{ 3 , 1 });
        org.junit.Assert.assertArrayEquals(global.getIndicesAt(4), new int[]{ 4 , 2 });
        org.junit.Assert.assertArrayEquals(global.getIndicesAt(5), new int[]{ 4 , 3 });
        org.junit.Assert.assertArrayEquals(local.getIndicesAt(1), new int[]{ 2 , 1 });
        org.junit.Assert.assertArrayEquals(local.getIndicesAt(2), new int[]{ 3 , 1 });
        org.junit.Assert.assertArrayEquals(local.getIndicesAt(3), new int[]{ 4 , 2 });
        org.junit.Assert.assertArrayEquals(single.getIndicesAt(1), new int[]{ 1 });
        org.junit.Assert.assertArrayEquals(single.getIndicesAt(2), new int[]{ 2 });
        org.junit.Assert.assertArrayEquals(single.getIndicesAt(3), new int[]{ 3 });
        org.junit.Assert.assertArrayEquals(single.getIndicesAt(4), new int[]{ 4 });
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetIndicesAtOutOfBounds() {
        global.getIndicesAt(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetIndicesAtOutOfBounds2() {
        global.getIndicesAt(6);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetIndicesAtOutOfBounds3() {
        local.getIndicesAt(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetIndicesAtOutOfBounds4() {
        local.getIndicesAt(4);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetIndicesAtOutOfBounds5() {
        single.getIndicesAt(0);
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void testGetIndicesAtOutOfBounds6() {
        single.getIndicesAt(5);
    }

    @org.junit.Test
    public void testGetLastIndexOf() {
        org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet cs = org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet.getAminoAcidCompoundSet();
        org.junit.Assert.assertEquals(global.getLastIndexOf(cs.getCompoundForString("A")), 1);
        org.junit.Assert.assertEquals(global.getLastIndexOf(cs.getCompoundForString("R")), 2);
        org.junit.Assert.assertEquals(global.getLastIndexOf(cs.getCompoundForString("N")), 3);
        org.junit.Assert.assertEquals(global.getLastIndexOf(cs.getCompoundForString("D")), 4);
        org.junit.Assert.assertEquals(global.getLastIndexOf(cs.getCompoundForString("G")), 5);
        org.junit.Assert.assertEquals(global.getLastIndexOf(cs.getCompoundForString("-")), 5);
        org.junit.Assert.assertEquals(global.getLastIndexOf(cs.getCompoundForString("E")), (-1));
        org.junit.Assert.assertEquals(local.getLastIndexOf(cs.getCompoundForString("R")), 1);
        org.junit.Assert.assertEquals(local.getLastIndexOf(cs.getCompoundForString("N")), 2);
        org.junit.Assert.assertEquals(local.getLastIndexOf(cs.getCompoundForString("D")), 3);
        org.junit.Assert.assertEquals(local.getLastIndexOf(cs.getCompoundForString("-")), 2);
        org.junit.Assert.assertEquals(local.getLastIndexOf(cs.getCompoundForString("K")), (-1));
        org.junit.Assert.assertEquals(single.getLastIndexOf(cs.getCompoundForString("A")), 1);
        org.junit.Assert.assertEquals(single.getLastIndexOf(cs.getCompoundForString("R")), 2);
        org.junit.Assert.assertEquals(single.getLastIndexOf(cs.getCompoundForString("N")), 3);
        org.junit.Assert.assertEquals(single.getLastIndexOf(cs.getCompoundForString("D")), 4);
        org.junit.Assert.assertEquals(single.getLastIndexOf(cs.getCompoundForString("G")), (-1));
    }

    @org.junit.Test
    public void testGetLength() {
        org.junit.Assert.assertEquals(global.getLength(), 5);
        org.junit.Assert.assertEquals(local.getLength(), 3);
        org.junit.Assert.assertEquals(single.getLength(), 4);
    }

    @org.junit.Test
    public void testGetOriginalSequences() {
        java.util.List<org.biojava.nbio.core.sequence.ProteinSequence> list = global.getOriginalSequences();
        org.junit.Assert.assertEquals(list.size(), 2);
        org.junit.Assert.assertEquals(list.get(0), query);
        org.junit.Assert.assertEquals(list.get(1), target);
        list = local.getOriginalSequences();
        org.junit.Assert.assertEquals(list.size(), 2);
        org.junit.Assert.assertEquals(list.get(0), query);
        org.junit.Assert.assertEquals(list.get(1), target);
        list = single.getOriginalSequences();
        org.junit.Assert.assertEquals(list.size(), 1);
        org.junit.Assert.assertEquals(list.get(0), query);
    }

    @org.junit.Test
    public void testGetSize() {
        org.junit.Assert.assertEquals(global.getSize(), 2);
        org.junit.Assert.assertEquals(local.getSize(), 2);
        org.junit.Assert.assertEquals(single.getSize(), 1);
    }

    // TODO SimpleProfile.getSubProfile(Location)
    @org.junit.Ignore
    @org.junit.Test
    public void testGetSubProfile() {
        org.junit.Assert.fail("Not yet implemented");
    }

    @org.junit.Test
    public void testIsCircular() {
        org.junit.Assert.assertFalse(global.isCircular());
        org.junit.Assert.assertFalse(local.isCircular());
        org.junit.Assert.assertFalse(single.isCircular());
    }

    @org.junit.Test
    public void testToStringInt() {
        org.junit.Assert.assertEquals(global.toString(3), java.lang.String.format(("          1 3%n" + ((((((("Query   1 ARN 3%n" + "           | %n") + "Target  1 -R- 1%n") + "%n") + "          4 5%n") + "Query   4 D- 4%n") + "          | %n") + "Target  2 DG 3%n"))));
        org.junit.Assert.assertEquals(local.toString(4), java.lang.String.format(("          1 3%n" + (("Query   2 RND 4%n" + "          | |%n") + "Target  1 R-D 2%n"))));
        org.junit.Assert.assertEquals(single.toString(4), java.lang.String.format(("         1  4%n" + "Query  1 ARND 4%n")));
    }

    @org.junit.Test
    public void testToStringFormatted() {
        org.junit.Assert.assertEquals(global.toString(org.biojava.nbio.core.alignment.template.Profile.StringFormat.ALN), java.lang.String.format(("CLUSTAL W MSA from BioJava%n%n" + (("Query     ARND- 4%n" + "           | | %n") + "Target    -R-DG 3%n"))));
        org.junit.Assert.assertEquals(local.toString(org.biojava.nbio.core.alignment.template.Profile.StringFormat.FASTA), java.lang.String.format((">Query%n" + (("RND%n" + ">Target%n") + "R-D%n"))));
        org.junit.Assert.assertEquals(single.toString(org.biojava.nbio.core.alignment.template.Profile.StringFormat.MSF), java.lang.String.format(("MSA from BioJava%n%n" + (((" MSF: 4  Type: P  Check: 735 ..%n%n" + " Name: Query  Len: 4  Check:  735  Weight: 1.0%n") + "%n//%n%n") + "Query ARND%n"))));
    }

    @org.junit.Test
    public void testToString() {
        org.junit.Assert.assertEquals(global.toString(), java.lang.String.format("ARND-%n-R-DG%n"));
        org.junit.Assert.assertEquals(local.toString(), java.lang.String.format("RND%nR-D%n"));
        org.junit.Assert.assertEquals(single.toString(), java.lang.String.format("ARND%n"));
    }

    @org.junit.Test
    public void testIterator() {
        for (org.biojava.nbio.core.alignment.template.AlignedSequence<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound> s : global) {
            org.junit.Assert.assertEquals(s.toString().length(), 5);
        }
        for (org.biojava.nbio.core.alignment.template.AlignedSequence<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound> s : local) {
            org.junit.Assert.assertEquals(s.toString().length(), 3);
        }
        for (org.biojava.nbio.core.alignment.template.AlignedSequence<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound> s : single) {
            org.junit.Assert.assertEquals(s.toString().length(), 4);
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleProfileTest#testGetAlignedSequenceInt */
    @org.junit.Test(timeout = 10000)
    public void testGetAlignedSequenceInt_cf3510_failAssert25() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = global.getAlignedSequence(1).toString();
            // MethodAssertGenerator build local variable
            Object o_4_0 = global.getAlignedSequence(2).toString();
            // MethodAssertGenerator build local variable
            Object o_7_0 = local.getAlignedSequence(1).toString();
            // MethodAssertGenerator build local variable
            Object o_10_0 = local.getAlignedSequence(2).toString();
            // StatementAdderOnAssert create literal from method
            int int_vc_2 = 2;
            // StatementAdderOnAssert create null value
            org.biojava.nbio.core.alignment.template.Profile vc_8 = (org.biojava.nbio.core.alignment.template.Profile)null;
            // StatementAdderMethod cloned existing statement
            vc_8.hasGap(int_vc_2);
            // MethodAssertGenerator build local variable
            Object o_19_0 = single.getAlignedSequence(1).toString();
            org.junit.Assert.fail("testGetAlignedSequenceInt_cf3510 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

