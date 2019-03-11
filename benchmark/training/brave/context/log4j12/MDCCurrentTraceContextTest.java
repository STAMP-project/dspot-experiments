package brave.context.log4j12;


import brave.propagation.CurrentTraceContext;
import brave.test.propagation.CurrentTraceContextTest;
import java.util.function.Supplier;
import org.junit.ComparisonFailure;
import org.junit.Test;


public class MDCCurrentTraceContextTest extends CurrentTraceContextTest {
    public MDCCurrentTraceContextTest() {
        MDCScopeDecoratorTest.assumeMDCWorks();
    }

    static class CurrentSupplier implements Supplier<CurrentTraceContext> {
        @Override
        public CurrentTraceContext get() {
            return MDCCurrentTraceContext.create();
        }
    }

    @Test
    public void is_inheritable() throws Exception {
        super.is_inheritable(currentTraceContext);
    }

    // Log4J 1.2.x MDC is inheritable by default
    @Test(expected = ComparisonFailure.class)
    public void isnt_inheritable() throws Exception {
        super.isnt_inheritable();
    }
}

