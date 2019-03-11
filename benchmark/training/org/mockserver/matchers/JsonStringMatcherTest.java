package org.mockserver.matchers;


import org.junit.Assert;
import org.junit.Test;
import org.mockserver.logging.MockServerLogger;

import static MatchType.ONLY_MATCHING_FIELDS;


/**
 *
 *
 * @author jamesdbloom
 */
public class JsonStringMatcherTest {
    @Test
    public void shouldMatchExactMatchingJson() {
        // given
        String matched = (((((((((((((((((((((((((("" + "{") + (NEW_LINE)) + "    \"menu\": {") + (NEW_LINE)) + "        \"id\": \"file\",") + (NEW_LINE)) + "        \"value\": \"File\",") + (NEW_LINE)) + "        \"popup\": {") + (NEW_LINE)) + "            \"menuitem\": [") + (NEW_LINE)) + "                {") + (NEW_LINE)) + "                    \"value\": \"Close\",") + (NEW_LINE)) + "                    \"onclick\": \"CloseDoc()\"") + (NEW_LINE)) + "                }") + (NEW_LINE)) + "            ]") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "}";
        // then
        Assert.assertTrue(matches(null, matched));
    }

    @Test
    public void shouldNotMatchExactMatchingJson() {
        // given
        String matched = (((((((((((((((((((((((((("" + "{") + (NEW_LINE)) + "    \"menu\": {") + (NEW_LINE)) + "        \"id\": \"file\",") + (NEW_LINE)) + "        \"value\": \"File\",") + (NEW_LINE)) + "        \"popup\": {") + (NEW_LINE)) + "            \"menuitem\": [") + (NEW_LINE)) + "                {") + (NEW_LINE)) + "                    \"value\": \"Close\",") + (NEW_LINE)) + "                    \"onclick\": \"CloseDoc()\"") + (NEW_LINE)) + "                }") + (NEW_LINE)) + "            ]") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "}";
        // then
        Assert.assertFalse(matches(null, matched));
    }

    @Test
    public void shouldMatchMatchingSubJson() {
        // given
        String matched = (((((((((((((((((((((((((((((((((((((((((("" + "{") + (NEW_LINE)) + "    \"menu\": {") + (NEW_LINE)) + "        \"id\": \"file\",") + (NEW_LINE)) + "        \"value\": \"File\",") + (NEW_LINE)) + "        \"popup\": {") + (NEW_LINE)) + "            \"menuitem\": [") + (NEW_LINE)) + "                {") + (NEW_LINE)) + "                    \"value\": \"New\",") + (NEW_LINE)) + "                    \"onclick\": \"CreateNewDoc()\"") + (NEW_LINE)) + "                }, ") + (NEW_LINE)) + "                {") + (NEW_LINE)) + "                    \"value\": \"Open\",") + (NEW_LINE)) + "                    \"onclick\": \"OpenDoc()\"") + (NEW_LINE)) + "                }, ") + (NEW_LINE)) + "                {") + (NEW_LINE)) + "                    \"value\": \"Close\",") + (NEW_LINE)) + "                    \"onclick\": \"CloseDoc()\"") + (NEW_LINE)) + "                }") + (NEW_LINE)) + "            ]") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "}";
        // then
        Assert.assertTrue(matches(null, matched));
        Assert.assertTrue(matches(null, matched));
    }

    @Test
    public void shouldMatchMatchingSubJsonWithSomeSubJsonFields() {
        // given
        String matched = (((((((((((((((((((((((((((((((((((((((((((((((("" + "{") + (NEW_LINE)) + "    \"glossary\": {") + (NEW_LINE)) + "        \"title\": \"example glossary\",") + (NEW_LINE)) + "        \"GlossDiv\": {") + (NEW_LINE)) + "            \"title\": \"S\",") + (NEW_LINE)) + "            \"GlossList\": {") + (NEW_LINE)) + "                \"GlossEntry\": {") + (NEW_LINE)) + "                    \"ID\": \"SGML\",") + (NEW_LINE)) + "                    \"SortAs\": \"SGML\",") + (NEW_LINE)) + "                    \"GlossTerm\": \"Standard Generalized Markup Language\",") + (NEW_LINE)) + "                    \"Acronym\": \"SGML\",") + (NEW_LINE)) + "                    \"Abbrev\": \"ISO 8879:1986\",") + (NEW_LINE)) + "                    \"GlossDef\": {") + (NEW_LINE)) + "                        \"para\": \"A meta-markup language, used to create markup languages such as DocBook.\",") + (NEW_LINE)) + "                        \"GlossSeeAlso\": [") + (NEW_LINE)) + "                            \"GML\",") + (NEW_LINE)) + "                            \"XML\"") + (NEW_LINE)) + "                        ]") + (NEW_LINE)) + "                    }, ") + (NEW_LINE)) + "                    \"GlossSee\": \"markup\"") + (NEW_LINE)) + "                }") + (NEW_LINE)) + "            }") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "}";
        // then
        Assert.assertTrue(matches(null, matched));
    }

    @Test
    public void shouldNotMatchNotMatchingSubJsonWithSomeSubJsonFields() {
        // given
        String matched = (((((((((((((((((((((((((((((((((((((((((((((((("" + "{") + (NEW_LINE)) + "    \"glossary\": {") + (NEW_LINE)) + "        \"title\": \"example glossary\",") + (NEW_LINE)) + "        \"GlossDiv\": {") + (NEW_LINE)) + "            \"title\": \"S\",") + (NEW_LINE)) + "            \"GlossList\": {") + (NEW_LINE)) + "                \"GlossEntry\": {") + (NEW_LINE)) + "                    \"ID\": \"SGML\",") + (NEW_LINE)) + "                    \"SortAs\": \"SGML\",") + (NEW_LINE)) + "                    \"GlossTerm\": \"Standard Generalized Markup Language\",") + (NEW_LINE)) + "                    \"Acronym\": \"SGML\",") + (NEW_LINE)) + "                    \"Abbrev\": \"ISO 8879:1986\",") + (NEW_LINE)) + "                    \"GlossDef\": {") + (NEW_LINE)) + "                        \"para\": \"A meta-markup language, used to create markup languages such as DocBook.\",") + (NEW_LINE)) + "                        \"GlossSeeAlso\": [") + (NEW_LINE)) + "                            \"GML\",") + (NEW_LINE)) + "                            \"XML\"") + (NEW_LINE)) + "                        ]") + (NEW_LINE)) + "                    }, ") + (NEW_LINE)) + "                    \"GlossSee\": \"markup\"") + (NEW_LINE)) + "                }") + (NEW_LINE)) + "            }") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "}";
        // then
        Assert.assertFalse(matches(null, matched));
    }

    @Test
    public void shouldMatchNotMatchingSubJsonWithSomeSubJsonFields() {
        // given
        String matched = (((((((((((((((((((((((((((((((((((((((((((((((("" + "{") + (NEW_LINE)) + "    \"glossary\": {") + (NEW_LINE)) + "        \"title\": \"example glossary\",") + (NEW_LINE)) + "        \"GlossDiv\": {") + (NEW_LINE)) + "            \"title\": \"S\",") + (NEW_LINE)) + "            \"GlossList\": {") + (NEW_LINE)) + "                \"GlossEntry\": {") + (NEW_LINE)) + "                    \"ID\": \"SGML\",") + (NEW_LINE)) + "                    \"SortAs\": \"SGML\",") + (NEW_LINE)) + "                    \"GlossTerm\": \"Standard Generalized Markup Language\",") + (NEW_LINE)) + "                    \"Acronym\": \"SGML\",") + (NEW_LINE)) + "                    \"Abbrev\": \"ISO 8879:1986\",") + (NEW_LINE)) + "                    \"GlossDef\": {") + (NEW_LINE)) + "                        \"para\": \"A meta-markup language, used to create markup languages such as DocBook.\",") + (NEW_LINE)) + "                        \"GlossSeeAlso\": [") + (NEW_LINE)) + "                            \"GML\",") + (NEW_LINE)) + "                            \"XML\"") + (NEW_LINE)) + "                        ]") + (NEW_LINE)) + "                    }, ") + (NEW_LINE)) + "                    \"GlossSee\": \"markup\"") + (NEW_LINE)) + "                }") + (NEW_LINE)) + "            }") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "}";
        // then
        Assert.assertTrue(matches(null, matched));
    }

    @Test
    public void shouldMatchMatchingSubJsonWithDifferentArrayOrder() {
        // given
        String matched = (((((((((((((((((((((((((((((((((((((((((("" + "{") + (NEW_LINE)) + "    \"menu\": {") + (NEW_LINE)) + "        \"id\": \"file\",") + (NEW_LINE)) + "        \"value\": \"File\",") + (NEW_LINE)) + "        \"popup\": {") + (NEW_LINE)) + "            \"menuitem\": [") + (NEW_LINE)) + "                {") + (NEW_LINE)) + "                    \"value\": \"New\",") + (NEW_LINE)) + "                    \"onclick\": \"CreateNewDoc()\"") + (NEW_LINE)) + "                }, ") + (NEW_LINE)) + "                {") + (NEW_LINE)) + "                    \"value\": \"Open\",") + (NEW_LINE)) + "                    \"onclick\": \"OpenDoc()\"") + (NEW_LINE)) + "                }, ") + (NEW_LINE)) + "                {") + (NEW_LINE)) + "                    \"value\": \"Close\",") + (NEW_LINE)) + "                    \"onclick\": \"CloseDoc()\"") + (NEW_LINE)) + "                }") + (NEW_LINE)) + "            ]") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "}";
        // then
        Assert.assertTrue(matches(null, matched));
        Assert.assertTrue(matches(null, matched));
    }

    @Test
    public void shouldNotMatchMatchingSubJsonWithDifferentArrayOrder() {
        // given
        String matched = (((((((((((((((((((((((((((((((((((((((((("" + "{") + (NEW_LINE)) + "    \"menu\": {") + (NEW_LINE)) + "        \"id\": \"file\",") + (NEW_LINE)) + "        \"value\": \"File\",") + (NEW_LINE)) + "        \"popup\": {") + (NEW_LINE)) + "            \"menuitem\": [") + (NEW_LINE)) + "                {") + (NEW_LINE)) + "                    \"value\": \"New\",") + (NEW_LINE)) + "                    \"onclick\": \"CreateNewDoc()\"") + (NEW_LINE)) + "                }, ") + (NEW_LINE)) + "                {") + (NEW_LINE)) + "                    \"value\": \"Open\",") + (NEW_LINE)) + "                    \"onclick\": \"OpenDoc()\"") + (NEW_LINE)) + "                }, ") + (NEW_LINE)) + "                {") + (NEW_LINE)) + "                    \"value\": \"Close\",") + (NEW_LINE)) + "                    \"onclick\": \"CloseDoc()\"") + (NEW_LINE)) + "                }") + (NEW_LINE)) + "            ]") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "}";
        // then
        Assert.assertFalse(matches(null, matched));
        Assert.assertFalse(matches(null, matched));
    }

    @Test
    public void shouldNotMatchIllegalJson() {
        Assert.assertFalse(matches(null, "illegal_json"));
        Assert.assertFalse(matches(null, "some_other_illegal_json"));
    }

    @Test
    public void shouldNotMatchNullExpectation() {
        Assert.assertTrue(matches(null, "some_value"));
    }

    @Test
    public void shouldNotMatchEmptyExpectation() {
        Assert.assertTrue(matches(null, "some_value"));
    }

    @Test
    public void shouldNotMatchNonMatchingJson() {
        // given
        String matched = (((((((((((((((((((((((((((((((((((((((((("" + "{") + (NEW_LINE)) + "    \"menu\": {") + (NEW_LINE)) + "        \"id\": \"file\",") + (NEW_LINE)) + "        \"value\": \"File\",") + (NEW_LINE)) + "        \"popup\": {") + (NEW_LINE)) + "            \"menuitem\": [") + (NEW_LINE)) + "                {") + (NEW_LINE)) + "                    \"value\": \"New\",") + (NEW_LINE)) + "                    \"onclick\": \"CreateNewDoc()\"") + (NEW_LINE)) + "                }, ") + (NEW_LINE)) + "                {") + (NEW_LINE)) + "                    \"value\": \"Open\",") + (NEW_LINE)) + "                    \"onclick\": \"OpenDoc()\"") + (NEW_LINE)) + "                }, ") + (NEW_LINE)) + "                {") + (NEW_LINE)) + "                    \"value\": \"Close\",") + (NEW_LINE)) + "                    \"onclick\": \"CloseDoc()\"") + (NEW_LINE)) + "                }") + (NEW_LINE)) + "            ]") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "}";
        // then
        Assert.assertFalse(matches(null, matched));
    }

    @Test
    public void shouldMatchJsonForIncorrectArrayOrder() {
        // given
        String matched = "{id:1,pets:[\"dog\",\"cat\",\"fish\"]}";
        // then
        Assert.assertTrue(matches(null, matched));
    }

    @Test
    public void shouldNotMatchJsonForIncorrectArrayOrder() {
        // given
        String matched = "{id:1,pets:[\"dog\",\"cat\",\"fish\"]}";
        // then
        Assert.assertFalse(matches(null, matched));
    }

    @Test
    public void shouldMatchJsonForExtraField() {
        // given
        String matched = "{id:1,pets:[\"dog\",\"cat\",\"fish\"],extraField:\"extraValue\"}";
        // then
        Assert.assertTrue(matches(null, matched));
    }

    @Test
    public void shouldNotMatchJsonForExtraField() {
        // given
        String matched = "{id:1,pets:[\"dog\",\"cat\",\"fish\"],extraField:\"extraValue\"}";
        // then
        Assert.assertFalse(matches(null, matched));
    }

    @Test
    public void shouldNotMatchNonMatchingSubJson() {
        // given
        String matched = (((((((((((((((((((((((((((((((((((((((((("" + "{") + (NEW_LINE)) + "    \"menu\": {") + (NEW_LINE)) + "        \"id\": \"file\",") + (NEW_LINE)) + "        \"value\": \"File\",") + (NEW_LINE)) + "        \"popup\": {") + (NEW_LINE)) + "            \"menuitem\": [") + (NEW_LINE)) + "                {") + (NEW_LINE)) + "                    \"value\": \"New\",") + (NEW_LINE)) + "                    \"onclick\": \"CreateNewDoc()\"") + (NEW_LINE)) + "                }, ") + (NEW_LINE)) + "                {") + (NEW_LINE)) + "                    \"value\": \"Open\",") + (NEW_LINE)) + "                    \"onclick\": \"OpenDoc()\"") + (NEW_LINE)) + "                }, ") + (NEW_LINE)) + "                {") + (NEW_LINE)) + "                    \"value\": \"Close\",") + (NEW_LINE)) + "                    \"onclick\": \"CloseDoc()\"") + (NEW_LINE)) + "                }") + (NEW_LINE)) + "            ]") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "}";
        // then
        Assert.assertFalse(matches(null, matched));
        Assert.assertFalse(matches(null, matched));
    }

    @Test
    public void shouldNotMatchNullTest() {
        Assert.assertFalse(new JsonStringMatcher(new MockServerLogger(), "some_value", ONLY_MATCHING_FIELDS).matches(null, null));
    }

    @Test
    public void shouldNotMatchEmptyTest() {
        Assert.assertFalse(matches(null, ""));
    }

    @Test
    public void showHaveCorrectEqualsBehaviour() {
        MockServerLogger mockServerLogger = new MockServerLogger();
        Assert.assertEquals(new JsonStringMatcher(mockServerLogger, "some_value", ONLY_MATCHING_FIELDS), new JsonStringMatcher(mockServerLogger, "some_value", ONLY_MATCHING_FIELDS));
    }
}

