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

    // Outros atributos e métodos da classe Tela

    // 1. Instância única da classe
    private static Tela instancia;

    // 2. Construtor privado para evitar criação de instâncias externas
    private Tela() {
        setLayout(new BorderLayout());
        executando = false;
        addMouseMotionListener(interagir);
        addMouseListener(interagir);

        setPecas();
        copiarPecas(pecas, copiaPecas);
    }

    // 3. Método estático para acessar a instância única
    public static Tela getInstance() {
        if (instancia == null) {
            instancia = new Tela();
        }
        return instancia;
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
            if ((corAtual == branco && pecaSelecionada.linha == 0) || (corAtual == preto && pecaSelecionada.linha == 7)) {
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
            // Verifica se o rei está em xeque
            if (reiEmXeque(corAtual)) {
                // Se o rei está em xeque, o movimento só é permitido se resolver o xeque
                if (movimentoResolveXeque(pecaSelecionada, pecaSelecionada.coluna, pecaSelecionada.linha)) {
                    isQuadrante = true;
                } else {
                    // Movimento não resolve o xeque, bloqueia
                    isQuadrante = false;
                    isMover = false;
                    return;
                }
            } else {
                // Se o rei não está em xeque, o movimento é permitido
                isQuadrante = true;
            }

            if (pecaSelecionada.peçaColidida != null) {
                copiaPecas.remove(pecaSelecionada.peçaColidida.getIndex());
            }
            roque();

            // Verifica se o movimento é ilegal (rei em xeque após o movimento)
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
            promovendo();
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
                        // Confirma o movimento
                        copiarPecas(copiaPecas, pecas); // Atualiza o estado oficial do tabuleiro
                        pecaSelecionada.atualizarPosicao();
                        if (roque != null) {
                            roque.atualizarPosicao();
                        }

                        // Garante que copiaPecas reflita o estado atualizado
                        copiarPecas(pecas, copiaPecas);

                        // Verifica se o rei adversário está em xeque após o movimento
                        int corAdversario = (corAtual == branco) ? preto : branco;
                        if (reiEmXeque(corAdversario)) {
                            System.out.println("Rei adversário em xeque!");
                            if (isXequeMate(corAdversario)) {
                                System.out.println("Xeque-mate detectado!");
                                String vencedor = (corAtual == branco) ? "Branco" : "Preto";
                                JOptionPane.showMessageDialog(
                                        this,
                                        "Xeque-mate! O jogador " + vencedor + " venceu!",
                                        "Fim de Jogo",
                                        JOptionPane.INFORMATION_MESSAGE
                                );
                                pararJogo();
                                return;
                            } else {
                                System.out.println("Apenas xeque, não xeque-mate.");
                                JOptionPane.showMessageDialog(
                                        this,
                                        "Xeque! O rei adversário está em perigo.",
                                        "Xeque",
                                        JOptionPane.WARNING_MESSAGE
                                );
                            }
                        }

                        if (promover()) {
                            promocaoPendente = true;
                        } else {
                            mudarJogador();
                        }

                        colunaIlegal = -1;
                        linhaIlegal = -1;
                    } else {
                        copiarPecas(pecas, copiaPecas);
                        pecaSelecionada.resetarPosicao();
                        pecaSelecionada = null;
                        colunaIlegal = -1;
                        linhaIlegal = -1;
                    }
                }
            }
        }
    }



    private boolean isXequeMate(int cor) {
        // Verifica se o rei está em xeque
        if (!reiEmXeque(cor)) {
            return false; // Não está em xeque, logo não pode ser xeque-mate
        }

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

        // Verifica se o rei pode se mover para alguma posição segura
        for (int linha = rei.linha - 1; linha <= rei.linha + 1; linha++) {
            for (int coluna = rei.coluna - 1; coluna <= rei.coluna + 1; coluna++) {
                if (linha >= 0 && linha < 8 && coluna >= 0 && coluna < 8) { // Dentro do tabuleiro
                    if (rei.podeMovimentar(coluna, linha)) {
                        // Simula o movimento do rei
                        if (movimentoResolveXeque(rei, coluna, linha)) {
                            return false; // O rei pode escapar do xeque
                        }
                    }
                }
            }
        }

        // Verifica se alguma peça pode bloquear o xeque ou capturar a peça atacante
        for (Peça peça : copiaPecas) {
            if (peça.cor == cor && peça.tipo != TipoPeca.REI) { // Ignora o rei
                for (int linha = 0; linha < 8; linha++) {
                    for (int coluna = 0; coluna < 8; coluna++) {
                        if (peça.podeMovimentar(coluna, linha)) {
                            // Simula o movimento da peça
                            if (movimentoResolveXeque(peça, coluna, linha)) {
                                return false; // A peça pode bloquear o xeque ou capturar a peça atacante
                            }
                        }
                    }
                }
            }
        }

        // Se nenhum movimento resolver o xeque, é xeque-mate
        return true;
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
        // Exibe as opções de promoção
        Object[] opcoes = {"Rainha", "Torre", "Bispo", "Cavalo"};
        int escolha = JOptionPane.showOptionDialog(
                this, // Componente pai (a tela do jogo)
                "Escolha a peça para promoção:", // Mensagem
                "Promoção de Peão", // Título da janela
                JOptionPane.DEFAULT_OPTION, // Tipo de opção
                JOptionPane.QUESTION_MESSAGE, // Tipo de mensagem
                null, // Ícone (não usado)
                opcoes, // Opções
                opcoes[0] // Opção padrão
        );

        // Remove o peão promovido
        pecas.remove(pecaSelecionada);
        copiaPecas.remove(pecaSelecionada);

        // Adiciona a nova peça escolhida
        Peça novaPeça = null;
        switch (escolha) {
            case 0:
                novaPeça = new Rainha(corAtual, pecaSelecionada.coluna, pecaSelecionada.linha);
                break;
            case 1:
                novaPeça = new Torre(corAtual, pecaSelecionada.coluna, pecaSelecionada.linha);
                break;
            case 2:
                novaPeça = new Bispo(corAtual, pecaSelecionada.coluna, pecaSelecionada.linha);
                break;
            case 3:
                novaPeça = new Cavalo(corAtual, pecaSelecionada.coluna, pecaSelecionada.linha);
                break;
            default:
                // Se o jogador fechar a janela sem escolher, promove para rainha por padrão
                novaPeça = new Rainha(corAtual, pecaSelecionada.coluna, pecaSelecionada.linha);
                break;
        }

        if (novaPeça != null) {
            pecas.add(novaPeça);
            copiaPecas.add(novaPeça);
        }

        // Reseta o estado de promoção
        promocaoPendente = false;
        pecaSelecionada = null;
        mudarJogador(); // Passa a vez para o próximo jogador
    }

    private boolean movimentoResolveXeque(Peça peca, int novaColuna, int novaLinha) {
        // Simula o movimento
        int colunaOriginal = peca.coluna;
        int linhaOriginal = peca.linha;
        Peça pecaCapturada = null;

        // Verifica se há uma peça na nova posição
        for (Peça p : copiaPecas) {
            if (p.coluna == novaColuna && p.linha == novaLinha) {
                pecaCapturada = p;
                break;
            }
        }

        // Move a peça
        peca.coluna = novaColuna;
        peca.linha = novaLinha;
        if (pecaCapturada != null) {
            copiaPecas.remove(pecaCapturada);
        }

        // Verifica se o rei ainda está em xeque após o movimento
        boolean reiAindaEmXeque = reiEmXeque(corAtual);

        // Reverte o movimento
        peca.coluna = colunaOriginal;
        peca.linha = linhaOriginal;
        if (pecaCapturada != null) {
            copiaPecas.add(pecaCapturada);
        }

        // Retorna true se o movimento resolver o xeque
        return !reiAindaEmXeque;
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
