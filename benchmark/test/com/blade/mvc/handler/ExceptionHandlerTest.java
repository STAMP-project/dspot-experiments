package com.blade.mvc.handler;


import com.blade.exception.BladeException;
import com.blade.exception.NotFoundException;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author biezhi
 * @unknown 2017/9/19
 */
public class ExceptionHandlerTest {
    private Request request;

    private Response response;

    @Test
    public void testInternalErrorException() throws Exception {
        DefaultExceptionHandler handler = new DefaultExceptionHandler();
        try {
            throw new Exception();
        } catch (Exception e) {
            handler.handle(e);
        }
        Mockito.verify(response).status(500);
        Mockito.verify(response).html(ArgumentMatchers.any(String.class));
    }

    @Test
    public void testNotFoundException() throws Exception {
        DefaultExceptionHandler handler = new DefaultExceptionHandler();
        try {
            throw new NotFoundException("/hello");
        } catch (BladeException e) {
            handler.handle(e);
        }
        Mockito.verify(response).status(404);
        Mockito.verify(response).html(ArgumentMatchers.any(String.class));
    }

    @Test
    public void testNotWriteBodyIfNotHtmlRequest() throws Exception {
        DefaultExceptionHandler handler = new DefaultExceptionHandler();
        try {
            throw new NotFoundException("/hello");
        } catch (BladeException e) {
            handler.handle(e);
        }
        Mockito.verify(response).status(404);
        Mockito.verify(response).html(ArgumentMatchers.any(String.class));
    }
}

