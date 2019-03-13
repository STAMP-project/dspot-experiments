package cucumber.api.event;


import gherkin.pickles.PickleLocation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;


public class CanonicalEventOrderTest {
    private static final int LESS_THAN = -1;

    private static final int EQUAL_TO = 0;

    private static final int GREATER_THAN = 1;

    private CanonicalEventOrder comparator = new CanonicalEventOrder();

    private Event runStarted = new TestRunStarted(CanonicalEventOrderTest.getTime());

    private Event testRead = new TestSourceRead(CanonicalEventOrderTest.getTime(), "uri", "source");

    private Event suggested = new SnippetsSuggestedEvent(CanonicalEventOrderTest.getTime(), "uri", Collections.<PickleLocation>emptyList(), Collections.<String>emptyList());

    private Event feature1Case1Started = CanonicalEventOrderTest.createTestCaseEvent("feature1", 1);

    private Event feature1Case2Started = CanonicalEventOrderTest.createTestCaseEvent("feature1", 9);

    private Event feature1Case3Started = CanonicalEventOrderTest.createTestCaseEvent("feature1", 11);

    private Event feature2Case1Started = CanonicalEventOrderTest.createTestCaseEvent("feature2", 1);

    private Event runFinished = new TestRunFinished(CanonicalEventOrderTest.getTime());

    @Test
    public void verifyTestRunStartedSortedCorrectly() {
        assertThat(comparator.compare(runStarted, runStarted)).isEqualTo(CanonicalEventOrderTest.EQUAL_TO);
        assertThat(comparator.compare(runStarted, testRead)).isEqualTo(CanonicalEventOrderTest.LESS_THAN);
        assertThat(comparator.compare(runStarted, suggested)).isEqualTo(CanonicalEventOrderTest.LESS_THAN);
        assertThat(comparator.compare(runStarted, feature1Case1Started)).isEqualTo(CanonicalEventOrderTest.LESS_THAN);
        assertThat(comparator.compare(runStarted, feature1Case2Started)).isEqualTo(CanonicalEventOrderTest.LESS_THAN);
        assertThat(comparator.compare(runStarted, feature1Case3Started)).isEqualTo(CanonicalEventOrderTest.LESS_THAN);
        assertThat(comparator.compare(runStarted, feature2Case1Started)).isEqualTo(CanonicalEventOrderTest.LESS_THAN);
        assertThat(comparator.compare(runStarted, runFinished)).isEqualTo(CanonicalEventOrderTest.LESS_THAN);
    }

    @Test
    public void verifyTestSourceReadSortedCorrectly() {
        assertThat(comparator.compare(testRead, runStarted)).isEqualTo(CanonicalEventOrderTest.GREATER_THAN);
        assertThat(comparator.compare(testRead, testRead)).isEqualTo(CanonicalEventOrderTest.EQUAL_TO);
        assertThat(comparator.compare(testRead, suggested)).isEqualTo(CanonicalEventOrderTest.LESS_THAN);
        assertThat(comparator.compare(testRead, feature1Case1Started)).isEqualTo(CanonicalEventOrderTest.LESS_THAN);
        assertThat(comparator.compare(testRead, feature1Case2Started)).isEqualTo(CanonicalEventOrderTest.LESS_THAN);
        assertThat(comparator.compare(testRead, feature1Case3Started)).isEqualTo(CanonicalEventOrderTest.LESS_THAN);
        assertThat(comparator.compare(testRead, feature2Case1Started)).isEqualTo(CanonicalEventOrderTest.LESS_THAN);
        assertThat(comparator.compare(testRead, runFinished)).isEqualTo(CanonicalEventOrderTest.LESS_THAN);
    }

