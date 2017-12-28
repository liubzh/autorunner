#!/bin/bash

adb shell <<!
echo AUTORUN_ON=false > /data/local/tmp/autorun.conf
echo SCREEN_STATE= > /data/local/tmp/autorun.screen
rm /data/local/tmp/screenshot*
echo "使用 Ctrl-C 结束命令即可，脚本进程会保留"
source /data/local/tmp/autorun.sh > /data/local/tmp/autorun.log
!
