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
import com.hazelcast.query.impl.extractor.AbstractExtractionSpecification;
import com.hazelcast.query.impl.extractor.AbstractExtractionTest;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.hazelcast.query.impl.extractor.AbstractExtractionSpecification.Expected.empty;
import static com.hazelcast.query.impl.extractor.AbstractExtractionSpecification.Expected.of;


/**
 * Specification test that verifies the behavior of corner-cases extraction in collections ONLY.
 * <p/>
 * Extraction mechanism: IN-BUILT REFLECTION EXTRACTION
 * <p/>
 * This test is parametrised on two axes (see the parametrisationData() method):
 * - in memory format
 * - indexing
 */
@RunWith(Parameterized.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ExtractionInListSpecTest extends AbstractExtractionTest {
    private static final ComplexTestDataStructure.Person BOND = ComplexTestDataStructure.person("Bond", ComplexTestDataStructure.limb("left-hand", ComplexTestDataStructure.tattoos(), ComplexTestDataStructure.finger("thumb"), ComplexTestDataStructure.finger(null)), ComplexTestDataStructure.limb("right-hand", ComplexTestDataStructure.tattoos("knife"), ComplexTestDataStructure.finger("middle"), ComplexTestDataStructure.finger("index")));

    private static final ComplexTestDataStructure.Person KRUEGER = ComplexTestDataStructure.person("Krueger", ComplexTestDataStructure.limb("linke-hand", ComplexTestDataStructure.tattoos("bratwurst"), ComplexTestDataStructure.finger("Zeigefinger"), ComplexTestDataStructure.finger("Mittelfinger")), ComplexTestDataStructure.limb("rechte-hand", ComplexTestDataStructure.tattoos(), ComplexTestDataStructure.finger("Ringfinger"), ComplexTestDataStructure.finger("Daumen")));

    private static final ComplexTestDataStructure.Person HUNT_NULL_TATTOOS = ComplexTestDataStructure.person("Hunt", ComplexTestDataStructure.limb("left", null, new ComplexTestDataStructure.Finger[]{  }));

    private static final ComplexTestDataStructure.Person HUNT_NULL_LIMB = ComplexTestDataStructure.person("Hunt");

    public ExtractionInListSpecTest(InMemoryFormat inMemoryFormat, AbstractExtractionSpecification.Index index, AbstractExtractionSpecification.Multivalue multivalue) {
        super(inMemoryFormat, index, multivalue);
    }

    @Test
    public void size_property() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInListSpecTest.BOND, ExtractionInListSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(Predicates.equal("limbs_.size", 2), mv), of(ExtractionInListSpecTest.BOND, ExtractionInListSpecTest.KRUEGER));
    }

    @Test
    public void size_property_atLeaf() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInListSpecTest.BOND, ExtractionInListSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(Predicates.equal("limbs_[0].tattoos_.size", 1), mv), of(ExtractionInListSpecTest.KRUEGER));
    }

    @Test
    public void null_collection_size() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInListSpecTest.HUNT_NULL_LIMB), AbstractExtractionSpecification.Query.of(Predicates.equal("limbs_[0].fingers_.size", 1), mv), empty());
    }

    @Test
    public void null_collection_size_compared_to_null() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInListSpecTest.HUNT_NULL_LIMB), AbstractExtractionSpecification.Query.of(Predicates.equal("limbs_[0].fingers_.size", null), mv), of(ExtractionInListSpecTest.HUNT_NULL_LIMB));
    }

    @Test
    public void null_collection_size_reduced() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInListSpecTest.HUNT_NULL_LIMB), AbstractExtractionSpecification.Query.of(Predicates.equal("limbs_[any].fingers_.size", 1), mv), empty());
    }

    @Test
    public void null_collection_size_reduced_compared_to_null() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInListSpecTest.HUNT_NULL_LIMB), AbstractExtractionSpecification.Query.of(Predicates.equal("limbs_[any].fingers_.size", null), mv), of(ExtractionInListSpecTest.HUNT_NULL_LIMB));
    }

    @Test
    public void null_collection_size_atLeaf() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInListSpecTest.HUNT_NULL_TATTOOS), AbstractExtractionSpecification.Query.of(Predicates.equal("limbs_[0].tattoos_.size", 1), mv), empty());
    }

    @Test
    public void null_collection_size_atLeaf_compared_to_null() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInListSpecTest.HUNT_NULL_TATTOOS), AbstractExtractionSpecification.Query.of(Predicates.equal("limbs_[0].tattoos_.size", null), mv), of(ExtractionInListSpecTest.HUNT_NULL_TATTOOS));
    }

    @Test
    public void null_collection_size_atLeaf_reduced() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInListSpecTest.HUNT_NULL_TATTOOS), AbstractExtractionSpecification.Query.of(Predicates.equal("limbs_[any].tattoos_.size", 1), mv), empty());
    }

    @Test
    public void null_collection_size_atLeaf_reduced_compared_to_null() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInListSpecTest.HUNT_NULL_TATTOOS), AbstractExtractionSpecification.Query.of(Predicates.equal("limbs_[any].tattoos_.size", null), mv), of(ExtractionInListSpecTest.HUNT_NULL_TATTOOS));
    }

    @Test
    public void indexOutOfBound_notExistingProperty() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInListSpecTest.BOND, ExtractionInListSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[100].sdafasdf", "knife"), mv), of(QueryException.class));
    }

    @Test
    public void indexOutOfBound_notExistingProperty_notAtLeaf() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInListSpecTest.BOND, ExtractionInListSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[100].sdafasdf.zxcvzxcv", "knife"), mv), of(QueryException.class));
    }

    @Test
    public void indexOutOfBound_atLeaf_notExistingProperty() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInListSpecTest.BOND, ExtractionInListSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[0].fingers_[100].asdfas", "knife"), mv), of(QueryException.class));
    }
}