    @Test
    public void verifySnippetsSuggestedSortedCorrectly() {
        assertThat(comparator.compare(suggested, runStarted)).isEqualTo(CanonicalEventOrderTest.GREATER_THAN);
        assertThat(comparator.compare(suggested, testRead)).isEqualTo(CanonicalEventOrderTest.GREATER_THAN);
        assertThat(comparator.compare(suggested, suggested)).isEqualTo(CanonicalEventOrderTest.EQUAL_TO);
        assertThat(comparator.compare(suggested, feature1Case1Started)).isEqualTo(CanonicalEventOrderTest.LESS_THAN);
        assertThat(comparator.compare(suggested, feature1Case2Started)).isEqualTo(CanonicalEventOrderTest.LESS_THAN);
        assertThat(comparator.compare(suggested, feature1Case3Started)).isEqualTo(CanonicalEventOrderTest.LESS_THAN);
        assertThat(comparator.compare(suggested, feature2Case1Started)).isEqualTo(CanonicalEventOrderTest.LESS_THAN);
        assertThat(comparator.compare(suggested, runFinished)).isEqualTo(CanonicalEventOrderTest.LESS_THAN);
    }

    @Test
    public void verifyTestCaseStartedSortedCorrectly() {
        final List<Event> greaterThan = Arrays.asList(runStarted, testRead, suggested);
        for (final Event e : greaterThan) {
            assertThat(comparator.compare(feature1Case1Started, e)).isEqualTo(CanonicalEventOrderTest.GREATER_THAN);
            assertThat(comparator.compare(feature1Case2Started, e)).isEqualTo(CanonicalEventOrderTest.GREATER_THAN);
            assertThat(comparator.compare(feature1Case3Started, e)).isEqualTo(CanonicalEventOrderTest.GREATER_THAN);
            assertThat(comparator.compare(feature2Case1Started, e)).isEqualTo(CanonicalEventOrderTest.GREATER_THAN);
        }
        final List<Event> lessThan = Collections.singletonList(runFinished);
        for (final Event e : lessThan) {
            assertThat(comparator.compare(feature1Case1Started, e)).isEqualTo(CanonicalEventOrderTest.LESS_THAN);
            assertThat(comparator.compare(feature1Case2Started, e)).isEqualTo(CanonicalEventOrderTest.LESS_THAN);
            assertThat(comparator.compare(feature1Case3Started, e)).isEqualTo(CanonicalEventOrderTest.LESS_THAN);
            assertThat(comparator.compare(feature2Case1Started, e)).isEqualTo(CanonicalEventOrderTest.LESS_THAN);
        }
        assertThat(comparator.compare(feature1Case1Started, feature1Case2Started)).isLessThan(CanonicalEventOrderTest.EQUAL_TO);
        assertThat(comparator.compare(feature1Case2Started, feature1Case3Started)).isLessThan(CanonicalEventOrderTest.EQUAL_TO);
        assertThat(comparator.compare(feature1Case3Started, feature2Case1Started)).isLessThan(CanonicalEventOrderTest.EQUAL_TO);
    }

    @Test
    public void verifyTestRunFinishedSortedCorrectly() {
        assertThat(comparator.compare(runFinished, runStarted)).isEqualTo(CanonicalEventOrderTest.GREATER_THAN);
        assertThat(comparator.compare(runFinished, suggested)).isEqualTo(CanonicalEventOrderTest.GREATER_THAN);
        assertThat(comparator.compare(runFinished, testRead)).isEqualTo(CanonicalEventOrderTest.GREATER_THAN);
        assertThat(comparator.compare(runFinished, feature1Case1Started)).isEqualTo(CanonicalEventOrderTest.GREATER_THAN);
        assertThat(comparator.compare(runFinished, feature1Case2Started)).isEqualTo(CanonicalEventOrderTest.GREATER_THAN);
        assertThat(comparator.compare(runFinished, feature1Case3Started)).isEqualTo(CanonicalEventOrderTest.GREATER_THAN);
        assertThat(comparator.compare(runFinished, feature2Case1Started)).isEqualTo(CanonicalEventOrderTest.GREATER_THAN);
        assertThat(comparator.compare(runFinished, runFinished)).isEqualTo(CanonicalEventOrderTest.EQUAL_TO);
    }
}

