/**
 * Quasar: lightweight threads and actors for the JVM.
 * Copyright (c) 2013-2014, Parallel Universe Software Co. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 3.0
 * as published by the Free Software Foundation.
 */
package co.paralleluniverse.actors;


import Channels.OverflowPolicy;
import co.paralleluniverse.common.test.TestUtil;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.dataflow.Val;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;


/**
 *
 *
 * @author eitan
 */
public class PascalTriangle {
    @Rule
    public TestName name = new TestName();

    @Rule
    public TestRule watchman = TestUtil.WATCHMAN;

    final MailboxConfig mailboxConfig = new MailboxConfig(4, OverflowPolicy.THROW);

    public PascalTriangle() {
    }

    @Test
    public void testPascalSum() throws Exception {
        int maxLevel = 20;// 800

        Val<BigInteger> res = new Val();
        new co.paralleluniverse.fibers.Fiber<Void>(new PascalTriangle.PascalNode(res, 1, BigInteger.ONE, true, null, maxLevel)).start();
        Assert.assertEquals(BigInteger.valueOf(2).pow((maxLevel - 1)), res.get(10, TimeUnit.SECONDS));
    }

    static class PascalNodeMessage {}

    static class RightBrother extends PascalTriangle.PascalNodeMessage {
        final ActorRef<PascalTriangle.PascalNodeMessage> node;

        final BigInteger val;

        public RightBrother(ActorRef<PascalTriangle.PascalNodeMessage> pn, BigInteger result) {
            this.node = pn;
            this.val = result;
        }
    }

    static class Nephew extends PascalTriangle.PascalNodeMessage {
        final ActorRef<PascalTriangle.PascalNodeMessage> node;

        public Nephew(ActorRef<PascalTriangle.PascalNodeMessage> pn) {
            this.node = pn;
        }
    }

    class PascalNode extends BasicActor<PascalTriangle.PascalNodeMessage, Void> {
        final Val<BigInteger> result;

        int level;

        int pos;

        BigInteger val;

        boolean isRight;

        ActorRef<PascalTriangle.PascalNodeMessage> left;

        int maxLevel;

        Val<BigInteger> leftResult = new Val();

        Val<BigInteger> rightResult = new Val();

        public PascalNode(Val<BigInteger> result, int level, BigInteger val, boolean isRight, ActorRef<PascalTriangle.PascalNodeMessage> left, int maxLevel) {
            super(mailboxConfig);
            this.result = result;
            this.level = level;
            this.val = val;
            this.isRight = isRight;
            this.left = left;
            this.maxLevel = maxLevel;
        }

        @Override
        protected Void doRun() throws SuspendExecution, InterruptedException {
            if ((level) == (maxLevel)) {
                result.set(val);
                return null;
            }
            ActorRef<PascalTriangle.PascalNodeMessage> leftChild;
            if ((left) != null) {
                left.send(new PascalTriangle.RightBrother(ref(), val));
                leftChild = receive(PascalTriangle.Nephew.class).node;
            } else
                leftChild = spawn();

            final PascalTriangle.RightBrother rb = (isRight) ? null : receive(PascalTriangle.RightBrother.class);
            final ActorRef<PascalTriangle.PascalNodeMessage> rightChild = new PascalTriangle.PascalNode(rightResult, ((level) + 1), val.add((rb == null ? BigInteger.ZERO : rb.val)), isRight, leftChild, maxLevel).spawn();
            if (rb != null)
                rb.node.send(new PascalTriangle.Nephew(rightChild));

            if ((left) == null)
                result.set(leftResult.get().add(rightResult.get()));
            else
                result.set(rightResult.get());

            return null;
        }
    }
}

