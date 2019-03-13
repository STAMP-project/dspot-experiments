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
public class BetweenRoutePredicateFactoryTests {
    @Test
    public void beforeStringWorks() {
        String dateString1 = BetweenRoutePredicateFactoryTests.plusHours(1);
        String dateString2 = BetweenRoutePredicateFactoryTests.plusHours(2);
        final boolean result = runPredicate(dateString1, dateString2);
        assertThat(result).as("Now is not before %s", dateString1).isFalse();
    }

    @Test
    public void betweenStringWorks() {
        String dateString1 = BetweenRoutePredicateFactoryTests.minusHours(1);
        String dateString2 = BetweenRoutePredicateFactoryTests.plusHours(1);
        ZonedDateTime parse = ZonedDateTime.parse(dateString1);
        final boolean result = runPredicate(dateString1, dateString2);
        assertThat(result).as("Now is not between %s and %s", dateString1, dateString2).isTrue();
    }

    @Test
    public void afterStringWorks() {
        String dateString1 = BetweenRoutePredicateFactoryTests.minusHours(2);
        String dateString2 = BetweenRoutePredicateFactoryTests.minusHours(1);
        final boolean result = runPredicate(dateString1, dateString2);
        assertThat(result).as("Now is not after %s", dateString2).isFalse();
    }

    @Test
    public void beforeEpochWorks() {
        String dateString1 = BetweenRoutePredicateFactoryTests.plusHoursMillis(1);
        String dateString2 = BetweenRoutePredicateFactoryTests.plusHoursMillis(2);
        final boolean result = runPredicate(dateString1, dateString2);
        assertThat(result).as("Now is not before %s", dateString1).isFalse();
    }

    @Test
    public void betweenEpochWorks() {
        String dateString1 = BetweenRoutePredicateFactoryTests.minusHoursMillis(1);
        String dateString2 = BetweenRoutePredicateFactoryTests.plusHoursMillis(1);
        final boolean result = runPredicate(dateString1, dateString2);
        assertThat(result).as("Now is not between %s and %s", dateString1, dateString2).isTrue();
    }

    @Test
    public void afterEpochWorks() {
        String dateString1 = BetweenRoutePredicateFactoryTests.minusHoursMillis(2);
        String dateString2 = BetweenRoutePredicateFactoryTests.minusHoursMillis(1);
        final boolean result = runPredicate(dateString1, dateString2);
        assertThat(result).as("Now is not after %s", dateString1).isFalse();
    }

    @Test
    public void testPredicates() {
        boolean result = new BetweenRoutePredicateFactory().apply(( c) -> c.setDatetime1(ZonedDateTime.now().minusHours(2)).setDatetime2(ZonedDateTime.now().plusHours(1))).test(BetweenRoutePredicateFactoryTests.getExchange());
        assertThat(result).isTrue();
    }
}

