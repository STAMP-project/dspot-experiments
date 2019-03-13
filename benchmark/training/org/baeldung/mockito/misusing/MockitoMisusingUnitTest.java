package org.baeldung.mockito.misusing;


import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.NotAMockException;


public class MockitoMisusingUnitTest {
    @Test
    public void givenNotASpy_whenDoReturn_thenThrowNotAMock() {
        try {
            List<String> list = new ArrayList<String>();
            Mockito.doReturn(100, lenient()).when(list).size();
            Assertions.fail("Should have thrown a NotAMockException because 'list' is not a mock!");
        } catch (NotAMockException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Argument passed to when() is not a mock!"));
        }
    }
}

