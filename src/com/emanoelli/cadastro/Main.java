package com.emanoelli.cadastro;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SistemaCadastro sistema = new SistemaCadastro(scanner);
        sistema.iniciar();
        scanner.close();
    }
}
