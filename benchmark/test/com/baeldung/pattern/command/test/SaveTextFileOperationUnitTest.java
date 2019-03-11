package com.baeldung.pattern.command.test;


import com.baeldung.pattern.command.command.TextFileOperation;
import com.baeldung.pattern.command.receiver.TextFile;
import org.junit.Test;


public class SaveTextFileOperationUnitTest {
    @Test
    public void givenSaveTextFileOperationIntance_whenCalledExecuteMethod_thenOneAssertion() {
        TextFileOperation openTextFileOperation = new com.baeldung.pattern.command.command.SaveTextFileOperation(new TextFile("file1.txt"));
        assertThat(openTextFileOperation.execute()).isEqualTo("Saving file file1.txt");
    }
}

