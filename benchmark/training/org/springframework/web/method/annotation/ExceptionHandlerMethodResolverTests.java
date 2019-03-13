/**
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.method.annotation;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.BindException;
import java.net.SocketException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;


/**
 * Test fixture for {@link ExceptionHandlerMethodResolver} tests.
 *
 * @author Rossen Stoyanchev
 */
public class ExceptionHandlerMethodResolverTests {
    @Test
    public void resolveMethodFromAnnotation() {
        ExceptionHandlerMethodResolver resolver = new ExceptionHandlerMethodResolver(ExceptionHandlerMethodResolverTests.ExceptionController.class);
        IOException exception = new IOException();
        Assert.assertEquals("handleIOException", resolver.resolveMethod(exception).getName());
    }

    @Test
    public void resolveMethodFromArgument() {
        ExceptionHandlerMethodResolver resolver = new ExceptionHandlerMethodResolver(ExceptionHandlerMethodResolverTests.ExceptionController.class);
        IllegalArgumentException exception = new IllegalArgumentException();
        Assert.assertEquals("handleIllegalArgumentException", resolver.resolveMethod(exception).getName());
    }

    @Test
    public void resolveMethodExceptionSubType() {
        ExceptionHandlerMethodResolver resolver = new ExceptionHandlerMethodResolver(ExceptionHandlerMethodResolverTests.ExceptionController.class);
        IOException ioException = new FileNotFoundException();
        Assert.assertEquals("handleIOException", resolver.resolveMethod(ioException).getName());
        SocketException bindException = new BindException();
        Assert.assertEquals("handleSocketException", resolver.resolveMethod(bindException).getName());
    }

    @Test
    public void resolveMethodBestMatch() {
        ExceptionHandlerMethodResolver resolver = new ExceptionHandlerMethodResolver(ExceptionHandlerMethodResolverTests.ExceptionController.class);
        SocketException exception = new SocketException();
        Assert.assertEquals("handleSocketException", resolver.resolveMethod(exception).getName());
    }

    @Test
    public void resolveMethodNoMatch() {
        ExceptionHandlerMethodResolver resolver = new ExceptionHandlerMethodResolver(ExceptionHandlerMethodResolverTests.ExceptionController.class);
        Exception exception = new Exception();
        Assert.assertNull("1st lookup", resolver.resolveMethod(exception));
        Assert.assertNull("2nd lookup from cache", resolver.resolveMethod(exception));
    }

    @Test
    public void resolveMethodInherited() {
        ExceptionHandlerMethodResolver resolver = new ExceptionHandlerMethodResolver(ExceptionHandlerMethodResolverTests.InheritedController.class);
        IOException exception = new IOException();
        Assert.assertEquals("handleIOException", resolver.resolveMethod(exception).getName());
    }

    @Test(expected = IllegalStateException.class)
    public void ambiguousExceptionMapping() {
        new ExceptionHandlerMethodResolver(ExceptionHandlerMethodResolverTests.AmbiguousController.class);
    }

    @Test(expected = IllegalStateException.class)
    public void noExceptionMapping() {
        new ExceptionHandlerMethodResolver(ExceptionHandlerMethodResolverTests.NoExceptionController.class);
    }

    @Controller
    static class ExceptionController {
        public void handle() {
        }

        @ExceptionHandler(IOException.class)
        public void handleIOException() {
        }

        @ExceptionHandler(SocketException.class)
        public void handleSocketException() {
        }

        @ExceptionHandler
        public void handleIllegalArgumentException(IllegalArgumentException exception) {
        }
    }

    @Controller
    static class InheritedController extends ExceptionHandlerMethodResolverTests.ExceptionController {
        @Override
        public void handleIOException() {
        }
    }

    @Controller
    static class AmbiguousController {
        public void handle() {
        }

        @ExceptionHandler({ BindException.class, IllegalArgumentException.class })
        public String handle1(Exception ex, HttpServletRequest request, HttpServletResponse response) throws IOException {
            return ClassUtils.getShortName(ex.getClass());
        }

        @ExceptionHandler
        public String handle2(IllegalArgumentException ex) {
            return ClassUtils.getShortName(ex.getClass());
        }
    }

    @Controller
    static class NoExceptionController {
        @ExceptionHandler
        public void handle() {
        }
    }
}

