package com.github.dreamhead.moco.mount;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MountToTest {
    @Test
    public void should_get_relative_path_from_uri() {
        MountTo to = new MountTo("/dir");
        Assert.assertThat(to.extract("/dir/filename").get(), CoreMatchers.is("filename"));
    }

    @Test
    public void should_return_null_if_uri_does_not_match() {
        MountTo to = new MountTo("/dir");
        Assert.assertThat(to.extract("/target/filename").isPresent(), CoreMatchers.is(false));
    }

    @Test
    public void should_return_null_if_no_relative_path_found() {
        MountTo to = new MountTo("/dir");
        Assert.assertThat(to.extract("/dir/").isPresent(), CoreMatchers.is(false));
    }
}

