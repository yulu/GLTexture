precision mediump float;       	// Set the default precision to medium. We don't need as high of a 
								// precision in the fragment shader.

uniform sampler2D u_Texture;    // The input texture.
//uniform float ResS, ResT;
  
varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment

void main()
{
	vec3 irgb = texture2D(u_Texture, v_TexCoordinate).rgb;
	float ResS = 720.;
	float ResT = 720.;
	
	//texel offsets
	vec2 offsetArray[25];
	for(int i = 0; i < 25; i++){
		float x;
		float y;
		x = float((i - 5*(i/5)) - 1);
		y = float(i / 5 - 1);
		offsetArray[i] = vec2(x*1./ResS, y*1./ResT);
	}

	//use loop
	vec3 mask[25];
	for(int i = 0; i < 25; i++){
		mask[i] = texture2D(u_Texture, v_TexCoordinate + offsetArray[i]).rgb;
	}
	
	//apply blur filter
	vec3 target = vec3(0., 0., 0.);
	target += 1.*(mask[0] + mask[4] + mask[20] + mask[24]);
	target += 4.*(mask[1] + mask[3] + mask[5] + mask[9] + mask[15]+mask[19]+mask[21]+mask[23]);
	target += 7.*(mask[2]+mask[10]+mask[14]+mask[22]);
	target += 16.*(mask[6]+mask[8]+mask[16]+mask[18]);
	target += 26.*(mask[7]+mask[11]+mask[13]+mask[17]);
	target += 41.*(mask[12]);
	
	target /= 273.;
	gl_FragColor = vec4(target, 1.);
}