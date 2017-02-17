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


package org.biojava.nbio.core.search.io;


/**
 * @author Paolo Pavan
 */
public class AmplHspTest {
    org.biojava.nbio.core.search.io.Hsp hspImpl = new org.biojava.nbio.core.search.io.blast.BlastHspBuilder().setHspNum(1).setHspBitScore(377.211).setHspEvalue(8.04143E-93).setHspQueryFrom(1).setHspQueryTo(224).setHspHitFrom(1035).setHspHitTo(811).setHspQueryFrame((-1)).setHspIdentity(213).setHspPositive(213).setHspGaps(5).setHspAlignLen(227).setHspQseq("CTGACGACAGCCATGCACCACCTGTCTCGACTTTCCCCCGAAGGGCACCTAATGTATCTCTACCTCGTTAGTCGGATGTCAAGACCTGGTAAGGTTTTTTCGCGTATCTTCGAATTAAACCACATACTCCACTGCTTGTGCGG-CCCCCGTCAATTCCTTTGAGTTTCAACCTTGCGGCCGTACTCCC-AGGTGGA-TACTTATTGTGTTAACTCCGGCACGGAAGG").setHspHseq("CTGACGACAACCATGCACCACCTGTCTCAACTTTCCCC-GAAGGGCACCTAATGTATCTCTACTTCGTTAGTTGGATGTCAAGACCTGGTAAGGTT-CTTCGCGTTGCTTCGAATTAAACCACATACTCCACTGCTTGTGCGGGCCCCCGTCAATTCCTTTGAGTTTCAACCTTGCGGTCGTACTCCCCAGGTGGATTACTTATTGTGTTAACTCCGGCACAGAAGG").setHspIdentityString("||||||||| |||||||||||||||||| ||||||||| |||||||||||||||||||||||| |||||||| |||||||||||||||||||||||  |||||||  |||||||||||||||||||||||||||||||||||| |||||||||||||||||||||||||||||||||| ||||||||| ||||||| |||||||||||||||||||||||| |||||").createBlastHsp();

    org.biojava.nbio.core.search.io.Hsp uncompleteHsp = new org.biojava.nbio.core.search.io.blast.BlastHspBuilder().setPercentageIdentity((100.0 / 100)).setHspAlignLen(48).setMismatchCount(0).setHspGaps(0).setHspQueryFrom(1).setHspQueryTo(48).setHspHitFrom(344).setHspHitTo(391).setHspEvalue(4.0E-19).setHspBitScore(95.6).createBlastHsp();

    public AmplHspTest() {
    }

    @org.junit.BeforeClass
    public static void setUpClass() {
    }

    @org.junit.AfterClass
    public static void tearDownClass() {
    }

    @org.junit.Before
    public void setUp() {
    }

    @org.junit.After
    public void tearDown() {
    }

    /**
     * Test of hashCode method, of class Hsp.
     */
    @org.junit.Test
    public void testHashCode() {
        java.lang.System.out.println("hashCode");
        org.biojava.nbio.core.search.io.Hsp instance;
        int expResult;
        int result;
        instance = new org.biojava.nbio.core.search.io.blast.BlastHspBuilder().setHspNum(1).setHspBitScore(377.211).setHspEvalue(8.04143E-93).setHspQueryFrom(1).setHspQueryTo(224).setHspHitFrom(1035).setHspHitTo(811).setHspQueryFrame((-1)).setHspIdentity(213).setHspPositive(213).setHspGaps(5).setHspAlignLen(227).setHspQseq("CTGACGACAGCCATGCACCACCTGTCTCGACTTTCCCCCGAAGGGCACCTAATGTATCTCTACCTCGTTAGTCGGATGTCAAGACCTGGTAAGGTTTTTTCGCGTATCTTCGAATTAAACCACATACTCCACTGCTTGTGCGG-CCCCCGTCAATTCCTTTGAGTTTCAACCTTGCGGCCGTACTCCC-AGGTGGA-TACTTATTGTGTTAACTCCGGCACGGAAGG").setHspHseq("CTGACGACAACCATGCACCACCTGTCTCAACTTTCCCC-GAAGGGCACCTAATGTATCTCTACTTCGTTAGTTGGATGTCAAGACCTGGTAAGGTT-CTTCGCGTTGCTTCGAATTAAACCACATACTCCACTGCTTGTGCGGGCCCCCGTCAATTCCTTTGAGTTTCAACCTTGCGGTCGTACTCCCCAGGTGGATTACTTATTGTGTTAACTCCGGCACAGAAGG").setHspIdentityString("||||||||| |||||||||||||||||| ||||||||| |||||||||||||||||||||||| |||||||| |||||||||||||||||||||||  |||||||  |||||||||||||||||||||||||||||||||||| |||||||||||||||||||||||||||||||||| ||||||||| ||||||| |||||||||||||||||||||||| |||||").createBlastHsp();
        expResult = hspImpl.hashCode();
        result = instance.hashCode();
        org.junit.Assert.assertEquals(expResult, result);
        instance = new org.biojava.nbio.core.search.io.blast.BlastHspBuilder().setPercentageIdentity((100.0 / 100)).setHspAlignLen(48).setMismatchCount(0).setHspGaps(0).setHspQueryFrom(1).setHspQueryTo(48).setHspHitFrom(344).setHspHitTo(391).setHspEvalue(4.0E-19).setHspBitScore(95.6).createBlastHsp();
        expResult = uncompleteHsp.hashCode();
        result = instance.hashCode();
        org.junit.Assert.assertEquals(expResult, result);
        org.biojava.nbio.core.search.io.Hsp uncompleteHsp2 = new org.biojava.nbio.core.search.io.blast.BlastHspBuilder().setPercentageIdentity((100.0 / 100)).setHspAlignLen(48).setMismatchCount(0).setHspGaps(0).setHspQueryFrom(1).setHspQueryTo(48).setHspHitFrom(344).setHspHitTo(391).setHspEvalue(4.0E-19).setHspBitScore(95.6).createBlastHsp();
        org.junit.Assert.assertEquals(uncompleteHsp.hashCode(), uncompleteHsp2.hashCode());
    }

