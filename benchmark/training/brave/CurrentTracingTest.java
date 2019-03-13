package brave;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.junit.Test;


public class CurrentTracingTest {
    @Test
    public void defaultsToNull() {
        assertThat(Tracing.current()).isNull();
    }

    @Test
    public void defaultsToNull_currentTracer() {
        assertThat(Tracing.currentTracer()).isNull();
    }

    @Test
    public void autoRegisters() {
        Tracing tracing = Tracing.newBuilder().build();
        assertThat(Tracing.current()).isSameAs(tracing);
        assertThat(Tracing.currentTracer()).isSameAs(tracing.tracer());
    }

    @Test
    public void setsNotCurrentOnClose() {
        autoRegisters();
        Tracing.current().close();
        assertThat(Tracing.current()).isNull();
        assertThat(Tracing.currentTracer()).isNull();
    }

    @Test
    public void canSetCurrentAgain() {
        setsNotCurrentOnClose();
        autoRegisters();
    }

    @Test
    public void onlyRegistersOnce() throws InterruptedException {
        final Tracing[] threadValues = new Tracing[10];// array ref for thread-safe setting

        List<Thread> getOrSet = new ArrayList<>(20);
        for (int i = 0; i < 10; i++) {
            final int index = i;
            getOrSet.add(new Thread(() -> threadValues[index] = Tracing.current()));
        }
        for (int i = 10; i < 20; i++) {
            getOrSet.add(new Thread(() -> Tracing.newBuilder().build().tracer()));
        }
        // make it less predictable
        Collections.shuffle(getOrSet);
        // start the races
        getOrSet.forEach(Thread::start);
        for (Thread thread : getOrSet) {
            thread.join();
        }
        Set<Tracing> tracers = new java.util.LinkedHashSet(Arrays.asList(threadValues));
        tracers.remove(null);
        // depending on race, we should have either one tracer or no tracer
        assertThat(((tracers.isEmpty()) || ((tracers.size()) == 1))).isTrue();
    }
}

