package com.example.proxydaemon.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.util.Locale

object NetworkUtils {
    val networkInfo = mutableMapOf<String, String>()

    fun isWifiConnected(context: Context): Boolean {
        // 获取当前 WiFi 连接状态
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = network?.let { connectivityManager.getNetworkCapabilities(it) }
        val isWifiConnected = capabilities?.let {
            it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    it.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
        } == true
        return isWifiConnected
    }

    fun initNetworkInfo(): Boolean{

        try {
            // 获取当前 IP 地址
            val ipOutput = RootShell.rootExec("ip addr show wlan0")
            val ipRegex = Regex("inet (\\d+\\.\\d+\\.\\d+\\.\\d+)")
            val ipMatch = ipRegex.find(ipOutput)
            ipMatch?.let {
                networkInfo["ip"] = it.groupValues[1]
            }

            // 获取当前SSID
            val ssid = RootShell.rootExec("iw dev wlan0 link | grep SSID | awk '{print $2}'").trim()
            networkInfo["ssid"] = ssid

            // 获取子网掩码
            val netmaskOutput = RootShell.rootExec("ip addr show wlan0 | grep inet")
            val netmaskRegex = Regex("inet \\d+\\.\\d+\\.\\d+\\.\\d+/([0-9]+)")
            val netmaskMatch = netmaskRegex.find(netmaskOutput)
            netmaskMatch?.let {
                val cidr = it.groupValues[1].toInt()
                val netmask = cidrToNetmask(cidr)
                networkInfo["netmask"] = netmask
            }

            // 获取路由器IP
            networkInfo["routerIP"] = getRouterIp(networkInfo["ip"].toString(),networkInfo["netmask"].toString()).toString()

            // 获取DNS
            networkInfo["dns"] = networkInfo["routerIP"].toString()

            // 网络前缀长度
            val netmaskPrefix = RootShell.rootExec("ip addr show wlan0 | grep inet | awk '{print $2}'").split("\n")[0].split("/")[1]
            networkInfo["netmaskPrefix"] = netmaskPrefix.toString()

        }catch (e: Exception){
            e.printStackTrace()
            return false
        }

        return true
    }

    // 将CIDR转换为子网掩码
    fun cidrToNetmask(cidr: Int): String {
        val mask = (0xffffffff shl (32 - cidr)) and 0xffffffff
        return String.format(Locale.US, "%d.%d.%d.%d",
            (mask shr 24) and 0xff,
            (mask shr 16) and 0xff,
            (mask shr 8) and 0xff,
            mask and 0xff)
    }

    // 获取路由器IP
    fun getRouterIp(ip: String, netmask: String): String? {
        val ipParts = ip.split(".")
        val maskParts = netmask.split(".")

        if (ipParts.size != 4 || maskParts.size != 4) return null

        val gatewayParts = mutableListOf<String>()

        for (i in 0..3) {
            // 如果子网掩码是255，说明该部分保留，直接用原 IP 的那部分
            // 否则（为0），说明这一段是主机位，设置为1（通常是网关）
            gatewayParts.add(
                if (maskParts[i] == "255") ipParts[i]
                else if (maskParts[i] == "0") "1"
                else "1" // 对于非255非0的情况，用 1 近似处理
            )
        }

        return gatewayParts.joinToString(".")
    }
}