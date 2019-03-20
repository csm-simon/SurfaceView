precision highp float;

varying vec2 texcoordOut;
uniform sampler2D originalTexture;

void main()
{
    gl_FragColor = texture2D(originalTexture, texcoordOut);
}