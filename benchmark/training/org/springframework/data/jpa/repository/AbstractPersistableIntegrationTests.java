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
package org.springframework.data.jpa.repository;


import javax.persistence.EntityManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.sample.CustomAbstractPersistable;
import org.springframework.data.jpa.repository.sample.CustomAbstractPersistableRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 * Integration tests for {@link AbstractPersistable}.
 *
 * @author Thomas Darimont
 * @author Oliver Gierke
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:config/namespace-autoconfig-context.xml" })
public class AbstractPersistableIntegrationTests {
    @Autowired
    CustomAbstractPersistableRepository repository;

    @Autowired
    EntityManager em;

    // DATAJPA-622
    @Test
    public void shouldBeAbleToSaveAndLoadCustomPersistableWithUuidId() {
        CustomAbstractPersistable entity = new CustomAbstractPersistable();
        CustomAbstractPersistable saved = save(entity);
        CustomAbstractPersistable found = repository.findById(getId()).get();
        Assert.assertThat(found, is(saved));
    }

    // DATAJPA-848
    @Test
    public void equalsWorksForProxiedEntities() {
        CustomAbstractPersistable entity = saveAndFlush(new CustomAbstractPersistable());
        em.clear();
        CustomAbstractPersistable proxy = repository.getOne(getId());
        Assert.assertThat(proxy, is(proxy));
    }
}

