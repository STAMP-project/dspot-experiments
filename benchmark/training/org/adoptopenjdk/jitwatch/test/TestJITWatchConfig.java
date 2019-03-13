/**
 * Copyright (c) 2013-2015 Chris Newland.
 * Licensed under https://github.com/AdoptOpenJDK/jitwatch/blob/master/LICENSE-BSD
 * Instructions: https://github.com/AdoptOpenJDK/jitwatch/wiki
 */
package org.adoptopenjdk.jitwatch.test;


import JITWatchConstants.S_PROFILE_DEFAULT;
import JITWatchConstants.S_PROFILE_SANDBOX;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.adoptopenjdk.jitwatch.core.JITWatchConfig;
import org.junit.Assert;
import org.junit.Test;


public class TestJITWatchConfig {
    private String testConfigFilename;

    @Test
    public void testConfigOnlyBuiltInProfiles() {
        JITWatchConfig config = new JITWatchConfig(new File(testConfigFilename));
        Set<String> configNames = config.getProfileNames();
        Assert.assertEquals(2, configNames.size());
        Assert.assertTrue(configNames.contains(S_PROFILE_DEFAULT));
        Assert.assertTrue(configNames.contains(S_PROFILE_SANDBOX));
        List<String> sourcesList = config.getSourceLocations();
        Assert.assertEquals(0, sourcesList.size());
    }

    @Test
    public void testEmptyConfigSaveReload() {
        JITWatchConfig config = new JITWatchConfig(new File(testConfigFilename));
        String foo = "foo";
        List<String> sourcesList = new ArrayList<String>();
        sourcesList.add(foo);
        config.setSourceLocations(sourcesList);
        config.marshalConfigToProperties();
        config.savePropertiesToFile();
        config = new JITWatchConfig(new File(testConfigFilename));
        List<String> retrievedSourcesList = config.getSourceLocations();
        Assert.assertEquals(1, retrievedSourcesList.size());
        Assert.assertTrue(retrievedSourcesList.contains(foo));
        Set<String> configNames = config.getProfileNames();
        Assert.assertEquals(2, configNames.size());
        Assert.assertTrue(configNames.contains(S_PROFILE_DEFAULT));
        Assert.assertTrue(configNames.contains(S_PROFILE_SANDBOX));
        config.setProfileName("MISSING");
        List<String> retrievedSourcesList2 = config.getSourceLocations();
        Assert.assertEquals(0, retrievedSourcesList2.size());
        Assert.assertFalse(retrievedSourcesList2.contains(foo));
    }

    @Test
    public void testSwitchBetweenDefaultAndUserProfiles() {
        JITWatchConfig config = new JITWatchConfig(new File(testConfigFilename));
        String foo = "foo";
        List<String> sourcesListFoo = new ArrayList<String>();
        sourcesListFoo.add(foo);
        config.setSourceLocations(sourcesListFoo);
        config.marshalConfigToProperties();
        config.savePropertiesToFile();
        config = new JITWatchConfig(new File(testConfigFilename));
        List<String> retrievedSourcesList = config.getSourceLocations();
        Assert.assertEquals(1, retrievedSourcesList.size());
        Assert.assertTrue(retrievedSourcesList.contains(foo));
        Set<String> configNames = config.getProfileNames();
        Assert.assertEquals(2, configNames.size());
        Assert.assertTrue(configNames.contains(S_PROFILE_DEFAULT));
        Assert.assertTrue(configNames.contains(S_PROFILE_SANDBOX));
        String secondProfileName = "Spaceship";
        config.setProfileName(secondProfileName);
        List<String> retrievedSourcesList2 = config.getSourceLocations();
        Assert.assertEquals(0, retrievedSourcesList2.size());
        String bar = "bar";
        List<String> sourcesListBar = new ArrayList<String>();
        sourcesListBar.add(bar);
        config.setSourceLocations(sourcesListBar);
        config.saveConfig();
        Assert.assertEquals(secondProfileName, config.getProfileName());
        configNames = config.getProfileNames();
        Assert.assertEquals(3, configNames.size());
        Assert.assertTrue(configNames.contains(S_PROFILE_DEFAULT));
        Assert.assertTrue(configNames.contains(S_PROFILE_SANDBOX));
        Assert.assertTrue(configNames.contains(secondProfileName));
        config.setProfileName(S_PROFILE_DEFAULT);
        Assert.assertEquals(S_PROFILE_DEFAULT, config.getProfileName());
        retrievedSourcesList = config.getSourceLocations();
        Assert.assertEquals(1, retrievedSourcesList.size());
        Assert.assertTrue(retrievedSourcesList.contains(foo));
        config.setProfileName(secondProfileName);
        retrievedSourcesList2 = config.getSourceLocations();
        Assert.assertEquals(1, retrievedSourcesList2.size());
        Assert.assertTrue(retrievedSourcesList2.contains(bar));
    }

    @Test
    public void testMakeCustomProfileThenDeleteIt() {
        JITWatchConfig config = new JITWatchConfig(new File(testConfigFilename));
        String foo = "foo";
        List<String> sourcesListFoo = new ArrayList<String>();
        sourcesListFoo.add(foo);
        config.setSourceLocations(sourcesListFoo);
        config.marshalConfigToProperties();
        config.savePropertiesToFile();
        config = new JITWatchConfig(new File(testConfigFilename));
        List<String> retrievedSourcesList = config.getSourceLocations();
        Assert.assertEquals(1, retrievedSourcesList.size());
        Assert.assertTrue(retrievedSourcesList.contains(foo));
        Set<String> configNames = config.getProfileNames();
        Assert.assertEquals(2, configNames.size());
        Assert.assertTrue(configNames.contains(S_PROFILE_DEFAULT));
        Assert.assertTrue(configNames.contains(S_PROFILE_SANDBOX));
        String customProfileName = "Spaceship";
        config.setProfileName(customProfileName);
        List<String> retrievedSourcesList2 = config.getSourceLocations();
        Assert.assertEquals(0, retrievedSourcesList2.size());
        String bar = "bar";
        List<String> sourcesListBar = new ArrayList<String>();
        sourcesListBar.add(bar);
        config.setSourceLocations(sourcesListBar);
        config.saveConfig();
        Assert.assertEquals(customProfileName, config.getProfileName());
        configNames = config.getProfileNames();
        Assert.assertEquals(3, configNames.size());
        Assert.assertTrue(configNames.contains(S_PROFILE_DEFAULT));
        Assert.assertTrue(configNames.contains(S_PROFILE_SANDBOX));
        Assert.assertTrue(configNames.contains(customProfileName));
        config.deleteProfile(customProfileName);
        configNames = config.getProfileNames();
        Assert.assertEquals(2, configNames.size());
        Assert.assertTrue(configNames.contains(S_PROFILE_DEFAULT));
        Assert.assertTrue(configNames.contains(S_PROFILE_SANDBOX));
        Assert.assertFalse(configNames.contains(customProfileName));
        Assert.assertEquals(S_PROFILE_DEFAULT, config.getProfileName());
        retrievedSourcesList = config.getSourceLocations();
        Assert.assertEquals(1, retrievedSourcesList.size());
        Assert.assertTrue(retrievedSourcesList.contains(foo));
    }
}

