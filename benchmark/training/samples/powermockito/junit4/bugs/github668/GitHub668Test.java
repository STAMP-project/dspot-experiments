package samples.powermockito.junit4.bugs.github668;


import java.util.HashSet;
import javax.security.auth.Subject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(Subject.class)
public class GitHub668Test {
    @Test
    public void shouldMockJavaxSystemFinalClasses() {
        Subject subject = mock(Subject.class);
        final HashSet<Object> value = new HashSet<Object>();
        when(subject.getPrivateCredentials()).thenReturn(value);
        assertThat(subject.getPrivateCredentials()).isSameAs(value);
    }
}

