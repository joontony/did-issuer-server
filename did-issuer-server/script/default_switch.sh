#!/bin/bash

SWITCH_PROXY_PORT=8081
echo "-> ⚡️ set default port : 기본 포트인 $SWITCH_PROXY_PORT 를 할당합니다."

echo "-> 🔄 NGINX 컨테이너 내의 프록시 방향 설정"
echo "-> 🥳 set \$service_url http://127.0.0.1:${SWITCH_PROXY_PORT};"
echo "set \$service_url http://127.0.0.1:${SWITCH_PROXY_PORT};"| tee /usr/local/etc/nginx/conf.d/service-url.inc >/dev/null 2>&1

echo "-> 🔄 NGINX 재시작"
/usr/local/bin/nginx -s reload