package at.uastw.hpc.imagerotation;

import static com.github.thomaseizinger.oocl.CLProgram.BuildOption;
import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import com.github.thomaseizinger.oocl.CLCommandQueue;
import com.github.thomaseizinger.oocl.CLContext;
import com.github.thomaseizinger.oocl.CLDevice;
import com.github.thomaseizinger.oocl.CLKernel;
import com.github.thomaseizinger.oocl.CLMemory;
import com.github.thomaseizinger.oocl.CLPlatform;
import com.github.thomaseizinger.oocl.CLRange;

public class ImageRotation {

    private final CLDevice device;
    private final URI kernelURI;

    private static final long BUFFER_FLAGS = CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR;

    private ImageRotation(CLDevice device, URI kernelURI) {
        this.device = device;
        this.kernelURI = kernelURI;
    }

    public static ImageRotation create() {

        final CLPlatform platform = CLPlatform.getFirst().orElseThrow(IllegalStateException::new);
        final CLDevice device = platform.getDevice(CLDevice.DeviceType.GPU).orElseThrow(IllegalStateException::new);

        final URI kernelURI = getKernelURI("/imgRotate.cl");

        return new ImageRotation(device, kernelURI);
    }

    public BufferedImage rotate(BufferedImage image, int degrees) {

        final int width = image.getWidth();
        final int height = image.getHeight();

        final int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
        final float[] metadata = new float[] {width, height, cos(degrees), sin(degrees)};

        final int[] output = new int[pixels.length];

        try (CLContext context = device.createContext()) {
            try (CLKernel imgRotate = context.createKernel(new File(kernelURI), "imgRotate", BuildOption.EMPTY)) {
                try (
                        CLMemory<int[]> pixelBuffer = context.createBuffer(BUFFER_FLAGS, pixels);
                        CLMemory<int[]> outputBuffer = context.createBuffer(BUFFER_FLAGS, output);
                        CLMemory<float[]> metadataBuffer = context.createBuffer(BUFFER_FLAGS, metadata)
                ) {
                    imgRotate.setArguments(pixelBuffer, outputBuffer, metadataBuffer);

                    final CLCommandQueue commandQueue = context.createCommandQueue();

                    commandQueue.execute(imgRotate, 2, CLRange.of(width, height), CLRange.of(1, 1));
                    commandQueue.finish();

                    commandQueue.readBuffer(outputBuffer);

                    final BufferedImage resultImage = new BufferedImage(width, height, image.getType());
                    resultImage.setRGB(0, 0, width, height, outputBuffer.getData(), 0, width);

                    return resultImage;
                }
            }
        }
    }

    private float sin(float degrees) {
        return (float) Math.sin(Math.toRadians(degrees));
    }

    private float cos(float degrees) {
        return (float) Math.cos(Math.toRadians(degrees));
    }

    private static URI getKernelURI(String location) {
        try {
            return ImageRotation.class.getResource(location).toURI();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
