package com.alibaba.json.bvt.bug;


import Feature.SupportArrayToBean;
import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


public class Bug_for_issue_630 extends TestCase {
    public void test_for_issue_null() throws Exception {
        Bug_for_issue_630.Model model = new Bug_for_issue_630.Model();
        model.id = 123;
        model.name = null;
        model.modelName = null;
        model.isFlay = false;
        // model.persons = new ArrayList<Person>();
        // model.persons.add(new Person());
        String str = JSON.toJSONString(model, BeanToArray);
        // System.out.println(str);
        JSON.parseObject(str, Bug_for_issue_630.Model.class, SupportArrayToBean);
    }

    public void test_for_issue_empty() throws Exception {
        Bug_for_issue_630.Model model = new Bug_for_issue_630.Model();
        model.id = 123;
        model.name = null;
        model.modelName = null;
        model.isFlay = false;
        model.persons = new ArrayList<Bug_for_issue_630.Person>();
        // model.persons.add(new Person());
        String str = JSON.toJSONString(model, BeanToArray);
        // System.out.println(str);
        JSON.parseObject(str, Bug_for_issue_630.Model.class, SupportArrayToBean);
    }

    public void test_for_issue_one() throws Exception {
        Bug_for_issue_630.Model model = new Bug_for_issue_630.Model();
        model.id = 123;
        model.name = null;
        model.modelName = null;
        model.isFlay = false;
        model.persons = new ArrayList<Bug_for_issue_630.Person>();
        model.persons.add(new Bug_for_issue_630.Person());
        String str = JSON.toJSONString(model, BeanToArray);
        // System.out.println(str);
        JSON.parseObject(str, Bug_for_issue_630.Model.class, SupportArrayToBean);
    }

    public static class Model {
        public int id;

        public String name;

        public String modelName;

        public boolean isFlay;

        public List<Bug_for_issue_630.Person> persons;// = new ArrayList<Person>();

    }

    public static class Person {
        public int id;

        public String name;
    }
}

