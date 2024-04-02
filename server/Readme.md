# Setup

Used nats-server-v2.10.12-linux-386 

1. Start NATS server
```
./nats-server --js -VV
```

2.  Create context
```
nats ctx add servertest --description "Test with servercode" --select
```
3. Create a stream
```
nats stream add
```
4. Start a subscription (for testing)
```
nats sub hello.world
```

5. Publish to subject
```
nats pub hello.world "Hi, there"
```