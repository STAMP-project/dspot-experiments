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
package org.springframework.cloud.gateway.handler.predicate;


import java.time.ZonedDateTime;
import org.junit.Test;


/**
 *
 *
 * @author Spencer Gibb
 */
public class BeforeRoutePredicateFactoryTests {
    @Test
    public void beforeStringWorks() {
        String dateString = BetweenRoutePredicateFactoryTests.minusHours(1);
        boolean result = runPredicate(dateString);
        assertThat(result).isFalse();
    }

    @Test
    public void afterStringWorks() {
        String dateString = BetweenRoutePredicateFactoryTests.plusHours(1);
        boolean result = runPredicate(dateString);
        assertThat(result).isTrue();
    }

    @Test
    public void beforeEpochWorks() {
        String dateString = BetweenRoutePredicateFactoryTests.minusHoursMillis(1);
        final boolean result = runPredicate(dateString);
        assertThat(result).isFalse();
    }

    @Test
    public void afterEpochWorks() {
        String dateString = BetweenRoutePredicateFactoryTests.plusHoursMillis(1);
        final boolean result = runPredicate(dateString);
        assertThat(result).isTrue();
    }

    @Test
    public void testPredicates() {
        boolean result = new BeforeRoutePredicateFactory().apply(( c) -> c.setDatetime(ZonedDateTime.now().minusHours(2))).test(BetweenRoutePredicateFactoryTests.getExchange());
        assertThat(result).isFalse();
    }
}

