package jmxHeap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class jmxTest {

	private static MBeanServerConnection connection;
	private static JMXConnector connector;
	private static int mem = (int) Math.pow(1024,2);

	public static void Connection(String hostname, String port) throws IOException {
		// EAP 7x | Wildfly 10+
		String urlString = "service:jmx:remote+http://" + hostname + ":" + port;
		JMXServiceURL serviceURL = new JMXServiceURL(urlString);
		Map<String, String[]> map = new HashMap();
		// credential user should previously exists in EAP/Wildfly environment (using
		// $JBOSS-HOME/bin/add-user.sh)
		String[] credentials = new String[2];
		credentials[0] = "admin";
		credentials[1] = "admin";
		map.put(JMXConnector.CREDENTIALS, credentials);

		// passing server credentials
		JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceURL, map);
		// omitting server credentials
		// JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceURL, null);

		connection = jmxConnector.getMBeanServerConnection();
	}

	private static void getHeapMemoryUsage() throws Exception {
		ObjectName memoryMXBean = new ObjectName("java.lang:type=Memory");
		CompositeDataSupport dataSenders = (CompositeDataSupport) connection.getAttribute(memoryMXBean,"HeapMemoryUsage");
		if (dataSenders != null) {
			Long commited = (Long) dataSenders.get("committed");
			Long init = (Long) dataSenders.get("init");
			Long max = (Long) dataSenders.get("max");
			Long used = (Long) dataSenders.get("used");
			Long percentage = ((used * 100) / max);
			System.out.println("commited   : " + commited / (1024 * 1024) + " MB");
			System.out.println("init       : " + init / (1024 * 1024) + " MB");
			System.out.println("max        : " + max / (1024 * 1024) + " MB");
			System.out.println("used       : " + used / (1024 * 1024) + " MB");
			System.out.println("percentage : " + percentage + " %");
		}
	}

	private static void getNonHeapMemoryUsage() throws Exception {
		ObjectName memoryMXBean = new ObjectName("java.lang:type=Memory");
		CompositeDataSupport dataSenders = (CompositeDataSupport) connection.getAttribute(memoryMXBean,"NonHeapMemoryUsage");
		if (dataSenders != null) {
			Long commited = (Long) dataSenders.get("committed");
			Long init = (Long) dataSenders.get("init");
			Long max = (Long) dataSenders.get("max");
			Long used = (Long) dataSenders.get("used");
			Long percentage = ((used * 100) / max);
			System.out.println("commited   : " + commited / mem + " MB");
			System.out.println("init       : " + init / mem + " MB");
			System.out.println("max        : " + max / mem + " MB");
			System.out.println("used       : " + used / mem + " MB");
			System.out.println("percentage : " + percentage + " %");
		}
	}
	
	private static void getOSDetails() throws Exception {
		ObjectName OSTemMXBean = new ObjectName("java.lang:type=OperatingSystem");
		Object systemLoadAverage = connection.getAttribute(OSTemMXBean, "SystemLoadAverage");

		Long freePhysicalMemory = (Long) connection.getAttribute(OSTemMXBean, "FreePhysicalMemorySize");
		Long processCpuTime = (Long) connection.getAttribute(OSTemMXBean, "ProcessCpuTime");
		Long committedVirtualMemorySize = (Long) connection.getAttribute(OSTemMXBean,"CommittedVirtualMemorySize");
		Long freeSwapSpaceSize = (Long) connection.getAttribute(OSTemMXBean, "FreeSwapSpaceSize");
		Long totalPhysicalMemorySize = (Long) connection.getAttribute(OSTemMXBean, "TotalPhysicalMemorySize");
		Long totalSwapSpaceSize = (Long) connection.getAttribute(OSTemMXBean, "TotalSwapSpaceSize");

		System.out.println("OS LoadAverage: " + systemLoadAverage);
		System.out.println("OS FreePhysicalMemory: " + (freePhysicalMemory / mem) + "-MB");
		System.out.println("OS processCpuTime: " + processCpuTime);
		System.out.println("OS committedVirtualMemorySize: " + (committedVirtualMemorySize / (1024 * 1024)) + "-MB");
		System.out.println("OS freeSwapSpaceSize: " + (freeSwapSpaceSize / mem) + "-MB");
		System.out.println("OS totalPhysicalMemorySize: " + (totalPhysicalMemorySize / mem) + "-MB");
		System.out.println("OS totalSwapSpaceSize: " + (totalSwapSpaceSize / mem) + "-MB");
	}

	public static void main(String[] args) throws Exception {
		//Get a connection to the WildFly/EAP MBean server
		String hostname =  "127.0.0.1";
		String port =  "9990";
		//to get the connection the jboss-client.jar should exists in the CLASSPATH
		Connection(hostname, port);
		System.out.println("----------HEAP Memory Usage---------");
		getHeapMemoryUsage();
		System.out.println("----------Non-HEAP Memory Usage---------");
		getNonHeapMemoryUsage();
		System.out.println("----------Operating System Usage---------");
		getOSDetails();
	}
	

}
