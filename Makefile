J = java
JC = javac
JCC = javacc
DIR = Parser

default: clean javac
server1: default s
server2: default z
client: default c

clean:
	rm -f ./*.class

javac:
	$(JC) ./**/*.java

s:
	@echo "------------------"
	@echo " Server           "
	@echo "------------------"
	$(J) trab1.ContactServer

z:
	@echo "------------------"
	@echo " Client           "
	@echo "------------------"
	$(J) trab1.DirServerImpl SD localhost

c:
	@echo "------------------"
	@echo " Client           "
	@echo "------------------"
	$(J) trab1.Client localhost/myContactServer
