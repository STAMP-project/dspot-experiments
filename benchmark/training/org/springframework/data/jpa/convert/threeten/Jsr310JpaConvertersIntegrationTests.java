/**
 * Copyright 2014-2019 the original author or authors.
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
package org.springframework.data.jpa.convert.threeten;


import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.domain.support.AbstractAttributeConverterIntegrationTests;
import org.springframework.data.jpa.support.EntityManagerTestUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;


/**
 * Integration tests for {@link Jsr310JpaConverters}.
 *
 * @author Oliver Gierke
 */
@ContextConfiguration
@Transactional
public class Jsr310JpaConvertersIntegrationTests extends AbstractAttributeConverterIntegrationTests {
    @Configuration
    static class Config extends AbstractAttributeConverterIntegrationTests.InfrastructureConfig {
        @Override
        protected String getPackageName() {
            return getClass().getPackage().getName();
        }
    }

    @PersistenceContext
    EntityManager em;

    // DATAJPA-650
    @Test
    public void usesJsr310JpaConverters() {
        Assume.assumeTrue(EntityManagerTestUtils.currentEntityManagerIsAJpa21EntityManager(em));
        DateTimeSample sample = new DateTimeSample();
        sample.instant = Instant.now();
        sample.localDate = LocalDate.now();
        sample.localTime = LocalTime.now();
        sample.localDateTime = LocalDateTime.now();
        sample.zoneId = ZoneId.of("Europe/Berlin");
        em.persist(sample);
        em.flush();
        em.clear();
        DateTimeSample result = em.find(DateTimeSample.class, sample.id);
        Assert.assertThat(result, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(result.instant, CoreMatchers.is(sample.instant));
        Assert.assertThat(result.localDate, CoreMatchers.is(sample.localDate));
        Assert.assertThat(result.localTime, CoreMatchers.is(sample.localTime));
        Assert.assertThat(result.localDateTime, CoreMatchers.is(sample.localDateTime));
        Assert.assertThat(result.zoneId, CoreMatchers.is(sample.zoneId));
    }
}

