package com.research.gltexture;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.view.SurfaceHolder;

public class GLLayer extends GLSurfaceView implements SurfaceHolder.Callback, 
	Camera.PreviewCallback, GLSurfaceView.Renderer{
	
	private final Context mActivityContext;
	private float texture_size = 1.0f;
	
	/**
	 * byte array stores the camera frame
	 */
	private byte[] glCameraFrame = new byte[512*512*3];
	//Bitmap glCameraFrame;
	
	/**
	 * texture handler of the camera frame
	 */
	private int[] textureHandle;
	
	/**
	 * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
	 * of being located at the center of the universe) to world space.
	 */
	private float[] mModelMatrix = new float[16];

	/**
	 * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
	 * it positions things relative to our eye.
	 */
	private float[] mViewMatrix = new float[16];

	/** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
	private float[] mProjectionMatrix = new float[16];
	
	/** Allocate storage for the final combined matrix. This will be passed into the shader program. */
	private float[] mMVPMatrix = new float[16];
	
	/** Store our model data in a float buffer. */
	private final FloatBuffer mCubePositions;
	private final FloatBuffer mCubeTextureCoordinates;
	
	/** This will be used to pass in the transformation matrix. */
	private int mMVPMatrixHandle;
	
	/** This will be used to pass in the texture. */
	private int mTextureUniformHandle;
	
	/** This will be used to pass in model position information. */
	private int mPositionHandle;
		
	/** This will be used to pass in model texture coordinate information. */
	private int mTextureCoordinateHandle;

	/** How many bytes per float. */
	private final int mBytesPerFloat = 4;	
	
	/** Size of the position data in elements. */
	private final int mPositionDataSize = 3;		
	
	/** Size of the texture coordinate data in elements. */
	private final int mTextureCoordinateDataSize = 2;
	
	/** This is a handle to our cube shading program. */
	private int mProgramHandle;
	
	/**
	 * Initialize the model data.
	 */
	public GLLayer(final Context activityContext)
	{	
		super(activityContext);
		mActivityContext = activityContext;
		
		// Define points for a cube.		
		
		// X, Y, Z
		final float[] cubePositionData =
		{
				// In OpenGL counter-clockwise winding is default. This means that when we look at a triangle, 
				// if the points are counter-clockwise we are looking at the "front". If not we are looking at
				// the back. OpenGL has an optimization where all back-facing triangles are culled, since they
				// usually represent the backside of an object and aren't visible anyways.
				
				// Front face
				(-1.0f)*texture_size, 			texture_size, 	texture_size,				
				(-1.0f)*texture_size, 	(-1.0f)*texture_size, 	texture_size,
						texture_size, 			texture_size, 	texture_size, 
				(-1.0f)*texture_size, 	(-1.0f)*texture_size,	texture_size, 				
						texture_size, 	(-1.0f)*texture_size, 	texture_size,
						texture_size, 			texture_size, 	texture_size
		};	
		
		// S, T (or X, Y)
		// Texture coordinate data.
		// Because images have a Y axis pointing downward (values increase as you move down the image) while
		// OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
		// What's more is that the texture coordinates are the same for every face.
		final float[] cubeTextureCoordinateData =
		{												
				// Front face
				0.0f, texture_size, 				
				texture_size, texture_size,
				0.0f, 0.0f,
				texture_size, texture_size,
				texture_size, 0.0f,
				0.0f, 0.0f
		};
		
		// Initialize the buffers.
		mCubePositions = ByteBuffer.allocateDirect(cubePositionData.length * mBytesPerFloat)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();							
		mCubePositions.put(cubePositionData).position(0);		
		
		mCubeTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * mBytesPerFloat)
		.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);
	}
	
	protected String getVertexShader()
	{
		return RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.vertex_shader);
	}
	
	protected String getFragmentShader()
	{
		return RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.toon_fragment_shader);
	}
	
	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) 
	{
		// Set the background clear color to black.
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		// Use culling to remove back faces.
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		
		// Enable depth testing
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		
		// The below glEnable() call is a holdover from OpenGL ES 1, and is not needed in OpenGL ES 2.
		// Enable texture mapping
		// GLES20.glEnable(GLES20.GL_TEXTURE_2D);
			
		// Position the eye in front of the origin.
		final float eyeX = 0.0f;
		final float eyeY = 0.0f;
		final float eyeZ = -0.5f;

		// We are looking toward the distance
		final float lookX = 0.0f;
		final float lookY = 0.0f;
		final float lookZ = -5.0f;

		// Set our up vector. This is where our head would be pointing were we holding the camera.
		final float upX = 0.0f;
		final float upY = 1.0f;
		final float upZ = 0.0f;

		// Set the view matrix. This matrix can be said to represent the camera position.
		// NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
		// view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
		Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);		

		final String vertexShader = getVertexShader();   		
 		final String fragmentShader = getFragmentShader();			
		
		final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);		
		final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);		
		
		mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, 
				new String[] {"a_Position", "a_TexCoordinate"});								                                							       
        
        // Load the texture
        //mTextureDataHandle = TextureHelper.loadTexture(mActivityContext, R.drawable.ali);
	}	
		
	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) 
	{
		// Set the OpenGL viewport to the same size as the surface.
		GLES20.glViewport(0, 0, width, height);

		// Create a new perspective projection matrix. The height will stay the same
		// while the width will vary as per aspect ratio.
		final float ratio = (float) width / height;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 10.0f;
		
		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
	}	

	@Override
	public void onDrawFrame(GL10 glUnused) 
	{
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);			        
                       
        // Set our per-vertex lighting program.
        GLES20.glUseProgram(mProgramHandle);
        
        // Set program handles for cube drawing.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");
        
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        
        // Bind the texture to this unit.
        //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        bindCameraTexture();
        
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);                            
        
        // Draw some cubes.        
        
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.7f, -3.5f);
        drawCube();      

	}				
	
	/**
	 * Draws a cube.
	 */			
	private void drawCube()
	{		
		// Pass in the position information
		mCubePositions.position(0);		
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
        		0, mCubePositions);        
                
        GLES20.glEnableVertexAttribArray(mPositionHandle);        
        
  
        // Pass in the texture coordinate information
        mCubeTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 
        		0, mCubeTextureCoordinates);
        
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        
		// This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);                 
        
        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
            
        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);                               
	}	
	
	/**
	 * This method is called if a new image from the camera arrived. The camera
	 * delivers images in a yuv color format. It is converted to a black and white
	 * image with a size of 512 x 512 pixels. Afterwards rendering the frame
	 */
	public void onPreviewFrame(byte[] frameByte, Camera camera){
		synchronized(this) {
			int w = CamLayer.previewSize.width;
			int h = CamLayer.previewSize.height;
		
			byte[] tempbb = new byte[w*h*3];
			yuv420rgb(frameByte, w, h, 0, tempbb);
		
			int bwCounter = 0;
			int yuvsCounter = 0;
			for(int y = 0; y < 511; y++){
				System.arraycopy(tempbb, yuvsCounter, glCameraFrame, bwCounter, 960*3);
				yuvsCounter += 960*3;
				bwCounter += 512*3;
			}
		}
		
		//byte to bitmap
		//glCameraFrame = BitmapFactory.decodeByteArray(frameByte, 0, frameByte.length);
		//System.arraycopy(frameByte, 0, glCameraFrame, 0, frameByte.length);
		
		//yuv420rgb(frameByte, 512, 512, 512, glCameraFrame);   
	}
	
	/**
	 * Generates a texture from the black and white array filled by the on PreviewFrame
	 */
	private void bindCameraTexture(){	
		synchronized(this){
			
			if(textureHandle == null)
				textureHandle = new int[1];
			else
				GLES20.glDeleteTextures(1, textureHandle, 0);
			
			GLES20.glGenTextures(1, textureHandle, 0);

			if(textureHandle[0] != 0 && glCameraFrame != null)
			{
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
				GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, 512, 512, 0, 
						GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, ByteBuffer.wrap(glCameraFrame));
				
				//Bitmap bmp = BitmapFactory.decodeByteArray(glCameraFrame, 0, glCameraFrame.length);
				
				//GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
				//bmp.recycle();
			}

		}
	}
	
	  /**
     * native libraries
     */
    static { 
        //System.loadLibrary( "imageprocessing" );
        System.loadLibrary( "yuv420rgb" );       
    } 
    
    /**
     * native function, that converts a byte array from ycbcr420 to RGB
     * @param in
     * @param width
     * @param height
     * @param textureSize
     * @param out
     */
    private native void yuv420rgb(byte[] in, int width, int height, int textureSize, byte[] out);
}
