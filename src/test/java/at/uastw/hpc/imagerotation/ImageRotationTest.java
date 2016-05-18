package at.uastw.hpc.imagerotation;

import static at.uastw.hpc.imagerotation.MatcherExtensions.similiarTo;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.jocl.CL;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ImageRotationTest {

    private ImageRotation sut;

    @Before
    public void setUp() throws Exception {
        CL.setExceptionsEnabled(true);
        sut = ImageRotation.createFromClasspathKernel("/imgRotate.cl");
    }

    @Test
    public void shouldRotateImageBy90Degrees() throws Exception {
        final BufferedImage lena = ImageIO.read(ImageRotationTest.class.getResource("/lena.bmp"));
        final BufferedImage expected = ImageIO.read(ImageRotationTest.class.getResource("/lena_rotated_180.bmp"));

        final BufferedImage result = sut.rotate(lena, 180);

        Assert.assertThat(result, similiarTo(expected, 99));
    }
}