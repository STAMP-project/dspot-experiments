/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.runtime;


import PriorityCategory.COMMAND_LINE;
import PriorityCategory.INVOCATION_POLICY;
import PriorityCategory.RC_FILE;
import SectionTypeCase.CHUNK_LIST;
import SectionTypeCase.OPTION_LIST;
import com.google.common.collect.ImmutableList;
import com.google.common.io.BaseEncoding;
import com.google.devtools.build.lib.bazel.BazelStartupOptionsModule.Options;
import com.google.devtools.build.lib.buildeventstream.BuildEventStreamProtos.BuildEventId.StructuredCommandLineId;
import com.google.devtools.build.lib.runtime.CommandLineEvent.ToolCommandLineEvent;
import com.google.devtools.build.lib.runtime.proto.CommandLineOuterClass.ChunkList;
import com.google.devtools.build.lib.runtime.proto.CommandLineOuterClass.CommandLine;
import com.google.devtools.build.lib.runtime.proto.CommandLineOuterClass.CommandLineSection;
import com.google.devtools.build.lib.runtime.proto.CommandLineOuterClass.OptionList;
import com.google.devtools.common.options.OptionsParser;
import com.google.devtools.common.options.OptionsParsingException;
import com.google.devtools.common.options.TestOptions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link CommandLineEvent}'s construction of the command lines.
 */
@RunWith(JUnit4.class)
public class CommandLineEventTest {
    @Test
    public void testMostlyEmpty_OriginalCommandLine() {
        OptionsParser fakeStartupOptions = OptionsParser.newOptionsParser(BlazeServerStartupOptions.class);
        OptionsParser fakeCommandOptions = OptionsParser.newOptionsParser(TestOptions.class);
        CommandLine line = asStreamProto(null).getStructuredCommandLine();
        assertThat(line).isNotNull();
        assertThat(line.getCommandLineLabel()).isEqualTo("original");
        checkCommandLineSectionLabels(line);
        assertThat(line.getSections(0).getChunkList().getChunk(0)).isEqualTo("testblaze");
        assertThat(line.getSections(1).getOptionList().getOptionCount()).isEqualTo(0);
        assertThat(line.getSections(2).getChunkList().getChunk(0)).isEqualTo("someCommandName");
        assertThat(line.getSections(3).getOptionList().getOptionCount()).isEqualTo(0);
        assertThat(line.getSections(4).getChunkList().getChunkCount()).isEqualTo(0);
    }

    @Test
    public void testMostlyEmpty_CanonicalCommandLine() {
        OptionsParser fakeStartupOptions = OptionsParser.newOptionsParser(BlazeServerStartupOptions.class);
        OptionsParser fakeCommandOptions = OptionsParser.newOptionsParser(TestOptions.class);
        CommandLine line = asStreamProto(null).getStructuredCommandLine();
        assertThat(line).isNotNull();
        assertThat(line.getCommandLineLabel()).isEqualTo("canonical");
        checkCommandLineSectionLabels(line);
        assertThat(line.getSections(0).getChunkList().getChunk(0)).isEqualTo("testblaze");
        assertThat(line.getSections(1).getOptionList().getOptionCount()).isEqualTo(1);
        assertThat(line.getSections(1).getOptionList().getOption(0).getCombinedForm()).isEqualTo("--ignore_all_rc_files");
        assertThat(line.getSections(2).getChunkList().getChunk(0)).isEqualTo("someCommandName");
        assertThat(line.getSections(3).getOptionList().getOptionCount()).isEqualTo(0);
        assertThat(line.getSections(4).getChunkList().getChunkCount()).isEqualTo(0);
    }

    @Test
    public void testActiveBazelrcs_OriginalCommandLine() throws OptionsParsingException {
        OptionsParser fakeStartupOptions = OptionsParser.newOptionsParser(BlazeServerStartupOptions.class, Options.class);
        fakeStartupOptions.parse("--bazelrc=/some/path", "--master_bazelrc", "--bazelrc", "/some/other/path");
        OptionsParser fakeCommandOptions = OptionsParser.newOptionsParser(TestOptions.class);
        CommandLine line = asStreamProto(null).getStructuredCommandLine();
        assertThat(line).isNotNull();
        assertThat(line.getCommandLineLabel()).isEqualTo("original");
        checkCommandLineSectionLabels(line);
        // Expect the provided rc-related startup options are correctly listed
        assertThat(line.getSections(0).getChunkList().getChunk(0)).isEqualTo("testblaze");
        assertThat(line.getSections(1).getOptionList().getOptionCount()).isEqualTo(3);
        assertThat(line.getSections(1).getOptionList().getOption(0).getCombinedForm()).isEqualTo("--bazelrc=/some/path");
        assertThat(line.getSections(1).getOptionList().getOption(1).getCombinedForm()).isEqualTo("--master_bazelrc");
        assertThat(line.getSections(1).getOptionList().getOption(2).getCombinedForm()).isEqualTo("--bazelrc /some/other/path");
        assertThat(line.getSections(2).getChunkList().getChunk(0)).isEqualTo("someCommandName");
        assertThat(line.getSections(3).getOptionList().getOptionCount()).isEqualTo(0);
        assertThat(line.getSections(4).getChunkList().getChunkCount()).isEqualTo(0);
    }

