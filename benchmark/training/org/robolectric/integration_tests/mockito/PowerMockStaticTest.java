package org.robolectric.integration_tests.mockito;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*" })
@PrepareForTest(PowerMockStaticTest.Static.class)
public class PowerMockStaticTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Test
    public void testStaticMocking() {
        PowerMockito.mockStatic(PowerMockStaticTest.Static.class);
        Mockito.when(PowerMockStaticTest.Static.staticMethod()).thenReturn("hello mock");
        assertThat(PowerMockStaticTest.Static.staticMethod()).isEqualTo("hello mock");
    }

    public static class Static {
        public static String staticMethod() {
            return "";
        }
    }
}

