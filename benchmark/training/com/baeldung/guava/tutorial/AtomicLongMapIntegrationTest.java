package com.baeldung.guava.tutorial;


import com.google.common.util.concurrent.AtomicLongMap;
import org.junit.Assert;
import org.junit.Test;


public class AtomicLongMapIntegrationTest {
    private static final String SPRING_COURSE_KEY = "Spring";

    private static final String HIBERNATE_COURSE_KEY = "hibernate";

    private static final String GUAVA_COURSE_KEY = "Guava";

    AtomicLongMap<String> courses = AtomicLongMap.create();

    @Test
    public void accumulateAndGet_withLongBinaryOperator_thenSuccessful() {
        long noOfStudents = 56;
        long oldValue = courses.get(AtomicLongMapIntegrationTest.SPRING_COURSE_KEY);
        long totalNotesRequired = courses.accumulateAndGet("Guava", noOfStudents, ( x, y) -> x * y);
        Assert.assertEquals(totalNotesRequired, (oldValue * noOfStudents));
    }

    @Test
    public void getAndAccumulate_withLongBinaryOperator_thenSuccessful() {
        long noOfStudents = 56;
        long beforeUpdate = courses.get(AtomicLongMapIntegrationTest.SPRING_COURSE_KEY);
        long onUpdate = courses.accumulateAndGet("Guava", noOfStudents, ( x, y) -> x * y);
        long afterUpdate = courses.get(AtomicLongMapIntegrationTest.SPRING_COURSE_KEY);
        Assert.assertEquals(onUpdate, afterUpdate);
        Assert.assertEquals(afterUpdate, (beforeUpdate * noOfStudents));
    }

    @Test
    public void updateAndGet_withLongUnaryOperator_thenSuccessful() {
        long beforeUpdate = courses.get(AtomicLongMapIntegrationTest.SPRING_COURSE_KEY);
        long onUpdate = courses.updateAndGet("Guava", ( x) -> x / 2);
        long afterUpdate = courses.get(AtomicLongMapIntegrationTest.SPRING_COURSE_KEY);
        Assert.assertEquals(onUpdate, afterUpdate);
        Assert.assertEquals(afterUpdate, (beforeUpdate / 2));
    }
}