    @Test
    public void testPassedInBazelrcs_OriginalCommandLine() throws OptionsParsingException {
        OptionsParser fakeStartupOptions = OptionsParser.newOptionsParser(BlazeServerStartupOptions.class, Options.class);
        OptionsParser fakeCommandOptions = OptionsParser.newOptionsParser(TestOptions.class);
        CommandLine line = asStreamProto(null).getStructuredCommandLine();
        assertThat(line).isNotNull();
        assertThat(line.getCommandLineLabel()).isEqualTo("original");
        checkCommandLineSectionLabels(line);
        // Expect the provided rc-related startup options are correctly listed
        assertThat(line.getSections(0).getChunkList().getChunk(0)).isEqualTo("testblaze");
        assertThat(line.getSections(1).getOptionList().getOptionCount()).isEqualTo(4);
        assertThat(line.getSections(1).getOptionList().getOption(0).getCombinedForm()).isEqualTo("--bazelrc=/some/path");
        assertThat(line.getSections(1).getOptionList().getOption(1).getCombinedForm()).isEqualTo("--master_bazelrc");
        assertThat(line.getSections(1).getOptionList().getOption(2).getCombinedForm()).isEqualTo("--bazelrc=/some/other/path");
        assertThat(line.getSections(1).getOptionList().getOption(3).getCombinedForm()).isEqualTo("--invocation_policy=notARealPolicy");
        assertThat(line.getSections(2).getChunkList().getChunk(0)).isEqualTo("someCommandName");
        assertThat(line.getSections(3).getOptionList().getOptionCount()).isEqualTo(0);
        assertThat(line.getSections(4).getChunkList().getChunkCount()).isEqualTo(0);
    }

    @Test
    public void testBazelrcs_CanonicalCommandLine() throws OptionsParsingException {
        OptionsParser fakeStartupOptions = OptionsParser.newOptionsParser(BlazeServerStartupOptions.class, Options.class);
        fakeStartupOptions.parse("--bazelrc=/some/path", "--master_bazelrc", "--bazelrc", "/some/other/path");
        OptionsParser fakeCommandOptions = OptionsParser.newOptionsParser(TestOptions.class);
        CommandLine line = asStreamProto(null).getStructuredCommandLine();
        assertThat(line).isNotNull();
        assertThat(line.getCommandLineLabel()).isEqualTo("canonical");
        checkCommandLineSectionLabels(line);
        // Expect the provided rc-related startup options are removed and replaced with the
        // rc-prevention options.
        assertThat(line.getSections(0).getChunkList().getChunk(0)).isEqualTo("testblaze");
        assertThat(line.getSections(1).getOptionList().getOptionCount()).isEqualTo(1);
        assertThat(line.getSections(1).getOptionList().getOption(0).getCombinedForm()).isEqualTo("--ignore_all_rc_files");
        assertThat(line.getSections(2).getChunkList().getChunk(0)).isEqualTo("someCommandName");
        assertThat(line.getSections(3).getOptionList().getOptionCount()).isEqualTo(0);
        assertThat(line.getSections(4).getChunkList().getChunkCount()).isEqualTo(0);
    }

