#!/bin/bash
# /bin/bash 경로에 있는 Bash 해석기를 해당 스크립트를 실행할 때 사용하겠다고 명시

SWITCH_SCRIPT="./switch.sh"
# NGINX 스위칭 스크립트 경로 변수에 담기

DEFAULT_SWITCH_SCRIPT="./default_switch.sh"
# NGINX 스위칭 스크립트 경로 변수에 담기

SERVER_HOME=/Users/wonyonghwang/forJenkins/jenkinsTest/build/libs

echo "-> 👨🏻‍💼<Jenkins 배포 시작>"
# echo : 주어진 인자를 표준 출력으로 출력하는 명령어
CURRENT_SERVER_PROFILE=$(curl -s http://localhost:28080/profile | head -n 1)
# curl : 주어진 URL을 통해 데이터를 전송하거나 받아오는 명령어입니다.
# -s : curl이 실행되는 동안 진행 상황이나 진단 메시지 등의 출력을 표시하지 않음(silent)

if [ "$CURRENT_SERVER_PROFILE" == blue ]
then
  echo "-> 💡 현재 구동중인 Profile : $CURRENT_SERVER_PROFILE"
  CURRENT_SERVER_PORT=8081
  SWITCH_SERVER_PROFILE=green
  SWITCH_SERVER_PORT=8082
elif [ "$CURRENT_SERVER_PROFILE" == green ]
then
  echo "-> 💡 현재 구동중인 Profile : $CURRENT_SERVER_PROFILE"
  CURRENT_SERVER_PORT=8082
  SWITCH_SERVER_PROFILE=blue
  SWITCH_SERVER_PORT=8081
else
  echo "-> ⚠️ profile not found : 일치하는 Profile이 없습니다."
  echo "-> ⚠️ set default profile : 기본 Profile인 blue를 할당합니다."
  echo "-> ⚠️ 블루, 그린이 모두 동작중이 아니었으므로, blue만 배포하고 스크립트를 종료합니다."
  SWITCH_SERVER_PROFILE=blue
  SWITCH_SERVER_PORT=8081
fi
# if문의 종료를 나타내는 키워드

echo "-> 🚚 $SWITCH_SERVER_PROFILE 배포"
SWITCH_PORT_PID=$(netstat -anv | grep $SWITCH_SERVER_PORT | head -n 1 | awk '{print $9}')
if [ ! -z "$SWITCH_PORT_PID" ]
then
  echo "-> 📦 $SWITCH_SERVER_PROFILE 의 PID : $SWITCH_PORT_PID"
  kill $SWITCH_PORT_PID >/dev/null 2>&1
fi

echo "-> 🚚 nohup java -jar $SERVER_HOME/forJenkins.jar --spring.profiles.active=$SWITCH_SERVER_PROFILE > /dev/null &"
nohup java -jar $SERVER_HOME/forJenkins.jar --spring.profiles.active=$SWITCH_SERVER_PROFILE > /dev/null &

echo "-> 🚑 $SWITCH_SERVER_PROFILE : 10초 후 Health Check Start"
echo "-> 🚑 curl -s http://localhost:$SWITCH_SERVER_PORT/actuator/health"
sleep 10

for retry_count in {1..10}
do
  response=$(curl -s http://localhost:$SWITCH_SERVER_PORT/actuator/health)
  count=$(echo $response | grep "UP" | wc -l)
  # grep은 입력된 텍스트에서 "application running!"이라는 문자열을 찾아낸다.
  # "wc -l"은 입력된 텍스트의 줄 수를 세는 역할을 한다.

  if [ $count -ge 1 ]
  # -ge는 "Greater than or equal to"라는 의미로, 여기서는 저장된 값이 1보다 크거나 같은지를 확인한다.
  then
    echo "-> 🚨 [$retry_count 번째 Health Check]"
    echo "-> ✅  Health Check 성공!!!"
    break
  else
    echo "-> ❌ Health Check 실패, 서버로부터 응답을 받지 못했습니다."
    echo "-> 📣 서버로부터 받은 응답 : $response"
  fi

  if [ $retry_count -eq 10 ]
  # -eq는 "Equal to"라는 의미로, 주어진 값이 다른 값과 같은지를 비교하는 역할을 한다.
  then
    echo "-> 😵‍💫 Health Check $retry_count 회 시도 모두 실패..."
    echo "-> 🫠 배포 종료"
    exit 1
    # 0 이외의 값은 오류인채 프로그램을 종료한다는 의미이다.
  fi

  echo "-> 🤔 Health Check 실패, 5초 후 재시도..."
  sleep 5
done

if [ -z "$CURRENT_SERVER_PORT" ]
then
# -z : 변수가 비어있을 경우
  echo "-> 🔄 NGINX : Default Port로 프록시 설정"
  if [ ! -x "$DEFAULT_SWITCH_SCRIPT" ]
  then
      chmod +x "$DEFAULT_SWITCH_SCRIPT"
  fi
    "$DEFAULT_SWITCH_SCRIPT"
    echo "->👨🏻‍💼<Jenkins 배포 성공> 스크립트를 종료합니다."
    exit 0
fi

echo "-> 🤼 NGINX 포트 스위칭 스크립트 시작"
if [ ! -x "$SWITCH_SCRIPT" ]
then
    chmod +x "$SWITCH_SCRIPT"
    # .sh 파일 실행 권한을 확인하고 없으면 추가
fi
"$SWITCH_SCRIPT"

echo "-> 🔫 기존 포트인 $CURRENT_SERVER_PROFILE 포트 : $CURRENT_SERVER_PORT KILL"
CURRENT_PORT_PID=$(netstat -anv | grep $CURRENT_SERVER_PORT | head -n 1 | awk '{print $9}')
echo "-> 📦 $CURRENT_SERVER_PROFILE 의 PID : $CURRENT_PORT_PID"
kill $CURRENT_PORT_PID >/dev/null 2>&1
echo "->👨🏻‍💼<Jenkins 배포 성공> 스크립트를 종료합니다."
