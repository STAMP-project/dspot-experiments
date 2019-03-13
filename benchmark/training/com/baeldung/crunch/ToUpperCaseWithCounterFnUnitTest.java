package com.baeldung.crunch;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.crunch.PCollection;
import org.apache.crunch.impl.mem.MemPipeline;
import org.apache.crunch.types.writable.Writables;
import org.junit.Assert;
import org.junit.Test;


public class ToUpperCaseWithCounterFnUnitTest {
    @Test
    public void whenFunctionCalled_counterIncementendForChangedValues() {
        PCollection<String> inputStrings = MemPipeline.collectionOf("This", "is", "a", "TEST", "string");
        PCollection<String> upperCaseStrings = inputStrings.parallelDo(new ToUpperCaseWithCounterFn(), Writables.strings());
        Assert.assertEquals(ImmutableList.of("THIS", "IS", "A", "TEST", "STRING"), Lists.newArrayList(upperCaseStrings.materialize()));
        Assert.assertEquals(4L, MemPipeline.getCounters().findCounter("UpperCase", "modified").getValue());
    }
}

