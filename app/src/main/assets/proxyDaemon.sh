#!/system/bin/sh

# tun interface (VPN tunnel)
tun='tun0'

# device interface to monitor (Wi-Fi client here, usually wlan0;
# if hotspot, might be ap0 or wlan1 depending on device)
dev='wlan0'

# loop interval in seconds
interval=3

# routing policy priority
pref=18000

# Enable IPv4 forwarding (turn the phone into a router)
sysctl -w net.ipv4.ip_forward=1

# Flush FORWARD chain (remove old forwarding rules)
iptables -F FORWARD

# NAT: masquerade all traffic going out through tun0
iptables -t nat -A POSTROUTING -o $tun -j MASQUERADE

# Add routing policy rules:
# 1) Default rule:
#    "ip rule add from all table main pref $pref"
#    - from all          : applies to packets from any source IP
#    - table main        : use the default 'main' routing table
#    - pref $pref        : the priority of this rule (higher number = lower priority)
#    Purpose: ensures that, by default, all packets follow the standard system routing.
ip rule add from all table main pref $pref

# 2) Interface-specific rule:
#    "ip rule add from all iif $dev table $tun pref $(expr $pref - 1)"
#    - from all          : applies to packets from any source IP
#    - iif $dev          : only packets entering the kernel via interface $dev (here wlan0)
#    - table $tun        : use the routing table associated with the VPN tunnel (tun0)
#    - pref $(expr $pref - 1) : priority slightly higher than the default, so it is matched first
#    Purpose: forces any traffic coming from the monitored interface ($dev) to be routed
#             through the VPN tunnel, overriding the default routing table.
ip rule add from all iif $dev table $tun pref $(expr $pref - 1)

# This string will be used to check if the rule still exists
contain="from all iif $dev lookup $tun"

# Start infinite loop to monitor and restore settings
while true ;do
    # Keep ip_forward = 1
    if [ "$(cat /proc/sys/net/ipv4/ip_forward)" = "0" ]; then
        sysctl -w net.ipv4.ip_forward=1
        echo "[$(date '+%H:%M:%S')] ip_forward was reset, enabled again"
    fi

    # Check and restore ip rule
    if [[ $(ip rule) != *$contain* ]]; then
             # Check if the device interface ($dev) is UP (active).
             # If the expected device name ($dev) is not found in this list,
             # It means the interface is either:
             # - down (manually disabled),
             # - disconnected (no Wi-Fi link),
             # - or not present at all.
            if [[ $(ip ad|grep 'state UP') != *$dev* ]]; then
                echo -e "[$(date "+%H:%M:%S")]dev has been lost."
            else
                # Otherwise, re-add the rule to force traffic from $dev to tun0
                ip rule add from all iif $dev table $tun pref $(expr $pref - 1)
                echo -e "[$(date "+%H:%M:%S")]network changed, reset the routing policy."
            fi
    fi

    # Wait before next check
    sleep $interval
done