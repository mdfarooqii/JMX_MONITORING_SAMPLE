/**
 * 
 */
package com.farooq.jmx.monitoring;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * @author faroooq
 *
 */
public class JMX_Monitoring {

	public static final String HOST = "localhost";
    public static final String PORT = "1234";
    
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
	JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + HOST + ":" + PORT + "/jmxrmi");
    JMXConnector jmxConnector = JMXConnectorFactory.connect(url);
    MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
    //ObjectName should be same as your MBean name
    CompositeData composite =  (CompositeData)mbeanServerConnection.getAttribute(new ObjectName("java.lang:type=Memory"),"HeapMemoryUsage");
    System.out.println(String.format("heap committed count [%s]",((long)composite.get("committed"))/1000));
    System.out.println(String.format("heap used memory [%s]",((long)composite.get("used"))/1000));
    System.out.println(String.format("heap max memory [%s]",((long)composite.get("max"))/1000));
    
    Set<ObjectName> mp = mbeanServerConnection.queryNames(new ObjectName(ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE+",*"), null);
    final int size = mp.size();
    final List<MemoryPoolMXBean> pools = new ArrayList<MemoryPoolMXBean>(size);
	for (ObjectName n : mp) {
		final MemoryPoolMXBean proxy = ManagementFactory.newPlatformMXBeanProxy(mbeanServerConnection, n.toString(),MemoryPoolMXBean.class);
		pools.add(proxy);
	}
	
	System.out.println("Size of memory pools is "+ pools.size());
	
	for (Iterator i = pools.iterator(); i.hasNext(); ) {

        MemoryPoolMXBean mpool = (MemoryPoolMXBean)i.next();
        MemoryUsage usage = mpool.getUsage();

        String name = mpool.getName();  	
        float init = usage.getInit()/1000;
        float used = usage.getUsed()/1000;
        float committed = usage.getCommitted()/1000;
        float max = usage.getMax()/1000;
        float pctUsed = (used / max)*100;
        float pctCommitted = (committed / max)*100;
        
        
        System.out.println(String.format("committed count [%s]",committed));
        System.out.println(String.format("used memory [%s]",used));
        System.out.println(String.format("max memory [%s]",max));
        System.out.println(String.format("pct used [%s]",pctUsed));
        

    }
    
    
    /*
     * inal Set<ObjectName> mp =
... server.queryNames(
... ... new ObjectName(ManagementFactory.
... ... ... MEMORY_POOL_MXBEAN_DOMAIN_TYPE+",\*"),
... ... ... null);
final int size = mp.size();
final List<MemoryPoolMXBean> pools = 
... new ArrayList<MemoryPoolMXBean>(size);
for (ObjectName n : mp) {
... final MemoryPoolMXBean proxy = 
... ... ManagementFactory.
... ... ... newPlatformMXBeanProxy(server,
... ... ... ... n.toString(),
... ... ... ... MemoryPoolMXBean.class);
... pools.add(proxy);
}
     * 
     * */
    
    
    
    /*List<MemoryPoolMXBean> remoteThread1 =
            ManagementFactory.getMemoryPoolMXBeans()newPlatformMXBeanProxy(mbeanServerConnection, ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE ,*);
   String [] memorypools =remoteThread1.getMemoryPoolNames();
   remoteThread1.getObjectName()
    
    long maxMemory = (long) composite.get("max");
    long usedMemory = (long) composite.get("used");
    long committedMemory = (long) composite.get("committed");
    
    System.out.println(String.format("committed count",composite.get("committed")));
    System.out.println(String.format("used memory",composite.get("used")));
    System.out.println(String.format("max memory",composite.get("max")));
    System.out.println(String.format("pct used [%s]",((usedMemory/maxMemory) * 100)));
    	
    */	
    	//https://www.cs.drexel.edu/~spiros/teaching/CS675/labs/JVMRuntimeClient.java
    	//https://blogs.oracle.com/jmxetc/entry/how_to_retrieve_remote_jvm	
    	//http://pastebin.com/uS5jYpd4
    	//http://www.programcreek.com/java-api-examples/java.lang.management.ThreadMXBean
    	//http://stackoverflow.com/questions/32810198/how-to-get-fixed-object-name-for-c3p0-mbeans-object
    	//https://www.ricston.com/blog/retrieving-jmx-information-programmatically/
    	//http://marxsoftware.blogspot.com/search/label/JMX
    	//http://www.javaspecialists.eu/archive/Issue093.html -- deadlock detection sample
    //https://blogs.oracle.com/jmxetc/entry/how_to_retrieve_remote_jvm -- pid and address and port	   	
    	
    	final ThreadMXBean remoteThread =
                ManagementFactory.newPlatformMXBeanProxy(mbeanServerConnection, ManagementFactory.THREAD_MXBEAN_NAME,ThreadMXBean.class);

        System.out.println("Threads of VM  are: " +remoteThread.getThreadInfo(remoteThread.getAllThreadIds()).toString());
        
       


	long [] threadIDs = remoteThread.getAllThreadIds();

	for(long thread : threadIDs) {
        	System.out.println("Thread " + thread +  " stack trace: " + 
			remoteThread.getThreadInfo(thread).toString());
	}
	
	long [] deadLockThreads = remoteThread.findDeadlockedThreads();
	System.out.println("Dead lock Threads  "+ (deadLockThreads == null ? "No dead locks" : deadLockThreads.toString()));
	
	
    if (deadLockThreads != null && deadLockThreads.length >0) {
    	System.out.println("Dead lock Threads  "+ remoteThread.getThreadInfo(remoteThread.findDeadlockedThreads()).toString());
    	for(long thread1 : deadLockThreads) {
    		System.out.println(String.format("Dead lock Thread running with id [%s]" , remoteThread.getThreadInfo(thread1)));
    	}
    }
    
    
   /* MemoryPoolMXBean memBean = (MemoryPoolMXBean) ManagementFactory.newPlatformMXBeanProxy(mbeanServerConnection, ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE , MemoryPoolMXBean.class);       	
        	
    String [] memBeans = memBean.getMemoryPoolNames();       	
    for(int i =0 ; memBeans!=null && i <memBeans.length;i++ ){
    	
    	memBean.
    	
    }
    
    for (Iterator i = memBeans.iterator(); i.hasNext(); ) {

        MemoryPoolMXBean mpool = (MemoryPoolMXBean)i.next();
        MemoryUsage usage = mpool.getUsage();

        String name = mpool.getName();  	
        float init = usage.getInit()/1000;
        float used = usage.getUsed()/1000;
        float committed = usage.getCommitted()/1000;
        float max = usage.getMax()/1000;
        float pctUsed = (used / max)*100;
        float pctCommitted = (committed / max)*100;

    }*/

    
    	/*
    	long[] ids = threadMXBean.getAllThreadIds();
    	
    	long[] deadLockThreads = threadMXBean.findDeadlockedThreads();
        if (ids != null && ids.length >0) {
        	for(int i = 0 ;i< ids.length ;i++){
        		System.out.println(String.format("Thread running with id [%s]" , ids));
        	}
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
    		
    */
    
    /*
     * 
     * def javaThreads = new javax.management.ObjectName("java.lang:type=Threading")
def server = JmxServer.retrieveServerConnection(pid)
long[] deadlockedThreadIds = server.invoke(javaThreads, "findDeadlockedThreads", null, null)
deadlockedThreadIds.each
     * ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    long[] ids = threadMXBean.findDeadlockedThreads();
    if (ids != null) {
        System.exit(127);
    }
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {}
			*/ 
			       
			        //close the connection
			        jmxConnector.close();
	    }
	 


}
