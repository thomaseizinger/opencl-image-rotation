package at.uastw.hpc.imagerotation;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class BufferedImageMatcher extends TypeSafeDiagnosingMatcher<BufferedImage> {

    private final BufferedImage expected;
    private final float expectedThreshold;
    private final int maximumVariance;

    public static Matcher<BufferedImage> similarTo(BufferedImage expected, float expectedThreshold, int maximumVariance) {
        return new BufferedImageMatcher(expected, expectedThreshold, maximumVariance);
    }

    public BufferedImageMatcher(BufferedImage expected, float expectedThreshold, int maximumVariance) {
        this.expected = expected;
        this.expectedThreshold = expectedThreshold;
        this.maximumVariance = maximumVariance;
    }

    @Override
    protected boolean matchesSafely(BufferedImage item, Description mismatchDescription) {

        final float actualSimilarity = compareImage(expected, item);

        mismatchDescription
                .appendText("a similarity of ")
                .appendValue(String.format("%.2f", actualSimilarity))
                .appendText(" %");

        return actualSimilarity > expectedThreshold;
    }

    private float compareImage(BufferedImage one, BufferedImage other) {

        float percentage = 0;
        try {
            DataBuffer dbA = one.getData().getDataBuffer();
            DataBuffer dbB = other.getData().getDataBuffer();

            int sizeA = dbA.getSize();
            int sizeB = dbB.getSize();

            int count = 0;

            if (sizeA == sizeB) {

                for (int i = 0; i < sizeA; i++) {

                    if (dbA.getElem(i) - dbB.getElem(i) <= maximumVariance) {
                        count = count + 1;
                    }
                }

                percentage = (count * 100) / sizeA;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return percentage;
    }

    @Override
    public void describeTo(Description description) {
        description
                .appendText("a similarity of at least ")
                .appendValue(String.format("%.2f", expectedThreshold))
                .appendText(" %");
    }
}
