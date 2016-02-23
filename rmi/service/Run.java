package rmi.service;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Run {
	
	public static void main(String[] args) {
        try {
            PersonServiceInterface personService=new PersonServiceImp();
            //ע��ͨѶ�˿�
            LocateRegistry.createRegistry(6600);
            //ע��ͨѶ·��
            //Naming.rebind("rmi://127.0.0.1:6600/PersonService", personService);
            System.out.println("Service Start!");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
