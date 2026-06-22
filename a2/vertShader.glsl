#version 430

layout (location=0) in vec3 position;
layout (location=1) in vec2 tex_coord;
layout (location=2) in vec3 vertNormal;
layout (location = 3) in vec3 vertTangent;

out vec2 tc;
out vec3 varyingNormal;
out vec3 varyingLightDir;
out vec3 varyingVertPos;
out vec3 varyingHalfVector;
out vec3 varyingTangent;
out vec4 shadow_coord;

struct PositionalLight {
	vec4 ambient;
	vec4 diffuse;
	vec4 specular;
	vec3 position;
};

uniform mat4 mv_matrix;
uniform mat4 p_matrix;
uniform mat4 norm_matrix;
uniform PositionalLight light;
layout (binding=0) uniform sampler2D s;
uniform float flipNormal;
uniform mat4 shadowMVP;

void main(void) {	
	varyingVertPos = (mv_matrix*vec4(position,1.0)).xyz;
	varyingLightDir = light.position - varyingVertPos;
	varyingNormal = (norm_matrix * vec4(vertNormal, 0.0)).xyz;
	if (flipNormal < 0.0)
    	varyingNormal = -varyingNormal;
	varyingTangent = (norm_matrix * vec4(vertTangent,1.0)).xyz;

	varyingHalfVector = normalize(normalize(varyingLightDir)
		+normalize(-varyingVertPos)).xyz;

	shadow_coord = shadowMVP * vec4(position, 1.0);
	gl_Position = p_matrix * mv_matrix * vec4(position,1.0);
	tc = tex_coord;
} 
