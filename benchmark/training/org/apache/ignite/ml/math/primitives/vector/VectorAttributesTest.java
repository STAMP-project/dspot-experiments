/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.ml.math.primitives.vector;


import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.apache.ignite.ml.math.primitives.matrix.impl.DenseMatrix;
import org.apache.ignite.ml.math.primitives.vector.impl.DenseVector;
import org.apache.ignite.ml.math.primitives.vector.impl.SparseVector;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class VectorAttributesTest {
    /**
     *
     */
    private final List<VectorAttributesTest.AttrCfg> attrCfgs = // IMPL NOTE this is somewhat artificial
    Arrays.asList(new VectorAttributesTest.AttrCfg("isDense", Vector::isDense, DenseVector.class), new VectorAttributesTest.AttrCfg("isArrayBased", Vector::isArrayBased, DenseVector.class), new VectorAttributesTest.AttrCfg("isSequentialAccess", Vector::isSequentialAccess, DenseVector.class, VectorAttributesTest.SparseLocalVectorSequentialAccess.class), new VectorAttributesTest.AttrCfg("guidNotNull", ( v) -> (v.guid()) == null), new VectorAttributesTest.AttrCfg("isRandomAccess", Vector::isRandomAccess, DenseVector.class, VectorAttributesTest.SparseLocalVectorSequentialAccess.class, VectorAttributesTest.SparseLocalVectorRandomAccess.class), new VectorAttributesTest.AttrCfg("isDistributed", Vector::isDistributed));

    /**
     *
     */
    private final List<VectorAttributesTest.Specification> specFixture = Arrays.asList(new VectorAttributesTest.Specification(new DenseVector(1)), new VectorAttributesTest.Specification(new org.apache.ignite.ml.math.primitives.vector.impl.DelegatingVector(new DenseVector(1)), DenseVector.class, "isDense", "isArrayBased", "isSequentialAccess", "isRandomAccess", "isDistributed"), new VectorAttributesTest.Specification(new VectorAttributesTest.SparseLocalVectorSequentialAccess(1)), new VectorAttributesTest.Specification(new VectorAttributesTest.SparseLocalVectorRandomAccess(1)), new VectorAttributesTest.Specification(new org.apache.ignite.ml.math.primitives.vector.impl.VectorizedViewMatrix(new DenseMatrix(1, 1), 0, 0, 1, 1), DenseVector.class, "isDense", "isRandomAccess", "isDistributed"));// TODO: IGNITE-5723, find out why "isSequentialAccess" fails here


    /**
     *
     */
    @Test
    public void isDenseTest() {
        assertAttribute("isDense");
    }

    /**
     *
     */
    @Test
    public void isArrayBasedTest() {
        assertAttribute("isArrayBased");
    }

    /**
     *
     */
    @Test
    public void isSequentialAccessTest() {
        assertAttribute("isSequentialAccess");
    }

    /**
     *
     */
    @Test
    public void guidTest() {
        assertAttribute("guidNotNull");
    }

    /**
     *
     */
    @Test
    public void isRandomAccessTest() {
        assertAttribute("isRandomAccess");
    }

    /**
     *
     */
    @Test
    public void isDistributedTest() {
        assertAttribute("isDistributed");
    }

    /**
     * See http://en.wikipedia.org/wiki/Specification_pattern
     */
    private static class Specification {
        /**
         *
         */
        private final Vector v;

        /**
         *
         */
        private final Class<? extends Vector> underlyingType;

        /**
         *
         */
        private final List<String> attrsFromUnderlying;

        /**
         *
         */
        final String desc;

        /**
         *
         */
        Specification(Vector v, Class<? extends Vector> underlyingType, String... attrsFromUnderlying) {
            this.v = v;
            this.underlyingType = underlyingType;
            this.attrsFromUnderlying = Arrays.asList(attrsFromUnderlying);
            final Class<? extends Vector> clazz = v.getClass();
            desc = (clazz.getSimpleName()) + (clazz.equals(underlyingType) ? "" : (" (underlying type " + (underlyingType.getSimpleName())) + ")");
        }

        /**
         *
         */
        Specification(Vector v) {
            this(v, v.getClass());
        }

        /**
         *
         */
        void verify(VectorAttributesTest.AttrCfg attr) {
            final boolean obtained = attr.obtain.apply(v);
            final Class<? extends Vector> typeToCheck = (attrsFromUnderlying.contains(attr.name)) ? underlyingType : v.getClass();
            final boolean exp = attr.trueInTypes.contains(typeToCheck);
            Assert.assertEquals(((("Unexpected " + (attr.name)) + " value for ") + (desc)), exp, obtained);
        }
    }

    /**
     *
     */
    private static class AttrCfg {
        /**
         *
         */
        final String name;

        /**
         *
         */
        final Function<Vector, Boolean> obtain;

        /**
         *
         */
        final List<Class> trueInTypes;

        /**
         *
         */
        AttrCfg(String name, Function<Vector, Boolean> obtain, Class... trueInTypes) {
            this.name = name;
            this.obtain = obtain;
            this.trueInTypes = Arrays.asList(trueInTypes);
        }
    }

    /**
     *
     */
    private static class SparseLocalVectorSequentialAccess extends SparseVector {
        /**
         *
         */
        public SparseLocalVectorSequentialAccess() {
            // No-op.
        }

        /**
         *
         */
        SparseLocalVectorSequentialAccess(int size) {
            super(size, SEQUENTIAL_ACCESS_MODE);
        }
    }

    /**
     *
     */
    private static class SparseLocalVectorRandomAccess extends SparseVector {
        /**
         *
         */
        public SparseLocalVectorRandomAccess() {
            // No-op.
        }

        /**
         *
         */
        SparseLocalVectorRandomAccess(int size) {
            super(size, RANDOM_ACCESS_MODE);
        }
    }
}

