package uk.gov.gchq.gaffer.commonutil;


import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.pair.Pair;
import uk.gov.gchq.koryphe.ValidationResult;


public class FieldUtilTest {
    /**
     * Compares the error set returned by the ValidationResult
     */
    @Test
    public void testNullField() {
        // Given
        Pair nullPair = new Pair("Test", null);
        Set<String> testErrorSet = new LinkedHashSet<>();
        testErrorSet.add("Test is required.");
        // When
        ValidationResult validationResult = FieldUtil.validateRequiredFields(nullPair);
        // Then
        Assert.assertEquals(validationResult.getErrors(), testErrorSet);
    }

    /**
     * Compares the empty error set returned by the ValidationResult
     */
    @Test
    public void testNotNullField() {
        // Given
        Pair nonNullPair = new Pair("Test", "Test");
        Set<String> testNoErrorSet = new LinkedHashSet<>();
        // When
        ValidationResult validationResult = FieldUtil.validateRequiredFields(nonNullPair);
        // Then
        Assert.assertEquals(validationResult.getErrors(), testNoErrorSet);
    }
}

