#!/bin/bash
trap "" HUP

# 与 Java 程序交互一次
# $1: Operation: 操作，与 Java 端程序相对应。
#  Java 分析截图，并将命令写入文件 autorun_communication
function wait_for_java() {
    screenshot
    echo "STATUS=WAITING_FOR_JAVA" > ${COMMUNICATION_FILE}
    echo "OPERATION=${1}"         >> ${COMMUNICATION_FILE}
    local start_looping_time=$(date +%s)
    local now
    sleep 1   # 等待 Java 端分析结果
    while true; do
        now=$(date +%s)
        if ((now - start_looping_time >= 3)); then
            echo "3秒超时，Java 没有为此画面做出响应"
            break;   # 如果耗时3秒以上，就跳出循环
        fi
        source ${COMMUNICATION_FILE}
        if [[ ${STATUS} == GOT_COMMANDS ]]; then
            echo "从 JAVA 端获取到命令"
            break    # 此状态证明从Java端生成了命令，并已执行完成，退出循环
        elif [[ ${STATUS} == NOTHING_TO_DO ]]; then
            echo "NOTHING TO DO"
            break    # 此状态证明 Java 端执行完成，但无需进行任何操作，退出循环
        else
            echo "wait_for_java: looping"
            sleep 1   # 继续等待 Java 端 分析结果
        fi
    done
    rm ${COMMUNICATION_FILE}
    rm ${SCREENSHOT_FILE}
}

# 亮屏解锁
function wakeup_unlock() {
    turn_on_screen   # 亮屏
    lockscreen=$(dumpsys window policy | grep mShowingLockscreen)
    if [[ ${lockscreen} == *mShowingLockscreen=true* ]]; then
        echo "解锁"
        input swipe 550 1260 550 200
        #sleep 1
        # input tap 550 1100
        # input tap 550 814
        # input tap 875 1100
        # input tap 550 1100
        input tap 540 1640
        input tap 540 1640
        input tap 540 1640
        input tap 540 1640
    fi
}

# 启动支付宝程序
function launch_alipay() {
    am start com.eg.android.AlipayGphone/.AlipayLogin
}

# 当前活动
function top_application() {
    local top_app=$(dumpsys activity a | grep "mFocusedActivity:")
    top_app=${top_app%/*}
    top_app=${top_app##* }
    echo "${top_app}" 
}

# 判断当前活动是否为支付宝
function top_app_is_alipay() {
    if [[ $(top_application) == com.eg.android.AlipayGphone ]]; then
        echo true
    else
        echo false
    fi
}

# 是否亮屏， ON 为亮屏， OFF 为灭屏
function screen_state() {
    powerstate=$(dumpsys power | grep "Display Power: state=")
    if [[ ${powerstate} == *ON ]]; then
        echo ON
    else
        echo OFF
    fi
}

function turn_on_screen() {
    if [[ $(screen_state) == OFF ]]; then
        echo "亮屏"
        input keyevent 26
        sleep 1
    fi
}

function turn_off_screen() {
    if [[ $(screen_state) == ON ]]; then
        echo "灭屏"
        input keyevent 26
        sleep 1
    fi
}

# 截屏并保存文件
function screenshot() {
    screencap -p ${SCREENSHOT_FILE}
}

AUTORUN_DIR="/data/local/tmp"
CONFIG_FILE="${AUTORUN_DIR}/autorun.conf"
SCREENSHOT_FILE="${AUTORUN_DIR}/autorun_screenshot.png"
COMMUNICATION_FILE="${AUTORUN_DIR}/autorun_communication"   # 用这个文件将当前画面所要的操作传递给 Java 端。
SCREEN_STATE_FILE="${AUTORUN_DIR}/autorun.screen"   # 保存
#LOOP_TIME=600    # 10分钟
LOOP_TIME=480    # 8分钟
#LOOP_TIME=900    # 15分钟
LOOP_WAIT_TIME=1   # 1 秒

alias check_top_application='
if [[ $(top_app_is_alipay) == false ]]; then
    echo "流程中止"
    continue
fi
'

function main() {
    local count=1
    local last_time_sec=0
    local now_sec
    local executing
    echo  "自动执行开始"
    while true; do
        # 读取配置项
        source "${CONFIG_FILE}"
        # 判断是否结束脚本
        if [[ ${AUTORUN_ON} == exit ]]; then
            break
        elif [[ ${AUTORUN_ON} != true ]]; then
            sleep ${LOOP_WAIT_TIME}
            continue
        fi
        if [[ $(screen_state) == ON ]]; then
            sleep ${LOOP_WAIT_TIME}
            continue
        fi
        # 只有灭屏状态才能触发喂鸡流程。
        # source "${SCREEN_STATE_FILE}"
        # if [[ ${SCREEN_STATE} == ON ]]; then
        #     sleep ${LOOP_WAIT_TIME}
        #     continue
        # fi
        now_sec=$(date +%s)
        if ((now_sec - last_time_sec < ${LOOP_TIME})); then
            #echo "计算循环时间 休眠 1s"
            sleep ${LOOP_WAIT_TIME}
            continue
        fi
        echo "第 ${count} 次: $(date "+%Y-%m-%d %H:%M:%S")"
        last_time_sec=$(date +%s)
        wakeup_unlock                     # 亮屏解锁
        echo "启动支付宝"
        launch_alipay                     # 启动支付宝
        sleep 1                           # 等待初始化
        check_top_activity
        input tap 133 1887                # 点击首页
        echo "进入蚂蚁庄园"
        input tap 148 921                 # 点击蚂蚁庄园图标
        sleep 3                           # 等待网络加载
        check_top_activity
        input tap 400 1400                # 点击左边偷吃小鸡
        input tap 852 1400                # 点击右边偷吃小鸡
        input tap 215 1460                # 点击爱心鸡蛋
        input tap 918 1772                # 点击饲料
        check_top_activity
        echo "使用加速卡"
        input tap 960 660                 # 点击道具
        #wait_for_java SPEED_UP           # 使用加速卡
        input tap 887 1748                # 点击加速卡
        input tap 758 1246                # 点击使用按钮
        sleep 3
        check_top_activity
        input tap 1020 1198               # 关闭道具界面
        echo "查看好友列表"
        input tap 162 1735                # 好友
        sleep 2                           # 等待网络加载
        check_top_activity
        wait_for_java NOTIFY_FRIENDS      # 通知好友 第一页
        check_top_activity
        input swipe 550 1850 550 920 1200 # 向上滑动好友列表
        wait_for_java NOTIFY_FRIENDS      # 通知好友 第二页
        check_top_activity
        input swipe 550 1850 550 920 1200 # 向上滑动好友列表
        wait_for_java NOTIFY_FRIENDS      # 通知好友 第三页
        check_top_activity
        input keyevent 4                  # 退出好友界面
        input tap 400 1400                # 确认左边偷吃小鸡已赶跑
        input tap 852 1400                # 确认右边偷吃小鸡已赶跑
        input tap 918 1772                # 确认饲料已喂养
        input keyevent 4                  # 退出蚂蚁庄园
        input keyevent 4                  # 退出支付宝
        turn_off_screen                   # 灭屏
        count=$((count+1))
    done
    echo "自动执行结束"
}

main "$@"
