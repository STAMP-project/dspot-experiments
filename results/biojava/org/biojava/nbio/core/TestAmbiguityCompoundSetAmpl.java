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


package org.biojava.nbio.core;


/**
 * A Test case for https://github.com/biojava/biojava/issues/344
 *
 * Created by andreas on 12/4/15.
 */
public class TestAmbiguityCompoundSetAmpl extends junit.framework.TestCase {
    @org.junit.Test
    public void testCompountSet() {
        try {
            org.biojava.nbio.core.sequence.template.CompoundSet<org.biojava.nbio.core.sequence.compound.NucleotideCompound> dnaSet = org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet.getDNACompoundSet();
            org.biojava.nbio.core.sequence.template.CompoundSet<org.biojava.nbio.core.sequence.compound.NucleotideCompound> rnaSet = org.biojava.nbio.core.sequence.compound.AmbiguityRNACompoundSet.getRNACompoundSet();
            org.biojava.nbio.core.sequence.DNASequence dna = new org.biojava.nbio.core.sequence.DNASequence("AGTCS", dnaSet);
            junit.framework.TestCase.assertEquals("AGTCS", dna.toString());
            org.biojava.nbio.core.sequence.RNASequence rna = dna.getRNASequence();
            rna = new org.biojava.nbio.core.sequence.RNASequence(dna.getSequenceAsString().replaceAll("T", "U"), org.biojava.nbio.core.sequence.compound.AmbiguityRNACompoundSet.getRNACompoundSet());// fails with missing compound S
            
            junit.framework.TestCase.assertEquals("AGUCS", rna.toString());
            /**
             * now, do the translation also using the underlying API (should not be needed for a user)
             */
            org.biojava.nbio.core.sequence.transcription.DNAToRNATranslator translator = new org.biojava.nbio.core.sequence.transcription.DNAToRNATranslator(new org.biojava.nbio.core.sequence.io.RNASequenceCreator(rnaSet), dnaSet, rnaSet, false);
            org.biojava.nbio.core.sequence.template.Sequence<org.biojava.nbio.core.sequence.compound.NucleotideCompound> translated = translator.createSequence(dna);
            junit.framework.TestCase.assertEquals("AGUCS", translated.toString());
        } catch (org.biojava.nbio.core.exceptions.CompoundNotFoundException e) {
            e.printStackTrace();
            junit.framework.TestCase.fail(e.getMessage());
        }
    }

