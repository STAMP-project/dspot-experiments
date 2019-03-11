package com.baeldung.className;


import RetrievingClassName.InnerClass;
import org.junit.Assert;
import org.junit.Test;


public class RetrievingClassNameUnitTest {
    // Retrieving Simple Name
    @Test
    public void givenRetrievingClassName_whenGetSimpleName_thenRetrievingClassName() {
        Assert.assertEquals("RetrievingClassName", RetrievingClassName.class.getSimpleName());
    }

    @Test
    public void givenPrimitiveInt_whenGetSimpleName_thenInt() {
        Assert.assertEquals("int", int.class.getSimpleName());
    }

    @Test
    public void givenRetrievingClassNameArray_whenGetSimpleName_thenRetrievingClassNameWithBrackets() {
        Assert.assertEquals("RetrievingClassName[]", RetrievingClassName[].class.getSimpleName());
        Assert.assertEquals("RetrievingClassName[][]", RetrievingClassName[][].class.getSimpleName());
    }

    @Test
    public void givenAnonymousClass_whenGetSimpleName_thenEmptyString() {
        Assert.assertEquals("", new RetrievingClassName() {}.getClass().getSimpleName());
    }

    // Retrieving Other Names
    // - Primitive Types
    @Test
    public void givenPrimitiveInt_whenGetName_thenInt() {
        Assert.assertEquals("int", int.class.getName());
    }

    @Test
    public void givenPrimitiveInt_whenGetTypeName_thenInt() {
        Assert.assertEquals("int", int.class.getTypeName());
    }

    @Test
    public void givenPrimitiveInt_whenGetCanonicalName_thenInt() {
        Assert.assertEquals("int", int.class.getCanonicalName());
    }

    // - Object Types
    @Test
    public void givenRetrievingClassName_whenGetName_thenCanonicalName() {
        Assert.assertEquals("com.baeldung.className.RetrievingClassName", RetrievingClassName.class.getName());
    }

    @Test
    public void givenRetrievingClassName_whenGetTypeName_thenCanonicalName() {
        Assert.assertEquals("com.baeldung.className.RetrievingClassName", RetrievingClassName.class.getTypeName());
    }

    @Test
    public void givenRetrievingClassName_whenGetCanonicalName_thenCanonicalName() {
        Assert.assertEquals("com.baeldung.className.RetrievingClassName", RetrievingClassName.class.getCanonicalName());
    }

    // - Inner Classes
    @Test
    public void givenRetrievingClassNameInnerClass_whenGetName_thenCanonicalNameWithDollarSeparator() {
        Assert.assertEquals("com.baeldung.className.RetrievingClassName$InnerClass", InnerClass.class.getName());
    }

    @Test
    public void givenRetrievingClassNameInnerClass_whenGetTypeName_thenCanonicalNameWithDollarSeparator() {
        Assert.assertEquals("com.baeldung.className.RetrievingClassName$InnerClass", InnerClass.class.getTypeName());
    }

    @Test
    public void givenRetrievingClassNameInnerClass_whenGetCanonicalName_thenCanonicalName() {
        Assert.assertEquals("com.baeldung.className.RetrievingClassName.InnerClass", InnerClass.class.getCanonicalName());
    }

    // - Anonymous Classes
    @Test
    public void givenAnonymousClass_whenGetName_thenCallingClassCanonicalNameWithDollarSeparatorAndCountNumber() {
        // These are the second and third appearences of an anonymous class in RetrievingClassNameUnitTest, hence $2 and $3 expectations
        Assert.assertEquals("com.baeldung.className.RetrievingClassNameUnitTest$2", new RetrievingClassName() {}.getClass().getName());
        Assert.assertEquals("com.baeldung.className.RetrievingClassNameUnitTest$3", new RetrievingClassName() {}.getClass().getName());
    }

