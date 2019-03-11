package brave.spring.webmvc;


import brave.SpanCustomizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


public class SpanCustomizingHandlerInterceptorTest {
    SpanCustomizingHandlerInterceptor interceptor;

    SpanCustomizingHandlerInterceptorTest.TestController controller = new SpanCustomizingHandlerInterceptorTest.TestController();

    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    SpanCustomizer span = Mockito.mock(SpanCustomizer.class);

    HandlerParser parser = Mockito.mock(HandlerParser.class);

    @Test
    public void preHandle_parsesAndAddsHttpRouteAttribute() {
        Mockito.when(request.getAttribute("brave.SpanCustomizer")).thenReturn(span);
        Mockito.when(request.getAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE)).thenReturn("/items/{itemId}");
        interceptor.preHandle(request, response, controller);
        Mockito.verify(request).getAttribute("brave.SpanCustomizer");
        Mockito.verify(request).getAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE);
        Mockito.verify(request).setAttribute("http.route", "/items/{itemId}");
        Mockito.verify(parser).preHandle(request, controller, span);
        Mockito.verifyNoMoreInteractions(request, response, parser, span);
    }

    @Test
    public void preHandle_parsesAndAddsHttpRouteAttribute_coercesNullToEmpty() {
        Mockito.when(request.getAttribute("brave.SpanCustomizer")).thenReturn(span);
        interceptor.preHandle(request, response, controller);
        Mockito.verify(request).getAttribute("brave.SpanCustomizer");
        Mockito.verify(request).getAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE);
        Mockito.verify(request).setAttribute("http.route", "");
        Mockito.verify(parser).preHandle(request, controller, span);
        Mockito.verifyNoMoreInteractions(request, response, parser, span);
    }

    @Test
    public void preHandle_nothingWhenNoSpanAttribute() {
        interceptor.preHandle(request, response, controller);
        Mockito.verify(request).getAttribute("brave.SpanCustomizer");
        Mockito.verifyNoMoreInteractions(request, request, parser, span);
    }

    @Controller
    static class TestController {
        @RequestMapping("/items/{itemId}")
        public ResponseEntity<String> items(@PathVariable("itemId")
        String itemId) {
            return new ResponseEntity(itemId, HttpStatus.OK);
        }
    }
}

