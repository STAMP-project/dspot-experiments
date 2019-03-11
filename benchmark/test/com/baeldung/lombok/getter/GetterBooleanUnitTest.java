package com.baeldung.lombok.getter;


import org.junit.Assert;
import org.junit.Test;


public class GetterBooleanUnitTest {
    @Test
    public void whenBasicBooleanField_thenMethodNamePrefixedWithIsFollowedByFieldName() {
        GetterBooleanPrimitive lombokExamples = new GetterBooleanPrimitive();
        Assert.assertFalse(lombokExamples.isRunning());
    }

    @Test
    public void whenBooleanFieldPrefixedWithIs_thenMethodNameIsSameAsFieldName() {
        GetterBooleanSameAccessor lombokExamples = new GetterBooleanSameAccessor();
        Assert.assertTrue(lombokExamples.isRunning());
    }

    @Test
    public void whenTwoBooleanFieldsCauseNamingConflict_thenLombokMapsToFirstDeclaredField() {
        GetterBooleanPrimitiveSameAccessor lombokExamples = new GetterBooleanPrimitiveSameAccessor();
        Assert.assertTrue(((lombokExamples.isRunning()) == (lombokExamples.running)));
        Assert.assertFalse(((lombokExamples.isRunning()) == (lombokExamples.isRunning)));
    }

    @Test
    public void whenFieldOfBooleanType_thenLombokPrefixesMethodWithGetInsteadOfIs() {
        GetterBooleanType lombokExamples = new GetterBooleanType();
        Assert.assertTrue(lombokExamples.getRunning());
    }
}

