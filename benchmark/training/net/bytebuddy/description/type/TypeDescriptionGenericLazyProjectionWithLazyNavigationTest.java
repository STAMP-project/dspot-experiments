package net.bytebuddy.description.type;


import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class TypeDescriptionGenericLazyProjectionWithLazyNavigationTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private TypeDescription rawSuperType;

    @Mock
    private TypeDescription.Generic superType;

    @Test
    public void testLazySuperClass() throws Exception {
        Mockito.when(typeDescription.getSuperClass()).thenReturn(superType);
        MatcherAssert.assertThat(new TypeDescriptionGenericLazyProjectionWithLazyNavigationTest.AssertingLazyType(typeDescription).getSuperClass().asErasure(), CoreMatchers.is(rawSuperType));
    }

    @Test
    public void testUndefinedLazySuperClass() throws Exception {
        MatcherAssert.assertThat(new TypeDescriptionGenericLazyProjectionWithLazyNavigationTest.AssertingLazyType(typeDescription).getSuperClass(), CoreMatchers.nullValue(TypeDescription.Generic.class));
    }

    @Test
    public void testInterfaceType() throws Exception {
        Mockito.when(typeDescription.getInterfaces()).thenReturn(new TypeList.Generic.Explicit(superType));
        MatcherAssert.assertThat(new TypeDescriptionGenericLazyProjectionWithLazyNavigationTest.AssertingLazyType(typeDescription).getInterfaces().getOnly().asErasure(), CoreMatchers.is(rawSuperType));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInterfaceTypeOutOfBounds() throws Exception {
        Mockito.when(typeDescription.getInterfaces()).thenReturn(new TypeList.Generic.Explicit(superType));
        new TypeDescriptionGenericLazyProjectionWithLazyNavigationTest.AssertingLazyType(typeDescription).getInterfaces().get(1);
    }

    private static class AssertingLazyType extends TypeDescription.Generic.LazyProjection.WithLazyNavigation {
        private final TypeDescription typeDescription;

        private AssertingLazyType(TypeDescription typeDescription) {
            this.typeDescription = typeDescription;
        }

        public AnnotationList getDeclaredAnnotations() {
            throw new AssertionError();
        }

        public TypeDescription asErasure() {
            return typeDescription;
        }

        @Override
        protected TypeDescription.Generic resolve() {
            throw new AssertionError();
        }
    }
}