    @Test
    public void testOptionsAtVariousPriorities_OriginalCommandLine() throws OptionsParsingException {
        OptionsParser fakeStartupOptions = OptionsParser.newOptionsParser(BlazeServerStartupOptions.class);
        OptionsParser fakeCommandOptions = OptionsParser.newOptionsParser(TestOptions.class);
        fakeCommandOptions.parse(COMMAND_LINE, "command line", ImmutableList.of("--test_string=foo", "--test_multiple_string=bar"));
        fakeCommandOptions.parse(INVOCATION_POLICY, "fake invocation policy", ImmutableList.of("--expanded_c=2"));
        fakeCommandOptions.parse(RC_FILE, "fake rc file", ImmutableList.of("--test_multiple_string=baz"));
        CommandLine line = asStreamProto(null).getStructuredCommandLine();
        assertThat(line).isNotNull();
        assertThat(line.getCommandLineLabel()).isEqualTo("original");
        checkCommandLineSectionLabels(line);
        assertThat(line.getSections(0).getChunkList().getChunk(0)).isEqualTo("testblaze");
        assertThat(line.getSections(1).getOptionList().getOptionCount()).isEqualTo(0);
        assertThat(line.getSections(2).getChunkList().getChunk(0)).isEqualTo("someCommandName");
        // Expect the rc file options and invocation policy options to not be listed with the explicit
        // command line options.
        assertThat(line.getSections(3).getOptionList().getOptionCount()).isEqualTo(2);
        assertThat(line.getSections(3).getOptionList().getOption(0).getCombinedForm()).isEqualTo("--test_string=foo");
        assertThat(line.getSections(3).getOptionList().getOption(1).getCombinedForm()).isEqualTo("--test_multiple_string=bar");
        assertThat(line.getSections(4).getChunkList().getChunkCount()).isEqualTo(0);
    }

    @Test
    public void testOptionsAtVariousPriorities_CanonicalCommandLine() throws OptionsParsingException {
        OptionsParser fakeStartupOptions = OptionsParser.newOptionsParser(BlazeServerStartupOptions.class);
        OptionsParser fakeCommandOptions = OptionsParser.newOptionsParser(TestOptions.class);
        fakeCommandOptions.parse(COMMAND_LINE, "command line", ImmutableList.of("--test_string=foo", "--test_multiple_string=bar"));
        fakeCommandOptions.parse(INVOCATION_POLICY, "fake invocation policy", ImmutableList.of("--expanded_c=2"));
        fakeCommandOptions.parse(RC_FILE, "fake rc file", ImmutableList.of("--test_multiple_string=baz"));
        CommandLine line = asStreamProto(null).getStructuredCommandLine();
        assertThat(line).isNotNull();
        assertThat(line.getCommandLineLabel()).isEqualTo("canonical");
        checkCommandLineSectionLabels(line);
        assertThat(line.getSections(0).getChunkList().getChunk(0)).isEqualTo("testblaze");
        assertThat(line.getSections(1).getOptionList().getOptionCount()).isEqualTo(1);
        assertThat(line.getSections(2).getChunkList().getChunk(0)).isEqualTo("someCommandName");
        // In the canonical line, expect the options in priority order.
        assertThat(line.getSections(3).getOptionList().getOptionCount()).isEqualTo(4);
        assertThat(line.getSections(3).getOptionList().getOption(0).getCombinedForm()).isEqualTo("--test_multiple_string=baz");
        assertThat(line.getSections(3).getOptionList().getOption(1).getCombinedForm()).isEqualTo("--test_string=foo");
        assertThat(line.getSections(3).getOptionList().getOption(2).getCombinedForm()).isEqualTo("--test_multiple_string=bar");
        assertThat(line.getSections(3).getOptionList().getOption(3).getCombinedForm()).isEqualTo("--expanded_c=2");
        assertThat(line.getSections(4).getChunkList().getChunkCount()).isEqualTo(0);
    }

    @Test
    public void testExpansionOption_OriginalCommandLine() throws OptionsParsingException {
        OptionsParser fakeStartupOptions = OptionsParser.newOptionsParser(BlazeServerStartupOptions.class);
        OptionsParser fakeCommandOptions = OptionsParser.newOptionsParser(TestOptions.class);
        fakeCommandOptions.parse(COMMAND_LINE, "command line", ImmutableList.of("--test_expansion"));
        CommandLine line = asStreamProto(null).getStructuredCommandLine();
        assertThat(line).isNotNull();
        assertThat(line.getCommandLineLabel()).isEqualTo("original");
        checkCommandLineSectionLabels(line);
        assertThat(line.getSections(0).getChunkList().getChunk(0)).isEqualTo("testblaze");
        assertThat(line.getSections(1).getOptionList().getOptionCount()).isEqualTo(0);
        assertThat(line.getSections(2).getChunkList().getChunk(0)).isEqualTo("someCommandName");
        // Expect the rc file option to not be listed with the explicit command line options.
        assertThat(line.getSections(3).getOptionList().getOptionCount()).isEqualTo(1);
        assertThat(line.getSections(3).getOptionList().getOption(0).getCombinedForm()).isEqualTo("--test_expansion");
        assertThat(line.getSections(4).getChunkList().getChunkCount()).isEqualTo(0);
    }

