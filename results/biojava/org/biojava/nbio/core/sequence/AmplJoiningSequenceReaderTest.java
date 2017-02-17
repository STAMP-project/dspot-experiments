

package org.biojava.nbio.core.sequence;


public class AmplJoiningSequenceReaderTest {
    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test
    public void canScan() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound> seq = new org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(new org.biojava.nbio.core.sequence.DNASequence("AAAA"), new org.biojava.nbio.core.sequence.DNASequence("GGG"), new org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(new org.biojava.nbio.core.sequence.DNASequence("A"), new org.biojava.nbio.core.sequence.DNASequence("C")), new org.biojava.nbio.core.sequence.DNASequence("TT"), new org.biojava.nbio.core.sequence.DNASequence("C"));
        java.lang.String expected = "AAAAGGGACTTC";
        java.lang.StringBuilder builderByIndex = new java.lang.StringBuilder();
        for (int i = 1; i <= (seq.getLength()); i++) {
            builderByIndex.append(seq.getCompoundAt(i));
        }
        java.lang.StringBuilder builderByIterator = org.biojava.nbio.core.sequence.template.SequenceMixin.toStringBuilder(seq);
        org.junit.Assert.assertEquals("Index builder", expected, builderByIndex.toString());
        org.junit.Assert.assertEquals("Iterator builder", expected, builderByIterator.toString());
    }

    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test
    public void empty() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound> seq = new org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(new org.biojava.nbio.core.sequence.DNASequence(""), new org.biojava.nbio.core.sequence.DNASequence(""), new org.biojava.nbio.core.sequence.DNASequence("A"), new org.biojava.nbio.core.sequence.DNASequence(""));
        org.junit.Assert.assertEquals("Testing empty sequences", "A", seq.getSequenceAsString());
    }

    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test
    public void canScan_literalMutation26_failAssert25() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        try {
            org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound> seq = new org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(new org.biojava.nbio.core.sequence.DNASequence("AAAA"), new org.biojava.nbio.core.sequence.DNASequence("GGG"), new org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(new org.biojava.nbio.core.sequence.DNASequence("A"), new org.biojava.nbio.core.sequence.DNASequence("C")), new org.biojava.nbio.core.sequence.DNASequence("TT"), new org.biojava.nbio.core.sequence.DNASequence("C"));
            java.lang.String expected = "AAAAGGGACTTC";
            java.lang.StringBuilder builderByIndex = new java.lang.StringBuilder();
            for (int i = 0; i <= (seq.getLength()); i++) {
                builderByIndex.append(seq.getCompoundAt(i));
            }
            java.lang.StringBuilder builderByIterator = org.biojava.nbio.core.sequence.template.SequenceMixin.toStringBuilder(seq);
            java.lang.Object o_23_0 = builderByIndex.toString();
            java.lang.Object o_25_0 = builderByIterator.toString();
            org.junit.Assert.fail("canScan_literalMutation26 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test(timeout = 10000)
    public void canScan_cf40_failAssert28_add845() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        try {
            org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound> seq = new org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(new org.biojava.nbio.core.sequence.DNASequence("AAAA"), new org.biojava.nbio.core.sequence.DNASequence("GGG"), new org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(new org.biojava.nbio.core.sequence.DNASequence("A"), new org.biojava.nbio.core.sequence.DNASequence("C")), new org.biojava.nbio.core.sequence.DNASequence("TT"), new org.biojava.nbio.core.sequence.DNASequence("C"));
            java.lang.String expected = "AAAAGGGACTTC";
            java.lang.StringBuilder builderByIndex = new java.lang.StringBuilder();
            for (int i = 1; i <= (seq.getLength()); i++) {
                builderByIndex.append(seq.getCompoundAt(i));
            }
            java.lang.StringBuilder builderByIterator = org.biojava.nbio.core.sequence.template.SequenceMixin.toStringBuilder(seq);
            java.lang.Object o_22_0 = builderByIndex.toString();
            org.junit.Assert.assertEquals(o_22_0, "AAAAGGGACTTC");
            int int_vc_0 = 1;
            org.junit.Assert.assertEquals(int_vc_0, 1);
            org.biojava.nbio.core.sequence.template.Sequence vc_0 = ((org.biojava.nbio.core.sequence.template.Sequence) (null));
            org.junit.Assert.assertNull(vc_0);
            vc_0.getCompoundAt(int_vc_0);
            vc_0.getCompoundAt(int_vc_0);
            java.lang.Object o_30_0 = builderByIterator.toString();
            org.junit.Assert.fail("canScan_cf40 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test(timeout = 10000)
    public void canScan_cf59_failAssert31_literalMutation966_failAssert20() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        try {
            try {
                org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound> seq = new org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(new org.biojava.nbio.core.sequence.DNASequence("AAAA"), new org.biojava.nbio.core.sequence.DNASequence("GGG"), new org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(new org.biojava.nbio.core.sequence.DNASequence("A"), new org.biojava.nbio.core.sequence.DNASequence("C")), new org.biojava.nbio.core.sequence.DNASequence("TT"), new org.biojava.nbio.core.sequence.DNASequence("C"));
                java.lang.String expected = "AAAAGGGACTTC";
                java.lang.StringBuilder builderByIndex = new java.lang.StringBuilder();
                for (int i = 0; i <= (seq.getLength()); i++) {
                    builderByIndex.append(seq.getCompoundAt(i));
                }
                java.lang.StringBuilder builderByIterator = org.biojava.nbio.core.sequence.template.SequenceMixin.toStringBuilder(seq);
                java.lang.Object o_22_0 = builderByIndex.toString();
                org.biojava.nbio.core.sequence.template.Sequence vc_17 = ((org.biojava.nbio.core.sequence.template.Sequence) (null));
                vc_17.getSequenceAsString();
                java.lang.Object o_28_0 = builderByIterator.toString();
                org.junit.Assert.fail("canScan_cf59 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("canScan_cf59_failAssert31_literalMutation966 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test
    public void canScan_literalMutation27_failAssert26_literalMutation807_literalMutation1359() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        try {
            org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound> seq = new org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(new org.biojava.nbio.core.sequence.DNASequence("AAAA"), new org.biojava.nbio.core.sequence.DNASequence("GGG"), new org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(new org.biojava.nbio.core.sequence.DNASequence("A"), new org.biojava.nbio.core.sequence.DNASequence("C")), new org.biojava.nbio.core.sequence.DNASequence("TT"), new org.biojava.nbio.core.sequence.DNASequence("C"));
            java.lang.String expected = "";
            org.junit.Assert.assertEquals(expected, "");
            org.junit.Assert.assertEquals(expected, "");
            java.lang.StringBuilder builderByIndex = new java.lang.StringBuilder();
            for (int i = 0; i <= (seq.getLength()); i++) {
                builderByIndex.append(seq.getCompoundAt(i));
            }
            java.lang.StringBuilder builderByIterator = org.biojava.nbio.core.sequence.template.SequenceMixin.toStringBuilder(seq);
            java.lang.Object o_23_0 = builderByIndex.toString();
            java.lang.Object o_25_0 = builderByIterator.toString();
            org.junit.Assert.fail("canScan_literalMutation27 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.sequence.JoiningSequenceReaderTest#empty */
    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test(timeout = 10000)
    public void empty_cf16_failAssert10() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound> seq = new org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(new org.biojava.nbio.core.sequence.DNASequence(""), new org.biojava.nbio.core.sequence.DNASequence(""), new org.biojava.nbio.core.sequence.DNASequence("A"), new org.biojava.nbio.core.sequence.DNASequence(""));
            // StatementAdderOnAssert create random local variable
            int vc_2 = -1814531737;
            // StatementAdderOnAssert create null value
            org.biojava.nbio.core.sequence.template.Sequence vc_0 = (org.biojava.nbio.core.sequence.template.Sequence)null;
            // StatementAdderMethod cloned existing statement
            vc_0.getCompoundAt(vc_2);
            // MethodAssertGenerator build local variable
            Object o_13_0 = seq.getSequenceAsString();
            org.junit.Assert.fail("empty_cf16 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.sequence.JoiningSequenceReaderTest#empty */
    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test(timeout = 10000)
    public void empty_cf34_failAssert13_literalMutation170_failAssert41() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound> seq = new org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(new org.biojava.nbio.core.sequence.DNASequence(""), new org.biojava.nbio.core.sequence.DNASequence(""), new org.biojava.nbio.core.sequence.DNASequence(""), new org.biojava.nbio.core.sequence.DNASequence(""));
                // StatementAdderOnAssert create null value
                org.biojava.nbio.core.sequence.template.Sequence vc_19 = (org.biojava.nbio.core.sequence.template.Sequence)null;
                // StatementAdderMethod cloned existing statement
                vc_19.getAsList();
                // MethodAssertGenerator build local variable
                Object o_11_0 = seq.getSequenceAsString();
                org.junit.Assert.fail("empty_cf34 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("empty_cf34_failAssert13_literalMutation170 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.sequence.JoiningSequenceReaderTest#empty */
    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test(timeout = 10000)
    public void empty_cf36_failAssert14_literalMutation180_failAssert50_literalMutation859_failAssert0() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // AssertGenerator generate try/catch block with fail statement
                try {
                    org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound> seq = new org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(new org.biojava.nbio.core.sequence.DNASequence(""), new org.biojava.nbio.core.sequence.DNASequence(""), new org.biojava.nbio.core.sequence.DNASequence(""), new org.biojava.nbio.core.sequence.DNASequence(""));
                    // StatementAdderOnAssert create null value
                    org.biojava.nbio.core.sequence.template.Sequence vc_21 = (org.biojava.nbio.core.sequence.template.Sequence)null;
                    // StatementAdderMethod cloned existing statement
                    vc_21.getCompoundSet();
                    // MethodAssertGenerator build local variable
                    Object o_11_0 = seq.getSequenceAsString();
                    org.junit.Assert.fail("empty_cf36 should have thrown NullPointerException");
                } catch (java.lang.NullPointerException eee) {
                }
                org.junit.Assert.fail("empty_cf36_failAssert14_literalMutation180 should have thrown CompoundNotFoundException");
            } catch (org.biojava.nbio.core.exceptions.CompoundNotFoundException eee) {
            }
            org.junit.Assert.fail("empty_cf36_failAssert14_literalMutation180_failAssert50_literalMutation859 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of org.biojava.nbio.core.sequence.JoiningSequenceReaderTest#empty */
    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test
    public void empty_literalMutation5_failAssert4() throws org.biojava.nbio.core.exceptions.CompoundNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound> seq = new org.biojava.nbio.core.sequence.storage.JoiningSequenceReader<org.biojava.nbio.core.sequence.compound.NucleotideCompound>(new org.biojava.nbio.core.sequence.DNASequence(""), new org.biojava.nbio.core.sequence.DNASequence(""), new org.biojava.nbio.core.sequence.DNASequence(""), new org.biojava.nbio.core.sequence.DNASequence(""));
            // MethodAssertGenerator build local variable
            Object o_7_0 = seq.getSequenceAsString();
            org.junit.Assert.fail("empty_literalMutation5 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }
}

