attribute vec4 av_Position;//顶点坐标
attribute vec2 af_Position;//纹理坐标
varying vec2 v_texPo;//用于把纹理坐标传到片面着色器中
void main(){
   v_texPo = af_Position;
   gl_Position = av_Position;
}