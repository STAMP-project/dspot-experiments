/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.common.options;


import TriState.AUTO;
import TriState.NO;
import TriState.YES;
import com.google.common.testing.EqualsTester;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static OptionDocumentationCategory.UNCATEGORIZED;
import static OptionEffectTag.NO_OP;


/**
 * Test for {@link Options}.
 */
@RunWith(JUnit4.class)
public class OptionsTest {
    private static final String[] NO_ARGS = new String[]{  };

    public static class HttpOptions extends OptionsBase {
        @Option(name = "host", documentationCategory = UNCATEGORIZED, effectTags = { NO_OP }, defaultValue = "www.google.com", help = "The URL at which the server will be running.")
        public String host;

        @Option(name = "port", abbrev = 'p', documentationCategory = UNCATEGORIZED, effectTags = { NO_OP }, defaultValue = "80", help = "The port at which the server will be running.")
        public int port;

        @Option(name = "debug", abbrev = 'd', documentationCategory = UNCATEGORIZED, effectTags = { NO_OP }, defaultValue = "false", help = "debug")
        public boolean isDebugging;

        @Option(name = "tristate", abbrev = 't', documentationCategory = UNCATEGORIZED, effectTags = { NO_OP }, defaultValue = "auto", help = "tri-state option returning auto by default")
        public TriState triState;

        @Option(name = "special", documentationCategory = UNCATEGORIZED, effectTags = { NO_OP }, defaultValue = "null", expansion = { "--host=special.google.com", "--port=8080" })
        public Void special;
    }

    @Test
    public void paragraphFill() throws Exception {
        // TODO(bazel-team): don't include trailing space after last word in line.
        String input = "The quick brown fox jumps over the lazy dog.";
        assertThat(OptionsUsage.paragraphFill(input, 2, 13)).isEqualTo(("  The quick \n  brown fox \n  jumps over \n  the lazy \n" + "  dog."));
        assertThat(OptionsUsage.paragraphFill(input, 3, 19)).isEqualTo("   The quick brown \n   fox jumps over \n   the lazy dog.");
        String input2 = "The quick brown fox jumps\nAnother paragraph.";
        assertThat(OptionsUsage.paragraphFill(input2, 2, 23)).isEqualTo("  The quick brown fox \n  jumps\n  Another paragraph.");
    }

    @Test
    public void getsDefaults() throws OptionsParsingException {
        Options<OptionsTest.HttpOptions> options = Options.parse(OptionsTest.HttpOptions.class, OptionsTest.NO_ARGS);
        String[] remainingArgs = options.getRemainingArgs();
        OptionsTest.HttpOptions webFlags = options.getOptions();
        assertThat(webFlags.host).isEqualTo("www.google.com");
        assertThat(webFlags.port).isEqualTo(80);
        assertThat(webFlags.isDebugging).isFalse();
        assertThat(webFlags.triState).isEqualTo(AUTO);
        assertThat(remainingArgs).hasLength(0);
    }

    @Test
    public void objectMethods() throws OptionsParsingException {
        String[] args = new String[]{ "--host", "foo", "--port", "80" };
        OptionsTest.HttpOptions left = Options.parse(OptionsTest.HttpOptions.class, args).getOptions();
        OptionsTest.HttpOptions likeLeft = Options.parse(OptionsTest.HttpOptions.class, args).getOptions();
        String[] rightArgs = new String[]{ "--host", "other", "--port", "90" };
        OptionsTest.HttpOptions right = Options.parse(OptionsTest.HttpOptions.class, rightArgs).getOptions();
        String toString = left.toString();
        // Don't rely on Set.toString iteration order:
        assertThat(toString).startsWith(("com.google.devtools.common.options.OptionsTest" + "$HttpOptions{"));
        assertThat(toString).contains("host=foo");
        assertThat(toString).contains("port=80");
        assertThat(toString).endsWith("}");
        new EqualsTester().addEqualityGroup(left).testEquals();
        assertThat(left.toString()).isEqualTo(likeLeft.toString());
        assertThat(left).isEqualTo(likeLeft);
        assertThat(likeLeft).isEqualTo(left);
        assertThat(left).isNotEqualTo(right);
        assertThat(right).isNotEqualTo(left);
        assertThat(left).isNotNull();
        assertThat(likeLeft).isNotNull();
        assertThat(likeLeft.hashCode()).isEqualTo(likeLeft.hashCode());
        assertThat(likeLeft.hashCode()).isEqualTo(left.hashCode());
        // Strictly speaking this is not required for hashCode to be correct,
        // but a good hashCode should be different at least for some values. So,
        // we're making sure that at least this particular pair of inputs yields
        // different values.
        assertThat(left.hashCode()).isNotEqualTo(right.hashCode());
    }

