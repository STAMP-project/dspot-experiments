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
package example.springdata.mongodb.querybyexample;


import StringMatcher.REGEX;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Integration test showing the usage of MongoDB Query-by-Example support through Spring Data repositories for a case
 * where two domain types are stored in one collection.
 *
 * @author Mark Paluch
 * @author Oliver Gierke
 * @unknown Paul van Dyk - VONYC Sessions Episode 496 with guest Armin van Buuren
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ContactRepositoryIntegrationTests {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    MongoOperations mongoOperations;

    Person skyler;

    Person walter;

    Person flynn;

    Relative marie;

    Relative hank;

    /**
     *
     *
     * @see #153
     */
    @Test
    public void countByConcreteSubtypeExample() {
        Example<Person> example = Example.of(new Person(null, null, null));
        Assert.assertThat(userRepository.count(example), CoreMatchers.is(3L));
    }

    /**
     *
     *
     * @see #153
     */
    @Test
    public void findAllPersonsBySimpleExample() {
        Example<Person> example = // 
        Example.of(new Person(".*", null, null), matching().withStringMatcher(REGEX));
        Assert.assertThat(userRepository.findAll(example), containsInAnyOrder(skyler, walter, flynn));
        Assert.assertThat(userRepository.findAll(example), CoreMatchers.not(containsInAnyOrder(hank, marie)));
    }

    /**
     *
     *
     * @see #153
     */
    @Test
    public void findAllRelativesBySimpleExample() {
        Example<Relative> example = // 
        Example.of(new Relative(".*", null, null), matching().withStringMatcher(REGEX));
        Assert.assertThat(contactRepository.findAll(example), containsInAnyOrder(hank, marie));
        Assert.assertThat(contactRepository.findAll(example), CoreMatchers.not(containsInAnyOrder(skyler, walter, flynn)));
    }
}

