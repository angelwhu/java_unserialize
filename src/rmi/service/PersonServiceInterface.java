package rmi.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import rmi.model.PersonEntity;

public interface PersonServiceInterface extends Remote{
	
	 public List<PersonEntity> GetList() throws RemoteException;

}
