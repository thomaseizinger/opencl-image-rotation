package at.uastw.hpc.imagerotation;

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
import com.github.thomaseizinger.oocl.CLProgram;
import com.github.thomaseizinger.oocl.CLRange;

public class ImageRotation {

    private final CLDevice device;
    private final URI kernelURI;

    private ImageRotation(CLDevice device, URI kernelURI) {
        this.device = device;
        this.kernelURI = kernelURI;
    }

    public static ImageRotation createFromClasspathKernel(String location) {

        final CLPlatform platform = CLPlatform.getFirst().orElseThrow(IllegalStateException::new);
        final CLDevice device = platform.getDevice(CLDevice.DeviceType.GPU).orElseThrow(IllegalStateException::new);

        final URI kernelURI = getKernelURI(location);

        return new ImageRotation(device, kernelURI);
    }

    public BufferedImage rotate(BufferedImage image, float degrees) {
        try (CLContext context = device.createContext()) {
            try (CLKernel imgRotate = context.createKernel(new File(kernelURI), "imgRotate", CLProgram.BuildOption
                    .EMPTY)) {

                final int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
                final int[] output = new int[pixels.length];

                final float[] metadata = new float[] {
                        image.getWidth(),
                        image.getHeight(),
                        (float) Math.cos(Math.toRadians(degrees)),
                        (float) Math.sin(Math.toRadians(degrees))
                };

                final CLMemory<int[]> pixelBuffer = context.createBuffer(CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, pixels);
                final CLMemory<int[]> outputBuffer = context.createBuffer(CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, output);
                final CLMemory<float[]> metadataBuffer = context.createBuffer(CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, metadata);

                imgRotate.setArguments(pixelBuffer, outputBuffer, metadataBuffer);

                final CLCommandQueue commandQueue = context.createCommandQueue();

                final CLRange globalWorkSize = CLRange.of(
                        image.getWidth(),
                        image.getHeight()
                );

                commandQueue.execute(imgRotate, 2, globalWorkSize, CLRange.of(1, 1));
                commandQueue.finish();

                commandQueue.readBuffer(outputBuffer);

                final BufferedImage resultImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
                resultImage.setRGB(0, 0, image.getWidth(), image.getHeight(), outputBuffer.getData(), 0, image.getWidth());

                return resultImage;
            }
        }
    }

    private static URI getKernelURI(String location) {
        try {
            return ImageRotation.class.getResource(location).toURI();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
