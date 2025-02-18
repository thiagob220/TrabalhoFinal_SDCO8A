package br;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.server.Unreferenced;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class Operador extends UnicastRemoteObject implements IOperador, Unreferenced {
    private Map<String, String> usuarios;
    private Map<String, String> repositorios;
    private Map<String, String> trancas;
    private Set<String> usuariosLogados;
    private ThreadLocal<String> usuarioAtual = new ThreadLocal<>();

    protected Operador() throws RemoteException {
        super();
        usuarios = new HashMap<>();
        repositorios = new HashMap<>();
        trancas = new HashMap<>();
        usuariosLogados = new HashSet<>();
    }

    @Override
    public synchronized boolean registrarUsuario(String login, String senha) throws RemoteException {
        if (usuarios.containsKey(login)) {
            return false; // Usuário já existe
        }
        usuarios.put(login, senha);
        return true;
    }

    @Override
    public synchronized boolean logarUsuario(String login, String senha) throws RemoteException {
        if (usuarios.containsKey(login) && usuarios.get(login).equals(senha) && !usuariosLogados.contains(login)) {
            usuariosLogados.add(login);
            usuarioAtual.set(login);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean criarRepositorio(String nome) throws RemoteException {
        if (usuarioAtual.get() == null) {
            return false; // Usuário não está logado
        }
        if (repositorios.containsKey(nome)) {
            return false; // Repositório já existe
        }
        repositorios.put(nome, "");
        return true;
    }

    @Override
    public synchronized boolean acessarRepositorio(String nome) throws RemoteException {
        if (usuarioAtual.get() == null) {
            return false; // Usuário não está logado
        }
        if (!repositorios.containsKey(nome)) {
            return false; // Repositório não existe
        }
        if (trancas.containsKey(nome)) {
            return false; // Repositório está bloqueado
        }

        // Fase de preparação (prepare)
        if (trancas.put(nome, usuarioAtual.get()) == null) {
            // Fase de confirmação (commit)
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean modificarRepositorio(String nome, String conteudo) throws RemoteException {
        if (usuarioAtual.get() == null) {
            return false; // Usuário não está logado
        }
        if (!repositorios.containsKey(nome) || !trancas.containsKey(nome) || !trancas.get(nome).equals(usuarioAtual.get())) {
            return false; // Repositório não existe ou não está bloqueado pelo usuário atual
        }
        repositorios.put(nome, conteudo);
        return true;
    }

    @Override
    public synchronized boolean liberarRepositorio(String nome) throws RemoteException {
        if (usuarioAtual.get() == null) {
            return false; // Usuário não está logado
        }
        if (!trancas.containsKey(nome) || !trancas.get(nome).equals(usuarioAtual.get())) {
            return false; // Repositório não está bloqueado pelo usuário atual
        }
        trancas.remove(nome);
        return true;
    }

    @Override
    public synchronized boolean deslogarUsuario() throws RemoteException {
        if (usuarioAtual.get() != null) {
            String usuario = usuarioAtual.get();
            usuariosLogados.remove(usuario);
            // Liberar qualquer repositório bloqueado pelo usuário
            trancas.entrySet().removeIf(entry -> entry.getValue().equals(usuario));
            usuarioAtual.set(null);
        }
        return true;
    }

    @Override
    public synchronized List<String> listarRepositorios() throws RemoteException {
        List<String> listaRepositorios = new ArrayList<>();
        for (Map.Entry<String, String> entry : repositorios.entrySet()) {
            String status = trancas.containsKey(entry.getKey()) ? " (bloqueado)" : " (disponível)";
            listaRepositorios.add(entry.getKey() + ": " + entry.getValue() + status);
        }
        return listaRepositorios;
    }

    @Override
    public synchronized List<String> listarUsuarios() throws RemoteException {
        List<String> listaUsuarios = new ArrayList<>();
        for (Map.Entry<String, String> entry : usuarios.entrySet()) {
            String status = usuariosLogados.contains(entry.getKey()) ? " (logado)" : "";
            listaUsuarios.add(entry.getKey() + " (" + entry.getValue() + ")" + status);
        }
        return listaUsuarios;
    }

    @Override
    public void unreferenced() {
        if (usuarioAtual.get() != null) {
            System.out.println("Liberando recursos para o usuário: " + usuarioAtual.get());
            try {
                deslogarUsuario();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
