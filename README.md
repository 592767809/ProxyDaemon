# ProxyDaemon

把闲置的安卓设备变成旁路由工具，以 v2ray 为代理工具，以[不良林](https://bulianglin.com/archives/android-gateway.html)的脚本作为基础。


## ✨功能介绍

- ✅ 自动检测 V2Ray 应用以及 V2Ray 代理是否已启动
- ✅ 一键执行 proxy.sh 脚本（需要 Root 权限）
- ✅ 实时日志输出
- ✅ 自动复制脚本到系统目录

## 📕 截图

<img src="images/fig1.png" alt="截图" width="300"/>

## 🛠️ 构建方法

克隆项目

```bash
git clone https://github.com/Sh-Fang/ProxyDaemon.git
```

打开Android Studio，选择项目目录，点击 Build -> Generate Bundle/APK -> Generate APKs

构建输出路径：
```BASH
app/build/outputs/apk/release/ProxyDaemon-release-v1.x.x.apk
```

# 📋 使用说明

## 前提条件：

- 手机已获取 Root 权限

- V2Ray 已在后台运行，并且已经连接了一个节点

## 第一次使用：

- 启动应用后，自动拷贝脚本 proxyDaemon.sh 到 /data/local/tmp/ 并自动赋予执行权限（chmod +x）

- 点击「立即执行脚本」按钮（请确保 V2Ray 已启动，并且已经连接了一个节点），后台运行脚本

- 若未检测到 V2Ray，按钮会置灰无法点击

## 刷新状态

- 启动 V2Ray 后，点击「刷新状态」按钮，可以重新检测，此时按钮可以点击

## 修改脚本：

- 如果需要修改脚本，可以在 assets/proxyDaemon.sh 中编写你自己的代理设置脚本


## 权限声明

- 本项目需要 root 权限以运行系统脚本，请确保你了解相关安全风险。

# 📝 License
[MIT License](./LICENSE) - 自由使用，注意风险自负。