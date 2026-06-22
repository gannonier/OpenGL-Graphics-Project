#version 430

in vec3 vNormal;
in vec3 vVertPos;
out vec4 fragColor;

layout (binding = 0) uniform samplerCube t;
uniform float reflectStrength;

vec4 applyFog(vec4 originalColor)
{
    vec4 fogColor = vec4(0.7, 0.8, 0.9, 1.0);
    float fogStart = 70.0;
    float fogEnd = 150.0;
    float dist = length(vVertPos);
    float fogFactor = clamp((fogEnd - dist)/(fogEnd - fogStart),0.0,1.0);
    return mix(fogColor, originalColor, fogFactor);
}

void main(void)
{
    vec3 viewDir = normalize(-vVertPos);
    vec3 normal = normalize(vNormal);
    vec3 r = reflect(-viewDir, normal);
    vec3 envColor = texture(t, r).rgb;
    vec3 baseColor = vec3(0.506, 0.616, 0.671);
    vec3 finalColor = mix(baseColor, envColor, reflectStrength);
    fragColor = applyFog(vec4(finalColor, 1.0));
}