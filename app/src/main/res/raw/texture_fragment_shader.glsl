precision mediump float;//精度
varying vec2 v_texPo;//传过来的纹理值
uniform sampler2D sTexture;//纹理类型
void main(){
    //第一个参数代表图片纹理，第二个参数代表纹理坐标点，通过GLSL的内建函数texture2D来获取对应位置纹理的颜色RGBA值
   gl_FragColor = texture2D(sTexture,v_texPo);
}