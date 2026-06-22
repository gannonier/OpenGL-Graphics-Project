#version 430

out vec4 color;
in vec3 varyingColor;

void main(void)
{
    color = vec4(varyingColor, 1.0);
}