J = java
JC = javac
JCC = javacc
DIR = Parser
JAR = ../json-simple-1.1.1.jar:../commons-codec-1.7.jar:../scribe-1.3.2.jar

default: clean javac
server1: default s
server2: default p
client: default c

clean:
	rm -f ./*.class

javac:
	$(JC) -cp ../json-simple-1.1.1.jar:../commons-codec-1.7.jar:../scribe-1.3.2.jar **/*.java


s:
	@echo "------------------"
	@echo " Server           "
	@echo "------------------"
	$(J) trab2.ContactServer

p:
	@echo "------------------"
	@echo " Proxy            "
	@echo "------------------"
	$(J) -cp $(JAR):. trab2.Proxy SD localhost

c:
	@echo "------------------"
	@echo " Client           "
	@echo "------------------"
	$(J) -cp $(JAR):. trab2.Client localhost/myContactServer
