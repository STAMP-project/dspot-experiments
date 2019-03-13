package com.baeldung.jackson.xml;


import com.baeldung.jackson.dtos.Address;
import com.baeldung.jackson.dtos.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;


public class XMLSerializeDeserializeUnitTest {
    @Test
    public void whenJavaSerializedToXmlStr_thenCorrect() throws JsonProcessingException {
        XmlMapper xmlMapper = new XmlMapper();
        String xml = xmlMapper.writeValueAsString(new SimpleBean());
        Assert.assertNotNull(xml);
    }

    @Test
    public void whenJavaSerializedToXmlFile_thenCorrect() throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.writeValue(new File("target/simple_bean.xml"), new SimpleBean());
        File file = new File("target/simple_bean.xml");
        Assert.assertNotNull(file);
    }

    @Test
    public void whenJavaGotFromXmlStr_thenCorrect() throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        SimpleBean value = xmlMapper.readValue("<SimpleBean><x>1</x><y>2</y></SimpleBean>", SimpleBean.class);
        Assert.assertTrue((((value.getX()) == 1) && ((value.getY()) == 2)));
    }

    @Test
    public void whenJavaGotFromXmlFile_thenCorrect() throws IOException {
        File file = new File("src/test/resources/simple_bean.xml");
        XmlMapper xmlMapper = new XmlMapper();
        String xml = XMLSerializeDeserializeUnitTest.inputStreamToString(new FileInputStream(file));
        SimpleBean value = xmlMapper.readValue(xml, SimpleBean.class);
        Assert.assertTrue((((value.getX()) == 1) && ((value.getY()) == 2)));
    }

    @Test
    public void whenJavaGotFromXmlStrWithCapitalElem_thenCorrect() throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        SimpleBeanForCapitalizedFields value = xmlMapper.readValue("<SimpleBeanForCapitalizedFields><X>1</X><y>2</y></SimpleBeanForCapitalizedFields>", SimpleBeanForCapitalizedFields.class);
        Assert.assertTrue((((value.getX()) == 1) && ((value.getY()) == 2)));
    }

    @Test
    public void whenJavaSerializedToXmlFileWithCapitalizedField_thenCorrect() throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.writeValue(new File("target/simple_bean_capitalized.xml"), new SimpleBeanForCapitalizedFields());
        File file = new File("target/simple_bean_capitalized.xml");
        Assert.assertNotNull(file);
    }

    @Test
    public void whenJavaDeserializedFromXmlFile_thenCorrect() throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        String xml = "<person><firstName>Rohan</firstName><lastName>Daye</lastName><phoneNumbers><phoneNumbers>9911034731</phoneNumbers><phoneNumbers>9911033478</phoneNumbers></phoneNumbers><address><address><streetNumber>1</streetNumber><streetName>Name1</streetName><city>City1</city></address><address><streetNumber>2</streetNumber><streetName>Name2</streetName><city>City2</city></address></address></person>";
        Person value = xmlMapper.readValue(xml, Person.class);
        Assert.assertTrue(((value.getAddress().get(0).getCity().equalsIgnoreCase("city1")) && (value.getAddress().get(1).getCity().equalsIgnoreCase("city2"))));
    }

    @Test
    public void whenJavaSerializedToXmlFile_thenSuccess() throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        String expectedXml = "<person><firstName>Rohan</firstName><lastName>Daye</lastName><phoneNumbers><phoneNumbers>9911034731</phoneNumbers><phoneNumbers>9911033478</phoneNumbers></phoneNumbers><address><address><streetNumber>1</streetNumber><streetName>Name1</streetName><city>City1</city></address><address><streetNumber>2</streetNumber><streetName>Name2</streetName><city>City2</city></address></address></person>";
        Person person = new Person();
        person.setFirstName("Rohan");
        person.setLastName("Daye");
        List<String> ph = new ArrayList<>();
        ph.add("9911034731");
        ph.add("9911033478");
        person.setPhoneNumbers(ph);
        List<Address> addresses = new ArrayList<>();
        Address address1 = new Address();
        address1.setStreetNumber("1");
        address1.setStreetName("Name1");
        address1.setCity("City1");
        Address address2 = new Address();
        address2.setStreetNumber("2");
        address2.setStreetName("Name2");
        address2.setCity("City2");
        addresses.add(address1);
        addresses.add(address2);
        person.setAddress(addresses);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        xmlMapper.writeValue(byteArrayOutputStream, person);
        Assertions.assertEquals(expectedXml, byteArrayOutputStream.toString());
    }
}

