package com.github.dreamhead.moco;


import com.google.common.io.Files;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class MocoEventStandaloneTest extends AbstractMocoStandaloneTest {
    private static final long IDLE = 1200;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void should_fire_event() throws IOException {
        runWithConfiguration("event.json");
        File file = folder.newFile();
        System.setOut(new PrintStream(new FileOutputStream(file)));
        Assert.assertThat(helper.get(remoteUrl("/event")), CoreMatchers.is("post_foo"));
        idle(MocoEventStandaloneTest.IDLE, TimeUnit.MILLISECONDS);
        Assert.assertThat(Files.toString(file, Charset.defaultCharset()), CoreMatchers.containsString("0XCAFEBABE"));
    }

    @Test
    public void should_fire_get_event() throws IOException {
        runWithConfiguration("event.json");
        File file = folder.newFile();
        System.setOut(new PrintStream(new FileOutputStream(file)));
        Assert.assertThat(helper.get(remoteUrl("/get_event")), CoreMatchers.is("get_foo"));
        idle(MocoEventStandaloneTest.IDLE, TimeUnit.MILLISECONDS);
        Assert.assertThat(Files.toString(file, Charset.defaultCharset()), CoreMatchers.containsString("0XCAFEBABE"));
    }

    @Test
    public void should_fire_get_event_with_template() throws IOException {
        runWithConfiguration("event.json");
        File file = folder.newFile();
        System.setOut(new PrintStream(new FileOutputStream(file)));
        Assert.assertThat(helper.get(remoteUrl("/get_event_template")), CoreMatchers.is("get_foo"));
        idle(MocoEventStandaloneTest.IDLE, TimeUnit.MILLISECONDS);
        Assert.assertThat(Files.toString(file, Charset.defaultCharset()), CoreMatchers.containsString("0XCAFEBABE"));
    }

    @Test
    public void should_fire_event_with_unit() throws IOException {
        runWithConfiguration("event.json");
        File file = folder.newFile();
        System.setOut(new PrintStream(new FileOutputStream(file)));
        Assert.assertThat(helper.get(remoteUrl("/event-with-unit")), CoreMatchers.is("post_foo"));
        idle(MocoEventStandaloneTest.IDLE, TimeUnit.MILLISECONDS);
        Assert.assertThat(Files.toString(file, Charset.defaultCharset()), CoreMatchers.containsString("0XCAFEBABE"));
    }

    @Test
    public void should_fire_event_with_post_url_template() throws IOException {
        runWithConfiguration("event.json");
        File file = folder.newFile();
        System.setOut(new PrintStream(new FileOutputStream(file)));
        Assert.assertThat(helper.get(remoteUrl("/post-event-with-template-url")), CoreMatchers.is("post_foo"));
        idle(MocoEventStandaloneTest.IDLE, TimeUnit.MILLISECONDS);
        Assert.assertThat(Files.toString(file, Charset.defaultCharset()), CoreMatchers.containsString("0XCAFEBABE"));
    }

    @Test
    public void should_fire_event_with_post_content_template() throws IOException {
        runWithConfiguration("event.json");
        File file = folder.newFile();
        System.setOut(new PrintStream(new FileOutputStream(file)));
        Assert.assertThat(helper.get(remoteUrl("/post-event-with-template-content")), CoreMatchers.is("post_foo"));
        idle(MocoEventStandaloneTest.IDLE, TimeUnit.MILLISECONDS);
        Assert.assertThat(Files.toString(file, Charset.defaultCharset()), CoreMatchers.containsString("0XCAFEBABE"));
    }

    @Test
    public void should_fire_event_with_post_json() throws IOException {
        runWithConfiguration("event.json");
        File file = folder.newFile();
        System.setOut(new PrintStream(new FileOutputStream(file)));
        Assert.assertThat(helper.get(remoteUrl("/event-with-json-post")), CoreMatchers.is("post_json_foo"));
        idle(MocoEventStandaloneTest.IDLE, TimeUnit.MILLISECONDS);
        Assert.assertThat(Files.toString(file, Charset.defaultCharset()), CoreMatchers.containsString("0XMOCOJSON"));
    }
}

