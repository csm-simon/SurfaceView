#extension GL_OES_EGL_image_external : require
precision mediump float;

varying vec2 texcoordOut;

const highp vec3 w = vec3(0.299, 0.587, 0.114);

uniform samplerExternalOES previewTexture;

uniform float beautyLevel;//(0-10)

void main() {
          highp vec3 textureColor = texture2D(previewTexture,texcoordOut).rgb;

          vec2 blurCoordinates[20];
          vec2 singleStepOffset = vec2(0.001,0.001);
          blurCoordinates[0] = texcoordOut.xy + singleStepOffset * vec2(0.0, -10.0);
          blurCoordinates[1] = texcoordOut.xy + singleStepOffset * vec2(0.0, 10.0);
          blurCoordinates[2] = texcoordOut.xy + singleStepOffset * vec2(-10.0, 0.0);
          blurCoordinates[3] = texcoordOut.xy + singleStepOffset * vec2(10.0, 0.0);
          blurCoordinates[4] = texcoordOut.xy + singleStepOffset * vec2(5.0, -8.0);
          blurCoordinates[5] = texcoordOut.xy + singleStepOffset * vec2(5.0, 8.0);
          blurCoordinates[6] = texcoordOut.xy + singleStepOffset * vec2(-5.0, 8.0);
          blurCoordinates[7] = texcoordOut.xy + singleStepOffset * vec2(-5.0, -8.0);
          blurCoordinates[8] = texcoordOut.xy + singleStepOffset * vec2(8.0, -5.0);
          blurCoordinates[9] = texcoordOut.xy + singleStepOffset * vec2(8.0, 5.0);
          blurCoordinates[10] = texcoordOut.xy + singleStepOffset * vec2(-8.0, 5.0);
          blurCoordinates[11] = texcoordOut.xy + singleStepOffset * vec2(-8.0, -5.0);
          blurCoordinates[12] = texcoordOut.xy + singleStepOffset * vec2(0.0, -6.0);
          blurCoordinates[13] = texcoordOut.xy + singleStepOffset * vec2(0.0, 6.0);
          blurCoordinates[14] = texcoordOut.xy + singleStepOffset * vec2(6.0, 0.0);
          blurCoordinates[15] = texcoordOut.xy + singleStepOffset * vec2(-6.0, 0.0);
          blurCoordinates[16] = texcoordOut.xy + singleStepOffset * vec2(-4.0, -4.0);
          blurCoordinates[17] = texcoordOut.xy + singleStepOffset * vec2(-4.0, 4.0);
          blurCoordinates[18] = texcoordOut.xy + singleStepOffset * vec2(4.0, -4.0);
          blurCoordinates[19] = texcoordOut.xy + singleStepOffset * vec2(4.0, 4.0);

          // 拿到中心点的g通道，并乘以权值20
          float sampleColor = textureColor.g * 20.0;

          // 越远的地方权值越小，近一点的8个点权值给2
          sampleColor += texture2D(previewTexture, blurCoordinates[0]).g;
          sampleColor += texture2D(previewTexture, blurCoordinates[1]).g;
          sampleColor += texture2D(previewTexture, blurCoordinates[2]).g;
          sampleColor += texture2D(previewTexture, blurCoordinates[3]).g;
          sampleColor += texture2D(previewTexture, blurCoordinates[4]).g;
          sampleColor += texture2D(previewTexture, blurCoordinates[5]).g;
          sampleColor += texture2D(previewTexture, blurCoordinates[6]).g;
          sampleColor += texture2D(previewTexture, blurCoordinates[7]).g;
          sampleColor += texture2D(previewTexture, blurCoordinates[8]).g;
          sampleColor += texture2D(previewTexture, blurCoordinates[9]).g;
          sampleColor += texture2D(previewTexture, blurCoordinates[10]).g;
          sampleColor += texture2D(previewTexture, blurCoordinates[11]).g;
          sampleColor += texture2D(previewTexture, blurCoordinates[12]).g * 2.0;
          sampleColor += texture2D(previewTexture, blurCoordinates[13]).g * 2.0;
          sampleColor += texture2D(previewTexture, blurCoordinates[14]).g * 2.0;
          sampleColor += texture2D(previewTexture, blurCoordinates[15]).g * 2.0;
          sampleColor += texture2D(previewTexture, blurCoordinates[16]).g * 2.0;
          sampleColor += texture2D(previewTexture, blurCoordinates[17]).g * 2.0;
          sampleColor += texture2D(previewTexture, blurCoordinates[18]).g * 2.0;
          sampleColor += texture2D(previewTexture, blurCoordinates[19]).g * 2.0;

          // 得到中心点模糊后的像素值
          sampleColor = sampleColor / 48.0;

          //高反差保留是Photoshop中的一个效果滤镜，主要删除图像中颜色变化不大的像素，保留色彩变化较大的部分，使图像中的阴影消失，边缘像素得以保留，亮调部分更加突出。

          highp float highPass = textureColor.g - sampleColor + 0.5;

          // 强光照处理
          for (int i=0;i<5;i++) {
                if (highPass < 0.5) {
                    highPass = highPass * highPass * 2.0;
                } else {
                    highPass = 1.0 - ((1.0 - highPass)*(1.0 - highPass)*2.0);
                }
          }

          float luminance = dot(textureColor,w);// 生成原像素的灰度值

          float alpha = pow(luminance,beautyLevel);// 生成的luminance值是位于[0,1]之间的闭区间，这样传入的param越小，alpha越大

          vec3 smoothColor = textureColor + (textureColor - vec3(highPass)) * alpha * 0.1;// 平滑

          gl_FragColor = vec4(mix(smoothColor.rgb,max(smoothColor,textureColor),alpha),1.0);// mix(x, y, a)是在两个值之间线性插值，通过 x*(1−a)+y*a 计算得到返回

//          gl_FragColor = texture2D(previewTexture, texcoordOut);

//对比度
//          mediump vec4 textureColor = texture2D(previewTexture,texcoordOut);
//          gl_FragColor = vec4(vec3(0.icon_beauty_level_5)+(textureColor.rgb-vec3(0.icon_beauty_level_5))*2.0,textureColor.a);

//马赛克
//             vec2 uv = texcoordOut.xy;
//             float dx = 10.0f * 0.0018f;
//             float dy = 10.0f * 0.001f;
//             vec2 coord = vec2(dx * floor(uv.x / dx), dy * floor(uv.y / dy));
//             vec3 textureColor = texture2D(previewTexture, coord).xyz;
//             gl_FragColor = vec4(textureColor, 1.0);

}
