package at.uastw.hpc.imagerotation;

import org.junit.Assert;
import org.junit.Test;

public class KernelTest {

    /**
     * see imgRotate.cl
     */
    @Test
    public void testKernelLogic() throws Exception {

        final float[] metaData = {300, 300, (float) Math.cos(Math.toRadians(180)), (float) Math.sin(Math.toRadians(180))};

        int x1 = 65;
        int y1 = 25;

        int x0 = (int) Math.floor(metaData[0] / 2);
        int y0 = (int) Math.floor(metaData[1] / 2);

        final float cos = metaData[2];
        final float sin = metaData[3];


        float xPos = cos * (x1 - x0) - sin * (y1 - y0) + x0;
        float yPos = sin * (x1 - x0) + cos * (y1 - y0) + y0;

        float xPosExpected = 300 - 65;
        float yPosExpected = 300 - 25;

        Assert.assertEquals(xPosExpected, xPos, 0.1);
        Assert.assertEquals(yPosExpected, yPos, 0.1);
    }
}
