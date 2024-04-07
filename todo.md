- set replication identity full
- Bug: Currently its only possible to connect to kafka from the host by manually entering 10.0.0.3 as advertised listener
- Bug: When starting all services kafka sometimes is not finding zookeeper because it aint ready yet
- Set up full cdc foundation
- Setup kotlin service
- 
- example hook:
If an advert is changed a record of a status change is tracked in another table