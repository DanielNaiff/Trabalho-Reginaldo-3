package principal;

import peças.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Tela extends JPanel implements Runnable {

    static Thread threadJogo;
    private boolean executando;
    private long ultimaAtualizacao;
    private final int FPS = 60; // Frames por segundo desejados
    private final double intervalo = 1000000000.0 / FPS; // Intervalo entre frames em nanosegundos
    Tabuleiro tabuleiro = new Tabuleiro();

    public static final int branco = 0;
    public static final int preto = 1;
    int corAtual = branco;

    public static ArrayList<Peça> pecas = new ArrayList<>();
    public static ArrayList<Peça> copiaPecas = new ArrayList<>();


    public Tela() {
        setLayout(new BorderLayout()); // Usando BorderLayout como exemplo
        setBackground(Color.GREEN); // Cor de fundo para visualização
        executando = false;

        setPecas();
        copiarPecas(pecas, copiaPecas);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphics2D = (Graphics2D)g;

        tabuleiro.desenhar(graphics2D);

        for(Peça p: copiaPecas){
            p.desenhar(graphics2D);
        }
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

    public static void setPecas() {
        pecas.add(new Peao(branco, 0, 6));
        pecas.add(new Peao(branco, 1, 6));
        pecas.add(new Peao(branco, 2, 6));
        pecas.add(new Peao(branco, 3, 6));
        pecas.add(new Peao(branco, 4, 6));
        pecas.add(new Peao(branco, 5, 6));
        pecas.add(new Peao(branco, 6, 6));
        pecas.add(new Peao(branco, 7, 6));
        pecas.add(new Torre(branco, 0, 7));
        pecas.add(new Torre(branco, 7, 7));
        pecas.add(new Cavalo(branco, 1, 7));
        pecas.add(new Cavalo(branco, 6, 7));
        pecas.add(new Bispo(branco, 2, 7));
        pecas.add(new Bispo(branco, 5, 7));
        pecas.add(new Rainha(branco, 3, 7));
        pecas.add(new Rei(branco, 4, 7));

        pecas.add(new Peao(preto, 0, 1));
        pecas.add(new Peao(preto, 1, 1));
        pecas.add(new Peao(preto, 2, 1));
        pecas.add(new Peao(preto, 3, 1));
        pecas.add(new Peao(preto, 4, 1));
        pecas.add(new Peao(preto, 5, 1));
        pecas.add(new Peao(preto, 6, 1));
        pecas.add(new Peao(preto, 7, 1));
        pecas.add(new Torre(preto, 0, 1));
        pecas.add(new Torre(preto, 7, 1));
        pecas.add(new Cavalo(preto, 1, 1));
        pecas.add(new Cavalo(preto, 6, 1));
        pecas.add(new Bispo(preto, 2, 1));
        pecas.add(new Bispo(preto, 5, 1));
        pecas.add(new Rainha(preto, 3, 1));
        pecas.add(new Rei(preto, 4, 1));
    }

    private void copiarPecas(ArrayList<Peça> atuais, ArrayList<Peça> copias){
        copias.clear();
        for(int i = 0; i< atuais.size(); i++){
            copias.add(atuais.get(i));
        }
    }


}
