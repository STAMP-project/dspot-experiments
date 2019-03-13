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
package org.springframework.web.reactive.result.method.annotation;


import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.HandlerResult;
import reactor.core.publisher.Mono;
import rx.Completable;
import rx.Single;


/**
 * Unit tests for {@link ResponseBodyResultHandler}.When adding a test also
 * consider whether the logic under test is in a parent class, then see:
 * <ul>
 * 	<li>{@code MessageWriterResultHandlerTests},
 *  <li>{@code ContentNegotiatingResultHandlerSupportTests}
 * </ul>
 *
 * @author Sebastien Deleuze
 * @author Rossen Stoyanchev
 */
public class ResponseBodyResultHandlerTests {
    private ResponseBodyResultHandler resultHandler;

    @Test
    public void supports() throws NoSuchMethodException {
        Object controller = new ResponseBodyResultHandlerTests.TestController();
        Method method;
        method = on(ResponseBodyResultHandlerTests.TestController.class).annotPresent(ResponseBody.class).resolveMethod();
        testSupports(controller, method);
        method = on(ResponseBodyResultHandlerTests.TestController.class).annotNotPresent(ResponseBody.class).resolveMethod("doWork");
        HandlerResult handlerResult = getHandlerResult(controller, method);
        Assert.assertFalse(this.resultHandler.supports(handlerResult));
    }

    @Test
    public void supportsRestController() throws NoSuchMethodException {
        Object controller = new ResponseBodyResultHandlerTests.TestRestController();
        Method method;
        method = on(ResponseBodyResultHandlerTests.TestRestController.class).returning(String.class).resolveMethod();
        testSupports(controller, method);
        method = on(ResponseBodyResultHandlerTests.TestRestController.class).returning(Mono.class, String.class).resolveMethod();
        testSupports(controller, method);
        method = on(ResponseBodyResultHandlerTests.TestRestController.class).returning(Single.class, String.class).resolveMethod();
        testSupports(controller, method);
        method = on(ResponseBodyResultHandlerTests.TestRestController.class).returning(Completable.class).resolveMethod();
        testSupports(controller, method);
    }

    @Test
    public void defaultOrder() throws Exception {
        Assert.assertEquals(100, this.resultHandler.getOrder());
    }

    @RestController
    @SuppressWarnings("unused")
    private static class TestRestController {
        public Mono<Void> handleToMonoVoid() {
            return null;
        }

        public String handleToString() {
            return null;
        }

        public Mono<String> handleToMonoString() {
            return null;
        }

        public Single<String> handleToSingleString() {
            return null;
        }

        public Completable handleToCompletable() {
            return null;
        }
    }

    @Controller
    @SuppressWarnings("unused")
    private static class TestController {
        @ResponseBody
        public String handleToString() {
            return null;
        }

        public String doWork() {
            return null;
        }
    }
}

