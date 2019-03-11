package com.blade.mvc;


import com.blade.BaseTestCase;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.hook.WebHook;
import com.blade.security.web.auth.BasicAuthMiddleware;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Middleware Test
 *
 * @author biezhi
2017/6/5
 */
public class MiddlewareTest extends BaseTestCase {
    @Test
    public void testMiddleware() throws Exception {
        WebHook middleware = Mockito.mock(WebHook.class);
        Signature signature = Mockito.mock(Signature.class);
        middleware.before(signature.routeContext());
        middleware.after(signature.routeContext());
        Mockito.verify(middleware).before(signature.routeContext());
        Mockito.verify(middleware).after(signature.routeContext());
    }

    @Test
    public void testAuthMiddleware() throws Exception {
        BasicAuthMiddleware basicAuthMiddleware = Mockito.mock(BasicAuthMiddleware.class);
        Signature signature = Mockito.mock(Signature.class);
        basicAuthMiddleware.before(signature.routeContext());
        basicAuthMiddleware.after(signature.routeContext());
        Mockito.verify(basicAuthMiddleware).before(signature.routeContext());
        Mockito.verify(basicAuthMiddleware).after(signature.routeContext());
    }
}

