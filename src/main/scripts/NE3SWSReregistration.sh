#! /bin/bash

DN="$1"
UNREG="/opt/oss/NSN-ne3sws_core/install/bin/ne3swsUnRegisterAgent.sh"
REG="/opt/oss/NSN-ne3sws_core/install/bin/ne3swsRegisterAgent.sh"

echo "Starting to un-register $DN..."
START_TIME=`date +%s`
$UNREG $DN
if [ $? -eq 0 ];then
    echo "Un-registration succeeded for $DN."
    echo "Starting to register $DN..."
    $REG $DN
        if [ $? -eq 0 ];then
            echo "Registration succeeded for $DN."
            exit 0
        else
            echo "Registration failed for $DN."
            exit 2
        fi
else
    echo "Un-registration failed for $DN."
    exit 1
fi

