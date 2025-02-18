
package br;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IOperador extends Remote {
    boolean registrarUsuario(String login, String senha) throws RemoteException;
    boolean logarUsuario(String login, String senha) throws RemoteException;
    boolean criarRepositorio(String nome) throws RemoteException;
    boolean acessarRepositorio(String nome) throws RemoteException;
    boolean modificarRepositorio(String nome, String conteudo) throws RemoteException;
    boolean liberarRepositorio(String nome) throws RemoteException;
    boolean deslogarUsuario() throws RemoteException;
    List<String> listarRepositorios() throws RemoteException;
    List<String> listarUsuarios() throws RemoteException;
}
