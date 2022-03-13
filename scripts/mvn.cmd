mvn versions:set -DnewVersion=1.0.2

mvn clean install -pl leaf-core -am
mvn clean install -pl leaf-client -am

mvn clean package -pl leaf-server -am

mvn clean deploy -pl leaf-client -am -Dmaven.test.skip=true -U -B