    @Test
    public void givenAnonymousClass_whenGetTypeName_thenCallingClassCanonicalNameWithDollarSeparatorAndCountNumber() {
        // These are the fourth and fifth appearences of an anonymous class in RetrievingClassNameUnitTest, hence $4 and $5 expectations
        Assert.assertEquals("com.baeldung.className.RetrievingClassNameUnitTest$4", new RetrievingClassName() {}.getClass().getTypeName());
        Assert.assertEquals("com.baeldung.className.RetrievingClassNameUnitTest$5", new RetrievingClassName() {}.getClass().getTypeName());
    }

    @Test
    public void givenAnonymousClass_whenGetCanonicalName_thenNull() {
        Assert.assertNull(new RetrievingClassName() {}.getClass().getCanonicalName());
    }

    // - Arrays
    @Test
    public void givenPrimitiveIntArray_whenGetName_thenOpeningBracketsAndPrimitiveIntLetter() {
        Assert.assertEquals("[I", int[].class.getName());
        Assert.assertEquals("[[I", int[][].class.getName());
    }

    @Test
    public void givenRetrievingClassNameArray_whenGetName_thenOpeningBracketsLetterLAndRetrievingClassNameGetName() {
        Assert.assertEquals("[Lcom.baeldung.className.RetrievingClassName;", RetrievingClassName[].class.getName());
        Assert.assertEquals("[[Lcom.baeldung.className.RetrievingClassName;", RetrievingClassName[][].class.getName());
    }

    @Test
    public void givenRetrievingClassNameInnerClassArray_whenGetName_thenOpeningBracketsLetterLAndRetrievingClassNameInnerClassGetName() {
        Assert.assertEquals("[Lcom.baeldung.className.RetrievingClassName$InnerClass;", InnerClass[].class.getName());
        Assert.assertEquals("[[Lcom.baeldung.className.RetrievingClassName$InnerClass;", InnerClass[][].class.getName());
    }

    @Test
    public void givenPrimitiveIntArray_whenGetTypeName_thenPrimitiveIntGetTypeNameWithBrackets() {
        Assert.assertEquals("int[]", int[].class.getTypeName());
        Assert.assertEquals("int[][]", int[][].class.getTypeName());
    }

    @Test
    public void givenRetrievingClassNameArray_whenGetTypeName_thenRetrievingClassNameGetTypeNameWithBrackets() {
        Assert.assertEquals("com.baeldung.className.RetrievingClassName[]", RetrievingClassName[].class.getTypeName());
        Assert.assertEquals("com.baeldung.className.RetrievingClassName[][]", RetrievingClassName[][].class.getTypeName());
    }

    @Test
    public void givenRetrievingClassNameInnerClassArray_whenGetTypeName_thenRetrievingClassNameInnerClassGetTypeNameWithBrackets() {
        Assert.assertEquals("com.baeldung.className.RetrievingClassName$InnerClass[]", InnerClass[].class.getTypeName());
        Assert.assertEquals("com.baeldung.className.RetrievingClassName$InnerClass[][]", InnerClass[][].class.getTypeName());
    }

    @Test
    public void givenPrimitiveIntArray_whenGetCanonicalName_thenPrimitiveIntGetCanonicalNameWithBrackets() {
        Assert.assertEquals("int[]", int[].class.getCanonicalName());
        Assert.assertEquals("int[][]", int[][].class.getCanonicalName());
    }

    @Test
    public void givenRetrievingClassNameArray_whenGetCanonicalName_thenRetrievingClassNameGetCanonicalNameWithBrackets() {
        Assert.assertEquals("com.baeldung.className.RetrievingClassName[]", RetrievingClassName[].class.getCanonicalName());
        Assert.assertEquals("com.baeldung.className.RetrievingClassName[][]", RetrievingClassName[][].class.getCanonicalName());
    }

    @Test
    public void givenRetrievingClassNameInnerClassArray_whenGetCanonicalName_thenRetrievingClassNameInnerClassGetCanonicalNameWithBrackets() {
        Assert.assertEquals("com.baeldung.className.RetrievingClassName.InnerClass[]", InnerClass[].class.getCanonicalName());
        Assert.assertEquals("com.baeldung.className.RetrievingClassName.InnerClass[][]", InnerClass[][].class.getCanonicalName());
    }
}

