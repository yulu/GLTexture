precision mediump float;       	// Set the default precision to medium. We don't need as high of a 
								// precision in the fragment shader.

uniform sampler2D u_Texture;    // The input texture.
  
varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment
const vec3 W = vec3(0.2125, 0.7154, 0.0721);

void main()
{
	float T = 0.5;

	vec2 st = v_TexCoordinate.st;
	vec3 irgb = texture2D(u_Texture, st).rgb;
	
	vec3 target = vec3(1., 1., 1.) - irgb;
	
	gl_FragColor = vec4(target, 1.);
}