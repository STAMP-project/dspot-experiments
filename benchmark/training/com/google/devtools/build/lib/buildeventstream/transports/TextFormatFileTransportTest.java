/**
 * Copyright 2016 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.buildeventstream.transports;


import com.google.common.base.Joiner;
import com.google.devtools.build.lib.buildeventstream.ArtifactGroupNamer;
import com.google.devtools.build.lib.buildeventstream.BuildEvent;
import com.google.devtools.build.lib.buildeventstream.BuildEventContext;
import com.google.devtools.build.lib.buildeventstream.BuildEventProtocolOptions;
import com.google.devtools.build.lib.buildeventstream.BuildEventServiceAbruptExitCallback;
import com.google.devtools.build.lib.buildeventstream.BuildEventStreamProtos;
import com.google.devtools.build.lib.buildeventstream.BuildEventStreamProtos.BuildStarted;
import com.google.devtools.build.lib.buildeventstream.BuildEventStreamProtos.Progress;
import com.google.devtools.build.lib.buildeventstream.BuildEventStreamProtos.TargetComplete;
import com.google.devtools.build.lib.buildeventstream.LocalFilesArtifactUploader;
import com.google.devtools.build.lib.buildeventstream.PathConverter;
import com.google.devtools.common.options.Options;
import com.google.protobuf.TextFormat;
import java.io.BufferedOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static com.google.common.io.Files.readLines;


/**
 * Tests {@link TextFormatFileTransport}. *
 */
@RunWith(JUnit4.class)
public class TextFormatFileTransportTest {
    private final BuildEventProtocolOptions defaultOpts = Options.getDefaults(BuildEventProtocolOptions.class);

    private static final BuildEventServiceAbruptExitCallback NO_OP_EXIT_CALLBACK = ( e) -> {
    };

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Mock
    public BuildEvent buildEvent;

    @Mock
    public PathConverter pathConverter;

    @Mock
    public ArtifactGroupNamer artifactGroupNamer;

    @Test
    public void testCreatesFileAndWritesProtoTextFormat() throws Exception {
        File output = tmp.newFile();
        BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(Paths.get(output.getAbsolutePath())));
        BuildEventStreamProtos.BuildEvent started = BuildEventStreamProtos.BuildEvent.newBuilder().setStarted(BuildStarted.newBuilder().setCommand("build")).build();
        Mockito.when(buildEvent.asStreamProto(Matchers.<BuildEventContext>any())).thenReturn(started);
        TextFormatFileTransport transport = new TextFormatFileTransport(outputStream, defaultOpts, new LocalFilesArtifactUploader(), TextFormatFileTransportTest.NO_OP_EXIT_CALLBACK, artifactGroupNamer);
        transport.sendBuildEvent(buildEvent);
        BuildEventStreamProtos.BuildEvent progress = BuildEventStreamProtos.BuildEvent.newBuilder().setProgress(Progress.newBuilder()).build();
        Mockito.when(buildEvent.asStreamProto(Matchers.<BuildEventContext>any())).thenReturn(progress);
        transport.sendBuildEvent(buildEvent);
        BuildEventStreamProtos.BuildEvent completed = BuildEventStreamProtos.BuildEvent.newBuilder().setCompleted(TargetComplete.newBuilder().setSuccess(true)).build();
        Mockito.when(buildEvent.asStreamProto(Matchers.<BuildEventContext>any())).thenReturn(completed);
        transport.sendBuildEvent(buildEvent);
        transport.close().get();
        String contents = TextFormatFileTransportTest.trimLines(Joiner.on(System.lineSeparator()).join(readLines(output, StandardCharsets.UTF_8)));
        assertThat(contents).contains(TextFormatFileTransportTest.trimLines(TextFormat.printToString(started)));
        assertThat(contents).contains(TextFormatFileTransportTest.trimLines(TextFormat.printToString(progress)));
        assertThat(contents).contains(TextFormatFileTransportTest.trimLines(TextFormat.printToString(completed)));
    }
}

