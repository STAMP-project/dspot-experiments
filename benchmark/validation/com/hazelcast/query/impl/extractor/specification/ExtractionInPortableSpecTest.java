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
import com.hazelcast.query.impl.extractor.AbstractExtractionSpecification;
import com.hazelcast.query.impl.extractor.AbstractExtractionTest;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.hazelcast.query.impl.extractor.AbstractExtractionSpecification.Expected.empty;
import static com.hazelcast.query.impl.extractor.AbstractExtractionSpecification.Query.of;


/**
 * Specification test that verifies the behavior of corner-cases extraction in single-value attributes.
 * It's a detailed test especially for portables, since the extraction is much more complex there.
 * <p/>
 * Extraction mechanism: IN-BUILT REFLECTION EXTRACTION
 * <p/>
 * This test is parametrised on two axes (see the parametrisationData() method):
 * - in memory format
 * - indexing
 */
@RunWith(Parameterized.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ExtractionInPortableSpecTest extends AbstractExtractionTest {
    private static final ComplexTestDataStructure.Person BOND = ComplexTestDataStructure.person("Bond", ComplexTestDataStructure.limb("left-hand", ComplexTestDataStructure.tattoos(), ComplexTestDataStructure.finger("thumb"), ComplexTestDataStructure.finger(null)), ComplexTestDataStructure.limb("right-hand", ComplexTestDataStructure.tattoos("knife"), ComplexTestDataStructure.finger("middle"), ComplexTestDataStructure.finger("index")));

    private static final ComplexTestDataStructure.Person KRUEGER = ComplexTestDataStructure.person("Krueger", ComplexTestDataStructure.limb("linke-hand", ComplexTestDataStructure.tattoos("bratwurst"), ComplexTestDataStructure.finger("Zeigefinger"), ComplexTestDataStructure.finger("Mittelfinger")), ComplexTestDataStructure.limb("rechte-hand", ComplexTestDataStructure.tattoos(), ComplexTestDataStructure.finger("Ringfinger"), ComplexTestDataStructure.finger("Daumen")));

    private static final ComplexTestDataStructure.Person HUNT_WITH_NULLS = ComplexTestDataStructure.person(null, ComplexTestDataStructure.limb(null, new ArrayList<String>(), new ComplexTestDataStructure.Finger[]{  }));

    public ExtractionInPortableSpecTest(InMemoryFormat inMemoryFormat, AbstractExtractionSpecification.Index index, AbstractExtractionSpecification.Multivalue multivalue) {
        super(inMemoryFormat, index, multivalue);
    }

    @Test
    public void wrong_attribute_name_atLeaf() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND, ExtractionInPortableSpecTest.KRUEGER, ExtractionInPortableSpecTest.HUNT_WITH_NULLS), of(Predicates.equal("name12312", "Bond"), mv), empty());
    }

    @Test
    public void wrong_attribute_name_atLeaf_comparedToNull() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND, ExtractionInPortableSpecTest.KRUEGER, ExtractionInPortableSpecTest.HUNT_WITH_NULLS), of(Predicates.equal("name12312", null), mv), AbstractExtractionSpecification.Expected.of(ExtractionInPortableSpecTest.BOND, ExtractionInPortableSpecTest.KRUEGER, ExtractionInPortableSpecTest.HUNT_WITH_NULLS));
    }

    @Test
    public void nested_wrong_attribute_name_atLeaf() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND, ExtractionInPortableSpecTest.KRUEGER), of(Predicates.equal("firstLimb.name12312", "left-hand"), mv), empty());
    }

    @Test
    public void nested_wrong_attribute_name_atLeaf_comparedToNull() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND, ExtractionInPortableSpecTest.KRUEGER), of(Predicates.equal("firstLimb.name12312", null), mv), AbstractExtractionSpecification.Expected.of(ExtractionInPortableSpecTest.BOND, ExtractionInPortableSpecTest.KRUEGER));
    }

    @Test
    public void indexOutOfBound_notExistingProperty() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND, ExtractionInPortableSpecTest.KRUEGER), of(equal("limbs_[100].sdafasdf", "knife"), mv), empty());
    }

    @Test
    public void indexOutOfBound_atLeaf_notExistingProperty() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND, ExtractionInPortableSpecTest.KRUEGER), of(equal("limbs_[0].fingers_[100].asdfas", "knife"), mv), empty());
    }

    @Test
    public void wrong_attribute_name_compared_to_null() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND, ExtractionInPortableSpecTest.KRUEGER, ExtractionInPortableSpecTest.HUNT_WITH_NULLS), of(Predicates.equal("name12312", null), mv), AbstractExtractionSpecification.Expected.of(ExtractionInPortableSpecTest.BOND, ExtractionInPortableSpecTest.KRUEGER, ExtractionInPortableSpecTest.HUNT_WITH_NULLS));
    }

    @Test
    public void primitiveNull_comparedToNull_matching() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND, ExtractionInPortableSpecTest.KRUEGER, ExtractionInPortableSpecTest.HUNT_WITH_NULLS), of(Predicates.equal("name", null), mv), AbstractExtractionSpecification.Expected.of(ExtractionInPortableSpecTest.HUNT_WITH_NULLS));
    }

    @Test
    public void primitiveNull_comparedToNotNull_notMatching() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND, ExtractionInPortableSpecTest.KRUEGER, ExtractionInPortableSpecTest.HUNT_WITH_NULLS), of(Predicates.equal("name", "Non-null-value"), mv), empty());
    }

    @Test
    public void nestedAttribute_firstIsNull_comparedToNotNull() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND, ExtractionInPortableSpecTest.KRUEGER, ExtractionInPortableSpecTest.HUNT_WITH_NULLS), of(Predicates.equal("secondLimb.name", "Non-null-value"), mv), empty());
    }

    @Test
    public void nestedAttribute_firstIsNull_comparedToNull() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND, ExtractionInPortableSpecTest.KRUEGER, ExtractionInPortableSpecTest.HUNT_WITH_NULLS), of(Predicates.equal("secondLimb.name", null), mv), AbstractExtractionSpecification.Expected.of(ExtractionInPortableSpecTest.HUNT_WITH_NULLS));
    }

    @Test
    public void correct_attribute_name() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND), of(Predicates.equal("name", "Bond"), mv), AbstractExtractionSpecification.Expected.of(ExtractionInPortableSpecTest.BOND));
    }

    @Test
    public void correct_nestedAttribute_name() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND), of(Predicates.equal("firstLimb.name", "left-hand"), mv), AbstractExtractionSpecification.Expected.of(ExtractionInPortableSpecTest.BOND));
    }

    @Test
    public void correct_portableAttribute() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND), of(Predicates.equal("firstLimb", ExtractionInPortableSpecTest.BOND.firstLimb.getPortable()), mv), AbstractExtractionSpecification.Expected.of(ExtractionInPortableSpecTest.BOND));
    }

    @Test
    public void correct_portableArrayInTheMiddle_matching() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND), of(Predicates.equal("limbs_[0].name", "left-hand"), mv), AbstractExtractionSpecification.Expected.of(ExtractionInPortableSpecTest.BOND));
    }

    @Test
    public void correct_portableArrayInTheMiddle_notMatching() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND), of(Predicates.equal("limbs_[0].name", "dasdfasdfasdf"), mv), empty());
    }

    @Test
    public void correct_portableInTheMiddle_portableAtTheEnd_matching() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND), of(Predicates.equal("firstLimb.fingers_[0]", ExtractionInPortableSpecTest.BOND.firstLimb.fingers_array[0].getPortable()), mv), AbstractExtractionSpecification.Expected.of(ExtractionInPortableSpecTest.BOND));
    }

    @Test
    public void correct_portableInTheMiddle_portableAtTheEnd_notMatching() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND), of(Predicates.equal("firstLimb.fingers_[0]", ExtractionInPortableSpecTest.BOND.firstLimb.fingers_array[1].getPortable()), mv), empty());
    }

    @Test
    public void correct_portableInTheMiddle_portableArrayAtTheEnd_primitiveAttribute_notMatching() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND), of(Predicates.equal("firstLimb.fingers_[0].name", "thumb123"), mv), empty());
    }

    @Test
    public void correct_portableArrayInTheMiddle_portableAtTheEnd_notMatching() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND), of(Predicates.equal("limbs_[0].fingers_[0]", ExtractionInPortableSpecTest.BOND.firstLimb.fingers_array[1].getPortable()), mv), empty());
    }

    @Test
    public void correct_portableArrayInTheMiddle_portableArrayAtTheEnd_primitiveAttribute_notMatching() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND), of(Predicates.equal("limbs_[0].fingers_[0].name", "thumb123"), mv), empty());
    }

    @Test
    public void correct_portableArrayInTheMiddle_portableArrayAtTheEnd_primitiveAttribute_matching() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND), of(Predicates.equal("firstLimb.fingers_[0].name", "thumb"), mv), AbstractExtractionSpecification.Expected.of(ExtractionInPortableSpecTest.BOND));
    }

    @Test
    public void correct_portableArrayAtTheEnd_matching() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND), of(Predicates.equal("limbs_[0]", ExtractionInPortableSpecTest.BOND.limbs_array[0].getPortable()), mv), AbstractExtractionSpecification.Expected.of(ExtractionInPortableSpecTest.BOND));
    }

    @Test
    public void correct_portableArrayAtTheEnd_notMatching() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND), of(Predicates.equal("limbs_[1]", ExtractionInPortableSpecTest.BOND.limbs_array[0].getPortable()), mv), empty());
    }

    @Test
    public void correct_primitiveArrayAtTheEnd_matching() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND), of(Predicates.equal("secondLimb.tattoos_[0]", "knife"), mv), AbstractExtractionSpecification.Expected.of(ExtractionInPortableSpecTest.BOND));
    }

    @Test
    public void correct_primitiveArrayAtTheEnd_notMatching() {
        execute(AbstractExtractionSpecification.Input.of(ExtractionInPortableSpecTest.BOND), of(Predicates.equal("secondLimb.tattoos_[0]", "knife123"), mv), empty());
    }
}

