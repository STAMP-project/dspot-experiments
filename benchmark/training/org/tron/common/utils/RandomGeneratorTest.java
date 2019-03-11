package org.tron.common.utils;


import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.tron.core.capsule.WitnessCapsule;


@Slf4j
@Ignore
public class RandomGeneratorTest {
    @Test
    public void shuffle() {
        final List<WitnessCapsule> witnessCapsuleListBefore = this.getWitnessList();
        logger.info(("updateWitnessSchedule,before: " + (getWitnessStringList(witnessCapsuleListBefore))));
        final List<WitnessCapsule> witnessCapsuleListAfter = new RandomGenerator<WitnessCapsule>().shuffle(witnessCapsuleListBefore, DateTime.now().getMillis());
        logger.info(("updateWitnessSchedule,after: " + (getWitnessStringList(witnessCapsuleListAfter))));
    }
}

