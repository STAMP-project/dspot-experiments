package org.baeldung.java.io;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Scanner;
import org.junit.Assert;
import org.junit.Test;


public class JavaScannerUnitTest {
    @Test
    public void whenReadFileWithScanner_thenCorrect() throws IOException {
        final Scanner scanner = new Scanner(new File("src/test/resources/test_read.in"));
        Assert.assertTrue(scanner.hasNext());
        Assert.assertEquals("Hello", scanner.next());
        Assert.assertEquals("world", scanner.next());
        scanner.close();
    }

    @Test
    public void whenConvertInputStreamToString_thenConverted() throws IOException {
        final String expectedValue = "Hello world";
        final FileInputStream inputStream = new FileInputStream("src/test/resources/test_read.in");
        final Scanner scanner = new Scanner(inputStream);
        scanner.useDelimiter("\\A");
        final String result = scanner.next();
        Assert.assertEquals(expectedValue, result);
        scanner.close();
    }

    @Test
    public void whenReadUsingBufferedReader_thenCorrect() throws IOException {
        final String firstLine = "Hello world";
        final String secondLine = "Hi, John";
        final BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/test_read_multiple.in"));
        String result = reader.readLine();
        Assert.assertEquals(firstLine, result);
        result = reader.readLine();
        Assert.assertEquals(secondLine, result);
        reader.close();
    }

    @Test
    public void whenReadUsingScanner_thenCorrect() throws IOException {
        final String firstLine = "Hello world";
        final FileInputStream inputStream = new FileInputStream("src/test/resources/test_read_multiple.in");
        final Scanner scanner = new Scanner(inputStream);
        final String result = scanner.nextLine();
        Assert.assertEquals(firstLine, result);
        scanner.useDelimiter(", ");
        Assert.assertEquals("Hi", scanner.next());
        Assert.assertEquals("John", scanner.next());
        scanner.close();
    }

    @Test
    public void whenReadingInputFromConsole_thenCorrect() {
        final String input = "Hello";
        final InputStream stdin = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        final Scanner scanner = new Scanner(System.in);
        final String result = scanner.next();
        Assert.assertEquals(input, result);
        System.setIn(stdin);
        scanner.close();
    }

    @Test
    public void whenValidateInputUsingScanner_thenValidated() throws IOException {
        final String input = "2000";
        final InputStream stdin = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        final Scanner scanner = new Scanner(System.in);
        final boolean isIntInput = scanner.hasNextInt();
        Assert.assertTrue(isIntInput);
        System.setIn(stdin);
        scanner.close();
    }

    @Test
    public void whenScanString_thenCorrect() throws IOException {
        final String input = "Hello 1 F 3.5";
        final Scanner scanner = new Scanner(input);
        scanner.useLocale(Locale.US);
        Assert.assertEquals("Hello", scanner.next());
        Assert.assertEquals(1, scanner.nextInt());
        Assert.assertEquals(15, scanner.nextInt(16));
        Assert.assertEquals(3.5, scanner.nextDouble(), 1.0E-8);
        scanner.close();
    }

    @Test
    public void whenFindPatternUsingScanner_thenFound() throws IOException {
        final String expectedValue = "world";
        final FileInputStream inputStream = new FileInputStream("src/test/resources/test_read.in");
        final Scanner scanner = new Scanner(inputStream);
        final String result = scanner.findInLine("wo..d");
        Assert.assertEquals(expectedValue, result);
        scanner.close();
    }

    @Test
    public void whenFindPatternInHorizon_thenFound() throws IOException {
        final String expectedValue = "world";
        final FileInputStream inputStream = new FileInputStream("src/test/resources/test_read.in");
        final Scanner scanner = new Scanner(inputStream);
        String result = scanner.findWithinHorizon("wo..d", 5);
        Assert.assertNull(result);
        result = scanner.findWithinHorizon("wo..d", 100);
        Assert.assertEquals(expectedValue, result);
        scanner.close();
    }

    @Test
    public void whenSkipPatternUsingScanner_thenSkiped() throws IOException {
        final FileInputStream inputStream = new FileInputStream("src/test/resources/test_read.in");
        final Scanner scanner = new Scanner(inputStream);
        scanner.skip(".e.lo");
        Assert.assertEquals("world", scanner.next());
        scanner.close();
    }

    @Test
    public void whenChangeScannerDelimiter_thenChanged() throws IOException {
        final String expectedValue = "Hello world";
        final String[] splited = expectedValue.split("o");
        final FileInputStream inputStream = new FileInputStream("src/test/resources/test_read.in");
        final Scanner scanner = new Scanner(inputStream);
        scanner.useDelimiter("o");
        Assert.assertEquals(splited[0], scanner.next());
        Assert.assertEquals(splited[1], scanner.next());
        Assert.assertEquals(splited[2], scanner.next());
        scanner.close();
    }

    @Test
    public void whenReadWithScannerTwoDelimiters_thenCorrect() throws IOException {
        final Scanner scanner = new Scanner(new File("src/test/resources/test_read_d.in"));
        scanner.useDelimiter(",|-");
        Assert.assertEquals("John", scanner.next());
        Assert.assertEquals("Adam", scanner.next());
        Assert.assertEquals("Tom", scanner.next());
        scanner.close();
    }
}

