package com.baeldung.kotlin;


import com.baeldung.lazy.ClassWithHeavyInitialization;
import junit.framework.TestCase;
import org.junit.Test;


public class LazyJavaUnitTest {
    @Test
    public void giveHeavyClass_whenInitLazy_thenShouldReturnInstanceOnFirstCall() {
        // when
        ClassWithHeavyInitialization classWithHeavyInitialization = ClassWithHeavyInitialization.getInstance();
        ClassWithHeavyInitialization classWithHeavyInitialization2 = ClassWithHeavyInitialization.getInstance();
        // then
        TestCase.assertTrue((classWithHeavyInitialization == classWithHeavyInitialization2));
    }
}

