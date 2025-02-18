package br;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class OperadorServlet extends HttpServlet {
    private IOperador operador;

    @Override
    public void init() throws ServletException {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            operador = (IOperador) registry.lookup("OperadorService");
        } catch (Exception e) {
            throw new ServletException("Erro ao conectar ao serviço RMI", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String comando = request.getParameter("comando");
        String login = request.getParameter("login");
        String senha = request.getParameter("senha");
        String nome = request.getParameter("nome");
        String conteudo = request.getParameter("conteudo");
        String resultado = "Comando não reconhecido";

        try {
            switch (comando) {
                case "registrar":
                    resultado = operador.registrarUsuario(login, senha) ? "Usuário registrado com sucesso." : "Erro ao registrar usuário.";
                    break;
                case "logar":
                    if (operador.logarUsuario(login, senha)) {
                        resultado = "Usuário logado com sucesso.";
                        Thread.currentThread().setName(login); // Define o usuário atual na thread
                    } else {
                        resultado = "Erro ao logar usuário.";
                    }
                    break;
                case "criar":
                    resultado = operador.criarRepositorio(nome) ? "Repositório criado com sucesso." : "Erro ao criar repositório.";
                    break;
                case "acessar":
                    resultado = operador.acessarRepositorio(nome) ? "Repositório acessado com sucesso." : "Erro ao acessar repositório.";
                    break;
                case "modificar":
                    resultado = operador.modificarRepositorio(nome, conteudo) ? "Repositório modificado com sucesso." : "Erro ao modificar repositório.";
                    break;
                case "liberar":
                    resultado = operador.liberarRepositorio(nome) ? "Repositório liberado com sucesso." : "Erro ao liberar repositório.";
                    break;
                case "deslogar":
                    if (operador.deslogarUsuario()) {
                        resultado = "Usuário deslogado com sucesso.";
                        Thread.currentThread().setName(null); // Limpa o usuário atual na thread
                    } else {
                        resultado = "Erro ao deslogar usuário.";
                    }
                    break;
                case "listarRepos":
                    resultado = "Repositórios:\n" + String.join("\n", operador.listarRepositorios());
                    break;
                case "listarUsuarios":
                    resultado = "Usuários cadastrados:\n" + String.join("\n", operador.listarUsuarios());
                    break;
                case "sair":
                    if (operador.deslogarUsuario()) {
                        resultado = "Usuário deslogado com sucesso.";
                        Thread.currentThread().setName(null); // Limpa o usuário atual na thread
                    } else {
                        resultado = "Erro ao deslogar usuário.";
                    }
                    break;
            }
        } catch (Exception e) {
            resultado = "Erro ao executar comando: " + e.getMessage();
        }

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.print(resultado);
    }
}
