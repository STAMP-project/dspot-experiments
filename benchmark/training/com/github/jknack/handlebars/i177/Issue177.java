package com.github.jknack.handlebars.i177;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;


public class Issue177 extends AbstractTest {
    // model classes
    public class Model1 {
        private List<String> listOfValues1 = new ArrayList<>();

        public List<String> getListOfValues1() {
            return listOfValues1;
        }

        public void setListOfValues1(final List<String> listOfValues1) {
            this.listOfValues1 = listOfValues1;
        }
    }

    public class User {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public class Model2 {
        private List<Issue177.User> users = new ArrayList<>();

        public List<Issue177.User> getUsers() {
            return users;
        }

        public void setUsers(final List<Issue177.User> users) {
            this.users = users;
        }
    }

    // model map
    Map<String, Object> modelMap;

    @Test
    public void test1() throws IOException {
        shouldCompileTo("{{model1.listOfValues1.[0]}}", modelMap, "m1-1");
    }

    @Test
    public void test2() throws IOException {
        shouldCompileTo("{{model1.listOfValues1.[0]}}\n{{model2.users.[0]}}", modelMap, "m1-1\nUser 1");
    }

    @Test
    public void test3() throws IOException {
        shouldCompileTo("{{model1.listOfValues1.[0]}}\n{{model2.users.[0].name}}", modelMap, "m1-1\nUser 1");
    }

    @Test
    public void test3a() throws IOException {
        shouldCompileTo("{{model2.users.[0].name}}", modelMap, "User 1");
    }

    @Test
    public void test4() throws IOException {
        shouldCompileTo("{{model1.listOfValues1.[0]}}\n{{#if model3}}\n{{model2.users.[0].name}}\n{{/if}}", modelMap, "m1-1\n\nUser 1\n");
    }
}

