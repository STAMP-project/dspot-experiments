package com.baeldung.transaction;


import com.baeldung.config.MongoReactiveConfig;
import com.baeldung.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * This test requires:
 * * mongodb instance running on the environment
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MongoReactiveConfig.class)
public class MongoTransactionReactiveLiveTest {
    @Autowired
    private ReactiveMongoOperations reactiveOps;

    @Test
    public void whenPerformTransaction_thenSuccess() {
        User user1 = new User("Jane", 23);
        User user2 = new User("John", 34);
        reactiveOps.inTransaction().execute(( action) -> action.insert(user1).then(action.insert(user2)));
    }
}

