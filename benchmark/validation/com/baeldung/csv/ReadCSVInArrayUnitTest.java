package com.baeldung.csv;


import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import org.junit.Assert;
import org.junit.Test;


public class ReadCSVInArrayUnitTest {
    public static final String COMMA_DELIMITER = ",";

    public static final String CSV_FILE = "src/test/resources/book.csv";

    public static final List<List<String>> EXPECTED_ARRAY = Collections.unmodifiableList(new ArrayList<List<String>>() {
        {
            add(new ArrayList<String>() {
                {
                    add("Mary Kom");
                    add("Unbreakable");
                }
            });
            add(new ArrayList<String>() {
                {
                    add("Kapil Isapuari");
                    add("Farishta");
                }
            });
        }
    });

    @Test
    public void givenCSVFile_whenBufferedReader_thenContentsAsExpected() throws IOException {
        List<List<String>> records = new ArrayList<List<String>>();
        try (BufferedReader br = new BufferedReader(new FileReader(ReadCSVInArrayUnitTest.CSV_FILE))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] values = line.split(ReadCSVInArrayUnitTest.COMMA_DELIMITER);
                records.add(Arrays.asList(values));
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < (ReadCSVInArrayUnitTest.EXPECTED_ARRAY.size()); i++) {
            Assert.assertArrayEquals(ReadCSVInArrayUnitTest.EXPECTED_ARRAY.get(i).toArray(), records.get(i).toArray());
        }
    }

    @Test
    public void givenCSVFile_whenScanner_thenContentsAsExpected() throws IOException {
        List<List<String>> records = new ArrayList<List<String>>();
        try (Scanner scanner = new Scanner(new File(ReadCSVInArrayUnitTest.CSV_FILE))) {
            while (scanner.hasNextLine()) {
                records.add(getRecordFromLine(scanner.nextLine()));
            } 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < (ReadCSVInArrayUnitTest.EXPECTED_ARRAY.size()); i++) {
            Assert.assertArrayEquals(ReadCSVInArrayUnitTest.EXPECTED_ARRAY.get(i).toArray(), records.get(i).toArray());
        }
    }

    @Test
    public void givenCSVFile_whenOpencsv_thenContentsAsExpected() throws IOException {
        List<List<String>> records = new ArrayList<List<String>>();
        try (CSVReader csvReader = new CSVReader(new FileReader(ReadCSVInArrayUnitTest.CSV_FILE))) {
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < (ReadCSVInArrayUnitTest.EXPECTED_ARRAY.size()); i++) {
            Assert.assertArrayEquals(ReadCSVInArrayUnitTest.EXPECTED_ARRAY.get(i).toArray(), records.get(i).toArray());
        }
    }
}

