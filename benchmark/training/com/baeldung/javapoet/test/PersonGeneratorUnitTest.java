package com.baeldung.javapoet.test;


import com.baeldung.javapoet.PersonGenerator;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class PersonGeneratorUnitTest {
    private PersonGenerator generator;

    private Path generatedFolderPath;

    private Path expectedFolderPath;

    @Test
    public void whenGenerateGenderEnum_thenGenerateGenderEnumAndWriteToFile() throws IOException {
        generator.generateGenderEnum();
        String fileName = "Gender.java";
        assertThatFileIsGeneratedAsExpected(fileName);
        deleteGeneratedFile(fileName);
    }

    @Test
    public void whenGeneratePersonInterface_thenGeneratePersonInterfaceAndWriteToFile() throws IOException {
        generator.generatePersonInterface();
        String fileName = "Person.java";
        assertThatFileIsGeneratedAsExpected(fileName);
        deleteGeneratedFile(fileName);
    }

    @Test
    public void whenGenerateStudentClass_thenGenerateStudentClassAndWriteToFile() throws IOException {
        generator.generateStudentClass();
        String fileName = "Student.java";
        assertThatFileIsGeneratedAsExpected(fileName);
        deleteGeneratedFile(fileName);
    }
}

