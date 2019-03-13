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
package org.springframework.web.reactive.result.view;


import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Unit tests for {@link UrlBasedViewResolver}.
 *
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 */
public class UrlBasedViewResolverTests {
    private UrlBasedViewResolver resolver;

    @Test
    public void viewNames() throws Exception {
        this.resolver.setViewClass(UrlBasedViewResolverTests.TestView.class);
        this.resolver.setViewNames("my*");
        Mono<View> mono = this.resolver.resolveViewName("my-view", Locale.US);
        Assert.assertNotNull(mono.block());
        mono = this.resolver.resolveViewName("not-my-view", Locale.US);
        Assert.assertNull(mono.block());
    }

    @Test
    public void redirectView() throws Exception {
        Mono<View> mono = this.resolver.resolveViewName("redirect:foo", Locale.US);
        StepVerifier.create(mono).consumeNextWith(( view) -> {
            assertEquals(.class, view.getClass());
            RedirectView redirectView = ((RedirectView) (view));
            assertEquals("foo", redirectView.getUrl());
            assertEquals(HttpStatus.SEE_OTHER, redirectView.getStatusCode());
        }).expectComplete().verify(Duration.ZERO);
    }

    @Test
    public void customizedRedirectView() throws Exception {
        this.resolver.setRedirectViewProvider(( url) -> new RedirectView(url, HttpStatus.FOUND));
        Mono<View> mono = this.resolver.resolveViewName("redirect:foo", Locale.US);
        StepVerifier.create(mono).consumeNextWith(( view) -> {
            assertEquals(.class, view.getClass());
            RedirectView redirectView = ((RedirectView) (view));
            assertEquals("foo", redirectView.getUrl());
            assertEquals(HttpStatus.FOUND, redirectView.getStatusCode());
        }).expectComplete().verify(Duration.ZERO);
    }

    private static class TestView extends AbstractUrlBasedView {
        @Override
        public boolean checkResourceExists(Locale locale) throws Exception {
            return true;
        }

        @Override
        protected Mono<Void> renderInternal(Map<String, Object> attributes, MediaType contentType, ServerWebExchange exchange) {
            return Mono.empty();
        }
    }
}

