package com.alibaba.json.bvt.dubbo;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.json.test.dubbo.FullAddress;
import com.alibaba.json.test.dubbo.HelloServiceImpl;
import com.alibaba.json.test.dubbo.Person;
import com.alibaba.json.test.dubbo.PersonInfo;
import com.alibaba.json.test.dubbo.PersonStatus;
import com.alibaba.json.test.dubbo.Phone;
import java.util.ArrayList;
import junit.framework.TestCase;


public class TestForDubbo extends TestCase {
    static Person person;

    static {
        TestForDubbo.person = new Person();
        TestForDubbo.person.setPersonId("superman111");
        TestForDubbo.person.setLoginName("superman");
        TestForDubbo.person.setEmail("sm@1.com");
        TestForDubbo.person.setPenName("pname");
        TestForDubbo.person.setStatus(PersonStatus.ENABLED);
        ArrayList<Phone> phones = new ArrayList<Phone>();
        Phone phone1 = new Phone("86", "0571", "87654321", "001");
        Phone phone2 = new Phone("86", "0571", "87654322", "002");
        phones.add(phone1);
        phones.add(phone2);
        PersonInfo pi = new PersonInfo();
        pi.setPhones(phones);
        Phone fax = new Phone("86", "0571", "87654321", null);
        pi.setFax(fax);
        FullAddress addr = new FullAddress("CN", "zj", "3480", "wensanlu", "315000");
        pi.setFullAddress(addr);
        pi.setMobileNo("13584652131");
        pi.setMale(true);
        pi.setDepartment("b2b");
        pi.setHomepageUrl("www.capcom.com");
        pi.setJobTitle("qa");
        pi.setName("superman");
        TestForDubbo.person.setInfoProfile(pi);
    }

    private HelloServiceImpl helloService = new HelloServiceImpl();

    public void testPerson() {
        Person p = helloService.showPerson(TestForDubbo.person);
        String text = JSON.toJSONString(p, WriteClassName);
        System.out.println(text);
        Person result = JSON.parseObject(text, Person.class);
        TestCase.assertEquals(result.getInfoProfile().getPhones().get(0).getArea(), TestForDubbo.person.getInfoProfile().getPhones().get(0).getArea());
        TestCase.assertEquals(result.getInfoProfile().getPhones().get(0).getCountry(), TestForDubbo.person.getInfoProfile().getPhones().get(0).getCountry());
        TestCase.assertEquals(result.getInfoProfile().getPhones().get(0).getExtensionNumber(), TestForDubbo.person.getInfoProfile().getPhones().get(0).getExtensionNumber());
        TestCase.assertEquals(result.getInfoProfile().getPhones().get(0).getNumber(), TestForDubbo.person.getInfoProfile().getPhones().get(0).getNumber());
    }
}

