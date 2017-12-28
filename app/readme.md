# AutoRunner

它自动打开运行蚂蚁庄园。并喂养小鸡、赶走偷吃小鸡、使用加速卡等功能。

执行以下命令：
```
adb push autorun.sh /data/local/tmp
adb shell
source /data/local/tmp/autorun.sh > /data/local/tmp/autorun.log
```
执行以上命令使脚本运行，然后打开APK，点击按钮开始自动运行，应用程序的目的是使设备固定时间唤醒，脚本程序的目的是执行点击操作。


```
adb shell tail -n10 /data/local/tmp/autorun.log
```
使用以上命令查看当前的程序状态。

```
adb shell
echo AUTORUN_ON=exit > /data/local/tmp/autorun.conf
```
使用以上命令结束脚本端程序，停止以后需要再次执行`source autorun.sh`的那段命令才可以。