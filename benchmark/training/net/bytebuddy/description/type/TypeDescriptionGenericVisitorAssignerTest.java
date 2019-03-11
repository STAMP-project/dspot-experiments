package net.bytebuddy.description.type;


import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;
import static net.bytebuddy.description.type.TypeDescription.Generic.Visitor.Assigner.INSTANCE;


public class TypeDescriptionGenericVisitorAssignerTest {
    private TypeDescription.Generic collectionWildcard;

    private TypeDescription.Generic collectionRaw;

    private TypeDescription.Generic collectionTypeVariableT;

    private TypeDescription.Generic collectionTypeVariableS;

    private TypeDescription.Generic collectionTypeVariableU;

    private TypeDescription.Generic collectionUpperBoundTypeVariableT;

    private TypeDescription.Generic collectionUpperBoundTypeVariableS;

    private TypeDescription.Generic collectionUpperBoundTypeVariableU;

    private TypeDescription.Generic collectionLowerBoundTypeVariableT;

    private TypeDescription.Generic collectionLowerBoundTypeVariableS;

    private TypeDescription.Generic collectionLowerBoundTypeVariableU;

    private TypeDescription.Generic listRaw;

    private TypeDescription.Generic listWildcard;

    private TypeDescription.Generic abstractListRaw;

    private TypeDescription.Generic arrayListRaw;

    private TypeDescription.Generic arrayListWildcard;

    private TypeDescription.Generic callableWildcard;

    private TypeDescription.Generic arrayListTypeVariableT;

    private TypeDescription.Generic arrayListTypeVariableS;

    private TypeDescription.Generic collectionRawArray;

    private TypeDescription.Generic listRawArray;

    private TypeDescription.Generic listWildcardArray;

    private TypeDescription.Generic arrayListRawArray;

    private TypeDescription.Generic stringArray;

    private TypeDescription.Generic objectArray;

    private TypeDescription.Generic objectNestedArray;

    private TypeDescription.Generic unboundWildcard;

    private TypeDescription.Generic typeVariableT;

    private TypeDescription.Generic typeVariableS;

    private TypeDescription.Generic typeVariableU;

    private TypeDescription.Generic typeVariableV;

    private TypeDescription.Generic arrayTypeVariableT;

    private TypeDescription.Generic arrayTypeVariableS;

    private TypeDescription.Generic arrayTypeVariableU;

    private TypeDescription.Generic arrayNestedTypeVariableT;

    @Test(expected = IllegalArgumentException.class)
    public void testAssignFromWildcardThrowsException() throws Exception {
        unboundWildcard.accept(INSTANCE);
    }

    @Test
    public void testAssignNonGenericTypeFromAssignableNonGenericType() throws Exception {
        MatcherAssert.assertThat(OBJECT.accept(INSTANCE).isAssignableFrom(TypeDescription.STRING.asGenericType()), CoreMatchers.is(true));
    }

    @Test
    public void testAssignNonGenericTypeFromNonAssignableNonGenericType() throws Exception {
        MatcherAssert.assertThat(TypeDescription.STRING.asGenericType().accept(INSTANCE).isAssignableFrom(OBJECT), CoreMatchers.is(false));
    }

    @Test
    public void testAssignObjectTypeFromAssignableGenericType() throws Exception {
        MatcherAssert.assertThat(OBJECT.accept(INSTANCE).isAssignableFrom(listWildcard), CoreMatchers.is(true));
    }

    @Test
    public void testAssignNonGenericTypeFromNonAssignableGenericType() throws Exception {
        MatcherAssert.assertThat(TypeDescription.STRING.asGenericType().accept(INSTANCE).isAssignableFrom(listWildcard), CoreMatchers.is(false));
    }

    @Test
    public void testAssignNonGenericSuperInterfaceTypeFromAssignableGenericInterfaceType() throws Exception {
        MatcherAssert.assertThat(collectionRaw.accept(INSTANCE).isAssignableFrom(listWildcard), CoreMatchers.is(true));
    }

    @Test
    public void testAssignNonGenericSuperInterfaceTypeFromAssignableGenericType() throws Exception {
        MatcherAssert.assertThat(collectionRaw.accept(INSTANCE).isAssignableFrom(arrayListWildcard), CoreMatchers.is(true));
    }

    @Test
    public void testAssignRawInterfaceTypeFromEqualGenericInterfaceType() throws Exception {
        MatcherAssert.assertThat(listRaw.accept(INSTANCE).isAssignableFrom(listWildcard), CoreMatchers.is(true));
    }

