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
 * Created on November 21, 2010
 * Author: Mark Chapman
 */


package org.biojava.nbio.core.sequence;


public class AmplMultipleSequenceAlignmentTest {
    private org.biojava.nbio.core.sequence.MultipleSequenceAlignment<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound> msaProteins;

    private org.biojava.nbio.core.sequence.MultipleSequenceAlignment<org.biojava.nbio.core.sequence.DNASequence, org.biojava.nbio.core.sequence.compound.NucleotideCompound> msaDNA;

    @org.junit.Before
    public void setup() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        msaProteins = new org.biojava.nbio.core.sequence.MultipleSequenceAlignment<org.biojava.nbio.core.sequence.ProteinSequence, org.biojava.nbio.core.sequence.compound.AminoAcidCompound>();
        for (int i = 0; i < 8; i++) {
            msaProteins.addAlignedSequence(new org.biojava.nbio.core.sequence.ProteinSequence("ARNDCEQGHILKMFPSTWYVBZJX"));
        }
        msaDNA = new org.biojava.nbio.core.sequence.MultipleSequenceAlignment<org.biojava.nbio.core.sequence.DNASequence, org.biojava.nbio.core.sequence.compound.NucleotideCompound>();
        for (int i = 0; i < 7; i++) {
            msaDNA.addAlignedSequence(new org.biojava.nbio.core.sequence.DNASequence("ATCGATCGATCGATCG"));
        }
    }

    @org.junit.Test
    public void testGetCompoundsAt() {
        org.biojava.nbio.core.sequence.compound.AminoAcidCompound aminoAcid = org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet.getAminoAcidCompoundSet().getCompoundForString("N");
        java.util.List<org.biojava.nbio.core.sequence.compound.AminoAcidCompound> colProteins = new java.util.ArrayList<org.biojava.nbio.core.sequence.compound.AminoAcidCompound>();
        for (int i = 0; i < 8; i++) {
            // AssertGenerator replace invocation
            boolean o_testGetCompoundsAt__10 = colProteins.add(aminoAcid);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testGetCompoundsAt__10);
        }
        org.junit.Assert.assertEquals(msaProteins.getCompoundsAt(3), colProteins);
        org.biojava.nbio.core.sequence.compound.NucleotideCompound nucleotide = org.biojava.nbio.core.sequence.compound.DNACompoundSet.getDNACompoundSet().getCompoundForString("C");
        java.util.List<org.biojava.nbio.core.sequence.compound.NucleotideCompound> colDNA = new java.util.ArrayList<org.biojava.nbio.core.sequence.compound.NucleotideCompound>();
        for (int i = 0; i < 7; i++) {
            // AssertGenerator replace invocation
            boolean o_testGetCompoundsAt__22 = colDNA.add(nucleotide);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testGetCompoundsAt__22);
        }
        org.junit.Assert.assertEquals(msaDNA.getCompoundsAt(3), colDNA);
    }

    /* amplification of org.biojava.nbio.core.sequence.MultipleSequenceAlignmentTest#testGetCompoundsAt */
    @org.junit.Test(timeout = 10000)
    public void testGetCompoundsAt_cf50_failAssert33() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.biojava.nbio.core.sequence.compound.AminoAcidCompound aminoAcid = org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet.getAminoAcidCompoundSet().getCompoundForString("N");
            java.util.List<org.biojava.nbio.core.sequence.compound.AminoAcidCompound> colProteins = new java.util.ArrayList<org.biojava.nbio.core.sequence.compound.AminoAcidCompound>();
            for (int i = 0; i < 8; i++) {
                colProteins.add(aminoAcid);
            }
            // MethodAssertGenerator build local variable
            Object o_11_0 = msaProteins.getCompoundsAt(3);
            org.biojava.nbio.core.sequence.compound.NucleotideCompound nucleotide = org.biojava.nbio.core.sequence.compound.DNACompoundSet.getDNACompoundSet().getCompoundForString("C");
            java.util.List<org.biojava.nbio.core.sequence.compound.NucleotideCompound> colDNA = new java.util.ArrayList<org.biojava.nbio.core.sequence.compound.NucleotideCompound>();
            for (int i = 0; i < 7; i++) {
                colDNA.add(nucleotide);
            }
            // StatementAdderOnAssert create random local variable
            org.biojava.nbio.core.sequence.MultipleSequenceAlignment vc_26 = new org.biojava.nbio.core.sequence.MultipleSequenceAlignment();
            // StatementAdderMethod cloned existing statement
            vc_26.getCompoundSet();
            // MethodAssertGenerator build local variable
            Object o_27_0 = msaDNA.getCompoundsAt(3);
            org.junit.Assert.fail("testGetCompoundsAt_cf50 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.sequence.MultipleSequenceAlignmentTest#testGetCompoundsAt */
    @org.junit.Test(timeout = 10000)
    public void testGetCompoundsAt_cf34_failAssert24() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.biojava.nbio.core.sequence.compound.AminoAcidCompound aminoAcid = org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet.getAminoAcidCompoundSet().getCompoundForString("N");
            java.util.List<org.biojava.nbio.core.sequence.compound.AminoAcidCompound> colProteins = new java.util.ArrayList<org.biojava.nbio.core.sequence.compound.AminoAcidCompound>();
            for (int i = 0; i < 8; i++) {
                colProteins.add(aminoAcid);
            }
            // MethodAssertGenerator build local variable
            Object o_11_0 = msaProteins.getCompoundsAt(3);
            org.biojava.nbio.core.sequence.compound.NucleotideCompound nucleotide = org.biojava.nbio.core.sequence.compound.DNACompoundSet.getDNACompoundSet().getCompoundForString("C");
            java.util.List<org.biojava.nbio.core.sequence.compound.NucleotideCompound> colDNA = new java.util.ArrayList<org.biojava.nbio.core.sequence.compound.NucleotideCompound>();
            for (int i = 0; i < 7; i++) {
                colDNA.add(nucleotide);
            }
            // StatementAdderOnAssert create random local variable
            org.biojava.nbio.core.sequence.MultipleSequenceAlignment vc_12 = new org.biojava.nbio.core.sequence.MultipleSequenceAlignment();
            // StatementAdderMethod cloned existing statement
            vc_12.toString();
            // MethodAssertGenerator build local variable
            Object o_27_0 = msaDNA.getCompoundsAt(3);
            org.junit.Assert.fail("testGetCompoundsAt_cf34 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.sequence.MultipleSequenceAlignmentTest#testGetCompoundsAt */
    @org.junit.Test(timeout = 10000)
    public void testGetCompoundsAt_add1_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.biojava.nbio.core.sequence.compound.AminoAcidCompound aminoAcid = org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet.getAminoAcidCompoundSet().getCompoundForString("N");
            java.util.List<org.biojava.nbio.core.sequence.compound.AminoAcidCompound> colProteins = new java.util.ArrayList<org.biojava.nbio.core.sequence.compound.AminoAcidCompound>();
            for (int i = 0; i < 8; i++) {
                // MethodCallAdder
                colProteins.add(aminoAcid);
                colProteins.add(aminoAcid);
            }
            // MethodAssertGenerator build local variable
            Object o_13_0 = msaProteins.getCompoundsAt(3);
            org.biojava.nbio.core.sequence.compound.NucleotideCompound nucleotide = org.biojava.nbio.core.sequence.compound.DNACompoundSet.getDNACompoundSet().getCompoundForString("C");
            java.util.List<org.biojava.nbio.core.sequence.compound.NucleotideCompound> colDNA = new java.util.ArrayList<org.biojava.nbio.core.sequence.compound.NucleotideCompound>();
            for (int i = 0; i < 7; i++) {
                colDNA.add(nucleotide);
            }
            // MethodAssertGenerator build local variable
            Object o_25_0 = msaDNA.getCompoundsAt(3);
            org.junit.Assert.fail("testGetCompoundsAt_add1 should have thrown Throwable");
        } catch (java.lang.Throwable eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.sequence.MultipleSequenceAlignmentTest#testGetCompoundsAt */
    @org.junit.Test(timeout = 10000)
    public void testGetCompoundsAt_cf24_failAssert19() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.biojava.nbio.core.sequence.compound.AminoAcidCompound aminoAcid = org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet.getAminoAcidCompoundSet().getCompoundForString("N");
            java.util.List<org.biojava.nbio.core.sequence.compound.AminoAcidCompound> colProteins = new java.util.ArrayList<org.biojava.nbio.core.sequence.compound.AminoAcidCompound>();
            for (int i = 0; i < 8; i++) {
                colProteins.add(aminoAcid);
            }
            // MethodAssertGenerator build local variable
            Object o_11_0 = msaProteins.getCompoundsAt(3);
            org.biojava.nbio.core.sequence.compound.NucleotideCompound nucleotide = org.biojava.nbio.core.sequence.compound.DNACompoundSet.getDNACompoundSet().getCompoundForString("C");
            java.util.List<org.biojava.nbio.core.sequence.compound.NucleotideCompound> colDNA = new java.util.ArrayList<org.biojava.nbio.core.sequence.compound.NucleotideCompound>();
            for (int i = 0; i < 7; i++) {
                colDNA.add(nucleotide);
            }
            // StatementAdderOnAssert create random local variable
            int vc_2 = -1733830533;
            // StatementAdderOnAssert create random local variable
            org.biojava.nbio.core.sequence.MultipleSequenceAlignment vc_1 = new org.biojava.nbio.core.sequence.MultipleSequenceAlignment();
            // StatementAdderMethod cloned existing statement
            vc_1.getAlignedSequence(vc_2);
            // MethodAssertGenerator build local variable
            Object o_29_0 = msaDNA.getCompoundsAt(3);
            org.junit.Assert.fail("testGetCompoundsAt_cf24 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.sequence.MultipleSequenceAlignmentTest#testGetCompoundsAt */
    @org.junit.Test(timeout = 10000)
    public void testGetCompoundsAt_cf30_failAssert21() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.biojava.nbio.core.sequence.compound.AminoAcidCompound aminoAcid = org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet.getAminoAcidCompoundSet().getCompoundForString("N");
            java.util.List<org.biojava.nbio.core.sequence.compound.AminoAcidCompound> colProteins = new java.util.ArrayList<org.biojava.nbio.core.sequence.compound.AminoAcidCompound>();
            for (int i = 0; i < 8; i++) {
                colProteins.add(aminoAcid);
            }
            // MethodAssertGenerator build local variable
            Object o_11_0 = msaProteins.getCompoundsAt(3);
            org.biojava.nbio.core.sequence.compound.NucleotideCompound nucleotide = org.biojava.nbio.core.sequence.compound.DNACompoundSet.getDNACompoundSet().getCompoundForString("C");
            java.util.List<org.biojava.nbio.core.sequence.compound.NucleotideCompound> colDNA = new java.util.ArrayList<org.biojava.nbio.core.sequence.compound.NucleotideCompound>();
            for (int i = 0; i < 7; i++) {
                colDNA.add(nucleotide);
            }
            // StatementAdderOnAssert create random local variable
            org.biojava.nbio.core.sequence.MultipleSequenceAlignment vc_8 = new org.biojava.nbio.core.sequence.MultipleSequenceAlignment();
            // StatementAdderMethod cloned existing statement
            vc_8.getLength();
            // MethodAssertGenerator build local variable
            Object o_27_0 = msaDNA.getCompoundsAt(3);
            org.junit.Assert.fail("testGetCompoundsAt_cf30 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.sequence.MultipleSequenceAlignmentTest#testGetCompoundsAt */
    @org.junit.Test(timeout = 10000)
    public void testGetCompoundsAt_cf38_failAssert28() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.biojava.nbio.core.sequence.compound.AminoAcidCompound aminoAcid = org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet.getAminoAcidCompoundSet().getCompoundForString("N");
            java.util.List<org.biojava.nbio.core.sequence.compound.AminoAcidCompound> colProteins = new java.util.ArrayList<org.biojava.nbio.core.sequence.compound.AminoAcidCompound>();
            for (int i = 0; i < 8; i++) {
                colProteins.add(aminoAcid);
            }
            // MethodAssertGenerator build local variable
            Object o_11_0 = msaProteins.getCompoundsAt(3);
            org.biojava.nbio.core.sequence.compound.NucleotideCompound nucleotide = org.biojava.nbio.core.sequence.compound.DNACompoundSet.getDNACompoundSet().getCompoundForString("C");
            java.util.List<org.biojava.nbio.core.sequence.compound.NucleotideCompound> colDNA = new java.util.ArrayList<org.biojava.nbio.core.sequence.compound.NucleotideCompound>();
            for (int i = 0; i < 7; i++) {
                colDNA.add(nucleotide);
            }
            // StatementAdderOnAssert create random local variable
            int vc_15 = -768733878;
            // StatementAdderOnAssert create random local variable
            org.biojava.nbio.core.sequence.MultipleSequenceAlignment vc_14 = new org.biojava.nbio.core.sequence.MultipleSequenceAlignment();
            // StatementAdderMethod cloned existing statement
            vc_14.toString(vc_15);
            // MethodAssertGenerator build local variable
            Object o_29_0 = msaDNA.getCompoundsAt(3);
            org.junit.Assert.fail("testGetCompoundsAt_cf38 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

