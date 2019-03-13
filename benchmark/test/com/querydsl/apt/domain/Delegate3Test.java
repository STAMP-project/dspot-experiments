package com.querydsl.apt.domain;


import QDelegate3Test_Geometry.geometry;
import QDelegate3Test_Point.point;
import QDelegate3Test_Polygon.polygon;
import org.junit.Test;


public class Delegate3Test {
    public static class Geometry implements Comparable<Delegate3Test.Geometry> {
        @Override
        public int compareTo(Delegate3Test.Geometry o) {
            return 0;
        }
    }

    public static class Point extends Delegate3Test.Geometry {}

    public static class Polygon extends Delegate3Test.Geometry {}

    @Test
    public void test() {
        geometry.isWithin(null);
        point.isWithin(null);
        polygon.isWithin(null);
    }
}

