package cucumber.runtime.filter;


import gherkin.events.PickleEvent;
import gherkin.pickles.PickleStep;
import gherkin.pickles.PickleTag;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class LinePredicateTest {
    private static final String NAME = "pickle_name";

    private static final String LANGUAGE = "en";

    private static final List<PickleStep> NO_STEPS = Collections.<PickleStep>emptyList();

    private static final List<PickleTag> NO_TAGS = Collections.<PickleTag>emptyList();

    @Test
    public void matches_pickles_from_files_not_in_the_predicate_map() {
        // the argument "path/file.feature another_path/file.feature:8"
        // results in only line predicates only for another_path/file.feature,
        // but all pickles from path/file.feature shall also be executed.
        PickleEvent pickleEvent = createPickleEventWithLocations("path/file.feature", Arrays.asList(pickleLocation(4)));
        LinePredicate predicate = new LinePredicate(Collections.singletonMap(URI.create("another_path/file.feature"), Arrays.asList(8)));
        Assert.assertTrue(predicate.apply(pickleEvent));
    }

    @Test
    public void matches_pickles_for_any_line_in_predicate() {
        PickleEvent pickleEvent = createPickleEventWithLocations("path/file.feature", Arrays.asList(pickleLocation(8)));
        LinePredicate predicate = new LinePredicate(Collections.singletonMap(URI.create("path/file.feature"), Arrays.asList(4, 8)));
        Assert.assertTrue(predicate.apply(pickleEvent));
    }

    @Test
    public void matches_pickles_on_any_location_of_the_pickle() {
        PickleEvent pickleEvent = createPickleEventWithLocations("path/file.feature", Arrays.asList(pickleLocation(4), pickleLocation(8)));
        LinePredicate predicate = new LinePredicate(Collections.singletonMap(URI.create("path/file.feature"), Arrays.asList(8)));
        Assert.assertTrue(predicate.apply(pickleEvent));
    }

    @Test
    public void does_not_matches_pickles_not_on_any_line_of_the_predicate() {
        PickleEvent pickleEvent = createPickleEventWithLocations("path/file.feature", Arrays.asList(pickleLocation(4), pickleLocation(8)));
        LinePredicate predicate = new LinePredicate(Collections.singletonMap(URI.create("path/file.feature"), Arrays.asList(10)));
        Assert.assertFalse(predicate.apply(pickleEvent));
    }
}

