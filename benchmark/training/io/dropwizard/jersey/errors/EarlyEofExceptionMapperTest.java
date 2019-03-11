package io.dropwizard.jersey.errors;


import Response.Status.BAD_REQUEST;
import javax.ws.rs.core.Response;
import org.eclipse.jetty.io.EofException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class EarlyEofExceptionMapperTest {
    private final EarlyEofExceptionMapper mapper = new EarlyEofExceptionMapper();

    @Test
    public void testToReponse() {
        final Response reponse = mapper.toResponse(new EofException());
        Assertions.assertEquals(BAD_REQUEST.getStatusCode(), reponse.getStatus());
    }
}

