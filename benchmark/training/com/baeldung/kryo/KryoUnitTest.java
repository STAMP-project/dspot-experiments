package com.baeldung.kryo;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class KryoUnitTest {
    private Kryo kryo;

    private Output output;

    private Input input;

    @Test
    public void givenObject_whenSerializing_thenReadCorrectly() {
        Object someObject = "Some string";
        kryo.writeClassAndObject(output, someObject);
        output.close();
        Object theObject = kryo.readClassAndObject(input);
        input.close();
        Assert.assertEquals(theObject, "Some string");
    }

    @Test
    public void givenObjects_whenSerializing_thenReadCorrectly() {
        String someString = "Multiple Objects";
        Date someDate = new Date(915170400000L);
        kryo.writeObject(output, someString);
        kryo.writeObject(output, someDate);
        output.close();
        String readString = kryo.readObject(input, String.class);
        Date readDate = kryo.readObject(input, Date.class);
        input.close();
        Assert.assertEquals(readString, "Multiple Objects");
        Assert.assertEquals(readDate.getTime(), 915170400000L);
    }

    @Test
    public void givenPerson_whenSerializing_thenReadCorrectly() {
        Person person = new Person();
        kryo.writeObject(output, person);
        output.close();
        Person readPerson = kryo.readObject(input, Person.class);
        input.close();
        Assert.assertEquals(readPerson.getName(), "John Doe");
    }

    @Test
    public void givenPerson_whenUsingCustomSerializer_thenReadCorrectly() {
        Person person = new Person();
        person.setAge(0);
        kryo.register(Person.class, new PersonSerializer());
        kryo.writeObject(output, person);
        output.close();
        Person readPerson = kryo.readObject(input, Person.class);
        input.close();
        Assert.assertEquals(readPerson.getName(), "John Doe");
        Assert.assertEquals(readPerson.getAge(), 18);
    }

    @Test
    public void givenPerson_whenCustomSerialization_thenReadCorrectly() {
        Person person = new Person();
        kryo.writeObject(output, person);
        output.close();
        Person readPerson = kryo.readObject(input, Person.class);
        input.close();
        Assert.assertEquals(readPerson.getName(), "John Doe");
        Assert.assertEquals(readPerson.getAge(), 18);
    }

    @Test
    public void givenJavaSerializable_whenSerializing_thenReadCorrectly() {
        ComplexClass complexClass = new ComplexClass();
        kryo.register(ComplexClass.class, new JavaSerializer());
        kryo.writeObject(output, complexClass);
        output.close();
        ComplexClass readComplexObject = kryo.readObject(input, ComplexClass.class);
        input.close();
        Assert.assertEquals(readComplexObject.getName(), "Bael");
    }
}

