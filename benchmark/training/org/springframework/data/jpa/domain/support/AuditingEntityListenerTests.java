/**
 * Copyright 2008-2019 the original author or authors.
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
package org.springframework.data.jpa.domain.support;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.sample.AnnotatedAuditableUser;
import org.springframework.data.jpa.domain.sample.AuditableRole;
import org.springframework.data.jpa.domain.sample.AuditableUser;
import org.springframework.data.jpa.domain.sample.AuditorAwareStub;
import org.springframework.data.jpa.repository.sample.AnnotatedAuditableUserRepository;
import org.springframework.data.jpa.repository.sample.AuditableUserRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 * Integration test for {@link AuditingEntityListener}.
 *
 * @author Oliver Gierke
 * @author Jens Schauder
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:auditing/auditing-entity-listener.xml")
@Transactional
@DirtiesContext
public class AuditingEntityListenerTests {
    @Autowired
    AuditableUserRepository repository;

    @Autowired
    AnnotatedAuditableUserRepository annotatedUserRepository;

    @Autowired
    AuditorAwareStub auditorAware;

    AuditableUser user;

    @Test
    public void auditsRootEntityCorrectly() {
        AuditingEntityListenerTests.assertDatesSet(user);
        AuditingEntityListenerTests.assertUserIsAuditor(user, user);
    }

    // DATAJPA-303
    @Test
    public void updatesLastModifiedDates() throws Exception {
        Thread.sleep(200);
        user.setFirstname("Oliver");
        user = saveAndFlush(user);
        Assert.assertThat(getCreatedDate().get().isBefore(getLastModifiedDate().get()), CoreMatchers.is(true));
    }

    @Test
    public void auditsTransitiveEntitiesCorrectly() {
        AuditableRole role = new AuditableRole();
        role.setName("ADMIN");
        user.addRole(role);
        repository.saveAndFlush(user);
        role = user.getRoles().iterator().next();
        AuditingEntityListenerTests.assertDatesSet(user);
        AuditingEntityListenerTests.assertDatesSet(role);
        AuditingEntityListenerTests.assertUserIsAuditor(user, user);
        AuditingEntityListenerTests.assertUserIsAuditor(user, role);
    }

    // DATAJPA-501
    @Test
    public void usesAnnotationMetadata() {
        AnnotatedAuditableUser auditableUser = save(new AnnotatedAuditableUser());
        Assert.assertThat(auditableUser.getCreateAt(), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(auditableUser.getLastModifiedBy(), CoreMatchers.is(CoreMatchers.notNullValue()));
    }
}

