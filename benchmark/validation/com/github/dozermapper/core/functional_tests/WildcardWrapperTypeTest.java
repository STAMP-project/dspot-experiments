/**
 * Copyright 2005-2019 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dozermapper.core.functional_tests;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests to confirm that Dozer can map between primitive and wrapper types. E.g.
 * a {@code long} field with a getter returning {@code long} and a setter taking
 * {@code Long} should be automatically mapped by Dozer.
 */
public class WildcardWrapperTypeTest extends AbstractFunctionalTest {
    /**
     * Mapping should work without configuration when field is primitive and
     * setter takes a wrapper class.
     */
    @Test
    public void testCanMapPrimitiveFieldAndWrapperSetter() {
        WildcardWrapperTypeTest.PrimitiveFieldWrapperSetter source = new WildcardWrapperTypeTest.PrimitiveFieldWrapperSetter();
        source.setPrimitive(Long.MAX_VALUE);
        WildcardWrapperTypeTest.PrimitiveFieldWrapperSetter target = mapper.map(source, WildcardWrapperTypeTest.PrimitiveFieldWrapperSetter.class);
        Assert.assertThat(target.getPrimitive(), CoreMatchers.equalTo(Long.MAX_VALUE));
    }

    /**
     * Mapping should work without configuration when field is primitive and
     * getter returns a wrapper class.
     */
    @Test
    public void testCanMapPrimitiveFieldAndWrapperGetter() {
        WildcardWrapperTypeTest.PrimitiveFieldWrapperGetter source = new WildcardWrapperTypeTest.PrimitiveFieldWrapperGetter();
        source.setPrimitive(true);
        WildcardWrapperTypeTest.PrimitiveFieldWrapperGetter target = mapper.map(source, WildcardWrapperTypeTest.PrimitiveFieldWrapperGetter.class);
        Assert.assertThat(target.getPrimitive(), CoreMatchers.equalTo(true));
    }

    /**
     * Mapping should work without configuration when field is primitive and
     * both getter and setters are of wrapper type.
     */
    @Test
    public void testCanMapPrimitiveFieldAndWrapperSetterAndGetter() {
        WildcardWrapperTypeTest.PrimitiveFieldWrapperGetterAndSetter source = new WildcardWrapperTypeTest.PrimitiveFieldWrapperGetterAndSetter();
        source.setPrimitive(Integer.MIN_VALUE);
        WildcardWrapperTypeTest.PrimitiveFieldWrapperGetterAndSetter target = mapper.map(source, WildcardWrapperTypeTest.PrimitiveFieldWrapperGetterAndSetter.class);
        Assert.assertThat(target.getPrimitive(), CoreMatchers.equalTo(Integer.MIN_VALUE));
    }

    /**
     * Sanity check: Wrapper setters that do not autobox to the primitive field type
     * should be ignored. E.g. a setter taking {@code Character} should not be considered
     * a setter for a {@code float} field.
     */
    @Test
    public void testDoesNotMapPrimitiveFieldAndUnrelatedWrapperSetter() {
        WildcardWrapperTypeTest.PrimitiveFieldUnrelatedSetter source = new WildcardWrapperTypeTest.PrimitiveFieldUnrelatedSetter();
        source.setPrimitive(Boolean.FALSE);
        WildcardWrapperTypeTest.PrimitiveFieldUnrelatedSetter target = mapper.map(source, WildcardWrapperTypeTest.PrimitiveFieldUnrelatedSetter.class);
        Assert.assertThat(target.getPrimitive(), CoreMatchers.equalTo(0.0));
    }

    /**
     * Mapping should work without configuration when field is of wrapper type and
     * setter takes a primitive type.
     */
    @Test
    public void testCanMapWrapperFieldAndPrimitiveSetter() {
        WildcardWrapperTypeTest.WrapperFieldPrimitiveSetter source = new WildcardWrapperTypeTest.WrapperFieldPrimitiveSetter();
        source.setPrimitive(Float.MAX_VALUE);
        WildcardWrapperTypeTest.WrapperFieldPrimitiveSetter target = mapper.map(source, WildcardWrapperTypeTest.WrapperFieldPrimitiveSetter.class);
        Assert.assertThat(target.getPrimitive(), CoreMatchers.equalTo(Float.MAX_VALUE));
    }

    /**
     * Mappings should work even if the setter is non-void and takes a primitive while
     * the field is of wrapper type.
     */
    @Test
    public void testCanMapWrapperFieldAndNonVoidPrimitiveSetter() {
        WildcardWrapperTypeTest.WrapperFieldAndNonVoidPrimitiveSetter source = new WildcardWrapperTypeTest.WrapperFieldAndNonVoidPrimitiveSetter();
        source.setPrimitive(Short.MIN_VALUE);
        WildcardWrapperTypeTest.WrapperFieldAndNonVoidPrimitiveSetter target = mapper.map(source, WildcardWrapperTypeTest.WrapperFieldAndNonVoidPrimitiveSetter.class);
        Assert.assertThat(target.getPrimitive(), CoreMatchers.equalTo(Short.MIN_VALUE));
    }

    @Test
    public void testCanMapBetweenClassUsingWrapperTypeAndClassUsingPrimitiveType() {
        WildcardWrapperTypeTest.PrimitiveFieldWrapperGetterAndSetter source = new WildcardWrapperTypeTest.PrimitiveFieldWrapperGetterAndSetter();
        source.setPrimitive(Integer.MIN_VALUE);
        WildcardWrapperTypeTest.PrimitiveFieldStandardGetterAndSetter target = mapper.map(source, WildcardWrapperTypeTest.PrimitiveFieldStandardGetterAndSetter.class);
        Assert.assertThat(target.getPrimitive(), CoreMatchers.equalTo(Integer.MIN_VALUE));
    }

    public static class PrimitiveFieldWrapperSetter {
        private long primitive;

        public long getPrimitive() {
            return primitive;
        }

        public void setPrimitive(Long primitive) {
            this.primitive = primitive;
        }
    }

    public static class PrimitiveFieldWrapperGetter {
        private boolean primitive;

        public Boolean getPrimitive() {
            return primitive;
        }

        public void setPrimitive(boolean primitive) {
            this.primitive = primitive;
        }
    }

    public static class PrimitiveFieldWrapperGetterAndSetter {
        private int primitive;

        public Integer getPrimitive() {
            return primitive;
        }

        public void setPrimitive(Integer primitive) {
            this.primitive = primitive;
        }
    }

    public static class PrimitiveFieldStandardGetterAndSetter {
        private int primitive;

        public int getPrimitive() {
            return primitive;
        }

        @SuppressWarnings("unused")
        public void setPrimitive(int primitive) {
            this.primitive = primitive;
        }
    }

    public static class PrimitiveFieldUnrelatedSetter {
        private double primitive;

        public double getPrimitive() {
            return primitive;
        }

        @SuppressWarnings("unused")
        public void setPrimitive(Boolean primitive) {
            this.primitive = Double.MIN_VALUE;
        }
    }

    public static class WrapperFieldPrimitiveSetter {
        private Float primitive;

        public Float getPrimitive() {
            return primitive;
        }

        public void setPrimitive(float primitive) {
            this.primitive = primitive;
        }
    }

    public static class WrapperFieldAndNonVoidPrimitiveSetter {
        private Short primitive;

        public Short getPrimitive() {
            return primitive;
        }

        public Short setPrimitive(short primitive) {
            return this.primitive = primitive;
        }
    }
}

