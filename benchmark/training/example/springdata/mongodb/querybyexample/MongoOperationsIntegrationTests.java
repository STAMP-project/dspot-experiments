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


import StringMatcher.ENDING;
import java.util.Optional;
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
 * Integration test showing the usage of MongoDB Query-by-Example support through Spring Data repositories.
 *
 * @author Mark Paluch
 * @author Oliver Gierke
 */
@SuppressWarnings("unused")
@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoOperationsIntegrationTests {
    @Autowired
    MongoOperations operations;

    Person skyler;

    Person walter;

    Person flynn;

    Person marie;

    Person hank;

    /**
     *
     *
     * @see #153
     */
    @Test
    public void ignoreNullProperties() {
        Query query = query(byExample(new Person(null, null, 17)));
        Assert.assertThat(operations.find(query, Person.class), CoreMatchers.hasItems(flynn));
    }

    /**
     *
     *
     * @see #153
     */
    @Test
    public void substringMatching() {
        Example<Person> example = Example.of(new Person("er", null, null), // 
        matching().withStringMatcher(ENDING));
        Assert.assertThat(operations.find(query(byExample(example)), Person.class), CoreMatchers.hasItems(skyler, walter));
    }

    /**
     *
     *
     * @see #154
     */
    @Test
    public void regexMatching() {
        Example<Person> example = Example.of(new Person("(Skyl|Walt)er", null, null), // 
        matching().withMatcher("firstname", ( matcher) -> matcher.regex()));
        Assert.assertThat(operations.find(query(byExample(example)), Person.class), CoreMatchers.hasItems(skyler, walter));
    }

    /**
     *
     *
     * @see #153
     */
    @Test
    public void matchStartingStringsIgnoreCase() {
        Example<Person> example = Example.of(new Person("Walter", "WHITE", null), // 
        // 
        // 
        matching().withIgnorePaths("age").withMatcher("firstname", CoreMatchers.startsWith()).withMatcher("lastname", ignoreCase()));
        Assert.assertThat(operations.find(query(byExample(example)), Person.class), CoreMatchers.hasItems(flynn, walter));
    }

    /**
     *
     *
     * @see #153
     */
    @Test
    public void configuringMatchersUsingLambdas() {
        Example<Person> example = Example.of(new Person("Walter", "WHITE", null), // 
        // 
        // 
        matching().withIgnorePaths("age").withMatcher("firstname", ( matcher) -> matcher.startsWith()).withMatcher("lastname", ( matcher) -> matcher.ignoreCase()));
        Assert.assertThat(operations.find(query(byExample(example)), Person.class), CoreMatchers.hasItems(flynn, walter));
    }

    /**
     *
     *
     * @see #153
     */
    @Test
    public void valueTransformer() {
        Example<Person> example = Example.of(new Person(null, "White", 99), // 
        matching().withMatcher("age", ( matcher) -> matcher.transform(( value) -> Optional.of(Integer.valueOf(50)))));
        Assert.assertThat(operations.find(query(byExample(example)), Person.class), CoreMatchers.hasItems(walter));
    }
}

