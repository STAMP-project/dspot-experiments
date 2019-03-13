package net.bytebuddy.dynamic;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class TargetTypeTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private TypeDescription targetType;

    @Mock
    private TypeDescription componentType;

    @Test
    public void testIsNotTargetType() throws Exception {
        Mockito.when(typeDescription.represents(TargetType.class)).thenReturn(false);
        MatcherAssert.assertThat(TargetType.resolve(typeDescription, targetType), CoreMatchers.is(typeDescription));
    }

    @Test
    public void testIsTargetType() throws Exception {
        Mockito.when(typeDescription.represents(TargetType.class)).thenReturn(true);
        MatcherAssert.assertThat(TargetType.resolve(typeDescription, targetType), CoreMatchers.is(targetType));
    }

    @Test
    public void testIsNotTargetTypeArray() throws Exception {
        Mockito.when(typeDescription.isArray()).thenReturn(true);
        Mockito.when(typeDescription.getComponentType()).thenReturn(componentType);
        Mockito.when(componentType.represents(TargetType.class)).thenReturn(false);
        MatcherAssert.assertThat(TargetType.resolve(typeDescription, targetType), CoreMatchers.is(typeDescription));
    }

    @Test
    public void testIsTargetTypeArray() throws Exception {
        Mockito.when(typeDescription.isArray()).thenReturn(true);
        Mockito.when(typeDescription.getComponentType()).thenReturn(componentType);
        Mockito.when(componentType.represents(TargetType.class)).thenReturn(true);
        TypeDescription resolvedType = TargetType.resolve(typeDescription, targetType);
        MatcherAssert.assertThat(resolvedType.isArray(), CoreMatchers.is(true));
        MatcherAssert.assertThat(resolvedType.getComponentType(), CoreMatchers.is(targetType));
    }

    @Test
    public void testConstructorIsHidden() throws Exception {
        MatcherAssert.assertThat(TargetType.class.getDeclaredConstructors().length, CoreMatchers.is(1));
        Constructor<?> constructor = TargetType.class.getDeclaredConstructor();
        MatcherAssert.assertThat(Modifier.isPrivate(constructor.getModifiers()), CoreMatchers.is(true));
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            Assert.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause().getClass(), CoreMatchers.<Class<?>>is(UnsupportedOperationException.class));
        }
    }

    @Test
    public void testTypeIsFinal() throws Exception {
        MatcherAssert.assertThat(Modifier.isFinal(TargetType.class.getModifiers()), CoreMatchers.is(true));
    }
}

