//#define GRAY
precision mediump float;       	// Set the default precision to medium. We don't need as high of a 
								// precision in the fragment shader.

uniform sampler2D u_Texture;    // The input texture.
  
varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment

void main()
{
	vec3 irgb = texture2D(u_Texture, v_TexCoordinate).rgb;
	float ResS = 720.;
	float ResT = 720.;
	
	vec2 stp0 = vec2(1./ResS, 0.);
	vec2 stpp = vec2(1./ResS, 1./ResT);
	vec3 c00 = texture2D(u_Texture, v_TexCoordinate).rgb;
	vec3 cp1p1 = texture2D(u_Texture, v_TexCoordinate + stpp).rgb;
	
	vec3 diffs = c00 - cp1p1;
	float max = diffs.r;
	if(abs(diffs.g)>abs(max)) max = diffs.g;
	if(abs(diffs.b)>abs(max)) max = diffs.b;
	
	float gray = clamp(max + .5, 0., 1.);
	vec3 color = vec3(gray, gray, gray);

#ifdef GRAY
	gl_FragColor = vec4(color, 1.);
#else
	gl_FragColor = vec4(mix(color, c00, 0.1), 1.);
#endif
}
