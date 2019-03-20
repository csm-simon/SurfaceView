attribute vec4 position;
attribute vec2 texcoord;

varying vec2 texcoordOut;

void main()
{
    texcoordOut = texcoord;
    gl_Position = position;
}
