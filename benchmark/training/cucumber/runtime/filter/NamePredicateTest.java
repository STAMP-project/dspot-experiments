package cucumber.runtime.filter;


import gherkin.events.PickleEvent;
import gherkin.pickles.PickleLocation;
import gherkin.pickles.PickleStep;
import gherkin.pickles.PickleTag;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class NamePredicateTest {
    private static final List<PickleStep> NO_STEPS = Collections.emptyList();

    private static final List<PickleTag> NO_TAGS = Collections.emptyList();

    private static final PickleLocation MOCK_LOCATION = Mockito.mock(PickleLocation.class);

    @Test
    public void anchored_name_pattern_matches_exact_name() {
        PickleEvent pickleEvent = createPickleWithName("a pickle name");
        NamePredicate predicate = new NamePredicate(Arrays.asList(Pattern.compile("^a pickle name$")));
        Assert.assertTrue(predicate.apply(pickleEvent));
    }

    @Test
    public void anchored_name_pattern_does_not_match_part_of_name() {
        PickleEvent pickleEvent = createPickleWithName("a pickle name with suffix");
        NamePredicate predicate = new NamePredicate(Arrays.asList(Pattern.compile("^a pickle name$")));
        Assert.assertFalse(predicate.apply(pickleEvent));
    }

    @Test
    public void non_anchored_name_pattern_matches_part_of_name() {
        PickleEvent pickleEvent = createPickleWithName("a pickle name with suffix");
        NamePredicate predicate = new NamePredicate(Arrays.asList(Pattern.compile("a pickle name")));
        Assert.assertTrue(predicate.apply(pickleEvent));
    }

    @Test
    public void wildcard_name_pattern_matches_part_of_name() {
        PickleEvent pickleEvent = createPickleWithName("a pickleEvent name");
        NamePredicate predicate = new NamePredicate(Arrays.asList(Pattern.compile("a .* name")));
        Assert.assertTrue(predicate.apply(pickleEvent));
    }
}

