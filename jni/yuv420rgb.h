//yuv420rgb.h

static inline void color_convert_common(
    unsigned char *pY, unsigned char *pUV,
    int width, int height, int text,
    unsigned char *buffer,
    int size, /* buffer size in bytes */
    int gray,
    int rotate);
