package org.embulk.spi.time;


import RubyTimeFormatDirective.AMPM_OF_DAY_LOWER_CASE;
import RubyTimeFormatDirective.AMPM_OF_DAY_UPPER_CASE;
import RubyTimeFormatDirective.CENTURY;
import RubyTimeFormatDirective.DAY_OF_MONTH_BLANK_PADDED;
import RubyTimeFormatDirective.DAY_OF_MONTH_ZERO_PADDED;
import RubyTimeFormatDirective.DAY_OF_WEEK_ABBREVIATED_NAME;
import RubyTimeFormatDirective.DAY_OF_WEEK_FULL_NAME;
import RubyTimeFormatDirective.DAY_OF_WEEK_STARTING_WITH_MONDAY_1;
import RubyTimeFormatDirective.DAY_OF_WEEK_STARTING_WITH_SUNDAY_0;
import RubyTimeFormatDirective.DAY_OF_YEAR;
import RubyTimeFormatDirective.HOUR_OF_AMPM_BLANK_PADDED;
import RubyTimeFormatDirective.HOUR_OF_AMPM_ZERO_PADDED;
import RubyTimeFormatDirective.HOUR_OF_DAY_BLANK_PADDED;
import RubyTimeFormatDirective.HOUR_OF_DAY_ZERO_PADDED;
import RubyTimeFormatDirective.MILLISECOND_SINCE_EPOCH;
import RubyTimeFormatDirective.MILLI_OF_SECOND;
import RubyTimeFormatDirective.MINUTE_OF_HOUR;
import RubyTimeFormatDirective.MONTH_OF_YEAR;
import RubyTimeFormatDirective.MONTH_OF_YEAR_ABBREVIATED_NAME;
import RubyTimeFormatDirective.MONTH_OF_YEAR_ABBREVIATED_NAME_ALIAS_SMALL_H;
import RubyTimeFormatDirective.MONTH_OF_YEAR_FULL_NAME;
import RubyTimeFormatDirective.NANO_OF_SECOND;
import RubyTimeFormatDirective.SECOND_OF_MINUTE;
import RubyTimeFormatDirective.SECOND_SINCE_EPOCH;
import RubyTimeFormatDirective.TIME_OFFSET;
import RubyTimeFormatDirective.TIME_ZONE_NAME;
import RubyTimeFormatDirective.WEEK_BASED_YEAR_WITHOUT_CENTURY;
import RubyTimeFormatDirective.WEEK_BASED_YEAR_WITH_CENTURY;
import RubyTimeFormatDirective.WEEK_OF_WEEK_BASED_YEAR;
import RubyTimeFormatDirective.WEEK_OF_YEAR_STARTING_WITH_MONDAY;
import RubyTimeFormatDirective.WEEK_OF_YEAR_STARTING_WITH_SUNDAY;
import RubyTimeFormatDirective.YEAR_WITHOUT_CENTURY;
import RubyTimeFormatDirective.YEAR_WITH_CENTURY;
import org.junit.Test;


/**
 * TestRubyTimeFormat tests org.embulk.spi.time.RubyTimeFormat.
 */
public class TestRubyTimeFormat {
    @Test
    public void testEmpty() {
        testFormat("");
    }

