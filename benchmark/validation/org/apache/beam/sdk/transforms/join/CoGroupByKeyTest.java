/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.transforms.join;


import CoGbkResult.CoGbkResultCoder;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.UsesSideInputs;
import org.apache.beam.sdk.testing.ValidatesRunner;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for CoGroupByKeyTest. Implements Serializable for anonymous DoFns.
 */
@RunWith(JUnit4.class)
public class CoGroupByKeyTest implements Serializable {
    @Rule
    public final transient TestPipeline p = TestPipeline.create();

    @Test
    @Category({ ValidatesRunner.class, UsesSideInputs.class })
    public void testCoGroupByKeyGetOnly() {
        final TupleTag<String> tag1 = new TupleTag();
        final TupleTag<String> tag2 = new TupleTag();
        PCollection<KV<Integer, CoGbkResult>> coGbkResults = buildGetOnlyGbk(p, tag1, tag2);
        PAssert.thatMap(coGbkResults).satisfies(( results) -> {
            assertEquals("collection1-1", results.get(1).getOnly(tag1));
            assertEquals("collection1-2", results.get(2).getOnly(tag1));
            assertEquals("collection2-2", results.get(2).getOnly(tag2));
            assertEquals("collection2-3", results.get(3).getOnly(tag2));
            return null;
        });
        p.run();
    }

    @Test
    @Category({ ValidatesRunner.class, UsesSideInputs.class })
    public void testCoGroupByKey() {
        final TupleTag<String> namesTag = new TupleTag();
        final TupleTag<String> addressesTag = new TupleTag();
        final TupleTag<String> purchasesTag = new TupleTag();
        PCollection<KV<Integer, CoGbkResult>> coGbkResults = buildPurchasesCoGbk(p, purchasesTag, addressesTag, namesTag);
        PAssert.thatMap(coGbkResults).satisfies(( results) -> {
            CoGbkResult result1 = results.get(1);
            assertEquals("John Smith", result1.getOnly(namesTag));
            assertThat(result1.getAll(purchasesTag), containsInAnyOrder("Shoes", "Book"));
            CoGbkResult result2 = results.get(2);
            assertEquals("Sally James", result2.getOnly(namesTag));
            assertEquals("53 S. 3rd", result2.getOnly(addressesTag));
            assertThat(result2.getAll(purchasesTag), containsInAnyOrder("Suit", "Boat"));
            CoGbkResult result3 = results.get(3);
            assertEquals("29 School Rd", "29 School Rd", result3.getOnly(addressesTag));
            assertThat(result3.getAll(purchasesTag), containsInAnyOrder("Car", "House"));
            CoGbkResult result8 = results.get(8);
            assertEquals("Jeffery Spalding", result8.getOnly(namesTag));
            assertEquals("6 Watling Rd", result8.getOnly(addressesTag));
            assertThat(result8.getAll(purchasesTag), containsInAnyOrder("House", "Suit Case"));
            CoGbkResult result20 = results.get(20);
            assertEquals("Joan Lichtfield", result20.getOnly(namesTag));
            assertEquals("3 W. Arizona", result20.getOnly(addressesTag));
            assertEquals("383 Jackson Street", results.get(10).getOnly(addressesTag));
            assertThat(results.get(4).getAll(purchasesTag), containsInAnyOrder("Suit"));
            assertThat(results.get(10).getAll(purchasesTag), containsInAnyOrder("Pens"));
            assertThat(results.get(11).getAll(purchasesTag), containsInAnyOrder("House"));
            assertThat(results.get(14).getAll(purchasesTag), containsInAnyOrder("Shoes"));
            return null;
        });
        p.run();
    }

    /**
     * A DoFn used in testCoGroupByKeyWithWindowing(), to test processing the results of a
     * CoGroupByKey.
     */
    private static class ClickOfPurchaseFn extends DoFn<KV<Integer, CoGbkResult>, KV<String, String>> {
        private final TupleTag<String> clicksTag;

        private final TupleTag<String> purchasesTag;

        private ClickOfPurchaseFn(TupleTag<String> clicksTag, TupleTag<String> purchasesTag) {
            this.clicksTag = clicksTag;
            this.purchasesTag = purchasesTag;
        }

        @ProcessElement
        public void processElement(ProcessContext c, BoundedWindow window) {
            BoundedWindow w = window;
            KV<Integer, CoGbkResult> e = c.element();
            CoGbkResult row = e.getValue();
            Iterable<String> clicks = row.getAll(clicksTag);
            Iterable<String> purchases = row.getAll(purchasesTag);
            for (String click : clicks) {
                for (String purchase : purchases) {
                    c.output(KV.of(((click + ":") + purchase), (((c.timestamp().getMillis()) + ":") + (w.maxTimestamp().getMillis()))));
                }
            }
        }
    }

    /**
     * A DoFn used in testCoGroupByKeyHandleResults(), to test processing the results of a
     * CoGroupByKey.
     */
    private static class CorrelatePurchaseCountForAddressesWithoutNamesFn extends DoFn<KV<Integer, CoGbkResult>, KV<String, Integer>> {
        private final TupleTag<String> purchasesTag;

        private final TupleTag<String> addressesTag;

        private final TupleTag<String> namesTag;

        private CorrelatePurchaseCountForAddressesWithoutNamesFn(TupleTag<String> purchasesTag, TupleTag<String> addressesTag, TupleTag<String> namesTag) {
            this.purchasesTag = purchasesTag;
            this.addressesTag = addressesTag;
            this.namesTag = namesTag;
        }

