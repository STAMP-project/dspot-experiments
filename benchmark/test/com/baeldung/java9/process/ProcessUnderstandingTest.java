package com.baeldung.java9.process;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class ProcessUnderstandingTest {
    @Test
    public void givenSourceProgram_whenExecutedFromAnotherProgram_thenSourceProgramOutput3() throws IOException {
        Process process = Runtime.getRuntime().exec("javac -cp src src\\main\\java\\com\\baeldung\\java9\\process\\OutputStreamExample.java");
        process = Runtime.getRuntime().exec("java -cp  src/main/java com.baeldung.java9.process.OutputStreamExample");
        BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
        int value = Integer.parseInt(output.readLine());
        Assertions.assertEquals(3, value);
    }

    @Test
    public void givenSourceProgram_whenReadingInputStream_thenFirstLineEquals3() throws IOException {
        Process process = Runtime.getRuntime().exec("javac -cp src src\\main\\java\\com\\baeldung\\java9\\process\\OutputStreamExample.java");
        process = Runtime.getRuntime().exec("java -cp  src/main/java com.baeldung.java9.process.OutputStreamExample");
        BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
        int value = Integer.parseInt(output.readLine());
        Assertions.assertEquals(3, value);
    }

    @Test
    public void givenSubProcess_whenEncounteringError_thenErrorStreamNotNull() throws IOException {
        Process process = Runtime.getRuntime().exec("javac -cp src src\\main\\java\\com\\baeldung\\java9\\process\\ProcessCompilationError.java");
        BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String errorString = error.readLine();
        Assertions.assertNotNull(errorString);
    }

    @Test
    public void givenProcessNotCreated_fromWithinJavaApplicationDestroying_thenProcessNotAlive() {
        Optional<ProcessHandle> optionalProcessHandle = ProcessHandle.of(5232);
        ProcessHandle processHandle = optionalProcessHandle.get();
        processHandle.destroy();
        Assertions.assertFalse(processHandle.isAlive());
    }

    @Test
    public void givenRunningProcesses_whenFilterOnProcessIdRange_thenGetSelectedProcessPid() {
        assertThat((((int) (ProcessHandle.allProcesses().filter(( ph) -> ((ph.pid()) > 10000) && ((ph.pid()) < 50000)).count())) > 0));
    }
}

