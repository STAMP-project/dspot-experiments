/**
 * Copyright (C) 2011 Clearspring Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.clearspring.analytics.stream;


public class TestStreamSummaryAmpl {
    private static final int NUM_ITERATIONS = 100000;

    @org.junit.Test
    public void testStreamSummary() {
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
        java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "A" , "A" };
        for (java.lang.String i : stream) {
            vs.offer(i);
            /* for(String s : vs.poll(3))
            System.out.print(s+" ");
             */
            java.lang.System.out.println(vs);
        }
    }

    @org.junit.Test
    public void testTopK() {
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
        java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "C" , "A" , "A" };
        for (java.lang.String i : stream) {
            vs.offer(i);
        }
        java.util.List<com.clearspring.analytics.stream.Counter<java.lang.String>> topK = vs.topK(3);
        for (com.clearspring.analytics.stream.Counter<java.lang.String> c : topK) {
            org.junit.Assert.assertTrue(java.util.Arrays.asList("A", "C", "X").contains(c.getItem()));
        }
    }

    @org.junit.Test
    public void testTopKWithIncrement() {
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
        java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "C" , "A" , "A" };
        for (java.lang.String i : stream) {
            vs.offer(i, 10);
        }
        java.util.List<com.clearspring.analytics.stream.Counter<java.lang.String>> topK = vs.topK(3);
        for (com.clearspring.analytics.stream.Counter<java.lang.String> c : topK) {
            org.junit.Assert.assertTrue(java.util.Arrays.asList("A", "C", "X").contains(c.getItem()));
        }
    }

    @org.junit.Test
    public void testGeometricDistribution() {
        com.clearspring.analytics.stream.StreamSummary<java.lang.Integer> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.Integer>(10);
        cern.jet.random.engine.RandomEngine re = cern.jet.random.engine.RandomEngine.makeDefault();
        for (int i = 0; i < (com.clearspring.analytics.stream.TestStreamSummaryAmpl.NUM_ITERATIONS); i++) {
            int z = cern.jet.random.Distributions.nextGeometric(0.25, re);
            vs.offer(z);
        }
        java.util.List<java.lang.Integer> top = vs.peek(5);
        java.lang.System.out.println("Geometric:");
        for (java.lang.Integer e : top) {
            java.lang.System.out.println(e);
        }
        int tippyTop = top.get(0);
        org.junit.Assert.assertEquals(0, tippyTop);
        java.lang.System.out.println(vs);
    }

    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test
    public void testCounterSerialization() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
        java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "C" , "A" , "A" };
        for (java.lang.String i : stream) {
            vs.offer(i);
        }
        java.util.List<com.clearspring.analytics.stream.Counter<java.lang.String>> topK = vs.topK(3);
        for (com.clearspring.analytics.stream.Counter<java.lang.String> c : topK) {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutput oo = new java.io.ObjectOutputStream(baos);
            oo.writeObject(c);
            oo.close();
            java.io.ObjectInput oi = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
            com.clearspring.analytics.stream.Counter<java.lang.String> clone = ((com.clearspring.analytics.stream.Counter<java.lang.String>) (oi.readObject()));
            org.junit.Assert.assertEquals(c.getCount(), clone.getCount());
            org.junit.Assert.assertEquals(c.getError(), clone.getError());
            org.junit.Assert.assertEquals(c.getItem(), clone.getItem());
        }
    }

    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test
    public void testSerialization() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
        java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "C" , "A" , "A" };
        for (java.lang.String i : stream) {
            vs.offer(i);
        }
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutput oo = new java.io.ObjectOutputStream(baos);
        oo.writeObject(vs);
        oo.close();
        java.io.ObjectInput oi = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> clone = ((com.clearspring.analytics.stream.StreamSummary<java.lang.String>) (oi.readObject()));
        org.junit.Assert.assertEquals(vs.toString(), clone.toString());
    }

    @org.junit.Test
    public void testByteSerialization() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
        java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "C" , "A" , "A" };
        for (java.lang.String i : stream) {
            vs.offer(i);
        }
        testSerialization(vs);
        // Empty
        vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(0);
        testSerialization(vs);
    }

    private void testSerialization(com.clearspring.analytics.stream.StreamSummary<?> vs) throws java.io.IOException, java.lang.ClassNotFoundException {
        byte[] bytes = vs.toBytes();
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> clone = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(bytes);
        org.junit.Assert.assertEquals(vs.toString(), clone.toString());
    }

    @org.junit.Test
    public void testTopKWithIncrementOutOfOrder() {
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs_increment = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs_single = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
        java.lang.String[] stream = new java.lang.String[]{ "A" , "B" , "C" , "D" , "A" };
        java.lang.Integer[] increments = new java.lang.Integer[]{ 15 , 20 , 25 , 30 , 1 };
        for (int i = 0; i < (stream.length); i++) {
            // AssertGenerator replace invocation
            boolean o_testTopKWithIncrementOutOfOrder__11 = vs_increment.offer(stream[i], increments[i]);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testTopKWithIncrementOutOfOrder__11);
            for (int k = 0; k < (increments[i]); k++) {
                vs_single.offer(stream[i]);
            }
        }
        java.lang.System.out.println("Insert with counts vs. single inserts:");
        java.lang.System.out.println(vs_increment);
        java.lang.System.out.println(vs_single);
        java.util.List<com.clearspring.analytics.stream.Counter<java.lang.String>> topK_increment = vs_increment.topK(3);
        java.util.List<com.clearspring.analytics.stream.Counter<java.lang.String>> topK_single = vs_single.topK(3);
        for (int i = 0; i < (topK_increment.size()); i++) {
            org.junit.Assert.assertEquals(topK_increment.get(i).getItem(), topK_single.get(i).getItem());
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testByteSerialization */
    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testByteSerialization_literalMutation6 */
    @org.junit.Test
    public void testByteSerialization_literalMutation6_literalMutation240_literalMutation6753_failAssert0() throws java.io.IOException, java.lang.ClassNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(0);
            java.lang.String[] stream = new java.lang.String[]{ "X" , ", object: " , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "C" , "A" , "A" };
            for (java.lang.String i : stream) {
                vs.offer(i);
            }
            testSerialization(vs);
            // Empty
            vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(0);
            testSerialization(vs);
            org.junit.Assert.fail("testByteSerialization_literalMutation6_literalMutation240_literalMutation6753 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testCounterSerialization */
    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test(timeout = 10000)
    public void testCounterSerialization_add9552() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
        java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "C" , "A" , "A" };
        for (java.lang.String i : stream) {
            // MethodCallAdder
            vs.offer(i);
            // AssertGenerator replace invocation
            boolean o_testCounterSerialization_add9552__9 = vs.offer(i);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testCounterSerialization_add9552__9);
        }
        java.util.List<com.clearspring.analytics.stream.Counter<java.lang.String>> topK = vs.topK(3);
        for (com.clearspring.analytics.stream.Counter<java.lang.String> c : topK) {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutput oo = new java.io.ObjectOutputStream(baos);
            oo.writeObject(c);
            oo.close();
            java.io.ObjectInput oi = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
            com.clearspring.analytics.stream.Counter<java.lang.String> clone = ((com.clearspring.analytics.stream.Counter<java.lang.String>) (oi.readObject()));
            org.junit.Assert.assertEquals(c.getCount(), clone.getCount());
            org.junit.Assert.assertEquals(c.getError(), clone.getError());
            org.junit.Assert.assertEquals(c.getItem(), clone.getItem());
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testCounterSerialization */
    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test(timeout = 10000)
    public void testCounterSerialization_cf9638() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
        java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "C" , "A" , "A" };
        for (java.lang.String i : stream) {
            vs.offer(i);
        }
        java.util.List<com.clearspring.analytics.stream.Counter<java.lang.String>> topK = vs.topK(3);
        for (com.clearspring.analytics.stream.Counter<java.lang.String> c : topK) {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutput oo = new java.io.ObjectOutputStream(baos);
            oo.writeObject(c);
            oo.close();
            java.io.ObjectInput oi = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
            com.clearspring.analytics.stream.Counter<java.lang.String> clone = ((com.clearspring.analytics.stream.Counter<java.lang.String>) (oi.readObject()));
            org.junit.Assert.assertEquals(c.getCount(), clone.getCount());
            org.junit.Assert.assertEquals(c.getError(), clone.getError());
            // AssertGenerator replace invocation
            java.lang.String o_testCounterSerialization_cf9638__31 = // StatementAdderMethod cloned existing statement
vs.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testCounterSerialization_cf9638__31, "[{5:[{A:2}]},{4:[{X:2},{C:2}]}]");
            org.junit.Assert.assertEquals(c.getItem(), clone.getItem());
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testCounterSerialization */
    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test(timeout = 10000)
    public void testCounterSerialization_cf9635() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
        java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "C" , "A" , "A" };
        for (java.lang.String i : stream) {
            vs.offer(i);
        }
        java.util.List<com.clearspring.analytics.stream.Counter<java.lang.String>> topK = vs.topK(3);
        for (com.clearspring.analytics.stream.Counter<java.lang.String> c : topK) {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutput oo = new java.io.ObjectOutputStream(baos);
            oo.writeObject(c);
            oo.close();
            java.io.ObjectInput oi = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
            com.clearspring.analytics.stream.Counter<java.lang.String> clone = ((com.clearspring.analytics.stream.Counter<java.lang.String>) (oi.readObject()));
            org.junit.Assert.assertEquals(c.getCount(), clone.getCount());
            org.junit.Assert.assertEquals(c.getError(), clone.getError());
            // AssertGenerator replace invocation
            int o_testCounterSerialization_cf9635__31 = // StatementAdderMethod cloned existing statement
vs.size();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testCounterSerialization_cf9635__31, 3);
            org.junit.Assert.assertEquals(c.getItem(), clone.getItem());
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testCounterSerialization */
    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test(timeout = 10000)
    public void testCounterSerialization_cf9632() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
        java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "C" , "A" , "A" };
        for (java.lang.String i : stream) {
            vs.offer(i);
        }
        java.util.List<com.clearspring.analytics.stream.Counter<java.lang.String>> topK = vs.topK(3);
        for (com.clearspring.analytics.stream.Counter<java.lang.String> c : topK) {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutput oo = new java.io.ObjectOutputStream(baos);
            oo.writeObject(c);
            oo.close();
            java.io.ObjectInput oi = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
            com.clearspring.analytics.stream.Counter<java.lang.String> clone = ((com.clearspring.analytics.stream.Counter<java.lang.String>) (oi.readObject()));
            org.junit.Assert.assertEquals(c.getCount(), clone.getCount());
            org.junit.Assert.assertEquals(c.getError(), clone.getError());
            // AssertGenerator replace invocation
            int o_testCounterSerialization_cf9632__31 = // StatementAdderMethod cloned existing statement
vs.getCapacity();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testCounterSerialization_cf9632__31, 3);
            org.junit.Assert.assertEquals(c.getItem(), clone.getItem());
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testCounterSerialization */
    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testCounterSerialization_cf9617 */
    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test(timeout = 10000)
    public void testCounterSerialization_cf9617_cf9905() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
        java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "C" , "A" , "A" };
        for (java.lang.String i : stream) {
            vs.offer(i);
        }
        java.util.List<com.clearspring.analytics.stream.Counter<java.lang.String>> topK = vs.topK(3);
        for (com.clearspring.analytics.stream.Counter<java.lang.String> c : topK) {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutput oo = new java.io.ObjectOutputStream(baos);
            oo.writeObject(c);
            oo.close();
            java.io.ObjectInput oi = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
            com.clearspring.analytics.stream.Counter<java.lang.String> clone = ((com.clearspring.analytics.stream.Counter<java.lang.String>) (oi.readObject()));
            org.junit.Assert.assertEquals(c.getCount(), clone.getCount());
            org.junit.Assert.assertEquals(c.getError(), clone.getError());
            // AssertGenerator replace invocation
            byte[] o_testCounterSerialization_cf9617__31 = // StatementAdderMethod cloned existing statement
vs.toBytes();
            // AssertGenerator add assertion
            byte[] array_654641299 = new byte[]{-84, -19, 0, 5, 119, 8, 0, 0, 0, 3, 0, 0, 0, 3, 115, 114, 0, 40, 99, 111, 109, 46, 99, 108, 101, 97, 114, 115, 112, 114, 105, 110, 103, 46, 97, 110, 97, 108, 121, 116, 105, 99, 115, 46, 115, 116, 114, 101, 97, 109, 46, 67, 111, 117, 110, 116, 101, 114, -7, -16, 21, -73, 39, -22, 45, -68, 12, 0, 0, 120, 112, 116, 0, 1, 88, 119, 16, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 2, 120, 115, 113, 0, 126, 0, 0, 116, 0, 1, 67, 119, 16, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 2, 120, 115, 113, 0, 126, 0, 0, 116, 0, 1, 65, 119, 16, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 2, 120};
	byte[] array_651942831 = (byte[])o_testCounterSerialization_cf9617__31;
	for(int ii = 0; ii <array_654641299.length; ii++) {
		org.junit.Assert.assertEquals(array_654641299[ii], array_651942831[ii]);
	};
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.stream.StreamSummary vc_122 = new com.clearspring.analytics.stream.StreamSummary();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.StreamSummary)vc_122).getCapacity(), 0);
            // AssertGenerator replace invocation
            int o_testCounterSerialization_cf9617_cf9905__37 = // StatementAdderMethod cloned existing statement
