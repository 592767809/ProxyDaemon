# ProxyDaemon

一个把闲置 Android 设备变成旁路由的 APP，内置[不良林](https://bulianglin.com/archives/android-gateway.html)的教程中使用的脚本。

为什么会有这个 APP。。。

因为我手机每次重启后，都需要电脑 adb shell 到手机，然后再去执行脚本，我觉得太麻烦了。


## 📕 软件截图

<p align="center">
  <img src="https://raw.githubusercontent.com/Sh-Fang/my-images/main/picgo/20250507121046727.png" alt="图1" width="230"/>
  <img src="https://raw.githubusercontent.com/Sh-Fang/my-images/main/picgo/20250507123847629.png" alt="图2" width="230" />
</p>


## 📋 如何使用

> 设备必须已经Root，并且已经安装了例如v2rayNG这样的代理App

### ProxyDaemon操作演示

App启动时会检测设备是否连接网络，如果网络没有连接，将无法往下进行。

<img src="https://raw.githubusercontent.com/Sh-Fang/my-images/main/picgo/202505161142875.png" alt="image-20250516114232834" style="zoom: 67%;" />

网络检测完成后会检测设备Root情况，如果设备没有Root，将无法往下进行。

设备Root后，会接着检测代理App（v2rayNG等）是否启动，以及代理状态（科学上网）是否连接。

<img src="https://raw.githubusercontent.com/Sh-Fang/my-images/main/picgo/202505161144859.png" alt="image-20250516114459794" style="zoom: 50%;" />

把ProxyDaemon切到后台，启动v2rayNG，返回ProxyDaemon点击“刷新状态”，可以看到提示“应用已启动”

<img src="https://raw.githubusercontent.com/Sh-Fang/my-images/main/picgo/202505161148553.png" alt="image-20250516114823501" style="zoom: 50%;" />

把ProxyDaemon切到后台，选择一个v2rayNG的节点并连接，返回ProxyDaemon点击“刷新状态”，可以看到提示“代理已连接”

<img src="https://raw.githubusercontent.com/Sh-Fang/my-images/main/picgo/202505161149672.png" alt="image-20250516114958624" style="zoom: 50%;" />

此时“立即执行脚本”按钮可以点击，点击按钮，ProxyDaemon会运行内置的脚本（如果设备目前已经有运行的脚本，点击按钮会先终止之前的脚本，再重新运行脚本）

<img src="https://raw.githubusercontent.com/Sh-Fang/my-images/main/picgo/202505161151634.png" alt="image-20250516115152586" style="zoom: 50%;" />

### 如何让其他设备使用这台旁路由

点击界面右上角的ℹ️图标，打开“使用帮助”

<img src="https://raw.githubusercontent.com/Sh-Fang/my-images/main/picgo/202505161153445.png" alt="image-20250516115318396" style="zoom: 50%;" />

如果你之前配置过旁路由，那么“通用”里面的帮助信息就足够用了

<img src="https://raw.githubusercontent.com/Sh-Fang/my-images/main/picgo/202505161154495.png" alt="image-20250516115430454" style="zoom:50%;" />

如果是第一次使用，在侧边栏选择你需要科学上网的设备（例如我的是Windows），就可以按照里面的教程来配置

<p align="center">
  <img src="https://raw.githubusercontent.com/Sh-Fang/my-images/main/picgo/202505161156960.png" alt="image-20250516115636916" alt="图1" width="240"/>
  <img src="https://raw.githubusercontent.com/Sh-Fang/my-images/main/picgo/202505161156641.png" alt="image-20250516115648598" alt="图2" width="240" />
</p>

配置完成后检查效果，电脑没有连接任何代理工具，浏览器可以直接访问谷歌

<img src="https://raw.githubusercontent.com/Sh-Fang/my-images/main/picgo/202505161203065.png" alt="image-20250516120325976" style="zoom:50%;" />

对于那些不太好设置代理的软件，例如CMD，设置旁路由以后也可以直接访问谷歌

<img src="https://raw.githubusercontent.com/Sh-Fang/my-images/main/picgo/202505161207021.png" alt="image-20250516120720964" style="zoom:50%;" />

### 软件更新

点击“关于”，里面当前App版本，以及检查更新的按钮

<img src="https://raw.githubusercontent.com/Sh-Fang/my-images/main/picgo/202505161209425.png" alt="image-20250516120919381" style="zoom:50%;" />




## 📦 下载

[点此下载最新版 APK](https://github.com/Sh-Fang/ProxyDaemon/releases/)  



## 🛠️ 自己构建APK

> 如果你需要自定义脚本，可以在 app/src/main/assets/proxyDaemon.sh 中修改代理脚本

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