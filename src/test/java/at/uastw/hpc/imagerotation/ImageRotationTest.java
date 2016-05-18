package at.uastw.hpc.imagerotation;

import static at.uastw.hpc.imagerotation.BufferedImageMatcher.similarTo;
import static junitparams.JUnitParamsRunner.$;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jocl.CL;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class ImageRotationTest {

    private ImageRotation sut;

    @Before
    public void setUp() throws Exception {
        CL.setExceptionsEnabled(true);
        sut = ImageRotation.create();
    }

    @Test
    @Parameters
    public void shouldRotateImage(BufferedImage imageToRotate, BufferedImage expectedImage, int degreeToRotate, float
            expectedSimilarity, int maximumColorVariance) throws Exception {

        final BufferedImage actualImage = sut.rotate(imageToRotate, degreeToRotate);

        Assert.assertThat(actualImage, similarTo(expectedImage, expectedSimilarity, maximumColorVariance));
    }

    public Object[] parametersForShouldRotateImage() {
        return $(
                $(loadImg("/lena.bmp"), loadImg("/lena_rotated_180.bmp"), 180, 99, 0),
                $(loadImg("/lena.bmp"), loadImg("/lena_rotated_15.bmp"), -15, 95, 25)
        );
    }

    private static BufferedImage loadImg(String location) {
        try {
            return ImageIO.read(ImageRotationTest.class.getResource(location));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}