package principal;

import javax.swing.*;

public class Principal {

    public static void main(String[] args) {
        // Criando a janela (JFrame)
        JFrame janela = new JFrame("Xadrez - Trabalho final Reginaldo");

        // Definindo o comportamento quando a janela for fechada
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Obtém a instância única de Tela
        Tela tela = Tela.getInstance();

        // Adicionando o painel à janela
        janela.add(tela);

        // Ajustando o tamanho inicial da janela e centralizando
        janela.setSize(528, 547); // Tamanho inicial da janela
        janela.setLocationRelativeTo(null);  // A janela será centralizada na tela
        // Impede que a janela seja redimensionada
        janela.setResizable(false);


        // Tornando a janela visível
        janela.setVisible(true);

        // Carregar e iniciar o jogo
        tela.carregarJogo();
    }
}