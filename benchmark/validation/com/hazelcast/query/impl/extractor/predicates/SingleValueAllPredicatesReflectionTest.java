/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.query.impl.extractor.predicates;


import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.query.Predicates;
import com.hazelcast.query.impl.extractor.AbstractExtractionSpecification;
import com.hazelcast.query.impl.extractor.AbstractExtractionTest;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.hazelcast.query.impl.extractor.AbstractExtractionSpecification.Expected.of;


/**
 * Tests whether all predicates work with the extraction in attributes that are not collections.
 * <p>
 * Extraction mechanism: IN-BUILT REFLECTION EXTRACTION
 * <p>
 * This test is parametrised:
 * - each test is executed separately for BINARY and OBJECT in memory format
 * - each test is executed separately having each query using NO_INDEX, UNORDERED_INDEX and ORDERED_INDEX.
 * In this way we are spec-testing most of the reasonable combinations of the configuration of map & extraction.
 */
@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class SingleValueAllPredicatesReflectionTest extends AbstractExtractionTest {
    private static final SingleValueDataStructure.Person BOND = SingleValueDataStructure.person(130);

    private static final SingleValueDataStructure.Person HUNT = SingleValueDataStructure.person(120);

    public SingleValueAllPredicatesReflectionTest(InMemoryFormat inMemoryFormat, AbstractExtractionSpecification.Index index, AbstractExtractionSpecification.Multivalue multivalue) {
        super(inMemoryFormat, index, multivalue);
    }

    @Test
    public void equals_predicate() {
        execute(AbstractExtractionSpecification.Input.of(SingleValueAllPredicatesReflectionTest.BOND, SingleValueAllPredicatesReflectionTest.HUNT), AbstractExtractionSpecification.Query.of(Predicates.equal("brain.iq", 130), mv), of(SingleValueAllPredicatesReflectionTest.BOND));
    }

    @Test
    public void between_predicate() {
        execute(AbstractExtractionSpecification.Input.of(SingleValueAllPredicatesReflectionTest.BOND, SingleValueAllPredicatesReflectionTest.HUNT), AbstractExtractionSpecification.Query.of(Predicates.between("brain.iq", 115, 135), mv), of(SingleValueAllPredicatesReflectionTest.BOND, SingleValueAllPredicatesReflectionTest.HUNT));
    }

    @Test
    public void greater_less_predicate() {
        execute(AbstractExtractionSpecification.Input.of(SingleValueAllPredicatesReflectionTest.BOND, SingleValueAllPredicatesReflectionTest.HUNT), AbstractExtractionSpecification.Query.of(Predicates.lessEqual("brain.iq", 120), mv), of(SingleValueAllPredicatesReflectionTest.HUNT));
    }

    @Test
    public void in_predicate() {
        execute(AbstractExtractionSpecification.Input.of(SingleValueAllPredicatesReflectionTest.BOND, SingleValueAllPredicatesReflectionTest.HUNT), AbstractExtractionSpecification.Query.of(Predicates.in("brain.iq", 120, 121, 122), mv), of(SingleValueAllPredicatesReflectionTest.HUNT));
    }

    @Test
    public void notEqual_predicate() {
        execute(AbstractExtractionSpecification.Input.of(SingleValueAllPredicatesReflectionTest.BOND, SingleValueAllPredicatesReflectionTest.HUNT), AbstractExtractionSpecification.Query.of(Predicates.notEqual("brain.iq", 130), mv), of(SingleValueAllPredicatesReflectionTest.HUNT));
    }

    @Test
    public void like_predicate() {
        execute(AbstractExtractionSpecification.Input.of(SingleValueAllPredicatesReflectionTest.BOND, SingleValueAllPredicatesReflectionTest.HUNT), AbstractExtractionSpecification.Query.of(Predicates.like("brain.name", "brain12_"), mv), of(SingleValueAllPredicatesReflectionTest.HUNT));
    }

    @Test
    public void ilike_predicate() {
        execute(AbstractExtractionSpecification.Input.of(SingleValueAllPredicatesReflectionTest.BOND, SingleValueAllPredicatesReflectionTest.HUNT), AbstractExtractionSpecification.Query.of(Predicates.ilike("brain.name", "BR%130"), mv), of(SingleValueAllPredicatesReflectionTest.BOND));
    }

    @Test
    public void regex_predicate() {
        execute(AbstractExtractionSpecification.Input.of(SingleValueAllPredicatesReflectionTest.BOND, SingleValueAllPredicatesReflectionTest.HUNT), AbstractExtractionSpecification.Query.of(Predicates.regex("brain.name", "brain13.*"), mv), of(SingleValueAllPredicatesReflectionTest.BOND));
    }

    @Test
    public void key_equal_predicate() {
        execute(AbstractExtractionSpecification.Input.of(SingleValueAllPredicatesReflectionTest.BOND, SingleValueAllPredicatesReflectionTest.HUNT), AbstractExtractionSpecification.Query.of(Predicates.equal("__key", 0), mv), of(SingleValueAllPredicatesReflectionTest.BOND));
    }
}

