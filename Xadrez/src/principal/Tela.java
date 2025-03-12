package principal;

import javax.swing.*;
import java.awt.*;

public class Tela extends JPanel implements Runnable {

    static Thread threadJogo;
    private boolean executando;
    private long ultimaAtualizacao;
    private final int FPS = 60; // Frames por segundo desejados
    private final double intervalo = 1000000000.0 / FPS; // Intervalo entre frames em nanosegundos

    public Tela() {
        setLayout(new BorderLayout()); // Usando BorderLayout como exemplo
        setBackground(Color.GREEN); // Cor de fundo para visualização
        executando = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    public void carregarJogo() {
        if (executando) {
            return;
        }
        executando = true;
        threadJogo = new Thread(this);
        threadJogo.start();
    }

    @Override
    public void run() {
        // Armazena o tempo da última atualização
        long ultimaAtualizacao = System.nanoTime();
        long tempoAtual;
        double delta = 0;

        while (executando) {
            tempoAtual = System.nanoTime();
            delta += (tempoAtual - ultimaAtualizacao) / intervalo; // Calcula o delta

            ultimaAtualizacao = tempoAtual;

            if (delta >= 1) { // Se o delta for maior ou igual a 1, significa que podemos atualizar o jogo
                atualizar();
                repaint(); // Repaint é chamado para renderizar a tela
                delta--; // Desconta o delta
            }

            try {
                Thread.sleep(2); // Delay de 2ms para evitar uso excessivo de CPU
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void atualizar() {
        // Atualizações do jogo acontecem aqui
        // Por exemplo: mover peças, checar colisões, etc.
    }

    public void pararJogo() {
        executando = false;
    }
}
