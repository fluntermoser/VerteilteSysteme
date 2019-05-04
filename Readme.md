# Java Based Master-Slave Application
The Master has a predefined set of dummy tasks that are distributed to a variable number of slaves.
Each slave is given one task to do at a time and returns a result.
If there are Tasks left, each slave gets a task again.
This pattern loops as long as there are tasks left.

If a slave fails to execute its task (for example if it shuts down), the task is marked as undone and will be distributed to another slave. 
(To simulate slave failure, simply shut it down)

## Application Run
To run the application(s) follow these steps: 
- build all java files located in src folder
- either use the provided script file (run.sh or run.bat)
- OR run the programm manually as described in the following steps

### Master
- go to folder: master\out\production\master_slave
- open command line
- run: java aau.distributedsystems.master.Master port max-number-of-slaves timeout-in-seconds (seconds), slave-failure-timeout (seconds, optional)

### Slave
- go to folder: master\out\production\master_slave
- open command line
- run: java aau.distributedsystems.slave.Slave server-address port-of-server unique-slave-id

## Params
The master and the slave application can be configured using command line arguments:

### Master
- port (the port the application should run on)
- max-number-of-slaves (the maximum amount of slaves that can be managed by the application)
- max-waiting-timeout (seconds, maximum amount of time the application waits for clients to connect)
- slave-failure-timeout (seconds, option, maximum amount of time the application waits to receive a result from the slave)

### Slave
- server-address (the address of the server the slave tries to connect to)
- port-of-server (the port of the server the slave tries to connect to)
- unique-slave-id (the unique id of each slave)