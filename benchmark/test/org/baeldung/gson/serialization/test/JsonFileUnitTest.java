package org.baeldung.gson.serialization.test;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class JsonFileUnitTest {
    @Parameterized.Parameter
    public Object object;

    @Test
    public void givenProperData_whenStoredInFile_shouldSaveJsonSuccessfully() {
        String filePath = "target/output.json";
        try (Writer writer = new FileWriter(filePath)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(object, writer);
            Assert.assertTrue(Files.exists(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

