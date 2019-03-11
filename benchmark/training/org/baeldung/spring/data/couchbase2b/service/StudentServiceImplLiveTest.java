package org.baeldung.spring.data.couchbase2b.service;


import com.couchbase.client.java.document.json.JsonObject;
import java.util.List;
import javax.validation.ConstraintViolationException;
import org.baeldung.spring.data.couchbase.model.Student;
import org.baeldung.spring.data.couchbase2b.MultiBucketLiveTest;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class StudentServiceImplLiveTest extends MultiBucketLiveTest {
    static final String typeField = "_class";

    static final String joe = "Joe";

    static final String college = "College";

    static final String joeCollegeId = (("student:" + (StudentServiceImplLiveTest.joe)) + ":") + (StudentServiceImplLiveTest.college);

    static final DateTime joeCollegeDob = DateTime.now().minusYears(21);

    static final Student joeCollege = new Student(StudentServiceImplLiveTest.joeCollegeId, StudentServiceImplLiveTest.joe, StudentServiceImplLiveTest.college, StudentServiceImplLiveTest.joeCollegeDob);

    static final JsonObject jsonJoeCollege = JsonObject.empty().put(StudentServiceImplLiveTest.typeField, Student.class.getName()).put("firstName", StudentServiceImplLiveTest.joe).put("lastName", StudentServiceImplLiveTest.college).put("created", DateTime.now().getMillis()).put("version", 1);

    static final String judy = "Judy";

    static final String jetson = "Jetson";

    static final String judyJetsonId = (("student:" + (StudentServiceImplLiveTest.judy)) + ":") + (StudentServiceImplLiveTest.jetson);

    static final DateTime judyJetsonDob = DateTime.now().minusYears(19).minusMonths(5).minusDays(3);

    static final Student judyJetson = new Student(StudentServiceImplLiveTest.judyJetsonId, StudentServiceImplLiveTest.judy, StudentServiceImplLiveTest.jetson, StudentServiceImplLiveTest.judyJetsonDob);

    static final JsonObject jsonJudyJetson = JsonObject.empty().put(StudentServiceImplLiveTest.typeField, Student.class.getName()).put("firstName", StudentServiceImplLiveTest.judy).put("lastName", StudentServiceImplLiveTest.jetson).put("created", DateTime.now().getMillis()).put("version", 1);

    @Autowired
    StudentServiceImpl studentService;

    @Test
    public void whenCreatingStudent_thenDocumentIsPersisted() {
        String firstName = "Eric";
        String lastName = "Stratton";
        DateTime dateOfBirth = DateTime.now().minusYears(25);
        String id = (("student:" + firstName) + ":") + lastName;
        Student expectedStudent = new Student(id, firstName, lastName, dateOfBirth);
        studentService.create(expectedStudent);
        Student actualStudent = studentService.findOne(id);
        Assert.assertNotNull(actualStudent.getCreated());
        Assert.assertNotNull(actualStudent);
        Assert.assertEquals(expectedStudent.getId(), actualStudent.getId());
    }

    @Test(expected = ConstraintViolationException.class)
    public void whenCreatingStudentWithInvalidFirstName_thenConstraintViolationException() {
        String firstName = "Er+ic";
        String lastName = "Stratton";
        DateTime dateOfBirth = DateTime.now().minusYears(25);
        String id = (("student:" + firstName) + ":") + lastName;
        Student student = new Student(id, firstName, lastName, dateOfBirth);
        studentService.create(student);
    }

    @Test(expected = ConstraintViolationException.class)
    public void whenCreatingStudentWithFutureDob_thenConstraintViolationException() {
        String firstName = "Jane";
        String lastName = "Doe";
        DateTime dateOfBirth = DateTime.now().plusDays(1);
        String id = (("student:" + firstName) + ":") + lastName;
        Student student = new Student(id, firstName, lastName, dateOfBirth);
        studentService.create(student);
    }

    @Test
    public void whenFindingStudentByJohnSmithId_thenReturnsJohnSmith() {
        Student actualStudent = studentService.findOne(StudentServiceImplLiveTest.joeCollegeId);
        Assert.assertNotNull(actualStudent);
        Assert.assertNotNull(actualStudent.getCreated());
        Assert.assertEquals(StudentServiceImplLiveTest.joeCollegeId, actualStudent.getId());
    }

    @Test
    public void whenFindingAllStudents_thenReturnsTwoOrMoreStudentsIncludingJoeCollegeAndJudyJetson() {
        List<Student> resultList = studentService.findAll();
        Assert.assertNotNull(resultList);
        Assert.assertFalse(resultList.isEmpty());
        Assert.assertTrue(resultContains(resultList, StudentServiceImplLiveTest.joeCollege));
        Assert.assertTrue(resultContains(resultList, StudentServiceImplLiveTest.judyJetson));
        Assert.assertTrue(((resultList.size()) >= 2));
    }

    @Test
    public void whenFindingByFirstNameJohn_thenReturnsOnlyStudentsNamedJohn() {
        String expectedFirstName = StudentServiceImplLiveTest.joe;
        List<Student> resultList = studentService.findByFirstName(expectedFirstName);
        Assert.assertNotNull(resultList);
        Assert.assertFalse(resultList.isEmpty());
        Assert.assertTrue(allResultsContainExpectedFirstName(resultList, expectedFirstName));
    }

    @Test
    public void whenFindingByLastNameSmith_thenReturnsOnlyStudentsNamedSmith() {
        String expectedLastName = StudentServiceImplLiveTest.college;
        List<Student> resultList = studentService.findByLastName(expectedLastName);
        Assert.assertNotNull(resultList);
        Assert.assertFalse(resultList.isEmpty());
        Assert.assertTrue(allResultsContainExpectedLastName(resultList, expectedLastName));
    }
}

