#!/bin/sh
set -e
mkdir -p keys
if [ -d /etc/secrets ]; then
  cp /etc/secrets/*.DER keys/ 2>/dev/null || true
fi
exec java -jar app.jar