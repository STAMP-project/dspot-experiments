package com.github.ltsopensource.core.failstore.berkeleydb;


import com.github.ltsopensource.core.commons.utils.CollectionUtils;
import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.core.domain.Pair;
import com.github.ltsopensource.core.failstore.FailStore;
import com.github.ltsopensource.core.failstore.FailStoreException;
import com.github.ltsopensource.core.json.JSON;
import java.util.List;
import org.junit.Test;


/**
 * Robert HG (254963746@qq.com) on 5/26/15.
 */
// @Test
// public void del() throws FailStoreException {
// failStore.delete(key);
// }
public class BerkeleydbFailStoreTest {
    FailStore failStore;

    private String key = "x2x3423412x";

    @Test
    public void put() throws FailStoreException {
        Job job = new Job();
        job.setTaskId("2131232");
        for (int i = 0; i < 100; i++) {
            failStore.put((((key) + "") + i), job);
        }
        System.out.println("??debug?????");
        failStore.close();
    }

    @Test
    public void fetchTop() throws FailStoreException {
        List<Pair<String, Job>> pairs = failStore.fetchTop(5, Job.class);
        if (CollectionUtils.isNotEmpty(pairs)) {
            for (Pair<String, Job> pair : pairs) {
                System.out.println(JSON.toJSONString(pair));
            }
        }
    }
}