    @Test
    public void testSingles() {
        testFormat("%Y", YEAR_WITH_CENTURY.toTokens());
        testFormat("%C", CENTURY.toTokens());
        testFormat("%y", YEAR_WITHOUT_CENTURY.toTokens());
        testFormat("%m", MONTH_OF_YEAR.toTokens());
        testFormat("%B", MONTH_OF_YEAR_FULL_NAME.toTokens());
        testFormat("%b", MONTH_OF_YEAR_ABBREVIATED_NAME.toTokens());
        testFormat("%h", MONTH_OF_YEAR_ABBREVIATED_NAME_ALIAS_SMALL_H.toTokens());
        testFormat("%d", DAY_OF_MONTH_ZERO_PADDED.toTokens());
        testFormat("%e", DAY_OF_MONTH_BLANK_PADDED.toTokens());
        testFormat("%j", DAY_OF_YEAR.toTokens());
        testFormat("%H", HOUR_OF_DAY_ZERO_PADDED.toTokens());
        testFormat("%k", HOUR_OF_DAY_BLANK_PADDED.toTokens());
        testFormat("%I", HOUR_OF_AMPM_ZERO_PADDED.toTokens());
        testFormat("%l", HOUR_OF_AMPM_BLANK_PADDED.toTokens());
        testFormat("%P", AMPM_OF_DAY_LOWER_CASE.toTokens());
        testFormat("%p", AMPM_OF_DAY_UPPER_CASE.toTokens());
        testFormat("%M", MINUTE_OF_HOUR.toTokens());
        testFormat("%S", SECOND_OF_MINUTE.toTokens());
        testFormat("%L", MILLI_OF_SECOND.toTokens());
        testFormat("%N", NANO_OF_SECOND.toTokens());
        testFormat("%z", TIME_OFFSET.toTokens());
        testFormat("%Z", TIME_ZONE_NAME.toTokens());
        testFormat("%A", DAY_OF_WEEK_FULL_NAME.toTokens());
        testFormat("%a", DAY_OF_WEEK_ABBREVIATED_NAME.toTokens());
        testFormat("%u", DAY_OF_WEEK_STARTING_WITH_MONDAY_1.toTokens());
        testFormat("%w", DAY_OF_WEEK_STARTING_WITH_SUNDAY_0.toTokens());
        testFormat("%G", WEEK_BASED_YEAR_WITH_CENTURY.toTokens());
        testFormat("%g", WEEK_BASED_YEAR_WITHOUT_CENTURY.toTokens());
        testFormat("%V", WEEK_OF_WEEK_BASED_YEAR.toTokens());
        testFormat("%U", WEEK_OF_YEAR_STARTING_WITH_SUNDAY.toTokens());
        testFormat("%W", WEEK_OF_YEAR_STARTING_WITH_MONDAY.toTokens());
        testFormat("%s", SECOND_SINCE_EPOCH.toTokens());
        testFormat("%Q", MILLISECOND_SINCE_EPOCH.toTokens());
    }

