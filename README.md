# Jetty 9/10 WebSocket memory test

## Running on jetty 9

```bash
# first terminal 
./start.sh 9
# second terminal
./gradlew --rerun-tasks :loadtest:test
# completes
```

## Running on jetty 10

```bash
# first terminal 
./start.sh 10
# second terminal
./gradlew --rerun-tasks :loadtest:test
# fails with Terminating due to java.lang.OutOfMemoryError: Java heap space
```

