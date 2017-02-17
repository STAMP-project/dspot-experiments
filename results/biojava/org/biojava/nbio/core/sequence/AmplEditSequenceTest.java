

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
    }

    @org.junit.Test
    public void insert() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        org.biojava.nbio.core.sequence.DNASequence seq = new org.biojava.nbio.core.sequence.DNASequence("ACGT");
        assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 1).edit(seq), "TTACGT");
        assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 2, 3).edit(seq), "ACTTGT");
        assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 3, 4).edit(seq), "ACGTTT");
        assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("A", 4).edit(seq), "ACGTA");
        assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("atgga", 3, 4).edit(new org.biojava.nbio.core.sequence.DNASequence("gataca")), "gatatggaaca");
    }

    private void assertSeq(org.biojava.nbio.core.sequence.template.Sequence<? extends org.biojava.nbio.core.sequence.template.Compound> seq, java.lang.String expected) {
        org.junit.Assert.assertEquals(("Asserting sequence " + expected), expected, seq.getSequenceAsString());
    }

    @org.junit.Test(timeout = 10000)
    public void badSubstitute_add1_failAssert0() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        try {
            new org.biojava.nbio.core.sequence.edits.Edit.Substitute<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("AAAA", 4).edit(new org.biojava.nbio.core.sequence.DNASequence("ACGT"));
            new org.biojava.nbio.core.sequence.edits.Edit.Substitute<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("AAAA", 4).edit(new org.biojava.nbio.core.sequence.DNASequence("ACGT"));
            org.junit.Assert.fail("badSubstitute_add1 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test
    public void delete_literalMutation14_failAssert10_literalMutation288_failAssert0() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        try {
            try {
                org.biojava.nbio.core.sequence.DNASequence seq = new org.biojava.nbio.core.sequence.DNASequence("AoGT");
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Delete<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(1).edit(seq), "CGT");
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Delete<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(8).edit(seq), "ACG");
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Delete<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(2, 3).edit(seq), "AT");
                org.junit.Assert.fail("delete_literalMutation14 should have thrown IndexOutOfBoundsException");
            } catch (java.lang.IndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("delete_literalMutation14_failAssert10_literalMutation288 should have thrown CompoundNotFoundException");
        } catch (org.biojava.nbio.core.exceptions eee) {
        }
    }

    @org.junit.Test
    public void delete_literalMutation14_failAssert10_literalMutation288_failAssert0_literalMutation622_failAssert0() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        try {
            try {
                try {
                    org.biojava.nbio.core.sequence.DNASequence seq = new org.biojava.nbio.core.sequence.DNASequence("");
                    assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Delete<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(1).edit(seq), "CGT");
                    assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Delete<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(8).edit(seq), "ACG");
                    assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Delete<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(2, 3).edit(seq), "AT");
                    org.junit.Assert.fail("delete_literalMutation14 should have thrown IndexOutOfBoundsException");
                } catch (java.lang.IndexOutOfBoundsException eee) {
                }
                org.junit.Assert.fail("delete_literalMutation14_failAssert10_literalMutation288 should have thrown CompoundNotFoundException");
            } catch (org.biojava.nbio.core.exceptions eee) {
            }
            org.junit.Assert.fail("delete_literalMutation14_failAssert10_literalMutation288_failAssert0_literalMutation622 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    @org.junit.Test
    public void delete_literalMutation4_failAssert0() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        try {
            org.biojava.nbio.core.sequence.DNASequence seq = new org.biojava.nbio.core.sequence.DNASequence("");
            assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Delete<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(1).edit(seq), "CGT");
            assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Delete<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(4).edit(seq), "ACG");
            assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Delete<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(2, 3).edit(seq), "AT");
            org.junit.Assert.fail("delete_literalMutation4 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    @org.junit.Test
    public void delete_literalMutation4_failAssert0_literalMutation35_failAssert1() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        try {
            try {
                org.biojava.nbio.core.sequence.DNASequence seq = new org.biojava.nbio.core.sequence.DNASequence("");
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Delete<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(0).edit(seq), "CGT");
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Delete<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(4).edit(seq), "ACG");
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Delete<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(2, 3).edit(seq), "AT");
                org.junit.Assert.fail("delete_literalMutation4 should have thrown IllegalStateException");
            } catch (java.lang.IllegalStateException eee) {
            }
            org.junit.Assert.fail("delete_literalMutation4_failAssert0_literalMutation35 should have thrown NegativeArraySizeException");
        } catch (java.lang.NegativeArraySizeException eee) {
        }
    }

    @org.junit.Test
    public void delete_literalMutation4_failAssert0_literalMutation35_failAssert1_literalMutation651_failAssert5() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        try {
            try {
                try {
                    org.biojava.nbio.core.sequence.DNASequence seq = new org.biojava.nbio.core.sequence.DNASequence("");
                    assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Delete<org.biojava.nbio.core.sequence.compound.NucleotideCompound>((-1)).edit(seq), "CGT");
                    assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Delete<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(4).edit(seq), "ACG");
                    assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Delete<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(2, 3).edit(seq), "AT");
                    org.junit.Assert.fail("delete_literalMutation4 should have thrown IllegalStateException");
                } catch (java.lang.IllegalStateException eee) {
                }
                org.junit.Assert.fail("delete_literalMutation4_failAssert0_literalMutation35 should have thrown NegativeArraySizeException");
            } catch (java.lang.NegativeArraySizeException eee) {
            }
            org.junit.Assert.fail("delete_literalMutation4_failAssert0_literalMutation35_failAssert1_literalMutation651 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.sequence.EditSequenceTest#insert */
    @org.junit.Test
    public void insert_literalMutation13_failAssert7() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.biojava.nbio.core.sequence.DNASequence seq = new org.biojava.nbio.core.sequence.DNASequence("ACGT");
            assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 2).edit(seq), "TTACGT");
            assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 2, 3).edit(seq), "ACTTGT");
            assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 3, 4).edit(seq), "ACGTTT");
            assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("A", 4).edit(seq), "ACGTA");
            // Original BioJava example
            assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("atgga", 3, 4).edit(new org.biojava.nbio.core.sequence.DNASequence("gataca")), "gatatggaaca");
            org.junit.Assert.fail("insert_literalMutation13 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.sequence.EditSequenceTest#insert */
    @org.junit.Test
    public void insert_literalMutation14_failAssert8_literalMutation599_failAssert29() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.biojava.nbio.core.sequence.DNASequence seq = new org.biojava.nbio.core.sequence.DNASequence("AwGT");
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 0).edit(seq), "TTACGT");
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 2, 3).edit(seq), "ACTTGT");
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 3, 4).edit(seq), "ACGTTT");
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("A", 4).edit(seq), "ACGTA");
                // Original BioJava example
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("atgga", 3, 4).edit(new org.biojava.nbio.core.sequence.DNASequence("gataca")), "gatatggaaca");
                org.junit.Assert.fail("insert_literalMutation14 should have thrown IllegalStateException");
            } catch (java.lang.IllegalStateException eee) {
            }
            org.junit.Assert.fail("insert_literalMutation14_failAssert8_literalMutation599 should have thrown CompoundNotFoundException");
        } catch (org.biojava.nbio.core.exceptions.CompoundNotFoundException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.sequence.EditSequenceTest#insert */
    @org.junit.Test
    public void insert_literalMutation25_failAssert19() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.biojava.nbio.core.sequence.DNASequence seq = new org.biojava.nbio.core.sequence.DNASequence("ACGT");
            assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 1).edit(seq), "TTACGT");
            assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 2, 0).edit(seq), "ACTTGT");
            assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 3, 4).edit(seq), "ACGTTT");
            assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("A", 4).edit(seq), "ACGTA");
            // Original BioJava example
            assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("atgga", 3, 4).edit(new org.biojava.nbio.core.sequence.DNASequence("gataca")), "gatatggaaca");
            org.junit.Assert.fail("insert_literalMutation25 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.sequence.EditSequenceTest#insert */
    @org.junit.Test
    public void insert_literalMutation27_failAssert21() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.biojava.nbio.core.sequence.DNASequence seq = new org.biojava.nbio.core.sequence.DNASequence("ACGT");
            assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 1).edit(seq), "TTACGT");
            assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 2, 6).edit(seq), "ACTTGT");
            assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 3, 4).edit(seq), "ACGTTT");
            assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("A", 4).edit(seq), "ACGTA");
            // Original BioJava example
            assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("atgga", 3, 4).edit(new org.biojava.nbio.core.sequence.DNASequence("gataca")), "gatatggaaca");
            org.junit.Assert.fail("insert_literalMutation27 should have thrown NoSuchElementException");
        } catch (java.util.NoSuchElementException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.sequence.EditSequenceTest#insert */
    @org.junit.Test
    public void insert_literalMutation34_failAssert28_literalMutation1944_failAssert15() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.biojava.nbio.core.sequence.DNASequence seq = new org.biojava.nbio.core.sequence.DNASequence("ACGT");
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 1).edit(seq), "TTACGT");
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 2, 3).edit(seq), "ACTTGT");
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 6, 0).edit(seq), "ACGTTT");
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("A", 4).edit(seq), "ACGTA");
                // Original BioJava example
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("atgga", 3, 4).edit(new org.biojava.nbio.core.sequence.DNASequence("gataca")), "gatatggaaca");
                org.junit.Assert.fail("insert_literalMutation34 should have thrown IndexOutOfBoundsException");
            } catch (java.lang.IndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("insert_literalMutation34_failAssert28_literalMutation1944 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.sequence.EditSequenceTest#insert */
    @org.junit.Test
    public void insert_literalMutation34_failAssert28_literalMutation1944_failAssert15_literalMutation5082_failAssert13() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // AssertGenerator generate try/catch block with fail statement
                try {
                    org.biojava.nbio.core.sequence.DNASequence seq = new org.biojava.nbio.core.sequence.DNASequence("ACGT");
                    assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 1).edit(seq), "TTACGT");
                    assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 2, 6).edit(seq), "ACTTGT");
                    assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 6, 0).edit(seq), "ACGTTT");
                    assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("A", 4).edit(seq), "ACGTA");
                    // Original BioJava example
                    assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("atgga", 3, 4).edit(new org.biojava.nbio.core.sequence.DNASequence("gataca")), "gatatggaaca");
                    org.junit.Assert.fail("insert_literalMutation34 should have thrown IndexOutOfBoundsException");
                } catch (java.lang.IndexOutOfBoundsException eee) {
                }
                org.junit.Assert.fail("insert_literalMutation34_failAssert28_literalMutation1944 should have thrown IllegalArgumentException");
            } catch (java.lang.IllegalArgumentException eee) {
            }
            org.junit.Assert.fail("insert_literalMutation34_failAssert28_literalMutation1944_failAssert15_literalMutation5082 should have thrown NoSuchElementException");
        } catch (java.util.NoSuchElementException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.sequence.EditSequenceTest#insert */
    @org.junit.Test
    public void insert_literalMutation58_failAssert52_literalMutation3495_failAssert28() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.biojava.nbio.core.sequence.DNASequence seq = new org.biojava.nbio.core.sequence.DNASequence("ACGT");
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 0).edit(seq), "TTACGT");
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 2, 3).edit(seq), "ACTTGT");
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 3, 4).edit(seq), "ACGTTT");
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("A", 4).edit(seq), "ACGTA");
                // Original BioJava example
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("atgga", 3, 8).edit(new org.biojava.nbio.core.sequence.DNASequence("gataca")), "gatatggaaca");
                org.junit.Assert.fail("insert_literalMutation58 should have thrown NoSuchElementException");
            } catch (java.util.NoSuchElementException eee) {
            }
            org.junit.Assert.fail("insert_literalMutation58_failAssert52_literalMutation3495 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.sequence.EditSequenceTest#insert */
    @org.junit.Test
    public void insert_literalMutation58_failAssert52_literalMutation3531_failAssert2_literalMutation4226_failAssert1() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // AssertGenerator generate try/catch block with fail statement
                try {
                    org.biojava.nbio.core.sequence.DNASequence seq = new org.biojava.nbio.core.sequence.DNASequence("ACGT");
                    assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 1).edit(seq), "TTACGT");
                    assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 2, 0).edit(seq), "ACTTGT");
                    assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 3, 4).edit(seq), "ACGTTT");
                    assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("A", 4).edit(seq), "ACGTA");
                    // Original BioJava example
                    assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("at9gga", 3, 8).edit(new org.biojava.nbio.core.sequence.DNASequence("gataca")), "gatatggaaca");
                    org.junit.Assert.fail("insert_literalMutation58 should have thrown NoSuchElementException");
                } catch (java.util.NoSuchElementException eee) {
                }
                org.junit.Assert.fail("insert_literalMutation58_failAssert52_literalMutation3531 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("insert_literalMutation58_failAssert52_literalMutation3531_failAssert2_literalMutation4226 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.sequence.EditSequenceTest#insert */
    @org.junit.Test
    public void insert_literalMutation63_failAssert57_literalMutation3840_failAssert5_literalMutation4412_failAssert24() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // AssertGenerator generate try/catch block with fail statement
                try {
                    org.biojava.nbio.core.sequence.DNASequence seq = new org.biojava.nbio.core.sequence.DNASequence("ACGT");
                    assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 0).edit(seq), "TTACGT");
                    assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 2, 3).edit(seq), "ACTTGT");
                    assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 6, 4).edit(seq), "ACGTTT");
                    assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("A", 4).edit(seq), "ACGTA");
                    // Original BioJava example
                    assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("atgga", 3, 4).edit(new org.biojava.nbio.core.sequence.DNASequence("g5ataca")), "gatatggaaca");
                    org.junit.Assert.fail("insert_literalMutation63 should have thrown CompoundNotFoundException");
                } catch (org.biojava.nbio.core.exceptions.CompoundNotFoundException eee) {
                }
                org.junit.Assert.fail("insert_literalMutation63_failAssert57_literalMutation3840 should have thrown IndexOutOfBoundsException");
            } catch (java.lang.IndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("insert_literalMutation63_failAssert57_literalMutation3840_failAssert5_literalMutation4412 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.sequence.EditSequenceTest#insert */
    @org.junit.Test
    public void insert_literalMutation66_failAssert60_literalMutation4040_failAssert10() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.biojava.nbio.core.sequence.DNASequence seq = new org.biojava.nbio.core.sequence.DNASequence("ACGT");
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 1).edit(seq), "TTACGT");
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 2, 3).edit(seq), "ACTTGT");
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("TT", 3, 8).edit(seq), "ACGTTT");
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("A", 4).edit(seq), "ACGTA");
                // Original BioJava example
                assertSeq(new org.biojava.nbio.core.sequence.edits.Edit.Insert<org.biojava.nbio.core.sequence.compound.NucleotideCompound>("atgga", 3, 4).edit(new org.biojava.nbio.core.sequence.DNASequence("SgpbL[")), "gatatggaaca");
                org.junit.Assert.fail("insert_literalMutation66 should have thrown CompoundNotFoundException");
            } catch (org.biojava.nbio.core.exceptions.CompoundNotFoundException eee) {
            }
            org.junit.Assert.fail("insert_literalMutation66_failAssert60_literalMutation4040 should have thrown NoSuchElementException");
        } catch (java.util.NoSuchElementException eee) {
        }
    }
}

