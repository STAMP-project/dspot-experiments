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
package com.hazelcast.query.impl.extractor.specification;


import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.query.Predicates;
import com.hazelcast.query.QueryException;
import com.hazelcast.query.extractor.ValueCallback;
import com.hazelcast.query.extractor.ValueCollector;
import com.hazelcast.query.extractor.ValueExtractor;
import com.hazelcast.query.extractor.ValueReader;
import com.hazelcast.query.impl.extractor.AbstractExtractionSpecification;
import com.hazelcast.query.impl.extractor.AbstractExtractionTest;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.hazelcast.query.impl.extractor.AbstractExtractionSpecification.Expected.of;


/**
 * Specification test that verifies the behavior of corner-cases extraction with extractor and arguments.
 * <p/>
 * Extraction mechanism: EXTRACTOR-BASED EXTRACTION
 * <p/>
 * This test is parametrised on two axes (see the parametrisationData() method):
 * - in memory format
 * - indexing
 * - extraction in collections and arrays
 */
@RunWith(Parameterized.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ExtractionWithExtractorsSpecTest extends AbstractExtractionTest {
    private static final ComplexTestDataStructure.Person BOND = ComplexTestDataStructure.person("Bond", ComplexTestDataStructure.limb("left-hand", ComplexTestDataStructure.tattoos(), ComplexTestDataStructure.finger("thumb"), ComplexTestDataStructure.finger(null)), ComplexTestDataStructure.limb("right-hand", ComplexTestDataStructure.tattoos("knife"), ComplexTestDataStructure.finger("middle"), ComplexTestDataStructure.finger("index")));

    private static final ComplexTestDataStructure.Person KRUEGER = ComplexTestDataStructure.person("Krueger", ComplexTestDataStructure.limb("linke-hand", ComplexTestDataStructure.tattoos("bratwurst"), ComplexTestDataStructure.finger("Zeigefinger"), ComplexTestDataStructure.finger("Mittelfinger")), ComplexTestDataStructure.limb("rechte-hand", ComplexTestDataStructure.tattoos(), ComplexTestDataStructure.finger("Ringfinger"), ComplexTestDataStructure.finger("Daumen")));

    private static final ComplexTestDataStructure.Person HUNT_NULL_LIMB = ComplexTestDataStructure.person("Hunt");

    public ExtractionWithExtractorsSpecTest(InMemoryFormat inMemoryFormat, AbstractExtractionSpecification.Index index, AbstractExtractionSpecification.Multivalue multivalue) {
        super(inMemoryFormat, index, multivalue);
    }

    @Test
    public void extractorWithParam_bondCase() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionWithExtractorsSpecTest.BOND, ExtractionWithExtractorsSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(Predicates.equal("tattoosCount[1]", 1), mv), of(ExtractionWithExtractorsSpecTest.BOND));
    }

    @Test
    public void extractorWithParam_kruegerCase() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionWithExtractorsSpecTest.BOND, ExtractionWithExtractorsSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(Predicates.equal("tattoosCount[0]", 1), mv), of(ExtractionWithExtractorsSpecTest.KRUEGER));
    }

    @Test
    public void extractorWithParam_nullCollection() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionWithExtractorsSpecTest.HUNT_NULL_LIMB), AbstractExtractionSpecification.Query.of(Predicates.equal("tattoosCount[0]", 1), mv), of(QueryException.class, IllegalArgumentException.class));
    }

    @Test
    public void extractorWithParam_indexOutOfBound() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionWithExtractorsSpecTest.BOND, ExtractionWithExtractorsSpecTest.KRUEGER, ExtractionWithExtractorsSpecTest.HUNT_NULL_LIMB), AbstractExtractionSpecification.Query.of(Predicates.equal("tattoosCount[2]", 1), mv), of(IndexOutOfBoundsException.class, QueryException.class));
    }

    @Test
    public void extractorWithParam_negativeInput() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionWithExtractorsSpecTest.BOND, ExtractionWithExtractorsSpecTest.KRUEGER, ExtractionWithExtractorsSpecTest.HUNT_NULL_LIMB), AbstractExtractionSpecification.Query.of(Predicates.equal("tattoosCount[-1]", 1), mv), of(ArrayIndexOutOfBoundsException.class, QueryException.class));
    }

    @Test
    public void extractorWithParam_wrongInput_noClosingWithArg() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionWithExtractorsSpecTest.BOND, ExtractionWithExtractorsSpecTest.KRUEGER, ExtractionWithExtractorsSpecTest.HUNT_NULL_LIMB), AbstractExtractionSpecification.Query.of(Predicates.equal("tattoosCount[0", 1), mv), AbstractExtractionSpecification.Expected.of(IllegalArgumentException.class));
    }

    @Test
    public void extractorWithParam_wrongInput_noOpeningWithArg() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionWithExtractorsSpecTest.BOND, ExtractionWithExtractorsSpecTest.KRUEGER, ExtractionWithExtractorsSpecTest.HUNT_NULL_LIMB), AbstractExtractionSpecification.Query.of(Predicates.equal("tattoosCount0]", 1), mv), AbstractExtractionSpecification.Expected.of(IllegalArgumentException.class));
    }

    @Test
    public void extractorWithParam_wrongInput_noClosing() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionWithExtractorsSpecTest.BOND, ExtractionWithExtractorsSpecTest.KRUEGER, ExtractionWithExtractorsSpecTest.HUNT_NULL_LIMB), AbstractExtractionSpecification.Query.of(Predicates.equal("tattoosCount[", 1), mv), AbstractExtractionSpecification.Expected.of(IllegalArgumentException.class));
    }

    @Test
    public void extractorWithParam_wrongInput_noOpening() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionWithExtractorsSpecTest.BOND, ExtractionWithExtractorsSpecTest.KRUEGER, ExtractionWithExtractorsSpecTest.HUNT_NULL_LIMB), AbstractExtractionSpecification.Query.of(Predicates.equal("tattoosCount]", 1), mv), AbstractExtractionSpecification.Expected.of(IllegalArgumentException.class));
    }

    @Test
    public void extractorWithParam_wrongInput_noArgumentWithBrackets() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionWithExtractorsSpecTest.BOND, ExtractionWithExtractorsSpecTest.KRUEGER, ExtractionWithExtractorsSpecTest.HUNT_NULL_LIMB), AbstractExtractionSpecification.Query.of(Predicates.equal("tattoosCount[]", 1), mv), of(QueryException.class));
    }

    @Test
    public void extractorWithParam_wrongInput_noArgumentNoBrackets() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionWithExtractorsSpecTest.BOND, ExtractionWithExtractorsSpecTest.KRUEGER, ExtractionWithExtractorsSpecTest.HUNT_NULL_LIMB), AbstractExtractionSpecification.Query.of(Predicates.equal("tattoosCount", 1), mv), of(QueryException.class));
    }

    @Test
    public void extractorWithParam_wrongInput_squareBracketsInInput() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionWithExtractorsSpecTest.BOND, ExtractionWithExtractorsSpecTest.KRUEGER, ExtractionWithExtractorsSpecTest.HUNT_NULL_LIMB), AbstractExtractionSpecification.Query.of(Predicates.equal("tattoosCount[1183[2]3]", 1), mv), of(QueryException.class));
    }

    @SuppressWarnings("unchecked")
    public static class LimbTattoosCountExtractor extends ValueExtractor {
        @Override
        public void extract(Object target, Object arguments, final ValueCollector collector) {
            Integer parsedId = Integer.parseInt(((String) (arguments)));
            if (target instanceof ComplexTestDataStructure.Person) {
                Integer size = ((ComplexTestDataStructure.Person) (target)).limbs_list.get(parsedId).tattoos_list.size();
                collector.addObject(size);
            } else {
                ValueReader reader = ((ValueReader) (target));
                reader.read((("limbs_portable[" + parsedId) + "].tattoos_portable"), new ValueCallback() {
                    @Override
                    public void onResult(Object value) {
                        collector.addObject(((String[]) (value)).length);
                    }
                });
            }
        }
    }
}

