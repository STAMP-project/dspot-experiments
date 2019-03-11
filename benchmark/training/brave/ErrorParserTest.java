package brave;


import ErrorParser.NOOP;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ErrorParserTest {
    @Mock
    SpanCustomizer customizer;

    @Mock
    ScopedSpan scopedSpan;

    ErrorParser parser = new ErrorParser();

    @Test
    public void error_customizer() {
        parser.error(new RuntimeException("this cake is a lie"), customizer);
        Mockito.verify(customizer).tag("error", "this cake is a lie");
    }

    @Test
    public void error_scopedSpan() {
        parser.error(new RuntimeException("this cake is a lie"), scopedSpan);
        Mockito.verify(scopedSpan).tag("error", "this cake is a lie");
    }

    @Test
    public void error_noMessage_customizer() {
        parser.error(new RuntimeException(), customizer);
        Mockito.verify(customizer).tag("error", "RuntimeException");
    }

    @Test
    public void error_noMessage_scopedSpan() {
        parser.error(new RuntimeException(), scopedSpan);
        Mockito.verify(scopedSpan).tag("error", "RuntimeException");
    }

    @Test
    public void error_noop_customizer() {
        NOOP.error(new RuntimeException("this cake is a lie"), customizer);
        Mockito.verifyNoMoreInteractions(customizer);
    }

    @Test
    public void error_noop_scopedSpan() {
        NOOP.error(new RuntimeException("this cake is a lie"), scopedSpan);
        Mockito.verifyNoMoreInteractions(scopedSpan);
    }
}

