/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.annotations;


import org.apache.geode.annotations.experimentalpackage.ClassInExperimentalPackage;
import org.apache.geode.experimental.nonexperimentalpackage.ClassInNonExperimentalPackage;
import org.junit.Test;


/**
 * Unit tests for the <tt>Experimental</tt> annotation. Verifies that the annotation can be applied
 * to Interfaces, Classes, Public and Protected Fields, Enums, Enum Constants, Public and Protected
 * Methods, Packages, and Constructors.
 */
public class ExperimentalJUnitTest {
    private static final String FIELD_NAME = "field";

    private static final String METHOD_NAME = "method";

    @Test
    public void shouldIdentifyExperimentalInterface() throws Exception {
        assertThat(ExperimentalJUnitTest.isExperimental(ExperimentalJUnitTest.RegularInterface.class)).isFalse();
        assertThat(ExperimentalJUnitTest.isExperimental(ExperimentalJUnitTest.ExperimentalInterface.class)).isTrue();
    }

    @Test
    public void shouldIdentifyExperimentalClass() throws Exception {
        assertThat(ExperimentalJUnitTest.isExperimental(ExperimentalJUnitTest.RegularClass.class)).isFalse();
        assertThat(ExperimentalJUnitTest.isExperimental(ExperimentalJUnitTest.ExperimentalClass.class)).isTrue();
    }

    @Test
    public void shouldIdentifyExperimentalPublicField() throws Exception {
        assertThat(ExperimentalJUnitTest.isExperimental(ExperimentalJUnitTest.RegularPublicField.class.getField(ExperimentalJUnitTest.FIELD_NAME))).isFalse();
        assertThat(ExperimentalJUnitTest.isExperimental(ExperimentalJUnitTest.ExperimentalPublicField.class.getField(ExperimentalJUnitTest.FIELD_NAME))).isTrue();
    }

    @Test
    public void shouldIdentifyExperimentalProtectedField() throws Exception {
        assertThat(ExperimentalJUnitTest.isExperimental(ExperimentalJUnitTest.RegularProtectedField.class.getDeclaredField(ExperimentalJUnitTest.FIELD_NAME))).isFalse();
        assertThat(ExperimentalJUnitTest.isExperimental(ExperimentalJUnitTest.ExperimentalProtectedField.class.getDeclaredField(ExperimentalJUnitTest.FIELD_NAME))).isTrue();
    }

    @Test
    public void shouldIdentifyExperimentalEnum() throws Exception {
        assertThat(ExperimentalJUnitTest.isExperimental(ExperimentalJUnitTest.RegularEnum.class)).isFalse();
        assertThat(ExperimentalJUnitTest.isExperimental(ExperimentalJUnitTest.ExperimentalEnum.class)).isTrue();
    }

    @Test
    public void shouldIdentifyExperimentalEnumConstant() throws Exception {
        assertThat(ExperimentalJUnitTest.isExperimental(ExperimentalJUnitTest.RegularEnumInstance.class.getField(ExperimentalJUnitTest.RegularEnumInstance.THREE.name()))).isFalse();
        assertThat(ExperimentalJUnitTest.isExperimental(ExperimentalJUnitTest.ExperimentalEnumInstance.class.getField(ExperimentalJUnitTest.ExperimentalEnumInstance.THREE.name()))).isTrue();
    }

    @Test
    public void shouldIdentifyExperimentalPublicMethod() throws Exception {
        assertThat(ExperimentalJUnitTest.isExperimental(ExperimentalJUnitTest.RegularPublicMethod.class.getMethod(ExperimentalJUnitTest.METHOD_NAME))).isFalse();
        assertThat(ExperimentalJUnitTest.isExperimental(ExperimentalJUnitTest.ExperimentalPublicMethod.class.getMethod(ExperimentalJUnitTest.METHOD_NAME))).isTrue();
    }

    @Test
    public void shouldIdentifyExperimentalProtectedMethod() throws Exception {
        assertThat(ExperimentalJUnitTest.isExperimental(ExperimentalJUnitTest.RegularProtectedMethod.class.getDeclaredMethod(ExperimentalJUnitTest.METHOD_NAME))).isFalse();
        assertThat(ExperimentalJUnitTest.isExperimental(ExperimentalJUnitTest.ExperimentalProtectedMethod.class.getDeclaredMethod(ExperimentalJUnitTest.METHOD_NAME))).isTrue();
    }

    @Test
    public void shouldIdentifyExperimentalPackage() throws Exception {
        assertThat(ExperimentalJUnitTest.isExperimental(ClassInNonExperimentalPackage.class.getPackage())).isFalse();
        assertThat(ExperimentalJUnitTest.isExperimental(ClassInExperimentalPackage.class.getPackage())).isTrue();
    }

    @Test
    public void shouldIdentifyExperimentalPublicConstructor() throws Exception {
        assertThat(ExperimentalJUnitTest.isExperimental(ExperimentalJUnitTest.RegularPublicConstructor.class.getConstructor())).isFalse();
        assertThat(ExperimentalJUnitTest.isExperimental(ExperimentalJUnitTest.ExperimentalPublicConstructor.class.getConstructor())).isTrue();
    }

    @Test
    public void shouldIdentifyExperimentalProtectedConstructor() throws Exception {
        assertThat(ExperimentalJUnitTest.isExperimental(ExperimentalJUnitTest.RegularProtectedConstructor.class.getConstructor())).isFalse();
        assertThat(ExperimentalJUnitTest.isExperimental(ExperimentalJUnitTest.ExperimentalProtectedConstructor.class.getConstructor())).isTrue();
    }

    public static interface RegularInterface {}

    @Experimental("This is an experimental interface")
    public static interface ExperimentalInterface {}

    public static class RegularClass {}

    @Experimental("This is an experimental class")
    public static class ExperimentalClass {}

    public static class RegularPublicField {
        public final boolean field = false;
    }

    public static class ExperimentalPublicField {
        @Experimental("This is an experimental public field")
        public final boolean field = false;
    }

    public static class RegularProtectedField {
        protected final boolean field = false;
    }

    public static class ExperimentalProtectedField {
        @Experimental("This is an experimental protected field")
        protected final boolean field = false;
    }

    public static enum RegularEnum {

        ONE,
        TWO,
        THREE;}

    @Experimental("This is an experimental enum")
    public static enum ExperimentalEnum {

        ONE,
        TWO,
        THREE;}

    public static enum RegularEnumInstance {

        ONE,
        TWO,
        THREE;}

    public static enum ExperimentalEnumInstance {

        ONE,
        TWO,
        @Experimental("This is an experimental enum constant")
        THREE;}

    public static class RegularPublicMethod {
        public void method() {
        }
    }

    public static class ExperimentalPublicMethod {
        @Experimental("This is an experimental public method")
        public void method() {
        }
    }

    public static class RegularProtectedMethod {
        public void method() {
        }
    }

    public static class ExperimentalProtectedMethod {
        @Experimental("This is an experimental protected method")
        protected void method() {
        }
    }

    public static class RegularPublicConstructor {
        public RegularPublicConstructor() {
        }
    }

    public static class ExperimentalPublicConstructor {
        @Experimental("This is an experimental public constructor")
        public ExperimentalPublicConstructor() {
        }
    }

    public static class RegularProtectedConstructor {
        public RegularProtectedConstructor() {
        }
    }

    public static class ExperimentalProtectedConstructor {
        @Experimental("This is an experimental protected constructor")
        public ExperimentalProtectedConstructor() {
        }
    }
}