    @Test
    public void testExpansionOption_CanonicalCommandLine() throws OptionsParsingException {
        OptionsParser fakeStartupOptions = OptionsParser.newOptionsParser(BlazeServerStartupOptions.class);
        OptionsParser fakeCommandOptions = OptionsParser.newOptionsParser(TestOptions.class);
        fakeCommandOptions.parse(COMMAND_LINE, "command line", ImmutableList.of("--test_expansion"));
        CommandLine line = asStreamProto(null).getStructuredCommandLine();
        assertThat(line).isNotNull();
        assertThat(line.getCommandLineLabel()).isEqualTo("canonical");
        checkCommandLineSectionLabels(line);
        assertThat(line.getSections(0).getChunkList().getChunk(0)).isEqualTo("testblaze");
        assertThat(line.getSections(1).getOptionList().getOptionCount()).isEqualTo(1);
        assertThat(line.getSections(2).getChunkList().getChunk(0)).isEqualTo("someCommandName");
        assertThat(line.getSections(3).getOptionList().getOptionCount()).isEqualTo(4);
        assertThat(line.getSections(3).getOptionList().getOption(0).getCombinedForm()).isEqualTo("--noexpanded_a");
        assertThat(line.getSections(3).getOptionList().getOption(1).getCombinedForm()).isEqualTo("--expanded_b=false");
        assertThat(line.getSections(3).getOptionList().getOption(2).getCombinedForm()).isEqualTo("--expanded_c 42");
        assertThat(line.getSections(3).getOptionList().getOption(3).getCombinedForm()).isEqualTo("--expanded_d bar");
        assertThat(line.getSections(4).getChunkList().getChunkCount()).isEqualTo(0);
    }

    @Test
    public void testOptionWithImplicitRequirement_OriginalCommandLine() throws OptionsParsingException {
        OptionsParser fakeStartupOptions = OptionsParser.newOptionsParser(BlazeServerStartupOptions.class);
        OptionsParser fakeCommandOptions = OptionsParser.newOptionsParser(TestOptions.class);
        fakeCommandOptions.parse(COMMAND_LINE, "command line", ImmutableList.of("--test_implicit_requirement=foo"));
        CommandLine line = asStreamProto(null).getStructuredCommandLine();
        assertThat(line).isNotNull();
        assertThat(line.getCommandLineLabel()).isEqualTo("original");
        checkCommandLineSectionLabels(line);
        assertThat(line.getSections(0).getChunkList().getChunk(0)).isEqualTo("testblaze");
        assertThat(line.getSections(1).getOptionList().getOptionCount()).isEqualTo(0);
        assertThat(line.getSections(2).getChunkList().getChunk(0)).isEqualTo("someCommandName");
        assertThat(line.getSections(3).getOptionList().getOptionCount()).isEqualTo(1);
        assertThat(line.getSections(3).getOptionList().getOption(0).getCombinedForm()).isEqualTo("--test_implicit_requirement=foo");
        assertThat(line.getSections(4).getChunkList().getChunkCount()).isEqualTo(0);
    }

    @Test
    public void testOptionWithImplicitRequirement_CanonicalCommandLine() throws OptionsParsingException {
        OptionsParser fakeStartupOptions = OptionsParser.newOptionsParser(BlazeServerStartupOptions.class);
        OptionsParser fakeCommandOptions = OptionsParser.newOptionsParser(TestOptions.class);
        fakeCommandOptions.parse(COMMAND_LINE, "command line", ImmutableList.of("--test_implicit_requirement=foo"));
        CommandLine line = asStreamProto(null).getStructuredCommandLine();
        assertThat(line).isNotNull();
        assertThat(line.getCommandLineLabel()).isEqualTo("canonical");
        checkCommandLineSectionLabels(line);
        // Unlike expansion flags, implicit requirements are not listed separately.
        assertThat(line.getSections(0).getChunkList().getChunk(0)).isEqualTo("testblaze");
        assertThat(line.getSections(1).getOptionList().getOptionCount()).isEqualTo(1);
        assertThat(line.getSections(2).getChunkList().getChunk(0)).isEqualTo("someCommandName");
        assertThat(line.getSections(3).getOptionList().getOptionCount()).isEqualTo(1);
        assertThat(line.getSections(3).getOptionList().getOption(0).getCombinedForm()).isEqualTo("--test_implicit_requirement=foo");
        assertThat(line.getSections(4).getChunkList().getChunkCount()).isEqualTo(0);
    }

