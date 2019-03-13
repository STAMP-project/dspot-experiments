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
package org.springframework.data.jpa.repository.support;


import java.util.Arrays;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.jpa.domain.sample.PersistableWithIdClass;
import org.springframework.data.jpa.domain.sample.PersistableWithIdClassPK;
import org.springframework.data.jpa.domain.sample.SampleEntity;
import org.springframework.data.jpa.domain.sample.SampleEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 * Integration test for {@link JpaRepository}.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:infrastructure.xml" })
@Transactional
public class JpaRepositoryTests {
    @PersistenceContext
    EntityManager em;

    JpaRepository<SampleEntity, SampleEntityPK> repository;

    CrudRepository<PersistableWithIdClass, PersistableWithIdClassPK> idClassRepository;

    @Test
    public void testCrudOperationsForCompoundKeyEntity() throws Exception {
        SampleEntity entity = new SampleEntity("foo", "bar");
        repository.saveAndFlush(entity);
        Assert.assertThat(repository.existsById(new SampleEntityPK("foo", "bar")), CoreMatchers.is(true));
        Assert.assertThat(repository.count(), CoreMatchers.is(1L));
        Assert.assertThat(repository.findById(new SampleEntityPK("foo", "bar")), CoreMatchers.is(Optional.of(entity)));
        repository.deleteAll(Arrays.asList(entity));
        repository.flush();
        Assert.assertThat(repository.count(), CoreMatchers.is(0L));
    }

    // DATAJPA-50
    @Test
    public void executesCrudOperationsForEntityWithIdClass() {
        PersistableWithIdClass entity = new PersistableWithIdClass(1L, 1L);
        idClassRepository.save(entity);
        Assert.assertThat(entity.getFirst(), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(entity.getSecond(), CoreMatchers.is(CoreMatchers.notNullValue()));
        PersistableWithIdClassPK id = new PersistableWithIdClassPK(entity.getFirst(), entity.getSecond());
        Assert.assertThat(idClassRepository.findById(id), CoreMatchers.is(Optional.of(entity)));
    }

    // DATAJPA-266
    @Test
    public void testExistsForDomainObjectsWithCompositeKeys() throws Exception {
        PersistableWithIdClass s1 = idClassRepository.save(new PersistableWithIdClass(1L, 1L));
        PersistableWithIdClass s2 = idClassRepository.save(new PersistableWithIdClass(2L, 2L));
        Assert.assertThat(idClassRepository.existsById(s1.getId()), CoreMatchers.is(true));
        Assert.assertThat(idClassRepository.existsById(s2.getId()), CoreMatchers.is(true));
        Assert.assertThat(idClassRepository.existsById(new PersistableWithIdClassPK(1L, 2L)), CoreMatchers.is(false));
    }

    // DATAJPA-527
    @Test
    public void executesExistsForEntityWithIdClass() {
        PersistableWithIdClass entity = new PersistableWithIdClass(1L, 1L);
        idClassRepository.save(entity);
        Assert.assertThat(entity.getFirst(), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(entity.getSecond(), CoreMatchers.is(CoreMatchers.notNullValue()));
        PersistableWithIdClassPK id = new PersistableWithIdClassPK(entity.getFirst(), entity.getSecond());
        Assert.assertThat(idClassRepository.existsById(id), CoreMatchers.is(true));
    }

    private static interface SampleEntityRepository extends JpaRepository<SampleEntity, SampleEntityPK> {}

    private static interface SampleWithIdClassRepository extends CrudRepository<PersistableWithIdClass, PersistableWithIdClassPK> {}
}

