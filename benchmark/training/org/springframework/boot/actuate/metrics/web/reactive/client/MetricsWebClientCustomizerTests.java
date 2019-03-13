/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.actuate.metrics.web.reactive.client;


import WebClient.Builder;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;


/**
 * Tests for {@link MetricsWebClientCustomizer}
 *
 * @author Brian Clozel
 */
public class MetricsWebClientCustomizerTests {
    private MetricsWebClientCustomizer customizer;

    private Builder clientBuilder;

    @Test
    public void customizeShouldAddFilterFunction() {
        this.clientBuilder.filter(Mockito.mock(ExchangeFilterFunction.class));
        this.customizer.customize(this.clientBuilder);
        this.clientBuilder.filters(( filters) -> assertThat(filters).hasSize(2).first().isInstanceOf(.class));
    }

    @Test
    public void customizeShouldNotAddDuplicateFilterFunction() {
        this.customizer.customize(this.clientBuilder);
        this.clientBuilder.filters(( filters) -> assertThat(filters).hasSize(1));
        this.customizer.customize(this.clientBuilder);
        this.clientBuilder.filters(( filters) -> assertThat(filters).hasSize(1).first().isInstanceOf(.class));
    }
}

