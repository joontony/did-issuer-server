#!/bin/bash

CURRENT_PROFILE=$(curl -s http://localhost:28080/profile)
echo "-> 💡 현재 구동중인 Spring Boot Port : $CURRENT_PROFILE"

if [ $CURRENT_PROFILE == blue ]
then
  CURRENT_PROXY_PORT=8081
  SWITCH_PROXY_PORT=8082
elif [ $CURRENT_PROFILE == green ]
then
  CURRENT_PROXY_PORT=8082
  SWITCH_PROXY_PORT=8081
else
  echo "-> ⚠️ profile not found : $CURRENT_PROFILE -> 일치하는 Profile이 없습니다."
  echo "-> ⚠️ set default port : 기본 포트인 8081을 할당합니다."
  SWITCH_PROXY_PORT=8081
fi

echo "-> ⚡️ 현재 NGINX 프록시 포트 : $CURRENT_PROXY_PORT"
echo "-> ⚡️ 변경할 NGINX 프록시 포트 : $SWITCH_PROXY_PORT"

echo "-> 🔄 NGINX 컨테이너 내의 프록시 방향 변경"
echo "-> 🥳 set \$service_url http://127.0.0.1:${SWITCH_PROXY_PORT};"
echo "set \$service_url http://127.0.0.1:${SWITCH_PROXY_PORT};"| tee /usr/local/etc/nginx/conf.d/service-url.inc >/dev/null 2>&1
# tee : 앞에서 출력한 문자열을 해당 경로에 덮어씀

echo "-> ✅  NGINX 재시작"
/usr/local/bin/nginx -s reload