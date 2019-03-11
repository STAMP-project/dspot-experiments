package uk.gov.gchq.gaffer.core.exception;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.core.exception.Error.ErrorBuilder;


public class ErrorTest {
    private static final String DETAILED_MSG = "detailedMessage";

    private static final String SIMPLE_MSG = "simpleMessage";

    @Test
    public void shouldNotBuildDetailedMessage() throws Exception {
        // Given
        setDebugMode("false");
        // When
        final Error error = new ErrorBuilder().simpleMessage(ErrorTest.SIMPLE_MSG).detailMessage(ErrorTest.DETAILED_MSG).build();
        // Then
        Assert.assertNotEquals("Detailed message is present when built and debug is false", ErrorTest.DETAILED_MSG, getDetailMessage());
    }

    @Test
    public void shouldNotBuildDetailedMessageWithMissingPropertyFlag() {
        // Given
        setDebugMode(null);
        // When
        final Error error = new ErrorBuilder().simpleMessage(ErrorTest.SIMPLE_MSG).detailMessage(ErrorTest.DETAILED_MSG).build();
        // Then
        Assert.assertNotEquals("Detailed message is present when build and debug is false", ErrorTest.DETAILED_MSG, getDetailMessage());
    }

    @Test
    public void shouldNotBuildDetailedMessageWithIncorrectPropertyFlad() {
        // Given
        setDebugMode("wrong");
        // When
        final Error error = new ErrorBuilder().simpleMessage(ErrorTest.SIMPLE_MSG).detailMessage(ErrorTest.DETAILED_MSG).build();
        // Then
        Assert.assertNotEquals("Detailed message is present when build and debug is false", ErrorTest.DETAILED_MSG, getDetailMessage());
    }

    @Test
    public void shouldBuildDetailedMessage() throws Exception {
        // Given
        setDebugMode("true");
        // When
        final Error error = new ErrorBuilder().simpleMessage(ErrorTest.SIMPLE_MSG).detailMessage(ErrorTest.DETAILED_MSG).build();
        // Then
        Assert.assertEquals("Detailed message is not present when built and debug is true", ErrorTest.DETAILED_MSG, getDetailMessage());
    }
}

