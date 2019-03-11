package com.hankcs.hanlp.model.perceptron.utility;


import java.util.Arrays;
import junit.framework.TestCase;


public class IOUtilityTest extends TestCase {
    public void testReadLineToArray() throws Exception {
        String line = " ??   ?? ! ";
        String[] array = IOUtility.readLineToArray(line);
        System.out.println(Arrays.toString(array));
    }
}

