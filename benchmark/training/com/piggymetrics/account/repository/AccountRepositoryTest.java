package com.piggymetrics.account.repository;


import com.piggymetrics.account.domain.Account;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@DataMongoTest
public class AccountRepositoryTest {
    @Autowired
    private AccountRepository repository;

    @Test
    public void shouldFindAccountByName() {
        Account stub = getStubAccount();
        repository.save(stub);
        Account found = repository.findByName(stub.getName());
        Assert.assertEquals(stub.getLastSeen(), found.getLastSeen());
        Assert.assertEquals(stub.getNote(), found.getNote());
        Assert.assertEquals(stub.getIncomes().size(), found.getIncomes().size());
        Assert.assertEquals(stub.getExpenses().size(), found.getExpenses().size());
    }
}

