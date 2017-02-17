

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

    @org.junit.Test(timeout = 10000)
    public void testGetAlignedSequenceInt_cf100_failAssert40() {
        try {
            java.lang.Object o_1_0 = global.getAlignedSequence(1).toString();
            java.lang.Object o_4_0 = global.getAlignedSequence(2).toString();
            java.lang.Object o_7_0 = local.getAlignedSequence(1).toString();
            java.lang.Object o_10_0 = local.getAlignedSequence(2).toString();
            int[] vc_61 = ((int[]) (null));
            org.biojava.nbio.core.alignment.template.Profile vc_59 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
            vc_59.getAlignedSequences(vc_61);
            java.lang.Object o_19_0 = single.getAlignedSequence(1).toString();
            org.junit.Assert.fail("testGetAlignedSequenceInt_cf100 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetAlignedSequenceInt_cf104_failAssert42_add186_literalMutation541_failAssert65() {
        try {
            try {
                java.lang.Object o_1_0 = global.getAlignedSequence(1).toString();
                org.junit.Assert.assertEquals(o_1_0, "ARND-");
                java.lang.Object o_4_0 = global.getAlignedSequence(2).toString();
                org.junit.Assert.assertEquals(o_4_0, "-R-DG");
                java.lang.Object o_7_0 = local.getAlignedSequence(1).toString();
                org.junit.Assert.assertEquals(o_7_0, "RND");
                java.lang.Object o_10_0 = local.getAlignedSequence(2).toString();
                org.junit.Assert.assertEquals(o_10_0, "R-D");
                int int_vc_9 = 2;
                org.junit.Assert.assertEquals(int_vc_9, 1);
                org.biojava.nbio.core.alignment.template.Profile vc_63 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
                org.junit.Assert.assertNull(vc_63);
                vc_63.getCompoundsAt(int_vc_9);
                vc_63.getCompoundsAt(int_vc_9);
                java.lang.Object o_19_0 = single.getAlignedSequence(1).toString();
                org.junit.Assert.fail("testGetAlignedSequenceInt_cf104 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testGetAlignedSequenceInt_cf104_failAssert42_add186_literalMutation541 should have thrown Throwable");
        } catch (java.lang.Throwable eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetAlignedSequenceIntOutOfBounds_add1_failAssert0() {
        try {
            global.getAlignedSequence(0);
            global.getAlignedSequence(0);
            org.junit.Assert.fail("testGetAlignedSequenceIntOutOfBounds_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetAlignedSequenceIntOutOfBounds2_add1_failAssert0() {
        try {
            global.getAlignedSequence(3);
            global.getAlignedSequence(3);
            org.junit.Assert.fail("testGetAlignedSequenceIntOutOfBounds2_add1 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetAlignedSequenceIntOutOfBounds3_add1_failAssert0() {
        try {
            local.getAlignedSequence(0);
            local.getAlignedSequence(0);
            org.junit.Assert.fail("testGetAlignedSequenceIntOutOfBounds3_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetAlignedSequenceIntOutOfBounds4_add1_failAssert0() {
        try {
            local.getAlignedSequence(3);
            local.getAlignedSequence(3);
            org.junit.Assert.fail("testGetAlignedSequenceIntOutOfBounds4_add1 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetAlignedSequenceIntOutOfBounds5_add1_failAssert0() {
        try {
            single.getAlignedSequence(0);
            single.getAlignedSequence(0);
            org.junit.Assert.fail("testGetAlignedSequenceIntOutOfBounds5_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetAlignedSequenceIntOutOfBounds6_add1_failAssert0() {
        try {
            single.getAlignedSequence(2);
            single.getAlignedSequence(2);
            org.junit.Assert.fail("testGetAlignedSequenceIntOutOfBounds6_add1 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetAlignedSequenceS_cf36_failAssert26() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        try {
            java.lang.Object o_1_0 = global.getAlignedSequence(query).toString();
            java.lang.Object o_4_0 = global.getAlignedSequence(target).toString();
            org.junit.Assert.assertNull(global.getAlignedSequence(new org.biojava.nbio.core.sequence.ProteinSequence("AR")));
            java.lang.Object o_10_0 = local.getAlignedSequence(query).toString();
            java.lang.Object o_13_0 = local.getAlignedSequence(target).toString();
            org.junit.Assert.assertNull(local.getAlignedSequence(new org.biojava.nbio.core.sequence.ProteinSequence("AR")));
            java.lang.Object o_19_0 = single.getAlignedSequence(query).toString();
            int vc_10 = 1367102476;
            org.biojava.nbio.core.alignment.template.Profile vc_8 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
            vc_8.hasGap(vc_10);
            org.junit.Assert.assertNull(single.getAlignedSequence(target));
            org.junit.Assert.fail("testGetAlignedSequenceS_cf36 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetAlignedSequences_cf100_failAssert50() {
        try {
            java.util.List<org.biojava.nbio.core.alignment.template.AlignedSequence<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound>> list = global.getAlignedSequences();
            java.lang.Object o_3_0 = list.size();
            java.lang.Object o_5_0 = list.get(0).toString();
            java.lang.Object o_8_0 = list.get(1).toString();
            list = local.getAlignedSequences();
            java.lang.Object o_13_0 = list.size();
            java.lang.Object o_15_0 = list.get(0).toString();
            java.lang.Object o_18_0 = list.get(1).toString();
            list = single.getAlignedSequences();
            java.lang.Object o_23_0 = list.size();
            int int_vc_7 = 1;
            org.biojava.nbio.core.alignment.template.Profile vc_41 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
            vc_41.getIndicesAt(int_vc_7);
            java.lang.Object o_31_0 = list.get(0).toString();
            org.junit.Assert.fail("testGetAlignedSequences_cf100 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetAlignedSequencesIntArray_cf101_failAssert63() {
        try {
            java.util.List<org.biojava.nbio.core.alignment.template.AlignedSequence<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound>> list = global.getAlignedSequences(2, 1, 2);
            java.lang.Object o_3_0 = list.size();
            java.lang.Object o_5_0 = list.get(0).toString();
            java.lang.Object o_8_0 = list.get(1).toString();
            java.lang.Object o_11_0 = list.get(2).toString();
            list = local.getAlignedSequences(2, 2, 1);
            java.lang.Object o_16_0 = list.size();
            java.lang.Object o_18_0 = list.get(0).toString();
            java.lang.Object o_21_0 = list.get(1).toString();
            java.lang.Object o_24_0 = list.get(2).toString();
            list = single.getAlignedSequences(1, 1);
            java.lang.Object o_29_0 = list.size();
            java.lang.Object o_31_0 = list.get(0).toString();
            int int_vc_5 = 2;
            org.biojava.nbio.core.alignment.template.Profile vc_33 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
            vc_33.getCompoundCountsAt(int_vc_5);
            java.lang.Object o_40_0 = list.get(1).toString();
            org.junit.Assert.fail("testGetAlignedSequencesIntArray_cf101 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetAlignedSequencesSArray_cf101_failAssert63() {
        try {
            java.util.List<org.biojava.nbio.core.alignment.template.AlignedSequence<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound>> list = global.getAlignedSequences(query, query, target);
            java.lang.Object o_3_0 = list.size();
            java.lang.Object o_5_0 = list.get(0).toString();
            java.lang.Object o_8_0 = list.get(1).toString();
            java.lang.Object o_11_0 = list.get(2).toString();
            list = local.getAlignedSequences(target, query, target);
            java.lang.Object o_16_0 = list.size();
            java.lang.Object o_18_0 = list.get(0).toString();
            java.lang.Object o_21_0 = list.get(1).toString();
            java.lang.Object o_24_0 = list.get(2).toString();
            list = single.getAlignedSequences(query, query);
            java.lang.Object o_29_0 = list.size();
            java.lang.Object o_31_0 = list.get(0).toString();
            int int_vc_5 = 2;
            org.biojava.nbio.core.alignment.template.Profile vc_33 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
            vc_33.getCompoundCountsAt(int_vc_5);
            java.lang.Object o_40_0 = list.get(1).toString();
            org.junit.Assert.fail("testGetAlignedSequencesSArray_cf101 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundAtIntInt_cf100_failAssert31() {
        try {
            java.lang.Object o_1_0 = global.getCompoundAt(1, 4).getShortName();
            java.lang.Object o_4_0 = global.getCompoundAt(2, 3).getShortName();
            java.lang.Object o_7_0 = local.getCompoundAt(1, 1).getShortName();
            java.lang.Object o_10_0 = local.getCompoundAt(2, 2).getShortName();
            int vc_74 = -552152772;
            org.biojava.nbio.core.alignment.template.Profile vc_72 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
            vc_72.getAlignedSequence(vc_74);
            java.lang.Object o_19_0 = single.getCompoundAt(1, 3).getShortName();
            org.junit.Assert.fail("testGetCompoundAtIntInt_cf100 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundAtIntIntOutOfBounds_add1_failAssert0() {
        try {
            global.getCompoundAt(0, 4);
            global.getCompoundAt(0, 4);
            org.junit.Assert.fail("testGetCompoundAtIntIntOutOfBounds_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundAtIntIntOutOfBounds2_add1_failAssert0() {
        try {
            global.getCompoundAt(3, 4);
            global.getCompoundAt(3, 4);
            org.junit.Assert.fail("testGetCompoundAtIntIntOutOfBounds2_add1 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundAtIntIntOutOfBounds3_add1_failAssert0() {
        try {
            global.getCompoundAt(1, 0);
            global.getCompoundAt(1, 0);
            org.junit.Assert.fail("testGetCompoundAtIntIntOutOfBounds3_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundAtIntIntOutOfBounds4_add1_failAssert0() {
        try {
            global.getCompoundAt(2, 6);
            global.getCompoundAt(2, 6);
            org.junit.Assert.fail("testGetCompoundAtIntIntOutOfBounds4_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundAtIntIntOutOfBounds5_add1_failAssert0() {
        try {
            local.getCompoundAt(0, 2);
            local.getCompoundAt(0, 2);
            org.junit.Assert.fail("testGetCompoundAtIntIntOutOfBounds5_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundAtIntIntOutOfBounds6_add1_failAssert0() {
        try {
            local.getCompoundAt(3, 2);
            local.getCompoundAt(3, 2);
            org.junit.Assert.fail("testGetCompoundAtIntIntOutOfBounds6_add1 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundAtIntIntOutOfBounds7_add1_failAssert0() {
        try {
            local.getCompoundAt(1, 0);
            local.getCompoundAt(1, 0);
            org.junit.Assert.fail("testGetCompoundAtIntIntOutOfBounds7_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundAtIntIntOutOfBounds8_add1_failAssert0() {
        try {
            local.getCompoundAt(2, 4);
            local.getCompoundAt(2, 4);
            org.junit.Assert.fail("testGetCompoundAtIntIntOutOfBounds8_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundAtIntIntOutOfBounds9_add1_failAssert0() {
        try {
            single.getCompoundAt(1, 0);
            single.getCompoundAt(1, 0);
            org.junit.Assert.fail("testGetCompoundAtIntIntOutOfBounds9_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundAtSInt_cf103_failAssert30() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        try {
            java.lang.Object o_1_0 = global.getCompoundAt(query, 2).getShortName();
            java.lang.Object o_4_0 = global.getCompoundAt(target, 5).getShortName();
            org.junit.Assert.assertNull(global.getCompoundAt(new org.biojava.nbio.core.sequence.ProteinSequence("AR"), 3));
            java.lang.Object o_10_0 = local.getCompoundAt(query, 2).getShortName();
            java.lang.Object o_13_0 = local.getCompoundAt(target, 3).getShortName();
            org.junit.Assert.assertNull(local.getCompoundAt(new org.biojava.nbio.core.sequence.ProteinSequence("AR"), 3));
            java.lang.Object o_19_0 = single.getCompoundAt(query, 2).getShortName();
            int int_vc_10 = 3;
            org.biojava.nbio.core.alignment.template.Profile vc_72 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
            vc_72.getAlignedSequence(int_vc_10);
            org.junit.Assert.assertNull(single.getCompoundAt(target, 3));
            org.junit.Assert.fail("testGetCompoundAtSInt_cf103 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundAtSIntOutOfBounds_add1_failAssert0() {
        try {
            global.getCompoundAt(query, 0);
            global.getCompoundAt(query, 0);
            org.junit.Assert.fail("testGetCompoundAtSIntOutOfBounds_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundAtSIntOutOfBounds2_add1_failAssert0() {
        try {
            global.getCompoundAt(target, 6);
            global.getCompoundAt(target, 6);
            org.junit.Assert.fail("testGetCompoundAtSIntOutOfBounds2_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundAtSIntOutOfBounds3_add1_failAssert0() {
        try {
            local.getCompoundAt(target, 0);
            local.getCompoundAt(target, 0);
            org.junit.Assert.fail("testGetCompoundAtSIntOutOfBounds3_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundAtSIntOutOfBounds4_add1_failAssert0() {
        try {
            local.getCompoundAt(query, 4);
            local.getCompoundAt(query, 4);
            org.junit.Assert.fail("testGetCompoundAtSIntOutOfBounds4_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundAtSIntOutOfBounds5_add1_failAssert0() {
        try {
            single.getCompoundAt(query, 0);
            single.getCompoundAt(query, 0);
            org.junit.Assert.fail("testGetCompoundAtSIntOutOfBounds5_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundSet_cf11_failAssert2() {
        try {
            java.lang.Object o_13_1 = org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet.getAminoAcidCompoundSet();
            java.lang.Object o_4_1 = org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet.getAminoAcidCompoundSet();
            java.lang.Object o_1_1 = org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet.getAminoAcidCompoundSet();
            java.lang.Object o_1_0 = global.getCompoundSet();
            java.lang.Object o_4_0 = local.getCompoundSet();
            int vc_15 = 1206684408;
            org.biojava.nbio.core.alignment.template.Profile vc_13 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
            vc_13.getCompoundWeightsAt(vc_15);
            java.lang.Object o_13_0 = single.getCompoundSet();
            org.junit.Assert.fail("testGetCompoundSet_cf11 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundsAt_cf105_failAssert40() {
        try {
            java.util.List<org.biojava.nbio.core.sequence.compound.AminoAcidCompound> column = global.getCompoundsAt(5);
            java.lang.Object o_3_0 = column.size();
            java.lang.Object o_5_0 = column.get(0).getShortName();
            java.lang.Object o_8_0 = column.get(1).getShortName();
            column = local.getCompoundsAt(2);
            java.lang.Object o_13_0 = column.size();
            java.lang.Object o_15_0 = column.get(0).getShortName();
            java.lang.Object o_18_0 = column.get(1).getShortName();
            column = single.getCompoundsAt(2);
            java.lang.Object o_23_0 = column.size();
            int[] vc_61 = ((int[]) (null));
            org.biojava.nbio.core.alignment.template.Profile vc_59 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
            vc_59.getAlignedSequences(vc_61);
            java.lang.Object o_31_0 = column.get(0).getShortName();
            org.junit.Assert.fail("testGetCompoundsAt_cf105 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundsAtOutOfBounds_add1_failAssert0() {
        try {
            global.getCompoundsAt(0);
            global.getCompoundsAt(0);
            org.junit.Assert.fail("testGetCompoundsAtOutOfBounds_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundsAtOutOfBounds2_add1_failAssert0() {
        try {
            global.getCompoundsAt(6);
            global.getCompoundsAt(6);
            org.junit.Assert.fail("testGetCompoundsAtOutOfBounds2_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundsAtOutOfBounds3_add1_failAssert0() {
        try {
            local.getCompoundsAt(0);
            local.getCompoundsAt(0);
            org.junit.Assert.fail("testGetCompoundsAtOutOfBounds3_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundsAtOutOfBounds4_add1_failAssert0() {
        try {
            local.getCompoundsAt(4);
            local.getCompoundsAt(4);
            org.junit.Assert.fail("testGetCompoundsAtOutOfBounds4_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundsAtOutOfBounds5_add1_failAssert0() {
        try {
            single.getCompoundsAt(0);
            single.getCompoundsAt(0);
            org.junit.Assert.fail("testGetCompoundsAtOutOfBounds5_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetCompoundsAtOutOfBounds6_add1_failAssert0() {
        try {
            single.getCompoundsAt(5);
            single.getCompoundsAt(5);
            org.junit.Assert.fail("testGetCompoundsAtOutOfBounds6_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetIndexOf_cf113_failAssert79() {
        try {
            java.lang.Object o_57_1 = -1;
            java.lang.Object o_37_1 = -1;
            java.lang.Object o_21_1 = -1;
            org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet cs = org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet.getAminoAcidCompoundSet();
            java.lang.Object o_3_0 = global.getIndexOf(cs.getCompoundForString("A"));
            java.lang.Object o_6_0 = global.getIndexOf(cs.getCompoundForString("R"));
            java.lang.Object o_9_0 = global.getIndexOf(cs.getCompoundForString("N"));
            java.lang.Object o_12_0 = global.getIndexOf(cs.getCompoundForString("D"));
            java.lang.Object o_15_0 = global.getIndexOf(cs.getCompoundForString("G"));
            java.lang.Object o_18_0 = global.getIndexOf(cs.getCompoundForString("-"));
            java.lang.Object o_21_0 = global.getIndexOf(cs.getCompoundForString("E"));
            java.lang.Object o_25_0 = local.getIndexOf(cs.getCompoundForString("R"));
            java.lang.Object o_28_0 = local.getIndexOf(cs.getCompoundForString("N"));
            java.lang.Object o_31_0 = local.getIndexOf(cs.getCompoundForString("D"));
            java.lang.Object o_34_0 = local.getIndexOf(cs.getCompoundForString("-"));
            java.lang.Object o_37_0 = local.getIndexOf(cs.getCompoundForString("K"));
            java.lang.Object o_41_0 = single.getIndexOf(cs.getCompoundForString("A"));
            java.lang.Object o_44_0 = single.getIndexOf(cs.getCompoundForString("R"));
            java.lang.Object o_47_0 = single.getIndexOf(cs.getCompoundForString("N"));
            java.lang.Object o_50_0 = single.getIndexOf(cs.getCompoundForString("D"));
            org.biojava.nbio.core.alignment.template.Profile vc_29 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
            vc_29.getLength();
            java.lang.Object o_57_0 = single.getIndexOf(cs.getCompoundForString("G"));
            org.junit.Assert.fail("testGetIndexOf_cf113 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetIndicesAt_cf101_failAssert88() {
        try {
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
            int int_vc_2 = 3;
            org.biojava.nbio.core.alignment.template.Profile vc_8 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
            vc_8.hasGap(int_vc_2);
            org.junit.Assert.assertArrayEquals(single.getIndicesAt(4), new int[]{ 4 });
            org.junit.Assert.fail("testGetIndicesAt_cf101 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetIndicesAt_cf105_failAssert90_cf17303_literalMutation32176_failAssert2() {
        try {
            try {
                org.junit.Assert.assertArrayEquals(global.getIndicesAt(1), new int[]{ 1 , 1 });
                org.junit.Assert.assertArrayEquals(global.getIndicesAt(2), new int[]{ 2 , 1 });
                org.junit.Assert.assertArrayEquals(global.getIndicesAt(3), new int[]{ 3 , 1 });
                org.junit.Assert.assertArrayEquals(global.getIndicesAt(4), new int[]{ 4 , 2 });
                org.junit.Assert.assertArrayEquals(global.getIndicesAt(5), new int[]{ 4 , 3 });
                org.junit.Assert.assertArrayEquals(local.getIndicesAt(1), new int[]{ 2 , 1 });
                org.junit.Assert.assertArrayEquals(local.getIndicesAt(2), new int[]{ 3 , 0 });
                org.junit.Assert.assertArrayEquals(local.getIndicesAt(3), new int[]{ 4 , 2 });
                org.junit.Assert.assertArrayEquals(single.getIndicesAt(1), new int[]{ 1 });
                org.junit.Assert.assertArrayEquals(single.getIndicesAt(2), new int[]{ 2 });
                org.junit.Assert.assertArrayEquals(single.getIndicesAt(3), new int[]{ 3 });
                org.biojava.nbio.core.alignment.template.Profile vc_11 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
                org.junit.Assert.assertNull(vc_11);
                vc_11.isCircular();
                int vc_7381 = 104607570;
                org.biojava.nbio.core.alignment.template.Profile vc_7379 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
                vc_7379.hasGap(vc_7381);
                org.junit.Assert.assertArrayEquals(single.getIndicesAt(4), new int[]{ 4 });
                org.junit.Assert.fail("testGetIndicesAt_cf105 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testGetIndicesAt_cf105_failAssert90_cf17303_literalMutation32176 should have thrown Throwable");
        } catch (java.lang.Throwable eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetIndicesAtOutOfBounds2_add1_failAssert0() {
        try {
            global.getIndicesAt(6);
            global.getIndicesAt(6);
            org.junit.Assert.fail("testGetIndicesAtOutOfBounds2_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetIndicesAtOutOfBounds3_add1_failAssert0() {
        try {
            local.getIndicesAt(0);
            local.getIndicesAt(0);
            org.junit.Assert.fail("testGetIndicesAtOutOfBounds3_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetIndicesAtOutOfBounds4_add1_failAssert0() {
        try {
            local.getIndicesAt(4);
            local.getIndicesAt(4);
            org.junit.Assert.fail("testGetIndicesAtOutOfBounds4_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetIndicesAtOutOfBounds5_add1_failAssert0() {
        try {
            single.getIndicesAt(0);
            single.getIndicesAt(0);
            org.junit.Assert.fail("testGetIndicesAtOutOfBounds5_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetIndicesAtOutOfBounds6_add1_failAssert0() {
        try {
            single.getIndicesAt(5);
            single.getIndicesAt(5);
            org.junit.Assert.fail("testGetIndicesAtOutOfBounds6_add1 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetLastIndexOf_cf114_failAssert80() {
        try {
            java.lang.Object o_57_1 = -1;
            java.lang.Object o_37_1 = -1;
            java.lang.Object o_21_1 = -1;
            org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet cs = org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet.getAminoAcidCompoundSet();
            java.lang.Object o_3_0 = global.getLastIndexOf(cs.getCompoundForString("A"));
            java.lang.Object o_6_0 = global.getLastIndexOf(cs.getCompoundForString("R"));
            java.lang.Object o_9_0 = global.getLastIndexOf(cs.getCompoundForString("N"));
            java.lang.Object o_12_0 = global.getLastIndexOf(cs.getCompoundForString("D"));
            java.lang.Object o_15_0 = global.getLastIndexOf(cs.getCompoundForString("G"));
            java.lang.Object o_18_0 = global.getLastIndexOf(cs.getCompoundForString("-"));
            java.lang.Object o_21_0 = global.getLastIndexOf(cs.getCompoundForString("E"));
            java.lang.Object o_25_0 = local.getLastIndexOf(cs.getCompoundForString("R"));
            java.lang.Object o_28_0 = local.getLastIndexOf(cs.getCompoundForString("N"));
            java.lang.Object o_31_0 = local.getLastIndexOf(cs.getCompoundForString("D"));
            java.lang.Object o_34_0 = local.getLastIndexOf(cs.getCompoundForString("-"));
            java.lang.Object o_37_0 = local.getLastIndexOf(cs.getCompoundForString("K"));
            java.lang.Object o_41_0 = single.getLastIndexOf(cs.getCompoundForString("A"));
            java.lang.Object o_44_0 = single.getLastIndexOf(cs.getCompoundForString("R"));
            java.lang.Object o_47_0 = single.getLastIndexOf(cs.getCompoundForString("N"));
            java.lang.Object o_50_0 = single.getLastIndexOf(cs.getCompoundForString("D"));
            org.biojava.nbio.core.alignment.template.Profile vc_29 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
            vc_29.getLength();
            java.lang.Object o_57_0 = single.getLastIndexOf(cs.getCompoundForString("G"));
            org.junit.Assert.fail("testGetLastIndexOf_cf114 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetLastIndexOf_cf116_failAssert81_literalMutation1124_literalMutation1698_failAssert24() {
        try {
            try {
                java.lang.Object o_57_1 = 0;
                org.junit.Assert.assertEquals(o_57_1, 2);
                java.lang.Object o_37_1 = -1;
                org.junit.Assert.assertEquals(o_37_1, (-1));
                java.lang.Object o_21_1 = -1;
                org.junit.Assert.assertEquals(o_21_1, (-1));
                org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet cs = org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet.getAminoAcidCompoundSet();
                java.lang.Object o_3_0 = global.getLastIndexOf(cs.getCompoundForString("A"));
                org.junit.Assert.assertEquals(o_3_0, 1);
                java.lang.Object o_6_0 = global.getLastIndexOf(cs.getCompoundForString("R"));
                org.junit.Assert.assertEquals(o_6_0, 2);
                java.lang.Object o_9_0 = global.getLastIndexOf(cs.getCompoundForString("N"));
                org.junit.Assert.assertEquals(o_9_0, 3);
                java.lang.Object o_12_0 = global.getLastIndexOf(cs.getCompoundForString("D"));
                org.junit.Assert.assertEquals(o_12_0, 4);
                java.lang.Object o_15_0 = global.getLastIndexOf(cs.getCompoundForString("G"));
                org.junit.Assert.assertEquals(o_15_0, 5);
                java.lang.Object o_18_0 = global.getLastIndexOf(cs.getCompoundForString("-"));
                org.junit.Assert.assertEquals(o_18_0, 5);
                java.lang.Object o_21_0 = global.getLastIndexOf(cs.getCompoundForString("E"));
                org.junit.Assert.assertEquals(o_21_0, (-1));
                java.lang.Object o_25_0 = local.getLastIndexOf(cs.getCompoundForString("R"));
                org.junit.Assert.assertEquals(o_25_0, 1);
                java.lang.Object o_28_0 = local.getLastIndexOf(cs.getCompoundForString("N"));
                org.junit.Assert.assertEquals(o_28_0, 2);
                java.lang.Object o_31_0 = local.getLastIndexOf(cs.getCompoundForString("D"));
                org.junit.Assert.assertEquals(o_31_0, 3);
                java.lang.Object o_34_0 = local.getLastIndexOf(cs.getCompoundForString("-"));
                org.junit.Assert.assertEquals(o_34_0, 2);
                java.lang.Object o_37_0 = local.getLastIndexOf(cs.getCompoundForString("K"));
                org.junit.Assert.assertEquals(o_37_0, (-1));
                java.lang.Object o_41_0 = single.getLastIndexOf(cs.getCompoundForString("A"));
                org.junit.Assert.assertEquals(o_41_0, 1);
                java.lang.Object o_44_0 = single.getLastIndexOf(cs.getCompoundForString("R"));
                org.junit.Assert.assertEquals(o_44_0, 2);
                java.lang.Object o_47_0 = single.getLastIndexOf(cs.getCompoundForString("N"));
                org.junit.Assert.assertEquals(o_47_0, 3);
                java.lang.Object o_50_0 = single.getLastIndexOf(cs.getCompoundForString("D"));
                org.junit.Assert.assertEquals(o_50_0, 4);
                org.biojava.nbio.core.alignment.template.Profile vc_31 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
                org.junit.Assert.assertNull(vc_31);
                vc_31.getSize();
                java.lang.Object o_57_0 = single.getLastIndexOf(cs.getCompoundForString("G"));
                org.junit.Assert.fail("testGetLastIndexOf_cf116 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testGetLastIndexOf_cf116_failAssert81_literalMutation1124_literalMutation1698 should have thrown Throwable");
        } catch (java.lang.Throwable eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetLength_cf104_failAssert35() {
        try {
            java.lang.Object o_1_0 = global.getLength();
            java.lang.Object o_3_0 = local.getLength();
            int int_vc_10 = 3;
            org.biojava.nbio.core.alignment.template.Profile vc_72 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
            vc_72.getAlignedSequence(int_vc_10);
            java.lang.Object o_11_0 = single.getLength();
            org.junit.Assert.fail("testGetLength_cf104 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testGetSize_cf101_failAssert32() {
        try {
            java.lang.Object o_1_0 = global.getSize();
            java.lang.Object o_3_0 = local.getSize();
            int int_vc_10 = 2;
            org.biojava.nbio.core.alignment.template.Profile vc_72 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
            vc_72.getAlignedSequence(int_vc_10);
            java.lang.Object o_11_0 = single.getSize();
            org.junit.Assert.fail("testGetSize_cf101 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testIsCircular_cf11_failAssert2() {
        try {
            java.lang.Object o_1_0 = global.isCircular();
            java.lang.Object o_3_0 = local.isCircular();
            int vc_15 = 1206684408;
            org.biojava.nbio.core.alignment.template.Profile vc_13 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
            vc_13.getCompoundWeightsAt(vc_15);
            java.lang.Object o_11_0 = single.isCircular();
            org.junit.Assert.fail("testIsCircular_cf11 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testToStringInt_cf111_failAssert77() {
        try {
            java.lang.Object o_11_1 = java.lang.String.format(("         1  4%n" + "Query  1 ARND 4%n"));
            java.lang.Object o_4_1 = java.lang.String.format(("          1 3%n" + (("Query   2 RND 4%n" + "          | |%n") + "Target  1 R-D 2%n")));
            java.lang.Object o_1_1 = java.lang.String.format(("          1 3%n" + ((((((("Query   1 ARN 3%n" + "           | %n") + "Target  1 -R- 1%n") + "%n") + "          4 5%n") + "Query   4 D- 4%n") + "          | %n") + "Target  2 DG 3%n")));
            java.lang.Object o_1_0 = global.toString(3);
            java.lang.Object o_4_0 = local.toString(4);
            org.biojava.nbio.core.alignment.template.Profile vc_29 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
            vc_29.getLength();
            java.lang.Object o_11_0 = single.toString(4);
            org.junit.Assert.fail("testToStringInt_cf111 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testToStringInt_cf85_failAssert72_literalMutation5363_failAssert0() {
        try {
            try {
                java.lang.Object o_13_1 = java.lang.String.format(("         1  4%n" + "Query  1 ARND 4%n"));
                java.lang.Object o_4_1 = java.lang.String.format(("          1 3%n" + (("Query   2 RND 4%n" + "          | |%n") + "Target  1 R-D 2%n")));
                java.lang.Object o_1_1 = java.lang.String.format(("          1 3%n" + ((((((("Query   1 ARN 3%n" + "           | %n") + "b#O(`2u%$+y!vI>tQ") + "%n") + "          4 5%n") + "Query   4 D- 4%n") + "          | %n") + "Target  2 DG 3%n")));
                java.lang.Object o_1_0 = global.toString(3);
                java.lang.Object o_4_0 = local.toString(4);
                int int_vc_2 = 4;
                org.biojava.nbio.core.alignment.template.Profile vc_8 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
                vc_8.hasGap(int_vc_2);
                java.lang.Object o_13_0 = single.toString(4);
                org.junit.Assert.fail("testToStringInt_cf85 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testToStringInt_cf85_failAssert72_literalMutation5363 should have thrown UnknownFormatConversionException");
        } catch (java.util.UnknownFormatConversionException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testToStringFormatted_cf100_failAssert71() {
        try {
            java.lang.Object o_13_1 = java.lang.String.format(("MSA from BioJava%n%n" + (((" MSF: 4  Type: P  Check: 735 ..%n%n" + " Name: Query  Len: 4  Check:  735  Weight: 1.0%n") + "%n//%n%n") + "Query ARND%n")));
            java.lang.Object o_4_1 = java.lang.String.format((">Query%n" + (("RND%n" + ">Target%n") + "R-D%n")));
            java.lang.Object o_1_1 = java.lang.String.format(("CLUSTAL W MSA from BioJava%n%n" + (("Query     ARND- 4%n" + "           | | %n") + "Target    -R-DG 3%n")));
            java.lang.Object o_1_0 = global.toString(org.biojava.nbio.core.alignment.template.Profile);
            java.lang.Object o_4_0 = local.toString(org.biojava.nbio.core.alignment.template.Profile);
            int vc_43 = 756832910;
            org.biojava.nbio.core.alignment.template.Profile vc_41 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
            vc_41.getIndicesAt(vc_43);
            java.lang.Object o_13_0 = single.toString(org.biojava.nbio.core.alignment.template.Profile);
            org.junit.Assert.fail("testToStringFormatted_cf100 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testToStringFormatted_cf102_failAssert72_literalMutation4821_failAssert0() {
        try {
            try {
                java.lang.Object o_11_1 = java.lang.String.format(("MSA from BioJava%n%n" + (((" MSF: 4  Type: P  Check: 735 ..%n%n" + " Name: Query  Len: 4  Check:  735  Weight: 1.0%n") + "%n//%cn%n") + "Query ARND%n")));
                java.lang.Object o_4_1 = java.lang.String.format((">Query%n" + (("RND%n" + ">Target%n") + "R-D%n")));
                java.lang.Object o_1_1 = java.lang.String.format(("CLUSTAL W MSA from BioJava%n%n" + (("Query     ARND- 4%n" + "           | | %n") + "Target    -R-DG 3%n")));
                java.lang.Object o_1_0 = global.toString(org.biojava.nbio.core.alignment.template.Profile);
                java.lang.Object o_4_0 = local.toString(org.biojava.nbio.core.alignment.template.Profile);
                org.biojava.nbio.core.alignment.template.Profile vc_44 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
                vc_44.toString();
                java.lang.Object o_11_0 = single.toString(org.biojava.nbio.core.alignment.template.Profile);
                org.junit.Assert.fail("testToStringFormatted_cf102 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testToStringFormatted_cf102_failAssert72_literalMutation4821 should have thrown MissingFormatArgumentException");
        } catch (java.util.MissingFormatArgumentException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testToString_cf11_failAssert2() {
        try {
            java.lang.Object o_13_1 = java.lang.String.format("ARND%n");
            java.lang.Object o_4_1 = java.lang.String.format("RND%nR-D%n");
            java.lang.Object o_1_1 = java.lang.String.format("ARND-%n-R-DG%n");
            java.lang.Object o_1_0 = global.toString();
            java.lang.Object o_4_0 = local.toString();
            int vc_15 = 1206684408;
            org.biojava.nbio.core.alignment.template.Profile vc_13 = ((org.biojava.nbio.core.alignment.template.Profile) (null));
            vc_13.getCompoundWeightsAt(vc_15);
            java.lang.Object o_13_0 = single.toString();
            org.junit.Assert.fail("testToString_cf11 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.alignment.SimpleProfileTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf104_failAssert35() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            for (org.biojava.nbio.core.alignment.template.AlignedSequence<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound> s : global) {
                // MethodAssertGenerator build local variable
                Object o_4_0 = s.toString().length();
            }
            for (org.biojava.nbio.core.alignment.template.AlignedSequence<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound> s : local) {
                // MethodAssertGenerator build local variable
                Object o_10_0 = s.toString().length();
            }
            for (org.biojava.nbio.core.alignment.template.AlignedSequence<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound> s : single) {
                // StatementAdderOnAssert create literal from method
                int int_vc_10 = 3;
                // StatementAdderOnAssert create null value
                org.biojava.nbio.core.alignment.template.Profile vc_72 = (org.biojava.nbio.core.alignment.template.Profile)null;
                // StatementAdderMethod cloned existing statement
                vc_72.getAlignedSequence(int_vc_10);
                // MethodAssertGenerator build local variable
                Object o_22_0 = s.toString().length();
            }
            org.junit.Assert.fail("testIterator_cf104 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

