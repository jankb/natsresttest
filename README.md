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


