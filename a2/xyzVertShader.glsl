#version 430

layout (location=0) in vec3 position;

uniform mat4 mv_matrix;
uniform mat4 p_matrix;
uniform bool is_sphere;

out vec3 varyingColor;

void main(void)
{
    gl_Position = p_matrix * mv_matrix * vec4(position, 1.0);
    if (is_sphere) 
        varyingColor = vec3(1.0,1.0,0.0);
    else 
        if (gl_VertexID < 2)
            varyingColor = vec3(1.0, 0.0, 0.0); 
        else if (gl_VertexID < 4)
            varyingColor = vec3(0.0, 1.0, 0.0);  
        else
            varyingColor = vec3(0.0, 0.0, 1.0); 
    

}