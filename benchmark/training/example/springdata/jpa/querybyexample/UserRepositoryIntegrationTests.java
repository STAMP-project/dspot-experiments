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


import StringMatcher.ENDING;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 * Integration test showing the usage of JPA Query-by-Example support through Spring Data repositories.
 *
 * @author Mark Paluch
 * @author Oliver Gierke
 * @author Jens Schauder
 */
@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class UserRepositoryIntegrationTests {
    @Autowired
    UserRepository repository;

    User skyler;

    User walter;

    User flynn;

    User marie;

    User hank;

    /**
     *
     *
     * @see #153
     */
    @Test
    public void countBySimpleExample() {
        Example<User> example = Example.of(new User(null, "White", null));
        assertThat(repository.count(example)).isEqualTo(3L);
    }

    /**
     *
     *
     * @see #153
     */
    @Test
    public void ignorePropertiesAndMatchByAge() {
        Example<User> example = Example.of(flynn, // 
        matching().withIgnorePaths("firstname", "lastname"));
        assertThat(repository.findOne(example)).contains(flynn);
    }

    /**
     *
     *
     * @see #153
     */
    @Test
    public void substringMatching() {
        Example<User> example = Example.of(new User("er", null, null), // 
        matching().withStringMatcher(ENDING));
        assertThat(repository.findAll(example)).containsExactly(skyler, walter);
    }

    /**
     *
     *
     * @see #153
     */
    @Test
    public void matchStartingStringsIgnoreCase() {
        Example<User> example = Example.of(new User("Walter", "WHITE", null), // 
        // 
        // 
        matching().withIgnorePaths("age").withMatcher("firstname", startsWith()).withMatcher("lastname", ignoreCase()));
        assertThat(repository.findAll(example)).containsExactlyInAnyOrder(flynn, walter);
    }

    /**
     *
     *
     * @see #153
     */
    @Test
    public void configuringMatchersUsingLambdas() {
        Example<User> example = Example.of(new User("Walter", "WHITE", null), // 
        // 
        // 
        matching().withIgnorePaths("age").withMatcher("firstname", ( matcher) -> matcher.startsWith()).withMatcher("lastname", ( matcher) -> matcher.ignoreCase()));
        assertThat(repository.findAll(example)).containsExactlyInAnyOrder(flynn, walter);
    }

    /**
     *
     *
     * @see #153
     */
    @Test
    public void valueTransformer() {
        Example<User> example = Example.of(new User(null, "White", 99), // 
        matching().withMatcher("age", ( matcher) -> matcher.transform(( value) -> Optional.of(Integer.valueOf(50)))));
        assertThat(repository.findAll(example)).containsExactly(walter);
    }
}

