/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2019 the AndroidAnnotations project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.helper;


import CanonicalNameConstants.INTEGER;
import CanonicalNameConstants.LONG;
import CanonicalNameConstants.STRING;
import CanonicalNameConstants.VIEW;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.TextView;
import java.lang.annotation.Annotation;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.androidannotations.ElementValidation;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class ValidatorParameterHelperTest {
    private ValidatorParameterHelper validator;

    @Test
    public void anyOfTypes() throws Exception {
        ExecutableElement executableElement = ValidatorParameterHelperTest.createMethod(Integer.class, Long.class);
        ElementValidation valid = new ElementValidation("", executableElement);
        validator.anyOfTypes(INTEGER, LONG).multiple().validate(executableElement, valid);
        Assert.assertTrue(valid.isValid());
    }

    @Test
    public void extendsViewType() throws Exception {
        ExecutableElement executableElement = ValidatorParameterHelperTest.createMethod(TextView.class);
        ElementValidation valid = new ElementValidation("", executableElement);
        validator.extendsType(VIEW).validate(executableElement, valid);
        Assert.assertTrue(valid.isValid());
    }

    @Test
    public void primitiveParam() throws Exception {
        ExecutableElement executableElement = ValidatorParameterHelperTest.createMethod(int.class, Integer.class);
        ElementValidation valid = new ElementValidation("", executableElement);
        validator.primitiveOrWrapper(TypeKind.INT).multiple().validate(executableElement, valid);
        Assert.assertTrue(valid.isValid());
    }

    @Test
    public void annotatedParam() throws Exception {
        ExecutableElement executableElement = ValidatorParameterHelperTest.createMethod(ValidatorParameterHelperTest.p(String.class, ValidatorParameterHelperTest.TestAnnotation.class));
        ElementValidation valid = new ElementValidation("", executableElement);
        validator.annotatedWith(ValidatorParameterHelperTest.TestAnnotation.class).validate(executableElement, valid);
        Assert.assertTrue(valid.isValid());
    }

    @Test
    public void optionalStringParam() throws Exception {
        ExecutableElement executableElement = ValidatorParameterHelperTest.createMethod(String.class);
        ElementValidation valid = new ElementValidation("", executableElement);
        validator.type(STRING).optional().validate(executableElement, valid);
        Assert.assertTrue(valid.isValid());
        ExecutableElement withoutArgument = ValidatorParameterHelperTest.createMethod();
        ElementValidation valid2 = new ElementValidation("", executableElement);
        validator.type(STRING).optional().validate(withoutArgument, valid2);
        Assert.assertTrue(valid2.isValid());
    }

    @Test
    public void stringParam() throws Exception {
        ExecutableElement executableElement = ValidatorParameterHelperTest.createMethod(String.class);
        ElementValidation valid = new ElementValidation("", executableElement);
        validator.type(STRING).validate(executableElement, valid);
        Assert.assertTrue(valid.isValid());
        executableElement = ValidatorParameterHelperTest.createMethod(int.class);
        valid = new ElementValidation("", executableElement);
        validator.noParam().validate(executableElement, valid);
        Assert.assertFalse(valid.isValid());
    }

    @Test
    public void anyParam() throws Exception {
        ExecutableElement executableElement = ValidatorParameterHelperTest.createMethod(String.class);
        ElementValidation valid = new ElementValidation("", executableElement);
        validator.anyType().validate(executableElement, valid);
        Assert.assertTrue(valid.isValid());
    }

    @Test
    public void noParam() throws Exception {
        ExecutableElement executableElement = ValidatorParameterHelperTest.createMethod();
        ElementValidation valid = new ElementValidation("", executableElement);
        validator.noParam().validate(executableElement, valid);
        Assert.assertTrue(valid.isValid());
        executableElement = ValidatorParameterHelperTest.createMethod(int.class);
        valid = new ElementValidation("", executableElement);
        validator.noParam().validate(executableElement, valid);
        Assert.assertFalse(valid.isValid());
    }

    @Test
    public void inOrderSuccess() throws Exception {
        ExecutableElement executableElement = ValidatorParameterHelperTest.createMethod(boolean.class, int.class);
        ElementValidation valid = new ElementValidation("", executableElement);
        // 
        // 
        // 
        // 
        validator.inOrder().type(boolean.class.getName()).type(long.class.getName()).optional().type(int.class.getName()).validate(executableElement, valid);
        Assert.assertTrue(valid.isValid());
    }

    @Test
    public void inOrderSuccess2() throws Exception {
        ExecutableElement executableElement = ValidatorParameterHelperTest.createMethod(boolean.class, boolean.class, int.class);
        ElementValidation valid = new ElementValidation("", executableElement);
        // 
        // 
        // 
        // 
        validator.inOrder().type(boolean.class.getName()).multiple().type(long.class.getName()).optional().type(int.class.getName()).validate(executableElement, valid);
        Assert.assertTrue(valid.isValid());
    }

    @Test
    public void inOrderFail() throws Exception {
        ExecutableElement executableElement = ValidatorParameterHelperTest.createMethod(AdapterView.class, long.class, boolean.class, Bundle.class);
        ElementValidation valid = new ElementValidation("", executableElement);
        // 
        // 
        // 
        // 
        validator.inOrder().type(boolean.class.getName()).optional().type(long.class.getName()).optional().type(int.class.getName()).optional().validate(executableElement, valid);
        Assert.assertFalse(valid.isValid());
    }

    @Test
    public void inOrderFail2() throws Exception {
        ExecutableElement executableElement = ValidatorParameterHelperTest.createMethod(long.class, boolean.class);
        ElementValidation valid = new ElementValidation("", executableElement);
        // 
        // 
        // 
        // 
        validator.inOrder().type(boolean.class.getName()).multiple().type(long.class.getName()).optional().type(int.class.getName()).optional().validate(executableElement, valid);
        Assert.assertFalse(valid.isValid());
    }

    @Test
    public void inOrderFail3() throws Exception {
        ExecutableElement executableElement = ValidatorParameterHelperTest.createMethod(boolean.class, boolean.class, long.class, Bundle.class);
        ElementValidation valid = new ElementValidation("", executableElement);
        // 
        // 
        // 
        // 
        validator.inOrder().type(boolean.class.getName()).multiple().type(long.class.getName()).optional().type(int.class.getName()).optional().validate(executableElement, valid);
        Assert.assertFalse(valid.isValid());
    }

    @Test
    public void inOrderFail4() throws Exception {
        ExecutableElement executableElement = ValidatorParameterHelperTest.createMethod(int.class, long.class, boolean.class);
        ElementValidation valid = new ElementValidation("", executableElement);
        // 
        // 
        // 
        // 
        validator.inOrder().type(boolean.class.getName()).optional().type(long.class.getName()).optional().type(int.class.getName()).optional().validate(executableElement, valid);
        Assert.assertFalse(valid.isValid());
    }

    @Test
    public void itemSelect() {
        ExecutableElement executableElement = ValidatorParameterHelperTest.createMethod(boolean.class, int.class);
        ElementValidation valid = new ElementValidation("", executableElement);
        // 
        // 
        // 
        validator.inOrder().primitiveOrWrapper(TypeKind.BOOLEAN).anyType().optional().validate(executableElement, valid);
        Assert.assertTrue(valid.isValid());
    }

    private @interface TestAnnotation {}

    private interface ClassAwareTypeMirror extends TypeMirror {
        Class<?> getMirrorClass();
    }

    private static final class IsSubtypeAnswer implements Answer<Object> {
        @Override
        public Boolean answer(InvocationOnMock invocation) throws Throwable {
            Object[] args = invocation.getArguments();
            ensureValidArgs(args);
            Class<?> subClass = ((ValidatorParameterHelperTest.ClassAwareTypeMirror) (args[0])).getClass();
            Class<?> superClass = ((ValidatorParameterHelperTest.ClassAwareTypeMirror) (args[1])).getClass();
            return superClass.isAssignableFrom(subClass);
        }

        private void ensureValidArgs(Object[] args) {
            if ((args.length) != 2) {
                throw new IllegalArgumentException("invalid argument count");
            }
            if (!((args[0]) instanceof ValidatorParameterHelperTest.ClassAwareTypeMirror)) {
                throw new IllegalArgumentException(("first argument has to be an instance of " + (ValidatorParameterHelperTest.ClassAwareTypeMirror.class)));
            }
            if (!((args[1]) instanceof ValidatorParameterHelperTest.ClassAwareTypeMirror)) {
                throw new IllegalArgumentException(("second argument has to be an instance of " + (ValidatorParameterHelperTest.ClassAwareTypeMirror.class)));
            }
        }
    }

    private static final class TypeElementFromQualifiedNameAnswer implements Answer<TypeElement> {
        @Override
        public TypeElement answer(InvocationOnMock invocation) throws Throwable {
            Object[] args = invocation.getArguments();
            ensureValidArgs(args);
            TypeElement typeElement = Mockito.mock(TypeElement.class);
            Class<?> typeClass = Class.forName(((String) (args[0])));
            Mockito.doReturn(ValidatorParameterHelperTest.mockTypeMirror(typeClass)).when(typeElement).asType();
            return typeElement;
        }

        private void ensureValidArgs(Object[] args) {
            if ((args.length) != 1) {
                throw new IllegalArgumentException("invalid argument count");
            }
            if (!((args[0]) instanceof String)) {
                throw new IllegalArgumentException(("first argument has to be an instance of " + (String.class)));
            }
        }
    }

    private static final class Param {
        final Class<?> type;

        final Class<? extends Annotation> annotationType;

        Param(Class<?> type, Class<? extends Annotation> annotationType) {
            this.type = type;
            this.annotationType = annotationType;
        }
    }
}

