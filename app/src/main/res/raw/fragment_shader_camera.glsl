#extension GL_OES_EGL_image_external : require
precision mediump float;

varying vec2 texcoordOut;

uniform samplerExternalOES previewTexture;

void main() {
          mediump vec4 textureColor = texture2D(previewTexture,texcoordOut);
          gl_FragColor = vec4(vec3(0.5)+(textureColor.rgb-vec3(0.5))*2.0,textureColor.a);

//             vec2 uv = texcoordOut.xy;
//             float dx = 10.0f * 0.0018f;
//             float dy = 10.0f * 0.001f;
//             vec2 coord = vec2(dx * floor(uv.x / dx), dy * floor(uv.y / dy));
//             vec3 textureColor = texture2D(previewTexture, coord).xyz;
//             gl_FragColor = vec4(textureColor, 1.0);
}