    @Test
    public void testRecurred() {
        testFormat("%c", DAY_OF_WEEK_ABBREVIATED_NAME.toTokens(), new RubyTimeFormatToken.Immediate(' '), MONTH_OF_YEAR_ABBREVIATED_NAME.toTokens(), new RubyTimeFormatToken.Immediate(' '), DAY_OF_MONTH_BLANK_PADDED.toTokens(), new RubyTimeFormatToken.Immediate(' '), HOUR_OF_DAY_ZERO_PADDED.toTokens(), new RubyTimeFormatToken.Immediate(':'), MINUTE_OF_HOUR.toTokens(), new RubyTimeFormatToken.Immediate(':'), SECOND_OF_MINUTE.toTokens(), new RubyTimeFormatToken.Immediate(' '), YEAR_WITH_CENTURY.toTokens());
        testFormat("%D", MONTH_OF_YEAR.toTokens(), new RubyTimeFormatToken.Immediate('/'), DAY_OF_MONTH_ZERO_PADDED.toTokens(), new RubyTimeFormatToken.Immediate('/'), YEAR_WITHOUT_CENTURY.toTokens());
        testFormat("%x", MONTH_OF_YEAR.toTokens(), new RubyTimeFormatToken.Immediate('/'), DAY_OF_MONTH_ZERO_PADDED.toTokens(), new RubyTimeFormatToken.Immediate('/'), YEAR_WITHOUT_CENTURY.toTokens());
        testFormat("%F", YEAR_WITH_CENTURY.toTokens(), new RubyTimeFormatToken.Immediate('-'), MONTH_OF_YEAR.toTokens(), new RubyTimeFormatToken.Immediate('-'), DAY_OF_MONTH_ZERO_PADDED.toTokens());
        testFormat("%n", new RubyTimeFormatToken.Immediate('\n'));
        testFormat("%R", HOUR_OF_DAY_ZERO_PADDED.toTokens(), new RubyTimeFormatToken.Immediate(':'), MINUTE_OF_HOUR.toTokens());
        testFormat("%r", HOUR_OF_AMPM_ZERO_PADDED.toTokens(), new RubyTimeFormatToken.Immediate(':'), MINUTE_OF_HOUR.toTokens(), new RubyTimeFormatToken.Immediate(':'), SECOND_OF_MINUTE.toTokens(), new RubyTimeFormatToken.Immediate(' '), AMPM_OF_DAY_UPPER_CASE.toTokens());
        testFormat("%T", HOUR_OF_DAY_ZERO_PADDED.toTokens(), new RubyTimeFormatToken.Immediate(':'), MINUTE_OF_HOUR.toTokens(), new RubyTimeFormatToken.Immediate(':'), SECOND_OF_MINUTE.toTokens());
        testFormat("%X", HOUR_OF_DAY_ZERO_PADDED.toTokens(), new RubyTimeFormatToken.Immediate(':'), MINUTE_OF_HOUR.toTokens(), new RubyTimeFormatToken.Immediate(':'), SECOND_OF_MINUTE.toTokens());
        testFormat("%t", new RubyTimeFormatToken.Immediate('\t'));
        testFormat("%v", DAY_OF_MONTH_BLANK_PADDED.toTokens(), new RubyTimeFormatToken.Immediate('-'), MONTH_OF_YEAR_ABBREVIATED_NAME.toTokens(), new RubyTimeFormatToken.Immediate('-'), YEAR_WITH_CENTURY.toTokens());
        testFormat("%+", DAY_OF_WEEK_ABBREVIATED_NAME.toTokens(), new RubyTimeFormatToken.Immediate(' '), MONTH_OF_YEAR_ABBREVIATED_NAME.toTokens(), new RubyTimeFormatToken.Immediate(' '), DAY_OF_MONTH_BLANK_PADDED.toTokens(), new RubyTimeFormatToken.Immediate(' '), HOUR_OF_DAY_ZERO_PADDED.toTokens(), new RubyTimeFormatToken.Immediate(':'), MINUTE_OF_HOUR.toTokens(), new RubyTimeFormatToken.Immediate(':'), SECOND_OF_MINUTE.toTokens(), new RubyTimeFormatToken.Immediate(' '), TIME_ZONE_NAME.toTokens(), new RubyTimeFormatToken.Immediate(' '), YEAR_WITH_CENTURY.toTokens());
    }

    @Test
    public void testExtended() {
        testFormat("%EC", CENTURY.toTokens());
        testFormat("%Oy", YEAR_WITHOUT_CENTURY.toTokens());
        testFormat("%:z", TIME_OFFSET.toTokens());
        testFormat("%::z", TIME_OFFSET.toTokens());
        testFormat("%:::z", TIME_OFFSET.toTokens());
    }

    @Test
    public void testPercents() {
        testFormat("%", new RubyTimeFormatToken.Immediate('%'));
        testFormat("%%", new RubyTimeFormatToken.Immediate('%'));
        // Split into two "%" tokens for some internal reasons.
        testFormat("%%%", new RubyTimeFormatToken.Immediate('%'), new RubyTimeFormatToken.Immediate('%'));
        testFormat("%%%%", new RubyTimeFormatToken.Immediate('%'), new RubyTimeFormatToken.Immediate('%'));
    }

    @Test
    public void testOrdinary() {
        testFormat("abc123", new RubyTimeFormatToken.Immediate("abc123"));
    }

    @Test
    public void testPercentButOrdinary() {
        testFormat("%f", new RubyTimeFormatToken.Immediate("%f"));
        testFormat("%Ed", new RubyTimeFormatToken.Immediate("%Ed"));
        testFormat("%OY", new RubyTimeFormatToken.Immediate("%OY"));
        testFormat("%::::z", new RubyTimeFormatToken.Immediate("%::::z"));
    }

    @Test
    public void testSpecifiersAndOrdinary() {
        testFormat("ab%Out%Expose", new RubyTimeFormatToken.Immediate("ab"), DAY_OF_WEEK_STARTING_WITH_MONDAY_1.toTokens(), new RubyTimeFormatToken.Immediate("t"), MONTH_OF_YEAR.toTokens(), new RubyTimeFormatToken.Immediate('/'), DAY_OF_MONTH_ZERO_PADDED.toTokens(), new RubyTimeFormatToken.Immediate('/'), YEAR_WITHOUT_CENTURY.toTokens(), new RubyTimeFormatToken.Immediate("pose"));
    }

