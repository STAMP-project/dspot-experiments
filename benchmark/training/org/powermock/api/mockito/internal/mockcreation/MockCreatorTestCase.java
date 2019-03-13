package org.powermock.api.mockito.internal.mockcreation;


import java.util.List;
import org.junit.Test;
import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.mockito.mock.MockName;


public class MockCreatorTestCase {
    @Test
    public void should_return_mock_name_when_settings_have_name() throws NoSuchMethodException, SecurityException {
        final String definedMockName = "my-list";
        final MockSettings settings = Mockito.withSettings().name(definedMockName);
        final List<?> result = createMock(settings);
        final MockName mockName = getMockName(result);
        assertThat(mockName.toString()).as("Mock name is configured").isEqualTo(definedMockName);
    }

    @Test
    public void should_return_class_name_when_settings_have_no_name() throws NoSuchMethodException, SecurityException {
        final MockSettings settings = Mockito.withSettings();
        final List<?> result = createMock(settings);
        final MockName mockName = getMockName(result);
        assertThat(mockName.toString()).as("Mock name is configured").isEqualTo("list");
    }
}

