package at.uastw.hpc.imagerotation;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public final class MatcherExtensions {

    public static Matcher<BufferedImage> similiarTo(BufferedImage expected, float percentageThreshold) {
        return new TypeSafeDiagnosingMatcher<BufferedImage>() {

            @Override
            protected boolean matchesSafely(BufferedImage item, Description mismatchDescription) {

                final float actualSimilarity = compareImage(expected, item);

                mismatchDescription.appendText("a similarity of ").appendValue(String.format("%.2f", actualSimilarity)).appendText(" %");

                return actualSimilarity > percentageThreshold;
            }

            private float compareImage(BufferedImage one, BufferedImage other) {

                float percentage = 0;
                try {
                    DataBuffer dbA = one.getData().getDataBuffer();
                    int sizeA = dbA.getSize();
                    DataBuffer dbB = other.getData().getDataBuffer();
                    int sizeB = dbB.getSize();
                    int count = 0;
                    // compare data-buffer objects //
                    if (sizeA == sizeB) {

                        for (int i = 0; i < sizeA; i++) {

                            if (dbA.getElem(i) - dbB.getElem(i) == 0) {
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
                description.appendText("a similarity of at least ").appendValue(String.format("%.2f", percentageThreshold)).appendText(" %");
            }
        };
    }
}
