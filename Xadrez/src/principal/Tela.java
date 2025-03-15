package principal;

import peças.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Tela extends JPanel implements Runnable {

    boolean isMover;
    boolean isQuadrante;
    private boolean promocaoPendente = false;


    private int colunaIlegal = -1; // -1 significa que não há quadrado ilegal
    private int linhaIlegal = -1;
    Interagir interagir = new Interagir();
    static Thread threadJogo;
    private boolean executando;
    Peça pecaSelecionada;
    public static Peça roque;
    private long ultimaAtualizacao;
    private final int FPS = 60; // Frames por segundo desejados
    private final double intervalo = 1000000000.0 / FPS; // Intervalo entre frames em nanosegundos
    Tabuleiro tabuleiro = new Tabuleiro();

    public static final int branco = 0;
    public static final int preto = 1;
    int corAtual = branco;

    public static ArrayList<Peça> pecas = new ArrayList<>();
    public static ArrayList<Peça> copiaPecas = new ArrayList<>();
    ArrayList<Peça> pecasPromovidas = new ArrayList<>();
    boolean podePromover;



    public Tela() {
        setLayout(new BorderLayout()); // Usando BorderLayout como exemplo
        setBackground(Color.GREEN); // Cor de fundo para visualização
        executando = false;
        addMouseMotionListener(interagir)  ;
        addMouseListener(interagir);


        setPecas();

        copiarPecas(pecas, copiaPecas);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphics2D = (Graphics2D) g;

        tabuleiro.desenhar(graphics2D);

        // Desenha o quadrado vermelho se houver uma posição ilegal
        if (colunaIlegal != -1 && linhaIlegal != -1) {
            graphics2D.setColor(new Color(255, 0, 0, 100)); // Vermelho com transparência
            graphics2D.fillRect(
                    colunaIlegal * Tabuleiro.tamanho,
                    linhaIlegal * Tabuleiro.tamanho,
                    Tabuleiro.tamanho,
                    Tabuleiro.tamanho
            );
        }

        // Desenha as peças
        ArrayList<Peça> copiaTemporaria = new ArrayList<>(copiaPecas);
        for (Peça p : copiaTemporaria) {
            p.desenhar(graphics2D);
        }
    }

    private boolean promover() {
        if (pecaSelecionada != null && pecaSelecionada.tipo == TipoPeca.PEAO) {
            if (corAtual == branco && pecaSelecionada.linha == 0 || corAtual == preto && pecaSelecionada.linha == 7) {
                promocaoPendente = true; // Define que há uma promoção pendente
                return true;
            }
        }
        return false;
    }

private void roque(){
        if(roque != null){
            if(roque.coluna == 0){
                roque.coluna += 3;
            }else if(roque.coluna == 7){
                roque.coluna -= 2;
            }
            roque.x = roque.getX(roque.coluna);
        }

}

    //se uma peca esta sendo segurada, mantem a posicao
    private void simular() {
        // Resetar a lista de pecas
        copiarPecas(pecas, copiaPecas);

        // Resetar a peca do roque
        if (roque != null) {
            roque.coluna = roque.preColuna;
            roque.x = roque.getX(roque.coluna);
            roque = null;
        }

        // Atualiza a posição da peça selecionada
        pecaSelecionada.x = interagir.x - Tabuleiro.tamanho / 2;
        pecaSelecionada.y = interagir.y - Tabuleiro.tamanho / 2;

        pecaSelecionada.coluna = pecaSelecionada.getColuna(pecaSelecionada.x);
        pecaSelecionada.linha = pecaSelecionada.getLinha(pecaSelecionada.y);

        isMover = false;
        isQuadrante = false;

        if (pecaSelecionada.podeMovimentar(pecaSelecionada.coluna, pecaSelecionada.linha)) {
            isQuadrante = true;

            if (pecaSelecionada.peçaColidida != null) {
                copiaPecas.remove(pecaSelecionada.peçaColidida.getIndex());
            }
            roque();

            // Verifica se o movimento é ilegal (rei em xeque)
            if (eIlegal(pecaSelecionada)) {
                // Define a posição ilegal
                colunaIlegal = pecaSelecionada.coluna;
                linhaIlegal = pecaSelecionada.linha;

                // Impede o movimento
                isQuadrante = false;
                isMover = false;
            } else {
                // Reseta a posição ilegal
                colunaIlegal = -1;
                linhaIlegal = -1;

                isMover = true;
            }
        }
    }

    private boolean eIlegal(Peça rei) {
        if (rei.tipo == TipoPeca.REI) {
            for (Peça peça : copiaPecas) {
                if (peça != rei && peça.cor != rei.cor && peça.podeMovimentar(rei.coluna, rei.linha)) {
                    return true; // Movimento ilegal (rei em xeque)
                }
            }
        }
        return false; // Movimento legal
    }

    private void atualizar() {
        if (promocaoPendente) {
            // Lógica de promoção
        } else {
            if (interagir.clicou) {
                if (pecaSelecionada == null) {
                    for (Peça peça : copiaPecas) {
                        if (peça.cor == corAtual && peça.coluna == interagir.x / Tabuleiro.tamanho && peça.linha == interagir.y / Tabuleiro.tamanho) {
                            pecaSelecionada = peça;
                            break;
                        }
                    }
                } else {
                    simular();
                }
            }

            if (!interagir.clicou) {
                if (pecaSelecionada != null) {
                    if (isQuadrante) {
                        copiarPecas(copiaPecas, pecas);
                        pecaSelecionada.atualizarPosicao();
                        if (roque != null) {
                            roque.atualizarPosicao();
                        }

                        // Verifica se o rei adversário está em xeque após o movimento
                        int corAdversario = (corAtual == branco) ? preto : branco;
                        if (reiEmXeque(corAdversario)) {
                            JOptionPane.showMessageDialog(
                                    this, // Componente pai (a tela do jogo)
                                    "Xeque! O rei adversário está em perigo.", // Mensagem
                                    "Xeque", // Título da janela
                                    JOptionPane.WARNING_MESSAGE // Tipo de mensagem
                            );
                        }

                        if (promover()) {
                            promocaoPendente = true;
                        } else {
                            mudarJogador();
                        }

                        // Reseta a posição ilegal após o movimento ser confirmado
                        colunaIlegal = -1;
                        linhaIlegal = -1;
                    } else {
                        copiarPecas(pecas, copiaPecas);
                        pecaSelecionada.resetarPosicao();
                        pecaSelecionada = null;

                        // Reseta a posição ilegal após o movimento ser cancelado
                        colunaIlegal = -1;
                        linhaIlegal = -1;
                    }
                }
            }
        }
    }
    private boolean reiEmXeque(int cor) {
        // Encontra o rei da cor especificada
        Peça rei = null;
        for (Peça peça : copiaPecas) {
            if (peça.tipo == TipoPeca.REI && peça.cor == cor) {
                rei = peça;
                break;
            }
        }

        if (rei == null) {
            return false; // Rei não encontrado (não deveria acontecer)
        }

        // Verifica se alguma peça adversária pode atacar o rei
        for (Peça peça : copiaPecas) {
            if (peça.cor != cor && peça.podeMovimentar(rei.coluna, rei.linha)) {
                return true; // Rei está em xeque
            }
        }

        return false; // Rei não está em xeque
    }

    private void promovendo() {
        if (interagir.clicou) {
            for (Peça peça : pecasPromovidas) {
                if (peça.coluna == interagir.x / Tabuleiro.tamanho && peça.linha == interagir.y / Tabuleiro.tamanho) {
                    // Remove o peão promovido
                    pecas.remove(pecaSelecionada);
                    copiaPecas.remove(pecaSelecionada);

                    // Adiciona a nova peça escolhida
                    Peça novaPeça = null;
                    switch (peça.tipo) {
                        case TORRE:
                            novaPeça = new Torre(corAtual, pecaSelecionada.coluna, pecaSelecionada.linha);
                            break;
                        case CAVALO:
                            novaPeça = new Cavalo(corAtual, pecaSelecionada.coluna, pecaSelecionada.linha);
                            break;
                        case BISPO:
                            novaPeça = new Bispo(corAtual, pecaSelecionada.coluna, pecaSelecionada.linha);
                            break;
                        case RAINHA:
                            novaPeça = new Rainha(corAtual, pecaSelecionada.coluna, pecaSelecionada.linha);
                            break;
                    }

                    if (novaPeça != null) {
                        pecas.add(novaPeça);
                        copiaPecas.add(novaPeça);
                    }

                    podePromover = false;
                    pecasPromovidas.clear();
                    mudarJogador();
                    break;
                }
            }
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

    public void pararJogo() {
        executando = false;
    }

    public static void setPecas() {

        pecas.add(new Peao(preto, 0, 1));
        pecas.add(new Peao(preto, 1, 1));
        pecas.add(new Peao(preto, 2, 1));
        pecas.add(new Peao(preto, 3, 1));
        pecas.add(new Peao(preto, 4, 1));
        pecas.add(new Peao(preto, 5, 1));
        pecas.add(new Peao(preto, 6, 1));
        pecas.add(new Peao(preto, 7, 1));
        pecas.add(new Torre(preto, 0, 0));
        pecas.add(new Torre(preto, 7, 0));
        pecas.add(new Cavalo(preto, 1, 0));
        pecas.add(new Cavalo(preto, 6, 0));
        pecas.add(new Bispo(preto, 2, 0));
        pecas.add(new Bispo(preto, 5, 0));
        pecas.add(new Rainha(preto, 3, 0));
        pecas.add(new Rei(preto, 4, 0));
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


    }

    private void mudarJogador() {
        if (corAtual == branco) {
            corAtual = preto;
            for (Peça peça : pecas) {
                if (peça.cor == preto) {
                    peça.pecaMoveuDoisPassos = false;
                }
            }
        } else {
            corAtual = branco;
            for (Peça peça : pecas) {
                if (peça.cor == branco) {
                    peça.pecaMoveuDoisPassos = false;
                }
            }
        }

        pecaSelecionada = null;
    }

    private void copiarPecas(ArrayList<Peça> atuais, ArrayList<Peça> copias){
        copias.clear();
        for(int i = 0; i< atuais.size(); i++){
            copias.add(atuais.get(i));
        }
    }


}
