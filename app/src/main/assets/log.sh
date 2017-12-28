#!/bin/bash

adb shell<<!
tail -n20 /data/local/tmp/autorun.log
exit
!
