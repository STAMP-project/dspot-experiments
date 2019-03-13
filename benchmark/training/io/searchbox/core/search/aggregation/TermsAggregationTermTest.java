package io.searchbox.core.search.aggregation;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.searchbox.core.search.aggregation.TermsAggregation.Entry;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TermsAggregationTermTest {
    private static String EXPECTED_KEY_VALUE = "someKeyValue";

    private static String EXPECTED_KEY_AS_STRING_VALUE = "someKeyAsStringValue";

    private static long EXPECTED_DOC_COUNT_VALUE = 100L;

    static String termObjectWithKeyAsStringField = ((((((((("{\n" + "    \"key\": \"") + (TermsAggregationTermTest.EXPECTED_KEY_VALUE)) + "\",\n") + "    \"key_as_string\": \"") + (TermsAggregationTermTest.EXPECTED_KEY_AS_STRING_VALUE)) + "\",\n") + "    \"doc_count\": ") + (TermsAggregationTermTest.EXPECTED_DOC_COUNT_VALUE)) + "\n") + "}";

    static String termObjectWithoutKeyAsStringField = (((((("{\n" + "    \"key\": \"") + (TermsAggregationTermTest.EXPECTED_KEY_VALUE)) + "\",\n") + "    \"doc_count\": ") + (TermsAggregationTermTest.EXPECTED_DOC_COUNT_VALUE)) + "\n") + "}";

    static String termsAggregationContent = ((((("{\n" + (("    \"doc_count_error_upper_bound\":0,\n" + "    \"sum_other_doc_count\":0,\n") + "    \"buckets\": [\n")) + (TermsAggregationTermTest.termObjectWithKeyAsStringField)) + "    ,\n") + (TermsAggregationTermTest.termObjectWithoutKeyAsStringField)) + "    ]\n") + "}";

    @Test
    public void testParseBuckets() {
        JsonObject termsAggregationJson = new Gson().fromJson(TermsAggregationTermTest.termsAggregationContent, JsonObject.class);
        TermsAggregation termsAggregation = new TermsAggregation("termsAggregation", termsAggregationJson);
        List<Entry> buckets = termsAggregation.getBuckets();
        Assert.assertNotNull(buckets);
        Assert.assertEquals(2, buckets.size());
        // Test for entry *with* key_as_value present
        Entry entryWithKeyAsString = buckets.get(0);
        Assert.assertEquals(TermsAggregationTermTest.EXPECTED_KEY_VALUE, entryWithKeyAsString.getKey());
        Assert.assertEquals(TermsAggregationTermTest.EXPECTED_KEY_AS_STRING_VALUE, entryWithKeyAsString.getKeyAsString());
        Assert.assertTrue(((TermsAggregationTermTest.EXPECTED_DOC_COUNT_VALUE) == (entryWithKeyAsString.getCount())));
        // Test for entry *without* key_as_value present
        Entry entryWithoutKeyAsString = buckets.get(1);
        Assert.assertEquals(TermsAggregationTermTest.EXPECTED_KEY_VALUE, entryWithoutKeyAsString.getKey());
        // If key_as_string wasn't populated, return key value
        Assert.assertEquals(TermsAggregationTermTest.EXPECTED_KEY_VALUE, entryWithoutKeyAsString.getKeyAsString());
        Assert.assertTrue(((TermsAggregationTermTest.EXPECTED_DOC_COUNT_VALUE) == (entryWithoutKeyAsString.getCount())));
    }
}

