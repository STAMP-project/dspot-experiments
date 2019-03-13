package com.baeldung.protobuf;


import AddressBookProtos.AddressBook;
import AddressBookProtos.Person;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class ProtobufUnitTest {
    private final String filePath = "address_book";

    @Test
    public void givenGeneratedProtobufClass_whenCreateClass_thenShouldCreateJavaInstance() {
        // when
        String email = "j@baeldung.com";
        int id = new Random().nextInt();
        String name = "Michael Program";
        String number = "01234567890";
        AddressBookProtos.Person person = Person.newBuilder().setId(id).setName(name).setEmail(email).addNumbers(number).build();
        // then
        Assert.assertEquals(person.getEmail(), email);
        Assert.assertEquals(person.getId(), id);
        Assert.assertEquals(person.getName(), name);
        Assert.assertEquals(person.getNumbers(0), number);
    }

    @Test
    public void givenAddressBookWithOnePerson_whenSaveAsAFile_shouldLoadFromFileToJavaClass() throws IOException {
        // given
        String email = "j@baeldung.com";
        int id = new Random().nextInt();
        String name = "Michael Program";
        String number = "01234567890";
        AddressBookProtos.Person person = Person.newBuilder().setId(id).setName(name).setEmail(email).addNumbers(number).build();
        // when
        AddressBookProtos.AddressBook addressBook = AddressBook.newBuilder().addPeople(person).build();
        FileOutputStream fos = new FileOutputStream(filePath);
        addressBook.writeTo(fos);
        fos.close();
        // then
        FileInputStream fis = new FileInputStream(filePath);
        AddressBookProtos.AddressBook deserialized = AddressBook.newBuilder().mergeFrom(fis).build();
        fis.close();
        Assert.assertEquals(deserialized.getPeople(0).getEmail(), email);
        Assert.assertEquals(deserialized.getPeople(0).getId(), id);
        Assert.assertEquals(deserialized.getPeople(0).getName(), name);
        Assert.assertEquals(deserialized.getPeople(0).getNumbers(0), number);
    }
}

