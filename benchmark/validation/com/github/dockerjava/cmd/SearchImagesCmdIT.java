package com.github.dockerjava.cmd;


import ch.lambdaj.Lambda;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.SearchItem;
import java.util.List;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SearchImagesCmdIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(SearchImagesCmdIT.class);

    @Test
    public void searchImages() throws DockerException {
        List<SearchItem> dockerSearch = dockerRule.getClient().searchImagesCmd("busybox").exec();
        SearchImagesCmdIT.LOG.info("Search returned {}", dockerSearch.toString());
        Matcher matcher = Matchers.hasItem(hasField("name", Matchers.equalTo("busybox")));
        MatcherAssert.assertThat(dockerSearch, matcher);
        MatcherAssert.assertThat(Lambda.filter(hasField("name", Matchers.is("busybox")), dockerSearch).size(), Matchers.equalTo(1));
    }
}

