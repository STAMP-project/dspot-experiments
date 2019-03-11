package com.github.dreamhead.moco.parser;


import com.github.dreamhead.moco.parser.model.GlobalSetting;
import com.google.common.collect.ImmutableList;
import java.io.InputStream;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class GlobalSettingParserTest {
    private GlobalSettingParser parser;

    @Test
    public void should_parse_settings_file() {
        InputStream stream = getResourceAsStream("settings/settings.json");
        ImmutableList<GlobalSetting> globalSettings = parser.parse(stream);
        Assert.assertThat(globalSettings.get(0).includes().get(0), CoreMatchers.is(join("src", "test", "resources", "settings", "details", "foo.json")));
        Assert.assertThat(globalSettings.get(1).includes().get(0), CoreMatchers.is(join("src", "test", "resources", "settings", "details", "bar.json")));
    }

    @Test
    public void should_parse_settings_file_with_context() {
        InputStream stream = getResourceAsStream("settings/context-settings.json");
        ImmutableList<GlobalSetting> globalSettings = parser.parse(stream);
        Assert.assertThat(globalSettings.get(0).includes().get(0), CoreMatchers.is(join("src", "test", "resources", "settings", "details", "foo.json")));
        Assert.assertThat(globalSettings.get(0).getContext(), CoreMatchers.is("/foo"));
        Assert.assertThat(globalSettings.get(1).includes().get(0), CoreMatchers.is(join("src", "test", "resources", "settings", "details", "bar.json")));
        Assert.assertThat(globalSettings.get(1).getContext(), CoreMatchers.is("/bar"));
    }

    @Test
    public void should_parse_setting_file_with_file_root() {
        InputStream stream = getResourceAsStream("settings/fileroot-settings.json");
        ImmutableList<GlobalSetting> globalSettings = parser.parse(stream);
        Assert.assertThat(globalSettings.get(0).includes().get(0), CoreMatchers.is(join("src", "test", "resources", "settings", "fileroot.json")));
        Assert.assertThat(globalSettings.get(0).getContext(), CoreMatchers.is("/fileroot"));
        Assert.assertThat(globalSettings.get(0).getFileRoot(), CoreMatchers.is("src/test/resources"));
    }

    @Test
    public void should_parse_setting_file_with_env() {
        InputStream stream = getResourceAsStream("settings/env-settings.json");
        ImmutableList<GlobalSetting> globalSettings = parser.parse(stream);
        Assert.assertThat(globalSettings.get(0).includes().get(0), CoreMatchers.is(join("src", "test", "resources", "settings", "details", "foo.json")));
        Assert.assertThat(globalSettings.get(0).getContext(), CoreMatchers.is("/foo"));
        Assert.assertThat(globalSettings.get(0).getEnv(), CoreMatchers.is("foo"));
        Assert.assertThat(globalSettings.get(1).includes().get(0), CoreMatchers.is(join("src", "test", "resources", "settings", "details", "bar.json")));
        Assert.assertThat(globalSettings.get(1).getContext(), CoreMatchers.is("/bar"));
        Assert.assertThat(globalSettings.get(1).getEnv(), CoreMatchers.is("bar"));
    }

    @Test
    public void should_parse_glob_settings_file() {
        InputStream stream = getResourceAsStream("settings/glob-settings.json");
        ImmutableList<GlobalSetting> globalSettings = parser.parse(stream);
        ImmutableList<String> includes = globalSettings.get(0).includes();
        Assert.assertThat(includes.contains(join("src", "test", "resources", "settings", "details", "foo.json")), CoreMatchers.is(true));
        Assert.assertThat(includes.contains(join("src", "test", "resources", "settings", "details", "bar.json")), CoreMatchers.is(true));
    }
}

