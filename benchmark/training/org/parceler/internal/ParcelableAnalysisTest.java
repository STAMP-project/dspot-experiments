/**
 * Copyright 2011-2015 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parceler.internal;


import ASTClassFactory.ASTParameterName;
import android.os.Parcel;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.androidtransfuse.adapter.ASTAnnotation;
import org.androidtransfuse.adapter.ASTParameter;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.bootstrap.Bootstrap;
import org.junit.Assert;
import org.junit.Test;
import org.parceler.Parcel.Serialization;


@Bootstrap
public class ParcelableAnalysisTest {
    @Inject
    private ParcelableAnalysis parcelableAnalysis;

    @Inject
    private ASTClassFactory astClassFactory;

    @Inject
    private ErrorCheckingMessager messager;

    private ASTType converterAst;

    static class TargetSubType {
        String value;
    }

    static class TargetSubTypeWriterConverter implements ParcelConverter<ParcelableAnalysisTest.TargetSubType> {
        @Override
        public void toParcel(ParcelableAnalysisTest.TargetSubType input, Parcel parcel) {
            parcel.writeString(input.value);
        }

        @Override
        public ParcelableAnalysisTest.TargetSubType fromParcel(Parcel parcel) {
            ParcelableAnalysisTest.TargetSubType target = new ParcelableAnalysisTest.TargetSubType();
            target.value = parcel.readString();
            return target;
        }
    }

    @Parcel
    abstract static class AbstractParcel {}

    @Parcel
    abstract static class AbstractParcelWithConstructtor {
        @ParcelConstructor
        AbstractParcelWithConstructtor() {
        }
    }

    @Test
    public void testAbstractParcel() {
        errors(ParcelableAnalysisTest.AbstractParcel.class);
        errors(ParcelableAnalysisTest.AbstractParcelWithConstructtor.class);
    }

    @Parcel
    abstract static class AbstractParcelWithFactory {
        @ParcelFactory
        public static ParcelableAnalysisTest.AbstractParcelWithFactory build() {
            return null;
        }
    }

    @Test
    public void testAbstractParcelWitFactory() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.AbstractParcelWithFactory.class);
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertNotNull(analysis.getConstructorPair().getFactoryMethod());
        Assert.assertNull(analysis.getConstructorPair().getConstructor());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
    }

    @Parcel
    interface InterfaceParcel {}

    @Test
    public void tesInterfaceParcel() {
        errors(ParcelableAnalysisTest.InterfaceParcel.class);
    }

    @Parcel(describeContents = 42)
    static class DescribedContents {}

    @Test
    public void testDescribeContents() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.DescribedContents.class);
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
        Assert.assertTrue((42 == (analysis.getDescribeContents())));
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
    }

    @Parcel
    static class FieldSerialization {
        String value;
    }

    @Test
    public void testFieldSerialization() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.FieldSerialization.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertEquals(1, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
        Assert.assertTrue(fieldsContain(analysis, "value"));
    }

    @Parcel
    static class TransientFieldSerialization {
        @Transient
        String value;
    }

    @Test
    public void testTransientFieldSerialization() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.TransientFieldSerialization.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
        Assert.assertFalse(fieldsContain(analysis, "value"));
    }

    @Parcel
    static class StaticFieldExcluded {
        static String staticField = "value";
    }

    @Parcel(Serialization.BEAN)
    static class StaticMethodsExcluded {
        public static String getStatic() {
            return "value";
        }

        public static void setStatic(String value) {
        }
    }

    @Test
    public void testStaticFieldExclusion() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.StaticFieldExcluded.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
        Assert.assertFalse(fieldsContain(analysis, "staticField"));
    }

    @Test
    public void testStaticMethodExclusion() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.StaticMethodsExcluded.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
    }

    @Parcel
    static class ConstructorSerialization {
        String value;

        @ParcelConstructor
        public ConstructorSerialization(@ParcelProperty("value")
        String value) {
            this.value = value;
        }
    }

    @Test
    public void testConstructor() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.ConstructorSerialization.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(1, analysis.getConstructorPair().getWriteReferences().size());
    }

    @Parcel
    static class UnnamedConstructorSerialization {
        String value;

        @ParcelConstructor
        public UnnamedConstructorSerialization(@ASTClassFactory.ASTParameterName("value")
        String value) {
            this.value = value;
        }
    }

    @Test
    public void testUnnamedConstructorSerialization() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.UnnamedConstructorSerialization.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(1, analysis.getConstructorPair().getWriteReferences().size());
    }

    @Parcel(Serialization.BEAN)
    static class ConstructorMethod {
        String value;

        @ParcelConstructor
        public ConstructorMethod(@ASTClassFactory.ASTParameterName("value")
        String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Test
    public void testConstructorMethod() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.ConstructorMethod.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(1, analysis.getConstructorPair().getWriteReferences().size());
    }

    @Parcel(Serialization.BEAN)
    static class ConstructorProtectedMethod {
        String value;

        @ParcelConstructor
        public ConstructorProtectedMethod(@ASTClassFactory.ASTParameterName("value")
        String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }

    @Test
    public void testConstructorProtectedMethod() {
        errors(ParcelableAnalysisTest.ConstructorProtectedMethod.class);
    }

    @Parcel(Serialization.BEAN)
    static class ConstructorAnnotatedPrivateMethod {
        String value;

        @ParcelConstructor
        public ConstructorAnnotatedPrivateMethod(@ASTClassFactory.ASTParameterName("value")
        String value) {
            this.value = value;
        }

        @ParcelProperty("value")
        private String getValue() {
            return value;
        }
    }

    @Test
    public void testConstructorAnnotatedPrivateMethod() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.ConstructorAnnotatedPrivateMethod.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(1, analysis.getConstructorPair().getWriteReferences().size());
    }

    @Parcel(Serialization.BEAN)
    static class Basic {
        String stringValue;

        int intValue;

        boolean booleanValue;

        public String getStringValue() {
            return stringValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        public int getIntValue() {
            return intValue;
        }

        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }

        public boolean isBooleanValue() {
            return booleanValue;
        }

        public void setBooleanValue(boolean booleanValue) {
            this.booleanValue = booleanValue;
        }
    }

    @Test
    public void testBasic() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.Basic.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(3, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
        Assert.assertTrue(methodsContain(analysis, "intValue"));
        Assert.assertTrue(methodsContain(analysis, "stringValue"));
        Assert.assertTrue(methodsContain(analysis, "booleanValue"));
    }

    @Parcel(Serialization.BEAN)
    static class Modifiers {
        String one;

        String two;

        String three;

        String four;

        public String getOne() {
            return one;
        }

        public void setOne(String one) {
            this.one = one;
        }

        String getTwo() {
            return two;
        }

        void setTwo(String two) {
            this.two = two;
        }

        protected String getThree() {
            return three;
        }

        protected void setThree(String three) {
            this.three = three;
        }

        private String getFour() {
            return four;
        }

        private void setFour(String four) {
            this.four = four;
        }
    }

    @Test
    public void testMethodModifers() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.Modifiers.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(1, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
        Assert.assertTrue(methodsContain(analysis, "one"));
    }

    @Parcel(Serialization.BEAN)
    static class MissingSetter {
        String stringValue;

        int intValue;

        public String getStringValue() {
            return stringValue;
        }

        public int getIntValue() {
            return intValue;
        }

        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }
    }

    @Test
    public void testMissingSetter() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.MissingSetter.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(1, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
        Assert.assertTrue(methodsContain(analysis, "intValue"));
        Assert.assertFalse(methodsContain(analysis, "stringValue"));
    }

    @Parcel(Serialization.BEAN)
    static class MissingGetter {
        String stringValue;

        int intValue;

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        public int getIntValue() {
            return intValue;
        }

        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }
    }

    @Test
    public void testMissingGetter() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.MissingGetter.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(1, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
        Assert.assertTrue(methodsContain(analysis, "intValue"));
        Assert.assertFalse(methodsContain(analysis, "stringValue"));
    }

    public static class Converter implements ParcelConverter {
        @Override
        public void toParcel(Object input, Parcel destinationParcel) {
        }

        @Override
        public Object fromParcel(Parcel parcel) {
            return null;
        }
    }

    @Parcel(converter = ParcelableAnalysisTest.Converter.class)
    static class Target {}

    @Test
    public void testParcelConverter() {
        ASTType targetAst = astClassFactory.getType(ParcelableAnalysisTest.Target.class);
        ASTAnnotation parcelASTAnnotaiton = targetAst.getASTAnnotation(Parcel.class);
        ASTType parcelConverterAst = astClassFactory.getType(ParcelableAnalysisTest.Converter.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(targetAst, parcelASTAnnotaiton);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertEquals(parcelConverterAst, analysis.getParcelConverterType());
    }

    @Parcel(Serialization.BEAN)
    static class BeanConverters {
        private ParcelableAnalysisTest.TargetSubType one;

        private ParcelableAnalysisTest.TargetSubType two;

        @ParcelPropertyConverter(ParcelableAnalysisTest.TargetSubTypeWriterConverter.class)
        public ParcelableAnalysisTest.TargetSubType getOne() {
            return one;
        }

        public void setOne(ParcelableAnalysisTest.TargetSubType one) {
            this.one = one;
        }

        public ParcelableAnalysisTest.TargetSubType getTwo() {
            return two;
        }

        @ParcelPropertyConverter(ParcelableAnalysisTest.TargetSubTypeWriterConverter.class)
        public void setTwo(ParcelableAnalysisTest.TargetSubType two) {
            this.two = two;
        }
    }

    @Test
    public void testBeanConverters() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.BeanConverters.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(2, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
        Assert.assertEquals(converterAst, analysis.getMethodPairs().get(0).getConverter());
        Assert.assertEquals(converterAst, analysis.getMethodPairs().get(1).getConverter());
    }

    @Parcel(Serialization.BEAN)
    static class MethodTransient {
        String stringValue;

        int intValue;

        @Transient
        public String getStringValue() {
            return stringValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        public int getIntValue() {
            return intValue;
        }

        @Transient
        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }
    }

    @Test
    public void testTransient() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.MethodTransient.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
        Assert.assertFalse(methodsContain(analysis, "stringValue"));
        Assert.assertFalse(methodsContain(analysis, "intValue"));
    }

    @Parcel
    static class FieldTransient {
        @Transient
        String stringValue;

        transient int intValue;
    }

    @Test
    public void testFieldTransientTransient() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.FieldTransient.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
        Assert.assertFalse(methodsContain(analysis, "stringValue"));
        Assert.assertFalse(methodsContain(analysis, "intValue"));
    }

    @Parcel
    static class DuplicateProperty {
        @ParcelProperty("value")
        String value;

        @ParcelProperty("value")
        String value2;
    }

    @Test
    public void testDuplicatePropertyError() {
        errors(ParcelableAnalysisTest.DuplicateProperty.class);
    }

    @Parcel
    static class NoDesignatedConstructor {
        String value;

        public NoDesignatedConstructor(@ASTClassFactory.ASTParameterName("value")
        String value) {
            this.value = value;
        }
    }

    @Test
    public void testNoDesignatedConstructor() {
        errors(ParcelableAnalysisTest.NoDesignatedConstructor.class);
    }

    @Parcel
    static class TooManyParcelConstructors {
        String value;

        @ParcelConstructor
        public TooManyParcelConstructors() {
        }

        @ParcelConstructor
        public TooManyParcelConstructors(@ASTClassFactory.ASTParameterName("value")
        String value) {
            this.value = value;
        }
    }

    @Test
    public void testTooManyParcelConstructor() {
        errors(ParcelableAnalysisTest.TooManyParcelConstructors.class);
    }

    @Parcel
    static class DefaultConstructor {
        public DefaultConstructor() {
        }
    }

    @Test
    public void testDefaultConstructor() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.DefaultConstructor.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
    }

    @Parcel
    static class FieldMethodProperty {
        String one;

        String two;

        @ParcelProperty("one")
        public String getSomeValue() {
            return one;
        }

        @ParcelProperty("two")
        public void setSomeValue(String two) {
            this.two = two;
        }
    }

    @Test
    public void testFieldMethodProperty() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.FieldMethodProperty.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertEquals(1, analysis.getFieldPairs().size());
        Assert.assertEquals(1, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
        Assert.assertTrue(fieldsContain(analysis, "one"));
        Assert.assertTrue(methodsContain(analysis, "two"));
    }

    @Parcel
    static class NonBeanMethodProperty {
        String one;

        @ParcelProperty("one")
        public String someValue() {
            return one;
        }

        @ParcelProperty("one")
        public void someValue(String one) {
            this.one = one;
        }
    }

    @Test
    public void testNonBeanMethodProperty() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.NonBeanMethodProperty.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(1, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
        Assert.assertFalse(fieldsContain(analysis, "one"));
        Assert.assertTrue(methodsContain(analysis, "one"));
    }

    @Parcel
    static class CollidingConstructorProperty {
        @ParcelProperty("value")
        String value;

        @ParcelConstructor
        public CollidingConstructorProperty(@ParcelProperty("value")
        String value) {
            this.value = value;
        }

        @ParcelProperty("value")
        public void setValue(String value) {
            this.value = value;
        }
    }

    @Test
    public void testCollidingConstructorProperty() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.CollidingConstructorProperty.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(1, analysis.getConstructorPair().getWriteReferences().size());
        Assert.assertTrue(constructorContains(analysis, "value"));
        Assert.assertFalse(fieldsContain(analysis, "value"));
        Assert.assertFalse(methodsContain(analysis, "value"));
    }

    @Parcel
    static class CollidingMethodProperty {
        @ParcelProperty("value")
        String someValue;

        @ParcelProperty("value")
        public void setSomeValue(String value) {
            this.someValue = value;
        }
    }

    @Test
    public void testCollidingMethodProperty() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.CollidingMethodProperty.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(1, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
        Assert.assertFalse(fieldsContain(analysis, "value"));
        Assert.assertTrue(methodsContain(analysis, "value"));
    }

    @Parcel
    static class PropertyConverterParcel {
        @ParcelPropertyConverter(ParcelableAnalysisTest.TargetSubTypeWriterConverter.class)
        ParcelableAnalysisTest.TargetSubType value;
    }

    @Test
    public void testParcelPropertyConverter() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.PropertyConverterParcel.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertEquals(1, analysis.getFieldPairs().size());
        Assert.assertEquals(converterAst, analysis.getFieldPairs().get(0).getConverter());
    }

    @Parcel
    static class MethodPropertyConverter {
        ParcelableAnalysisTest.TargetSubType value;

        @ParcelProperty("value")
        @ParcelPropertyConverter(ParcelableAnalysisTest.TargetSubTypeWriterConverter.class)
        public void setValue(ParcelableAnalysisTest.TargetSubType value) {
            this.value = value;
        }
    }

    @Test
    public void testMethodPropertyConverter() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.MethodPropertyConverter.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertEquals(1, analysis.getMethodPairs().size());
        Assert.assertEquals(converterAst, analysis.getMethodPairs().get(0).getConverter());
    }

    @Parcel
    static class ConstructorConverterSerialization {
        ParcelableAnalysisTest.TargetSubType value;

        @ParcelConstructor
        public ConstructorConverterSerialization(@ParcelProperty("value")
        @ParcelPropertyConverter(ParcelableAnalysisTest.TargetSubTypeWriterConverter.class)
        ParcelableAnalysisTest.TargetSubType value) {
            this.value = value;
        }
    }

    @Test
    public void testConstructorConverterSerialization() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.ConstructorConverterSerialization.class);
        ASTParameter parameter = analysis.getConstructorPair().getConstructor().getParameters().get(0);
        Map<ASTParameter, ASTType> converters = analysis.getConstructorPair().getConverters();
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertEquals(1, converters.size());
        Assert.assertEquals(converterAst, converters.get(parameter));
    }

    @Parcel
    static class UnnamedConstructorConverterSerialization {
        ParcelableAnalysisTest.TargetSubType value;

        @ParcelConstructor
        public UnnamedConstructorConverterSerialization(@ParcelPropertyConverter(ParcelableAnalysisTest.TargetSubTypeWriterConverter.class)
        @ASTClassFactory.ASTParameterName("value")
        ParcelableAnalysisTest.TargetSubType value) {
            this.value = value;
        }
    }

    @Test
    public void testUnnamedConstructorConverterSerialization() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.UnnamedConstructorConverterSerialization.class);
        ASTParameter parameter = analysis.getConstructorPair().getConstructor().getParameters().get(0);
        Map<ASTParameter, ASTType> converters = analysis.getConstructorPair().getConverters();
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertEquals(1, converters.size());
        Assert.assertEquals(converterAst, converters.get(parameter));
    }

    @Parcel
    static class CollidingConstructorParameterConverterSerialization {
        @ParcelPropertyConverter(ParcelableAnalysisTest.TargetSubTypeWriterConverter.class)
        ParcelableAnalysisTest.TargetSubType value;

        @ParcelConstructor
        public CollidingConstructorParameterConverterSerialization(@ParcelPropertyConverter(ParcelableAnalysisTest.TargetSubTypeWriterConverter.class)
        ParcelableAnalysisTest.TargetSubType value) {
            this.value = value;
        }
    }

    @Test
    public void testCollidingConverterSerialization() {
        errors(ParcelableAnalysisTest.CollidingConstructorParameterConverterSerialization.class);
    }

    @Parcel
    static class CollidingMethodParameterConverterSerialization {
        @ParcelProperty("value")
        @ParcelPropertyConverter(ParcelableAnalysisTest.TargetSubTypeWriterConverter.class)
        ParcelableAnalysisTest.TargetSubType value;

        @ParcelProperty("value")
        @ParcelPropertyConverter(ParcelableAnalysisTest.TargetSubTypeWriterConverter.class)
        public void setValue(ParcelableAnalysisTest.TargetSubType value) {
            this.value = value;
        }
    }

    @Test
    public void testCollidingMethodParameterConverterSerialization() {
        errors(ParcelableAnalysisTest.CollidingMethodParameterConverterSerialization.class);
    }

    @Parcel
    static class CollidingMethodConverterSerialization {
        ParcelableAnalysisTest.TargetSubType value;

        @ParcelProperty("value")
        @ParcelPropertyConverter(ParcelableAnalysisTest.TargetSubTypeWriterConverter.class)
        public void setValue(ParcelableAnalysisTest.TargetSubType value) {
            this.value = value;
        }

        @ParcelProperty("value")
        @ParcelPropertyConverter(ParcelableAnalysisTest.TargetSubTypeWriterConverter.class)
        public ParcelableAnalysisTest.TargetSubType getValue() {
            return value;
        }
    }

    @Test
    public void testCollidingMethodConverterSerialization() {
        errors(ParcelableAnalysisTest.CollidingMethodConverterSerialization.class);
    }

    public static class SuperClass {
        String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @Parcel
    static class FieldSubClass extends ParcelableAnalysisTest.SuperClass {
        String value;
    }

    @Test
    public void testFieldInheritance() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.FieldSubClass.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertEquals(2, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
        Assert.assertTrue(fieldsContain(analysis, "value"));
    }

    @Parcel(Serialization.BEAN)
    static class MethodSubClass extends ParcelableAnalysisTest.SuperClass {
        String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @Test
    public void testMethodOverrideInheritance() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.MethodSubClass.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(1, analysis.getMethodPairs().size());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
        Assert.assertTrue(methodsContain(analysis, "value"));
    }

    @Parcel
    static class ConstructorSubclass extends ParcelableAnalysisTest.SuperClass {
        @ParcelConstructor
        public ConstructorSubclass(@ASTClassFactory.ASTParameterName("value")
        String value) {
            super.value = value;
        }
    }

    @Test
    public void testConstructorWithSuperClassParameter() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.ConstructorSubclass.class);
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
        Assert.assertEquals(1, analysis.getConstructorPair().getWriteReferences().size());
        constructorContains(analysis, "value");
    }

    @Parcel
    static class DefaultToEmptyBeanConstructor {
        public DefaultToEmptyBeanConstructor() {
        }

        public DefaultToEmptyBeanConstructor(String value) {
        }
    }

    @Test
    public void testDefaultToEmptyBeanConstructor() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.DefaultToEmptyBeanConstructor.class);
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
        constructorContains(analysis, "value");
    }

    @Parcel
    static class ConstructorAmbiguousReaderSubclass extends ParcelableAnalysisTest.SuperClass {
        private String value;

        @ParcelConstructor
        public ConstructorAmbiguousReaderSubclass(@ParcelProperty("value")
        String value) {
            super.value = value;
        }
    }

    @Test
    public void testConstructorAmbiguousReaderSubclass() {
        errors(ParcelableAnalysisTest.ConstructorAmbiguousReaderSubclass.class);
    }

    @Parcel
    static class FactoryMethod {
        String value;

        @ParcelProperty("value")
        public String getValue() {
            return value;
        }

        @ParcelFactory
        public static ParcelableAnalysisTest.FactoryMethod build(@ParcelProperty("value")
        String value) {
            return new ParcelableAnalysisTest.FactoryMethod();
        }
    }

    @Test
    public void testFactoryMethod() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.FactoryMethod.class);
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertNotNull(analysis.getConstructorPair().getFactoryMethod());
        Assert.assertNull(analysis.getConstructorPair().getConstructor());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
    }

    @Parcel
    static class FactoryMethodAndConstructor {
        String value;

        @ParcelConstructor
        public FactoryMethodAndConstructor() {
        }

        @ParcelProperty("value")
        public String getValue() {
            return value;
        }

        @ParcelFactory
        public static ParcelableAnalysisTest.FactoryMethod build(String value) {
            return new ParcelableAnalysisTest.FactoryMethod();
        }
    }

    @Test
    public void testFactoryMethodAndConstructor() {
        errors(ParcelableAnalysisTest.FactoryMethodAndConstructor.class);
    }

    @Parcel
    static class NonStaticFactoryMethod {
        String value;

        @ParcelProperty("value")
        public String getValue() {
            return value;
        }

        @ParcelFactory
        public ParcelableAnalysisTest.NonStaticFactoryMethod build(String value) {
            return new ParcelableAnalysisTest.NonStaticFactoryMethod();
        }
    }

    @Test
    public void testNonStaticFactoryMethod() {
        errors(ParcelableAnalysisTest.NonStaticFactoryMethod.class);
    }

    @Parcel
    static class MismatchedFactoryMethodParams {
        public String getValue() {
            return null;
        }

        @ParcelFactory
        public ParcelableAnalysisTest.MismatchedFactoryMethodParams build(String value) {
            return new ParcelableAnalysisTest.MismatchedFactoryMethodParams();
        }
    }

    @Test
    public void testMismatchedFactoryMethodParams() {
        errors(ParcelableAnalysisTest.MismatchedFactoryMethodParams.class);
    }

    @Parcel
    static class ConverterSubType {
        @ParcelPropertyConverter(ParcelableAnalysisTest.TargetSubTypeWriterConverter.class)
        ParcelableAnalysisTest.TargetSubType targetSubType;
    }

    @Test
    public void testConverterSubType() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.ConverterSubType.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(1, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
    }

    @Parcel
    static class MismatchedTypes {
        String value;

        @ParcelConstructor
        public MismatchedTypes(@ASTClassFactory.ASTParameterName("value")
        Integer value) {
        }
    }

    @Test
    public void testMismatchedTypes() {
        errors(ParcelableAnalysisTest.MismatchedTypes.class);
    }

    @Parcel
    class NonStaticInnerClass {}

    @Test
    public void testNonStaticInnerClass() {
        errors(ParcelableAnalysisTest.NonStaticInnerClass.class);
    }

    @Parcel
    static class UnmappedType {
        Object value;
    }

    @Test
    public void testUnmappedType() {
        errors(ParcelableAnalysisTest.UnmappedType.class);
    }

    @Parcel
    static class NonGenericMapCollection {
        Map value;
    }

    @Test
    public void testNonGenericMapCollection() {
        errors(ParcelableAnalysisTest.NonGenericMapCollection.class);
    }

    @Parcel
    static class NonMappedGenericsMapCollection {
        Map<String, Object> value;
    }

    @Test
    public void testNonMappedGenericsMapCollection() {
        errors(ParcelableAnalysisTest.NonMappedGenericsMapCollection.class);
    }

    @Parcel
    static class NonGenericListCollection {
        List value;
    }

    @Test
    public void testNonGenericListCollection() {
        errors(ParcelableAnalysisTest.NonGenericListCollection.class);
    }

    @Parcel
    static class NonMappedGenericsListCollection {
        List<Object> value;
    }

    @Test
    public void testNonMappedGenericsListCollection() {
        errors(ParcelableAnalysisTest.NonMappedGenericsListCollection.class);
    }

    static class BaseNonParcel {
        String notAnalyzed;
    }

    @Parcel(analyze = ParcelableAnalysisTest.ParcelExtension.class)
    static class ParcelExtension extends ParcelableAnalysisTest.BaseNonParcel {
        String value;
    }

    @Test
    public void testAnalysisLimit() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.ParcelExtension.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(1, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
    }

    static class A {
        String a;
    }

    static class B extends ParcelableAnalysisTest.A {
        String b;
    }

    @Parcel(analyze = { ParcelableAnalysisTest.A.class, ParcelableAnalysisTest.C.class })
    static class C extends ParcelableAnalysisTest.B {
        String c;
    }

    @Test
    public void testSkipAnalysis() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.C.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(2, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
    }

    @Parcel(Serialization.VALUE)
    static class ValueClass {
        private String value;

        public String value() {
            return value;
        }

        public void value(String value) {
            this.value = value;
        }

        public void someOtherMethod(String input) {
        }
    }

    @Test
    public void testValueClassAnalysis() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.ValueClass.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(1, analysis.getMethodPairs().size());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
        methodsContain(analysis, "value");
    }

    static class BaseGenericClass<T> {
        T value;

        public T getValue() {
            return null;
        }

        public void setValue(T value) {
        }
    }

    @Parcel
    static class Value {}

    @Parcel
    static class Concrete extends ParcelableAnalysisTest.BaseGenericClass<ParcelableAnalysisTest.Value> {}

    @Parcel(Serialization.BEAN)
    static class ConcreteBean extends ParcelableAnalysisTest.BaseGenericClass<ParcelableAnalysisTest.Value> {}

    @Test
    public void testGenericDeclaredType() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.Concrete.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(1, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
        fieldsContain(analysis, "value");
        Assert.assertEquals(astClassFactory.getType(ParcelableAnalysisTest.Value.class), analysis.getFieldPairs().get(0).getReference().getType());
    }

    @Test
    public void testGenericMethodDeclaredType() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.ConcreteBean.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(1, analysis.getMethodPairs().size());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
        fieldsContain(analysis, "value");
        Assert.assertEquals(astClassFactory.getType(ParcelableAnalysisTest.Value.class), analysis.getMethodPairs().get(0).getReference().getType());
    }

    @Parcel
    static class CallbackExample {
        @OnWrap
        public void onWrap() {
        }

        @OnUnwrap
        public void onUnwrap() {
        }
    }

    @Test
    public void testCallbackAnalysis() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.CallbackExample.class);
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
        Assert.assertEquals(1, analysis.getWrapCallbacks().size());
        Assert.assertEquals(1, analysis.getUnwrapCallbacks().size());
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
    }

    @Parcel
    static class CallbackInheritanceExample extends ParcelableAnalysisTest.CallbackExample {
        @OnWrap
        public void onWrap2() {
        }

        @OnUnwrap
        public void onUnwrap2() {
        }
    }

    @Test
    public void testCallbackInheritanceAnalysis() {
        ParcelableDescriptor analysis = analyze(ParcelableAnalysisTest.CallbackInheritanceExample.class);
        Assert.assertNull(analysis.getParcelConverterType());
        Assert.assertNotNull(analysis.getConstructorPair());
        Assert.assertEquals(0, analysis.getFieldPairs().size());
        Assert.assertEquals(0, analysis.getMethodPairs().size());
        Assert.assertEquals(0, analysis.getConstructorPair().getWriteReferences().size());
        Assert.assertEquals(2, analysis.getWrapCallbacks().size());
        Assert.assertEquals(2, analysis.getUnwrapCallbacks().size());
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
    }

    @Parcel
    static class CallbackWrapReturnNonNull {
        @OnWrap
        public String onWrap() {
            return null;
        }
    }

    @Test
    public void testCallbackWrapReturnNonNull() {
        errors(ParcelableAnalysisTest.CallbackWrapReturnNonNull.class);
    }

    @Parcel
    static class CallbackUnwrapReturnNonNull {
        @OnUnwrap
        public String onUnwrap() {
            return null;
        }
    }

    @Test
    public void testCallbackUnwrapReturnNonNull() {
        errors(ParcelableAnalysisTest.CallbackUnwrapReturnNonNull.class);
    }

    @Parcel
    static class CallbackWrapAcceptValue {
        @OnWrap
        public void onWrap(String value) {
        }
    }

    @Test
    public void testCallbackWrapAcceptValue() {
        errors(ParcelableAnalysisTest.CallbackWrapAcceptValue.class);
    }

    @Parcel
    static class CallbackUnwrapAcceptValue {
        @OnUnwrap
        public void onUnwrap(String value) {
        }
    }

    @Test
    public void testCallbackUnwrapAcceptValue() {
        errors(ParcelableAnalysisTest.CallbackUnwrapReturnNonNull.class);
    }

    @Parcel(Serialization.BEAN)
    static class MismatchedBeanTypes {
        Integer value;

        @ParcelConstructor
        public MismatchedBeanTypes(@ASTClassFactory.ASTParameterName("value")
        int value) {
        }

        public Integer getValue() {
            return value;
        }
    }

    @Test
    public void testMismatchedBeanTypes() {
        errors(ParcelableAnalysisTest.MismatchedBeanTypes.class);
    }

    @Parcel(Serialization.BEAN)
    static class MismatchedPrimitiveBeanTypes {
        Integer value;

        @ParcelConstructor
        public MismatchedPrimitiveBeanTypes(@ASTClassFactory.ASTParameterName("value")
        int value) {
        }

        public Integer getValue() {
            return value;
        }
    }

    @Test
    public void testMismatchedPrimitiveBeanTypes() {
        errors(ParcelableAnalysisTest.MismatchedPrimitiveBeanTypes.class);
    }

    @Parcel(Serialization.BEAN)
    static class MismatchedPrimitiveBeanTypes2 {
        int value;

        @ParcelConstructor
        public MismatchedPrimitiveBeanTypes2(@ASTClassFactory.ASTParameterName("value")
        Integer value) {
        }

        public int getValue() {
            return value;
        }
    }

    @Test
    public void testmismatchedPrimitiveBeanTypes2() {
        analyze(ParcelableAnalysisTest.MismatchedPrimitiveBeanTypes2.class);
        Assert.assertFalse(messager.getMessage(), messager.isErrored());
    }

    @Parcel
    static class UndefinedGeneric<T> {
        T value;
    }

    @Test
    public void testUndefinedGeneric() {
        analyze(ParcelableAnalysisTest.UndefinedGeneric.class);
        Assert.assertTrue(messager.getMessage(), messager.isErrored());
    }
}

