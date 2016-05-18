__kernel void imgRotate(__global float * src, __global float * dest, __global float * metaData) {

    // metaData[0] = width
    // metaData[1] = height
    // metaData[2] = cos
    // metaData[3] = sin

    const int iX = get_global_id(0);
    const int iY = get_global_id(1);
    const int x0 = floor(metaData[0] / 2);
    const int y0 = floor(metaData[1] / 2);

    float xPos = ( (float) (iX - x0)) * metaData[2] - ( (float) (iY - y0) * metaData[3]) + x0;
    float yPos = ( (float) (iY - y0)) * metaData[2] + ( (float) (iX - x0) * metaData[3]) + y0;

    dest[iY * (int)metaData[0] + iX] = src[ (int) (floor(yPos * (int)metaData[0] + xPos)) ];
}