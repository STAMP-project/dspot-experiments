/**
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests;


import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.definition.type.FactType;
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.Role;


@RunWith(Parameterized.class)
public class AnnotationsTest {
    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AnnotationsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    public enum AnnPropEnum {

        ONE("one"),
        TWO("two"),
        THREE("three");
        private final String value;

        AnnPropEnum(final String s) {
            this.value = s;
        }

        public String getValue() {
            return value;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.FIELD })
    public @interface Annot {
        int intProp() default 0;

        Class typeProp() default Object.class;

        String strProp() default "foo";

        AnnotationsTest.AnnPropEnum enumProp() default AnnotationsTest.AnnPropEnum.ONE;

        double[] dblArrProp() default { 0.4, 0.5 };

        Class[] typeArrProp() default {  };

        String[] strArrProp() default { "a", "b", "c" };

        AnnotationsTest.AnnPropEnum[] enumArrProp() default { AnnotationsTest.AnnPropEnum.TWO, AnnotationsTest.AnnPropEnum.THREE };
    }

    @Test
    public void annotationTest() {
        final String drl = (((((((((((((((((((((((((((((((((((((("package org.drools.compiler.test;\n " + ("" + "import ")) + (AnnotationsTest.AnnPropEnum.class.getCanonicalName())) + "; \n ") + "import ") + (Position.class.getCanonicalName())) + "; \n ") + "import ") + (AnnotationsTest.class.getCanonicalName())) + "; \n") + "import ") + (AnnotationsTest.Annot.class.getCanonicalName())) + "; \n") + "") + "declare AnnotatedBean \n") + " @Deprecated \n") + "") + " @Annot( intProp=7 ") + "         ,typeProp=String.class ") + "         ,strProp=\"hello world\" ") + "         ,enumProp=AnnPropEnum.THREE ") + "         ,dblArrProp={1.0,2.0} ") + "         ,typeArrProp={String.class, AnnotationsTest.class} ") + "         ,strArrProp={\"x1\",\"x2\"} ") + "         ,enumArrProp={AnnPropEnum.ONE, AnnPropEnum.THREE} ") + "         ) \n ") + " \n ") + " @role(event) \n ") + " ") + " age : int \n") + " name : String      @key    @Position(0)    @Deprecated \n") + " end \n ") + " ") + " \n\n") + " ") + "declare SecondBean \n ") + " @NonexistingAnnotation") + "  \n") + " field : String @Annot \n") + "end \n";
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("annotations-test", kieBaseTestConfiguration, drl);
        final Class clazz = kbase.getFactType("org.drools.compiler.test", "AnnotatedBean").getFactClass();
        Assert.assertNotNull(clazz);
        try {
            final Field fld = clazz.getDeclaredField("name");
            Assert.assertEquals(3, fld.getAnnotations().length);
            Assert.assertNotNull(fld.getAnnotation(Deprecated.class));
            Assert.assertNotNull(fld.getAnnotation(Position.class));
            Assert.assertNotNull(fld.getAnnotation(Key.class));
            final Position pos = fld.getAnnotation(Position.class);
            Assert.assertEquals(0, pos.value());
        } catch (final NoSuchFieldException nsfe) {
            Assert.fail(("field name has not been generated correctly : " + (nsfe.getMessage())));
        }
        final Annotation[] anns = clazz.getAnnotations();
        Assert.assertEquals(3, anns.length);
        Assert.assertNotNull(clazz.getAnnotation(Deprecated.class));
        Assert.assertNotNull(clazz.getAnnotation(AnnotationsTest.Annot.class));
        Assert.assertNotNull(clazz.getAnnotation(Role.class));
        final AnnotationsTest.Annot ann = ((AnnotationsTest.Annot) (clazz.getAnnotation(AnnotationsTest.Annot.class)));
        Assert.assertEquals(7, ann.intProp());
        Assert.assertEquals(String.class, ann.typeProp());
        Assert.assertEquals("hello world", ann.strProp());
        Assert.assertEquals(AnnotationsTest.AnnPropEnum.THREE, ann.enumProp());
        Assert.assertArrayEquals(new double[]{ 1.0, 2.0 }, ann.dblArrProp(), 1.0E-16);
        Assert.assertArrayEquals(new Class[]{ String.class, AnnotationsTest.class }, ann.typeArrProp());
        Assert.assertArrayEquals(new String[]{ "x1", "x2" }, ann.strArrProp());
        Assert.assertArrayEquals(new AnnotationsTest.AnnPropEnum[]{ AnnotationsTest.AnnPropEnum.ONE, AnnotationsTest.AnnPropEnum.THREE }, ann.enumArrProp());
        final Class clazz2 = kbase.getFactType("org.drools.compiler.test", "SecondBean").getFactClass();
        Assert.assertNotNull(clazz2);
        final Annotation[] anns2 = clazz2.getAnnotations();
        Assert.assertEquals(0, anns2.length);
        AnnotationsTest.Annot ann2 = null;
        try {
            final Field fld2 = clazz2.getDeclaredField("field");
            Assert.assertNotNull(fld2.getAnnotation(AnnotationsTest.Annot.class));
            ann2 = fld2.getAnnotation(AnnotationsTest.Annot.class);
        } catch (final NoSuchFieldException nsfe) {
            Assert.fail(("field name has not been generated correctly : " + (nsfe.getMessage())));
        }
        Assert.assertNotNull(ann2);
        Assert.assertEquals(0, ann2.intProp());
        Assert.assertEquals("foo", ann2.strProp());
        Assert.assertEquals(AnnotationsTest.AnnPropEnum.ONE, ann2.enumProp());
        Assert.assertArrayEquals(new double[]{ 0.4, 0.5 }, ann2.dblArrProp(), 1.0E-16);
        Assert.assertArrayEquals(new String[]{ "a", "b", "c" }, ann2.strArrProp());
        Assert.assertArrayEquals(new AnnotationsTest.AnnPropEnum[]{ AnnotationsTest.AnnPropEnum.TWO, AnnotationsTest.AnnPropEnum.THREE }, ann2.enumArrProp());
    }

    @Test
    public void annotationErrorTest() {
        final String drl = "package org.drools.compiler.test;\n " + ((((("" + "declare MissingAnnotationBean \n") + " @IgnoreMissingAnnotation1 \n") + "") + " name : String      @IgnoreMissingAnnotation2( noProp = 999 ) \n") + " end \n ");
        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        Assertions.assertThat(kieBuilder.getResults().getMessages()).isEmpty();
        final String drl2 = ((((((((("package org.drools.compiler.test;\n " + ("" + "import ")) + (AnnotationsTest.Annot.class.getCanonicalName())) + "; \n") + "") + "") + "declare MissingAnnotationBean \n") + " @Annot( wrongProp1 = 1 ) \n") + "") + " name : String      @Annot( wrongProp2 = 2, wrongProp3 = 3 ) \n") + " end \n ";
        final KieBuilder kieBuilder2 = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl2);
        Assertions.assertThat(kieBuilder2.getResults().getMessages()).hasSize(3);
    }

    @Test
    public void testAnnotationNameClash() {
        final String drl = ((((((((((("package org.drools.test\n" + ((("" + "declare Annot\n") + " id : int ") + " @")) + (AnnotationsTest.Annot.class.getCanonicalName())) + "( intProp = 3, typeProp = String.class, typeArrProp = {} ) \n") + " ") + "end\n") + "") + "rule X\n") + "when\n") + " \n") + "then\n") + " insert( new Annot( 22 ) ); ") + "end";
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("annotations-test", kieBaseTestConfiguration, drl);
        final FactType ft = kbase.getFactType("org.drools.test", "Annot");
        try {
            final Object o = ft.newInstance();
            final AnnotationsTest.Annot a = o.getClass().getDeclaredField("id").getAnnotation(AnnotationsTest.Annot.class);
            Assert.assertEquals(3, a.intProp());
            Assert.assertEquals(String.class, a.typeProp());
            Assert.assertEquals(0, a.typeArrProp().length);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    public static class Duration {}

    @Test
    public void testAnnotationNameClashWithRegularClass() {
        final String drl = (((((((("package org.drools.test\n" + "import ") + (AnnotationsTest.Duration.class.getCanonicalName())) + "; ") + "declare Annot ") + "  @role( event )") + "  @duration( durat ) ") + "  durat : long ") + "end\n") + "";
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("annotations-test", kieBaseTestConfiguration, drl);
        final FactType ft = kbase.getFactType("org.drools.test", "Annot");
        Assert.assertNotNull(ft);
    }

    public @interface Simple {
        int[] numbers();
    }

    @Test
    public void testAnnotationOnLHSAndMerging() {
        final String drl = ((((((((((("package org.drools.compiler; " + (" " + "import ")) + (AnnotationsTest.Annot.class.getCanonicalName())) + "; ") + " ") + "rule \"test collect with annotation\" ") + "    when ") + "       ( and @Annot ") + "         String() ") + "         Integer() ) ") + "    then ") + "end ") + "";
        KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, true, drl);
    }
}

