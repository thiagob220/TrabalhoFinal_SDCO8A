package br;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Servidor {
    public static void main(String[] args) {
        try {
            Operador operador = new Operador();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("OperadorService", operador);
            System.out.println("Servidor RMI pronto.");
        } catch (Exception e) {
            System.err.println("Erro no servidor: " + e.toString());
            e.printStackTrace();
        }
    }
}

