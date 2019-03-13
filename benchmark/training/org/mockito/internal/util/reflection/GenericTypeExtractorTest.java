/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util.reflection;


import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockitoutil.TestBase;


public class GenericTypeExtractorTest extends TestBase {
    class Base<T> {}

    static class StaticBase<T> {}

    interface IBase<T> {}

    interface StaticIBase<T> {}

    class IntImpl extends GenericTypeExtractorTest.Base<Integer> {}

    static class StaticIntImpl extends GenericTypeExtractorTest.StaticBase<Integer> {}

    class NestedImpl extends GenericTypeExtractorTest.Base<GenericTypeExtractorTest.Base<String>> {}

    class NonGeneric extends GenericTypeExtractorTest.Base {}

    class IIntImpl implements GenericTypeExtractorTest.IBase<Integer> {}

    class INestedImpl implements GenericTypeExtractorTest.IBase<GenericTypeExtractorTest.IBase<String>> {}

    class INonGeneric implements GenericTypeExtractorTest.IBase {}

    class Mixed extends GenericTypeExtractorTest.Base<Integer> implements GenericTypeExtractorTest.IBase<String> {}

    class Deeper extends GenericTypeExtractorTest.IntImpl implements Serializable {}

    class EvenDeeper extends GenericTypeExtractorTest.Deeper implements Cloneable {}

    interface Iface extends GenericTypeExtractorTest.IBase<Integer> {}

    interface IDeeper extends Serializable , Cloneable , GenericTypeExtractorTest.Iface {}

    interface Crazy extends Serializable , Cloneable , GenericTypeExtractorTest.IDeeper {}

    class Crazier extends GenericTypeExtractorTest.EvenDeeper implements GenericTypeExtractorTest.Crazy {}

    @Test
    public void finds_generic_type() {
        Assert.assertEquals(Integer.class, GenericTypeExtractor.genericTypeOf(GenericTypeExtractorTest.IntImpl.class, GenericTypeExtractorTest.Base.class, GenericTypeExtractorTest.IBase.class));
        Assert.assertEquals(Integer.class, GenericTypeExtractor.genericTypeOf(GenericTypeExtractorTest.StaticIntImpl.class, GenericTypeExtractorTest.StaticBase.class, GenericTypeExtractorTest.IBase.class));
        Assert.assertEquals(Object.class, GenericTypeExtractor.genericTypeOf(GenericTypeExtractorTest.NestedImpl.class, GenericTypeExtractorTest.Base.class, GenericTypeExtractorTest.IBase.class));
        Assert.assertEquals(Object.class, GenericTypeExtractor.genericTypeOf(GenericTypeExtractorTest.NonGeneric.class, GenericTypeExtractorTest.Base.class, GenericTypeExtractorTest.IBase.class));
        Assert.assertEquals(Object.class, GenericTypeExtractor.genericTypeOf(String.class, GenericTypeExtractorTest.Base.class, GenericTypeExtractorTest.IBase.class));
        Assert.assertEquals(Object.class, GenericTypeExtractor.genericTypeOf(String.class, List.class, Map.class));
        Assert.assertEquals(String.class, GenericTypeExtractor.genericTypeOf(new GenericTypeExtractorTest.Base<String>() {}.getClass(), GenericTypeExtractorTest.Base.class, GenericTypeExtractorTest.IBase.class));
        Assert.assertEquals(String.class, GenericTypeExtractor.genericTypeOf(new GenericTypeExtractorTest.IBase<String>() {}.getClass(), GenericTypeExtractorTest.Base.class, GenericTypeExtractorTest.IBase.class));
        Assert.assertEquals(String.class, GenericTypeExtractor.genericTypeOf(new GenericTypeExtractorTest.StaticBase<String>() {}.getClass(), GenericTypeExtractorTest.StaticBase.class, GenericTypeExtractorTest.IBase.class));
        Assert.assertEquals(String.class, GenericTypeExtractor.genericTypeOf(new GenericTypeExtractorTest.StaticIBase<String>() {}.getClass(), GenericTypeExtractorTest.Base.class, GenericTypeExtractorTest.StaticIBase.class));
        Assert.assertEquals(Integer.class, GenericTypeExtractor.genericTypeOf(GenericTypeExtractorTest.Mixed.class, GenericTypeExtractorTest.Base.class, GenericTypeExtractorTest.IBase.class));
        Assert.assertEquals(Integer.class, GenericTypeExtractor.genericTypeOf(GenericTypeExtractorTest.IIntImpl.class, GenericTypeExtractorTest.Base.class, GenericTypeExtractorTest.IBase.class));
        Assert.assertEquals(Object.class, GenericTypeExtractor.genericTypeOf(GenericTypeExtractorTest.INestedImpl.class, GenericTypeExtractorTest.Base.class, GenericTypeExtractorTest.IBase.class));
        Assert.assertEquals(Object.class, GenericTypeExtractor.genericTypeOf(GenericTypeExtractorTest.INonGeneric.class, GenericTypeExtractorTest.IBase.class, GenericTypeExtractorTest.INonGeneric.class));
        Assert.assertEquals(Integer.class, GenericTypeExtractor.genericTypeOf(GenericTypeExtractorTest.Deeper.class, GenericTypeExtractorTest.Base.class, GenericTypeExtractorTest.IBase.class));
        Assert.assertEquals(Integer.class, GenericTypeExtractor.genericTypeOf(GenericTypeExtractorTest.EvenDeeper.class, GenericTypeExtractorTest.Base.class, GenericTypeExtractorTest.IBase.class));
        Assert.assertEquals(Integer.class, GenericTypeExtractor.genericTypeOf(GenericTypeExtractorTest.Iface.class, GenericTypeExtractorTest.Base.class, GenericTypeExtractorTest.IBase.class));
        Assert.assertEquals(Integer.class, GenericTypeExtractor.genericTypeOf(GenericTypeExtractorTest.IDeeper.class, GenericTypeExtractorTest.Base.class, GenericTypeExtractorTest.IBase.class));
        Assert.assertEquals(Integer.class, GenericTypeExtractor.genericTypeOf(GenericTypeExtractorTest.Crazy.class, GenericTypeExtractorTest.Base.class, GenericTypeExtractorTest.IBase.class));
        Assert.assertEquals(Integer.class, GenericTypeExtractor.genericTypeOf(GenericTypeExtractorTest.Crazier.class, GenericTypeExtractorTest.Base.class, GenericTypeExtractorTest.IBase.class));
    }
}

