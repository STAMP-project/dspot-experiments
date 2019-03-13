package com.baeldung.snakeyaml;


import Tag.MAP;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;


public class JavaToYAMLSerializationUnitTest {
    @Test
    public void whenDumpMap_thenGenerateCorrectYAML() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("name", "Silenthand Olleander");
        data.put("race", "Human");
        data.put("traits", new String[]{ "ONE_HAND", "ONE_EYE" });
        Yaml yaml = new Yaml();
        StringWriter writer = new StringWriter();
        yaml.dump(data, writer);
        String expectedYaml = "name: Silenthand Olleander\nrace: Human\ntraits: [ONE_HAND, ONE_EYE]\n";
        Assert.assertEquals(expectedYaml, writer.toString());
    }

    @Test
    public void whenDumpACustomType_thenGenerateCorrectYAML() {
        Customer customer = new Customer();
        customer.setAge(45);
        customer.setFirstName("Greg");
        customer.setLastName("McDowell");
        Yaml yaml = new Yaml();
        StringWriter writer = new StringWriter();
        yaml.dump(customer, writer);
        String expectedYaml = "!!com.baeldung.snakeyaml.Customer {age: 45, contactDetails: null, firstName: Greg,\n  homeAddress: null, lastName: McDowell}\n";
        Assert.assertEquals(expectedYaml, writer.toString());
    }

    @Test
    public void whenDumpAsCustomType_thenGenerateCorrectYAML() {
        Customer customer = new Customer();
        customer.setAge(45);
        customer.setFirstName("Greg");
        customer.setLastName("McDowell");
        Yaml yaml = new Yaml();
        String expectedYaml = "{age: 45, contactDetails: null, firstName: Greg, homeAddress: null, lastName: McDowell}\n";
        Assert.assertEquals(expectedYaml, yaml.dumpAs(customer, MAP, null));
    }
}

