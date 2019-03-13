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


import android.os.Parcel;
import javax.inject.Inject;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.bootstrap.Bootstrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 *
 *
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
@Bootstrap
public class ParcelableGeneratorTest {
    private static final String TEST_VALUE = "test";

    @Inject
    private ParcelableGenerator generator;

    @Inject
    private ASTClassFactory astClassFactory;

    @Inject
    private CodeGenerationUtil codeGenerationUtil;

    private ASTType targetType;

    private ASTType converterType;

    private Parcel parcel;

    private Target target;

    @Test
    public void testFieldSerialization() throws Exception {
        ParcelableDescriptor descriptor = new ParcelableDescriptor();
        ASTField astField = targetType.getFields().iterator().next();
        descriptor.getFieldPairs().add(new ReferencePair<FieldReference>("value", new FieldReference(targetType, "value", astField, astField.getASTType()), new FieldReference(targetType, "value", astField, astField.getASTType()), null));
        testSerialization(descriptor);
    }

    @Test
    public void testFieldConverterSerialization() throws Exception {
        ParcelableDescriptor descriptor = new ParcelableDescriptor();
        ASTField astField = targetType.getFields().iterator().next();
        descriptor.getFieldPairs().add(new ReferencePair<FieldReference>("value", new FieldReference(targetType, "value", astField, astField.getASTType()), new FieldReference(targetType, "value", astField, astField.getASTType()), converterType));
        testSerialization(descriptor);
    }

    @Test
    public void testMethodSerialization() throws Exception {
        ParcelableDescriptor descriptor = new ParcelableDescriptor();
        ASTMethod getter = getMethod("getValue", targetType.getMethods());
        ASTMethod setter = getMethod("setValue", targetType.getMethods());
        ASTType stringType = astClassFactory.getType(String.class);
        descriptor.getMethodPairs().add(new ReferencePair<MethodReference>("value", new MethodReference(targetType, targetType, "value", stringType, setter), new MethodReference(targetType, targetType, "value", stringType, getter), null));
        testSerialization(descriptor);
    }

    @Test
    public void testMethodConverterSerialization() throws Exception {
        ParcelableDescriptor descriptor = new ParcelableDescriptor();
        ASTMethod getter = getMethod("getValue", targetType.getMethods());
        ASTMethod setter = getMethod("setValue", targetType.getMethods());
        ASTType stringType = astClassFactory.getType(String.class);
        descriptor.getMethodPairs().add(new ReferencePair<MethodReference>("value", new MethodReference(targetType, targetType, "value", stringType, setter), new MethodReference(targetType, targetType, "value", stringType, getter), converterType));
        testSerialization(descriptor);
    }

    @Test
    public void testConstructorFieldSerialization() throws Exception {
        ParcelableDescriptor descriptor = new ParcelableDescriptor();
        ASTType stringType = astClassFactory.getType(String.class);
        ASTConstructor constructor = getConstructor(targetType.getConstructors(), stringType);
        ConstructorReference constructorReference = new ConstructorReference(constructor);
        ASTField astField = targetType.getFields().iterator().next();
        constructorReference.putReference(constructor.getParameters().get(0), new FieldReference(targetType, "value", astField, astField.getASTType()));
        descriptor.setConstructorPair(constructorReference);
        testSerialization(descriptor);
    }

    @Test
    public void testConstructorFieldConverterSerialization() throws Exception {
        ParcelableDescriptor descriptor = new ParcelableDescriptor();
        ASTType stringType = astClassFactory.getType(String.class);
        ASTConstructor constructor = getConstructor(targetType.getConstructors(), stringType);
        ConstructorReference constructorReference = new ConstructorReference(constructor);
        ASTField astField = targetType.getFields().iterator().next();
        constructorReference.putReference(constructor.getParameters().get(0), new FieldReference(targetType, "value", astField, astField.getASTType()));
        constructorReference.putConverter(constructor.getParameters().get(0), converterType);
        descriptor.setConstructorPair(constructorReference);
        testSerialization(descriptor);
    }

    @Test
    public void testConstructorMethodSerialization() throws Exception {
        ParcelableDescriptor descriptor = new ParcelableDescriptor();
        ASTMethod getter = getMethod("getValue", targetType.getMethods());
        ASTType stringType = astClassFactory.getType(String.class);
        ASTConstructor constructor = getConstructor(targetType.getConstructors(), stringType);
        ConstructorReference constructorReference = new ConstructorReference(constructor);
        ASTField astField = targetType.getFields().iterator().next();
        constructorReference.putReference(constructor.getParameters().get(0), new FieldReference(targetType, "value", astField, astField.getASTType()));
        descriptor.setConstructorPair(constructorReference);
        testSerialization(descriptor);
    }

    @Test
    public void testConstructorMethodConverterSerialization() throws Exception {
        ParcelableDescriptor descriptor = new ParcelableDescriptor();
        ASTMethod getter = getMethod("getValue", targetType.getMethods());
        ASTType stringType = astClassFactory.getType(String.class);
        ASTConstructor constructor = getConstructor(targetType.getConstructors(), stringType);
        ConstructorReference constructorReference = new ConstructorReference(constructor);
        ASTField astField = targetType.getFields().iterator().next();
        constructorReference.putReference(constructor.getParameters().get(0), new FieldReference(targetType, "value", astField, astField.getASTType()));
        constructorReference.putConverter(constructor.getParameters().get(0), converterType);
        descriptor.setConstructorPair(constructorReference);
        testSerialization(descriptor);
    }
}

