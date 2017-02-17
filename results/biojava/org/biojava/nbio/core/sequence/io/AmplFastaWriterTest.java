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


package org.biojava.nbio.core.sequence.io;


public class AmplFastaWriterTest {
    @org.junit.Test
    public void writeBasicFasta() throws java.lang.Exception {
        java.lang.String id = "Example";
        java.lang.String dnaLineOne = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        java.lang.String dnaLineTwo = "T";
        org.biojava.nbio.core.sequence.DNASequence s = new org.biojava.nbio.core.sequence.DNASequence((dnaLineOne + dnaLineTwo));
        s.setAccession(new org.biojava.nbio.core.sequence.AccessionID(id));
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        org.biojava.nbio.core.sequence.io.FastaWriterHelper.writeSequence(baos, s);
        java.lang.String actual = new java.lang.String(baos.toByteArray());
        java.lang.String expected = java.lang.String.format(">%s%n%s%n%s%n", id, dnaLineOne, dnaLineTwo);
        org.junit.Assert.assertThat("Writer not as expected", actual, org.hamcrest.CoreMatchers.is(expected));
    }

    @org.junit.Test
    public void writeFastaEqualToLineLength() throws java.lang.Exception {
        java.lang.String id = "Example";
        java.lang.String dna = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAT";
        org.biojava.nbio.core.sequence.DNASequence s = new org.biojava.nbio.core.sequence.DNASequence(dna);
        s.setAccession(new org.biojava.nbio.core.sequence.AccessionID(id));
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        org.biojava.nbio.core.sequence.io.FastaWriterHelper.writeSequence(baos, s);
        java.lang.String actual = new java.lang.String(baos.toByteArray());
        java.lang.String expected = java.lang.String.format(">%s%n%s%n", id, dna);
        org.junit.Assert.assertThat("Writer not as expected", actual, org.hamcrest.CoreMatchers.is(expected));
    }

    /* amplification of org.biojava.nbio.core.sequence.io.FastaWriterTest#writeBasicFasta */
    @org.junit.Test(timeout = 10000)
    public void writeBasicFasta_cf24() throws java.lang.Exception {
        java.lang.String id = "Example";
        java.lang.String dnaLineOne = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        java.lang.String dnaLineTwo = "T";
        org.biojava.nbio.core.sequence.DNASequence s = new org.biojava.nbio.core.sequence.DNASequence((dnaLineOne + dnaLineTwo));
        s.setAccession(new org.biojava.nbio.core.sequence.AccessionID(id));
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        org.biojava.nbio.core.sequence.io.FastaWriterHelper.writeSequence(baos, s);
        java.lang.String actual = new java.lang.String(baos.toByteArray());
        java.lang.String expected = java.lang.String.format(">%s%n%s%n%s%n", id, dnaLineOne, dnaLineTwo);
        // StatementAdderOnAssert create random local variable
        java.lang.String[] vc_5 = new java.lang.String []{new java.lang.String(),new java.lang.String(),new java.lang.String(),new java.lang.String()};
        // StatementAdderOnAssert create null value
        org.biojava.nbio.core.sequence.io.FastaWriter vc_2 = (org.biojava.nbio.core.sequence.io.FastaWriter)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2);
        // StatementAdderMethod cloned existing statement
        vc_2.main(vc_5);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2);
        org.junit.Assert.assertThat("Writer not as expected", actual, org.hamcrest.CoreMatchers.is(expected));
    }

    /* amplification of org.biojava.nbio.core.sequence.io.FastaWriterTest#writeBasicFasta */
    @org.junit.Test(timeout = 10000)
    public void writeBasicFasta_cf22_literalMutation674() throws java.lang.Exception {
        java.lang.String id = "Example";
        java.lang.String dnaLineOne = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        java.lang.String dnaLineTwo = "T";
        org.biojava.nbio.core.sequence.DNASequence s = new org.biojava.nbio.core.sequence.DNASequence((dnaLineOne + dnaLineTwo));
        s.setAccession(new org.biojava.nbio.core.sequence.AccessionID(id));
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        org.biojava.nbio.core.sequence.io.FastaWriterHelper.writeSequence(baos, s);
        java.lang.String actual = new java.lang.String(baos.toByteArray());
        java.lang.String expected = java.lang.String.format(">%s%n%s%n%s%n", id, dnaLineOne, dnaLineTwo);
        // StatementAdderOnAssert create null value
        java.lang.String[] vc_4 = (java.lang.String[])null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4);
        // StatementAdderOnAssert create null value
        org.biojava.nbio.core.sequence.io.FastaWriter vc_2 = (org.biojava.nbio.core.sequence.io.FastaWriter)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2);
        // StatementAdderMethod cloned existing statement
        vc_2.main(vc_4);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2);
        org.junit.Assert.assertThat("", actual, org.hamcrest.CoreMatchers.is(expected));
    }

    /* amplification of org.biojava.nbio.core.sequence.io.FastaWriterTest#writeFastaEqualToLineLength */
    @org.junit.Test(timeout = 10000)
    public void writeFastaEqualToLineLength_cf8370() throws java.lang.Exception {
        java.lang.String id = "Example";
        java.lang.String dna = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAT";
        org.biojava.nbio.core.sequence.DNASequence s = new org.biojava.nbio.core.sequence.DNASequence(dna);
        s.setAccession(new org.biojava.nbio.core.sequence.AccessionID(id));
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        org.biojava.nbio.core.sequence.io.FastaWriterHelper.writeSequence(baos, s);
        java.lang.String actual = new java.lang.String(baos.toByteArray());
        java.lang.String expected = java.lang.String.format(">%s%n%s%n", id, dna);
        // StatementAdderOnAssert create random local variable
        java.lang.String[] vc_2285 = new java.lang.String []{new java.lang.String()};
        // StatementAdderOnAssert create null value
        org.biojava.nbio.core.sequence.io.FastaWriter vc_2282 = (org.biojava.nbio.core.sequence.io.FastaWriter)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2282);
        // StatementAdderMethod cloned existing statement
        vc_2282.main(vc_2285);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2282);
        org.junit.Assert.assertThat("Writer not as expected", actual, org.hamcrest.CoreMatchers.is(expected));
    }

    /* amplification of org.biojava.nbio.core.sequence.io.FastaWriterTest#writeFastaEqualToLineLength */
    @org.junit.Test(timeout = 10000)
    public void writeFastaEqualToLineLength_literalMutation8351_literalMutation8445_cf13719() throws java.lang.Exception {
        java.lang.String id = "";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(id, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(id, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(id, "");
        java.lang.String dna = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAT";
        org.biojava.nbio.core.sequence.DNASequence s = new org.biojava.nbio.core.sequence.DNASequence(dna);
        s.setAccession(new org.biojava.nbio.core.sequence.AccessionID(id));
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        org.biojava.nbio.core.sequence.io.FastaWriterHelper.writeSequence(baos, s);
        java.lang.String actual = new java.lang.String(baos.toByteArray());
        java.lang.String expected = java.lang.String.format(">%s%n%s%n", id, dna);
        // StatementAdderOnAssert create null value
        java.lang.String[] vc_3754 = (java.lang.String[])null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3754);
        // StatementAdderOnAssert create null value
        org.biojava.nbio.core.sequence.io.FastaWriter vc_3752 = (org.biojava.nbio.core.sequence.io.FastaWriter)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3752);
        // StatementAdderMethod cloned existing statement
        vc_3752.main(vc_3754);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3752);
        org.junit.Assert.assertThat(":B{ywL,&y)#32|2fiNp\\$M", actual, org.hamcrest.CoreMatchers.is(expected));
    }
}

