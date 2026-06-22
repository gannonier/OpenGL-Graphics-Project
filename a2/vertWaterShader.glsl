#version 430

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 vertNormal;

out vec3 varyingNormal;
out vec3 varyingLightDir;
out vec3 varyingVertPos;
out vec2 tc;
out vec4 glp;

struct PositionalLight
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    vec3 position;
};

uniform PositionalLight light;

uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 p_matrix;
uniform mat4 norm_matrix;

uniform float waterTime;

void main(void)
{
    vec3 animatedPos = position;

    float wave1 = sin(position.x * 1.7 + waterTime * 2.0) * 0.08;
    float wave2 = cos(position.z * 1.3 + waterTime * 1.5) * 0.08;

    animatedPos.y += wave1 + wave2;

    vec4 worldPos = m_matrix * vec4(animatedPos, 1.0);
    vec4 eyePos = v_matrix * worldPos;

    varyingVertPos = eyePos.xyz;
    varyingLightDir = light.position - varyingVertPos;
    varyingNormal = normalize((norm_matrix * vec4(vertNormal, 0.0)).xyz);

    tc = texCoord;

    glp = p_matrix * eyePos;
    gl_Position = glp;
}