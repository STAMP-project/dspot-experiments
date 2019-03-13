package org.robobinding.codegen.viewbinding;


import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;
import org.robobinding.codegen.apt.element.SetterElement;
import org.robobinding.codegen.apt.type.WrappedDeclaredType;
import org.robobinding.codegen.apt.type.WrappedPrimitiveType;


/**
 *
 *
 * @since 1.0
 * @author Cheng Wei
 */
public class SimpleOneWayPropertyInfoTest {
    private Mockery context;

    @Test
    public void givenPrimitivePropertyType_thenReturnBoxedType() {
        final SetterElement setter = context.mock(SetterElement.class);
        final WrappedPrimitiveType primitiveType = context.mock(WrappedPrimitiveType.class);
        context.checking(new Expectations() {
            {
                oneOf(setter).parameterType();
                will(returnValue(primitiveType));
                oneOf(primitiveType).isPrimitive();
                will(returnValue(true));
                oneOf(primitiveType).boxedClassName();
                will(returnValue(Integer.class.getName()));
            }
        });
        SimpleOneWayPropertyInfo info = new SimpleOneWayPropertyInfo(setter);
        Assert.assertThat(info.propertyType(), Matchers.equalTo(Integer.class.getName()));
    }

    @Test
    public void givenObjectPropertyType_thenReturnSameType() {
        final SetterElement setter = context.mock(SetterElement.class);
        final WrappedDeclaredType declaredType = context.mock(WrappedDeclaredType.class);
        context.checking(new Expectations() {
            {
                oneOf(setter).parameterType();
                will(returnValue(declaredType));
                oneOf(declaredType).isPrimitive();
                will(returnValue(false));
                oneOf(declaredType).className();
                will(returnValue(Object.class.getName()));
            }
        });
        SimpleOneWayPropertyInfo info = new SimpleOneWayPropertyInfo(setter);
        Assert.assertThat(info.propertyType(), Matchers.equalTo(Object.class.getName()));
    }

    @Test
    public void shouldGetCorrectAttributeTypeName() {
        final SetterElement setter = context.mock(SetterElement.class);
        context.checking(new Expectations() {
            {
                oneOf(setter).propertyName();
                will(returnValue("prop1"));
            }
        });
        SimpleOneWayPropertyInfo info = new SimpleOneWayPropertyInfo(setter);
        Assert.assertThat(info.bindingTypeName(), Matchers.equalTo("Prop1Attribute"));
    }
}

