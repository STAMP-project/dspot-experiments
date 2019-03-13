package brave.propagation;


import brave.Tracing;
import org.junit.Test;

import static Propagation.B3_STRING;


/**
 * Ensures there's no NPE when tracing builder uses defaults
 */
public class PropagationConstantsTest {
    @Test
    public void eagerReferencePropagationConstantPriorToUse() {
        Propagation<String> foo = B3_STRING;
        Tracing.newBuilder().build().close();
    }
}

