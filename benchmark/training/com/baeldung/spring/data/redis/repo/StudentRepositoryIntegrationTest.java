package com.baeldung.spring.data.redis.repo;


import Student.Gender;
import com.baeldung.spring.data.redis.config.RedisConfig;
import com.baeldung.spring.data.redis.model.Student;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.embedded.RedisServer;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RedisConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class StudentRepositoryIntegrationTest {
    @Autowired
    private StudentRepository studentRepository;

    private static RedisServer redisServer;

    @Test
    public void whenSavingStudent_thenAvailableOnRetrieval() throws Exception {
        final Student student = new Student("Eng2015001", "John Doe", Gender.MALE, 1);
        studentRepository.save(student);
        final Student retrievedStudent = studentRepository.findById(student.getId()).get();
        Assert.assertEquals(student.getId(), retrievedStudent.getId());
    }

    @Test
    public void whenUpdatingStudent_thenAvailableOnRetrieval() throws Exception {
        final Student student = new Student("Eng2015001", "John Doe", Gender.MALE, 1);
        studentRepository.save(student);
        student.setName("Richard Watson");
        studentRepository.save(student);
        final Student retrievedStudent = studentRepository.findById(student.getId()).get();
        Assert.assertEquals(student.getName(), retrievedStudent.getName());
    }

    @Test
    public void whenSavingStudents_thenAllShouldAvailableOnRetrieval() throws Exception {
        final Student engStudent = new Student("Eng2015001", "John Doe", Gender.MALE, 1);
        final Student medStudent = new Student("Med2015001", "Gareth Houston", Gender.MALE, 2);
        studentRepository.save(engStudent);
        studentRepository.save(medStudent);
        List<Student> students = new ArrayList<>();
        studentRepository.findAll().forEach(students::add);
        Assert.assertEquals(students.size(), 2);
    }

    @Test
    public void whenDeletingStudent_thenNotAvailableOnRetrieval() throws Exception {
        final Student student = new Student("Eng2015001", "John Doe", Gender.MALE, 1);
        studentRepository.save(student);
        studentRepository.deleteById(student.getId());
        final Student retrievedStudent = studentRepository.findById(student.getId()).orElse(null);
        Assert.assertNull(retrievedStudent);
    }
}

