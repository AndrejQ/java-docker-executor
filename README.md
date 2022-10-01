## Docker
```shell
docker build -t java-executor -f ./Dockerfile .
docker run --privileged -it --rm -p 8080:8080 java-executor
```

### API


```shell
curl --location --request POST 'localhost:8080/' \
--header 'Content-Type: text/plain' \
--data-raw '
String hello = "hello";
String world = "world";
var result = hello + " " + world;
System.out.println(result);
'
```