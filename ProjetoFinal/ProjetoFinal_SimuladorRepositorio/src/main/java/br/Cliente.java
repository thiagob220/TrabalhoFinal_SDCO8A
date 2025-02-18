package br;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            IOperador operador = (IOperador) registry.lookup("OperadorService");

            Scanner scanner = new Scanner(System.in);
            String comando;

            while (true) {
                System.out.println("Digite um comando (registrar, logar, criar, acessar, modificar, liberar, deslogar, listarRepos, listarUsuarios, sair):");
                comando = scanner.nextLine();

                switch (comando) {
                    case "registrar":
                        System.out.print("Login: ");
                        String login = scanner.nextLine();
                        System.out.print("Senha: ");
                        String senha = scanner.nextLine();
                        boolean registrado = operador.registrarUsuario(login, senha);
                        System.out.println(registrado ? "Usuário registrado." : "Erro ao registrar usuário.");
                        break;
                    
                    case "logar":
                        System.out.print("Login: ");
                        login = scanner.nextLine();
                        System.out.print("Senha: ");
                        senha = scanner.nextLine();
                        boolean logado = operador.logarUsuario(login, senha);
                        System.out.println(logado ? "Usuário logado." : "Erro ao logar usuário.");
                        break;
                    
                    case "criar":
                        System.out.print("Nome do repositório: ");
                        String nomeRepo = scanner.nextLine();
                        boolean criado = operador.criarRepositorio(nomeRepo);
                        System.out.println(criado ? "Repositório criado." : "Erro ao criar repositório.");
                        break;

                    case "acessar":
                        System.out.print("Nome do repositório: ");
                        nomeRepo = scanner.nextLine();
                        boolean acessado = operador.acessarRepositorio(nomeRepo);
                        System.out.println(acessado ? "Repositório acessado." : "Erro ao acessar repositório.");
                        break;

                    case "modificar":
                        System.out.print("Nome do repositório: ");
                        nomeRepo = scanner.nextLine();
                        System.out.print("Conteúdo: ");
                        String conteudo = scanner.nextLine();
                        boolean modificado = operador.modificarRepositorio(nomeRepo, conteudo);
                        System.out.println(modificado ? "Repositório modificado." : "Erro ao modificar repositório.");
                        break;

                    case "liberar":
                        System.out.print("Nome do repositório: ");
                        nomeRepo = scanner.nextLine();
                        boolean liberado = operador.liberarRepositorio(nomeRepo);
                        System.out.println(liberado ? "Repositório liberado." : "Erro ao liberar repositório.");
                        break;

                    case "deslogar":
                        operador.deslogarUsuario();
                        System.out.println("Usuário deslogado.");
                        break;

                    case "listarRepos":
                        System.out.println("Repositórios: " + operador.listarRepositorios());
                        break;

                    case "listarUsuarios":
                        System.out.println("Usuários cadastrados: " + operador.listarUsuarios());
                        break;

                    case "sair":
                        System.exit(0);

                    default:
                        System.out.println("Comando inválido.");
                }
            }
        } catch (Exception e) {
            System.err.println("Erro no cliente: " + e.toString());
            e.printStackTrace();
        }
    }
}

