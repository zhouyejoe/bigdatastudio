compile:
	#javac -cp  ~/Big_Data/hadoop-1.2.1/hadoop-core-1.2.1.jar -d bin/ src/WordCount.java
	javac -cp  ~/Big_Data/hadoop-1.2.1/hadoop-core-1.2.1.jar -d bin/ src/*.java

jar:
	#cd bin/; jar cf wordcount.jar *.class ; mv wordcount.jar ../
	cd bin/; jar cf overmem.jar *.class ; mv overmem.jar ../
