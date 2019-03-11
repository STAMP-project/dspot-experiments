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
package example.springdata.cassandra.people;


import example.springdata.cassandra.util.CassandraKeyspace;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Integration test for {@link ReactiveCassandraTemplate}.
 *
 * @author Mark Paluch
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ReactiveCassandraTemplateIntegrationTest {
    @ClassRule
    public static final CassandraKeyspace CASSANDRA_KEYSPACE = CassandraKeyspace.onLocalhost();

    @Autowired
    ReactiveCassandraTemplate template;

    /**
     * This sample performs a count, inserts data and performs a count again using reactive operator chaining. It prints
     * the two counts ({@code 4} and {@code 6}) to the console.
     */
    @Test
    public void shouldInsertAndCountData() {
        Mono<Long> saveAndCount = // 
        // 
        // 
        // 
        // 
        template.count(Person.class).doOnNext(System.out::println).thenMany(// 
        Flux.just(new Person("Hank", "Schrader", 43), new Person("Mike", "Ehrmantraut", 62))).flatMap(template::insert).last().flatMap(( v) -> template.count(.class)).doOnNext(System.out::println);
        StepVerifier.create(saveAndCount).expectNext(6L).verifyComplete();
    }

    /**
     * Note that the all object conversions are performed before the results are printed to the console.
     */
    @Test
    public void convertReactorTypesToRxJava1() throws Exception {
        Flux<Person> flux = template.select(select().from("person").where(eq("lastname", "White")), Person.class);
        long count = // 
        // 
        // 
        // 
        rx.RxReactiveStreams.toObservable(flux).count().toSingle().toBlocking().value();// 

        assertThat(count).isEqualTo(2);
    }
}

