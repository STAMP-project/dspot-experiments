package brave.propagation;


import CurrentTraceContext.Default;
import brave.test.propagation.CurrentTraceContextTest;
import java.util.function.Supplier;
import org.junit.Test;


public class DefaultCurrentTraceContextTest extends CurrentTraceContextTest {
    static class CurrentSupplier implements Supplier<CurrentTraceContext> {
        @Override
        public CurrentTraceContext get() {
            return Default.create();
        }
    }

    @Test
    public void is_inheritable() throws Exception {
        super.is_inheritable(Default.inheritable());
    }
}

