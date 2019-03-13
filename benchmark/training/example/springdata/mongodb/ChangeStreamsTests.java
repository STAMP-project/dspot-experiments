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
package example.springdata.mongodb;


import com.mongodb.MongoClient;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.reactivestreams.client.MongoClients;
import example.springdata.mongodb.util.EmbeddedMongo;
import java.time.Duration;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.messaging.ChangeStreamRequest;
import org.springframework.data.mongodb.core.messaging.DefaultMessageListenerContainer;
import org.springframework.data.mongodb.core.messaging.MessageListenerContainer;
import org.springframework.data.mongodb.core.messaging.Subscription;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


/**
 * A simple Test demonstrating required {@link Configuration} for consumption of MongoDB
 * <a href="https://docs.mongodb.com/manual/changeStreams/">Change Streams</a> using the sync and Reactive Streams Java
 * driver.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 */
@RunWith(SpringRunner.class)
@DataMongoTest
public class ChangeStreamsTests {
    @ClassRule
    public static EmbeddedMongo replSet = EmbeddedMongo.replSet().configure();

    @Autowired
    MessageListenerContainer container;// for imperative style


    @Autowired
    MongoTemplate template;

    @Autowired
    ReactiveMongoOperations reactiveTemplate;// for reactive style


    Person gabriel = new Person("Gabriel", "Lorca", 30);

    Person michael = new Person("Michael", "Burnham", 30);

    Person ash = new Person("Ash", "Tyler", 35);

    /**
     * Configuration? Yes we need a bit of it - Do not worry, it won't be much!
     */
    @SpringBootApplication(exclude = EmbeddedMongoAutoConfiguration.class)
    static class Config {
        /**
         * Configure {@link MongoClient} to enable
         * {@link org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration}.
         *
         * @return the {@link MongoClient}.
         */
        @Bean
        MongoClient mongoClient() {
            return ChangeStreamsTests.replSet.getMongoClient();
        }

        /**
         * Configure {@link SimpleMongoDbFactory} pointing to the embedded MongoDB connection.
         *
         * @return a new {@link SimpleReactiveMongoDatabaseFactory}.
         */
        @Bean
        SimpleMongoDbFactory mongoDbFactory() {
            return new SimpleMongoDbFactory(ChangeStreamsTests.replSet.getMongoClient(), "changestreams");
        }

        /**
         * Configure {@link SimpleReactiveMongoDatabaseFactory} pointing to the embedded MongoDB connection.
         *
         * @return a new {@link SimpleReactiveMongoDatabaseFactory}.
         */
        @Bean
        SimpleReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory() {
            return new SimpleReactiveMongoDatabaseFactory(MongoClients.create(ChangeStreamsTests.replSet.getConnectionString()), "changestreams");
        }

        /**
         * Since listening to a <a href="https://docs.mongodb.com/manual/changeStreams/">Change Stream</a> using the sync
         * MongoDB Java Driver is a blocking class, we need to move load to another {@link Thread} by simply using a
         * {@link MessageListenerContainer}.
         * <p />
         * As this is a {@link org.springframework.context.SmartLifecycle smart lifecycle component} we do actually not need
         * to worry about its lifecycle, the resource allocation and freeing.
         *
         * @param template
         * 		must not be {@literal null}.
         * @return a new {@link MessageListenerContainer}
         */
        @Bean
        MessageListenerContainer messageListenerContainer(MongoTemplate template) {
            return new DefaultMessageListenerContainer(template) {
                /* auto startup will be changed for M2, so this should no longer be required. */
                @Override
                public boolean isAutoStartup() {
                    return true;
                }
            };
        }
    }

    /**
     * Use the {@link MessageListenerContainer} registered within the
     * {@link org.springframework.context.ApplicationContext} to subscribe to MongoDB Change Streams. Events published via
     * {@link com.mongodb.client.ChangeStreamIterable} are passed to the
     * {@link org.springframework.data.mongodb.core.messaging.MessageListener#onMessage(Message) MessageListener}.
     */
    @Test
    public void imperativeChangeEvents() throws InterruptedException {
        CollectingMessageListener<ChangeStreamDocument<Document>, Person> messageListener = new CollectingMessageListener();
        ChangeStreamRequest<Person> request = // we are only interested in inserts
        // 
        // 
        ChangeStreamRequest.builder(messageListener).collection("person").filter(newAggregation(match(where("operationType").is("insert")))).build();
        Subscription subscription = container.register(request, Person.class);
        subscription.await(Duration.ofMillis(200));// wait till the subscription becomes active

        template.save(gabriel);
        template.save(ash);
        messageListener.awaitNextMessages(2);
        assertThat(messageListener.messageCount()).isEqualTo(2);// first two insert events, so far so good

        // 
        // 
        // 
        template.update(Person.class).matching(query(where("id").is(ash.getId()))).apply(update("age", 40)).first();
        Thread.sleep(200);
        assertThat(messageListener.messageCount()).isEqualTo(2);// updates are skipped

        template.save(michael);
        messageListener.awaitNextMessages(1);
        assertThat(messageListener.messageCount()).isEqualTo(3);// there we go, all events received.

    }

    /**
     * Use a {@link reactor.core.publisher.Flux} to subscribe to MongoDB Change Streams.
     */
    @Test
    public void reactiveChangeEvents() {
        Flux<ChangeStreamEvent<Person>> changeStream = reactiveTemplate.changeStream("person", ChangeStreamOptions.builder().filter(newAggregation(match(where("operationType").is("insert")))).build(), Person.class);
        // change streams are infinite streams, at some point we need to unsubscribe
        // there we go, all events received.
        // updates are skipped
        // Save another document and await its change event
        // 
        // Update a document
        // wait till change streams becomes active
        // Save documents and await their change events
        // 
        // 
        StepVerifier.create(changeStream).expectSubscription().expectNoEvent(Duration.ofMillis(200)).then(() -> {
            StepVerifier.create(reactiveTemplate.save(gabriel)).expectNextCount(1).verifyComplete();
            StepVerifier.create(reactiveTemplate.save(ash)).expectNextCount(1).verifyComplete();
        }).expectNextCount(2).then(() -> {
            StepVerifier.create(// 
            // 
            // 
            reactiveTemplate.update(.class).matching(query(where("id").is(ash.getId()))).apply(update("age", 40)).first()).expectNextCount(1).verifyComplete();
        }).expectNoEvent(Duration.ofMillis(200)).then(() -> {
            StepVerifier.create(reactiveTemplate.save(michael)).expectNextCount(1).verifyComplete();
        }).expectNextCount(1).thenCancel().verify();
    }
}

