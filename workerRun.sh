docker run -i --rm \
  --name "$1" \
  --mount type=bind,source="$2",target=/source/,readonly \
  --memory="40m" \
  --cpus="0.5" \
  openjdk:17-jdk-slim \
  /bin/sh \
  -c "javac -d /opt /source/Main_$1.java && cd opt && java -Xmx10M Main_$1"