    @Test
    public void testRubyTestPatterns() {
        testFormat("%Y-%m-%dT%H:%M:%S", YEAR_WITH_CENTURY.toTokens(), new RubyTimeFormatToken.Immediate('-'), MONTH_OF_YEAR.toTokens(), new RubyTimeFormatToken.Immediate('-'), DAY_OF_MONTH_ZERO_PADDED.toTokens(), new RubyTimeFormatToken.Immediate('T'), HOUR_OF_DAY_ZERO_PADDED.toTokens(), new RubyTimeFormatToken.Immediate(':'), MINUTE_OF_HOUR.toTokens(), new RubyTimeFormatToken.Immediate(':'), SECOND_OF_MINUTE.toTokens());
        testFormat("%d-%b-%y", DAY_OF_MONTH_ZERO_PADDED.toTokens(), new RubyTimeFormatToken.Immediate('-'), MONTH_OF_YEAR_ABBREVIATED_NAME.toTokens(), new RubyTimeFormatToken.Immediate('-'), YEAR_WITHOUT_CENTURY.toTokens());
        testFormat("%A %B %d %y", DAY_OF_WEEK_FULL_NAME.toTokens(), new RubyTimeFormatToken.Immediate(' '), MONTH_OF_YEAR_FULL_NAME.toTokens(), new RubyTimeFormatToken.Immediate(' '), DAY_OF_MONTH_ZERO_PADDED.toTokens(), new RubyTimeFormatToken.Immediate(' '), YEAR_WITHOUT_CENTURY.toTokens());
        testFormat("%B %d, %y", MONTH_OF_YEAR_FULL_NAME.toTokens(), new RubyTimeFormatToken.Immediate(' '), DAY_OF_MONTH_ZERO_PADDED.toTokens(), new RubyTimeFormatToken.Immediate(", "), YEAR_WITHOUT_CENTURY.toTokens());
        testFormat("%B%t%d,%n%y", MONTH_OF_YEAR_FULL_NAME.toTokens(), new RubyTimeFormatToken.Immediate('\t'), DAY_OF_MONTH_ZERO_PADDED.toTokens(), new RubyTimeFormatToken.Immediate(','), new RubyTimeFormatToken.Immediate('\n'), YEAR_WITHOUT_CENTURY.toTokens());
        testFormat("%I:%M:%S %p", HOUR_OF_AMPM_ZERO_PADDED.toTokens(), new RubyTimeFormatToken.Immediate(':'), MINUTE_OF_HOUR.toTokens(), new RubyTimeFormatToken.Immediate(':'), SECOND_OF_MINUTE.toTokens(), new RubyTimeFormatToken.Immediate(' '), AMPM_OF_DAY_UPPER_CASE.toTokens());
        testFormat("%I:%M:%S %p %Z", HOUR_OF_AMPM_ZERO_PADDED.toTokens(), new RubyTimeFormatToken.Immediate(':'), MINUTE_OF_HOUR.toTokens(), new RubyTimeFormatToken.Immediate(':'), SECOND_OF_MINUTE.toTokens(), new RubyTimeFormatToken.Immediate(' '), AMPM_OF_DAY_UPPER_CASE.toTokens(), new RubyTimeFormatToken.Immediate(' '), TIME_ZONE_NAME.toTokens());
        testFormat("%a%d%b%y%H%p%Z", DAY_OF_WEEK_ABBREVIATED_NAME.toTokens(), DAY_OF_MONTH_ZERO_PADDED.toTokens(), MONTH_OF_YEAR_ABBREVIATED_NAME.toTokens(), YEAR_WITHOUT_CENTURY.toTokens(), HOUR_OF_DAY_ZERO_PADDED.toTokens(), AMPM_OF_DAY_UPPER_CASE.toTokens(), TIME_ZONE_NAME.toTokens());
        testFormat("%Y9%m9%d", YEAR_WITH_CENTURY.toTokens(), new RubyTimeFormatToken.Immediate('9'), MONTH_OF_YEAR.toTokens(), new RubyTimeFormatToken.Immediate('9'), DAY_OF_MONTH_ZERO_PADDED.toTokens());
        testFormat("%k%M%S", HOUR_OF_DAY_BLANK_PADDED.toTokens(), MINUTE_OF_HOUR.toTokens(), SECOND_OF_MINUTE.toTokens());
        testFormat("%l%M%S", HOUR_OF_AMPM_BLANK_PADDED.toTokens(), MINUTE_OF_HOUR.toTokens(), SECOND_OF_MINUTE.toTokens());
        testFormat("%Y.", YEAR_WITH_CENTURY.toTokens(), new RubyTimeFormatToken.Immediate('.'));
        testFormat("%Y. ", YEAR_WITH_CENTURY.toTokens(), new RubyTimeFormatToken.Immediate(". "));
        testFormat("%Y-%m-%d", YEAR_WITH_CENTURY.toTokens(), new RubyTimeFormatToken.Immediate('-'), MONTH_OF_YEAR.toTokens(), new RubyTimeFormatToken.Immediate('-'), DAY_OF_MONTH_ZERO_PADDED.toTokens());
        testFormat("%Y-%m-%e", YEAR_WITH_CENTURY.toTokens(), new RubyTimeFormatToken.Immediate('-'), MONTH_OF_YEAR.toTokens(), new RubyTimeFormatToken.Immediate('-'), DAY_OF_MONTH_BLANK_PADDED.toTokens());
        testFormat("%Y-%j", YEAR_WITH_CENTURY.toTokens(), new RubyTimeFormatToken.Immediate('-'), DAY_OF_YEAR.toTokens());
        testFormat("%H:%M:%S", HOUR_OF_DAY_ZERO_PADDED.toTokens(), new RubyTimeFormatToken.Immediate(':'), MINUTE_OF_HOUR.toTokens(), new RubyTimeFormatToken.Immediate(':'), SECOND_OF_MINUTE.toTokens());
        testFormat("%k:%M:%S", HOUR_OF_DAY_BLANK_PADDED.toTokens(), new RubyTimeFormatToken.Immediate(':'), MINUTE_OF_HOUR.toTokens(), new RubyTimeFormatToken.Immediate(':'), SECOND_OF_MINUTE.toTokens());
        testFormat("%A,", DAY_OF_WEEK_FULL_NAME.toTokens(), new RubyTimeFormatToken.Immediate(','));
        testFormat("%B,", MONTH_OF_YEAR_FULL_NAME.toTokens(), new RubyTimeFormatToken.Immediate(','));
        testFormat("%FT%T%Z", YEAR_WITH_CENTURY.toTokens(), new RubyTimeFormatToken.Immediate('-'), MONTH_OF_YEAR.toTokens(), new RubyTimeFormatToken.Immediate('-'), DAY_OF_MONTH_ZERO_PADDED.toTokens(), new RubyTimeFormatToken.Immediate('T'), HOUR_OF_DAY_ZERO_PADDED.toTokens(), new RubyTimeFormatToken.Immediate(':'), MINUTE_OF_HOUR.toTokens(), new RubyTimeFormatToken.Immediate(':'), SECOND_OF_MINUTE.toTokens(), TIME_ZONE_NAME.toTokens());
        testFormat("%FT%T.%N%Z", YEAR_WITH_CENTURY.toTokens(), new RubyTimeFormatToken.Immediate('-'), MONTH_OF_YEAR.toTokens(), new RubyTimeFormatToken.Immediate('-'), DAY_OF_MONTH_ZERO_PADDED.toTokens(), new RubyTimeFormatToken.Immediate('T'), HOUR_OF_DAY_ZERO_PADDED.toTokens(), new RubyTimeFormatToken.Immediate(':'), MINUTE_OF_HOUR.toTokens(), new RubyTimeFormatToken.Immediate(':'), SECOND_OF_MINUTE.toTokens(), new RubyTimeFormatToken.Immediate('.'), NANO_OF_SECOND.toTokens(), TIME_ZONE_NAME.toTokens());
    }
}

