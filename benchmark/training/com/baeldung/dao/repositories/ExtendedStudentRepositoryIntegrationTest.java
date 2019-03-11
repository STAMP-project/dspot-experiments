package com.baeldung.dao.repositories;


import com.baeldung.config.PersistenceConfiguration;
import com.baeldung.domain.Student;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { PersistenceConfiguration.class })
@DirtiesContext
public class ExtendedStudentRepositoryIntegrationTest {
    @Resource
    private ExtendedStudentRepository extendedStudentRepository;

    @Test
    public void givenStudents_whenFindByName_thenGetOk() {
        List<Student> students = extendedStudentRepository.findByAttributeContainsText("name", "john");
        assertThat(students.size()).isEqualTo(2);
    }
}