    /**
     * Test of equals method, of class Hsp.
     */
    @org.junit.Test
    public void testEquals() {
        java.lang.System.out.println("equals");
        java.lang.Object o;
        o = new org.biojava.nbio.core.search.io.blast.BlastHspBuilder().setHspNum(1).setHspBitScore(377.211).setHspEvalue(8.04143E-93).setHspQueryFrom(1).setHspQueryTo(224).setHspHitFrom(1035).setHspHitTo(811).setHspQueryFrame((-1)).setHspIdentity(213).setHspPositive(213).setHspGaps(5).setHspAlignLen(227).setHspQseq("CTGACGACAGCCATGCACCACCTGTCTCGACTTTCCCCCGAAGGGCACCTAATGTATCTCTACCTCGTTAGTCGGATGTCAAGACCTGGTAAGGTTTTTTCGCGTATCTTCGAATTAAACCACATACTCCACTGCTTGTGCGG-CCCCCGTCAATTCCTTTGAGTTTCAACCTTGCGGCCGTACTCCC-AGGTGGA-TACTTATTGTGTTAACTCCGGCACGGAAGG").setHspHseq("CTGACGACAACCATGCACCACCTGTCTCAACTTTCCCC-GAAGGGCACCTAATGTATCTCTACTTCGTTAGTTGGATGTCAAGACCTGGTAAGGTT-CTTCGCGTTGCTTCGAATTAAACCACATACTCCACTGCTTGTGCGGGCCCCCGTCAATTCCTTTGAGTTTCAACCTTGCGGTCGTACTCCCCAGGTGGATTACTTATTGTGTTAACTCCGGCACAGAAGG").setHspIdentityString("||||||||| |||||||||||||||||| ||||||||| |||||||||||||||||||||||| |||||||| |||||||||||||||||||||||  |||||||  |||||||||||||||||||||||||||||||||||| |||||||||||||||||||||||||||||||||| ||||||||| ||||||| |||||||||||||||||||||||| |||||").createBlastHsp();
        org.biojava.nbio.core.search.io.Hsp instance = hspImpl;
        org.junit.Assert.assertEquals(o, instance);
        // example of Hsp retrieved from uncomplete report.
        // (Those HSP may come from a tabular format, for example)
        o = new org.biojava.nbio.core.search.io.blast.BlastHspBuilder().setPercentageIdentity((100.0 / 100)).setHspAlignLen(48).setMismatchCount(0).setHspGaps(0).setHspQueryFrom(1).setHspQueryTo(48).setHspHitFrom(344).setHspHitTo(391).setHspEvalue(4.0E-19).setHspBitScore(95.6).createBlastHsp();
        org.junit.Assert.assertEquals(uncompleteHsp, o);
    }

    /**
     * Test of getAlignment method, of class Hsp.
     */
    @org.junit.Test
    public void testGetAlignment() {
        java.lang.System.out.println("getAlignment");
        org.biojava.nbio.core.alignment.template.SequencePair<org.biojava.nbio.core.sequence.DNASequence, org.biojava.nbio.core.sequence.compound.NucleotideCompound> aln = hspImpl.getAlignment();
        java.lang.StringBuilder s = new java.lang.StringBuilder();
        s.append(hspImpl.getHspQseq());
        s.append(java.lang.String.format("%n"));
        s.append(hspImpl.getHspHseq());
        s.append(java.lang.String.format("%n"));
        java.lang.String expResult = s.toString();
        java.lang.String result = aln.toString();
        org.junit.Assert.assertEquals(expResult, result);
    }
}

