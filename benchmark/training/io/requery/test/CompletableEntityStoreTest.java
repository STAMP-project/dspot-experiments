/**
 * Copyright 2016 requery.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.requery.test;


import Person.ABOUT;
import Person.AGE;
import Person.ID;
import Person.NAME;
import io.requery.Persistable;
import io.requery.async.CompletionStageEntityStore;
import io.requery.test.model.Person;
import io.requery.test.model.Phone;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;


public class CompletableEntityStoreTest extends RandomData {
    protected CompletionStageEntityStore<Persistable> data;

    private CompletableEntityStoreTest.TransactionState transactionState;

    private enum TransactionState {

        BEGIN,
        COMMIT,
        ROLLBACK;}

    @Test
    public void testInsert() throws Exception {
        Person person = randomPerson();
        data.insert(person).thenAccept(new Consumer<Person>() {
            @Override
            public void accept(Person person) {
                Assert.assertTrue(((person.getId()) > 0));
                Person cached = data.select(Person.class).where(ID.equal(person.getId())).get().first();
                Assert.assertSame(cached, person);
            }
        }).toCompletableFuture().get();
        Assert.assertEquals(transactionState, CompletableEntityStoreTest.TransactionState.COMMIT);
    }

    @Test
    public void testInsertCount() throws Exception {
        Person person = randomPerson();
        data.insert(person).thenAccept(new Consumer<Person>() {
            @Override
            public void accept(Person person) {
                Assert.assertTrue(((person.getId()) > 0));
            }
        }).thenCompose(new Function<Void, CompletionStage<Integer>>() {
            @Override
            public CompletionStage<Integer> apply(Void aVoid) {
                return data.count(Person.class).get().toCompletableFuture();
            }
        }).toCompletableFuture().get();
    }

    @Test
    public void testInsertOneToMany() throws Exception {
        final Person person = randomPerson();
        data.insert(person).thenApply(new Function<Person, Phone>() {
            @Override
            public Phone apply(Person person) {
                Phone phone1 = randomPhone();
                phone1.setOwner(person);
                return phone1;
            }
        }).thenCompose(new Function<Phone, CompletionStage<Phone>>() {
            @Override
            public CompletionStage<Phone> apply(Phone phone) {
                return data.insert(phone);
            }
        }).toCompletableFuture().get();
        HashSet<Phone> set = new HashSet(person.getPhoneNumbers().toList());
        Assert.assertEquals(1, set.size());
    }

    @Test
    public void testQueryUpdate() throws InterruptedException, ExecutionException {
        Person person = randomPerson();
        person.setAge(100);
        data.insert(person).toCompletableFuture().get();
        CompletableFuture<Integer> rowCount = data.update(Person.class).set(ABOUT, "nothing").set(AGE, 50).where(AGE.equal(100)).get().toCompletableFuture(Executors.newSingleThreadExecutor());
        Assert.assertEquals(1, rowCount.get().intValue());
    }

    @Test
    public void testInsertBlocking() throws Exception {
        final Person person = randomPerson();
        data.toBlocking().insert(person);
        Assert.assertTrue(((person.getId()) > 0));
    }

    @Test
    public void testQueryStream() throws Exception {
        for (int i = 0; i < 30; i++) {
            Person person = randomPerson();
            data.insert(person).toCompletableFuture().get();
        }
        final List<Person> people = new ArrayList<>();
        data.select(Person.class).orderBy(NAME.asc().nullsLast()).limit(50).get().stream().forEach(new Consumer<Person>() {
            @Override
            public void accept(Person person) {
                people.add(person);
            }
        });
        Assert.assertSame(30, people.size());
    }
}

