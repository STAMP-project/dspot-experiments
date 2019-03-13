package org.baeldung.core.exceptions;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileNotFoundExceptionUnitTest {
    private static final Logger LOG = LoggerFactory.getLogger(FileNotFoundExceptionUnitTest.class);

    private String fileName = Double.toString(Math.random());

    @Test(expected = FileNotFoundExceptionUnitTest.BusinessException.class)
    public void raiseBusinessSpecificException() throws IOException {
        try {
            readFailingFile();
        } catch (FileNotFoundException ex) {
            throw new FileNotFoundExceptionUnitTest.BusinessException("BusinessException: necessary file was not present.");
        }
    }

    @Test
    public void createFile() throws IOException {
        try {
            readFailingFile();
        } catch (FileNotFoundException ex) {
            try {
                new File(fileName).createNewFile();
                readFailingFile();
            } catch (IOException ioe) {
                throw new RuntimeException("BusinessException: even creation is not possible.");
            }
        }
    }

    @Test
    public void logError() throws IOException {
        try {
            readFailingFile();
        } catch (FileNotFoundException ex) {
            FileNotFoundExceptionUnitTest.LOG.error((("Optional file " + (fileName)) + " was not found."));
        }
    }

    private class BusinessException extends RuntimeException {
        BusinessException(String string) {
            super(string);
        }
    }
}

