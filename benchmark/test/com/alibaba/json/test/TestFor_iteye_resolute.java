package com.alibaba.json.test;


import java.io.Serializable;
import junit.framework.TestCase;


public class TestFor_iteye_resolute extends TestCase {
    private static final int SIZE = 1000;

    private static final int LOOP_COUNT = 1000 * 10;

    public void test_perf() {
        for (int i = 0; i < 10; ++i) {
            json();
            javaSer();
            System.out.println();
        }
    }

    public static class User implements Serializable {
        private static final long serialVersionUID = 1L;

        private int id;

        private String name;

        public User(int id) {
            super();
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

