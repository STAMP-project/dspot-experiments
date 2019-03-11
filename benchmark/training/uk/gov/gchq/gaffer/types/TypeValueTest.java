package uk.gov.gchq.gaffer.types;


import org.junit.Assert;
import org.junit.Test;


public class TypeValueTest {
    @Test
    public void testComparisonsAreAsExpected() {
        TypeValue typeValue = new TypeValue("a", "b");
        Assert.assertEquals(0, typeValue.compareTo(new TypeValue("a", "b")));
        Assert.assertTrue(((typeValue.compareTo(null)) > 0));
        Assert.assertTrue(((typeValue.compareTo(new TypeValue())) > 0));
        Assert.assertTrue(((typeValue.compareTo(new TypeValue("1", "b"))) > 0));
        Assert.assertTrue(((typeValue.compareTo(new TypeValue("a", "a"))) > 0));
        Assert.assertTrue(((typeValue.compareTo(new TypeValue("b", "a"))) < 0));
        Assert.assertTrue(((typeValue.compareTo(new TypeValue("a", "c"))) < 0));
    }

    @Test
    public void testHashCodeAndEqualsMethodTreatsEmptyStringAsNull() {
        // Given
        TypeValue typeValueEmptyStrings = new TypeValue("", "");
        TypeValue typeValueNullStrings = new TypeValue(null, null);
        // When
        boolean equalsResult = typeValueEmptyStrings.equals(typeValueNullStrings);
        // Then
        Assert.assertTrue(equalsResult);
        Assert.assertEquals(typeValueEmptyStrings.hashCode(), typeValueNullStrings.hashCode());
    }
}

