__kernel void imgRotate(__global int * src, __global int * dest, __global float * metaData) {

    const int width = (int) metaData[0];
    const int height = (int) metaData[1];
    const float cos = metaData[2];
    const float sin = metaData[3];

    const int x1 = get_global_id(0);
    const int y1 = get_global_id(1);

    const int x0 = floor(metaData[0] / 2);
    const int y0 = floor(metaData[1] / 2);

    float xPos = cos * (x1 - x0) - sin * (y1 - y0) + x0;
    float yPos = sin * (x1 - x0) + cos * (y1 - y0) + y0;

    if (xPos >= 0 && yPos >= 0 && xPos < width && yPos < height) {
     //   printf("x1: %d, y1: %d -> xPos: %f, yPos: %f\n", x1, y1, xPos, yPos);

        dest[y1 * width + x1] = src[  (int)(floor(yPos) * width + floor(xPos)) ];
    }
}