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


public class AmplCasePreservingProteinSequenceCreatorTest {
    @org.junit.Test
    public void testConstructor() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        org.biojava.nbio.core.sequence.io.CasePreservingProteinSequenceCreator creator = new org.biojava.nbio.core.sequence.io.CasePreservingProteinSequenceCreator(org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet.getAminoAcidCompoundSet());
        java.lang.String seq = "aCDEfgHI-Jkl";
        org.biojava.nbio.core.sequence.ProteinSequence prot = ((org.biojava.nbio.core.sequence.ProteinSequence) (creator.getSequence(seq, 0)));
        java.util.Collection<java.lang.Object> uppercase = prot.getUserCollection();
        // test some assumptions. Hopefully work on non-english locals too?
        org.junit.Assert.assertFalse(java.lang.Character.isUpperCase('-'));
        org.junit.Assert.assertFalse(java.lang.Character.isUpperCase('.'));
        org.junit.Assert.assertEquals("Lengths differ", seq.length(), uppercase.size());
        int i = 0;
        for (java.lang.Object obj : uppercase) {
            org.junit.Assert.assertTrue("Not a Boolean", (obj instanceof java.lang.Boolean));
            java.lang.Boolean bool = ((java.lang.Boolean) (obj));
            org.junit.Assert.assertEquals(("Doesn't match case of " + (seq.charAt(i))), java.lang.Character.isUpperCase(seq.charAt(i)), bool);
            i++;
        }
    }

    /* amplification of org.biojava.nbio.core.sequence.io.CasePreservingProteinSequenceCreatorTest#testConstructor */
    @org.junit.Test
    public void testConstructor_literalMutation1() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        org.biojava.nbio.core.sequence.io.CasePreservingProteinSequenceCreator creator = new org.biojava.nbio.core.sequence.io.CasePreservingProteinSequenceCreator(org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet.getAminoAcidCompoundSet());
        java.lang.String seq = "";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(seq, "");
        org.biojava.nbio.core.sequence.ProteinSequence prot = ((org.biojava.nbio.core.sequence.ProteinSequence) (creator.getSequence(seq, 0)));
        java.util.Collection<java.lang.Object> uppercase = prot.getUserCollection();
        // test some assumptions. Hopefully work on non-english locals too?
        org.junit.Assert.assertFalse(java.lang.Character.isUpperCase('-'));
        org.junit.Assert.assertFalse(java.lang.Character.isUpperCase('.'));
        org.junit.Assert.assertEquals("Lengths differ", seq.length(), uppercase.size());
        int i = 0;
        for (java.lang.Object obj : uppercase) {
            org.junit.Assert.assertTrue("Not a Boolean", (obj instanceof java.lang.Boolean));
            java.lang.Boolean bool = ((java.lang.Boolean) (obj));
            org.junit.Assert.assertEquals(("Doesn't match case of " + (seq.charAt(i))), java.lang.Character.isUpperCase(seq.charAt(i)), bool);
            i++;
        }
    }
}

