package com.baeldung;


import com.baeldung.repository.StudentRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { SpringJenkinsPipelineApplication.class, TestMongoConfig.class })
public class SomeIntegrationTest {
    @Autowired
    private StudentRepository studentRepository;

    @Test
    public void whenInserting_andCount_thenWeDontGetZero() {
        long count = studentRepository.count();
        Assert.assertNotEquals(0, count);
    }
}

