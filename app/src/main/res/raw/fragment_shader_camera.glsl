#extension GL_OES_EGL_image_external : require
precision mediump float;

varying vec2 texcoordOut;

uniform samplerExternalOES previewTexture;

void main() {
          mediump vec4 textureColor = texture2D(previewTexture,texcoordOut);
          gl_FragColor = vec4(vec3(0.5)+(textureColor.rgb-vec3(0.5))*2.0,textureColor.a);
}
