#version 430

in vec3 varyingNormal;
in vec3 varyingLightDir;
in vec3 varyingVertPos;
in vec3 varyingHalfVector;
in vec3 varyingTangent;
in vec2 tc;
in vec4 shadow_coord;

out vec4 color;

struct PositionalLight
{	vec4 ambient;  
	vec4 diffuse;  
	vec4 specular;  
	vec3 position;
};

struct Material
{	vec4 ambient;  
	vec4 diffuse;  
	vec4 specular;  
	float shininess;
};

uniform mat4 mv_matrix;
uniform mat4 p_matrix;
uniform mat4 norm_matrix;

uniform vec4 globalAmbient;
uniform PositionalLight light;
uniform Material material;

layout (binding=0) uniform sampler2D s;
layout (binding=1) uniform sampler2D normalMap;
layout (binding = 2) uniform sampler2DShadow shadowTex;
uniform bool useNormalMap;
uniform float alpha;
uniform bool useTexture;
uniform bool useShadow;

vec3 calcNewNormal()
{
    vec3 normal = normalize(varyingNormal);
    vec3 tangent = normalize(varyingTangent);
    tangent = normalize(tangent - dot(tangent, normal) * normal);
    vec3 bitangent = cross(tangent, normal);
    mat3 tbn = mat3(tangent, bitangent, normal);
    vec3 retrievedNormal = texture(normalMap, tc).xyz;
    retrievedNormal = retrievedNormal * 2.0 - 1.0;
    vec3 newNormal = normalize(tbn * retrievedNormal);
    return newNormal;
}

vec4 applyFog(vec4 originalColor)
{
    vec4 fogColor = vec4(0.7, 0.8, 0.9, 1.0);
    float fogStart = 70.0;
    float fogEnd = 150.0;
    float dist = length(varyingVertPos);
    float fogFactor = clamp((fogEnd - dist)/(fogEnd - fogStart),0.0,1.0);
    return vec4(mix(fogColor.rgb, originalColor.rgb, fogFactor), originalColor.a);
}

void main(void) {	
    vec3 L = normalize(varyingLightDir);
    vec3 N = normalize(varyingNormal);

    if (useNormalMap)
        N = calcNewNormal();
    else
        N = normalize(varyingNormal);

    float cosTheta = dot(L,N);

    vec3 H = normalize(varyingHalfVector);

    float cosPhi = dot(H,N);

    float notInShadow = 1.0;
    if (useShadow) {
        vec3 proj = shadow_coord.xyz / shadow_coord.w;
        if (proj.x >= 0.0 && proj.x <= 1.0 &&
            proj.y >= 0.0 && proj.y <= 1.0 &&
            proj.z >= 0.0 && proj.z <= 1.0)
        {
            notInShadow = texture(shadowTex, proj);
        }
    }

    vec3 ambient = ((globalAmbient*material.ambient)+(light.ambient*material.ambient)).xyz;
    vec3 diffuse = light.diffuse.xyz * material.diffuse.xyz * max(cosTheta,0.0);
    vec3 specular = light.specular.xyz * material.specular.xyz * pow(max(cosPhi, 0.0), material.shininess*3.0);

    vec4 texColor;
    if (useTexture)
        texColor = texture(s, tc);
    else
        texColor = vec4(material.diffuse.rgb, 1.0);

    float shadowFactor = mix(0.35, 1.0, notInShadow);
    vec3 litColor = ambient + shadowFactor * (diffuse + specular);
    vec3 finalColor = texColor.rgb * litColor;

    color = applyFog(vec4(finalColor, alpha));
}
