#version 430

in vec3 varyingNormal;
in vec3 varyingLightDir;
in vec3 varyingVertPos;
in vec2 tc;
in vec4 glp;

out vec4 color;

layout (binding = 0) uniform sampler2D reflectTex;
layout (binding = 1) uniform sampler2D refractTex;

struct PositionalLight
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    vec3 position;
};

struct Material
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    float shininess;
};

uniform vec4 globalAmbient;
uniform PositionalLight light;
uniform Material material;

uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 p_matrix;
uniform mat4 norm_matrix;

uniform float waterTime;

void main(void)
{
    vec3 L = normalize(varyingLightDir);
    vec3 baseN = normalize(varyingNormal);
    vec3 V = normalize(-varyingVertPos);

    float waveX = sin(tc.x * 40.0 + waterTime * 2.5) * 0.08;
    float waveZ = cos(tc.y * 35.0 + waterTime * 2.0) * 0.08;

    vec3 N = normalize(baseN + vec3(waveX, 0.0, waveZ));
    vec3 Nfresnel = baseN;                              

    vec3 R = normalize(reflect(-L, N));
    float cosPhi = max(dot(V, R), 0.0);

    vec3 ambient = ((globalAmbient * material.ambient) + (light.ambient * material.ambient)).xyz;
    vec3 diffuse = light.diffuse.xyz * material.diffuse.xyz * max(dot(L, N), 0.0);
    vec3 specular = light.specular.xyz * material.specular.xyz * pow(cosPhi, material.shininess);

    vec2 screenTC = (glp.xy / glp.w) * 0.5 + 0.5;

    vec2 distortion;
    distortion.x = sin(tc.y * 30.0 + waterTime * 2.0) * 0.012;
    distortion.y = cos(tc.x * 30.0 + waterTime * 2.3) * 0.012;

    vec2 refractTC = clamp(screenTC + distortion, 0.001, 0.999);
    vec2 reflectTC = clamp(vec2(screenTC.x, 1.0 - screenTC.y) + distortion, 0.001, 0.999);

    vec4 refractColor = texture(refractTex, refractTC);
    vec4 reflectColor = texture(reflectTex, reflectTC);

    float cosTheta = clamp(dot(V, Nfresnel), 0.0, 1.0);
    float F0 = 0.06;
    float fresnel = F0 + (1.0 - F0) * pow(1.0 - cosTheta, 5.0);
    fresnel = clamp(fresnel * 1.25, 0.06, 0.70);

    vec4 blueTint = vec4(0.0, 0.32, 0.90, 1.0);
    reflectColor.rgb = mix(reflectColor.rgb, blueTint.rgb, 0.28);
    refractColor.rgb = mix(refractColor.rgb, blueTint.rgb, 0.18);
    vec4 waterColor = mix(refractColor, reflectColor, fresnel);
    waterColor.rgb = mix(waterColor.rgb, blueTint.rgb, 0.18);

    color = vec4(waterColor.rgb + specular * 0.45, 0.85);
}