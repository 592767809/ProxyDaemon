# ProxyDaemon

把闲置的安卓设备变成旁路由工具，以 v2ray 为代理工具，以[不良林](https://bulianglin.com/archives/android-gateway.html)的脚本作为基础。

## 功能特点

- ✅ UI 操作一键执行脚本
- ✅ 实时日志输出
- ✅ V2Ray 状态指示
- ✅ 自动复制脚本到系统目录

## 截图

<img src="images/fig1.png" alt="截图" width="300"/>

## 构建方法

### 环境要求

- Android Studio Giraffe 或更高版本
- Kotlin 1.9+
- Android SDK 24+
- **已 root 的 Android 设备**（执行 `su` 命令）

### 构建 APK

```bash
./gradlew assembleRelease
```

构建输出路径：
```BASH
app/build/outputs/apk/release/ProxyDaemon-release-v1.x.x.apk
```

你也可以直接使用 Android Studio 的 Build 菜单进行构建。

# 权限声明
本项目需要 root 权限以运行系统脚本，请确保你了解相关安全风险。

# License
[MIT License](./LICENSE) - 自由使用，注意风险自负。