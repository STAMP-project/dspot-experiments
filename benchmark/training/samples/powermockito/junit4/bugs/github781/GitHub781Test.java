package samples.powermockito.junit4.bugs.github781;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(EqualsStatic.class)
public class GitHub781Test {
    private SpyObject partialMock = null;

    private final boolean result = true;

    @Test
    public void testCallMockStaticEquals() {
        PowerMockito.mockStatic(EqualsStatic.class);
        PowerMockito.when(EqualsStatic.equals()).thenReturn(result);
        partialMock = Mockito.spy(new SpyObject());
        assertThat(partialMock.callEquals()).isEqualTo(result);
    }
}

