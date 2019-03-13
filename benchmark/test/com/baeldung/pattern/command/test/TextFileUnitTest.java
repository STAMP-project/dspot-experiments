package com.baeldung.pattern.command.test;


import com.baeldung.pattern.command.receiver.TextFile;
import org.junit.Test;


public class TextFileUnitTest {
    private static TextFile textFile;

    @Test
    public void givenTextFileInstance_whenCalledopenMethod_thenOneAssertion() {
        assertThat(TextFileUnitTest.textFile.open()).isEqualTo("Opening file file1.txt");
    }

    @Test
    public void givenTextFileInstance_whenCalledwriteMethod_thenOneAssertion() {
        assertThat(TextFileUnitTest.textFile.write()).isEqualTo("Writing to file file1.txt");
    }

    @Test
    public void givenTextFileInstance_whenCalledsaveMethod_thenOneAssertion() {
        assertThat(TextFileUnitTest.textFile.save()).isEqualTo("Saving file file1.txt");
    }

    @Test
    public void givenTextFileInstance_whenCalledcopyMethod_thenOneAssertion() {
        assertThat(TextFileUnitTest.textFile.copy()).isEqualTo("Copying file file1.txt");
    }

    @Test
    public void givenTextFileInstance_whenCalledpasteMethod_thenOneAssertion() {
        assertThat(TextFileUnitTest.textFile.paste()).isEqualTo("Pasting file file1.txt");
    }
}