vc_122.getCapacity();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testCounterSerialization_cf9617_cf9905__37, 0);
            org.junit.Assert.assertEquals(c.getItem(), clone.getItem());
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testGeometricDistribution */
    @org.junit.Test(timeout = 10000)
    public void testGeometricDistribution_cf10744() {
        com.clearspring.analytics.stream.StreamSummary<java.lang.Integer> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.Integer>(10);
        cern.jet.random.engine.RandomEngine re = cern.jet.random.engine.RandomEngine.makeDefault();
        for (int i = 0; i < (com.clearspring.analytics.stream.TestStreamSummaryAmpl.NUM_ITERATIONS); i++) {
            int z = cern.jet.random.Distributions.nextGeometric(0.25, re);
            vs.offer(z);
        }
        java.util.List<java.lang.Integer> top = vs.peek(5);
        java.lang.System.out.println("Geometric:");
        for (java.lang.Integer e : top) {
            java.lang.System.out.println(e);
        }
        int tippyTop = top.get(0);
        // AssertGenerator replace invocation
        int o_testGeometricDistribution_cf10744__21 = // StatementAdderMethod cloned existing statement
vs.getCapacity();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testGeometricDistribution_cf10744__21, 10);
        org.junit.Assert.assertEquals(0, tippyTop);
        java.lang.System.out.println(vs);
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testGeometricDistribution */
    @org.junit.Test(timeout = 10000)
    public void testGeometricDistribution_cf10772_failAssert21() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.StreamSummary<java.lang.Integer> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.Integer>(10);
            cern.jet.random.engine.RandomEngine re = cern.jet.random.engine.RandomEngine.makeDefault();
            for (int i = 0; i < (com.clearspring.analytics.stream.TestStreamSummaryAmpl.NUM_ITERATIONS); i++) {
                int z = cern.jet.random.Distributions.nextGeometric(0.25, re);
                vs.offer(z);
            }
            java.util.List<java.lang.Integer> top = vs.peek(5);
            java.lang.System.out.println("Geometric:");
            for (java.lang.Integer e : top) {
                java.lang.System.out.println(e);
            }
            int tippyTop = top.get(0);
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.stream.StreamSummary vc_431 = new com.clearspring.analytics.stream.StreamSummary();
            // StatementAdderMethod cloned existing statement
            vc_431.topK(tippyTop);
            java.lang.System.out.println(vs);
            org.junit.Assert.fail("testGeometricDistribution_cf10772 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testGeometricDistribution */
    @org.junit.Test(timeout = 10000)
    public void testGeometricDistribution_cf10747() {
        com.clearspring.analytics.stream.StreamSummary<java.lang.Integer> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.Integer>(10);
        cern.jet.random.engine.RandomEngine re = cern.jet.random.engine.RandomEngine.makeDefault();
        for (int i = 0; i < (com.clearspring.analytics.stream.TestStreamSummaryAmpl.NUM_ITERATIONS); i++) {
            int z = cern.jet.random.Distributions.nextGeometric(0.25, re);
            vs.offer(z);
        }
        java.util.List<java.lang.Integer> top = vs.peek(5);
        java.lang.System.out.println("Geometric:");
        for (java.lang.Integer e : top) {
            java.lang.System.out.println(e);
        }
        int tippyTop = top.get(0);
        // AssertGenerator replace invocation
        int o_testGeometricDistribution_cf10747__21 = // StatementAdderMethod cloned existing statement
vs.size();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testGeometricDistribution_cf10747__21, 10);
        org.junit.Assert.assertEquals(0, tippyTop);
        java.lang.System.out.println(vs);
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testGeometricDistribution */
    @org.junit.Test(timeout = 10000)
    public void testGeometricDistribution_add10645() {
        com.clearspring.analytics.stream.StreamSummary<java.lang.Integer> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.Integer>(10);
        cern.jet.random.engine.RandomEngine re = cern.jet.random.engine.RandomEngine.makeDefault();
        for (int i = 0; i < (com.clearspring.analytics.stream.TestStreamSummaryAmpl.NUM_ITERATIONS); i++) {
            int z = cern.jet.random.Distributions.nextGeometric(0.25, re);
            // MethodCallAdder
            vs.offer(z);
            // AssertGenerator replace invocation
            boolean o_testGeometricDistribution_add10645__13 = vs.offer(z);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGeometricDistribution_add10645__13);
        }
        java.util.List<java.lang.Integer> top = vs.peek(5);
        java.lang.System.out.println("Geometric:");
        for (java.lang.Integer e : top) {
            java.lang.System.out.println(e);
        }
        int tippyTop = top.get(0);
        org.junit.Assert.assertEquals(0, tippyTop);
        java.lang.System.out.println(vs);
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testGeometricDistribution */
    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testGeometricDistribution_cf10747 */
    @org.junit.Test(timeout = 10000)
    public void testGeometricDistribution_cf10747_cf11291_failAssert20() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_23_1 = 10;
            com.clearspring.analytics.stream.StreamSummary<java.lang.Integer> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.Integer>(10);
            cern.jet.random.engine.RandomEngine re = cern.jet.random.engine.RandomEngine.makeDefault();
            for (int i = 0; i < (com.clearspring.analytics.stream.TestStreamSummaryAmpl.NUM_ITERATIONS); i++) {
                int z = cern.jet.random.Distributions.nextGeometric(0.25, re);
                vs.offer(z);
            }
            java.util.List<java.lang.Integer> top = vs.peek(5);
            java.lang.System.out.println("Geometric:");
            for (java.lang.Integer e : top) {
                java.lang.System.out.println(e);
            }
            int tippyTop = top.get(0);
            // AssertGenerator replace invocation
            int o_testGeometricDistribution_cf10747__21 = // StatementAdderMethod cloned existing statement
vs.size();
            // MethodAssertGenerator build local variable
            Object o_23_0 = o_testGeometricDistribution_cf10747__21;
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.stream.StreamSummary vc_581 = new com.clearspring.analytics.stream.StreamSummary();
            // StatementAdderMethod cloned existing statement
            vc_581.topK(tippyTop);
            java.lang.System.out.println(vs);
            org.junit.Assert.fail("testGeometricDistribution_cf10747_cf11291 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testGeometricDistribution */
    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testGeometricDistribution_cf10747 */
    @org.junit.Test(timeout = 10000)
    public void testGeometricDistribution_cf10747_cf11291_failAssert20_add13138() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_23_1 = 10;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_23_1, 10);
            com.clearspring.analytics.stream.StreamSummary<java.lang.Integer> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.Integer>(10);
            cern.jet.random.engine.RandomEngine re = cern.jet.random.engine.RandomEngine.makeDefault();
            for (int i = 0; i < (com.clearspring.analytics.stream.TestStreamSummaryAmpl.NUM_ITERATIONS); i++) {
                int z = cern.jet.random.Distributions.nextGeometric(0.25, re);
                vs.offer(z);
            }
            java.util.List<java.lang.Integer> top = vs.peek(5);
            java.lang.System.out.println("Geometric:");
            for (java.lang.Integer e : top) {
                java.lang.System.out.println(e);
            }
            int tippyTop = top.get(0);
            // AssertGenerator replace invocation
            int o_testGeometricDistribution_cf10747__21 = // StatementAdderMethod cloned existing statement
vs.size();
            // MethodAssertGenerator build local variable
            Object o_23_0 = o_testGeometricDistribution_cf10747__21;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_23_0, 10);
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.stream.StreamSummary vc_581 = new com.clearspring.analytics.stream.StreamSummary();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.StreamSummary)vc_581).getCapacity(), 0);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_581.topK(tippyTop);
            // StatementAdderMethod cloned existing statement
            vc_581.topK(tippyTop);
            java.lang.System.out.println(vs);
            org.junit.Assert.fail("testGeometricDistribution_cf10747_cf11291 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testSerialization */
    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test(timeout = 10000)
    public void testSerialization_add13177() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
        java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "C" , "A" , "A" };
        for (java.lang.String i : stream) {
            // MethodCallAdder
            vs.offer(i);
            // AssertGenerator replace invocation
            boolean o_testSerialization_add13177__9 = vs.offer(i);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testSerialization_add13177__9);
        }
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutput oo = new java.io.ObjectOutputStream(baos);
        oo.writeObject(vs);
        oo.close();
        java.io.ObjectInput oi = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> clone = ((com.clearspring.analytics.stream.StreamSummary<java.lang.String>) (oi.readObject()));
        org.junit.Assert.assertEquals(vs.toString(), clone.toString());
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testSerialization */
    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test(timeout = 10000)
    public void testSerialization_cf13260() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
        java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "C" , "A" , "A" };
        for (java.lang.String i : stream) {
            vs.offer(i);
        }
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutput oo = new java.io.ObjectOutputStream(baos);
        oo.writeObject(vs);
        oo.close();
        java.io.ObjectInput oi = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> clone = ((com.clearspring.analytics.stream.StreamSummary<java.lang.String>) (oi.readObject()));
        // AssertGenerator replace invocation
        int o_testSerialization_cf13260__20 = // StatementAdderMethod cloned existing statement
