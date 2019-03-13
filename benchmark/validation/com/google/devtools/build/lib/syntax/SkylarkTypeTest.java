/**
 * Copyright 2018 The Bazel Authors. All Rights Reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.syntax;


import SkylarkType.BOTTOM;
import SkylarkType.Combination;
import SkylarkType.DICT;
import SkylarkType.INT;
import SkylarkType.LIST;
import SkylarkType.STRING;
import SkylarkType.STRING_PAIR;
import SkylarkType.TOP;
import SkylarkType.TUPLE;
import SkylarkType.Union;
import com.google.devtools.build.lib.skylarkinterface.SkylarkModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for Skylark's type system.
 */
@RunWith(JUnit4.class)
public final class SkylarkTypeTest {
    @SkylarkModule(name = "ParentType", doc = "A parent class annotated with @SkylarkModule.")
    private static class ParentClassWithSkylarkModule {}

    private static class ChildClass extends SkylarkTypeTest.ParentClassWithSkylarkModule {}

    @Test
    public void simpleTypeOfChild_UsesTypeOfAncestorHavingSkylarkModule() {
        assertThat(SkylarkType.of(SkylarkTypeTest.ChildClass.class).getType()).isEqualTo(SkylarkTypeTest.ParentClassWithSkylarkModule.class);
    }

    @Test
    public void typeHierarchy_Basic() throws Exception {
        assertThat(INT.includes(BOTTOM)).isTrue();
        assertThat(BOTTOM.includes(INT)).isFalse();
        assertThat(TOP.includes(INT)).isTrue();
    }

    @Test
    public void typeHierarchy_Combination() throws Exception {
        SkylarkType combo = Combination.of(LIST, INT);
        assertThat(LIST.includes(combo)).isTrue();
    }

    @Test
    public void typeHierarchy_Union() throws Exception {
        SkylarkType combo = Combination.of(LIST, INT);
        SkylarkType union = Union.of(DICT, LIST);
        assertThat(union.includes(DICT)).isTrue();
        assertThat(union.includes(combo)).isTrue();
        assertThat(union.includes(STRING)).isFalse();
    }

    @Test
    public void typeHierarchy_Intersection() throws Exception {
        SkylarkType combo = Combination.of(LIST, INT);
        SkylarkType union1 = Union.of(DICT, LIST);
        SkylarkType union2 = Union.of(LIST, DICT, STRING, INT);
        SkylarkType inter = SkylarkType.intersection(union1, union2);
        assertThat(inter.includes(DICT)).isTrue();
        assertThat(inter.includes(LIST)).isTrue();
        assertThat(inter.includes(combo)).isTrue();
        assertThat(inter.includes(INT)).isFalse();
    }

    @Test
    public void testStringPairTuple() {
        assertThat(SkylarkType.intersection(STRING_PAIR, TUPLE)).isEqualTo(STRING_PAIR);
    }
}

