#version 150 core


uniform float m00 = 1.; // X homothetic value in 3x3 matrix
uniform float m02 = 0.; // X translation value in 3x3 matrix
uniform float m11 = 1.; // Y homothetic value in 3x3 matrix
uniform float m12 = 0.; // Y translation value in 3x3 matrix
uniform float screenWidth;
uniform float screenHeight;


in vec2 vertexPosition;
in vec2 vertexUV;
in vec2 vertexNormal;
in float vertexCurvature;
in float vertexThickness;

out vec2 fragmentUV;
out float fragmentCurvature;
out float fragmentThickness;


void main() {
	//gl_Position = vec4 ( vertexPosition , 1f );
	gl_Position = vec4( -1 + 2 * (vertexPosition.x * m00 + m02) / (screenWidth + 1), 1 - 2 * ( vertexPosition.y * m11 + m12 ) / ( screenHeight + 1 ), 0, 1);
	fragmentUV = vertexUV;
	fragmentCurvature = vertexCurvature;
	fragmentThickness = vertexThickness;
}