    @Test
    public void equals() throws OptionsParsingException {
        String[] args = new String[]{ "--host", "foo", "--port", "80" };
        OptionsTest.HttpOptions options1 = Options.parse(OptionsTest.HttpOptions.class, args).getOptions();
        String[] args2 = new String[]{ "-p", "80", "--host", "foo" };
        OptionsTest.HttpOptions options2 = Options.parse(OptionsTest.HttpOptions.class, args2).getOptions();
        // Order/abbreviations shouldn't matter.
        assertThat(options1).isEqualTo(options2);
        // Explicitly setting a default shouldn't matter.
        assertThat(Options.parse(OptionsTest.HttpOptions.class, "--port", "80").getOptions()).isEqualTo(Options.parse(OptionsTest.HttpOptions.class).getOptions());
        assertThat(Options.parse(OptionsTest.HttpOptions.class, "--port", "3").getOptions()).isNotEqualTo(Options.parse(OptionsTest.HttpOptions.class).getOptions());
    }

    @Test
    public void getsFlagsProvidedInArguments() throws OptionsParsingException {
        String[] args = new String[]{ "--host", "google.com", "-p", "8080"// short form
        , "--debug" };
        Options<OptionsTest.HttpOptions> options = Options.parse(OptionsTest.HttpOptions.class, args);
        String[] remainingArgs = options.getRemainingArgs();
        OptionsTest.HttpOptions webFlags = options.getOptions();
        assertThat(webFlags.host).isEqualTo("google.com");
        assertThat(webFlags.port).isEqualTo(8080);
        assertThat(webFlags.isDebugging).isTrue();
        assertThat(remainingArgs).hasLength(0);
    }

    @Test
    public void getsFlagsProvidedWithEquals() throws OptionsParsingException {
        String[] args = new String[]{ "--host=google.com", "--port=8080", "--debug" };
        Options<OptionsTest.HttpOptions> options = Options.parse(OptionsTest.HttpOptions.class, args);
        String[] remainingArgs = options.getRemainingArgs();
        OptionsTest.HttpOptions webFlags = options.getOptions();
        assertThat(webFlags.host).isEqualTo("google.com");
        assertThat(webFlags.port).isEqualTo(8080);
        assertThat(webFlags.isDebugging).isTrue();
        assertThat(remainingArgs).hasLength(0);
    }

    @Test
    public void booleanNo() throws OptionsParsingException {
        Options<OptionsTest.HttpOptions> options = Options.parse(OptionsTest.HttpOptions.class, new String[]{ "--nodebug", "--notristate" });
        OptionsTest.HttpOptions webFlags = options.getOptions();
        assertThat(webFlags.isDebugging).isFalse();
        assertThat(webFlags.triState).isEqualTo(NO);
    }

    @Test
    public void booleanAbbrevMinus() throws OptionsParsingException {
        Options<OptionsTest.HttpOptions> options = Options.parse(OptionsTest.HttpOptions.class, new String[]{ "-d-", "-t-" });
        OptionsTest.HttpOptions webFlags = options.getOptions();
        assertThat(webFlags.isDebugging).isFalse();
        assertThat(webFlags.triState).isEqualTo(NO);
    }

    @Test
    public void boolean0() throws OptionsParsingException {
        Options<OptionsTest.HttpOptions> options = Options.parse(OptionsTest.HttpOptions.class, new String[]{ "--debug=0", "--tristate=0" });
        OptionsTest.HttpOptions webFlags = options.getOptions();
        assertThat(webFlags.isDebugging).isFalse();
        assertThat(webFlags.triState).isEqualTo(NO);
    }

    @Test
    public void boolean1() throws OptionsParsingException {
        Options<OptionsTest.HttpOptions> options = Options.parse(OptionsTest.HttpOptions.class, new String[]{ "--debug=1", "--tristate=1" });
        OptionsTest.HttpOptions webFlags = options.getOptions();
        assertThat(webFlags.isDebugging).isTrue();
        assertThat(webFlags.triState).isEqualTo(YES);
    }

    @Test
    public void retainsStuffThatsNotOptions() throws OptionsParsingException {
        String[] args = new String[]{ "these", "aint", "options" };
        Options<OptionsTest.HttpOptions> options = Options.parse(OptionsTest.HttpOptions.class, args);
        String[] remainingArgs = options.getRemainingArgs();
        assertThat(Arrays.asList(remainingArgs)).isEqualTo(Arrays.asList(args));
    }

