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


package org.biojava.nbio.core.search.io.blast;


/**
 * @author Paolo Pavan
 */
public class AmplBlastXMLParserTest {
    public AmplBlastXMLParserTest() {
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
     * Test of setFile method, of class BlastXMLParser.
     */
    @org.junit.Test
    public void testSetFile() {
        java.lang.System.out.println("setFile");
        java.io.File f = null;
        org.biojava.nbio.core.search.io.blast.BlastXMLParser instance = new org.biojava.nbio.core.search.io.blast.BlastXMLParser();
        instance.setFile(f);
    }

    protected java.io.File getFileForResource(java.lang.String resource) {
        java.net.URL resourceURL = this.getClass().getResource(resource);
        java.lang.String filepath = resourceURL.getFile();
        filepath = filepath.replaceAll("%20", " ");
        java.io.File file = new java.io.File(filepath);
        return file;
    }

    /**
     * Test of createObjects method, of class BlastXMLParser.
     */
    @org.junit.Test
    public void testCreateObjects() throws java.lang.Exception {
        java.lang.System.out.println("createObjects");
        java.lang.String resource = "/org/biojava/nbio/core/search/io/blast/small-blastreport.blastxml";
        java.io.File file = getFileForResource(resource);
        org.biojava.nbio.core.search.io.blast.BlastXMLParser instance = new org.biojava.nbio.core.search.io.blast.BlastXMLParser();
        instance.setFile(file);
        // instance.setQueryReferences(null);
        // instance.setDatabaseReferences(null);
        java.util.List<org.biojava.nbio.core.search.io.Result> result = instance.createObjects(1.0E-10);
        // test with random manual selected results
        org.biojava.nbio.core.search.io.blast.BlastHsp hsp1hit1res1 = new org.biojava.nbio.core.search.io.blast.BlastHspBuilder().setHspNum(1).setHspBitScore(2894.82).setHspScore(1567).setHspEvalue(0).setHspQueryFrom(1).setHspQueryTo(1567).setHspHitFrom(616309).setHspHitTo(617875).setHspQueryFrame(1).setHspHitFrame(1).setHspIdentity(1567).setHspPositive(1567).setHspGaps(0).setHspAlignLen(1567).setHspQseq("TTAAATTGAGAGTTTGATCCTGGCTCAGGATGAACGCTGGTGGCGTGCCTAATACATGCAAGTCGTACGCTAGCCGCTGAATTGATCCTTCGGGTGAAGTGAGGCAATGACTAGAGTGGCGAACTGGTGAGTAACACGTAAGAAACCTGCCCTTTAGTGGGGGATAACATTTGGAAACAGATGCTAATACCGCGTAACAACAAATCACACATGTGATCTGTTTGAAAGGTCCTTTTGGATCGCTAGAGGATGGTCTTGCGGCGTATTAGCTTGTTGGTAGGGTAGAAGCCTACCAAGGCAATGATGCGTAGCCGAGTTGAGAGACTGGCCGGCCACATTGGGACTGAGACACTGCCCAAACTCCTACGGGAGGCTGCAGTAGGGAATTTTCCGCAATGCACGAAAGTGTGACGGAGCGACGCCGCGTGTGTGATGAAGGCTTTCGGGTCGTAAAGCACTGTTGTAAGGGAAGAATAACTGAATTCAGAGAAAGTTTTCAGCTTGACGGTACCTTACCAGAAAGGGATGGCTAAATACGTGCCAGCAGCCGCGGTAATACGTATGTCCCGAGCGTTATCCGGATTTATTGGGCGTAAAGCGAGCGCAGACGGTTTATTAAGTCTGATGTGAAATCCCGAGGCCCAACCTCGGAACTGCATTGGAAACTGATTTACTTGAGTGCGATAGAGGCAAGTGGAACTCCATGTGTAGCGGTGAAATGCGTAGATATGTGGAAGAACACCAGTGGCGAAAGCGGCTTGCTAGATCGTAACTGACGTTGAGGCTCGAAAGTATGGGTAGCAAACGGGATTAGATACCCCGGTAGTCCATACCGTAAACGATGGGTGCTAGTTGTTAAGAGGTTTCCGCCTCCTAGTGACGTAGCAAACGCATTAAGCACCCCGCCTGAGGAGTACGGCCGCAAGGCTAAAACTTAAAGGAATTGACGGGGACCCGCACAAGCGGTGGAGCATGTGGTTTAATTCGAAGATACGCGAAAAACCTTACCAGGTCTTGACATACCAATGATCGCTTTTGTAATGAAAGCTTTTCTTCGGAACATTGGATACAGGTGGTGCATGGTCGTCGTCAGCTCGTGTCGTGAGATGTTGGGTTAAGTCCCGCAACGAGCGCAACCCTTGTTATTAGTTGCCAGCATTTAGTTGGGCACTCTAATGAGACTGCCGGTGATAAACCGGAGGAAGGTGGGGACGACGTCAGATCATCATGCCCCTTATGACCTGGGCAACACACGTGCTACAATGGGAAGTACAACGAGTCGCAAACCGGCGACGGTAAGCTAATCTCTTAAAACTTCTCTCAGTTCGGACTGGAGTCTGCAACTCGACTCCACGAAGGCGGAATCGCTAGTAATCGCGAATCAGCATGTCGCGGTGAATACGTTCCCGGGTCTTGTACACACCGCCCGTCAAATCATGGGAGTCGGAAGTACCCAAAGTCGCTTGGCTAACTTTTAGAGGCCGGTGCCTAAGGTAAAATCGATGACTGGGATTAAGTCGTAACAAGGTAGCCGTAGGAGAACCTGCGGCTGGATCACCTCCTTTCT").setHspHseq("TTAAATTGAGAGTTTGATCCTGGCTCAGGATGAACGCTGGTGGCGTGCCTAATACATGCAAGTCGTACGCTAGCCGCTGAATTGATCCTTCGGGTGAAGTGAGGCAATGACTAGAGTGGCGAACTGGTGAGTAACACGTAAGAAACCTGCCCTTTAGTGGGGGATAACATTTGGAAACAGATGCTAATACCGCGTAACAACAAATCACACATGTGATCTGTTTGAAAGGTCCTTTTGGATCGCTAGAGGATGGTCTTGCGGCGTATTAGCTTGTTGGTAGGGTAGAAGCCTACCAAGGCAATGATGCGTAGCCGAGTTGAGAGACTGGCCGGCCACATTGGGACTGAGACACTGCCCAAACTCCTACGGGAGGCTGCAGTAGGGAATTTTCCGCAATGCACGAAAGTGTGACGGAGCGACGCCGCGTGTGTGATGAAGGCTTTCGGGTCGTAAAGCACTGTTGTAAGGGAAGAATAACTGAATTCAGAGAAAGTTTTCAGCTTGACGGTACCTTACCAGAAAGGGATGGCTAAATACGTGCCAGCAGCCGCGGTAATACGTATGTCCCGAGCGTTATCCGGATTTATTGGGCGTAAAGCGAGCGCAGACGGTTTATTAAGTCTGATGTGAAATCCCGAGGCCCAACCTCGGAACTGCATTGGAAACTGATTTACTTGAGTGCGATAGAGGCAAGTGGAACTCCATGTGTAGCGGTGAAATGCGTAGATATGTGGAAGAACACCAGTGGCGAAAGCGGCTTGCTAGATCGTAACTGACGTTGAGGCTCGAAAGTATGGGTAGCAAACGGGATTAGATACCCCGGTAGTCCATACCGTAAACGATGGGTGCTAGTTGTTAAGAGGTTTCCGCCTCCTAGTGACGTAGCAAACGCATTAAGCACCCCGCCTGAGGAGTACGGCCGCAAGGCTAAAACTTAAAGGAATTGACGGGGACCCGCACAAGCGGTGGAGCATGTGGTTTAATTCGAAGATACGCGAAAAACCTTACCAGGTCTTGACATACCAATGATCGCTTTTGTAATGAAAGCTTTTCTTCGGAACATTGGATACAGGTGGTGCATGGTCGTCGTCAGCTCGTGTCGTGAGATGTTGGGTTAAGTCCCGCAACGAGCGCAACCCTTGTTATTAGTTGCCAGCATTTAGTTGGGCACTCTAATGAGACTGCCGGTGATAAACCGGAGGAAGGTGGGGACGACGTCAGATCATCATGCCCCTTATGACCTGGGCAACACACGTGCTACAATGGGAAGTACAACGAGTCGCAAACCGGCGACGGTAAGCTAATCTCTTAAAACTTCTCTCAGTTCGGACTGGAGTCTGCAACTCGACTCCACGAAGGCGGAATCGCTAGTAATCGCGAATCAGCATGTCGCGGTGAATACGTTCCCGGGTCTTGTACACACCGCCCGTCAAATCATGGGAGTCGGAAGTACCCAAAGTCGCTTGGCTAACTTTTAGAGGCCGGTGCCTAAGGTAAAATCGATGACTGGGATTAAGTCGTAACAAGGTAGCCGTAGGAGAACCTGCGGCTGGATCACCTCCTTTCT").setHspIdentityString("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||").createBlastHsp();
        java.util.List<org.biojava.nbio.core.search.io.Hsp> hsplist = new java.util.ArrayList<org.biojava.nbio.core.search.io.Hsp>();
        hsplist.add(hsp1hit1res1);
        hsplist.add(hsp1hit1res1);
        org.biojava.nbio.core.search.io.blast.BlastHit hit1res1 = new org.biojava.nbio.core.search.io.blast.BlastHitBuilder().setHitNum(1).setHitId("gnl|BL_ORD_ID|2006").setHitDef("CP000411 Oenococcus oeni PSU-1, complete genome").setHitAccession("0").setHitLen(1780517).setHsps(hsplist).createBlastHit();
        java.util.List<org.biojava.nbio.core.search.io.Hit> hitlist = new java.util.ArrayList<org.biojava.nbio.core.search.io.Hit>();
        hitlist.add(hit1res1);
        org.biojava.nbio.core.search.io.blast.BlastResult res1 = new org.biojava.nbio.core.search.io.blast.BlastResultBuilder().setProgram("blastn").setVersion("BLASTN 2.2.29+").setReference("Zheng Zhang, Scott Schwartz, Lukas Wagner, and Webb Miller (2000), &quot;A greedy algorithm for aligning DNA sequences&quot;, J Comput Biol 2000; 7(1-2):203-14.").setQueryID("Query_1").setQueryDef("CP000411_-_16S_rRNA Oenococcus oeni PSU-1, complete genome").setQueryLength(1567).createBlastResult();
        org.biojava.nbio.core.search.io.Result expRes1 = result.get(0);
        org.biojava.nbio.core.search.io.Hit expHit1res1 = expRes1.iterator().next();
        org.biojava.nbio.core.search.io.Hsp expHsp1hit1res1 = expHit1res1.iterator().next();
        // result not testable without all hits and hsp
        // assertEquals(expRes1, res1);
        // hit test
        org.junit.Assert.assertEquals(expHit1res1, hit1res1);
        // hsp test
        org.junit.Assert.assertEquals(expHsp1hit1res1, hsp1hit1res1);
    }

    /**
     * Test of getFileExtensions method, of class BlastXMLParser.
     */
    @org.junit.Test
    public void testGetFileExtensions() {
        java.lang.System.out.println("getFileExtensions");
        org.biojava.nbio.core.search.io.blast.BlastXMLParser instance = new org.biojava.nbio.core.search.io.blast.BlastXMLParser();
        java.util.List<java.lang.String> result = instance.getFileExtensions();
        org.junit.Assert.assertTrue(result.contains("blastxml"));
    }

    /**
     * Test of setQueryReferences method, of class BlastXMLParser.
     */
    @org.junit.Test
    @org.junit.Ignore
    public void testSetQueryReferences() {
        java.lang.System.out.println("setQueryReferences");
        java.util.List<org.biojava.nbio.core.sequence.template.Sequence> sequences = null;
        org.biojava.nbio.core.search.io.blast.BlastXMLParser instance = new org.biojava.nbio.core.search.io.blast.BlastXMLParser();
        instance.setQueryReferences(sequences);
        // TODO review the generated test code and remove the default call to fail.
        org.junit.Assert.fail("The test case is a prototype.");
    }

    /**
     * Test of setDatabaseReferences method, of class BlastXMLParser.
     */
    @org.junit.Test
    @org.junit.Ignore
    public void testSetDatabaseReferences() {
        java.lang.System.out.println("setDatabaseReferences");
        java.util.List<org.biojava.nbio.core.sequence.template.Sequence> sequences = null;
        org.biojava.nbio.core.search.io.blast.BlastXMLParser instance = new org.biojava.nbio.core.search.io.blast.BlastXMLParser();
        instance.setDatabaseReferences(sequences);
        // TODO review the generated test code and remove the default call to fail.
        org.junit.Assert.fail("The test case is a prototype.");
    }

    /**
     * Test of storeObjects method, of class BlastXMLParser.
     */
    @org.junit.Test
    public void testStoreObjects() throws java.lang.Exception {
        // not implemented yet
    }
}