    @Test
    public void testAssignRawTypeFromEqualGenericType() throws Exception {
        MatcherAssert.assertThat(arrayListRaw.accept(INSTANCE).isAssignableFrom(arrayListWildcard), CoreMatchers.is(true));
    }

    @Test
    public void testAssignNonGenericSuperTypeFromAssignableGenericType() throws Exception {
        MatcherAssert.assertThat(abstractListRaw.accept(INSTANCE).isAssignableFrom(arrayListWildcard), CoreMatchers.is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssignNonGenericTypeFromWildcardThrowsException() throws Exception {
        OBJECT.accept(INSTANCE).isAssignableFrom(unboundWildcard);
    }

    @Test
    public void testAssignNonGenericTypeFromAssignableTypeVariable() throws Exception {
        MatcherAssert.assertThat(OBJECT.accept(INSTANCE).isAssignableFrom(typeVariableT), CoreMatchers.is(true));
    }

    @Test
    public void testAssignNonGenericTypeFromNonAssignableTypeVariable() throws Exception {
        MatcherAssert.assertThat(TypeDescription.STRING.asGenericType().accept(INSTANCE).isAssignableFrom(typeVariableT), CoreMatchers.is(false));
    }

    @Test
    public void testAssignNonGenericSuperArrayTypeFromAssignableGenericArrayType() throws Exception {
        MatcherAssert.assertThat(collectionRawArray.accept(INSTANCE).isAssignableFrom(listWildcardArray), CoreMatchers.is(true));
    }

    @Test
    public void testAssignRawArrayTypeFromEqualGenericArrayType() throws Exception {
        MatcherAssert.assertThat(listRawArray.accept(INSTANCE).isAssignableFrom(listWildcardArray), CoreMatchers.is(true));
    }

    @Test
    public void testAssignNonGenericArrayFromNonAssignableGenericArrayType() throws Exception {
        MatcherAssert.assertThat(stringArray.accept(INSTANCE).isAssignableFrom(listWildcardArray), CoreMatchers.is(false));
    }

    @Test
    public void testAssignNonGenericArrayFromAssignableGenericArrayType() throws Exception {
        MatcherAssert.assertThat(objectArray.accept(INSTANCE).isAssignableFrom(listWildcardArray), CoreMatchers.is(true));
    }

    @Test
    public void testAssignNonGenericArrayFromGenericArrayTypeOfIncompatibleArity() throws Exception {
        MatcherAssert.assertThat(objectNestedArray.accept(INSTANCE).isAssignableFrom(listWildcardArray), CoreMatchers.is(false));
    }

    @Test
    public void testAssignObjectTypeFromGenericArrayType() throws Exception {
        MatcherAssert.assertThat(OBJECT.accept(INSTANCE).isAssignableFrom(listWildcardArray), CoreMatchers.is(true));
    }

    @Test
    public void testAssignCloneableTypeFromGenericArrayType() throws Exception {
        MatcherAssert.assertThat(of(Cloneable.class).accept(INSTANCE).isAssignableFrom(listWildcardArray), CoreMatchers.is(true));
    }

    @Test
    public void testAssignSerializableTypeFromGenericArrayType() throws Exception {
        MatcherAssert.assertThat(of(Serializable.class).accept(INSTANCE).isAssignableFrom(listWildcardArray), CoreMatchers.is(true));
    }

    @Test
    public void testAssignTypeVariableFromNonGenericType() throws Exception {
        MatcherAssert.assertThat(typeVariableT.accept(INSTANCE).isAssignableFrom(OBJECT), CoreMatchers.is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssignTypeVariableFromWildcardTypeThrowsException() throws Exception {
        typeVariableT.accept(INSTANCE).isAssignableFrom(unboundWildcard);
    }

    @Test
    public void testAssignTypeVariableFromGenericArrayType() throws Exception {
        MatcherAssert.assertThat(typeVariableT.accept(INSTANCE).isAssignableFrom(listWildcardArray), CoreMatchers.is(false));
    }

    @Test
    public void testAssignTypeVariableFromParameterizedType() throws Exception {
        MatcherAssert.assertThat(typeVariableT.accept(INSTANCE).isAssignableFrom(listWildcard), CoreMatchers.is(false));
    }

    @Test
    public void testAssignTypeVariableFromEqualTypeVariable() throws Exception {
        MatcherAssert.assertThat(typeVariableT.accept(INSTANCE).isAssignableFrom(typeVariableT), CoreMatchers.is(true));
    }

    @Test
    public void testAssignTypeVariableFromNonAssignableWildcard() throws Exception {
        MatcherAssert.assertThat(typeVariableT.accept(INSTANCE).isAssignableFrom(typeVariableS), CoreMatchers.is(false));
    }

    @Test
    public void testAssignTypeVariableFromAssignableWildcard() throws Exception {
        MatcherAssert.assertThat(typeVariableT.accept(INSTANCE).isAssignableFrom(typeVariableU), CoreMatchers.is(true));
    }

    @Test
    public void testAssignGenericArrayFromAssignableGenericArray() throws Exception {
        MatcherAssert.assertThat(arrayTypeVariableT.accept(INSTANCE).isAssignableFrom(arrayTypeVariableU), CoreMatchers.is(true));
    }

    @Test
    public void testAssignGenericNestedArrayFromNonAssignableGenericArray() throws Exception {
        MatcherAssert.assertThat(arrayTypeVariableT.accept(INSTANCE).isAssignableFrom(arrayNestedTypeVariableT), CoreMatchers.is(false));
    }

    @Test
    public void testAssignGenericNestedArrayFromAssignableObjectArray() throws Exception {
        MatcherAssert.assertThat(of(Object[][].class).accept(INSTANCE).isAssignableFrom(arrayNestedTypeVariableT), CoreMatchers.is(true));
    }

    @Test
    public void testAssignGenericArrayFromNonAssignableGenericArray() throws Exception {
        MatcherAssert.assertThat(arrayTypeVariableT.accept(INSTANCE).isAssignableFrom(arrayTypeVariableS), CoreMatchers.is(false));
    }

    @Test
    public void testAssignGenericArrayFromNonAssignableNonGenericNonArrayType() throws Exception {
        MatcherAssert.assertThat(arrayTypeVariableT.accept(INSTANCE).isAssignableFrom(OBJECT), CoreMatchers.is(false));
    }

    @Test
    public void testAssignGenericArrayFromNonAssignableNonGenericArrayType() throws Exception {
        MatcherAssert.assertThat(arrayTypeVariableT.accept(INSTANCE).isAssignableFrom(objectArray), CoreMatchers.is(false));
    }

    @Test
    public void testAssignGenericArrayFromAssignableNonGenericArrayType() throws Exception {
        MatcherAssert.assertThat(listWildcardArray.accept(INSTANCE).isAssignableFrom(arrayListRawArray), CoreMatchers.is(true));
    }

    @Test
    public void testAssignGenericArrayFromNonAssignableTypeVariable() throws Exception {
        MatcherAssert.assertThat(arrayTypeVariableT.accept(INSTANCE).isAssignableFrom(typeVariableT), CoreMatchers.is(false));
    }

    @Test
    public void testAssignGenericArrayFromNonAssignableParameterizedType() throws Exception {
        MatcherAssert.assertThat(arrayTypeVariableT.accept(INSTANCE).isAssignableFrom(arrayListWildcard), CoreMatchers.is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssignGenericArrayFromWildcardThrowsException() throws Exception {
        arrayTypeVariableT.accept(INSTANCE).isAssignableFrom(unboundWildcard);
    }

    @Test
    public void testAssignParameterizedWildcardTypeFromEqualType() throws Exception {
        MatcherAssert.assertThat(collectionWildcard.accept(INSTANCE).isAssignableFrom(collectionWildcard), CoreMatchers.is(true));
    }

    @Test
    public void testAssignParameterizedWildcardTypeFromEqualRawType() throws Exception {
        MatcherAssert.assertThat(collectionWildcard.accept(INSTANCE).isAssignableFrom(collectionRaw), CoreMatchers.is(true));
    }

    @Test
    public void testAssignParameterizedWildcardTypeFromAssignableParameterizedWildcardType() throws Exception {
        MatcherAssert.assertThat(collectionWildcard.accept(INSTANCE).isAssignableFrom(arrayListWildcard), CoreMatchers.is(true));
    }

    @Test
    public void testAssignParameterizedWildcardTypeFromAssignableParameterizedNonWildcardTypeType() throws Exception {
        MatcherAssert.assertThat(collectionWildcard.accept(INSTANCE).isAssignableFrom(arrayListTypeVariableT), CoreMatchers.is(true));
    }

    @Test
    public void testAssignParameterizedWildcardTypeFromAssignableTypeVariableType() throws Exception {
        MatcherAssert.assertThat(collectionWildcard.accept(INSTANCE).isAssignableFrom(typeVariableV), CoreMatchers.is(true));
    }

    @Test
    public void testAssignParameterizedWildcardTypeFromNonAssignableRawType() throws Exception {
        MatcherAssert.assertThat(collectionWildcard.accept(INSTANCE).isAssignableFrom(TypeDescription.STRING.asGenericType()), CoreMatchers.is(false));
    }

    @Test
    public void testAssignParameterizedWildcardTypeFromNonAssignableParameterizedType() throws Exception {
        MatcherAssert.assertThat(collectionWildcard.accept(INSTANCE).isAssignableFrom(callableWildcard), CoreMatchers.is(false));
    }

    @Test
    public void testAssignParameterizedWildcardTypeFromNonAssignableGenericArrayType() throws Exception {
        MatcherAssert.assertThat(collectionWildcard.accept(INSTANCE).isAssignableFrom(arrayTypeVariableT), CoreMatchers.is(false));
    }

    @Test
    public void testAssignParameterizedWildcardTypeFromNonAssignableTypeVariableType() throws Exception {
        MatcherAssert.assertThat(collectionWildcard.accept(INSTANCE).isAssignableFrom(typeVariableT), CoreMatchers.is(false));
    }

    @Test
    public void testAssignParameterizedTypeVariableTypeFromEqualParameterizedTypeVariableTypeType() throws Exception {
        MatcherAssert.assertThat(collectionTypeVariableT.accept(INSTANCE).isAssignableFrom(collectionTypeVariableT), CoreMatchers.is(true));
    }

    @Test
    public void testAssignParameterizedTypeVariableTypeFromAssignableParameterizedTypeVariableTypeType() throws Exception {
        MatcherAssert.assertThat(collectionTypeVariableT.accept(INSTANCE).isAssignableFrom(arrayListTypeVariableT), CoreMatchers.is(true));
    }

    @Test
    public void testAssignParameterizedTypeVariableTypeFromNonAssignableParameterizedTypeVariableTypeType() throws Exception {
        MatcherAssert.assertThat(collectionTypeVariableT.accept(INSTANCE).isAssignableFrom(arrayListTypeVariableS), CoreMatchers.is(false));
    }

    @Test
    public void testAssignUpperBoundFromAssignableBound() throws Exception {
        MatcherAssert.assertThat(collectionUpperBoundTypeVariableT.accept(INSTANCE).isAssignableFrom(collectionTypeVariableT), CoreMatchers.is(true));
    }

    @Test
    public void testAssignUpperBoundFromAssignableBoundSuperType() throws Exception {
        MatcherAssert.assertThat(collectionUpperBoundTypeVariableT.accept(INSTANCE).isAssignableFrom(collectionTypeVariableU), CoreMatchers.is(true));
    }

    @Test
    public void testAssignUpperBoundFromAssignableUpperBoundSuperType() throws Exception {
        MatcherAssert.assertThat(collectionUpperBoundTypeVariableT.accept(INSTANCE).isAssignableFrom(collectionUpperBoundTypeVariableU), CoreMatchers.is(true));
    }

    @Test
    public void testAssignUpperBoundFromAssignableUpperBoundEqualType() throws Exception {
        MatcherAssert.assertThat(collectionUpperBoundTypeVariableT.accept(INSTANCE).isAssignableFrom(collectionTypeVariableU), CoreMatchers.is(true));
    }

    @Test
    public void testAssignUpperBoundFromNonAssignableBoundType() throws Exception {
        MatcherAssert.assertThat(collectionUpperBoundTypeVariableT.accept(INSTANCE).isAssignableFrom(collectionTypeVariableS), CoreMatchers.is(false));
    }

    @Test
    public void testAssignUpperBoundFromNonAssignableUpperBoundType() throws Exception {
        MatcherAssert.assertThat(collectionUpperBoundTypeVariableT.accept(INSTANCE).isAssignableFrom(collectionUpperBoundTypeVariableS), CoreMatchers.is(false));
    }

    @Test
    public void testAssignUpperBoundFromLowerpperBoundType() throws Exception {
        MatcherAssert.assertThat(collectionUpperBoundTypeVariableT.accept(INSTANCE).isAssignableFrom(collectionLowerBoundTypeVariableT), CoreMatchers.is(false));
    }

    @Test
    public void testAssignLowerBoundFromAssignableBound() throws Exception {
        MatcherAssert.assertThat(collectionLowerBoundTypeVariableT.accept(INSTANCE).isAssignableFrom(collectionTypeVariableT), CoreMatchers.is(true));
    }

    @Test
    public void testAssignLowerBoundFromAssignableBoundSuperType() throws Exception {
        MatcherAssert.assertThat(collectionLowerBoundTypeVariableU.accept(INSTANCE).isAssignableFrom(collectionTypeVariableT), CoreMatchers.is(true));
    }

    @Test
    public void testAssignLowerBoundFromAssignableUpperBoundSuperType() throws Exception {
        MatcherAssert.assertThat(collectionLowerBoundTypeVariableU.accept(INSTANCE).isAssignableFrom(collectionLowerBoundTypeVariableT), CoreMatchers.is(true));
    }

    @Test
    public void testAssigLowerBoundFromAssignableUpperBoundEqualType() throws Exception {
        MatcherAssert.assertThat(collectionLowerBoundTypeVariableU.accept(INSTANCE).isAssignableFrom(collectionTypeVariableT), CoreMatchers.is(true));
    }

    @Test
    public void testAssignLowerBoundFromNonAssignableBoundType() throws Exception {
        MatcherAssert.assertThat(collectionLowerBoundTypeVariableT.accept(INSTANCE).isAssignableFrom(collectionTypeVariableS), CoreMatchers.is(false));
    }

    @Test
    public void testAssignLowerBoundFromNonAssignableUpperBoundType() throws Exception {
        MatcherAssert.assertThat(collectionLowerBoundTypeVariableT.accept(INSTANCE).isAssignableFrom(collectionLowerBoundTypeVariableS), CoreMatchers.is(false));
    }

    @Test
    public void testAssignLowerBoundFromLowerpperBoundType() throws Exception {
        MatcherAssert.assertThat(collectionLowerBoundTypeVariableT.accept(INSTANCE).isAssignableFrom(collectionUpperBoundTypeVariableT), CoreMatchers.is(false));
    }

    @Test
    public void testAssignLowerBoundFromAssignableBoundSubType() throws Exception {
        MatcherAssert.assertThat(collectionLowerBoundTypeVariableU.accept(INSTANCE).isAssignableFrom(collectionTypeVariableT), CoreMatchers.is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssignParameterizedTypeFromWildcardTypeThrowsException() throws Exception {
        collectionWildcard.accept(INSTANCE).isAssignableFrom(unboundWildcard);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssignIncompatibleParameterizedTypesThrowsException() throws Exception {
        TypeDescription.Generic source = Mockito.mock(TypeDescription.Generic.class);
        TypeDescription.Generic target = Mockito.mock(TypeDescription.Generic.class);
        TypeDescription erasure = Mockito.mock(TypeDescription.class);
        Mockito.when(source.asErasure()).thenReturn(erasure);
        Mockito.when(target.asErasure()).thenReturn(erasure);
        Mockito.when(source.getTypeArguments()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(target.getTypeArguments()).thenReturn(new TypeList.Generic.Explicit(Mockito.mock(TypeDescription.Generic.class)));
        new TypeDescription.Generic.Visitor.Assigner.Dispatcher.ForParameterizedType(target).onParameterizedType(source);
    }

    @SuppressWarnings({ "unused", "unchecked" })
    private static class GenericTypes<T, S, U extends T, V extends List<?>> {
        private Collection collectionRaw;

        private Collection<?> collectionWildcard;

        private Collection<T> collectionTypeVariableT;

        private Collection<S> collectionTypeVariableS;

        private Collection<U> collectionTypeVariableU;

        private Collection<? extends T> collectionUpperBoundTypeVariableT;

        private Collection<? extends S> collectionUpperBoundTypeVariableS;

        private Collection<? extends U> collectionUpperBoundTypeVariableU;

        private Collection<? super T> collectionLowerBoundTypeVariableT;

        private Collection<? super S> collectionLowerBoundTypeVariableS;

        private Collection<? super U> collectionLowerBoundTypeVariableU;

        private Collection[] collectionRawArray;

        private List listRaw;

        private List<?> listWildcard;

        private List[] listRawArray;

        private List<?>[] listWildcardArray;

        private AbstractList abstractListRaw;

        private ArrayList arrayListRaw;

        private ArrayList<?> arrayListWildcard;

        private ArrayList[] arrayListRawArray;

        private ArrayList<T> arrayListTypeVariableT;

        private ArrayList<S> arrayListTypeVariableS;

        private ArrayList<U> arrayListTypeVariableU;

        private ArrayList<V> arrayListTypeVariableV;

        private Callable<?> callableWildcard;

        private T[] arrayTypeVariableT;

        private T[][] arrayNestedTypeVariableT;

        private S[] arrayTypeVariableS;

        private U[] arrayTypeVariableU;
    }
}

