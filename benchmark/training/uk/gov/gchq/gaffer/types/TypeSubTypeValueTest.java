package uk.gov.gchq.gaffer.types;


import org.junit.Assert;
import org.junit.Test;


public class TypeSubTypeValueTest {
    @Test
    public void testComparisonsAreAsExpected() {
        TypeSubTypeValue typeSubTypeValue = new TypeSubTypeValue("a", "b", "c");
        Assert.assertEquals(0, typeSubTypeValue.compareTo(new TypeSubTypeValue("a", "b", "c")));
        Assert.assertTrue(((typeSubTypeValue.compareTo(null)) > 0));
        Assert.assertTrue(((typeSubTypeValue.compareTo(new TypeSubTypeValue())) > 0));
        Assert.assertTrue(((typeSubTypeValue.compareTo(new TypeSubTypeValue("1", "b", "c"))) > 0));
        Assert.assertTrue(((typeSubTypeValue.compareTo(new TypeSubTypeValue("a", "a", "c"))) > 0));
        Assert.assertTrue(((typeSubTypeValue.compareTo(new TypeSubTypeValue("a", "b", "a"))) > 0));
        Assert.assertTrue(((typeSubTypeValue.compareTo(new TypeSubTypeValue("b", "a", "c"))) < 0));
        Assert.assertTrue(((typeSubTypeValue.compareTo(new TypeSubTypeValue("a", "c", "c"))) < 0));
        Assert.assertTrue(((typeSubTypeValue.compareTo(new TypeSubTypeValue("a", "b", "d"))) < 0));
    }

    @Test
    public void testHashCodeAndEqualsMethodTreatsEmptyStringAsNull() {
        // Given
        TypeSubTypeValue typeSubTypeValueEmptyStrings = new TypeSubTypeValue("", "", "X");
        TypeSubTypeValue typeSubTypeValueNullStrings = new TypeSubTypeValue(null, null, "X");
        // When
        Boolean equalsResult = typeSubTypeValueEmptyStrings.equals(typeSubTypeValueNullStrings);
        // Then
        Assert.assertTrue(equalsResult);
        Assert.assertEquals(typeSubTypeValueEmptyStrings.hashCode(), typeSubTypeValueNullStrings.hashCode());
    }
}

