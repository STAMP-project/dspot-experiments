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
 */


package org.biojava.nbio.core.sequence;


public class AmplEditSequenceTest {
    @org.junit.Test
    public void substitute() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        org.biojava.nbio.core.sequence.DNASequence seq = new org.biojava.nbio.core.sequence.DNASequence("ACGT");
        assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Substitute<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("T", 2).edit(seq), "ATGT");
        assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Substitute<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 2).edit(seq), "ATTT");
        assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Substitute<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("T", 1).edit(seq), "TCGT");
        assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Substitute<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TTC", 2).edit(seq), "ATTC");
    }

    @org.junit.Test(expected = java.lang.IndexOutOfBoundsException.class)
    public void badSubstitute() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        new org.biojava.nbio.core.sequence.edits.Edit.Substitute<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("AAAA", 4).edit(new org.biojava.nbio.core.sequence.DNASequence("ACGT"));
    }

    @org.junit.Test
    public void delete() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        org.biojava.nbio.core.sequence.DNASequence seq = new org.biojava.nbio.core.sequence.DNASequence("ACGT");
        assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Delete<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(1).edit(seq), "CGT");
        assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Delete<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(4).edit(seq), "ACG");
        assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Delete<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(2, 3).edit(seq), "AT");
        // disabling this test, because we can't create a CompoundSet if we have no sequences...
        // assertSeq(new Edit.Delete<NucleotideCompound>(1,4).edit(seq), "");
    }

    @org.junit.Test
    public void insert() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        org.biojava.nbio.core.sequence.DNASequence seq = new org.biojava.nbio.core.sequence.DNASequence("ACGT");
        assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 1).edit(seq), "TTACGT");
        assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 2, 3).edit(seq), "ACTTGT");
        assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 3, 4).edit(seq), "ACGTTT");
        assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("A", 4).edit(seq), "ACGTA");
        // Original BioJava example
        assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("atgga", 3, 4).edit(new org.biojava.nbio.core.sequence.DNASequence("gataca")), "gatatggaaca");
    }

    private void assertSeq(org.biojava.nbio.core.sequence.template.Sequence<? extends org.biojava.nbio.core.sequence.template.Compound> seq, java.lang.String expected) {
        org.junit.Assert.assertEquals(("Asserting sequence " + expected), expected, seq.getSequenceAsString());
    }
}

