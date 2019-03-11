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
package org.springframework.data.jpa.repository;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.sample.User;
import org.springframework.data.jpa.repository.sample.RedeclaringRepositoryMethodsRepository;
import org.springframework.data.jpa.repository.sample.SampleConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 *
 * @author Thomas Darimont
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SampleConfig.class)
@Transactional
public class RedeclaringRepositoryMethodsTests {
    @Autowired
    RedeclaringRepositoryMethodsRepository repository;

    User ollie;

    User tom;

    // DATAJPA-398
    @Test
    public void adjustedWellKnownPagedFindAllMethodShouldReturnOnlyTheUserWithFirstnameOliver() {
        ollie = save(ollie);
        tom = save(tom);
        Page<User> page = repository.findAll(PageRequest.of(0, 2));
        Assert.assertThat(page.getNumberOfElements(), is(1));
        Assert.assertThat(page.getContent().get(0).getFirstname(), is("Oliver"));
    }

    // DATAJPA-398
    @Test
    public void adjustedWllKnownFindAllMethodShouldReturnAnEmptyList() {
        ollie = save(ollie);
        tom = save(tom);
        List<User> result = repository.findAll();
        Assert.assertThat(result.isEmpty(), is(true));
    }
}

