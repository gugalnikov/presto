#!/bin/bash -eux

curl -s http://127.0.0.1:8500/v1/agent/connect/ca/leaf/presto | jq -r .CertPEM > /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/leaf.pem
curl -s http://127.0.0.1:8500/v1/agent/connect/ca/leaf/presto | jq -r .PrivateKeyPEM > /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/leaf.key
curl -s http://127.0.0.1:8500/v1/agent/connect/ca/roots | jq -r .Roots[].RootCert > /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/roots.pem

echo "creating p12..."
openssl pkcs12 -export -in /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/leaf.pem -inkey /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/leaf.key -certfile /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/leaf.pem -out /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/presto.p12

echo "importing p12 into jks..."
keytool -importkeystore -srckeystore /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/presto.p12 -srcstoretype pkcs12 -destkeystore /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/presto.jks -deststoretype JKS -storepass changeit

echo "adding root to jks..."
keytool -import -trustcacerts -keystore /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/presto.jks -storepass changeit -alias Root -file /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/roots.pem

echo "jks to pkcs12 format..."
keytool -importkeystore -srckeystore /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/presto.jks -destkeystore /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/presto.jks -deststoretype pkcs12

echo "creating bundle..."
cat /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/leaf.pem /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/roots.pem > /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/bundle.pem

#openssl x509 -outform der -in /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/leaf.pem -out /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/leaf.der
#openssl x509 -outform der -in /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/roots.pem -out /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/roots.der

#keytool -delete -noprompt -storepass changeit -alias consul_leaf -keystore /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/presto.jks
#keytool -delete -noprompt -storepass changeit -alias consul_roots -keystore /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/presto.jks

#keytool -import -noprompt -storepass changeit -alias consul_leaf -keystore /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/presto.jks -file /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/leaf.der
#keytool -import -noprompt -storepass changeit -alias consul_roots -keystore /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/presto.jks -file /Users/aviveros/Workspace/ConnectNativeDemo/certs/presto/roots.der
