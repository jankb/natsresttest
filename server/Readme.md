# Setup

Used [nats-server-v2.10.12-linux-386](https://github.com/nats-io/nats-server/releases/download/v2.10.12/nats-server-v2.10.12-linux-386.tar.gz)

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

## Trouble shooting
In case of:
```
$nats server info
nats: error: no results received, ensure the account used has system privileges and appropriate permissions
```
It appears that you need to set a system account.
Create a config file (server.conf) for the server that contains a system user:
```
accounts: {
  SYS: {
    users: [{user: sys, password: pass}]
  }
}
system_account: SYS
```
Start the jetstream server with:
```
./nats-server --js -VV -c server.conf
```
Then run:
```
nats server info --user sys --password pass
```

Not having to enter username and password every time there are a couple of options.

Set environment variables:
```
export NATS_USER=sys
export NATS_PASSWORD=pass
```

Save to context file (~/home/user/.config/nats/config)
```
nats context save relevantcontext --user sys --password pass
```

You can also edit the relevant context file directly.

## Build 
Generate jar
```
gradlew assemble
```

Run jar
```
java -jar  build/libs/server-0.0.1-SNAPSHOT.jar --server.port=8081
```
