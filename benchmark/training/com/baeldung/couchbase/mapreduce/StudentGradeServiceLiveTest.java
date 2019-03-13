package com.baeldung.couchbase.mapreduce;


import com.couchbase.client.java.document.JsonDocument;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StudentGradeServiceLiveTest {
    private static final Logger logger = LoggerFactory.getLogger(StudentGradeServiceLiveTest.class);

    static StudentGradeService studentGradeService;

    static Set<String> gradeIds = new HashSet<>();

    @Test
    public final void whenFindAll_thenSuccess() {
        List<JsonDocument> docs = StudentGradeServiceLiveTest.studentGradeService.findAll();
        printDocuments(docs);
    }

    @Test
    public final void whenFindByCourse_thenSuccess() {
        List<JsonDocument> docs = StudentGradeServiceLiveTest.studentGradeService.findByCourse("History");
        printDocuments(docs);
    }

    @Test
    public final void whenFindByCourses_thenSuccess() {
        List<JsonDocument> docs = StudentGradeServiceLiveTest.studentGradeService.findByCourses("History", "Science");
        printDocuments(docs);
    }

    @Test
    public final void whenFindByGradeInRange_thenSuccess() {
        List<JsonDocument> docs = StudentGradeServiceLiveTest.studentGradeService.findByGradeInRange(80, 89, true);
        printDocuments(docs);
    }

    @Test
    public final void whenFindByGradeLessThan_thenSuccess() {
        List<JsonDocument> docs = StudentGradeServiceLiveTest.studentGradeService.findByGradeLessThan(60);
        printDocuments(docs);
    }

    @Test
    public final void whenFindByGradeGreaterThan_thenSuccess() {
        List<JsonDocument> docs = StudentGradeServiceLiveTest.studentGradeService.findByGradeGreaterThan(90);
        printDocuments(docs);
    }

    @Test
    public final void whenFindByCourseAndGradeInRange_thenSuccess() {
        List<JsonDocument> docs = StudentGradeServiceLiveTest.studentGradeService.findByCourseAndGradeInRange("Math", 80, 89, true);
        printDocuments(docs);
    }

    @Test
    public final void whenFindTopGradesByCourse_thenSuccess() {
        List<JsonDocument> docs = StudentGradeServiceLiveTest.studentGradeService.findTopGradesByCourse("Science", 2);
        printDocuments(docs);
    }

    @Test
    public final void whenCountStudentsByCourse_thenSuccess() {
        Map<String, Long> map = StudentGradeServiceLiveTest.studentGradeService.countStudentsByCourse();
        printMap(map);
    }

    @Test
    public final void whenSumCreditHoursByStudent_thenSuccess() {
        Map<String, Long> map = StudentGradeServiceLiveTest.studentGradeService.sumCreditHoursByStudent();
        printMap(map);
    }

    @Test
    public final void whenSumGradePointsByStudent_thenSuccess() {
        Map<String, Long> map = StudentGradeServiceLiveTest.studentGradeService.sumGradePointsByStudent();
        printMap(map);
    }

    @Test
    public final void whenCalculateGpaByStudent_thenSuccess() {
        Map<String, Float> map = StudentGradeServiceLiveTest.studentGradeService.calculateGpaByStudent();
        printGpaMap(map);
    }
}

