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


public class AmplDNATest {
    private org.biojava.nbio.core.sequence.compound.DNACompoundSet set = new org.biojava.nbio.core.sequence.compound.DNACompoundSet();

    private org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet ambiguity = new org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet();

    @org.junit.Test
    public void reverseComplement() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        java.lang.String s = getSeq().getInverse().getSequenceAsString();
        org.junit.Assert.assertThat("Reversed Complemented sequence not as expected", s, org.hamcrest.CoreMatchers.is("GCAT"));
    }

    @org.junit.Test
    public void complement() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        java.lang.String s = new org.biojava.nbio.core.sequence.views.ComplementSequenceView<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(getSeq()).getSequenceAsString();
        org.junit.Assert.assertThat("Complemented sequence not as expected", s, org.hamcrest.CoreMatchers.is("TACG"));
    }

    @org.junit.Test
    public void reverse() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        org.biojava.nbio.core.sequence.template.SequenceView<org.biojava.nbio.core.sequence.compound.NucleotideCompound> r = new org.biojava.nbio.core.sequence.views.ReversedSequenceView<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(getSeq());
        org.junit.Assert.assertThat("Reversed sequence not as expected", r.getSequenceAsString(), org.hamcrest.CoreMatchers.is("CGTA"));
        org.junit.Assert.assertThat("Base at 2 not right", r.getCompoundAt(2).toString(), org.hamcrest.CoreMatchers.is("G"));
        java.util.List<java.lang.String> actual = new java.util.ArrayList<java.lang.String>();
        java.util.List<java.lang.String> expected = java.util.Arrays.asList("C", "G", "T", "A");
        for (org.biojava.nbio.core.sequence.compound.NucleotideCompound c : r) {
            actual.add(c.toString());
        }
        org.junit.Assert.assertThat("Iterator not behaving as expected", actual, org.hamcrest.CoreMatchers.is(expected));
        org.junit.Assert.assertThat("Index of T not as expected", r.getIndexOf(set.getCompoundForString("T")), org.hamcrest.CoreMatchers.is(3));
    }

    @org.junit.Test
    public void subSequence() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        org.biojava.nbio.core.sequence.DNASequence seq = getSeq("ACGTGGC");
        org.biojava.nbio.core.sequence.template.SequenceView<org.biojava.nbio.core.sequence.compound.NucleotideCompound> subSeq = seq.getSubSequence(2, 4);
        org.junit.Assert.assertThat("Index 2 is the same as index 1 in the sub sequence", seq.getCompoundAt(2), org.hamcrest.CoreMatchers.is(subSeq.getCompoundAt(1)));
        org.junit.Assert.assertThat("Length is equal to 3", subSeq.getLength(), org.hamcrest.CoreMatchers.is(3));
        org.junit.Assert.assertThat("Index 4 is the same as index 3 in the sub sequence", seq.getCompoundAt(4), org.hamcrest.CoreMatchers.is(subSeq.getCompoundAt(3)));
        org.junit.Assert.assertThat("Sub sequence contains only expected bases", subSeq.getSequenceAsString(), org.hamcrest.CoreMatchers.is("CGT"));
    }

    @org.junit.Test
    public void translateToRna() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        java.lang.String s = getSeq("ATGGCGGCGCTGAGCGGT").getRNASequence().getSequenceAsString();
        org.junit.Assert.assertThat("RNA as expected", s, org.hamcrest.CoreMatchers.is("AUGGCGGCGCUGAGCGGU"));
        java.lang.String s2 = getSeq("ATGGCGGCGCTGAGCGGT").getRNASequence(org.biojava.nbio.core.sequence.transcription.Frame.TWO).getSequenceAsString();
        org.junit.Assert.assertThat("RNA as expected", s2, org.hamcrest.CoreMatchers.is("UGGCGGCGCUGAGCGGU"));
    }

    @org.junit.Test
    public void respectCase() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        java.lang.String s = "ATgc";
        org.biojava.nbio.core.sequence.DNASequence dna = getSeq(s);
        org.junit.Assert.assertThat("Sequence does not remember casing", dna.getSequenceAsString(), org.hamcrest.CoreMatchers.is(s));
        org.junit.Assert.assertThat("Reversed complement sequence forgets case", dna.getInverse().getSequenceAsString(), org.hamcrest.CoreMatchers.is("gcAT"));
    }

    @org.junit.Test(expected = org.biojava.nbio.core.exceptions.CompoundNotFoundException.class)
    public void bogusSequence() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        getSeq("ATGCx");
    }

    @org.junit.Test
    public void basesEqual() {
        boolean equal = set.compoundsEqual(set.getCompoundForString("a"), set.getCompoundForString("A"));
        org.junit.Assert.assertTrue("a & A should be equal bases", equal);
    }

    @org.junit.Test
    public void basesEquivalent() {
        assertBaseEquivalence(ambiguity, "N", "A");
        assertBaseEquivalence(ambiguity, "G", "K");
        assertBaseEquivalence(ambiguity, "V", "C");
        assertBaseEquivalence(ambiguity, "W", "T");
        assertBaseEquivalence(ambiguity, "n", "A");
        assertBaseEquivalence(ambiguity, "g", "K");
        assertBaseEquivalence(ambiguity, "v", "C");
        assertBaseEquivalence(ambiguity, "w", "T");
    }

    @org.junit.Test
    public void gc() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        org.junit.Assert.assertThat("GC content not as expected", org.biojava.nbio.core.sequence.template.SequenceMixin.countGC(getSeq("GCGC")), org.hamcrest.CoreMatchers.is(4));
        org.junit.Assert.assertThat("GC content not as expected", getSeq("GCGC").getGCCount(), org.hamcrest.CoreMatchers.is(4));
        org.junit.Assert.assertThat("GC content not as expected", org.biojava.nbio.core.sequence.template.SequenceMixin.countGC(getSeq("GAAC")), org.hamcrest.CoreMatchers.is(2));
        org.junit.Assert.assertThat("GC content not as expected", org.biojava.nbio.core.sequence.template.SequenceMixin.countGC(getSeq("AATTTATATGAATTTATATGAATTTATATGAATTTATATGAATTTATATGAATTTATATGAATTTATATGAATTTATATGAATTTATATG")), org.hamcrest.CoreMatchers.is(9));
    }

    @org.junit.Test
    public void at() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        org.junit.Assert.assertThat("AT content not as expected", org.biojava.nbio.core.sequence.template.SequenceMixin.countAT(getSeq("GCGC")), org.hamcrest.CoreMatchers.is(0));
        org.junit.Assert.assertThat("AT content not as expected", org.biojava.nbio.core.sequence.template.SequenceMixin.countAT(getSeq("GCAT")), org.hamcrest.CoreMatchers.is(2));
        org.junit.Assert.assertThat("AT content not as expected", org.biojava.nbio.core.sequence.template.SequenceMixin.countAT(getSeq("atAT")), org.hamcrest.CoreMatchers.is(4));
        org.junit.Assert.assertThat("GC content not as expected", org.biojava.nbio.core.sequence.template.SequenceMixin.countAT(getSeq("AATTTATATGAATTTATATGAATTTATATGAATTTATATGAATTTATATGAATTTATATGAATTTATATGAATTTATATGAATTTATATG")), org.hamcrest.CoreMatchers.is(81));
    }

    @org.junit.Test
    public void composition() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        org.biojava.nbio.core.sequence.DNASequence seq = getSeq("ATTGGGCCCC");
        org.biojava.nbio.core.sequence.template.CompoundSet<org.biojava.nbio.core.sequence.compound.NucleotideCompound> set = seq.getCompoundSet();
        java.util.Map<org.biojava.nbio.core.sequence.compound.NucleotideCompound, java.lang.Double> distribution = org.biojava.nbio.core.sequence.template.SequenceMixin.getDistribution(seq);
        org.junit.Assert.assertThat("A distribution not as expected", distribution.get(set.getCompoundForString("A")), org.hamcrest.CoreMatchers.is(0.1));
        org.junit.Assert.assertThat("T distribution not as expected", distribution.get(set.getCompoundForString("T")), org.hamcrest.CoreMatchers.is(0.2));
        org.junit.Assert.assertThat("G distribution not as expected", distribution.get(set.getCompoundForString("G")), org.hamcrest.CoreMatchers.is(0.3));
        org.junit.Assert.assertThat("C distribution not as expected", distribution.get(set.getCompoundForString("C")), org.hamcrest.CoreMatchers.is(0.4));
    }

    @org.junit.Test
    public void twoBit() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        java.lang.String expected = "ATGCAACTGA";
        org.biojava.nbio.core.sequence.DNASequence seq = getSeq(expected);
        org.biojava.nbio.core.sequence.template.SequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound> twoBitFromSeq = new org.biojava.nbio.core.sequence.storage.TwoBitSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(seq);
        // being cheeky here & getting compound set from seq
        org.biojava.nbio.core.sequence.template.SequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound> twoBitFromString = new org.biojava.nbio.core.sequence.storage.TwoBitSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(expected, seq.getCompoundSet());
        org.junit.Assert.assertThat("TwoBit from Sequence not as expected", twoBitFromSeq.getSequenceAsString(), org.hamcrest.CoreMatchers.is(expected));
        org.junit.Assert.assertThat("TwoBit from String not as expected", twoBitFromString.getSequenceAsString(), org.hamcrest.CoreMatchers.is(expected));
    }

    @org.junit.Test
    public void fourBit() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        java.lang.String expected = "ATGCAACTGA";
        org.biojava.nbio.core.sequence.DNASequence seq = getSeq(expected);
        org.biojava.nbio.core.sequence.template.SequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound> bitFromSeq = new org.biojava.nbio.core.sequence.storage.FourBitSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(seq);
        // being cheeky here & getting compound set from seq
        org.biojava.nbio.core.sequence.template.SequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound> bitFromString = new org.biojava.nbio.core.sequence.storage.FourBitSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(expected, seq.getCompoundSet());
        org.junit.Assert.assertThat("FourBit from Sequence not as expected", bitFromSeq.getSequenceAsString(), org.hamcrest.CoreMatchers.is(expected));
        org.junit.Assert.assertThat("FourBit from String not as expected", bitFromString.getSequenceAsString(), org.hamcrest.CoreMatchers.is(expected));
    }

    @org.junit.Test(expected = java.lang.IllegalStateException.class)
    public void badTwoBit() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        org.biojava.nbio.core.sequence.DNASequence seq = getSeq();
        new org.biojava.nbio.core.sequence.storage.TwoBitSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("ATNGC", seq.getCompoundSet());
    }

    @org.junit.Test
    public void singleCompoundSequence() {
        org.biojava.nbio.core.sequence.template.CompoundSet<org.biojava.nbio.core.sequence.compound.NucleotideCompound> cs = set;
        org.biojava.nbio.core.sequence.compound.NucleotideCompound a = cs.getCompoundForString("A");
        org.biojava.nbio.core.sequence.compound.NucleotideCompound n = cs.getCompoundForString("N");
        int length = 1000;
        org.biojava.nbio.core.sequence.template.ProxySequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound> sr = new org.biojava.nbio.core.sequence.storage.SingleCompoundSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(n, cs, length);
        org.biojava.nbio.core.sequence.DNASequence seq = new org.biojava.nbio.core.sequence.DNASequence(sr);
        int intCount = 0;
        int iteratorCount = 0;
        for (int i = 1; i <= (seq.getLength()); i++) {
            if (seq.getCompoundAt(i).equals(n)) {
                intCount++;
            }
        }
        for (org.biojava.nbio.core.sequence.compound.NucleotideCompound c : seq) {
            if (c.equals(n)) {
                iteratorCount++;
            }
        }
        org.junit.Assert.assertThat("All positions from getCompoundAt() report N", intCount, org.hamcrest.CoreMatchers.is(length));
        org.junit.Assert.assertThat("All positions from iterator report N", iteratorCount, org.hamcrest.CoreMatchers.is(length));
        org.junit.Assert.assertThat("Non N compound reports -ve", seq.getIndexOf(a), org.hamcrest.CoreMatchers.is((-1)));
        org.junit.Assert.assertThat("Index of N compound reports 1", seq.getIndexOf(n), org.hamcrest.CoreMatchers.is(1));
        org.junit.Assert.assertThat("Non N compound reports -ve", seq.getLastIndexOf(a), org.hamcrest.CoreMatchers.is((-1)));
        org.junit.Assert.assertThat("Last index of N compound reports length", seq.getLastIndexOf(n), org.hamcrest.CoreMatchers.is(length));
    }

    @org.junit.Test
    public void kmerNonOverlap() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        org.biojava.nbio.core.sequence.DNASequence d = new org.biojava.nbio.core.sequence.DNASequence("ATGTGCA");
        java.util.List<org.biojava.nbio.core.sequence.template.SequenceView<org.biojava.nbio.core.sequence.compound.NucleotideCompound>> l = org.biojava.nbio.core.sequence.template.SequenceMixin.nonOverlappingKmers(d, 3);
        org.junit.Assert.assertThat("Asserting we generate only 2 k-mers", l.size(), org.hamcrest.CoreMatchers.is(2));
        org.junit.Assert.assertThat("Asserting first k-mer", l.get(0).getSequenceAsString(), org.hamcrest.CoreMatchers.is("ATG"));
        org.junit.Assert.assertThat("Asserting second k-mer", l.get(1).getSequenceAsString(), org.hamcrest.CoreMatchers.is("TGC"));
    }

    @org.junit.Test
    public void kmerOverlap() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        org.biojava.nbio.core.sequence.DNASequence d = new org.biojava.nbio.core.sequence.DNASequence("ATGTT");
        java.util.List<org.biojava.nbio.core.sequence.template.SequenceView<org.biojava.nbio.core.sequence.compound.NucleotideCompound>> l = org.biojava.nbio.core.sequence.template.SequenceMixin.overlappingKmers(d, 3);
        org.junit.Assert.assertThat("Asserting we generate only 3 k-mers", l.size(), org.hamcrest.CoreMatchers.is(3));
        org.junit.Assert.assertThat("Asserting first k-mer", l.get(0).getSequenceAsString(), org.hamcrest.CoreMatchers.is("ATG"));
        org.junit.Assert.assertThat("Asserting second k-mer", l.get(1).getSequenceAsString(), org.hamcrest.CoreMatchers.is("TGT"));
        org.junit.Assert.assertThat("Asserting second k-mer", l.get(2).getSequenceAsString(), org.hamcrest.CoreMatchers.is("GTT"));
    }

    @org.junit.Test
    public void kmerOverlapExceedingSequenceLength() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        org.biojava.nbio.core.sequence.DNASequence d = new org.biojava.nbio.core.sequence.DNASequence("ATGTT");
        java.util.List<org.biojava.nbio.core.sequence.template.SequenceView<org.biojava.nbio.core.sequence.compound.NucleotideCompound>> l = org.biojava.nbio.core.sequence.template.SequenceMixin.overlappingKmers(d, 2);
        org.junit.Assert.assertThat("Asserting we generate 4 k-mers", l.size(), org.hamcrest.CoreMatchers.is(4));
        org.junit.Assert.assertThat("Asserting first k-mer", l.get(0).getSequenceAsString(), org.hamcrest.CoreMatchers.is("AT"));
        org.junit.Assert.assertThat("Asserting second k-mer", l.get(2).getSequenceAsString(), org.hamcrest.CoreMatchers.is("GT"));
        org.junit.Assert.assertThat("Asserting second k-mer", l.get(3).getSequenceAsString(), org.hamcrest.CoreMatchers.is("TT"));
    }

    @org.junit.Test
    public void sequenceEquality() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        org.biojava.nbio.core.sequence.DNASequence d = getSeq("ATGC");
        org.junit.Assert.assertTrue("Asserting sequences are identical", org.biojava.nbio.core.sequence.template.SequenceMixin.sequenceEquality(d, d));
        org.junit.Assert.assertFalse("Sequence identical but case different", org.biojava.nbio.core.sequence.template.SequenceMixin.sequenceEquality(d, getSeq("ATGc")));
        org.junit.Assert.assertTrue("Asserting sequences are identical ignoring case", org.biojava.nbio.core.sequence.template.SequenceMixin.sequenceEqualityIgnoreCase(d, d));
        org.junit.Assert.assertTrue("Asserting sequences are identical ignoring case & case different", org.biojava.nbio.core.sequence.template.SequenceMixin.sequenceEqualityIgnoreCase(d, getSeq("aTgC")));
        org.junit.Assert.assertFalse("Sequence lengths differ", org.biojava.nbio.core.sequence.template.SequenceMixin.sequenceEquality(d, getSeq("ATG")));
        org.biojava.nbio.core.sequence.DNASequence bsr = new org.biojava.nbio.core.sequence.DNASequence(new org.biojava.nbio.core.sequence.storage.TwoBitSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("ATGC", org.biojava.nbio.core.sequence.compound.DNACompoundSet.getDNACompoundSet()));
        org.biojava.nbio.core.sequence.DNASequence bsrCI = new org.biojava.nbio.core.sequence.DNASequence(new org.biojava.nbio.core.sequence.storage.TwoBitSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("ATGc", org.biojava.nbio.core.sequence.compound.DNACompoundSet.getDNACompoundSet()));
        org.junit.Assert.assertTrue("Asserting sequences are identical; backing stores differ", org.biojava.nbio.core.sequence.template.SequenceMixin.sequenceEquality(d, bsr));
        org.junit.Assert.assertTrue("Asserting sequences are identical ignoring case; backing stores differ", org.biojava.nbio.core.sequence.template.SequenceMixin.sequenceEqualityIgnoreCase(d, bsrCI));
    }

    // @Test
    // public void randomTwoBit() throws Exception {
    // int[] ar = new int[1000000];
    // Random r = new Random();
    // for(int i = 0; i < ar.length; i++) {
    // ar[i] = r.nextInt();
    // }
    // 
    // System.out.println(Runtime.getRuntime().freeMemory());
    // System.out.println(Runtime.getRuntime().totalMemory());
    // TwoBitArrayWorker<NucleotideCompound> worker =
    // new TwoBitArrayWorker<NucleotideCompound>(getSeq().getCompoundSet(), ar);
    // SequenceProxyLoader<NucleotideCompound> sbs =
    // new BitSequenceReader<NucleotideCompound>(worker, new AccessionID("barf"));
    // 
    // System.out.println(sbs.getLength());
    // 
    // System.out.println(Runtime.getRuntime().freeMemory());
    // System.out.println(Runtime.getRuntime().totalMemory());
    // 
    // List<NucleotideCompound> c = sbs.getAsList();
    // 
    // System.out.println(Runtime.getRuntime().freeMemory());
    // System.out.println(Runtime.getRuntime().totalMemory());
    // 
    // //    OutputStream os = new BufferedOutputStream(new FileOutputStream(new File("/tmp/random.fasta")));
    // //
    // //    List<DNASequence> seqs = Arrays.asList(new DNASequence(sbs, sbs.getCompoundSet()));
    // //    seqs.get(0).setAccession(sbs.getAccession());
    // //    FastaHeaderFormatInterface<DNASequence, NucleotideCompound> headerFormat =
    // //      new GenericFastaHeaderFormat<DNASequence, NucleotideCompound>();
    // //
    // //    FastaWriter<DNASequence, NucleotideCompound> writer =
    // //      new FastaWriter<DNASequence, NucleotideCompound>(os, seqs, headerFormat);
    // //
    // //    writer.process();
    // //
    // //    IOUtils.close(os);
    // }
    private org.biojava.nbio.core.sequence.DNASequence getSeq() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        return getSeq(null);
    }

    private org.biojava.nbio.core.sequence.DNASequence getSeq(final java.lang.String seq) throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        java.lang.String target = (seq == null) ? "ATGC" : seq;
        return new org.biojava.nbio.core.sequence.DNASequence(target);
    }

    private void assertBaseEquivalence(org.biojava.nbio.core.sequence.template.CompoundSet<org.biojava.nbio.core.sequence.compound.NucleotideCompound> compoundSet, java.lang.String one, java.lang.String two) {
        boolean equal = compoundSet.compoundsEquivalent(compoundSet.getCompoundForString(one), compoundSet.getCompoundForString(two));
        org.junit.Assert.assertTrue((((one + " & ") + two) + " should be equivalent bases"), equal);
    }
}

