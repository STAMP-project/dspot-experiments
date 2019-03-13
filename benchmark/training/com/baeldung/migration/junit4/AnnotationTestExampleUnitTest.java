package com.baeldung.migration.junit4;


import com.baeldung.migration.junit4.categories.Annotations;
import com.baeldung.migration.junit4.categories.JUnit4UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ Annotations.class, JUnit4UnitTest.class })
public class AnnotationTestExampleUnitTest {
    @Test(expected = Exception.class)
    public void shouldRaiseAnException() throws Exception {
        throw new Exception("This is my expected exception");
    }
}

