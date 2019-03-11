package com.baeldung.eclipsecollections;


import java.util.List;
import org.assertj.core.api.Assertions;
import org.eclipse.collections.api.list.MutableList;
import org.junit.Test;


public class FlatCollectUnitTest {
    MutableList<String> addresses1;

    MutableList<String> addresses2;

    MutableList<String> addresses3;

    MutableList<String> addresses4;

    List<String> expectedAddresses;

    MutableList<Student> students;

    @Test
    public void whenFlatCollect_thenCorrect() {
        MutableList<String> addresses = students.flatCollect(Student::getAddresses);
        Assertions.assertThat(addresses).containsExactlyElementsOf(this.expectedAddresses);
    }
}

