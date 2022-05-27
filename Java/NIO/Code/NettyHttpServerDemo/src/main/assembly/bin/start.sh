#!/bin/bash
PRO_NAME="NettyHttpServerDemo"
#服务参数
JAR_NAME="NettyHttpServerDemo.jar"
MAIN_CLASS="com.crazymakercircle.Bootstrap.EchoServerBootstrapApplication"
WORK_PATH="/work/${PRO_NAME}"
JVM="-server -Xms64m -Xmx256m"

LOG="${WORK_PATH}/logs/console.log"
REMOTE_CONFIG="-Xdebug -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n"

echo "JVM:$JVM"


RETVAL="0"

# See how we were called.
function start() {
    if [ ! -f ${LOG} ]; then
        touch ${LOG}
    fi
        nohup java ${JVM} -jar ${WORK_PATH}/lib/${JAR_NAME} ${MAIN_CLASS} >> ${LOG} 2>&1 &
    status
}

function stop() {
    pid=$(ps -ef | grep -v 'grep' | egrep $JAR_NAME| awk '{printf $2 " "}')
    if [ "$pid" != "" ]; then
        echo -n $"Shutting down boot: "
        kill -9 "$pid"
    else
        echo "${JAR_NAME} is stopped"
    fi
    status
}

function debug() {
    echo " start remote debug mode .........."
    if [ ! -f ${LOG} ]; then
        touch ${LOG}
    fi
        nohup java ${JVM} ${REMOTE_CONFIG}  -jar ${WORK_PATH}/lib/${JAR_NAME} ${MAIN_CLASS} >> ${LOG} 2>&1 &
}

function status(){
    pid=$(ps -ef | grep -v 'grep' | egrep $JAR_NAME| awk '{printf $2 " "}')
    #echo "$pid"
    if [ "$pid" != "" ]; then
        echo "${JAR_NAME} is running,pid is $pid"
    else
        echo "${JAR_NAME} is stopped"
    fi
}

function usage(){
    echo "Usage: $0 {start|debug|stop|restart|status}"
    RETVAL="2"
}

# See how we were called.
case "$1" in
    start)
        start
    ;;
    debug)
        debug
    ;;
    stop)
        stop
    ;;
    restart)
        stop
    	start
    ;;
    status)
        status
    ;;
    *)
        usage
    ;;
esac

exit ${RETVAL}
