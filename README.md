# ProxyDaemon

一个把闲置 Android 设备变成旁路由的 APP，内置[不良林](https://bulianglin.com/archives/android-gateway.html)的教程中使用的脚本。

为什么会有这个 APP。。。

因为我手机每次重启后，都需要电脑 adb shell 到手机，然后再去执行脚本，我觉得太麻烦了。


## 📕 软件截图

<p align="center">
  <img src="images/fig1.png" alt="图1" width="230" style="margin-right: 20px;"/>
  <img src="images/fig2.png" alt="图2" width="230" style="margin-right: 20px;"/>
</p>

## ✨ 功能介绍

- ✅ 自动检测 V2Ray 应用以及 V2Ray 代理是否已启动
- ✅ 自动复制脚本到系统目录
- ✅ 一键执行 proxy.sh 脚本（需要 Root 权限）
- ✅ 实时日志输出



## 📋 使用说明

> 再次提醒：这个 APP 是安装到闲置的并且已经 Root 过的安卓设备上的，按照下面的使用方法启动脚本后，这台安卓设备就变成了一个旁路由设备。


### 前提条件：

- 手机已获取 Root 权限

- 手机安装了 V2ray 应用，并有可以用的节点

### 第一次使用：

- APP打开后，会自动检测相关状态，如果状态全部 OK 才能执行脚本
  
- APP会把内置的脚本 proxyDaemon.sh 拷贝到 /data/local/tmp/ 并自动赋予执行权限（chmod +x）

- 点击「立即执行脚本」按钮，后台运行脚本

- 界面右上角有一个ℹ️图标，点击这个图标可以进入帮助页面，里面有教程，指导你如何设置其他设备使用该旁路由（包括Android，iOS，Windows，MacOS，Linux）


### 刷新状态

- 手动启动 V2Ray 后，页面状态不会自动变化，需要手动点击「刷新状态」按钮，然后再继续操作

### 修改脚本：

- 如果需要修改脚本，可以在 assets/proxyDaemon.sh 中编写你自己的代理设置脚本




## 📦 下载

[点此下载最新版 APK](https://github.com/Sh-Fang/ProxyDaemon/releases/)  





## 🛠️ 自己构建APK

克隆项目

```bash
git clone https://github.com/Sh-Fang/ProxyDaemon.git
```

打开Android Studio，选择项目目录，点击 Build -> Generate Bundle/APK -> Generate APKs

构建输出路径：
```BASH
app/build/outputs/apk/release/ProxyDaemon-release-v1.x.x.apk
```



## 📝 License
[MIT License](./LICENSE) - 自由使用，注意风险自负。