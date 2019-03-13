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
import com.hazelcast.query.QueryException;
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
 * Specification test that verifies the behavior of corner-cases extraction in arrays ONLY.
 * <p/>
 * Extraction mechanism: IN-BUILT REFLECTION EXTRACTION
 * <p/>
 * This test is parametrised on two axes (see the parametrisationData() method):
 * - in memory format
 * - indexing
 */
@RunWith(Parameterized.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ExtractionInArraySpecTest extends AbstractExtractionTest {
    private static final ComplexTestDataStructure.Person BOND = ComplexTestDataStructure.person("Bond", ComplexTestDataStructure.limb("left-hand", ComplexTestDataStructure.tattoos(), ComplexTestDataStructure.finger("thumb"), ComplexTestDataStructure.finger(null)), ComplexTestDataStructure.limb("right-hand", ComplexTestDataStructure.tattoos("knife"), ComplexTestDataStructure.finger("middle"), ComplexTestDataStructure.finger("index")));

    private static final ComplexTestDataStructure.Person KRUEGER = ComplexTestDataStructure.person("Krueger", ComplexTestDataStructure.limb("linke-hand", ComplexTestDataStructure.tattoos("bratwurst"), ComplexTestDataStructure.finger("Zeigefinger"), ComplexTestDataStructure.finger("Mittelfinger")), ComplexTestDataStructure.limb("rechte-hand", ComplexTestDataStructure.tattoos(), ComplexTestDataStructure.finger("Ringfinger"), ComplexTestDataStructure.finger("Daumen")));

    private static final ComplexTestDataStructure.Person HUNT_NULL_TATTOOS = ComplexTestDataStructure.person("Hunt", ComplexTestDataStructure.limb("left", null, new ComplexTestDataStructure.Finger[]{  }));

    private static final ComplexTestDataStructure.Person HUNT_NULL_LIMB = ComplexTestDataStructure.person("Hunt");

    public ExtractionInArraySpecTest(InMemoryFormat inMemoryFormat, AbstractExtractionSpecification.Index index, AbstractExtractionSpecification.Multivalue multivalue) {
        super(inMemoryFormat, index, multivalue);
    }

    @Test
    public void indexOutOfBound_notExistingProperty() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInArraySpecTest.BOND, ExtractionInArraySpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[100].sdafasdf", "knife"), mv), of(QueryException.class));
    }

    @Test
    public void indexOutOfBound_notExistingProperty_notAtLeaf() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInArraySpecTest.BOND, ExtractionInArraySpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[100].sdafasdf.zxcvzxcv", "knife"), mv), of(QueryException.class));
    }

    @Test
    public void indexOutOfBound_atLeaf_notExistingProperty() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInArraySpecTest.BOND, ExtractionInArraySpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[0].tattoos_[100].asdfas", "knife"), mv), of(QueryException.class));
    }
}

