/**
 * Copyright 2014-2018 the original author or authors.
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
package example.springdata.mongodb.advanced;


import ApplicationConfiguration.SYSTEM_PROFILE_DB;
import com.mongodb.client.FindIterable;
import example.springdata.mongodb.customer.Customer;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;

import static AdvancedRepository.META_COMMENT;


/**
 *
 *
 * @author Christoph Strobl
 * @author Oliver Gierke
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AdvancedIntegrationTests {
    @Autowired
    AdvancedRepository repository;

    @Autowired
    MongoOperations operations;

    Customer dave;

    Customer oliver;

    Customer carter;

    /**
     * This test demonstrates usage of {@code $comment} {@link Meta} usage. One can also enable profiling using
     * {@code --profile=2} when starting {@literal mongod}.
     * <p>
     * <strong>NOTE</strong>: Requires MongoDB v. 2.6.4+
     */
    @Test
    public void findByFirstnameUsingMetaAttributes() {
        // execute derived finder method just to get the comment in the profile log
        repository.findByFirstname(dave.getFirstname());
        // execute another finder without meta attributes that should not be picked up
        repository.findByLastname(dave.getLastname(), Sort.by("firstname"));
        FindIterable<Document> cursor = operations.getCollection(SYSTEM_PROFILE_DB).find(new com.mongodb.BasicDBObject("query.$comment", META_COMMENT));
        for (Document document : cursor) {
            Document query = ((Document) (document.get("query")));
            assertThat(query).containsKey("foo");
        }
    }
}

