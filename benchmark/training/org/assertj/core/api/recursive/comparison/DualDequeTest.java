/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.api.recursive.comparison;


import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


public class DualDequeTest {
    private RecursiveComparisonConfiguration recursiveComparisonConfiguration;

    @Test
    public void should_ignore_dual_keys_with_a_null_first_key() {
        // GIVEN
        recursiveComparisonConfiguration.setIgnoreAllActualNullFields(true);
        DualValueDeque dualKayDeque = new DualValueDeque(recursiveComparisonConfiguration);
        DualValue dualKeyA = DualDequeTest.dualKey(null, "A");
        DualValue dualKeyB = DualDequeTest.dualKey("B", "B");
        DualValue dualKeyC = DualDequeTest.dualKey(null, "C");
        DualValue dualKeyD = DualDequeTest.dualKey("D", "D");
        DualValue dualKeyE = DualDequeTest.dualKey("E", "E");
        // WHEN
        dualKayDeque.add(dualKeyA);
        dualKayDeque.add(dualKeyB);
        dualKayDeque.addFirst(dualKeyC);
        dualKayDeque.add(dualKeyD);
        dualKayDeque.addLast(dualKeyE);
        dualKayDeque.add(1, dualKeyA);
        dualKayDeque.addAll(Lists.list(dualKeyA, dualKeyB, dualKeyC));
        dualKayDeque.addAll(0, Lists.list(dualKeyA, dualKeyB, dualKeyC));
        // THEN
        Assertions.assertThat(dualKayDeque).containsExactly(dualKeyB, dualKeyB, dualKeyD, dualKeyE, dualKeyB);
    }

    @Test
    public void should_not_ignore_any_dual_keys() {
        // GIVEN
        DualValueDeque dualKayDeque = new DualValueDeque(recursiveComparisonConfiguration);
        DualValue dualKeyA = DualDequeTest.dualKey(null, "A");
        DualValue dualKeyB = DualDequeTest.dualKey("B", "B");
        DualValue dualKeyC = DualDequeTest.dualKey(null, "C");
        DualValue dualKeyD = DualDequeTest.dualKey("D", "D");
        DualValue dualKeyE = DualDequeTest.dualKey("E", "E");
        // WHEN
        dualKayDeque.add(dualKeyA);
        dualKayDeque.add(dualKeyB);
        dualKayDeque.addFirst(dualKeyC);
        dualKayDeque.add(dualKeyD);
        dualKayDeque.addLast(dualKeyE);
        dualKayDeque.add(1, dualKeyA);
        dualKayDeque.addAll(Lists.list(dualKeyA, dualKeyB, dualKeyC));
        dualKayDeque.addAll(0, Lists.list(dualKeyA, dualKeyB, dualKeyC));
        // THEN
        Assertions.assertThat(dualKayDeque).containsExactly(dualKeyA, dualKeyB, dualKeyC, dualKeyC, dualKeyA, dualKeyA, dualKeyB, dualKeyD, dualKeyE, dualKeyA, dualKeyB, dualKeyC);
    }
}

