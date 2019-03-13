package com.airbnb.lottie;


import Animator.AnimatorListener;
import com.airbnb.lottie.utils.LottieValueAnimator;
import java.util.concurrent.atomic.AtomicBoolean;
import junit.framework.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class LottieValueAnimatorUnitTest extends BaseTest {
    private interface VerifyListener {
        void verify(InOrder inOrder);
    }

    private LottieComposition composition;

    private LottieValueAnimator animator;

    private AnimatorListener spyListener;

    private InOrder inOrder;

    private AtomicBoolean isDone;

    @Test
    public void testInitialState() {
        LottieValueAnimatorUnitTest.assertClose(0.0F, animator.getFrame());
    }

    @Test
    public void testResumingMaintainsValue() {
        animator.setFrame(500);
        animator.resumeAnimation();
        LottieValueAnimatorUnitTest.assertClose(500.0F, animator.getFrame());
    }

    @Test
    public void testFrameConvertsToAnimatedFraction() {
        animator.setFrame(500);
        animator.resumeAnimation();
        LottieValueAnimatorUnitTest.assertClose(0.5F, animator.getAnimatedFraction());
        LottieValueAnimatorUnitTest.assertClose(0.5F, animator.getAnimatedValueAbsolute());
    }

    @Test
    public void testPlayingResetsValue() {
        animator.setFrame(500);
        animator.playAnimation();
        LottieValueAnimatorUnitTest.assertClose(0.0F, animator.getFrame());
        LottieValueAnimatorUnitTest.assertClose(0.0F, animator.getAnimatedFraction());
    }

    @Test
    public void testReversingMaintainsValue() {
        animator.setFrame(250);
        animator.reverseAnimationSpeed();
        LottieValueAnimatorUnitTest.assertClose(250.0F, animator.getFrame());
        LottieValueAnimatorUnitTest.assertClose(0.75F, animator.getAnimatedFraction());
        LottieValueAnimatorUnitTest.assertClose(0.25F, animator.getAnimatedValueAbsolute());
    }

    @Test
    public void testReversingWithMinValueMaintainsValue() {
        animator.setMinFrame(100);
        animator.setFrame(1000);
        animator.reverseAnimationSpeed();
        LottieValueAnimatorUnitTest.assertClose(1000.0F, animator.getFrame());
        LottieValueAnimatorUnitTest.assertClose(0.0F, animator.getAnimatedFraction());
        LottieValueAnimatorUnitTest.assertClose(1.0F, animator.getAnimatedValueAbsolute());
    }

    @Test
    public void testReversingWithMaxValueMaintainsValue() {
        animator.setMaxFrame(900);
        animator.reverseAnimationSpeed();
        LottieValueAnimatorUnitTest.assertClose(0.0F, animator.getFrame());
        LottieValueAnimatorUnitTest.assertClose(1.0F, animator.getAnimatedFraction());
        LottieValueAnimatorUnitTest.assertClose(0.0F, animator.getAnimatedValueAbsolute());
    }

    @Test
    public void testResumeReversingWithMinValueMaintainsValue() {
        animator.setMaxFrame(900);
        animator.reverseAnimationSpeed();
        animator.resumeAnimation();
        LottieValueAnimatorUnitTest.assertClose(900.0F, animator.getFrame());
        LottieValueAnimatorUnitTest.assertClose(0.0F, animator.getAnimatedFraction());
        LottieValueAnimatorUnitTest.assertClose(0.9F, animator.getAnimatedValueAbsolute());
    }

    @Test
    public void testPlayReversingWithMinValueMaintainsValue() {
        animator.setMaxFrame(900);
        animator.reverseAnimationSpeed();
        animator.playAnimation();
        LottieValueAnimatorUnitTest.assertClose(900.0F, animator.getFrame());
        LottieValueAnimatorUnitTest.assertClose(0.0F, animator.getAnimatedFraction());
        LottieValueAnimatorUnitTest.assertClose(0.9F, animator.getAnimatedValueAbsolute());
    }

    @Test
    public void testMinAndMaxBothSet() {
        animator.setMinFrame(200);
        animator.setMaxFrame(800);
        animator.setFrame(400);
        LottieValueAnimatorUnitTest.assertClose(0.33333F, animator.getAnimatedFraction());
        LottieValueAnimatorUnitTest.assertClose(0.4F, animator.getAnimatedValueAbsolute());
        animator.reverseAnimationSpeed();
        LottieValueAnimatorUnitTest.assertClose(400.0F, animator.getFrame());
        LottieValueAnimatorUnitTest.assertClose(0.66666F, animator.getAnimatedFraction());
        LottieValueAnimatorUnitTest.assertClose(0.4F, animator.getAnimatedValueAbsolute());
        animator.resumeAnimation();
        LottieValueAnimatorUnitTest.assertClose(400.0F, animator.getFrame());
        LottieValueAnimatorUnitTest.assertClose(0.66666F, animator.getAnimatedFraction());
        LottieValueAnimatorUnitTest.assertClose(0.4F, animator.getAnimatedValueAbsolute());
        animator.playAnimation();
        LottieValueAnimatorUnitTest.assertClose(800.0F, animator.getFrame());
        LottieValueAnimatorUnitTest.assertClose(0.0F, animator.getAnimatedFraction());
        LottieValueAnimatorUnitTest.assertClose(0.8F, animator.getAnimatedValueAbsolute());
    }

    @Test
    public void testSetFrameIntegrity() {
        animator.setMinAndMaxFrames(200, 800);
        // setFrame < minFrame should clamp to minFrame
        animator.setFrame(100);
        Assert.assertEquals(200.0F, animator.getFrame());
        animator.setFrame(900);
        Assert.assertEquals(800.0F, animator.getFrame());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMinAndMaxFrameIntegrity() {
        animator.setMinAndMaxFrames(800, 200);
    }

    @Test
    public void testDefaultAnimator() {
        testAnimator(new LottieValueAnimatorUnitTest.VerifyListener() {
            @Override
            public void verify(InOrder inOrder) {
                inOrder.verify(spyListener, Mockito.times(1)).onAnimationStart(animator, false);
                inOrder.verify(spyListener, Mockito.times(1)).onAnimationEnd(animator, false);
                Mockito.verify(spyListener, Mockito.times(0)).onAnimationCancel(animator);
                Mockito.verify(spyListener, Mockito.times(0)).onAnimationRepeat(animator);
            }
        });
    }

    @Test
    public void testReverseAnimator() {
        animator.reverseAnimationSpeed();
        testAnimator(new LottieValueAnimatorUnitTest.VerifyListener() {
            @Override
            public void verify(InOrder inOrder) {
                inOrder.verify(spyListener, Mockito.times(1)).onAnimationStart(animator, true);
                inOrder.verify(spyListener, Mockito.times(1)).onAnimationEnd(animator, true);
                Mockito.verify(spyListener, Mockito.times(0)).onAnimationCancel(animator);
                Mockito.verify(spyListener, Mockito.times(0)).onAnimationRepeat(animator);
            }
        });
    }

    @Test
    public void testLoopingAnimatorOnce() {
        animator.setRepeatCount(1);
        testAnimator(new LottieValueAnimatorUnitTest.VerifyListener() {
            @Override
            public void verify(InOrder inOrder) {
                Mockito.verify(spyListener, Mockito.times(1)).onAnimationStart(animator, false);
                Mockito.verify(spyListener, Mockito.times(1)).onAnimationRepeat(animator);
                Mockito.verify(spyListener, Mockito.times(1)).onAnimationEnd(animator, false);
                Mockito.verify(spyListener, Mockito.times(0)).onAnimationCancel(animator);
            }
        });
    }

    @Test
    public void testLoopingAnimatorZeroTimes() {
        animator.setRepeatCount(0);
        testAnimator(new LottieValueAnimatorUnitTest.VerifyListener() {
            @Override
            public void verify(InOrder inOrder) {
                Mockito.verify(spyListener, Mockito.times(1)).onAnimationStart(animator, false);
                Mockito.verify(spyListener, Mockito.times(0)).onAnimationRepeat(animator);
                Mockito.verify(spyListener, Mockito.times(1)).onAnimationEnd(animator, false);
                Mockito.verify(spyListener, Mockito.times(0)).onAnimationCancel(animator);
            }
        });
    }

    @Test
    public void testLoopingAnimatorTwice() {
        animator.setRepeatCount(2);
        testAnimator(new LottieValueAnimatorUnitTest.VerifyListener() {
            @Override
            public void verify(InOrder inOrder) {
                Mockito.verify(spyListener, Mockito.times(1)).onAnimationStart(animator, false);
                Mockito.verify(spyListener, Mockito.times(2)).onAnimationRepeat(animator);
                Mockito.verify(spyListener, Mockito.times(1)).onAnimationEnd(animator, false);
                Mockito.verify(spyListener, Mockito.times(0)).onAnimationCancel(animator);
            }
        });
    }

    @Test
    public void testLoopingAnimatorOnceReverse() {
        animator.setFrame(1000);
        animator.setRepeatCount(1);
        animator.reverseAnimationSpeed();
        testAnimator(new LottieValueAnimatorUnitTest.VerifyListener() {
            @Override
            public void verify(InOrder inOrder) {
                inOrder.verify(spyListener, Mockito.times(1)).onAnimationStart(animator, true);
                inOrder.verify(spyListener, Mockito.times(1)).onAnimationRepeat(animator);
                inOrder.verify(spyListener, Mockito.times(1)).onAnimationEnd(animator, true);
                Mockito.verify(spyListener, Mockito.times(0)).onAnimationCancel(animator);
            }
        });
    }

    @Test
    public void setMinFrameSmallerThanComposition() {
        animator.setMinFrame((-9000));
        LottieValueAnimatorUnitTest.assertClose(animator.getMinFrame(), composition.getStartFrame());
    }

    @Test
    public void setMaxFrameLargerThanComposition() {
        animator.setMaxFrame(9000);
        LottieValueAnimatorUnitTest.assertClose(animator.getMaxFrame(), composition.getEndFrame());
    }

    @Test
    public void setMinFrameBeforeComposition() {
        LottieValueAnimator animator = createAnimator();
        animator.setMinFrame(100);
        animator.setComposition(composition);
        LottieValueAnimatorUnitTest.assertClose(100.0F, animator.getMinFrame());
    }

    @Test
    public void setMaxFrameBeforeComposition() {
        LottieValueAnimator animator = createAnimator();
        animator.setMaxFrame(100);
        animator.setComposition(composition);
        LottieValueAnimatorUnitTest.assertClose(100.0F, animator.getMaxFrame());
    }

    @Test
    public void setMinAndMaxFrameBeforeComposition() {
        LottieValueAnimator animator = createAnimator();
        animator.setMinAndMaxFrames(100, 900);
        animator.setComposition(composition);
        LottieValueAnimatorUnitTest.assertClose(100.0F, animator.getMinFrame());
        LottieValueAnimatorUnitTest.assertClose(900.0F, animator.getMaxFrame());
    }

    @Test
    public void setMinFrameAfterComposition() {
        LottieValueAnimator animator = createAnimator();
        animator.setComposition(composition);
        animator.setMinFrame(100);
        LottieValueAnimatorUnitTest.assertClose(100.0F, animator.getMinFrame());
    }

    @Test
    public void setMaxFrameAfterComposition() {
        LottieValueAnimator animator = createAnimator();
        animator.setComposition(composition);
        animator.setMaxFrame(100);
        Assert.assertEquals(100.0F, animator.getMaxFrame());
    }

    @Test
    public void setMinAndMaxFrameAfterComposition() {
        LottieValueAnimator animator = createAnimator();
        animator.setComposition(composition);
        animator.setMinAndMaxFrames(100, 900);
        LottieValueAnimatorUnitTest.assertClose(100.0F, animator.getMinFrame());
        LottieValueAnimatorUnitTest.assertClose(900.0F, animator.getMaxFrame());
    }

    @Test
    public void maxFrameOfNewShorterComposition() {
        LottieValueAnimator animator = createAnimator();
        animator.setComposition(composition);
        LottieComposition composition2 = createComposition(0, 500);
        animator.setComposition(composition2);
        LottieValueAnimatorUnitTest.assertClose(500.0F, animator.getMaxFrame());
    }

    @Test
    public void maxFrameOfNewLongerComposition() {
        LottieValueAnimator animator = createAnimator();
        animator.setComposition(composition);
        LottieComposition composition2 = createComposition(0, 1500);
        animator.setComposition(composition2);
        LottieValueAnimatorUnitTest.assertClose(1500.0F, animator.getMaxFrame());
    }

    @Test
    public void clearComposition() {
        animator.clearComposition();
        LottieValueAnimatorUnitTest.assertClose(0.0F, animator.getMaxFrame());
        LottieValueAnimatorUnitTest.assertClose(0.0F, animator.getMinFrame());
    }

    @Test
    public void resetComposition() {
        animator.clearComposition();
        animator.setComposition(composition);
        LottieValueAnimatorUnitTest.assertClose(0.0F, animator.getMinFrame());
        LottieValueAnimatorUnitTest.assertClose(1000.0F, animator.getMaxFrame());
    }

    @Test
    public void resetAndSetMinBeforeComposition() {
        animator.clearComposition();
        animator.setMinFrame(100);
        animator.setComposition(composition);
        LottieValueAnimatorUnitTest.assertClose(100.0F, animator.getMinFrame());
        LottieValueAnimatorUnitTest.assertClose(1000.0F, animator.getMaxFrame());
    }

    @Test
    public void resetAndSetMinAterComposition() {
        animator.clearComposition();
        animator.setComposition(composition);
        animator.setMinFrame(100);
        LottieValueAnimatorUnitTest.assertClose(100.0F, animator.getMinFrame());
        LottieValueAnimatorUnitTest.assertClose(1000.0F, animator.getMaxFrame());
    }
}

