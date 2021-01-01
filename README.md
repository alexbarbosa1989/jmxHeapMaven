# jmxHeapMaven

1. Clone the project:

~~~
git clone https://github.com/alexbarbosa1989/jmxHeap
~~~

2. Compile it:

~~~
mvn clean compile assembly:assembly
~~~

3. Execute the generated jar:

~~~
java -jar target/jmxTest-0.0.1-SNAPSHOT.jar
~~~

NOTE: The main class has hard-coded the connection parameters, change them according to your particular environment attributes
