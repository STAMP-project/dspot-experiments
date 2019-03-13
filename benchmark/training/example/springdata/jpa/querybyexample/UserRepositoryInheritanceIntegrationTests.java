/**
 * Copyright 2016-2018 the original author or authors.
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
package example.springdata.jpa.querybyexample;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 * Integration test showing the usage of JPA Query-by-Example support through Spring Data repositories and entities
 * using inheritance.
 *
 * @author Mark Paluch
 * @author Oliver Gierke
 */
@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class UserRepositoryInheritanceIntegrationTests {
    @Autowired
    UserRepository repository;

    User skyler;

    User walter;

    User flynn;

    /**
     *
     *
     * @see #153
     */
    @Test
    public void countByExample() {
        Assert.assertThat(repository.count(Example.of(new User(null, "White", null))), CoreMatchers.is(3L));
    }

    /**
     *
     *
     * @see #153
     */
    @Test
    public void countSubtypesByExample() {
        Assert.assertThat(repository.count(Example.of(new SpecialUser(null, "White", null))), CoreMatchers.is(2L));
    }
}

