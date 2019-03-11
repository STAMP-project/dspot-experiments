/**
 * Copyright 2011-2019 the original author or authors.
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


import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.sample.Role;
import org.springframework.data.jpa.repository.sample.RoleRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;


/**
 * Integration tests for {@link RoleRepository}.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml" })
@Transactional
public class RoleRepositoryIntegrationTests {
    @Autowired
    RoleRepository repository;

    @Test
    public void createsRole() throws Exception {
        Role reference = new Role("ADMIN");
        Role result = save(reference);
        Assert.assertThat(result, is(reference));
    }

    @Test
    public void updatesRole() throws Exception {
        Role reference = new Role("ADMIN");
        Role result = save(reference);
        Assert.assertThat(result, is(reference));
        // Change role name
        ReflectionTestUtils.setField(reference, "name", "USER");
        repository.save(reference);
        Assert.assertThat(repository.findById(result.getId()), is(Optional.of(reference)));
    }

    // DATAJPA-509
    @Test
    public void shouldUseExplicitlyConfiguredEntityNameInOrmXmlInCountQueries() {
        Role reference = new Role("ADMIN");
        repository.save(reference);
        Assert.assertThat(count(), is(1L));
    }

    // DATAJPA-509
    @Test
    public void shouldUseExplicitlyConfiguredEntityNameInOrmXmlInExistsQueries() {
        Role reference = new Role("ADMIN");
        reference = save(reference);
        Assert.assertThat(existsById(reference.getId()), is(true));
    }

    // DATAJPA-509
    @Test
    public void shouldUseExplicitlyConfiguredEntityNameInDerivedCountQueries() {
        Role reference = new Role("ADMIN");
        reference = save(reference);
        Assert.assertThat(repository.countByName(reference.getName()), is(1L));
    }
}

