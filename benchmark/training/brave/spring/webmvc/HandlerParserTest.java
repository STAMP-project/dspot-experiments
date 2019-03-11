package brave.spring.webmvc;


import HandlerParser.NOOP;
import brave.SpanCustomizer;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;


public class HandlerParserTest {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    SpanCustomizer customizer = Mockito.mock(SpanCustomizer.class);

    HandlerParser parser = new HandlerParser();

    HandlerParserTest.TestController controller = new HandlerParserTest.TestController();

    @Controller
    static class TestController {
        @RequestMapping("/items/{itemId}")
        public ResponseEntity<String> items(@PathVariable
        String itemId) {
            return new ResponseEntity(itemId, HttpStatus.OK);
        }
    }

    /**
     * For Spring WebMVC 3.1+
     */
    @Test
    public void preHandle_HandlerMethod_addsClassAndMethodTags() throws Exception {
        parser.preHandle(request, new HandlerMethod(controller, HandlerParserTest.TestController.class.getMethod("items", String.class)), customizer);
        Mockito.verify(customizer).tag("mvc.controller.class", "TestController");
        Mockito.verify(customizer).tag("mvc.controller.method", "items");
        Mockito.verifyNoMoreInteractions(request, customizer);
    }

    /**
     * For Spring WebMVC 2.5
     */
    @Test
    public void preHandle_Handler_addsClassTag() {
        parser.preHandle(request, controller, customizer);
        Mockito.verify(customizer).tag("mvc.controller.class", "TestController");
        Mockito.verifyNoMoreInteractions(request, customizer);
    }

    @Test
    public void preHandle_NOOP_addsNothing() {
        NOOP.preHandle(request, controller, customizer);
        Mockito.verifyNoMoreInteractions(request, customizer);
    }
}

