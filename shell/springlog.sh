#!/usr/bin/bash
case $1 in
start)
  for host in hadoop102 hadoop103 hadoop104 ; do
      echo "===========================启动 '$host' 采集日志============================="
      ssh host "source /etc/profile;nohup java -jar /opt/module/gmall-logger-0.0.1-SNAPSHOT.jar >/dev/null 2>&1 &"
  done
  ;;
stop)
  for host in hadoop102 hadoop103 hadoop104 ; do
      echo "===========================关闭 '$host' 采集日志============================="
      ssh host "jps | grep gmall-logger-0.0.1-SNAPSHOT.jar | awk '{print \$1}'|xargs -n1 kill -9"
  done
  ;;
*)
  echo "输入参数有误"
  echo "start 启动采集"
  echo "stop 停止采集"
  ;;
esac