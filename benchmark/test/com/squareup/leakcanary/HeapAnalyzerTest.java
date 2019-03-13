package com.squareup.leakcanary;


import com.squareup.haha.perflib.RootObj;
import com.squareup.haha.perflib.Snapshot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class HeapAnalyzerTest {
    private static final List<RootObj> DUP_ROOTS = Arrays.asList(new RootObj(SYSTEM_CLASS, 6L), new RootObj(SYSTEM_CLASS, 5L), new RootObj(SYSTEM_CLASS, 3L), new RootObj(SYSTEM_CLASS, 5L), new RootObj(NATIVE_STATIC, 3L));

    private HeapAnalyzer heapAnalyzer;

    @Test
    public void ensureUniqueRoots() {
        Snapshot snapshot = createSnapshot(HeapAnalyzerTest.DUP_ROOTS);
        heapAnalyzer.deduplicateGcRoots(snapshot);
        Collection<RootObj> uniqueRoots = snapshot.getGCRoots();
        assertThat(uniqueRoots).hasSize(4);
        List<Long> rootIds = new ArrayList<>();
        for (RootObj root : uniqueRoots) {
            rootIds.add(root.getId());
        }
        Collections.sort(rootIds);
        // 3 appears twice because even though two RootObjs have the same id, they're different types.
        assertThat(rootIds).containsExactly(3L, 3L, 5L, 6L);
    }
}

