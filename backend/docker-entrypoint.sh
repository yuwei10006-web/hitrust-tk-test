#!/bin/sh
set -e
mkdir -p keys
if [ -d /etc/secrets ]; then
  for f in /etc/secrets/*.DER.b64; do
    [ -e "$f" ] || continue
    name=$(basename "$f" .b64)
    base64 -d "$f" > "keys/$name"
  done
fi
echo "keys/ 目錄內容："
ls -la keys/
exec java -jar app.jar