    @Test
    public void retainsStuffThatsNotComplexOptions() throws OptionsParsingException {
        String[] args = new String[]{ "--host", "google.com", "notta", "--port=8080", "option", "--debug=true" };
        String[] notoptions = new String[]{ "notta", "option" };
        Options<OptionsTest.HttpOptions> options = Options.parse(OptionsTest.HttpOptions.class, args);
        String[] remainingArgs = options.getRemainingArgs();
        assertThat(Arrays.asList(remainingArgs)).isEqualTo(Arrays.asList(notoptions));
    }

    @Test
    public void wontParseUnknownOptions() {
        String[] args = new String[]{ "--unknown", "--other=23", "--options" };
        try {
            Options.parse(OptionsTest.HttpOptions.class, args);
            Assert.fail();
        } catch (OptionsParsingException e) {
            assertThat(e).hasMessageThat().isEqualTo("Unrecognized option: --unknown");
        }
    }

    @Test
    public void requiresOptionValue() {
        String[] args = new String[]{ "--port" };
        try {
            Options.parse(OptionsTest.HttpOptions.class, args);
            Assert.fail();
        } catch (OptionsParsingException e) {
            assertThat(e).hasMessageThat().isEqualTo("Expected value after --port");
        }
    }

    @Test
    public void handlesDuplicateOptions_full() throws Exception {
        String[] args = new String[]{ "--port=80", "--port", "81" };
        Options<OptionsTest.HttpOptions> options = Options.parse(OptionsTest.HttpOptions.class, args);
        OptionsTest.HttpOptions webFlags = options.getOptions();
        assertThat(webFlags.port).isEqualTo(81);
    }

    @Test
    public void handlesDuplicateOptions_abbrev() throws Exception {
        String[] args = new String[]{ "--port=80", "-p", "81" };
        Options<OptionsTest.HttpOptions> options = Options.parse(OptionsTest.HttpOptions.class, args);
        OptionsTest.HttpOptions webFlags = options.getOptions();
        assertThat(webFlags.port).isEqualTo(81);
    }

    @Test
    public void duplicateOptionsOkWithSameValues() throws Exception {
        // These would throw OptionsParsingException if they failed.
        Options.parse(OptionsTest.HttpOptions.class, "--port=80", "--port", "80");
        Options.parse(OptionsTest.HttpOptions.class, "--port=80", "-p", "80");
    }

    @Test
    public void isPickyAboutBooleanValues() {
        try {
            Options.parse(OptionsTest.HttpOptions.class, new String[]{ "--debug=not_a_boolean" });
            Assert.fail();
        } catch (OptionsParsingException e) {
            assertThat(e).hasMessageThat().isEqualTo(("While parsing option --debug=not_a_boolean: " + "\'not_a_boolean\' is not a boolean"));
        }
    }

    @Test
    public void isPickyAboutBooleanNos() {
        try {
            Options.parse(OptionsTest.HttpOptions.class, new String[]{ "--nodebug=1" });
            Assert.fail();
        } catch (OptionsParsingException e) {
            assertThat(e).hasMessageThat().isEqualTo("Unexpected value after boolean option: --nodebug=1");
        }
    }

    @Test
    public void usageForBuiltinTypesNoExpansion() {
        String usage = Options.getUsage(OptionsTest.HttpOptions.class);
        // We can't rely on the option ordering.
        assertThat(usage).contains(("  --[no]debug [-d] (a boolean; default: \"false\")\n" + "    debug"));
        assertThat(usage).contains(("  --host (a string; default: \"www.google.com\")\n" + "    The URL at which the server will be running."));
        assertThat(usage).contains(("  --port [-p] (an integer; default: \"80\")\n" + "    The port at which the server will be running."));
        assertThat(usage).contains(("  --[no]tristate [-t] (a tri-state (auto, yes, no); default: \"auto\")\n" + "    tri-state option returning auto by default"));
    }

    @Test
    public void usageForStaticExpansion() {
        String usage = Options.getUsage(OptionsTest.HttpOptions.class);
        assertThat(usage).contains("  --special\n      Expands to: --host=special.google.com --port=8080");
    }

    public static class NullTestOptions extends OptionsBase {
        @Option(name = "host", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "null", help = "The URL at which the server will be running.")
        public String host;

        @Option(name = "none", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "null", expansion = { "--host=www.google.com" }, help = "An expanded option.")
        public Void none;
    }

    @Test
    public void usageForNullDefault() {
        String usage = Options.getUsage(OptionsTest.NullTestOptions.class);
        assertThat(usage).contains(("  --host (a string; default: see description)\n" + "    The URL at which the server will be running."));
        assertThat(usage).contains(("  --none\n" + ("    An expanded option.\n" + "      Expands to: --host=www.google.com")));
    }

    public static class MyURLConverter implements Converter<URL> {
        @Override
        public URL convert(String input) throws OptionsParsingException {
            try {
                return new URL(input);
            } catch (MalformedURLException e) {
                throw new OptionsParsingException(((("Could not convert '" + input) + "': ") + (e.getMessage())));
            }
        }

