package io.hawt.ide;


import org.junit.Test;


public class IdeFacadeTest {
    IdeFacade facade = new IdeFacade();

    @Test
    public void testFindsFileInProject() throws Exception {
        String absoluteFile = assertFindSampleFileName();
        System.out.println(("Found absolute file: " + absoluteFile));
    }
}

