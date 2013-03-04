//yuv420rgb.h

static inline void color_convert_common(
    unsigned char *pY, unsigned char *pUV,
    int width, int height,
    unsigned char *buffer,
    int size, /* buffer size in bytes */
    int gray,
    int rotate);

static inline void clip_common(
		unsigned char *in_buffer,
		int in_w, int in_h,
		unsigned char *out_buffer,
		int out_w, int out_h);
