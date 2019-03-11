package io.dropwizard.jersey.validation;


import java.util.Collections;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Request;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.model.Invocable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class JerseyViolationExceptionTest {
    @Test
    public void testAccessors() {
        final Set<? extends ConstraintViolation<?>> violations = Collections.emptySet();
        @SuppressWarnings("unchecked")
        final Inflector<Request, ?> inf = Mockito.mock(Inflector.class);
        final Invocable inv = Invocable.create(inf);
        final JerseyViolationException test = new JerseyViolationException(violations, inv);
        Assertions.assertSame(inv, test.getInvocable());
    }
}