    @Test
    public void testDefaultToolCommandLine() throws OptionsParsingException {
        OptionsParser parser = OptionsParser.newOptionsParser(CommonCommandOptions.class);
        ToolCommandLineEvent event = parser.getOptions(CommonCommandOptions.class).toolCommandLine;
        // Test that the actual default value is an empty command line.
        assertThat(event.asStreamProto(null).getStructuredCommandLine()).isEqualTo(CommandLine.getDefaultInstance());
    }

    @Test
    public void testLabelessParsingOfCompiledToolCommandLine() throws OptionsParsingException {
        OptionsParser parser = OptionsParser.newOptionsParser(CommonCommandOptions.class);
        CommandLine original = CommandLine.newBuilder().addSections(CommandLineSection.getDefaultInstance()).build();
        parser.parse(("--experimental_tool_command_line=" + (BaseEncoding.base64().encode(original.toByteArray()))));
        ToolCommandLineEvent event = parser.getOptions(CommonCommandOptions.class).toolCommandLine;
        StructuredCommandLineId id = event.getEventId().asStreamProto().getStructuredCommandLine();
        CommandLine line = event.asStreamProto(null).getStructuredCommandLine();
        assertThat(id.getCommandLineLabel()).isEqualTo("tool");
        assertThat(line.getSectionsCount()).isEqualTo(1);
    }

    @Test
    public void testParsingOfCompiledToolCommandLine() throws OptionsParsingException {
        OptionsParser parser = OptionsParser.newOptionsParser(CommonCommandOptions.class);
        CommandLine original = CommandLine.newBuilder().setCommandLineLabel("something meaningful").addSections(CommandLineSection.newBuilder().setSectionLabel("command").setChunkList(ChunkList.newBuilder().addChunk("aCommand"))).addSections(CommandLineSection.newBuilder().setSectionLabel("someArguments").setChunkList(ChunkList.newBuilder().addChunk("arg1").addChunk("arg2"))).addSections(CommandLineSection.newBuilder().setSectionLabel("someOptions").setOptionList(OptionList.getDefaultInstance())).build();
        parser.parse(("--experimental_tool_command_line=" + (BaseEncoding.base64().encode(original.toByteArray()))));
        ToolCommandLineEvent event = parser.getOptions(CommonCommandOptions.class).toolCommandLine;
        StructuredCommandLineId id = event.getEventId().asStreamProto().getStructuredCommandLine();
        CommandLine line = event.asStreamProto(null).getStructuredCommandLine();
        assertThat(id.getCommandLineLabel()).isEqualTo("tool");
        assertThat(line.getCommandLineLabel()).isEqualTo("something meaningful");
        assertThat(line.getSectionsCount()).isEqualTo(3);
        assertThat(line.getSections(0).getSectionTypeCase()).isEqualTo(CHUNK_LIST);
        assertThat(line.getSections(0).getChunkList().getChunkCount()).isEqualTo(1);
        assertThat(line.getSections(0).getChunkList().getChunk(0)).isEqualTo("aCommand");
        assertThat(line.getSections(1).getSectionTypeCase()).isEqualTo(CHUNK_LIST);
        assertThat(line.getSections(1).getChunkList().getChunkCount()).isEqualTo(2);
        assertThat(line.getSections(1).getChunkList().getChunk(0)).isEqualTo("arg1");
        assertThat(line.getSections(1).getChunkList().getChunk(1)).isEqualTo("arg2");
        assertThat(line.getSections(2).getSectionTypeCase()).isEqualTo(OPTION_LIST);
        assertThat(line.getSections(2).getOptionList().getOptionCount()).isEqualTo(0);
    }

    @Test
    public void testSimpleStringToolCommandLine() throws OptionsParsingException {
        OptionsParser parser = OptionsParser.newOptionsParser(CommonCommandOptions.class);
        parser.parse("--experimental_tool_command_line=The quick brown fox jumps over the lazy dog");
        ToolCommandLineEvent event = parser.getOptions(CommonCommandOptions.class).toolCommandLine;
        StructuredCommandLineId id = event.getEventId().asStreamProto().getStructuredCommandLine();
        CommandLine line = event.asStreamProto(null).getStructuredCommandLine();
        assertThat(id.getCommandLineLabel()).isEqualTo("tool");
        assertThat(line.getCommandLineLabel()).isEqualTo("tool");
        assertThat(line.getSectionsCount()).isEqualTo(1);
        assertThat(line.getSections(0).getSectionTypeCase()).isEqualTo(CHUNK_LIST);
        assertThat(line.getSections(0).getChunkList().getChunk(0)).isEqualTo("The quick brown fox jumps over the lazy dog");
    }
}

