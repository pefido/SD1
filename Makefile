J = java
JC = javac
JCC = javacc
DIR = Parser

default: clean javac
server: clean javac s

clean:
	rm -f ./*.class

javac:
	$(JC) ./**/*.java

s:
	@echo "------------------"
	@echo " Server           "
	@echo "------------------"
	$(J) trab1.DirServerImpl
