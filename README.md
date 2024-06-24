# natsresttest
Playing around with kotlin-nats

## NATS server in docker.

Start NATS server:
> docker run --rm  --name nats-server -p 8222:8222 -ti nats:latest

Start client one:
> docker run --rm --name nats-box -it natsio/nats-box:latest

Find ip of nats-server:
> docker inspect nats-server | grep IPAddress


Configure a context:
> nats context add demo --server <IPAddress>:4222 --description "Demo with docker" --select

Setup a subscription:
> nats sub demo


Connect to runnint nats-box image:
> docker exec -ti nats-box sh

Pubish something:
> nats pub demo "Hello"

## RestNATSSserver, NATS setup

Setup server
> ./nats-server -js -c server.conf

Create a stream with subject 'provetaking.order'
> nats stream add

Post to endpoint
> curl -X POST localhost:8082/message -d '{"message":"voff"}' -H "Content-Type: application/json"

Publish to subject
> nats pub provetaking.order "{"message":"bjeff"}"
 
## Nats notes
Starting nats-server jetstream
> ./nats-server --js -V -c server.conf

server.conf
```
accounts: {
  SYS: {
    users: [{user: sys, password: pass}]
    jetstream: enable
  }
}
```

>NATS_USER=sys NATS_PASSWORD=pass nats server ls

nats creds in same dir as running app.
in .config/nats/context/file_for_relevantcontext.json:
```json
{
  "description": "",
  "url": "tls://<relevant_url>:4222",
  "socks_proxy": "",
  "token": "",
  "user": "",
  "password": "",
  "creds": "nats.creds",
  "nkey": "",
  "cert": "",
  "key": "",
  "ca": "",
  "nsc": "",
  "jetstream_domain": "",
  "jetstream_api_prefix": "",
  "jetstream_event_prefix": "",
  "inbox_prefix": "",
  "user_jwt": "",
  "color_scheme": ""
}
```
