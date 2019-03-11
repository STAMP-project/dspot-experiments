/**
 * Copyright 2018 the original author or authors.
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
package example.springdata.mongodb.sync;


import State.CREATED;
import State.DONE;
import com.mongodb.MongoClient;
import example.springdata.mongodb.util.EmbeddedMongo;
import java.util.function.Consumer;
import org.assertj.core.api.Assertions;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * Test showing MongoDB Transaction usage through a synchronous (imperative) API using Spring's managed transactions.
 *
 * @author Christoph Strobl
 * @unknown The Core - Peter V. Brett
 * @see org.springframework.transaction.annotation.Transactional
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
public class TransitionServiceTests {
    @ClassRule
    public static EmbeddedMongo replSet = EmbeddedMongo.replSet().configure();

    static final String DB_NAME = "spring-data-tx-examples";

    @Autowired
    TransitionService transitionService;

    @Autowired
    MongoClient client;

    @Configuration
    @ComponentScan
    @EnableMongoRepositories
    @EnableTransactionManagement
    static class Config extends AbstractMongoConfiguration {
        @Bean
        PlatformTransactionManager transactionManager(MongoDbFactory dbFactory) {
            return new org.springframework.data.mongodb.MongoTransactionManager(dbFactory);
        }

        @Override
        @Bean
        public MongoClient mongoClient() {
            return TransitionServiceTests.replSet.getMongoClient();
        }

        @Override
        protected String getDatabaseName() {
            return TransitionServiceTests.DB_NAME;
        }
    }

    @Test
    public void txCommitRollback() {
        for (int i = 0; i < 10; i++) {
            Process process = transitionService.newProcess();
            try {
                transitionService.run(getId());
                Assertions.assertThat(stateInDb(process)).isEqualTo(DONE);
            } catch (IllegalStateException e) {
                Assertions.assertThat(stateInDb(process)).isEqualTo(CREATED);
            }
        }
        client.getDatabase(TransitionServiceTests.DB_NAME).getCollection("processes").find(new Document()).forEach(((Consumer<? super Document>) (System.out::println)));
    }
}

