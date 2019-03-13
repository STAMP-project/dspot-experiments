/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.util;


import org.junit.Test;


public class StringUtilTest {
    @Test
    public void testNotCleanIfMissingQuotePrefix() {
        StringUtilTest.assertCleaned("COLUMN_NAME'", "COLUMN_NAME'");
    }

    @Test
    public void testNotCleanIfMissingQuotePostfix() {
        StringUtilTest.assertCleaned("'COLUMN_NAME", "'COLUMN_NAME");
    }

    @Test
    public void testCleanQuotesIfQuoted() {
        StringUtilTest.assertCleaned("'COLUMN_NAME'", "COLUMN_NAME");
    }

    @Test
    public void testDoesNotReduceAnyQuotesIfNotQuotedString() {
        StringUtilTest.assertCleaned("prefix ''Something in quotes'' postfix", "prefix ''Something in quotes'' postfix");
    }

    @Test
    public void testReducesDoubleSingleQuotesInQuotedStringToSingleSingleQuotes() {
        StringUtilTest.assertCleaned("'prefix ''Something in quotes'' postfix'", "prefix 'Something in quotes' postfix");
        StringUtilTest.assertCleaned("'a '''' b ''' c '''' d '' e '' f '''", "a '' b '' c '' d ' e ' f '");
    }

    @Test
    public void testCleanDateFormat() {
        StringUtilTest.assertCleaned("'yyyy-MM-dd''T''HH:mm:ssX'", "yyyy-MM-dd'T'HH:mm:ssX");
        StringUtilTest.assertCleaned("'yyyy.MM.dd G ''at'' HH:mm:ss z'", "yyyy.MM.dd G 'at' HH:mm:ss z");
        StringUtilTest.assertCleaned("'EEE, MMM d, ''''yy'", "EEE, MMM d, ''yy");
        StringUtilTest.assertCleaned("'hh ''o''clock'' a, zzzz'", "hh 'o'clock' a, zzzz");
        StringUtilTest.assertCleaned("'YYYY-''W''ww-u'", "YYYY-'W'ww-u");
    }
}

