#!/usr/bin/env bash

mvn clean package

echo 'Copy files...'

scp -i ~/.ssh/id_rsa \
    target/SocialNetwork-1.1-SNAPSHOT.jar \
    javapro@176.118.165.204:/home/javapro/

echo 'Restart server...'

ssh -i ~/.ssh/id_rsa javapro@176.118.165.204 << EOF
pgrep java | xargs kill -9
nohup java -jar SocialNetwork-1.1-SNAPSHOT.jar > log.txt &
EOF

echo 'Bye'