start java -cp ./master/out/production/master_slave/ aau.distributedsystems.master.Master 8080 3 3600
start java -cp ./master/out/production/master_slave/ aau.distributedsystems.slave.Slave localhost 8080 1
start java -cp ./master/out/production/master_slave/ aau.distributedsystems.slave.Slave localhost 8080 2
start java -cp ./master/out/production/master_slave/ aau.distributedsystems.slave.Slave localhost 8080 3

