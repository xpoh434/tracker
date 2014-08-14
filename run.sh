JAVA_HOME=/usr/lib/jvm/jre-1.7.0-openjdk.x86_64/
cd $(dirname $0)
$JAVA_HOME/bin/java -cp lib/*:bin/  me.tracker.boot.App -server -Xmx512MB > /dev/null 2>&1 &
