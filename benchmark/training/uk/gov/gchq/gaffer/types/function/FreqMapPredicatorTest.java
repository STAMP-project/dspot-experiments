package uk.gov.gchq.gaffer.types.function;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.JsonAssert;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.types.FreqMap;
import uk.gov.gchq.koryphe.impl.predicate.Regex;


public class FreqMapPredicatorTest {
    private FreqMap freqMap;

    @Test
    public void shouldFilterMapWithMultipleResults() {
        // given
        final Regex predicate = new Regex("^\\wo\\w$");
        final FreqMapPredicator fRegexPredicator = new FreqMapPredicator(predicate);
        // when
        final FreqMap fRegex = fRegexPredicator.apply(freqMap);
        // then
        Assert.assertEquals(fRegex.size(), 2);
        Assert.assertTrue(fRegex.containsKey("cow"));
        Assert.assertTrue(fRegex.containsKey("dog"));
    }

    @Test
    public void shouldFilterMapWithSingleResult() {
        // given
        final Regex predicate = new Regex("^c.*o.*g$");
        final FreqMapPredicator fRegexPredicator = new FreqMapPredicator(predicate);
        // when
        final FreqMap fRegex = fRegexPredicator.apply(freqMap);
        // then
        Assert.assertEquals(fRegex.size(), 1);
        Assert.assertTrue(fRegex.containsKey("catdog"));
    }

    @Test
    public void shouldHandleNulls() {
        // given
        final FreqMapPredicator nullRegPredicator = new FreqMapPredicator(null);
        // when
        final FreqMap map = nullRegPredicator.apply(freqMap);
        // then
        Assert.assertThat(map, CoreMatchers.is(freqMap));
    }

    @Test
    public void shouldNotMutateOriginalValue() {
        // given
        final Regex predicate = new Regex("^\\wo\\w$");
        final FreqMapPredicator fRegexPredicator = new FreqMapPredicator(predicate);
        // when
        final FreqMap fRegex = fRegexPredicator.apply(freqMap);
        // then
        Assert.assertEquals(fRegex.size(), 2);
        Assert.assertTrue(fRegex.containsKey("cow"));
        Assert.assertTrue(fRegex.containsKey("dog"));
        Assert.assertEquals(freqMap.size(), 4);
        Assert.assertTrue(freqMap.containsKey("cat"));
        Assert.assertTrue(freqMap.containsKey("dog"));
        Assert.assertTrue(freqMap.containsKey("catdog"));
        Assert.assertTrue(freqMap.containsKey("cow"));
    }

    @Test
    public void shouldJsonSerialiseAndDeserialise() throws SerialisationException {
        // given
        final FreqMapPredicator nullPredicator = new FreqMapPredicator();
        final FreqMapPredicator regexPredicator = new FreqMapPredicator(new Regex("^\\wo\\w$"));
        // when
        final String json = new String(JSONSerialiser.serialise(nullPredicator, true));
        final String json2 = new String(JSONSerialiser.serialise(regexPredicator, false));
        // then
        JsonAssert.assertEquals(String.format(("{%n" + ("  \"class\" : \"uk.gov.gchq.gaffer.types.function.FreqMapPredicator\"%n" + "}"))), json);
        JsonAssert.assertEquals(("{\"class\":\"uk.gov.gchq.gaffer.types.function.FreqMapPredicator\"," + ("\"predicate\":{\"class\":\"uk.gov.gchq.koryphe.impl.predicate.Regex\",\"value\":" + "{\"java.util.regex.Pattern\":\"^\\\\wo\\\\w$\"}}}")), json2);
        final FreqMapPredicator deserializedNull = JSONSerialiser.deserialise(json, FreqMapPredicator.class);
        final FreqMapPredicator deserializedRegex = JSONSerialiser.deserialise(json2, FreqMapPredicator.class);
        Assert.assertEquals(deserializedNull.apply(freqMap).size(), freqMap.size());
        Assert.assertEquals(deserializedRegex.apply(freqMap).size(), 2);
    }
}

