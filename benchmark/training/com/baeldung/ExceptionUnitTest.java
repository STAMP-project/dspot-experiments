package com.baeldung;


import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class ExceptionUnitTest {
    @Test
    public void whenModifyMapDuringIteration_thenThrowExecption() {
        Map<Integer, String> hashmap = new HashMap<>();
        hashmap.put(1, "One");
        hashmap.put(2, "Two");
        Executable executable = () -> hashmap.forEach(( key, value) -> hashmap.remove(1));
        Assertions.assertThrows(ConcurrentModificationException.class, executable);
    }
}