vs.size();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSerialization_cf13260__20, 3);
        org.junit.Assert.assertEquals(vs.toString(), clone.toString());
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testSerialization */
    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test(timeout = 10000)
    public void testSerialization_cf13242() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
        java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "C" , "A" , "A" };
        for (java.lang.String i : stream) {
            vs.offer(i);
        }
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutput oo = new java.io.ObjectOutputStream(baos);
        oo.writeObject(vs);
        oo.close();
        java.io.ObjectInput oi = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> clone = ((com.clearspring.analytics.stream.StreamSummary<java.lang.String>) (oi.readObject()));
        // AssertGenerator replace invocation
        byte[] o_testSerialization_cf13242__20 = // StatementAdderMethod cloned existing statement
clone.toBytes();
        // AssertGenerator add assertion
        byte[] array_1616919759 = new byte[]{-84, -19, 0, 5, 119, 8, 0, 0, 0, 3, 0, 0, 0, 3, 115, 114, 0, 40, 99, 111, 109, 46, 99, 108, 101, 97, 114, 115, 112, 114, 105, 110, 103, 46, 97, 110, 97, 108, 121, 116, 105, 99, 115, 46, 115, 116, 114, 101, 97, 109, 46, 67, 111, 117, 110, 116, 101, 114, -7, -16, 21, -73, 39, -22, 45, -68, 12, 0, 0, 120, 112, 116, 0, 1, 88, 119, 16, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 2, 120, 115, 113, 0, 126, 0, 0, 116, 0, 1, 67, 119, 16, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 2, 120, 115, 113, 0, 126, 0, 0, 116, 0, 1, 65, 119, 16, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 2, 120};
	byte[] array_1036400070 = (byte[])o_testSerialization_cf13242__20;
	for(int ii = 0; ii <array_1616919759.length; ii++) {
		org.junit.Assert.assertEquals(array_1616919759[ii], array_1036400070[ii]);
	};
        org.junit.Assert.assertEquals(vs.toString(), clone.toString());
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testSerialization */
    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test(timeout = 10000)
    public void testSerialization_cf13263() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
        java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "C" , "A" , "A" };
        for (java.lang.String i : stream) {
            vs.offer(i);
        }
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutput oo = new java.io.ObjectOutputStream(baos);
        oo.writeObject(vs);
        oo.close();
        java.io.ObjectInput oi = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> clone = ((com.clearspring.analytics.stream.StreamSummary<java.lang.String>) (oi.readObject()));
        // AssertGenerator replace invocation
        java.lang.String o_testSerialization_cf13263__20 = // StatementAdderMethod cloned existing statement
