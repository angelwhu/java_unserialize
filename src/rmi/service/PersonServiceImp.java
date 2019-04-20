package rmi.service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;

import rmi.model.PersonEntity;

public class PersonServiceImp extends UnicastRemoteObject implements PersonServiceInterface{

	
	protected PersonServiceImp() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<PersonEntity> GetList() throws RemoteException {
		// TODO Auto-generated method stub  
		System.out.println("Get Person Start!");
		List<PersonEntity> personList=new LinkedList<PersonEntity>();

		PersonEntity person1=new PersonEntity();
		person1.setAge(25);
		person1.setId(0);
		person1.setName("Leslie");
		personList.add(person1);

		PersonEntity person2=new PersonEntity();
		person2.setAge(25);
		person2.setId(1);
		person2.setName("Rose");
		personList.add(person2);

		return personList;
		
	}
	
	
}
