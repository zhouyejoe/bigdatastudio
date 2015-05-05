compile:
	#javac -cp  ~/Big_Data/hadoop-1.2.1/hadoop-core-1.2.1.jar -d bin/ src/WordCount.java
	javac -cp ../hadoop/hadoop-core-1.2.1.jar -d bin/ src/*.java
	javac -cp ../hadoop/hadoop-core-1.2.1.jar:/home/ubuntu/hadoop/lib/commons-logging-1.1.1.jar -d hadoop-bin/  our-hadoop/*.java

jar:
	#cd bin/; jar cf wordcount.jar *.class ; mv wordcount.jar ../
	cd bin/; jar cf overmem.jar *.class ; mv overmem.jar ../
	cd hadoop-bin; jar cf ganglia32.jar *; mv ganglia32.jar ../ 

