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

import static com.hazelcast.query.impl.extractor.AbstractExtractionSpecification.Expected.empty;
import static com.hazelcast.query.impl.extractor.AbstractExtractionSpecification.Expected.of;


/**
 * Specification test that verifies the behavior of corner-cases extraction in arrays and collections.
 * <p/>
 * Extraction mechanism: IN-BUILT REFLECTION EXTRACTION
 * <p/>
 * This test is parametrised on two axes (see the parametrisationData() method):
 * - in memory format
 * - indexing
 * - extraction in collections and arrays
 */
@RunWith(Parameterized.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ExtractionInCollectionSpecTest extends AbstractExtractionTest {
    private static final ComplexTestDataStructure.Person BOND = ComplexTestDataStructure.person("Bond", ComplexTestDataStructure.limb("left-hand", ComplexTestDataStructure.tattoos(), ComplexTestDataStructure.finger("thumb"), ComplexTestDataStructure.finger(null)), ComplexTestDataStructure.limb("right-hand", ComplexTestDataStructure.tattoos("knife"), ComplexTestDataStructure.finger("middle"), ComplexTestDataStructure.finger("index")));

    private static final ComplexTestDataStructure.Person KRUEGER = ComplexTestDataStructure.person("Krueger", ComplexTestDataStructure.limb("linke-hand", ComplexTestDataStructure.tattoos("bratwurst"), ComplexTestDataStructure.finger("Zeigefinger"), ComplexTestDataStructure.finger("Mittelfinger")), ComplexTestDataStructure.limb("rechte-hand", ComplexTestDataStructure.tattoos(), ComplexTestDataStructure.finger("Ringfinger"), ComplexTestDataStructure.finger("Daumen")));

    private static final ComplexTestDataStructure.Person HUNT_NULL_TATTOOS = ComplexTestDataStructure.person("Hunt", ComplexTestDataStructure.limb("left", null, new ComplexTestDataStructure.Finger[]{  }));

    private static final ComplexTestDataStructure.Person HUNT_NULL_TATTOO_IN_ARRAY = ComplexTestDataStructure.person("Hunt", ComplexTestDataStructure.limb("left", ComplexTestDataStructure.tattoos(null, "cross"), new ComplexTestDataStructure.Finger[]{  }));

    private static final ComplexTestDataStructure.Person HUNT_NULL_LIMB = ComplexTestDataStructure.person("Hunt");

    private static final ComplexTestDataStructure.Person HUNT_NULL_FIRST = ComplexTestDataStructure.person("Hunt", null, ComplexTestDataStructure.limb("left", ComplexTestDataStructure.tattoos(null, "cross"), null, ComplexTestDataStructure.finger("thumbie")));

    private static final ComplexTestDataStructure.Person HUNT_PRIMITIVE_NULL_FIRST = ComplexTestDataStructure.person("Hunt", ComplexTestDataStructure.limb("left", ComplexTestDataStructure.tattoos(null, "cross"), ComplexTestDataStructure.finger("thumbie")));

    private static final ComplexTestDataStructure.Person HUNT_NO_NULL_FIRST = ComplexTestDataStructure.person("Hunt", ComplexTestDataStructure.limb("left", ComplexTestDataStructure.tattoos("cross"), ComplexTestDataStructure.finger("thumbie")));

    public ExtractionInCollectionSpecTest(InMemoryFormat inMemoryFormat, AbstractExtractionSpecification.Index index, AbstractExtractionSpecification.Multivalue multivalue) {
        super(inMemoryFormat, index, multivalue);
    }

    @Test
    public void notComparable_returned() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[1].fingers_", "knife"), mv), AbstractExtractionSpecification.Expected.of(IllegalArgumentException.class));
    }

    @Test
    public void indexOutOfBound_comparedToNull() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[100].tattoos_[1]", null), mv), of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER));
    }

    @Test
    public void indexOutOfBound() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[100].tattoos_[1]", "knife"), mv), empty());
    }

    @Test
    public void indexOutOfBound_negative() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[-1].tattoos_[1]", "knife"), mv), of(QueryException.class));
    }

    @Test
    public void indexOutOfBound_nullCollection_comparedToNull() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.HUNT_NULL_LIMB), AbstractExtractionSpecification.Query.of(equal("limbs_[100].tattoos_[1]", null), mv), of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.HUNT_NULL_LIMB));
    }

    @Test
    public void indexOutOfBound_nullCollection() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.HUNT_NULL_LIMB), AbstractExtractionSpecification.Query.of(equal("limbs_[100].tattoos_[1]", "knife"), mv), empty());
    }

    @Test
    public void indexOutOfBound_nullCollection_negative() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.HUNT_NULL_LIMB), AbstractExtractionSpecification.Query.of(equal("limbs_[-1].tattoos_[1]", "knife"), mv), of(QueryException.class));
    }

    @Test
    public void indexOutOfBound_atLeaf_comparedToNull() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[0].tattoos_[100]", null), mv), of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER));
    }

    @Test
    public void indexOutOfBound_atLeaf() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[0].tattoos_[100]", "knife"), mv), empty());
    }

    @Test
    public void indexOutOfBound_negative_atLeaf() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[0].tattoos_[-1]", "knife"), mv), of(QueryException.class));
    }

    @Test
    public void indexOutOfBound_nullCollection_atLeaf_comparedToNull() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.HUNT_NULL_TATTOOS), AbstractExtractionSpecification.Query.of(equal("limbs_[0].tattoos_[100]", null), mv), of(ExtractionInCollectionSpecTest.HUNT_NULL_TATTOOS));
    }

    @Test
    public void indexOutOfBound_nullCollection_atLeaf() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.HUNT_NULL_TATTOOS), AbstractExtractionSpecification.Query.of(equal("limbs_[0].tattoos_[100]", "knife"), mv), empty());
    }

    @Test
    public void indexOutOfBound_nullCollection_negative_atLeaf() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.HUNT_NULL_TATTOOS), AbstractExtractionSpecification.Query.of(equal("limbs_[0].tattoos_[-1]", "knife"), mv), of(QueryException.class));
    }

    @Test
    public void indexOutOfBound_atLeaf_notExistingPropertyOnPrimitiveField() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[0].tattoos_[100].asdfas", "knife"), mv), of(QueryException.class));
    }

    @Test
    public void indexOutOfBound_nullCollection_reduced_comparedToNull() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.HUNT_NULL_LIMB), AbstractExtractionSpecification.Query.of(equal("limbs_[any].tattoos_[1]", null), mv), of(ExtractionInCollectionSpecTest.HUNT_NULL_LIMB));
    }

    @Test
    public void indexOutOfBound_nullCollection_reduced() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.HUNT_NULL_LIMB), AbstractExtractionSpecification.Query.of(equal("limbs_[any].tattoos_[1]", "knife"), mv), empty());
    }

    @Test
    public void indexOutOfBound_nullCollection_reduced_atLeaf_comparedToNull() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.HUNT_NULL_TATTOOS), AbstractExtractionSpecification.Query.of(equal("limbs_[0].tattoos_[any]", null), mv), of(ExtractionInCollectionSpecTest.HUNT_NULL_TATTOOS));
    }

    @Test
    public void indexOutOfBound_nullCollection_reduced_atLeaf() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.HUNT_NULL_TATTOOS), AbstractExtractionSpecification.Query.of(equal("limbs_[0].tattoos_[any]", "knife"), mv), empty());
    }

    @Test
    public void emptyCollection_reduced_atLeaf() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.HUNT_NULL_TATTOOS), AbstractExtractionSpecification.Query.of(equal("limbs_[0].fingers_[any]", null), mv), of(ExtractionInCollectionSpecTest.HUNT_NULL_TATTOOS));
    }

    @Test
    public void comparable_notPrimitive() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[0].fingers_[0]", ComplexTestDataStructure.finger("thumb")), mv), of(ExtractionInCollectionSpecTest.BOND));
    }

    @Test
    public void comparable_notPrimitive_reduced() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[any].fingers_[0]", ComplexTestDataStructure.finger("thumb")), mv), of(ExtractionInCollectionSpecTest.BOND));
    }

    @Test
    public void comparable_primitive() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[0].fingers_[0].name", "thumb"), mv), of(ExtractionInCollectionSpecTest.BOND));
    }

    @Test
    public void comparable_primitive_reduced() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[any].fingers_[any].name", "thumb"), mv), of(ExtractionInCollectionSpecTest.BOND));
    }

    @Test
    public void comparable_primitive_comparedToNull() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[0].fingers_[0].name", null), mv), empty());
    }

    @Test
    public void comparable_notPrimitive_comparedToNull() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[0].fingers_[0]", null), mv), empty());
    }

    @Test
    public void comparable_primitive_comparedToNull_matching() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[0].fingers_[1].name", null), mv), of(ExtractionInCollectionSpecTest.BOND));
    }

    @Test
    public void comparable_primitive_comparedToNull_reduced_matching() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[0].fingers_[any].name", null), mv), of(ExtractionInCollectionSpecTest.BOND));
    }

    @Test
    public void comparable_primitive_reduced_comparedToNull_matching() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[any].fingers_[1].name", null), mv), of(ExtractionInCollectionSpecTest.BOND));
    }

    @Test
    public void comparable_primitive_reduced_attribute_comparedToNull_matching2() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[any].fingers_[any].name", null), mv), of(ExtractionInCollectionSpecTest.BOND));
    }

    @Test
    public void comparable_primitive_reduced_comparedToNull_matching2() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[any].fingers_[any]", null), mv), empty());
    }

    @Test
    public void comparable_primitive_reduced_atLeaf_comparedToNull_matching() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER), AbstractExtractionSpecification.Query.of(equal("limbs_[any].tattoos_[any]", null), mv), of(ExtractionInCollectionSpecTest.BOND, ExtractionInCollectionSpecTest.KRUEGER));
    }

    @Test
    public void github8134_firstNonNull_string() {
        ComplexTestDataStructure.Person carlos = ComplexTestDataStructure.person("Carlos", ComplexTestDataStructure.limb("l", null), ComplexTestDataStructure.limb(null, null));
        execute(AbstractExtractionSpecification.Input.of(carlos), AbstractExtractionSpecification.Query.of(equal("limbs_[any].name", 'l'), mv), of(carlos));
    }

    @Test
    public void github8134_firstNull_string() {
        ComplexTestDataStructure.Person carlos = ComplexTestDataStructure.person("Carlos", ComplexTestDataStructure.limb(null, null), ComplexTestDataStructure.limb("l", null));
        execute(AbstractExtractionSpecification.Input.of(carlos), AbstractExtractionSpecification.Query.of(equal("limbs_[any].name", 'l'), mv), of(carlos));
    }

    @Test
    public void comparable_nullVsNoNullFirst_case1() {
        ignoreForPortable("Portables can't handle nulls in collection");
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.HUNT_NO_NULL_FIRST, ExtractionInCollectionSpecTest.HUNT_NULL_FIRST), AbstractExtractionSpecification.Query.of(equal("limbs_[any].tattoos_[any]", "cross"), mv), of(ExtractionInCollectionSpecTest.HUNT_NO_NULL_FIRST, ExtractionInCollectionSpecTest.HUNT_NULL_FIRST));
    }

    @Test
    public void comparable_nullVsNoNullFirst_case2() {
        ignoreForPortable("Portables can't handle nulls in collection");
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.HUNT_NO_NULL_FIRST, ExtractionInCollectionSpecTest.HUNT_NULL_FIRST), AbstractExtractionSpecification.Query.of(equal("limbs_[any].tattoos_[any]", null), mv), of(ExtractionInCollectionSpecTest.HUNT_NULL_FIRST));
    }

    @Test
    public void comparable_nullVsNoNullFirst_case3() {
        ignoreForPortable("Portables can't handle nulls in collection");
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.HUNT_NO_NULL_FIRST, ExtractionInCollectionSpecTest.HUNT_NULL_FIRST), AbstractExtractionSpecification.Query.of(equal("limbs_[any].fingers_[any]", null), mv), of(ExtractionInCollectionSpecTest.HUNT_NULL_FIRST));
    }

    @Test
    public void comparable_nullVsNoNullFirst_case4() {
        ignoreForPortable("Portables can't handle nulls in collection");
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.HUNT_NO_NULL_FIRST, ExtractionInCollectionSpecTest.HUNT_NULL_FIRST), AbstractExtractionSpecification.Query.of(equal("limbs_[any].fingers_[any]", ComplexTestDataStructure.finger("thumbie")), mv), of(ExtractionInCollectionSpecTest.HUNT_NO_NULL_FIRST, ExtractionInCollectionSpecTest.HUNT_NULL_FIRST));
    }

    @Test
    public void comparable_nullVsNoNullFirst_case5() {
        ignoreForPortable("Portables can't handle nulls in collection");
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.HUNT_NO_NULL_FIRST, ExtractionInCollectionSpecTest.HUNT_NULL_FIRST), AbstractExtractionSpecification.Query.of(equal("limbs_[any].fingers_[any].name", "thumbie"), mv), of(ExtractionInCollectionSpecTest.HUNT_NO_NULL_FIRST, ExtractionInCollectionSpecTest.HUNT_NULL_FIRST));
    }

    @Test
    public void comparable_nullVsNoNullFirst_case6() {
        ignoreForPortable("Portables can't handle nulls in collection");
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.HUNT_NO_NULL_FIRST, ExtractionInCollectionSpecTest.HUNT_NULL_FIRST), AbstractExtractionSpecification.Query.of(equal("limbs_[any].fingers_[any]", null), mv), of(ExtractionInCollectionSpecTest.HUNT_NULL_FIRST));
    }

    @Test
    public void comparable_nullVsNoNullFirst_case7() {
        ignoreForPortable("Portables can't handle nulls in collection");
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.HUNT_NO_NULL_FIRST, ExtractionInCollectionSpecTest.HUNT_NULL_FIRST), AbstractExtractionSpecification.Query.of(equal("limbs_[any].fingers_[any]", ComplexTestDataStructure.finger("thumbie")), mv), of(ExtractionInCollectionSpecTest.HUNT_NO_NULL_FIRST, ExtractionInCollectionSpecTest.HUNT_NULL_FIRST));
    }

    @Test
    public void comparable_nullVsNoNullFirst_case8() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.HUNT_NO_NULL_FIRST, ExtractionInCollectionSpecTest.HUNT_PRIMITIVE_NULL_FIRST), AbstractExtractionSpecification.Query.of(equal("limbs_[any].fingers_[any]", ComplexTestDataStructure.finger("thumbie")), mv), of(ExtractionInCollectionSpecTest.HUNT_NO_NULL_FIRST, ExtractionInCollectionSpecTest.HUNT_PRIMITIVE_NULL_FIRST));
    }

    @Test
    public void comparable_primitive_notReduced_null_inside() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.HUNT_NULL_TATTOO_IN_ARRAY), AbstractExtractionSpecification.Query.of(equal("limbs_[0].tattoos_[1]", "cross"), mv), of(ExtractionInCollectionSpecTest.HUNT_NULL_TATTOO_IN_ARRAY));
    }

    @Test
    public void comparable_primitive_reduced_null_inside() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInCollectionSpecTest.HUNT_NULL_TATTOO_IN_ARRAY, ExtractionInCollectionSpecTest.HUNT_NO_NULL_FIRST), AbstractExtractionSpecification.Query.of(equal("limbs_[any].tattoos_[any]", "cross"), mv), of(ExtractionInCollectionSpecTest.HUNT_NULL_TATTOO_IN_ARRAY, ExtractionInCollectionSpecTest.HUNT_NO_NULL_FIRST));
    }
}

