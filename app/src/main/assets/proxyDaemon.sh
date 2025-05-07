#!/system/bin/sh

# 变量定义
tun='tun0'             # 虚拟接口
dev='wlan0'            # 物理接口
interval=3             # 检测间隔
pref=18000             # 策略路由优先级

# 开启 IP 转发
sysctl -w net.ipv4.ip_forward=1

# 清除已有规则
iptables -F FORWARD
iptables -t nat -F POSTROUTING

# 添加 NAT 转换
iptables -t nat -A POSTROUTING -o $tun -j MASQUERADE

# 添加路由策略
ip rule add from all table main pref $pref
ip rule add from all iif $dev table $tun pref $(expr $pref - 1)

# 允许局域网发往代理（$dev -> $tun）
iptables -A FORWARD -i $dev -o $tun -j ACCEPT

# 允许代理回包到局域网（$tun -> $dev）
iptables -A FORWARD -i $tun -o $dev -m state --state ESTABLISHED,RELATED -j ACCEPT

contain="from all iif $dev lookup $tun"

# 无限循环检测网络状态并恢复策略
while true; do
    now=$(date "+%H:%M:%S")
    ip rule | grep -q "$contain"
    rule_ok=$?

    ip addr | grep -q "$dev"
    dev_up=$?

    if [ "$rule_ok" -ne 0 ]; then
        if [ "$dev_up" -ne 0 ]; then
            echo "[$now] $dev is down"
        else
            ip rule add from all iif $dev table $tun pref $(expr $pref - 1)
            echo "[$now] Reset routing policy for $dev"
        fi
    fi
    sleep "$interval"
done