        @Override
        public String getTypeDescription() {
            return "a url";
        }
    }

    public static class UsesCustomConverter extends OptionsBase {
        @Option(name = "url", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "http://www.google.com/", converter = OptionsTest.MyURLConverter.class)
        public URL url;
    }

    @Test
    public void customConverter() throws Exception {
        Options<OptionsTest.UsesCustomConverter> options = Options.parse(OptionsTest.UsesCustomConverter.class, new String[0]);
        URL expected = new URL("http://www.google.com/");
        assertThat(options.getOptions().url).isEqualTo(expected);
    }

    @Test
    public void customConverterThrowsException() throws Exception {
        String[] args = new String[]{ "--url=a_malformed:url" };
        try {
            Options.parse(OptionsTest.UsesCustomConverter.class, args);
            Assert.fail();
        } catch (OptionsParsingException e) {
            assertThat(e).hasMessageThat().isEqualTo(("While parsing option --url=a_malformed:url: " + ("Could not convert 'a_malformed:url': " + "no protocol: a_malformed:url")));
        }
    }

    @Test
    public void usageWithCustomConverter() {
        assertThat(Options.getUsage(OptionsTest.UsesCustomConverter.class)).isEqualTo("  --url (a url; default: \"http://www.google.com/\")\n");
    }

    @Test
    public void unknownBooleanOption() {
        try {
            Options.parse(OptionsTest.HttpOptions.class, new String[]{ "--no-debug" });
            Assert.fail();
        } catch (OptionsParsingException e) {
            assertThat(e).hasMessageThat().isEqualTo("Unrecognized option: --no-debug");
        }
    }

    public static class J extends OptionsBase {
        @Option(name = "j", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "null")
        public String string;
    }

    @Test
    public void nullDefaultForReferenceTypeOption() throws Exception {
        OptionsTest.J options = Options.parse(OptionsTest.J.class, OptionsTest.NO_ARGS).getOptions();
        assertThat(options.string).isNull();
    }

    @Test
    public void nullIsNotInterpretedSpeciallyExceptAsADefaultValue() throws Exception {
        OptionsTest.HttpOptions options = Options.parse(OptionsTest.HttpOptions.class, new String[]{ "--host", "null" }).getOptions();
        assertThat(options.host).isEqualTo("null");
    }

    @Test
    public void nonDecimalRadicesForIntegerOptions() throws Exception {
        Options<OptionsTest.HttpOptions> options = Options.parse(OptionsTest.HttpOptions.class, new String[]{ "--port", "0x51" });
        assertThat(options.getOptions().port).isEqualTo(81);
    }

    @Test
    public void expansionOptionSimple() throws Exception {
        Options<OptionsTest.HttpOptions> options = Options.parse(OptionsTest.HttpOptions.class, new String[]{ "--special" });
        assertThat(options.getOptions().host).isEqualTo("special.google.com");
        assertThat(options.getOptions().port).isEqualTo(8080);
    }

    @Test
    public void expansionOptionOverride() throws Exception {
        Options<OptionsTest.HttpOptions> options = Options.parse(OptionsTest.HttpOptions.class, new String[]{ "--port=90", "--special", "--host=foo" });
        assertThat(options.getOptions().host).isEqualTo("foo");
        assertThat(options.getOptions().port).isEqualTo(8080);
    }

    @Test
    public void expansionOptionEquals() throws Exception {
        Options<OptionsTest.HttpOptions> options1 = Options.parse(OptionsTest.HttpOptions.class, new String[]{ "--host=special.google.com", "--port=8080" });
        Options<OptionsTest.HttpOptions> options2 = Options.parse(OptionsTest.HttpOptions.class, new String[]{ "--special" });
        assertThat(options1.getOptions()).isEqualTo(options2.getOptions());
    }

    @Test
    public void usageForExpansionFunction() {
        // Expect that the usage text contains the expansion appropriate to the options bases that were
        // loaded into the options parser.
        String usage = Options.getUsage(TestOptions.class);
        assertThat(usage).contains(("  --prefix_expansion\n" + ("    Expands to all options with a specific prefix.\n" + "      Expands to: --specialexp_bar --specialexp_foo")));
    }

    @Test
    public void expansionFunction() throws Exception {
        Options<TestOptions> options1 = Options.parse(TestOptions.class, new String[]{ "--prefix_expansion" });
        Options<TestOptions> options2 = Options.parse(TestOptions.class, new String[]{ "--specialexp_foo", "--specialexp_bar" });
        assertThat(options1.getOptions()).isEqualTo(options2.getOptions());
    }
}

