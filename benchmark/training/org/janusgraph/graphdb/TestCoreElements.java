/**
 * Copyright 2017 JanusGraph Authors
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
package org.janusgraph.graphdb;


import Cardinality.LIST;
import Cardinality.SET;
import Cardinality.SINGLE;
import Direction.BOTH;
import Direction.OUT;
import Multiplicity.MANY2ONE;
import Multiplicity.MULTI;
import Multiplicity.ONE2ONE;
import Multiplicity.SIMPLE;
import Order.ASC;
import Order.DESC;
import org.janusgraph.core.Multiplicity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests classes, enums and other non-interfaces in the core package
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class TestCoreElements {
    @Test
    public void testMultiplicityCardinality() {
        Assertions.assertEquals(MULTI, Multiplicity.convert(LIST));
        Assertions.assertEquals(SIMPLE, Multiplicity.convert(SET));
        Assertions.assertEquals(MANY2ONE, Multiplicity.convert(SINGLE));
        Assertions.assertEquals(MULTI.getCardinality(), LIST);
        Assertions.assertEquals(SIMPLE.getCardinality(), SET);
        Assertions.assertEquals(MANY2ONE.getCardinality(), SINGLE);
        Assertions.assertFalse(MULTI.isConstrained());
        Assertions.assertTrue(SIMPLE.isConstrained());
        Assertions.assertTrue(ONE2ONE.isConstrained());
        Assertions.assertTrue(ONE2ONE.isConstrained(BOTH));
        Assertions.assertTrue(SIMPLE.isConstrained(BOTH));
        Assertions.assertFalse(MULTI.isUnique(OUT));
        Assertions.assertTrue(MANY2ONE.isUnique(OUT));
    }

    @Test
    public void testOrder() {
        Assertions.assertTrue(((ASC.modulateNaturalOrder("A".compareTo("B"))) < 0));
        Assertions.assertTrue(((DESC.modulateNaturalOrder("A".compareTo("B"))) > 0));
    }
}

