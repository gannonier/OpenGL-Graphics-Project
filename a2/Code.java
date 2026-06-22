package a2;

import java.nio.*;
import javax.swing.*;
import java.lang.Math;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.util.*;
import com.jogamp.common.nio.Buffers;
import org.joml.*;
import java.awt.event.*;
import java.awt.BorderLayout;

public class Code extends JFrame implements GLEventListener, KeyListener {	
	private GLCanvas myCanvas;
	private int renderingProgram;
	private int xyzRenderingProgram;
	private int renderingProgramCubeMap;
	private int renderingProgramEnvMap;
	private int renderingProgramWater;
	private int renderingProgramShadow;
	private int vao[] = new int[1];
	private int vbo[] = new int[34];

	private float cameraX, cameraY, cameraZ;
	private Vector3f cameraU = new Vector3f();
	private Vector3f cameraV = new Vector3f();
	private Vector3f cameraN = new Vector3f();
	private float bowlLocx, bowlLocY, bowlLocZ;
	private float banaLocX, banaLocY, banaLocZ;
	private float orangeLocX, orangeLocY, orangeLocZ;
	private float appleLocX, appleLocY, appleLocZ;
	private float tableLocx, tableLocY, tableLocZ;
	private float pitcherLocX, pitcherLocY, pitcherLocZ;
	private float waterLocX, waterLocY, waterLocZ;
	private float fountainLocX, fountainLocY, fountainLocZ;

	private int bowlTexture;
	private int bananaTexture;
	private int orangeTexture;
	private int appleTexture;
	private int skyBoxTexture;
	private int tableTexture;
	private int tableNormalMap;
	private int fountainTexture;
	private int groundTexture;
	private int groundNormalMap;

	private Bowl myBowl;
	private int numBowlVerts;
	private ImportedModel myBanana;
	private int numBananaVerts;
	private Sphere myOrange;
	private int numOrangeVerts;
	private ImportedModel myApple;
	private int numAppleVerts;
	private ImportedModel myTable;
	private int numTableVerts;
	private Sphere lightSphere;
	private int numLightSphereVerts;
	private ImportedModel myPitcher;
	private int numPitcherVerts;
	private ImportedModel myFountain;
	private int numFountainVerts;
	private int numGroundVerts;
	private int numWaterVerts;

	private float cameraSpeed = 15.0f;
	private float cameraAngle = 0.5f;
	private float lightSpeed = 10.0f;