        @ProcessElement
        public void processElement(ProcessContext c) {
            KV<Integer, CoGbkResult> e = c.element();
            CoGbkResult row = e.getValue();
            // Don't actually care about the id.
            Iterable<String> names = row.getAll(namesTag);
            if (names.iterator().hasNext()) {
                // Nothing to do. There was a name.
                return;
            }
            Iterable<String> addresses = row.getAll(addressesTag);
            if (!(addresses.iterator().hasNext())) {
                // Nothing to do, there was no address.
                return;
            }
            // Buffer the addresses so we can accredit all of them with
            // corresponding purchases. All addresses are for the same id, so
            // if there are multiple, we apply the same purchase count to all.
            ArrayList<String> addressList = new ArrayList<>();
            for (String address : addresses) {
                addressList.add(address);
            }
            Iterable<String> purchases = row.getAll(purchasesTag);
            int purchaseCount = Iterables.size(purchases);
            for (String address : addressList) {
                c.output(KV.of(address, purchaseCount));
            }
        }
    }

    /**
     * Tests that the consuming DoFn (CorrelatePurchaseCountForAddressesWithoutNamesFn) performs as
     * expected.
     */
    @SuppressWarnings("unchecked")
    @Test
    @Category(NeedsRunner.class)
    public void testConsumingDoFn() throws Exception {
        TupleTag<String> purchasesTag = new TupleTag();
        TupleTag<String> addressesTag = new TupleTag();
        TupleTag<String> namesTag = new TupleTag();
        // result1 should get filtered out because it has a name.
        CoGbkResult result1 = CoGbkResult.of(purchasesTag, Arrays.asList("3a", "3b")).and(addressesTag, Arrays.asList("2a", "2b")).and(namesTag, Arrays.asList("1a"));
        // result 2 should be counted because it has an address and purchases.
        CoGbkResult result2 = CoGbkResult.of(purchasesTag, Arrays.asList("5a", "5b")).and(addressesTag, Arrays.asList("4a")).and(namesTag, new ArrayList());
        // result 3 should not be counted because it has no addresses.
        CoGbkResult result3 = CoGbkResult.of(purchasesTag, Arrays.asList("7a", "7b")).and(addressesTag, new ArrayList()).and(namesTag, new ArrayList());
        // result 4 should be counted as 0, because it has no purchases.
        CoGbkResult result4 = CoGbkResult.of(purchasesTag, new ArrayList()).and(addressesTag, Arrays.asList("8a")).and(namesTag, new ArrayList());
        KvCoder<Integer, CoGbkResult> coder = KvCoder.of(VarIntCoder.of(), CoGbkResultCoder.of(CoGbkResultSchema.of(ImmutableList.of(purchasesTag, addressesTag, namesTag)), UnionCoder.of(ImmutableList.of(StringUtf8Coder.of(), StringUtf8Coder.of(), StringUtf8Coder.of()))));
        PCollection<KV<String, Integer>> results = p.apply(Create.of(KV.of(1, result1), KV.of(2, result2), KV.of(3, result3), KV.of(4, result4)).withCoder(coder)).apply(ParDo.of(new CoGroupByKeyTest.CorrelatePurchaseCountForAddressesWithoutNamesFn(purchasesTag, addressesTag, namesTag)));
        PAssert.that(results).containsInAnyOrder(KV.of("4a", 2), KV.of("8a", 0));
        p.run();
    }

    /**
     * Tests the pipeline end-to-end. Builds the purchases CoGroupByKey, and applies
     * CorrelatePurchaseCountForAddressesWithoutNamesFn to the results.
     */
    @SuppressWarnings("unchecked")
    @Test
    @Category(ValidatesRunner.class)
    public void testCoGroupByKeyHandleResults() {
        TupleTag<String> namesTag = new TupleTag();
        TupleTag<String> addressesTag = new TupleTag();
        TupleTag<String> purchasesTag = new TupleTag();
        PCollection<KV<Integer, CoGbkResult>> coGbkResults = buildPurchasesCoGbk(p, purchasesTag, addressesTag, namesTag);
        // Do some simple processing on the result of the CoGroupByKey.  Count the
        // purchases for each address on record that has no associated name.
        PCollection<KV<String, Integer>> purchaseCountByKnownAddressesWithoutKnownNames = coGbkResults.apply(ParDo.of(new CoGroupByKeyTest.CorrelatePurchaseCountForAddressesWithoutNamesFn(purchasesTag, addressesTag, namesTag)));
        PAssert.that(purchaseCountByKnownAddressesWithoutKnownNames).containsInAnyOrder(KV.of("29 School Rd", 2), KV.of("383 Jackson Street", 1));
        p.run();
    }

    /**
     * Tests the pipeline end-to-end with FixedWindows.
     */
    @SuppressWarnings("unchecked")
    @Test
    @Category(ValidatesRunner.class)
    public void testCoGroupByKeyWithWindowing() {
        TupleTag<String> clicksTag = new TupleTag();
        TupleTag<String> purchasesTag = new TupleTag();
        PCollection<KV<Integer, CoGbkResult>> coGbkResults = buildPurchasesCoGbkWithWindowing(p, clicksTag, purchasesTag);
        PCollection<KV<String, String>> clickOfPurchase = coGbkResults.apply(ParDo.of(new CoGroupByKeyTest.ClickOfPurchaseFn(clicksTag, purchasesTag)));
        PAssert.that(clickOfPurchase).containsInAnyOrder(KV.of("Click t0:Boat t1", "0:3"), KV.of("Click t0:Shoesi t2", "0:3"), KV.of("Click t0:Pens t3", "0:3"), KV.of("Click t4:Car t6", "4:7"), KV.of("Click t4:Book t7", "4:7"), KV.of("Click t6:Car t6", "4:7"), KV.of("Click t6:Book t7", "4:7"), KV.of("Click t8:House t8", "8:11"), KV.of("Click t8:Shoes t9", "8:11"), KV.of("Click t8:House t10", "8:11"));
        p.run();
    }
}

