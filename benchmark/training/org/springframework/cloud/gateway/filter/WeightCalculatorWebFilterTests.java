/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.gateway.filter;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.cloud.gateway.event.PredicateArgsEvent;
import org.springframework.cloud.gateway.support.WeightConfig;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;


public class WeightCalculatorWebFilterTests {
    @Test
    public void testWeightCalculation() {
        WeightCalculatorWebFilter filter = new WeightCalculatorWebFilter();
        String grp1 = "group1";
        String grp2 = "group2";
        int grp1idx = 1;
        int grp2idx = 1;
        assertWeightCalculation(filter, grp1, (grp1idx++), 1, Arrays.asList(1.0));
        assertWeightCalculation(filter, grp2, (grp2idx++), 1, Arrays.asList(1.0));
        assertWeightCalculation(filter, grp1, (grp1idx++), 3, Arrays.asList(0.25, 0.75), 0.25);
        assertWeightCalculation(filter, grp2, (grp2idx++), 1, Arrays.asList(0.5, 0.5), 0.5);
        assertWeightCalculation(filter, grp1, (grp1idx++), 6, Arrays.asList(0.1, 0.3, 0.6), 0.1, 0.4);
        assertWeightCalculation(filter, grp2, (grp2idx++), 2, Arrays.asList(0.25, 0.25, 0.5), 0.25, 0.5);
        assertWeightCalculation(filter, grp2, (grp2idx++), 4, Arrays.asList(0.125, 0.125, 0.25, 0.5), 0.125, 0.25, 0.5);
    }

    @Test
    public void testChooseRouteWithRandom() {
        WeightCalculatorWebFilter filter = new WeightCalculatorWebFilter();
        filter.addWeightConfig(new WeightConfig("groupa", "route1", 1));
        filter.addWeightConfig(new WeightConfig("groupa", "route2", 3));
        filter.addWeightConfig(new WeightConfig("groupa", "route3", 6));
        Random random = Mockito.mock(Random.class);
        Mockito.when(random.nextDouble()).thenReturn(0.05).thenReturn(0.2).thenReturn(0.6);
        filter.setRandom(random);
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("http://localhost").build());
        WebFilterChain filterChain = Mockito.mock(WebFilterChain.class);
        filter.filter(exchange, filterChain);
        Map<String, String> weights = WeightCalculatorWebFilter.getWeights(exchange);
        assertThat(weights).containsEntry("groupa", "route1");
        filter.filter(exchange, filterChain);
        weights = WeightCalculatorWebFilter.getWeights(exchange);
        assertThat(weights).containsEntry("groupa", "route2");
        filter.filter(exchange, filterChain);
        weights = WeightCalculatorWebFilter.getWeights(exchange);
        assertThat(weights).containsEntry("groupa", "route3");
    }

    @Test
    public void receivesPredicateArgsEvent() {
        WeightCalculatorWebFilter filter = Mockito.mock(WeightCalculatorWebFilter.class);
        Mockito.doNothing().when(filter).addWeightConfig(ArgumentMatchers.any(WeightConfig.class));
        Mockito.doCallRealMethod().when(filter).handle(ArgumentMatchers.any(PredicateArgsEvent.class));
        HashMap<String, Object> args = new HashMap<>();
        args.put("weight.group", "group1");
        args.put("weight.weight", "1");
        PredicateArgsEvent event = new PredicateArgsEvent(this, "routeA", args);
        filter.handle(event);
        ArgumentCaptor<WeightConfig> configCaptor = ArgumentCaptor.forClass(WeightConfig.class);
        Mockito.verify(filter).addWeightConfig(configCaptor.capture());
        WeightConfig weightConfig = configCaptor.getValue();
        assertThat(weightConfig.getGroup()).isEqualTo("group1");
        assertThat(weightConfig.getRouteId()).isEqualTo("routeA");
        assertThat(weightConfig.getWeight()).isEqualTo(1);
    }
}

