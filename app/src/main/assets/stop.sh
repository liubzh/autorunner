#!/bin/bash

adb shell <<!
echo AUTORUN_ON=exit > /data/local/tmp/autorun.conf
echo STATUS=NOTHING_TO_DO > /data/local/tmp/autorun_communication
exit
!
