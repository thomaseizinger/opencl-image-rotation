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

        int iX = 65;
        int iY = 25;

        int x0 = (int) Math.floor(metaData[0] / 2);
        int y0 = (int) Math.floor(metaData[1] / 2);

        final float cos = metaData[2];
        final float sin = metaData[3];


        float xPosActual = (float) (iX - x0) * cos - (float) (iY - y0) * sin + x0 - 1;
        float yPosActual = (float) (iX - x0) * sin + (float) (iY - y0) * cos + y0 - 1;

        float xPosExpected = 300 - 65 - 1;
        float yPosExpected = 300 - 25 - 1;

        Assert.assertEquals(xPosExpected, xPosActual, 0.1);
        Assert.assertEquals(yPosExpected, yPosActual, 0.1);
    }
}
