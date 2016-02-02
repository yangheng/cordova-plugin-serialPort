#   cordova-plugin-serialPort
Cordova 安卓串口插件

---

##  安装
---
```  
cordova plugin add cordova-plugin-serialPort
``` 
##  支持平台

-   android
##  安装注意
---

*  需要手动修改AndroidManifest.xml ,在application 节点加上android:name="Application"

```
 <application android:hardwareAccelerated="true" android:icon="@drawable/icon" android:label="@string/app_name" android:name="Application" android:supportsRtl="true">
```
* 安装之前需要修改插件里面的plugin.xml Application.java 的位置,把$package.$dir 替换成app你的package 的目录

```
<source-file src="src/android/Application.java" target-dir="src/$package.$dir"/>
```
*   手动修改 Application.java 的package 换成自己app的package
```
package $package;
```

##  JavaScript API
---

*   获取串口列表
```js
window.SerialPortActivity.getSerialPort(function success(data), function error());
```
`data` 返回的是个串口的device数组

*   打开串口

```js
window.SerialPortActivity.openSerialPort(device, baudRate, function success(data) , function error ());
```
`device` 是getSerialPort 中返回的device
`baudRate` 串口设备波特率

*   注册接收数据监听函数
```js
window.SerialPortActivity.startNotify(receiveCallback,function error ())
  }
var receiveCallback=function(data){
// Decode the ArrayBuffer into a typed Array based on the data you expect
    var unit8 = new Uint8Array(data);
  }
```
`receiveCallback` 接收串口数据的函数，参数 `data` 是 ArrayBuffer，需要转成js对应的数组

*   检测串口是否可用
```js
window.SerialPortActivity.portDetect(function success(data),function error ())
  }
```
`success` 检测串口成功的回调函数，回调参数`data` = 1 ，则可用，为 `data` = 1 则不可用 

*   关闭串口
```
window.SerialPortActivity.closeSerialPort(function success(), function error())
```
最好是只有在程序退出的时候再去调用此接口