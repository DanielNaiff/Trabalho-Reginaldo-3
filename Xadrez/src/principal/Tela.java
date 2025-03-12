package principal;

import javax.swing.*;
import java.awt.*;

public class Tela extends JPanel implements Runnable {

    static Thread jogoThread;

    public Tela() {
        setLayout(new BorderLayout()); // Usando BorderLayout como exemplo
        setBackground(Color.GREEN); // Cor de fundo para visualização
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Aqui você pode desenhar o tabuleiro de xadrez ou outras partes do jogo.
    }

    public void carregarJogo() {
        jogoThread = new Thread(this);
        jogoThread.start();
    }

    //metodo responsavel por fazer o loop do jogo
    @Override
    public void run() {
        
    }
}
