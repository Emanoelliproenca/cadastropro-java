package com.emanoelli.cadastro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class SistemaCadastro {
    private static final String ARQUIVO_USUARIOS = "usuarios.txt";

    private final ArrayList<Usuario> usuarios = new ArrayList<>();
    private final Scanner scanner;

    public SistemaCadastro(Scanner scanner) {
        this.scanner = scanner;
        carregarUsuariosDoArquivo();
    }

    public void iniciar() {
        int opcao;

        do {
            mostrarMenu();
            opcao = lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1 -> cadastrarUsuario();
                case 2 -> listarUsuarios();
                case 3 -> buscarPorEmail();
                case 4 -> removerUsuario();
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 0);
    }

    private void mostrarMenu() {
        System.out.println("\n===== CADASTRO PRO =====");
        System.out.println("1 - Cadastrar usuário");
        System.out.println("2 - Listar usuários");
        System.out.println("3 - Buscar usuário por email");
        System.out.println("4 - Remover usuário (por email ou nome)");
        System.out.println("0 - Sair");
    }

    private void cadastrarUsuario() {
        System.out.println("\n--- Cadastro de Usuário ---");

        String nome = lerTexto("Nome: ");
        String email = lerEmail("Email: ");

        if (emailJaExiste(email)) {
            System.out.println("Já existe um usuário cadastrado com esse email.");
            return;
        }

        int idade = lerIdade("Idade: ");

        usuarios.add(new Usuario(nome, email, idade));
        salvarUsuariosEmArquivo();
        System.out.println("Usuário cadastrado com sucesso.");
    }

    private void listarUsuarios() {
        System.out.println("\n--- Lista de Usuários ---");

        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }

        for (int i = 0; i < usuarios.size(); i++) {
            System.out.println((i + 1) + ") " + usuarios.get(i));
        }
    }

    private void buscarPorEmail() {
        System.out.println("\n--- Buscar por Email ---");
        String emailBusca = lerEmail("Digite o email para buscar: ");

        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(emailBusca)) {
                System.out.println("Encontrado: " + u);
                return;
            }
        }

        System.out.println("Nenhum usuário encontrado com esse email.");
    }

    private void removerUsuario() {
        System.out.println("\n--- Remover Usuário ---");

        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado para remover.");
            return;
        }

        System.out.println("1 - Remover por email");
        System.out.println("2 - Remover por nome");
        int tipo = lerInteiro("Escolha uma opção: ");

        if (tipo == 1) {
            removerPorEmail();
        } else if (tipo == 2) {
            removerPorNome();
        } else {
            System.out.println("Opção inválida.");
        }
    }

    private void removerPorEmail() {
        String emailRemover = lerEmail("Digite o email do usuário para remover: ");

        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getEmail().equalsIgnoreCase(emailRemover)) {
                Usuario removido = usuarios.remove(i);
                salvarUsuariosEmArquivo();
                System.out.println("Usuário removido: " + removido.getNome());
                return;
            }
        }

        System.out.println("Nenhum usuário encontrado com esse email.");
    }

    private void removerPorNome() {
        String nomeRemover = lerTexto("Digite o nome do usuário para remover: ");

        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getNome().equalsIgnoreCase(nomeRemover)) {
                Usuario removido = usuarios.remove(i);
                salvarUsuariosEmArquivo();
                System.out.println("Usuário removido: " + removido.getNome());
                return;
            }
        }

        System.out.println("Nenhum usuário encontrado com esse nome.");
    }


    private String lerTexto(String mensagem) {
        System.out.print(mensagem);
        String texto = scanner.nextLine().trim();

        while (texto.isEmpty()) {
            System.out.print("Não pode ser vazio. " + mensagem);
            texto = scanner.nextLine().trim();
        }

        return texto;
    }

    private String lerEmail(String mensagem) {
        System.out.print(mensagem);
        String email = scanner.nextLine().trim();

        while (!emailValido(email)) {
            System.out.print("Email inválido. Tente novamente: ");
            email = scanner.nextLine().trim();
        }

        return email;
    }

    private boolean emailValido(String email) {
        return email.contains("@") && email.contains(".") && email.length() >= 6;
    }

    private int lerIdade(String mensagem) {
        int idade = lerInteiro(mensagem);

        while (idade < 0 || idade > 120) {
            System.out.print("Idade inválida (0 a 120). " + mensagem);
            idade = lerInteiro("");
        }

        return idade;
    }

    private int lerInteiro(String mensagem) {
        if (!mensagem.isBlank()) {
            System.out.print(mensagem);
        }

        while (!scanner.hasNextInt()) {
            System.out.print("Digite um número válido: ");
            scanner.nextLine();
        }

        int valor = scanner.nextInt();
        scanner.nextLine();
        return valor;
    }

    private boolean emailJaExiste(String email) {
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    private void salvarUsuariosEmArquivo() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_USUARIOS))) {
            for (Usuario u : usuarios) {
                writer.write(u.getNome() + ";" + u.getEmail() + ";" + u.getIdade());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar usuários.");
        }
    }

    private void carregarUsuariosDoArquivo() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_USUARIOS))) {
            String linha;

            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes.length != 3) continue;

                String nome = partes[0];
                String email = partes[1];

                int idade;
                try {
                    idade = Integer.parseInt(partes[2]);
                } catch (NumberFormatException e) {
                    continue;
                }

                if (!emailJaExiste(email)) {
                    usuarios.add(new Usuario(nome, email, idade));
                }
            }
        } catch (IOException e) {
            // primeira execução: arquivo ainda não existe
        }
    }
}
