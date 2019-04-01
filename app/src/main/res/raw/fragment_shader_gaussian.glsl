precision highp float;

uniform sampler2D originalTexture;

varying highp vec2 texcoordOut;

uniform float radius;
uniform int type;

void main()
{
    lowp vec4 sum = vec4(0.0);
    if(type == 0)
    {
        sum += texture2D(originalTexture, texcoordOut + vec2(0,-radius*4.0)) * 0.05;
        sum += texture2D(originalTexture, texcoordOut + vec2(0,-radius*3.0)) * 0.09;
        sum += texture2D(originalTexture, texcoordOut + vec2(0,-radius*2.0)) * 0.12;
        sum += texture2D(originalTexture, texcoordOut + vec2(0,-radius*1.0)) * 0.15;
        sum += texture2D(originalTexture, texcoordOut) * 0.18;
        sum += texture2D(originalTexture, texcoordOut + vec2(0,radius*1.0)) * 0.15;
        sum += texture2D(originalTexture, texcoordOut + vec2(0,radius*2.0)) * 0.12;
        sum += texture2D(originalTexture, texcoordOut + vec2(0,radius*3.0)) * 0.09;
        sum += texture2D(originalTexture, texcoordOut + vec2(0,radius*4.0)) * 0.05;
    } else {
        sum += texture2D(originalTexture, texcoordOut + vec2(-radius*4.0,0)) * 0.05;
        sum += texture2D(originalTexture, texcoordOut + vec2(-radius*3.0,0)) * 0.09;
        sum += texture2D(originalTexture, texcoordOut + vec2(-radius*2.0,0)) * 0.12;
        sum += texture2D(originalTexture, texcoordOut + vec2(-radius*1.0,0)) * 0.15;
        sum += texture2D(originalTexture, texcoordOut) * 0.18;
        sum += texture2D(originalTexture, texcoordOut + vec2(radius*1.0,0)) * 0.15;
        sum += texture2D(originalTexture, texcoordOut + vec2(radius*2.0,0)) * 0.12;
        sum += texture2D(originalTexture, texcoordOut + vec2(radius*3.0,0)) * 0.09;
        sum += texture2D(originalTexture, texcoordOut + vec2(radius*4.0,0)) * 0.05;
    }
	gl_FragColor = sum;
}