vs.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSerialization_cf13263__20, "[{5:[{A:2}]},{4:[{X:2},{C:2}]}]");
        org.junit.Assert.assertEquals(vs.toString(), clone.toString());
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testSerialization */
    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test(timeout = 10000)
    public void testSerialization_cf13257() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
        java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "C" , "A" , "A" };
        for (java.lang.String i : stream) {
            vs.offer(i);
        }
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutput oo = new java.io.ObjectOutputStream(baos);
        oo.writeObject(vs);
        oo.close();
        java.io.ObjectInput oi = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> clone = ((com.clearspring.analytics.stream.StreamSummary<java.lang.String>) (oi.readObject()));
        // AssertGenerator replace invocation
        int o_testSerialization_cf13257__20 = // StatementAdderMethod cloned existing statement
vs.getCapacity();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSerialization_cf13257__20, 3);
        org.junit.Assert.assertEquals(vs.toString(), clone.toString());
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testStreamSummary */
    @org.junit.Test(timeout = 10000)
    public void testStreamSummary_add45205() {
        com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
        java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "A" , "A" };
        for (java.lang.String i : stream) {
            // MethodCallAdder
            vs.offer(i);
            // AssertGenerator replace invocation
            boolean o_testStreamSummary_add45205__9 = vs.offer(i);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testStreamSummary_add45205__9);
            /* for(String s : vs.poll(3))
            System.out.print(s+" ");
             */
            java.lang.System.out.println(vs);
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testStreamSummary */
    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testStreamSummary_add45205 */
    @org.junit.Test(timeout = 10000)
    public void testStreamSummary_add45205_cf45331_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
            java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "A" , "A" };
            for (java.lang.String i : stream) {
                // MethodCallAdder
                vs.offer(i);
                // AssertGenerator replace invocation
                boolean o_testStreamSummary_add45205__9 = vs.offer(i);
                // StatementAdderOnAssert create literal from method
                int int_vc_1360 = 10000;
                // StatementAdderOnAssert create random local variable
                com.clearspring.analytics.stream.StreamSummary vc_11331 = new com.clearspring.analytics.stream.StreamSummary();
                // StatementAdderMethod cloned existing statement
                vc_11331.topK(int_vc_1360);
                // MethodAssertGenerator build local variable
                Object o_17_0 = o_testStreamSummary_add45205__9;
                /* for(String s : vs.poll(3))
                System.out.print(s+" ");
                 */
                java.lang.System.out.println(vs);
            }
            org.junit.Assert.fail("testStreamSummary_add45205_cf45331 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testStreamSummary */
    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testStreamSummary_add45205 */
    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testStreamSummary_add45205_cf45325 */
    @org.junit.Test(timeout = 10000)
    public void testStreamSummary_add45205_cf45325_failAssert8_add45634() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
            java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "A" , "A" };
            for (java.lang.String i : stream) {
                // MethodCallAdder
                vs.offer(i);
                // AssertGenerator replace invocation
                boolean o_testStreamSummary_add45205_cf45325_failAssert8_add45634__11 = // MethodCallAdder
vs.offer(i);
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(o_testStreamSummary_add45205_cf45325_failAssert8_add45634__11);
                // AssertGenerator replace invocation
                boolean o_testStreamSummary_add45205__9 = vs.offer(i);
                // StatementAdderOnAssert create literal from method
                int int_vc_1359 = 10000;
                // StatementAdderOnAssert create random local variable
                com.clearspring.analytics.stream.StreamSummary vc_11328 = new com.clearspring.analytics.stream.StreamSummary();
                // StatementAdderMethod cloned existing statement
                vc_11328.peek(int_vc_1359);
                // MethodAssertGenerator build local variable
                Object o_17_0 = o_testStreamSummary_add45205__9;
                /* for(String s : vs.poll(3))
                System.out.print(s+" ");
                 */
                java.lang.System.out.println(vs);
            }
            org.junit.Assert.fail("testStreamSummary_add45205_cf45325 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testStreamSummary */
    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testStreamSummary_add45205 */
    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testStreamSummary_add45205_cf45325 */
    @org.junit.Test(timeout = 10000)
    public void testStreamSummary_add45205_cf45325_failAssert8_literalMutation45640() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.StreamSummary)vs).getCapacity(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.StreamSummary)vs).size(), 0);
            java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "A" , "A" };
            for (java.lang.String i : stream) {
                // MethodCallAdder
                vs.offer(i);
                // AssertGenerator replace invocation
                boolean o_testStreamSummary_add45205__9 = vs.offer(i);
                // StatementAdderOnAssert create literal from method
                int int_vc_1359 = 10000;
                // StatementAdderOnAssert create random local variable
                com.clearspring.analytics.stream.StreamSummary vc_11328 = new com.clearspring.analytics.stream.StreamSummary();
                // StatementAdderMethod cloned existing statement
                vc_11328.peek(int_vc_1359);
                // MethodAssertGenerator build local variable
                Object o_17_0 = o_testStreamSummary_add45205__9;
                /* for(String s : vs.poll(3))
                System.out.print(s+" ");
                 */
                java.lang.System.out.println(vs);
            }
            org.junit.Assert.fail("testStreamSummary_add45205_cf45325 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testTopK */
    @org.junit.Test(timeout = 10000)
    public void testTopK_cf45901_failAssert10() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
            java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "C" , "A" , "A" };
            for (java.lang.String i : stream) {
                vs.offer(i);
            }
            java.util.List<com.clearspring.analytics.stream.Counter<java.lang.String>> topK = vs.topK(3);
            for (com.clearspring.analytics.stream.Counter<java.lang.String> c : topK) {
                // StatementAdderOnAssert create random local variable
                com.clearspring.analytics.stream.StreamSummary vc_11374 = new com.clearspring.analytics.stream.StreamSummary();
                // StatementAdderMethod cloned existing statement
                vc_11374.size();
                // MethodAssertGenerator build local variable
                Object o_17_0 = java.util.Arrays.asList("A", "C", "X").contains(c.getItem());
            }
            org.junit.Assert.fail("testTopK_cf45901 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testTopK */
    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testTopK_cf45906 */
    @org.junit.Test(timeout = 10000)
    public void testTopK_cf45906_failAssert14_literalMutation46185() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
            java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "C" , "A" , "A" };
            for (java.lang.String i : stream) {
                vs.offer(i);
            }
            java.util.List<com.clearspring.analytics.stream.Counter<java.lang.String>> topK = vs.topK(3);
            for (com.clearspring.analytics.stream.Counter<java.lang.String> c : topK) {
                // StatementAdderOnAssert create random local variable
                int vc_11379 = 985983078;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(vc_11379, 985983078);
                // StatementAdderOnAssert create null value
                com.clearspring.analytics.stream.StreamSummary vc_11377 = (com.clearspring.analytics.stream.StreamSummary)null;
                // StatementAdderMethod cloned existing statement
                vc_11377.peek(vc_11379);
                // MethodAssertGenerator build local variable
                Object o_19_0 = java.util.Arrays.asList("A", "C", "X").contains(c.getItem());
            }
            org.junit.Assert.fail("testTopK_cf45906 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testTopKWithIncrement */
    @org.junit.Test(timeout = 10000)
    public void testTopKWithIncrement_cf48515_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
            java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "C" , "A" , "A" };
            for (java.lang.String i : stream) {
                vs.offer(i, 10);
            }
            java.util.List<com.clearspring.analytics.stream.Counter<java.lang.String>> topK = vs.topK(3);
            for (com.clearspring.analytics.stream.Counter<java.lang.String> c : topK) {
                // StatementAdderOnAssert create random local variable
                com.clearspring.analytics.stream.StreamSummary vc_12076 = new com.clearspring.analytics.stream.StreamSummary();
                // StatementAdderMethod cloned existing statement
                vc_12076.toString();
                // MethodAssertGenerator build local variable
                Object o_17_0 = java.util.Arrays.asList("A", "C", "X").contains(c.getItem());
            }
            org.junit.Assert.fail("testTopKWithIncrement_cf48515 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testTopKWithIncrement */
    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testTopKWithIncrement_cf48520 */
    @org.junit.Test(timeout = 10000)
    public void testTopKWithIncrement_cf48520_failAssert16_literalMutation48872() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
            java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "C" , "A" , "A" };
            for (java.lang.String i : stream) {
                vs.offer(i, 10);
            }
            java.util.List<com.clearspring.analytics.stream.Counter<java.lang.String>> topK = vs.topK(3);
            for (com.clearspring.analytics.stream.Counter<java.lang.String> c : topK) {
                // StatementAdderOnAssert create literal from method
                int int_vc_1449 = 5;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(int_vc_1449, 5);
                // StatementAdderOnAssert create random local variable
                com.clearspring.analytics.stream.StreamSummary vc_12078 = new com.clearspring.analytics.stream.StreamSummary();
                // StatementAdderMethod cloned existing statement
                vc_12078.peek(int_vc_1449);
                // MethodAssertGenerator build local variable
                Object o_19_0 = java.util.Arrays.asList("A", "C", "X").contains(c.getItem());
            }
            org.junit.Assert.fail("testTopKWithIncrement_cf48520 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testTopKWithIncrement */
    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testTopKWithIncrement_cf48522 */
    @org.junit.Test(timeout = 10000)
    public void testTopKWithIncrement_cf48522_failAssert18_literalMutation48950_cf49469_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
                java.lang.String[] stream = new java.lang.String[]{ "X" , "X" , "Y" , "Z" , "A" , "B" , "C" , "X" , "X" , "A" , "C" , "A" , "A" };
                for (java.lang.String i : stream) {
                    vs.offer(i, 10);
                }
                java.util.List<com.clearspring.analytics.stream.Counter<java.lang.String>> topK = vs.topK(3);
                for (com.clearspring.analytics.stream.Counter<java.lang.String> c : topK) {
                    // MethodAssertGenerator build local variable
                    Object o_22_1 = 6;
                    // StatementAdderOnAssert create literal from method
                    int int_vc_1450 = 6;
                    // StatementAdderOnAssert create random local variable
                    int vc_12229 = -1615321958;
                    // StatementAdderMethod cloned existing statement
                    vs.peek(vc_12229);
                    // MethodAssertGenerator build local variable
                    Object o_22_0 = int_vc_1450;
                    // StatementAdderOnAssert create null value
                    com.clearspring.analytics.stream.StreamSummary vc_12080 = (com.clearspring.analytics.stream.StreamSummary)null;
                    // StatementAdderMethod cloned existing statement
                    vc_12080.topK(int_vc_1450);
                    // MethodAssertGenerator build local variable
                    Object o_19_0 = java.util.Arrays.asList("A", "C", "X").contains(c.getItem());
                }
                org.junit.Assert.fail("testTopKWithIncrement_cf48522 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testTopKWithIncrement_cf48522_failAssert18_literalMutation48950_cf49469 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testTopKWithIncrementOutOfOrder */
    /* amplification of com.clearspring.analytics.stream.TestStreamSummary#testTopKWithIncrementOutOfOrder_literalMutation51052 */
    @org.junit.Test
    public void testTopKWithIncrementOutOfOrder_literalMutation51052_literalMutation57259_failAssert19_literalMutation62585() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs_increment = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(3);
            com.clearspring.analytics.stream.StreamSummary<java.lang.String> vs_single = new com.clearspring.analytics.stream.StreamSummary<java.lang.String>(2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.StreamSummary)vs_single).getCapacity(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.StreamSummary)vs_single).size(), 0);
            // AssertGenerator add assertion
            byte[] array_1300830033 = new byte[]{-84, -19, 0, 5, 119, 8, 0, 0, 0, 2, 0, 0, 0, 0};
	byte[] array_625259165 = (byte[])((com.clearspring.analytics.stream.StreamSummary)vs_single).toBytes();
	for(int ii = 0; ii <array_1300830033.length; ii++) {
		org.junit.Assert.assertEquals(array_1300830033[ii], array_625259165[ii]);
	};
            java.lang.String[] stream = new java.lang.String[]{ "A" , "B" , "C" , "D" , "A" };
            java.lang.Integer[] increments = new java.lang.Integer[]{ 15 , 20 , // TestDataMutator on numbers
            12 , 30 , 1 };
            for (int i = 0; i < (stream.length); i++) {
                // AssertGenerator replace invocation
                boolean o_testTopKWithIncrementOutOfOrder_literalMutation51052__11 = vs_increment.offer(stream[i], increments[i]);
                // MethodAssertGenerator build local variable
                Object o_14_0 = o_testTopKWithIncrementOutOfOrder_literalMutation51052__11;
                for (int k = 0; k < (increments[i]); k++) {
                    vs_single.offer(stream[i]);
                }
            }
            java.lang.System.out.println("Insert with counts vs. single inserts:");
            java.lang.System.out.println(vs_increment);
            java.lang.System.out.println(vs_single);
            java.util.List<com.clearspring.analytics.stream.Counter<java.lang.String>> topK_increment = vs_increment.topK(3);
            java.util.List<com.clearspring.analytics.stream.Counter<java.lang.String>> topK_single = vs_single.topK(3);
            for (int i = 1; i < (topK_increment.size()); i++) {
                // MethodAssertGenerator build local variable
                Object o_34_1 = topK_single.get(i).getItem();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_34_1, "A");
                // MethodAssertGenerator build local variable
                Object o_34_0 = topK_increment.get(i).getItem();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_34_0, "B");
            }
            org.junit.Assert.fail("testTopKWithIncrementOutOfOrder_literalMutation51052_literalMutation57259 should have thrown Exception");
        } catch (java.lang.Exception eee) {
        }
    }
}

