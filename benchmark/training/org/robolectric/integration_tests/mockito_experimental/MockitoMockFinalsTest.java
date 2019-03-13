package org.robolectric.integration_tests.mockito_experimental;


import android.text.Layout;
import android.widget.TextView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class MockitoMockFinalsTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    TextView textView;

    @Test
    public void testInjection() {
        Layout layout = Mockito.mock(Layout.class);
        Mockito.when(textView.getLayout()).thenReturn(layout);
        assertThat(textView.getLayout()).isSameAs(layout);
    }

    @Test
    public void canMockUserId() {
        MockitoMockFinalsTest.User user = Mockito.mock(MockitoMockFinalsTest.User.class);
        Mockito.when(user.getId()).thenReturn(1);
        assertThat(user.getId()).isEqualTo(1);
    }

    static final class User {
        final int getId() {
            return -1;
        }
    }
}

