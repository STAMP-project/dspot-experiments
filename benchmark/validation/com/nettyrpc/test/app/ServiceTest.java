package com.nettyrpc.test.app;


import com.nettyrpc.client.RPCFuture;
import com.nettyrpc.client.RpcClient;
import com.nettyrpc.client.proxy.IAsyncObjectProxy;
import com.nettyrpc.test.client.HelloService;
import com.nettyrpc.test.client.Person;
import com.nettyrpc.test.client.PersonService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:client-spring.xml")
public class ServiceTest {
    @Autowired
    private RpcClient rpcClient;

    @Test
    public void helloTest1() {
        HelloService helloService = rpcClient.create(HelloService.class);
        String result = helloService.hello("World");
        Assert.assertEquals("Hello! World", result);
    }

    @Test
    public void helloTest2() {
        HelloService helloService = rpcClient.create(HelloService.class);
        Person person = new Person("Yong", "Huang");
        String result = helloService.hello(person);
        Assert.assertEquals("Hello! Yong Huang", result);
    }

    @Test
    public void helloPersonTest() {
        PersonService personService = rpcClient.create(PersonService.class);
        int num = 5;
        List<Person> persons = personService.GetTestPerson("xiaoming", num);
        List<Person> expectedPersons = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            expectedPersons.add(new Person(Integer.toString(i), "xiaoming"));
        }
        MatcherAssert.assertThat(persons, IsEqual.equalTo(expectedPersons));
        for (int i = 0; i < (persons.size()); ++i) {
            System.out.println(persons.get(i));
        }
    }

    @Test
    public void helloFutureTest1() throws InterruptedException, ExecutionException {
        IAsyncObjectProxy helloService = rpcClient.createAsync(HelloService.class);
        RPCFuture result = helloService.call("hello", "World");
        Assert.assertEquals("Hello! World", result.get());
    }

    @Test
    public void helloFutureTest2() throws InterruptedException, ExecutionException {
        IAsyncObjectProxy helloService = rpcClient.createAsync(HelloService.class);
        Person person = new Person("Yong", "Huang");
        RPCFuture result = helloService.call("hello", person);
        Assert.assertEquals("Hello! Yong Huang", result.get());
    }

    @Test
    public void helloPersonFutureTest1() throws InterruptedException, ExecutionException {
        IAsyncObjectProxy helloPersonService = rpcClient.createAsync(PersonService.class);
        int num = 5;
        RPCFuture result = helloPersonService.call("GetTestPerson", "xiaoming", num);
        List<Person> persons = ((List<Person>) (result.get()));
        List<Person> expectedPersons = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            expectedPersons.add(new Person(Integer.toString(i), "xiaoming"));
        }
        MatcherAssert.assertThat(persons, IsEqual.equalTo(expectedPersons));
        for (int i = 0; i < num; ++i) {
            System.out.println(persons.get(i));
        }
    }
}

