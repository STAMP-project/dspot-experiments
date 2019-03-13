package com.baeldung.jni;


import org.junit.Assert;
import org.junit.Test;


public class JNINativeManualTest {
    @Test
    public void whenNativeHelloWorld_thenOutputIsAsExpected() {
        HelloWorldJNI helloWorld = new HelloWorldJNI();
        String helloFromNative = helloWorld.sayHello();
        Assert.assertTrue(((!(helloFromNative.isEmpty())) && (helloFromNative.equals("Hello from C++ !!"))));
    }

    @Test
    public void whenSumNative_thenResultIsArithmeticallyCorrect() {
        ExampleParametersJNI parametersNativeMethods = new ExampleParametersJNI();
        Assert.assertTrue(((parametersNativeMethods.sumIntegers(200, 400)) == 600L));
    }

    @Test
    public void whenSayingNativeHelloToMe_thenResultIsAsExpected() {
        ExampleParametersJNI parametersNativeMethods = new ExampleParametersJNI();
        Assert.assertEquals(parametersNativeMethods.sayHelloToMe("Orange", true), "Ms. Orange");
    }

    @Test
    public void whenCreatingNativeObject_thenObjectIsNotNullAndHasCorrectData() {
        String name = "Iker Casillas";
        double balance = 2378.78;
        ExampleObjectsJNI objectsNativeMethods = new ExampleObjectsJNI();
        UserData userFromNative = objectsNativeMethods.createUser(name, balance);
        Assert.assertNotNull(userFromNative);
        Assert.assertEquals(userFromNative.name, name);
        Assert.assertTrue(((userFromNative.balance) == balance));
    }

    @Test
    public void whenNativeCallingObjectMethod_thenResultIsAsExpected() {
        String name = "Sergio Ramos";
        double balance = 666.77;
        ExampleObjectsJNI objectsNativeMethods = new ExampleObjectsJNI();
        UserData userData = new UserData();
        userData.name = name;
        userData.balance = balance;
        Assert.assertEquals(objectsNativeMethods.printUserData(userData), ((("[name]=" + name) + ", [balance]=") + balance));
    }
}