    /* amplification of org.biojava.nbio.core.TestAmbiguityCompoundSet#testCompountSet */
    @org.junit.Test
    public void testCompountSet_literalMutation17_failAssert15() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            try {
                org.biojava.nbio.core.sequence.template.CompoundSet<org.biojava.nbio.core.sequence.compound.NucleotideCompound> dnaSet = org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet.getDNACompoundSet();
                org.biojava.nbio.core.sequence.template.CompoundSet<org.biojava.nbio.core.sequence.compound.NucleotideCompound> rnaSet = org.biojava.nbio.core.sequence.compound.AmbiguityRNACompoundSet.getRNACompoundSet();
                org.biojava.nbio.core.sequence.DNASequence dna = new org.biojava.nbio.core.sequence.DNASequence("AGTCS", dnaSet);
                // MethodAssertGenerator build local variable
                Object o_9_0 = dna.toString();
                org.biojava.nbio.core.sequence.RNASequence rna = dna.getRNASequence();
                rna = new org.biojava.nbio.core.sequence.RNASequence(dna.getSequenceAsString().replaceAll("T", "U"), org.biojava.nbio.core.sequence.compound.AmbiguityRNACompoundSet.getRNACompoundSet());// fails with missing compound S
                
                // MethodAssertGenerator build local variable
                Object o_19_0 = rna.toString();
                /**
                 * now, do the translation also using the underlying API (should not be needed for a user)
                 */
                org.biojava.nbio.core.sequence.transcription.DNAToRNATranslator translator = new org.biojava.nbio.core.sequence.transcription.DNAToRNATranslator(new org.biojava.nbio.core.sequence.io.RNASequenceCreator(rnaSet), dnaSet, rnaSet, true);
                org.biojava.nbio.core.sequence.template.Sequence<org.biojava.nbio.core.sequence.compound.NucleotideCompound> translated = translator.createSequence(dna);
                // MethodAssertGenerator build local variable
                Object o_28_0 = translated.toString();
            } catch (org.biojava.nbio.core.exceptions.CompoundNotFoundException e) {
                e.printStackTrace();
                junit.framework.TestCase.fail(e.getMessage());
            }
            org.junit.Assert.fail("testCompountSet_literalMutation17 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.TestAmbiguityCompoundSet#testCompountSet */
    @org.junit.Test(timeout = 10000)
    public void testCompountSet_literalMutation17_failAssert15_cf698() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            try {
                org.biojava.nbio.core.sequence.template.CompoundSet<org.biojava.nbio.core.sequence.compound.NucleotideCompound> dnaSet = org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet.getDNACompoundSet();
                org.biojava.nbio.core.sequence.template.CompoundSet<org.biojava.nbio.core.sequence.compound.NucleotideCompound> rnaSet = org.biojava.nbio.core.sequence.compound.AmbiguityRNACompoundSet.getRNACompoundSet();
                org.biojava.nbio.core.sequence.DNASequence dna = new org.biojava.nbio.core.sequence.DNASequence("AGTCS", dnaSet);
                // MethodAssertGenerator build local variable
                Object o_9_0 = dna.toString();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_9_0, "AGTCS");
                org.biojava.nbio.core.sequence.RNASequence rna = dna.getRNASequence();
                rna = new org.biojava.nbio.core.sequence.RNASequence(dna.getSequenceAsString().replaceAll("T", "U"), org.biojava.nbio.core.sequence.compound.AmbiguityRNACompoundSet.getRNACompoundSet());// fails with missing compound S
                
                // MethodAssertGenerator build local variable
                Object o_19_0 = rna.toString();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_19_0, "AGUCS");
                /**
                 * now, do the translation also using the underlying API (should not be needed for a user)
                 */
                org.biojava.nbio.core.sequence.transcription.DNAToRNATranslator translator = new org.biojava.nbio.core.sequence.transcription.DNAToRNATranslator(new org.biojava.nbio.core.sequence.io.RNASequenceCreator(rnaSet), dnaSet, rnaSet, true);
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((org.biojava.nbio.core.sequence.transcription.DNAToRNATranslator)translator).getToCompoundSet().equals(rnaSet));
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(((java.util.ArrayList)((org.biojava.nbio.core.sequence.compound.AmbiguityRNACompoundSet)((org.biojava.nbio.core.sequence.transcription.DNAToRNATranslator)translator).getToCompoundSet()).getAllCompounds()).isEmpty());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((java.util.ArrayList)((org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet)((org.biojava.nbio.core.sequence.transcription.DNAToRNATranslator)translator).getFromCompoundSet()).getAllCompounds()).size(), 33);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet)((org.biojava.nbio.core.sequence.transcription.DNAToRNATranslator)translator).getFromCompoundSet()).getMaxSingleCompoundStringLength(), 1);
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((org.biojava.nbio.core.sequence.transcription.DNAToRNATranslator)translator).getFromCompoundSet().equals(dnaSet));
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(((org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet)((org.biojava.nbio.core.sequence.transcription.DNAToRNATranslator)translator).getFromCompoundSet()).isCompoundStringLengthEqual());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((org.biojava.nbio.core.sequence.compound.AmbiguityRNACompoundSet)((org.biojava.nbio.core.sequence.transcription.DNAToRNATranslator)translator).getToCompoundSet()).getMaxSingleCompoundStringLength(), 1);
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((org.biojava.nbio.core.sequence.compound.AmbiguityRNACompoundSet)((org.biojava.nbio.core.sequence.transcription.DNAToRNATranslator)translator).getToCompoundSet()).isComplementable());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((java.util.ArrayList)((org.biojava.nbio.core.sequence.compound.AmbiguityRNACompoundSet)((org.biojava.nbio.core.sequence.transcription.DNAToRNATranslator)translator).getToCompoundSet()).getAllCompounds()).size(), 31);
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet)((org.biojava.nbio.core.sequence.transcription.DNAToRNATranslator)translator).getFromCompoundSet()).isComplementable());
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(((java.util.ArrayList)((org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet)((org.biojava.nbio.core.sequence.transcription.DNAToRNATranslator)translator).getFromCompoundSet()).getAllCompounds()).isEmpty());
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(((org.biojava.nbio.core.sequence.compound.AmbiguityRNACompoundSet)((org.biojava.nbio.core.sequence.transcription.DNAToRNATranslator)translator).getToCompoundSet()).isCompoundStringLengthEqual());
                org.biojava.nbio.core.sequence.template.Sequence<org.biojava.nbio.core.sequence.compound.NucleotideCompound> translated = translator.createSequence(dna);
                // MethodAssertGenerator build local variable
                Object o_28_0 = translated.toString();
            } catch (org.biojava.nbio.core.exceptions.CompoundNotFoundException e) {
                e.printStackTrace();
                // StatementAdderOnAssert create null value
                org.biojava.nbio.core.sequence.template.CompoundSet vc_532 = (org.biojava.nbio.core.sequence.template.CompoundSet)null;
                // StatementAdderMethod cloned existing statement
                vc_532.getMaxSingleCompoundStringLength();
                junit.framework.TestCase.fail(e.getMessage());
            }
            org.junit.Assert.fail("testCompountSet_literalMutation17 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