	// allocate variables for display() function
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);  // buffer for transfering matrix to uniform
	private Matrix4fStack mvStack = new Matrix4fStack(15);
	private Matrix4f pMat = new Matrix4f();  // perspective matrix
	private Matrix4f vMat = new Matrix4f();  // view matrix
	private Matrix4f mMat = new Matrix4f();  // model matrix
	private Matrix4f mvMat = new Matrix4f(); // model-view matrix
	private Matrix4f tMat = new Matrix4f();  // T matrix
	private Matrix4f rMat = new Matrix4f();  // R matrix
	private Matrix4f invTrMat = new Matrix4f(); // inverse-transpose
	private int mvLoc, pLoc, vLoc, nLoc, envMvLoc, envPLoc, envNLoc, reflectStrength;
	private int alphaLoc, flipLoc, useTextureLoc, useShadowLoc;
	private int waterMLoc, waterVLoc, waterPLoc, waterNLoc, waterTimeLoc;
	private int useNormal, reflectSamplerLoc, refractSamplerLoc;
	private int shadowMVPLoc;
	private float aspect;

	private int reflectTextureId;
	private int refractTextureId;
	private int reflectFrameBuffer;
	private int refractFrameBuffer;
	private int[] bufferId = new int[1];

	private double tf;
	private double startTime;
	private double lastTime;
	private double currentTime;
	private double deltaTime;
	private float waterTime = 0.0f;

	private int globalAmbLoc, ambLoc, diffLoc, specLoc, posLoc, mambLoc, mdiffLoc, mspecLoc, mshiLoc;
	float[] bowlAmb = Utils.silverAmbient();
	float[] bowlDiff = Utils.silverDiffuse();
	float[] bowlSpec = Utils.silverSpecular();
	float bowlShi = Utils.silverShininess();

	//yellow plastic
	float[] banAmb = new float [] {0.0f,  0.0f, 0.0f, 1};
	float[] banDiff = new float [] {0.5f,  0.5f, 0.0f, 1};
	float[] banSpec = new float [] {0.6f,  0.6f, 0.5f, 1};
	float banShi = 32.0f;

	//yellow rubber
	float[] oraAmb = new float [] {0.05f,  0.05f, 0.0f, 1};
	float[] oraDiff = new float [] {0.5f,  0.5f, 0.4f, 1};
	float[] oraSpec = new float [] {0.7f,  0.7f, 0.04f, 1};
	float oraShi = 10.0f;

	//red plastic material
	float[] appleAmb = new float [] {0.0f,  0.0f, 0.0f, 1};
	float[] appleDiff = new float [] {0.5f,  0.0f, 0.0f, 1};
	float[] appleSpec = new float [] {0.7f,  0.6f, 0.6f, 1};
	float appleShi = 32.0f;

	//pitcher material
	float[] pitcherAmb = new float[] {0.6f, 0.8f, 1.0f, 1.0f};
	float[] pitcherDiff = new float[] {0.65f, 0.85f, 1.0f, 1.0f};
	float[] pitcherSpec = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
	float pitcherShi = 96.0f;

	float[] tableAmb = Utils.bronzeAmbient();
	float[] tableDiff = Utils.bronzeDiffuse();
	float[] tableSpec = Utils.bronzeSpecular();
	float tableShi = Utils.bronzeShininess();

	float[] globalAmbient = new float[] { 0.5f, 0.5f, 0.5f, 1.0f };
	float[] lightAmbient  = new float[] { 0.07f, 0.07f, 0.07f, 1.0f };
	float[] lightDiffuse  = new float[] { 1.3f, 1.3f, 1.3f, 1.0f };
	float[] lightSpecular = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	private Vector3f initialLightLoc = new Vector3f(-10.0f, 10.0f, 4.0f);
	private Vector3f currentLightPos = new Vector3f();
	private final float[] off4 = {0.0f, 0.0f, 0.0f, 1.0f};
	private final float[] off3 = {0.0f, 0.0f, 0.0f};
	private float[] lightPos = new float[3];
	private Vector4f lightP = new Vector4f();

	private boolean keyW, keyS, keyA, keyD, keyQ, keyE, keyUp, keyDown, keyLeft, keyRight;
	private boolean keyF, keyH, keyT, keyG, keyR, keyY;
	private boolean toggleAxis = true;
	private boolean lightOn = true;
	private int isSphere;

	// shadow stuff
	private int scSizeX, scSizeY;
	private int [] shadowTex = new int[1];
	private int [] shadowBuffer = new int[1];
	private Matrix4f lightVmat = new Matrix4f();
	private Matrix4f lightPmat = new Matrix4f();
	private Matrix4f shadowMVP1 = new Matrix4f();
	private Matrix4f shadowMVP2 = new Matrix4f();
	private Matrix4f bias = new Matrix4f();
	private Vector3f sTarget = new Vector3f(25.0f, -14.0f, -15.0f);
	private Vector3f sUp = new Vector3f(0.0f, 1.0f, 0.0f);

	public Code()
	{	setTitle("CSC Lab 2 - Fruit Bowl");
		setSize(1200,600);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		myCanvas.addKeyListener(this);
		this.add(myCanvas);
		this.setVisible(true);
		Animator animator = new Animator(myCanvas);
		animator.start();
	}

	private void createReflectRefractBuffers() {	
		GL4 gl = (GL4) GLContext.getCurrentGL();
	
		// Initialize Reflect Framebuffer
		gl.glGenFramebuffers(1, bufferId, 0);
		reflectFrameBuffer = bufferId[0];
		gl.glBindFramebuffer(GL_FRAMEBUFFER, reflectFrameBuffer);
		gl.glGenTextures(1, bufferId, 0);
		reflectTextureId = bufferId[0];
		gl.glBindTexture(GL_TEXTURE_2D, reflectTextureId);
		gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, myCanvas.getWidth(), myCanvas.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		gl.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, reflectTextureId, 0);
		gl.glDrawBuffer(GL_COLOR_ATTACHMENT0);
		gl.glGenTextures(1, bufferId, 0);
		gl.glBindTexture(GL_TEXTURE_2D, bufferId[0]);
		gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, myCanvas.getWidth(), myCanvas.getHeight(), 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		gl.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, bufferId[0], 0);

		// Initialize Refract Framebuffer
		gl.glGenFramebuffers(1, bufferId, 0);
		refractFrameBuffer = bufferId[0];
		gl.glBindFramebuffer(GL_FRAMEBUFFER, refractFrameBuffer);
		gl.glGenTextures(1, bufferId, 0);
		refractTextureId = bufferId[0];
		gl.glBindTexture(GL_TEXTURE_2D, refractTextureId);
		gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, myCanvas.getWidth(), myCanvas.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		gl.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, refractTextureId, 0);
		gl.glDrawBuffer(GL_COLOR_ATTACHMENT0);
		gl.glGenTextures(1, bufferId, 0);
		gl.glBindTexture(GL_TEXTURE_2D, bufferId[0]);
		gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, myCanvas.getWidth(), myCanvas.getHeight(), 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		gl.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, bufferId[0], 0);
	}

	public void display(GLAutoDrawable drawable)
	{
		GL4 gl = (GL4) GLContext.getCurrentGL();

		currentTime = System.currentTimeMillis();
		tf = (currentTime - startTime) / 1000.0;
		deltaTime = (currentTime - lastTime) / 1000.0;
		lastTime = currentTime;

		waterTime += (float) deltaTime;

		// movement
		if (keyW) cameraNMove((float)deltaTime);
		if (keyS) cameraNMove(-((float)deltaTime));
		if (keyA) cameraUMove(-((float)deltaTime));
		if (keyD) cameraUMove((float)deltaTime);
		if (keyQ) cameraVMove(-((float)deltaTime));
		if (keyE) cameraVMove((float)deltaTime);

		if (keyLeft) yaw((float)deltaTime);
		if (keyRight) yaw((float)-deltaTime);
		if (keyUp) pitch((float)deltaTime);
		if (keyDown) pitch((float)-deltaTime);

		if (keyF) lightXMove(-((float)deltaTime));
		if (keyH) lightXMove((float)deltaTime);
		if (keyT) lightYMove((float)deltaTime);
		if (keyG) lightYMove(-((float)deltaTime));
		if (keyR) lightZMove((float)deltaTime);
		if (keyY) lightZMove(-((float)deltaTime));

		// build normal camera view matrix
		rMat.set(
			cameraU.x, cameraV.x, -cameraN.x, 0,
			cameraU.y, cameraV.y, -cameraN.y, 0,
			cameraU.z, cameraV.z, -cameraN.z, 0,
			0,         0,         0,          1
		);

		tMat.identity();
		tMat.translate(-cameraX, -cameraY, -cameraZ);

		vMat.identity();
		vMat.mul(rMat);
		vMat.mul(tMat);

		Matrix4f normalView = new Matrix4f(vMat);

		lightVmat.identity().setLookAt(currentLightPos, sTarget, sUp);
		lightPmat.identity().setPerspective(
			(float) Math.toRadians(120.0f), aspect, 0.1f, 250.0f);

		// PASS 0: shadow map
		gl.glBindFramebuffer(GL_FRAMEBUFFER, shadowBuffer[0]);
		gl.glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, shadowTex[0], 0);
		gl.glViewport(0, 0, scSizeX, scSizeY);
		gl.glDrawBuffer(GL_NONE);
		gl.glReadBuffer(GL_NONE);

		gl.glClear(GL_DEPTH_BUFFER_BIT);

		gl.glEnable(GL_DEPTH_TEST);
		gl.glEnable(GL_POLYGON_OFFSET_FILL);
		gl.glPolygonOffset(3.0f, 5.0f);

		drawShadowCasters(gl);

		gl.glDisable(GL_POLYGON_OFFSET_FILL);
		
		// reflection view matrix
		Matrix4f reflectionMatrix = new Matrix4f()
			.identity()
			.translate(0.0f, waterLocY, 0.0f)
			.scale(1.0f, -1.0f, 1.0f)
			.translate(0.0f, -waterLocY, 0.0f);

		Matrix4f reflectedView = new Matrix4f(normalView).mul(reflectionMatrix);

		// PASS 1: reflection texture
		gl.glBindFramebuffer(GL_FRAMEBUFFER, reflectFrameBuffer);
		gl.glViewport(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glClear(GL_DEPTH_BUFFER_BIT);

		vMat.set(reflectedView);
		drawSceneObjects(gl, true);

		// PASS 2: refraction texture
		gl.glBindFramebuffer(GL_FRAMEBUFFER, refractFrameBuffer);
		gl.glViewport(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		vMat.set(normalView);
		gl.glActiveTexture(GL_TEXTURE2);
		gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]);

		drawSceneObjects(gl, false);
		drawPitcher(gl);

		// PASS 3: real scene
		gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
		gl.glViewport(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		vMat.set(normalView);

		gl.glActiveTexture(GL_TEXTURE2);	
		gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]);

		drawSceneObjects(gl, false);
		drawWater(gl);
		drawPitcher(gl);

		if (toggleAxis) {
			drawAxisAndLight(gl);
		}
	}

	private void drawShadowCasters(GL4 gl) {
		gl.glUseProgram(renderingProgramShadow);

		int shadowMVPLoc = gl.glGetUniformLocation(renderingProgramShadow, "shadowMVP");

		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);

		mvStack.clear();
		mvStack.pushMatrix();

		mvStack.pushMatrix();
		mvStack.translate(bowlLocx, bowlLocY, bowlLocZ);

		// Bowl
		Matrix4f bowlModel = new Matrix4f();
		bowlModel.translate(bowlLocx, bowlLocY, bowlLocZ);

		shadowMVP1.identity();
		shadowMVP1.mul(lightPmat);
		shadowMVP1.mul(lightVmat);
		shadowMVP1.mul(bowlModel);

		gl.glUniformMatrix4fv(shadowMVPLoc, 1, false, shadowMVP1.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		gl.glDrawArrays(GL_TRIANGLES, 0, numBowlVerts);

		// Banana
		Matrix4f bananaModel = new Matrix4f();
		bananaModel.translate(bowlLocx, bowlLocY, bowlLocZ);
		bananaModel.translate(banaLocX, banaLocY, banaLocZ);
		bananaModel.rotateX((float)Math.toRadians(90.0f));
		bananaModel.rotateY((float)Math.toRadians(180.0f));
		bananaModel.rotateZ((float)Math.toRadians(70.0f));
		bananaModel.scale(0.3f, 0.3f, 0.3f);
		bananaModel.rotateX((float)Math.sin(2.5f * tf) * 0.2f);

		shadowMVP1.identity();
		shadowMVP1.mul(lightPmat);
		shadowMVP1.mul(lightVmat);
		shadowMVP1.mul(bananaModel);

		gl.glUniformMatrix4fv(shadowMVPLoc, 1, false, shadowMVP1.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		gl.glDrawArrays(GL_TRIANGLES, 0, numBananaVerts);

		// Orange
		Matrix4f orangeModel = new Matrix4f();
		orangeModel.translate(bowlLocx, bowlLocY, bowlLocZ);
		orangeModel.translate(
			orangeLocX,
			orangeLocY + (float)Math.abs(Math.sin(2.0f * tf)) * 0.8f,
			orangeLocZ
		);

		shadowMVP1.identity();
		shadowMVP1.mul(lightPmat);
		shadowMVP1.mul(lightVmat);
		shadowMVP1.mul(orangeModel);

		gl.glUniformMatrix4fv(shadowMVPLoc, 1, false, shadowMVP1.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		gl.glDrawArrays(GL_TRIANGLES, 0, numOrangeVerts);

		// Apple
		Matrix4f appleModel = new Matrix4f();
		appleModel.translate(bowlLocx, bowlLocY, bowlLocZ);
		appleModel.translate(appleLocX, appleLocY, appleLocZ);
		appleModel.rotateX((float)Math.toRadians(120.0f));
		appleModel.scale(2.0f, 2.0f, 1.0f + (float)Math.abs(Math.sin(4.5f * tf)) * 0.7f);

		shadowMVP1.identity();
		shadowMVP1.mul(lightPmat);
		shadowMVP1.mul(lightVmat);
		shadowMVP1.mul(appleModel);

		gl.glUniformMatrix4fv(shadowMVPLoc, 1, false, shadowMVP1.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		gl.glDrawArrays(GL_TRIANGLES, 0, numAppleVerts);

		// Table
		Matrix4f tableModel = new Matrix4f();
		tableModel.translate(tableLocx, tableLocY, tableLocZ);
		tableModel.scale(0.2f, 0.2f, 0.2f);

		shadowMVP1.identity();
		shadowMVP1.mul(lightPmat);
		shadowMVP1.mul(lightVmat);
		shadowMVP1.mul(tableModel);

		gl.glUniformMatrix4fv(shadowMVPLoc, 1, false, shadowMVP1.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[15]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		gl.glDrawArrays(GL_TRIANGLES, 0, numTableVerts);

		// Fountain
		Matrix4f fountainModel = new Matrix4f();
		fountainModel.translate(fountainLocX, fountainLocY, fountainLocZ);
		fountainModel.scale(20.0f, 20.0f, 20.0f);

		shadowMVP1.identity();
		shadowMVP1.mul(lightPmat);
		shadowMVP1.mul(lightVmat);
		shadowMVP1.mul(fountainModel);

		gl.glUniformMatrix4fv(shadowMVPLoc, 1, false, shadowMVP1.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[26]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		gl.glDrawArrays(GL_TRIANGLES, 0, numFountainVerts);

		// Pitcher
		Matrix4f pitcherModel = new Matrix4f();
		pitcherModel.translate(pitcherLocX, pitcherLocY, pitcherLocZ);
		pitcherModel.scale(100.0f, 100.0f, 100.0f);

		shadowMVP1.identity();
		shadowMVP1.mul(lightPmat);
		shadowMVP1.mul(lightVmat);
		shadowMVP1.mul(pitcherModel);

		gl.glUniformMatrix4fv(shadowMVPLoc, 1, false, shadowMVP1.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[20]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		gl.glDrawArrays(GL_TRIANGLES, 0, numPitcherVerts);
	}

	private void drawSceneObjects(GL4 gl,boolean reflected){
		//CUBE MAP using buffer #15
		gl.glUseProgram(renderingProgramCubeMap);

		vLoc = gl.glGetUniformLocation(renderingProgramCubeMap, "v_matrix");
		gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
		pLoc = gl.glGetUniformLocation(renderingProgramCubeMap, "p_matrix");
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_CUBE_MAP, skyBoxTexture);

		gl.glDisable(GL_CULL_FACE);
		gl.glDisable(GL_DEPTH_TEST);

		gl.glDrawArrays(GL_TRIANGLES, 0, 36);

		gl.glEnable(GL_DEPTH_TEST);
		gl.glEnable(GL_CULL_FACE);

		if (reflected)
			gl.glFrontFace(GL_CW);
		else
			gl.glFrontFace(GL_CCW);

		//ALL OBJECTS AND CAMERA

		gl.glUseProgram(renderingProgram);

		mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");
		nLoc = gl.glGetUniformLocation(renderingProgram, "norm_matrix");
		useNormal = gl.glGetUniformLocation(renderingProgram, "useNormalMap");
		alphaLoc = gl.glGetUniformLocation(renderingProgram, "alpha");
		flipLoc = gl.glGetUniformLocation(renderingProgram, "flipNormal");
		useTextureLoc = gl.glGetUniformLocation(renderingProgram, "useTexture");
		shadowMVPLoc = gl.glGetUniformLocation(renderingProgram, "shadowMVP");
		useShadowLoc = gl.glGetUniformLocation(renderingProgram, "useShadow");

		mambLoc = gl.glGetUniformLocation(renderingProgram, "material.ambient");
		mdiffLoc = gl.glGetUniformLocation(renderingProgram, "material.diffuse");
		mspecLoc = gl.glGetUniformLocation(renderingProgram, "material.specular");
		mshiLoc = gl.glGetUniformLocation(renderingProgram, "material.shininess");

		installLights();

		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
		mvStack.clear();
		mvStack.pushMatrix();
		mvStack.mul(vMat);

		//BOWL: PARENT
		mvStack.pushMatrix();
		mvStack.translate(bowlLocx, bowlLocY, bowlLocZ);
		gl.glUseProgram(renderingProgramEnvMap);

		envMvLoc = gl.glGetUniformLocation(renderingProgramEnvMap, "mv_matrix");
		envPLoc = gl.glGetUniformLocation(renderingProgramEnvMap, "p_matrix");
		envNLoc = gl.glGetUniformLocation(renderingProgramEnvMap, "norm_matrix");
		reflectStrength = gl.glGetUniformLocation(renderingProgramEnvMap, "reflectStrength");
		
		gl.glUniform1f(reflectStrength, 0.75f);
		
		gl.glUniformMatrix4fv(envMvLoc, 1, false, mvStack.get(vals));
		gl.glUniformMatrix4fv(envPLoc, 1, false, pMat.get(vals));

		new Matrix4f(mvStack).invert(invTrMat);
		invTrMat.transpose();
		gl.glUniformMatrix4fv(envNLoc, 1, false, invTrMat.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_CUBE_MAP, skyBoxTexture);

		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glDrawArrays(GL_TRIANGLES, 0, numBowlVerts);

		// switch back to normal shader
		gl.glUseProgram(renderingProgram);

		//draw banana using buffer #5
		mvStack.pushMatrix();
		mvStack.translate(banaLocX, banaLocY, banaLocZ);
		mvStack.rotateX((float)Math.toRadians(90.0f));
		mvStack.rotateY((float)Math.toRadians(180.0f));
		mvStack.rotateZ((float)Math.toRadians(70.0f));
		mvStack.scale(0.3f, 0.3f, 0.3f);
		mvStack.rotateX((float)Math.sin(2.5f * tf) * 0.2f);

		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
		gl.glUniform1i(useShadowLoc, 0);

		new Matrix4f(mvStack).invert(invTrMat);
		invTrMat.transpose();
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, bananaTexture);

		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glUniform1i(useNormal, 0);
		installMaterial(banAmb,banDiff,banSpec,banShi);
		gl.glDrawArrays(GL_TRIANGLES, 0, numBananaVerts);
		mvStack.popMatrix();

		//draw orange using buffer #8
		mvStack.pushMatrix();
		mvStack.translate(orangeLocX, orangeLocY + (float)Math.abs(Math.sin(2.0f * tf)) * 0.8f, orangeLocZ);

		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
		gl.glUniform1i(useShadowLoc, 0);

		new Matrix4f(mvStack).invert(invTrMat);
		invTrMat.transpose();
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, orangeTexture);

		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glUniform1i(useNormal, 0);
		gl.glUniform1f(alphaLoc, 1.0f);
		gl.glUniform1f(flipLoc, 1.0f);
		gl.glUniform1i(useTextureLoc, 1);
		installMaterial(oraAmb,oraDiff,oraSpec,oraShi);
		gl.glDrawArrays(GL_TRIANGLES, 0, numOrangeVerts);
		mvStack.popMatrix();

		//draw apple using buffer #12
		mvStack.pushMatrix();
		mvStack.translate(appleLocX, appleLocY, appleLocZ);
		mvStack.rotateX((float)Math.toRadians(120.0f));
		mvStack.scale(2.0f, 2.0f,1.0f + (float)Math.abs(Math.sin(4.5f * tf)) * 0.7f);
		
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
		gl.glUniform1i(useShadowLoc, 0);

		new Matrix4f(mvStack).invert(invTrMat);
		invTrMat.transpose();
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[13]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[14]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, appleTexture);

		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glUniform1i(useNormal, 0);
		gl.glUniform1f(alphaLoc, 1.0f);
		gl.glUniform1f(flipLoc, 1.0f);
		gl.glUniform1i(useTextureLoc, 1);
		installMaterial(appleAmb,appleDiff,appleSpec,appleShi);
		gl.glDrawArrays(GL_TRIANGLES, 0, numAppleVerts);
		mvStack.popMatrix();
		
		mvStack.popMatrix();
		mvStack.popMatrix();

		//draw table using buffer #15
		mMat.identity();
		mMat.translate(tableLocx, tableLocY, tableLocZ);
		mMat.scale(0.2f, 0.2f, 0.2f);

		shadowMVP2.identity();
		shadowMVP2.mul(bias);
		shadowMVP2.mul(lightPmat);
		shadowMVP2.mul(lightVmat);
		shadowMVP2.mul(mMat);

		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);

		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
		gl.glUniformMatrix4fv(shadowMVPLoc, 1, false, shadowMVP2.get(vals));
		gl.glUniform1i(useShadowLoc, 1);

		mvMat.invert(invTrMat);
		invTrMat.transpose();
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[15]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
	
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[16]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[17]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[19]);
		gl.glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(3);

		gl.glUniform1i(useNormal, 1);
		gl.glUniform1f(alphaLoc, 1.0f);
		gl.glUniform1f(flipLoc, 1.0f);
		gl.glUniform1i(useTextureLoc, 1);		

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, tableTexture);

		gl.glActiveTexture(GL_TEXTURE1);
		gl.glBindTexture(GL_TEXTURE_2D, tableNormalMap);

		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		installMaterial(tableAmb,tableDiff,tableSpec,tableShi);
		gl.glDrawArrays(GL_TRIANGLES, 0, numTableVerts);
		
		drawGround(gl);
		drawFountain(gl);
	}

	private void drawGround(GL4 gl) {
		gl.glUseProgram(renderingProgram);

		mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");
		nLoc = gl.glGetUniformLocation(renderingProgram, "norm_matrix");
		useNormal = gl.glGetUniformLocation(renderingProgram, "useNormalMap");
		alphaLoc = gl.glGetUniformLocation(renderingProgram, "alpha");
		flipLoc = gl.glGetUniformLocation(renderingProgram, "flipNormal");
		useTextureLoc = gl.glGetUniformLocation(renderingProgram, "useTexture");
		shadowMVPLoc = gl.glGetUniformLocation(renderingProgram, "shadowMVP");
		useShadowLoc = gl.glGetUniformLocation(renderingProgram, "useShadow");

		mambLoc = gl.glGetUniformLocation(renderingProgram, "material.ambient");
		mdiffLoc = gl.glGetUniformLocation(renderingProgram, "material.diffuse");
		mspecLoc = gl.glGetUniformLocation(renderingProgram, "material.specular");
		mshiLoc = gl.glGetUniformLocation(renderingProgram, "material.shininess");
		
		installLights();

		mMat.identity();

		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);

		shadowMVP2.identity();
		shadowMVP2.mul(bias);
		shadowMVP2.mul(lightPmat);
		shadowMVP2.mul(lightVmat);
		shadowMVP2.mul(mMat);

		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
		gl.glUniformMatrix4fv(shadowMVPLoc, 1, false, shadowMVP2.get(vals));
		gl.glUniform1i(useShadowLoc, 1);

		new Matrix4f(mvMat).invert(invTrMat);
		invTrMat.transpose();
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[29]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[30]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[31]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[32]);
		gl.glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(3);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, groundTexture);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);


		gl.glActiveTexture(GL_TEXTURE1);
		gl.glBindTexture(GL_TEXTURE_2D, groundNormalMap);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

		gl.glUniform1i(useNormal, 1);
		gl.glUniform1i(useTextureLoc, 1);
		gl.glUniform1f(alphaLoc, 1.0f);
		gl.glUniform1f(flipLoc, 1.0f);

		installMaterial(tableAmb, tableDiff, tableSpec, tableShi);

		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glDisable(GL_BLEND);

		gl.glDrawArrays(GL_TRIANGLES, 0, numGroundVerts);
	}

	private void drawFountain(GL4 gl) {
		gl.glUseProgram(renderingProgram);

		mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");
		nLoc = gl.glGetUniformLocation(renderingProgram, "norm_matrix");
		useNormal = gl.glGetUniformLocation(renderingProgram, "useNormalMap");
		alphaLoc = gl.glGetUniformLocation(renderingProgram, "alpha");
		flipLoc = gl.glGetUniformLocation(renderingProgram, "flipNormal");
		useTextureLoc = gl.glGetUniformLocation(renderingProgram, "useTexture");
		shadowMVPLoc = gl.glGetUniformLocation(renderingProgram, "shadowMVP");
		useShadowLoc = gl.glGetUniformLocation(renderingProgram, "useShadow");

		mambLoc = gl.glGetUniformLocation(renderingProgram, "material.ambient");
		mdiffLoc = gl.glGetUniformLocation(renderingProgram, "material.diffuse");
		mspecLoc = gl.glGetUniformLocation(renderingProgram, "material.specular");
		mshiLoc = gl.glGetUniformLocation(renderingProgram, "material.shininess");

		installLights();

		mMat.identity();
		mMat.translate(fountainLocX, fountainLocY, fountainLocZ);
		mMat.scale(20.0f, 20.0f, 20.0f);

		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);

		shadowMVP2.identity();
		shadowMVP2.mul(bias);
		shadowMVP2.mul(lightPmat);
		shadowMVP2.mul(lightVmat);
		shadowMVP2.mul(mMat);

		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
		gl.glUniformMatrix4fv(shadowMVPLoc, 1, false, shadowMVP2.get(vals));
		gl.glUniform1i(useShadowLoc, 1);

		new Matrix4f(mvMat).invert(invTrMat);
		invTrMat.transpose();
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[26]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[27]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[28]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, fountainTexture);

		gl.glUniform1i(useNormal, 0);
		gl.glUniform1i(useTextureLoc, 1);
		gl.glUniform1f(alphaLoc, 1.0f);
		gl.glUniform1f(flipLoc, 1.0f);

		installMaterial(tableAmb, tableDiff, tableSpec, tableShi);

		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glDisable(GL_BLEND);

		gl.glDrawArrays(GL_TRIANGLES, 0, numFountainVerts);
	}

	private void drawWater(GL4 gl) {
    	gl.glUseProgram(renderingProgramWater);

		reflectSamplerLoc = gl.glGetUniformLocation(renderingProgramWater, "reflectTex");
		refractSamplerLoc = gl.glGetUniformLocation(renderingProgramWater, "refractTex");
		gl.glUniform1i(reflectSamplerLoc, 0);
		gl.glUniform1i(refractSamplerLoc, 1);

		installWaterLights();

		waterMLoc = gl.glGetUniformLocation(renderingProgramWater, "m_matrix");
		waterVLoc = gl.glGetUniformLocation(renderingProgramWater, "v_matrix");
		waterPLoc = gl.glGetUniformLocation(renderingProgramWater, "p_matrix");
		waterNLoc = gl.glGetUniformLocation(renderingProgramWater, "norm_matrix");
		waterTimeLoc = gl.glGetUniformLocation(renderingProgramWater, "waterTime");

    	mMat.identity();
    	mMat.translate(waterLocX, waterLocY, waterLocZ);

    	mvMat.identity();
    	mvMat.mul(vMat);
    	mvMat.mul(mMat);

    	new Matrix4f(mvMat).invert(invTrMat);
		invTrMat.transpose();
		gl.glUniformMatrix4fv(waterNLoc, 1, false, invTrMat.get(vals));
    	gl.glUniformMatrix4fv(waterMLoc, 1, false, mMat.get(vals));
		gl.glUniformMatrix4fv(waterVLoc, 1, false, vMat.get(vals));
    	gl.glUniformMatrix4fv(waterPLoc, 1, false, pMat.get(vals));
    	gl.glUniformMatrix4fv(waterNLoc, 1, false, invTrMat.get(vals));
    	gl.glUniform1f(waterTimeLoc, waterTime);

    	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[23]);
    	gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
    	gl.glEnableVertexAttribArray(0);

    	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[24]);
    	gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
    	gl.glEnableVertexAttribArray(1);

    	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[25]);
    	gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
    	gl.glEnableVertexAttribArray(2);

    	gl.glActiveTexture(GL_TEXTURE0);
    	gl.glBindTexture(GL_TEXTURE_2D, reflectTextureId);

    	gl.glActiveTexture(GL_TEXTURE1);
    	gl.glBindTexture(GL_TEXTURE_2D, refractTextureId);

    	gl.glEnable(GL_BLEND);
   	 	gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
   	 	gl.glBlendEquation(GL_FUNC_ADD);

    	gl.glEnable(GL_DEPTH_TEST);
    	gl.glDepthFunc(GL_LEQUAL);

    	gl.glDisable(GL_CULL_FACE);

    	gl.glDrawArrays(GL_TRIANGLES, 0, numWaterVerts);

    	gl.glDisable(GL_BLEND);
    	gl.glEnable(GL_CULL_FACE);
	}

	private void drawPitcher(GL4 gl)	{
		gl.glUseProgram(renderingProgram);

		mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");
		nLoc = gl.glGetUniformLocation(renderingProgram, "norm_matrix");
		useNormal = gl.glGetUniformLocation(renderingProgram, "useNormalMap");
		alphaLoc = gl.glGetUniformLocation(renderingProgram, "alpha");
		flipLoc = gl.glGetUniformLocation(renderingProgram, "flipNormal");
		useTextureLoc = gl.glGetUniformLocation(renderingProgram, "useTexture");
		shadowMVPLoc = gl.glGetUniformLocation(renderingProgram, "shadowMVP");
		useShadowLoc = gl.glGetUniformLocation(renderingProgram, "useShadow");

		mambLoc = gl.glGetUniformLocation(renderingProgram, "material.ambient");
		mdiffLoc = gl.glGetUniformLocation(renderingProgram, "material.diffuse");
		mspecLoc = gl.glGetUniformLocation(renderingProgram, "material.specular");
		mshiLoc = gl.glGetUniformLocation(renderingProgram, "material.shininess");

		installLights();

		mMat.identity();
		mMat.translate(pitcherLocX, pitcherLocY, pitcherLocZ);
		mMat.scale(100.0f, 100.0f, 100.0f);

		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);

		shadowMVP2.identity();
		shadowMVP2.mul(bias);
		shadowMVP2.mul(lightPmat);
		shadowMVP2.mul(lightVmat);
		shadowMVP2.mul(mMat);

		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
		gl.glUniformMatrix4fv(shadowMVPLoc, 1, false, shadowMVP2.get(vals));
		gl.glUniform1i(useShadowLoc, 1);

		new Matrix4f(mvMat).invert(invTrMat);
		invTrMat.transpose();
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[20]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[21]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[22]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		gl.glUniform1i(useNormal, 0);
		gl.glUniform1i(useTextureLoc, 0);

		installMaterial(pitcherAmb, pitcherDiff, pitcherSpec, pitcherShi);

		gl.glEnable(GL_BLEND);
		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		gl.glBlendEquation(GL_FUNC_ADD);

		gl.glEnable(GL_CULL_FACE);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glDepthMask(false);

		gl.glCullFace(GL_FRONT);
		gl.glUniform1f(alphaLoc, 0.25f);
		gl.glUniform1f(flipLoc, -1.0f);
		gl.glDrawArrays(GL_TRIANGLES, 0, numPitcherVerts);

		gl.glCullFace(GL_BACK);
		gl.glUniform1f(alphaLoc, 0.45f);
		gl.glUniform1f(flipLoc, 1.0f);
		gl.glDrawArrays(GL_TRIANGLES, 0, numPitcherVerts);

		gl.glDepthMask(true);
		gl.glDisable(GL_BLEND);
		gl.glCullFace(GL_BACK);

		gl.glUniform1f(alphaLoc, 1.0f);
		gl.glUniform1f(flipLoc, 1.0f);
		gl.glUniform1i(useTextureLoc, 1);
	}

	private void drawAxisAndLight(GL4 gl) {
		gl.glUseProgram(xyzRenderingProgram);

		mvLoc = gl.glGetUniformLocation(xyzRenderingProgram, "mv_matrix");
		pLoc = gl.glGetUniformLocation(xyzRenderingProgram, "p_matrix");
		isSphere = gl.glGetUniformLocation(xyzRenderingProgram, "is_sphere");

		// LIGHT SPHERE
		mMat.identity();
		mMat.translate(currentLightPos.x, currentLightPos.y, currentLightPos.z);
		mMat.scale(0.2f, 0.2f, 0.2f);

		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);

		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
		gl.glUniform1i(isSphere, 1);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glDrawArrays(GL_TRIANGLES, 0, numLightSphereVerts);

		// XYZ AXES
		mMat.identity();

		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);

		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
		gl.glUniform1i(isSphere, 0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[18]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glDrawArrays(GL_LINES, 0, 6);
	}

	public void init(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) drawable.getGL();
		startTime = System.currentTimeMillis();
		lastTime = startTime;
		renderingProgram = Utils.createShaderProgram("a2/vertShader.glsl", "a2/fragShader.glsl");
		xyzRenderingProgram = Utils.createShaderProgram("a2/xyzVertShader.glsl", "a2/xyzFragShader.glsl");
		renderingProgramCubeMap = Utils.createShaderProgram("a2/vertCShader.glsl", "a2/fragCShader.glsl");
		renderingProgramEnvMap = Utils.createShaderProgram("a2/vertEnvShader.glsl", "a2/fragEnvShader.glsl");
		renderingProgramWater = Utils.createShaderProgram("a2/vertWaterShader.glsl", "a2/fragWaterShader.glsl");
		renderingProgramShadow = Utils.createShaderProgram("a2/vertShadowShader.glsl", "a2/fragShadowShader.glsl");

		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);

		setupVertices();

		cameraX = 0.0f; cameraY = 5.0f; cameraZ = 30.0f;
		cameraU.set(1.0f, 0.0f, 0.0f);
		cameraV.set(0.0f, 1.0f, 0.0f);
		cameraN.set(0.0f, 0.0f, -1.0f);
		bowlLocx = 0.0f; bowlLocY = -0.85f; bowlLocZ = 0.0f;
		banaLocX = -1.4f; banaLocY = -1.0f; banaLocZ = 2.5f;
		orangeLocX = 0.0f; orangeLocY = -0.1f; orangeLocZ = 0.2f;
		appleLocX = -1.8f; appleLocY = 0.2f; appleLocZ = 0.9f;
		tableLocx = 0.0f; tableLocY = -19.0f; tableLocZ = 0.0f;
		pitcherLocX = 5.0f;pitcherLocY = -3.5f; pitcherLocZ = 0.5f;
		waterLocX = 50.0f; waterLocY = -10.0f; waterLocZ = -30.0f;
		fountainLocX = 50.0f; fountainLocY = -19.0f; fountainLocZ = -30.0f;
		currentLightPos.set(initialLightLoc);

		bowlTexture = Utils.loadTexture("bowl.jpg");
		bananaTexture = Utils.loadTexture("banana.jpg");
		orangeTexture = Utils.loadTexture("orange.jpg");
		appleTexture = Utils.loadTexture("apple.jpg");
		tableTexture = Utils.loadTexture("Picnic_Texture.png");
		tableNormalMap = Utils.loadTexture("Picnic_Normal.png");
		skyBoxTexture = Utils.loadCubeMap("cubeMap");
		fountainTexture = Utils.loadTexture("fountain_texture.png");
		groundTexture = Utils.loadTexture("ground_texture.jpg");
		groundNormalMap = Utils.loadTexture("ground_normal.jpg");
		createReflectRefractBuffers();
		setupShadowBuffers();
		bias.set(
			0.5f, 0.0f, 0.0f, 0.0f,
			0.0f, 0.5f, 0.0f, 0.0f,
			0.0f, 0.0f, 0.5f, 0.0f,
			0.5f, 0.5f, 0.5f, 1.0f
		);
		gl.glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);

	}

	private void setupShadowBuffers()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		scSizeX = myCanvas.getWidth();
		scSizeY = myCanvas.getHeight();
	
		gl.glGenFramebuffers(1, shadowBuffer, 0);
	
		gl.glGenTextures(1, shadowTex, 0);
		gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]);
		gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32,
						scSizeX, scSizeY, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
		
		// may reduce shadow border artifacts
		float[] borderColor = {1.0f, 1.0f, 1.0f, 1.0f};
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
		gl.glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor, 0);
	}

	private void installMaterial(float[] amb, float[] diff, float[] spec, float shi) {
    	GL4 gl = (GL4) GLContext.getCurrentGL();

    	gl.glProgramUniform4fv(renderingProgram, mambLoc, 1, amb, 0);
    	gl.glProgramUniform4fv(renderingProgram, mdiffLoc, 1, diff, 0);
    	gl.glProgramUniform4fv(renderingProgram, mspecLoc, 1, spec, 0);
    	gl.glProgramUniform1f(renderingProgram, mshiLoc, shi);
	}

	private void installLights() {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		lightP.set(currentLightPos, 1.0f);
		lightP.mul(vMat);

		lightPos[0] = lightP.x();
		lightPos[1] = lightP.y();
		lightPos[2] = lightP.z();

		globalAmbLoc = gl.glGetUniformLocation(renderingProgram, "globalAmbient");
		ambLoc = gl.glGetUniformLocation(renderingProgram, "light.ambient");
		diffLoc = gl.glGetUniformLocation(renderingProgram, "light.diffuse");
		specLoc = gl.glGetUniformLocation(renderingProgram, "light.specular");
		posLoc = gl.glGetUniformLocation(renderingProgram, "light.position");

		gl.glProgramUniform4fv(renderingProgram, globalAmbLoc, 1, globalAmbient, 0);

		if (lightOn) {
			gl.glProgramUniform4fv(renderingProgram, ambLoc, 1, lightAmbient, 0);
			gl.glProgramUniform4fv(renderingProgram, diffLoc, 1, lightDiffuse, 0);
			gl.glProgramUniform4fv(renderingProgram, specLoc, 1, lightSpecular, 0);
			gl.glProgramUniform3fv(renderingProgram, posLoc, 1, lightPos, 0);
		} else {
			gl.glProgramUniform4fv(renderingProgram, ambLoc, 1, off4, 0);
			gl.glProgramUniform4fv(renderingProgram, diffLoc, 1, off4, 0);
			gl.glProgramUniform4fv(renderingProgram, specLoc, 1, off4, 0);
			gl.glProgramUniform3fv(renderingProgram, posLoc, 1, off3, 0);
		}

	}

	private void installWaterLights() {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		lightP.set(currentLightPos, 1.0f);
		lightP.mul(vMat);

		lightPos[0] = lightP.x();
		lightPos[1] = lightP.y();
		lightPos[2] = lightP.z();

		int wGlobalAmbLoc = gl.glGetUniformLocation(renderingProgramWater, "globalAmbient");
		int wAmbLoc       = gl.glGetUniformLocation(renderingProgramWater, "light.ambient");
		int wDiffLoc      = gl.glGetUniformLocation(renderingProgramWater, "light.diffuse");
		int wSpecLoc      = gl.glGetUniformLocation(renderingProgramWater, "light.specular");
		int wPosLoc       = gl.glGetUniformLocation(renderingProgramWater, "light.position");

		int wMatAmbLoc    = gl.glGetUniformLocation(renderingProgramWater, "material.ambient");
		int wMatDiffLoc   = gl.glGetUniformLocation(renderingProgramWater, "material.diffuse");
		int wMatSpecLoc   = gl.glGetUniformLocation(renderingProgramWater, "material.specular");
		int wMatShiLoc    = gl.glGetUniformLocation(renderingProgramWater, "material.shininess");

		gl.glProgramUniform4fv(renderingProgramWater, wGlobalAmbLoc, 1, globalAmbient, 0);

		if (lightOn) {
			gl.glProgramUniform4fv(renderingProgramWater, wAmbLoc, 1, lightAmbient, 0);
			gl.glProgramUniform4fv(renderingProgramWater, wDiffLoc, 1, lightDiffuse, 0);
			gl.glProgramUniform4fv(renderingProgramWater, wSpecLoc, 1, lightSpecular, 0);
			gl.glProgramUniform3fv(renderingProgramWater, wPosLoc, 1, lightPos, 0);
		} else {
			gl.glProgramUniform4fv(renderingProgramWater, wAmbLoc, 1, off4, 0);
			gl.glProgramUniform4fv(renderingProgramWater, wDiffLoc, 1, off4, 0);
			gl.glProgramUniform4fv(renderingProgramWater, wSpecLoc, 1, off4, 0);
			gl.glProgramUniform3fv(renderingProgramWater, wPosLoc, 1, off3, 0);
		}

		// water material
		float[] waterAmb  = new float[] {0.1f, 0.2f, 0.35f, 1.0f};
		float[] waterDiff = new float[] {0.2f, 0.45f, 0.75f, 1.0f};
		float[] waterSpec = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
		float waterShi = 128.0f;

		gl.glProgramUniform4fv(renderingProgramWater, wMatAmbLoc, 1, waterAmb, 0);
		gl.glProgramUniform4fv(renderingProgramWater, wMatDiffLoc, 1, waterDiff, 0);
		gl.glProgramUniform4fv(renderingProgramWater, wMatSpecLoc, 1, waterSpec, 0);
		gl.glProgramUniform1f(renderingProgramWater, wMatShiLoc, waterShi);
	}

	private void setupVertices()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();

		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);

        /** CUBE */
		float[] vertexPositions =
		{	-1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, -1.0f, 1.0f,  1.0f, -1.0f, -1.0f,  1.0f, -1.0f,
			1.0f, -1.0f, -1.0f, 1.0f, -1.0f,  1.0f, 1.0f,  1.0f, -1.0f,
			1.0f, -1.0f,  1.0f, 1.0f,  1.0f,  1.0f, 1.0f,  1.0f, -1.0f,
			1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f, 1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f,  1.0f, -1.0f,  1.0f,  1.0f, 1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f,
			-1.0f,  1.0f, -1.0f, 1.0f,  1.0f, -1.0f, 1.0f,  1.0f,  1.0f,
			1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f, -1.0f
		};
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(vertexPositions);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit()*4, vertBuf, GL_STATIC_DRAW);

		/** BOWL */
		myBowl = new Bowl(96, 48, 3.0f, 75.0f);
		numBowlVerts = myBowl.getIndices().length;

		int[] indices = myBowl.getIndices();
		Vector3f[] vertices = myBowl.getVertices();
		Vector2f[] tex  = myBowl.getTexCoords();
		Vector3f[] norm = myBowl.getNormals();

		float[] pvalues = new float[indices.length*3];
		float[] tvalues = new float[indices.length*2];
		float[] nvalues = new float[indices.length*3];

		for (int i=0; i<indices.length; i++)
		{	
			pvalues[i*3] = (float) (vertices[indices[i]]).x;
			pvalues[i*3+1] = (float) (vertices[indices[i]]).y;
			pvalues[i*3+2] = (float) (vertices[indices[i]]).z;
			tvalues[i*2] = (float) (tex[indices[i]]).x;
			tvalues[i*2+1] = (float) (tex[indices[i]]).y;
			nvalues[i*3] = (float) (norm[indices[i]]).x;
			nvalues[i*3+1]= (float)(norm[indices[i]]).y;
			nvalues[i*3+2]=(float) (norm[indices[i]]).z;
		}

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		FloatBuffer vertBuf2 = Buffers.newDirectFloatBuffer(pvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf2.limit()*4, vertBuf2, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		FloatBuffer texBuf2 = Buffers.newDirectFloatBuffer(tvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, texBuf2.limit()*4, texBuf2, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		FloatBuffer norBuf = Buffers.newDirectFloatBuffer(nvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, norBuf.limit()*4,norBuf, GL_STATIC_DRAW);

		/** BANANA */ 
		myBanana = new ImportedModel("banana.obj");
		numBananaVerts = myBanana.getNumVertices();

		vertices = myBanana.getVertices();
		tex = myBanana.getTexCoords();
		norm = myBanana.getNormals();

		float[] pvalues2 = new float[numBananaVerts*3];
		float[] tvalues2 = new float[numBananaVerts*2];
		float[] nvalues2 = new float[numBananaVerts*3];

		for (int i=0; i<numBananaVerts; i++)
		{	
			pvalues2[i*3] = (float) (vertices[i]).x;
			pvalues2[i*3+1] = (float) (vertices[i]).y;
			pvalues2[i*3+2] = (float) (vertices[i]).z;
			tvalues2[i*2] = (float) (tex[i]).x;
			tvalues2[i*2+1] = (float) (tex[i]).y;
			nvalues2[i*3] = (float) (norm[i]).x;
			nvalues2[i*3+1]= (float)(norm[i]).y;
			nvalues2[i*3+2]=(float) (norm[i]).z;
		}

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		FloatBuffer vertBuf3 = Buffers.newDirectFloatBuffer(pvalues2);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf3.limit()*4, vertBuf3, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		FloatBuffer texBuf3 = Buffers.newDirectFloatBuffer(tvalues2);
		gl.glBufferData(GL_ARRAY_BUFFER, texBuf3.limit()*4, texBuf3, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		FloatBuffer norBuf2 = Buffers.newDirectFloatBuffer(nvalues2);
		gl.glBufferData(GL_ARRAY_BUFFER, norBuf2.limit()*4,norBuf2, GL_STATIC_DRAW);

		/** ORANGE */
		myOrange = new Sphere(96);
		numOrangeVerts = myOrange.getIndices().length;

		indices = myOrange.getIndices();
		vertices = myOrange.getVertices();
		tex  = myOrange.getTexCoords();
		norm = myOrange.getNormals();

		float[] pvalues3 = new float[indices.length*3];
		float[] tvalues3 = new float[indices.length*2];
		float[] nvalues3 = new float[indices.length*3];

		for (int i=0; i<indices.length; i++)
		{	
			pvalues3[i*3] = (float) (vertices[indices[i]]).x;
			pvalues3[i*3+1] = (float) (vertices[indices[i]]).y;
			pvalues3[i*3+2] = (float) (vertices[indices[i]]).z;
			tvalues3[i*2] = (float) (tex[indices[i]]).x;
			tvalues3[i*2+1] = (float) (tex[indices[i]]).y;
			nvalues3[i*3] = (float) (norm[indices[i]]).x;
			nvalues3[i*3+1]= (float)(norm[indices[i]]).y;
			nvalues3[i*3+2]=(float) (norm[indices[i]]).z;
		}

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
		FloatBuffer vertBuf5 = Buffers.newDirectFloatBuffer(pvalues3);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf5.limit()*4, vertBuf5, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
		FloatBuffer texBuf5 = Buffers.newDirectFloatBuffer(tvalues3);
		gl.glBufferData(GL_ARRAY_BUFFER, texBuf5.limit()*4, texBuf5, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
		FloatBuffer norBuf4 = Buffers.newDirectFloatBuffer(nvalues3);
		gl.glBufferData(GL_ARRAY_BUFFER, norBuf4.limit()*4,norBuf4, GL_STATIC_DRAW);

		/** LIGHT SPHERE */
		lightSphere = new Sphere(24);
		numLightSphereVerts = lightSphere.getIndices().length;

		int[] lightIndices = lightSphere.getIndices();
		Vector3f[] lightVerts = lightSphere.getVertices();

		float[] lightPvalues = new float[lightIndices.length * 3];

		for (int i = 0; i < lightIndices.length; i++) {
    		lightPvalues[i*3]   = lightVerts[lightIndices[i]].x;
    		lightPvalues[i*3+1] = lightVerts[lightIndices[i]].y;
    		lightPvalues[i*3+2] = lightVerts[lightIndices[i]].z;
		}

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
		FloatBuffer lightVertBuf = Buffers.newDirectFloatBuffer(lightPvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, lightVertBuf.limit() * 4, lightVertBuf, GL_STATIC_DRAW);

		/** APPLE */
		myApple = new ImportedModel("apple.obj");
		numAppleVerts = myApple.getNumVertices();

		vertices = myApple.getVertices();
		tex = myApple.getTexCoords();
		norm = myApple.getNormals();

		float[] pvalues4 = new float[numAppleVerts*3];
		float[] tvalues4 = new float[numAppleVerts*2];
		float[] nvalues4 = new float[numAppleVerts*3];

		//had to recenter apple since its z origin was so far away
		float centerZ = (768.467f + 769.152f) / 2.0f;

		for (int i=0; i<numAppleVerts; i++)
		{	
			pvalues4[i*3] = (float) (vertices[i]).x;
			pvalues4[i*3+1] = (float) (vertices[i]).y;
			pvalues4[i*3+2] = (float) (vertices[i]).z - centerZ;
			tvalues4[i*2] = (float) (tex[i]).x;
			tvalues4[i*2+1] = (float) (tex[i]).y;
			nvalues4[i*3] = (float) (norm[i]).x;
			nvalues4[i*3+1]= (float)(norm[i]).y;
			nvalues4[i*3+2]=(float) (norm[i]).z;
		}

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
		FloatBuffer vertBuf4 = Buffers.newDirectFloatBuffer(pvalues4);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf4.limit()*4, vertBuf4, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[13]);
		FloatBuffer texBuf4 = Buffers.newDirectFloatBuffer(tvalues4);
		gl.glBufferData(GL_ARRAY_BUFFER, texBuf4.limit()*4, texBuf4, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[14]);
		FloatBuffer norBuf3 = Buffers.newDirectFloatBuffer(nvalues4);
		gl.glBufferData(GL_ARRAY_BUFFER, norBuf3.limit()*4,norBuf3, GL_STATIC_DRAW);

		/** TABLE */
		myTable = new ImportedModel("picnic_table.obj");
		numTableVerts = myTable.getNumVertices();

		vertices = myTable.getVertices();
		tex = myTable.getTexCoords();
		norm = myTable.getNormals();
		Vector3f[] tan = myTable.getTangents();

		float[] pvalues5 = new float[numTableVerts*3];
		float[] tvalues5 = new float[numTableVerts*2];
		float[] nvalues5 = new float[numTableVerts*3];
		float[] tanvalues = new float[numTableVerts*3];

		for (int i=0; i<numTableVerts; i++)
		{	
			pvalues5[i*3] = (float) (vertices[i]).x;
			pvalues5[i*3+1] = (float) (vertices[i]).y;
			pvalues5[i*3+2] = (float) (vertices[i]).z;
			tvalues5[i*2] = (float) (tex[i]).x;
			tvalues5[i*2+1] = (float) (tex[i]).y;
			nvalues5[i*3] = (float) (norm[i]).x;
			nvalues5[i*3+1]= (float)(norm[i]).y;
			nvalues5[i*3+2]=(float) (norm[i]).z;
			tanvalues[i*3]   = tan[i].x();
			tanvalues[i*3+1] = tan[i].y();
			tanvalues[i*3+2] = tan[i].z();
		}

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[15]);
		FloatBuffer vertBuf6 = Buffers.newDirectFloatBuffer(pvalues5);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf6.limit()*4, vertBuf6, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[16]);
		FloatBuffer texBuf6 = Buffers.newDirectFloatBuffer(tvalues5);
		gl.glBufferData(GL_ARRAY_BUFFER, texBuf6.limit()*4, texBuf6, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[17]);
		FloatBuffer norBuf5 = Buffers.newDirectFloatBuffer(nvalues5);
		gl.glBufferData(GL_ARRAY_BUFFER, norBuf5.limit()*4,norBuf5, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[19]);
		FloatBuffer tanBuf = Buffers.newDirectFloatBuffer(tanvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, tanBuf.limit() * 4, tanBuf, GL_STATIC_DRAW);

		/** AXIS */
		float[] axisPositions = {
    		0.0f, 0.0f, 0.0f, 10.0f, 0.0f, 0.0f,
    		0.0f, 0.0f, 0.0f, 0.0f, 10.0f, 0.0f,
    		0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 10.0f
		};

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[18]);
		FloatBuffer axisBuf = Buffers.newDirectFloatBuffer(axisPositions);
		gl.glBufferData(GL_ARRAY_BUFFER, axisBuf.limit()*4, axisBuf, GL_STATIC_DRAW);

		/** PITCHER */
		myPitcher = new ImportedModel("pitcher.obj");
		numPitcherVerts = myPitcher.getNumVertices();

		vertices = myPitcher.getVertices();
		tex = myPitcher.getTexCoords();
		norm = myPitcher.getNormals();

		float[] pvalues6 = new float[numPitcherVerts*3];
		float[] tvalues6 = new float[numPitcherVerts*2];
		float[] nvalues6 = new float[numPitcherVerts*3];

		for (int i=0; i<numPitcherVerts; i++)
		{	
			pvalues6[i*3] = (float) (vertices[i]).x;
			pvalues6[i*3+1] = (float) (vertices[i]).y;
			pvalues6[i*3+2] = (float) (vertices[i]).z;
			tvalues6[i*2] = (float) (tex[i]).x;
			tvalues6[i*2+1] = (float) (tex[i]).y;
			nvalues6[i*3] = (float) (norm[i]).x;
			nvalues6[i*3+1]= (float)(norm[i]).y;
			nvalues6[i*3+2]=(float) (norm[i]).z;
		}

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[20]);
		FloatBuffer vertBuf7 = Buffers.newDirectFloatBuffer(pvalues6);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf7.limit()*4, vertBuf7, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[21]);
		FloatBuffer texBuf7 = Buffers.newDirectFloatBuffer(tvalues6);
		gl.glBufferData(GL_ARRAY_BUFFER, texBuf7.limit()*4, texBuf7, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[22]);
		FloatBuffer norBuf7 = Buffers.newDirectFloatBuffer(nvalues6);
		gl.glBufferData(GL_ARRAY_BUFFER, norBuf7.limit()*4, norBuf7, GL_STATIC_DRAW);
	
		/** WATER */
		int waterSegments = 96;
		float waterRadius = 20.0f;
		numWaterVerts = waterSegments * 3;
		
		float[] waterPositions = new float[numWaterVerts * 3];
		float[] waterTexCoords = new float[numWaterVerts * 2];
		float[] waterNormals = new float[numWaterVerts * 3];


		for (int i = 0; i < waterSegments; i++) {
			float angle1 = (float)(2.0 * Math.PI * i / waterSegments);
			float angle2 = (float)(2.0 * Math.PI * (i + 1) / waterSegments);

			float x1 = (float)Math.cos(angle1) * waterRadius;
			float z1 = (float)Math.sin(angle1) * waterRadius;

			float x2 = (float)Math.cos(angle2) * waterRadius;
			float z2 = (float)Math.sin(angle2) * waterRadius;

			int baseVert = i * 3;

			waterPositions[(baseVert + 0) * 3]     = 0.0f;
			waterPositions[(baseVert + 0) * 3 + 1] = 0.0f;
			waterPositions[(baseVert + 0) * 3 + 2] = 0.0f;
			waterTexCoords[(baseVert + 0) * 2]     = 0.5f;
			waterTexCoords[(baseVert + 0) * 2 + 1] = 0.5f;
			waterPositions[(baseVert + 1) * 3]     = x1;
			waterPositions[(baseVert + 1) * 3 + 1] = 0.0f;
			waterPositions[(baseVert + 1) * 3 + 2] = z1;
			waterTexCoords[(baseVert + 1) * 2]     = 0.5f + (x1 / waterRadius) * 0.5f;
			waterTexCoords[(baseVert + 1) * 2 + 1] = 0.5f + (z1 / waterRadius) * 0.5f;
			waterPositions[(baseVert + 2) * 3]     = x2;
			waterPositions[(baseVert + 2) * 3 + 1] = 0.0f;
			waterPositions[(baseVert + 2) * 3 + 2] = z2;
			waterTexCoords[(baseVert + 2) * 2]     = 0.5f + (x2 / waterRadius) * 0.5f;
			waterTexCoords[(baseVert + 2) * 2 + 1] = 0.5f + (z2 / waterRadius) * 0.5f;

			for (int j = 0; j < 3; j++) {
				waterNormals[(baseVert + j) * 3]     = 0.0f;
				waterNormals[(baseVert + j) * 3 + 1] = 1.0f;
				waterNormals[(baseVert + j) * 3 + 2] = 0.0f;
			}
		}

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[23]);
		FloatBuffer waterVertBuf = Buffers.newDirectFloatBuffer(waterPositions);
		gl.glBufferData(GL_ARRAY_BUFFER, waterVertBuf.limit() * 4, waterVertBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[24]);
		FloatBuffer waterTexBuf = Buffers.newDirectFloatBuffer(waterTexCoords);
		gl.glBufferData(GL_ARRAY_BUFFER, waterTexBuf.limit() * 4, waterTexBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[25]);
		FloatBuffer waterNorBuf = Buffers.newDirectFloatBuffer(waterNormals);
		gl.glBufferData(GL_ARRAY_BUFFER, waterNorBuf.limit() * 4, waterNorBuf, GL_STATIC_DRAW);
	
		/** FOUNTAIN */
		myFountain = new ImportedModel("fountain.obj");
		numFountainVerts = myFountain.getNumVertices();

		vertices = myFountain.getVertices();
		tex = myFountain.getTexCoords();
		norm = myFountain.getNormals();

		float[] fountainPvalues = new float[numFountainVerts * 3];
		float[] fountainTvalues = new float[numFountainVerts * 2];
		float[] fountainNvalues = new float[numFountainVerts * 3];

		for (int i = 0; i < numFountainVerts; i++)
		{
			fountainPvalues[i*3] = (float)vertices[i].x;
			fountainPvalues[i*3+1] = (float)vertices[i].y;
			fountainPvalues[i*3+2] = (float)vertices[i].z;
			fountainTvalues[i*2] = (float)tex[i].x;
			fountainTvalues[i*2+1] = (float)tex[i].y;
			fountainNvalues[i*3] = (float)norm[i].x;
			fountainNvalues[i*3+1] = (float)norm[i].y;
			fountainNvalues[i*3+2] = (float)norm[i].z;
		}

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[26]);
		FloatBuffer fountainVertBuf = Buffers.newDirectFloatBuffer(fountainPvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, fountainVertBuf.limit() * 4, fountainVertBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[27]);
		FloatBuffer fountainTexBuf = Buffers.newDirectFloatBuffer(fountainTvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, fountainTexBuf.limit() * 4, fountainTexBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[28]);
		FloatBuffer fountainNorBuf = Buffers.newDirectFloatBuffer(fountainNvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, fountainNorBuf.limit() * 4, fountainNorBuf, GL_STATIC_DRAW);
	
		/** GROUND */
		numGroundVerts = 6;

		float groundSize = 300.0f;
		float groundY = -19.05f;
		float[] groundPositions = {
			-groundSize, groundY, -groundSize,
			-groundSize, groundY,  groundSize,
			groundSize, groundY, -groundSize,
			groundSize, groundY, -groundSize,
			-groundSize, groundY,  groundSize,
			groundSize, groundY,  groundSize
		};
		float texRepeat = 8.0f;
		float[] groundTexCoords = {
			0.0f,0.0f,0.0f, texRepeat,
			texRepeat, 0.0f,texRepeat, 0.0f,
			0.0f, texRepeat, texRepeat, texRepeat
		};
		float[] groundNormals = {
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f
		};
		float[] groundTangents = {
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f
		};

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[29]);
		FloatBuffer groundVertBuf = Buffers.newDirectFloatBuffer(groundPositions);
		gl.glBufferData(GL_ARRAY_BUFFER, groundVertBuf.limit() * 4, groundVertBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[30]);
		FloatBuffer groundTexBuf = Buffers.newDirectFloatBuffer(groundTexCoords);
		gl.glBufferData(GL_ARRAY_BUFFER, groundTexBuf.limit() * 4, groundTexBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[31]);
		FloatBuffer groundNorBuf = Buffers.newDirectFloatBuffer(groundNormals);
		gl.glBufferData(GL_ARRAY_BUFFER, groundNorBuf.limit() * 4, groundNorBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[32]);
		FloatBuffer groundTanBuf = Buffers.newDirectFloatBuffer(groundTangents);
		gl.glBufferData(GL_ARRAY_BUFFER, groundTanBuf.limit() * 4, groundTanBuf, GL_STATIC_DRAW);
	}

	public static void main(String[] args) { new Code(); }

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.identity().setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);
	
    	setupShadowBuffers();
	}
	public void dispose(GLAutoDrawable drawable) {}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:
				System.exit(0);
				break;
			case KeyEvent.VK_SPACE:
				toggleAxis = !toggleAxis;
				break;
			case KeyEvent.VK_L:
				lightOn = !lightOn;
				break;
			case KeyEvent.VK_W:
				keyW = true;
				break;
			case KeyEvent.VK_S:
				keyS = true;
				break;
			case KeyEvent.VK_A:
				keyA = true;
				break;
			case KeyEvent.VK_D:
				keyD = true;
				break;
			case KeyEvent.VK_Q:
				keyQ = true;
				break;
			case KeyEvent.VK_E:
				keyE = true;
				break;
			case KeyEvent.VK_UP:
				keyUp = true;
				break;
			case KeyEvent.VK_DOWN:
				keyDown = true;
				break;
			case KeyEvent.VK_LEFT:
				keyLeft = true;
				break;
			case KeyEvent.VK_RIGHT:
				keyRight = true;
				break;
			case KeyEvent.VK_1:
				keyF = true;
				break;
			case KeyEvent.VK_4:
				keyG = true;
				break;
			case KeyEvent.VK_2:
				keyH = true;
				break;
			case KeyEvent.VK_3:
				keyT = true;
				break;
			case KeyEvent.VK_5:
				keyY = true;
				break;
			case KeyEvent.VK_6:
				keyR = true;
				break;
		}
	}

	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_W:
				keyW = false;
				break;
			case KeyEvent.VK_S:
				keyS = false;
				break;
			case KeyEvent.VK_A:
				keyA = false;
				break;
			case KeyEvent.VK_D:
				keyD = false;
				break;
			case KeyEvent.VK_Q:
				keyQ = false;
				break;
			case KeyEvent.VK_E:
				keyE = false;
				break;
			case KeyEvent.VK_UP:
				keyUp = false;
				break;
			case KeyEvent.VK_DOWN:
				keyDown = false;
				break;
			case KeyEvent.VK_LEFT:
				keyLeft = false;
				break;
			case KeyEvent.VK_RIGHT:
				keyRight = false;
				break;
			case KeyEvent.VK_1:
				keyF = false;
				break;
			case KeyEvent.VK_4:
				keyG = false;
				break;
			case KeyEvent.VK_2:
				keyH = false;
				break;
			case KeyEvent.VK_3:
				keyT = false;
				break;	
			case KeyEvent.VK_5:
				keyY = false;
				break;
			case KeyEvent.VK_6:
				keyR = false;
				break;
		}
	}

	private void cameraNMove(float direction) {
		cameraX += cameraN.x * cameraSpeed * direction;
		cameraY += cameraN.y * cameraSpeed * direction;
		cameraZ += cameraN.z * cameraSpeed * direction;
	}

	private void cameraUMove(float direction) {
		cameraX += cameraU.x * cameraSpeed * direction;
		cameraY += cameraU.y * cameraSpeed * direction;
		cameraZ += cameraU.z * cameraSpeed * direction;
	}

	private void cameraVMove(float direction) {
		cameraX += cameraV.x * cameraSpeed * direction;
		cameraY += cameraV.y * cameraSpeed * direction;
		cameraZ += cameraV.z * cameraSpeed * direction;
	}

	private void lightXMove(float direction) {
		currentLightPos.x += lightSpeed * direction;
	}

	private void lightYMove(float direction) {
		currentLightPos.y += lightSpeed * direction;
	}

	private void lightZMove(float direction) {
		currentLightPos.z += lightSpeed * direction;
	}

	private void yaw(float direction) {
		cameraU.rotateAxis(cameraAngle*direction, cameraV.x, cameraV.y, cameraV.z);
    	cameraN.rotateAxis(cameraAngle*direction, cameraV.x, cameraV.y, cameraV.z);

		cameraU.normalize();
		cameraN.normalize();
		cameraV.normalize();
	}

	private void pitch(float direction) {
		cameraV.rotateAxis(cameraAngle*direction, cameraU.x, cameraU.y, cameraU.z);
		cameraN.rotateAxis(cameraAngle*direction, cameraU.x, cameraU.y, cameraU.z);

		cameraV.normalize();
		cameraN.normalize();
		cameraU.normalize();
	}
	
}