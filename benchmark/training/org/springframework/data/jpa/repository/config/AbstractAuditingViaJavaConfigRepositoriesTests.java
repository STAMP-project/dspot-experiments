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
package org.springframework.data.jpa.repository.config;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.domain.sample.AuditableUser;
import org.springframework.data.jpa.repository.sample.AuditableUserRepository;
import org.springframework.data.jpa.repository.sample.SampleEvaluationContextExtension;
import org.springframework.data.jpa.util.FixedDate;
import org.springframework.data.spel.spi.EvaluationContextExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.data.jpa.repository.sample.SampleEvaluationContextExtension.SampleSecurityContextHolder.getCurrent;


/**
 * Integration tests for auditing via Java config.
 *
 * @author Thomas Darimont
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@DirtiesContext
public abstract class AbstractAuditingViaJavaConfigRepositoriesTests {
    @Autowired
    AuditableUserRepository auditableUserRepository;

    @Autowired
    AuditorAware<AuditableUser> auditorAware;

    AuditableUser auditor;

    @Autowired
    EntityManager em;

    @Configuration
    @Import(InfrastructureConfig.class)
    @EnableJpaRepositories(basePackageClasses = AuditableUserRepository.class)
    static class TestConfig {
        @Bean
        EvaluationContextExtension sampleEvaluationContextExtension() {
            return new SampleEvaluationContextExtension();
        }
    }

    @Test
    public void basicAuditing() throws Exception {
        AuditableUser user = new AuditableUser(null);
        user.setFirstname("user");
        AuditableUser savedUser = save(user);
        TimeUnit.MILLISECONDS.sleep(10);
        Assert.assertThat(getCreatedDate(), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(getCreatedDate().get().isBefore(LocalDateTime.now()), CoreMatchers.is(true));
        AuditableUser createdBy = getCreatedBy().get();
        Assert.assertThat(createdBy, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(createdBy.getFirstname(), CoreMatchers.is(this.auditor.getFirstname()));
    }

    // DATAJPA-382
    @Test
    public void shouldAllowUseOfDynamicSpelParametersInUpdateQueries() {
        AuditableUser oliver = save(new AuditableUser(null, "oliver"));
        AuditableUser christoph = save(new AuditableUser(null, "christoph"));
        AuditableUser thomas = save(new AuditableUser(null, "thomas"));
        em.detach(oliver);
        em.detach(christoph);
        em.detach(thomas);
        em.detach(auditor);
        FixedDate.INSTANCE.setDate(new Date());
        getCurrent().setPrincipal(thomas);
        auditableUserRepository.updateAllNamesToUpperCase();
        // DateTime now = new DateTime(FixedDate.INSTANCE.getDate());
        LocalDateTime now = LocalDateTime.ofInstant(FixedDate.INSTANCE.getDate().toInstant(), ZoneId.systemDefault());
        List<AuditableUser> users = findAll();
        for (AuditableUser user : users) {
            Assert.assertThat(user.getFirstname(), CoreMatchers.is(user.getFirstname().toUpperCase()));
            Assert.assertThat(getLastModifiedBy(), CoreMatchers.is(Optional.of(thomas)));
            Assert.assertThat(getLastModifiedDate(), CoreMatchers.is(Optional.of(now)));
        }
    }
}

