package com.querydsl.apt.domain;


import com.querydsl.core.annotations.QueryProjection;
import org.junit.Test;


public class Array2Test {
    public static class Example {
        byte[] imageData;

        @QueryProjection
        public Example(byte[] param0) {
            this.imageData = param0;
        }
    }

    @Test
    public void test() {
        newInstance(new byte[0]);
    }
}

