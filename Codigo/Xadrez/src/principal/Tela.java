package principal;

import peças.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Tela extends JPanel implements Runnable {

    boolean isMover;
    boolean isQuadrante;
    boolean fimDeJogo = false;
    private boolean promocaoPendente = false;

    public static final int branco = 0;
    public static final int preto = 1;

    private int colunaIlegal = -1; // -1 significa que não há quadrado ilegal
    private int linhaIlegal = -1;
    Interagir interagir = new Interagir();
    static Thread threadJogo;
    private boolean executando;
    Peça pecaSelecionada;
    Peça pecaXeque;
    public static Peça roque;
    private long ultimaAtualizacao;
    private final int FPS = 60; // Frames por segundo desejados
    private final double intervalo = 1000000000.0 / FPS; // Intervalo entre frames em nanosegundos
    Tabuleiro tabuleiro = new Tabuleiro();


    int corAtual = branco;

    public static ArrayList<Peça> pecas = new ArrayList<>();
    public static ArrayList<Peça> copiaPecas = new ArrayList<>();
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
    public static synchronized Tela getInstance() {
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

        isMover = false;
        isQuadrante = false;

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

        if (pecaSelecionada.podeMovimentar(pecaSelecionada.coluna, pecaSelecionada.linha)) {
            // Verifica se o rei está em xeque
            isMover = true;

            if (pecaSelecionada.peçaColidida != null) {
                copiaPecas.remove(pecaSelecionada.peçaColidida.getIndex());
            }
            roque();

            // Verifica se o movimento é ilegal (rei em xeque após o movimento)
            if (!eIlegal(pecaSelecionada) && !oponentePodematarORei()) {
                isQuadrante = true;

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

                        // Verifica se o rei adversário está em xeque
                        if (reiEmXeque()) {
                            if (isXequeMate()) {
                                // Xeque-mate: exibe mensagem e fecha o programa
                                String vencedor = (corAtual == branco) ? "Branco" : "Preto";
                                JOptionPane.showMessageDialog(
                                        this,
                                        "Xeque-mate! O jogador " + vencedor + " venceu!",
                                        "Fim de Jogo",
                                        JOptionPane.INFORMATION_MESSAGE
                                );
                                fimDeJogo = true;
                                System.exit(0); // Fecha o programa
                            } else {
                                // Apenas xeque: exibe mensagem de aviso
                                JOptionPane.showMessageDialog(
                                        this,
                                        "Xeque! O rei adversário está em perigo.",
                                        "Xeque",
                                        JOptionPane.WARNING_MESSAGE
                                );
                            }
                        }

                        // Garante que copiaPecas reflita o estado atualizado
                        copiarPecas(pecas, copiaPecas);

                        // Verifica se há promoção pendente
                        if (promover()) {
                            promocaoPendente = true;
                        } else {
                            mudarJogador();
                        }

                        // Reseta a posição ilegal
                        colunaIlegal = -1;
                        linhaIlegal = -1;
                    } else {
                        // Movimento inválido: reseta a peça selecionada
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
    //verifica se o rei pode se mexer entre os 8 quadrantes
    private boolean reiPodeSeMexer(Peça rei){
        if(movimentoValido(rei, -1, -1)){
            return true;
        }
        if(movimentoValido(rei, 0, -1)){
            return true;
        }
        if(movimentoValido(rei, 1, -1)){
            return true;
        }
        if(movimentoValido(rei, -1, 0)){
            return true;
        }
        if(movimentoValido(rei, 1, 0)){
            return true;
        }
        if(movimentoValido(rei, -1, 1)){
            return true;
        }
        if(movimentoValido(rei, 0, 1)){
            return true;
        }
        if(movimentoValido(rei, 1, 1)){
            return true;
        }

        return false;
    }

    //verifica se e um movimento valido do rei
    private boolean movimentoValido(Peça rei,int novaColuna, int novaLinha ){
        boolean movimentoValido = false;

        rei.coluna += novaColuna;
        rei.linha += novaLinha;

        if(rei.podeMovimentar(rei.coluna, rei.linha)){
            if(rei.peçaColidida != null){
                copiaPecas.remove(rei.peçaColidida.getIndex());
            }

            if(eIlegal(rei) == false){
                movimentoValido = true;
            }
        }

        rei.resetarPosicao();
        copiarPecas(pecas, copiaPecas);

        return movimentoValido;
    }

    private boolean isXequeMate() {
       Peça rei = getRei(true);

       if(reiPodeSeMexer(rei)){
           return false;
       }else{
           int colDiff = Math.abs(pecaXeque.coluna - rei.coluna);
           int linhaDiff = Math.abs(pecaXeque.linha - rei.linha);

           if(colDiff == 0){
               //a peca xeque esta atacando verticalmente
               if(pecaXeque.linha < rei.linha){
                   //esta acima do rei
                   for(int linha = pecaXeque.linha; linha < rei.linha; linha++){
                       for(Peça peça: copiaPecas){
                           if(peça != rei && peça.cor != corAtual && peça.podeMovimentar(pecaXeque.coluna, linha)){
                               return false;
                           }
                       }
                   }
               }

               if(pecaXeque.linha > rei.linha){
                   //esta abaixo do rei
                   for(int linha = pecaXeque.linha; linha > rei.linha; linha--){
                       for(Peça peça: copiaPecas){
                           if(peça != rei && peça.cor != corAtual && peça.podeMovimentar(pecaXeque.coluna, linha)){
                               return false;
                           }
                       }
                   }
               }

           }else if(linhaDiff == 0){
               // a peca xeque esta atacando horizontalemente
               if(pecaXeque.coluna < rei.coluna){
                   // a peca esta a esquerda
                   for(int coluna= pecaXeque.linha; coluna < rei.coluna; coluna++){
                       for(Peça peça: copiaPecas){
                           if(peça != rei && peça.cor != corAtual && peça.podeMovimentar(coluna, pecaXeque.linha)){
                               return false;
                           }
                       }
                   }

               }

               if(pecaXeque.coluna > rei.coluna){
                   // a peca esta a direita
                   for(int coluna= pecaXeque.linha; coluna > rei.coluna; coluna--){
                       for(Peça peça: copiaPecas){
                           if(peça != rei && peça.cor != corAtual && peça.podeMovimentar(coluna, pecaXeque.linha)){
                               return false;
                           }
                       }
                   }

               }

           }else if(colDiff == linhaDiff){
               // a peca xeque esta atacando diagonalmente
               if(pecaXeque.linha < rei.linha){
                   // a peca xeque esta acima do rei
                   if(pecaXeque.coluna < rei.coluna){
                       // a peca xeque esta na esquerda acima
                       for(int coluna = pecaXeque.coluna, linha = pecaXeque.linha; coluna < rei.coluna; coluna++, linha++){
                           for(Peça peça: copiaPecas){
                               if(peça != rei && peça.cor != corAtual && peça.podeMovimentar(coluna, linha)){
                                   return false;
                               }
                           }
                       }

                   }

                   if(pecaXeque.coluna > rei.coluna){
                       // a peca xeque esta na direita acima
                       for(int coluna = pecaXeque.coluna, linha = pecaXeque.linha; coluna > rei.coluna; coluna--, linha++){
                           for(Peça peça: copiaPecas){
                               if(peça != rei && peça.cor != corAtual && peça.podeMovimentar(coluna, linha)){
                                   return false;
                               }
                           }
                       }

                   }

               }
               if(pecaXeque.linha > rei.linha){
                   // a peca xeque esta abaixo do rei
                   if(pecaXeque.coluna < rei.coluna){
                       //a peca xeque esta na esquerda
                       for(int coluna = pecaXeque.coluna, linha = pecaXeque.linha; coluna < rei.coluna; coluna++, linha--){
                           for(Peça peça: copiaPecas){
                               if(peça != rei && peça.cor != corAtual && peça.podeMovimentar(coluna, linha)){
                                   return false;
                               }
                           }
                       }

                   }

                   if(pecaXeque.coluna > rei.coluna){
                       // a peca xeque esta na direita abaixo
                       for(int coluna = pecaXeque.coluna, linha = pecaXeque.linha; coluna < rei.coluna; coluna--, linha--){
                           for(Peça peça: copiaPecas){
                               if(peça != rei && peça.cor != corAtual && peça.podeMovimentar(coluna, linha)){
                                   return false;
                               }
                           }
                       }

                   }
               }
           }
       }

       return true;
    }

    private Peça getRei(boolean op) {

        Peça rei = null;

        for (Peça peça : copiaPecas) {
            if(op){
                if (peça.tipo == TipoPeca.REI && (peça.cor != corAtual)) {
                    return peça;
                }
            }
            else{
                if(peça.tipo == TipoPeca.REI && peça.cor == corAtual){
                    rei = peça;
                }
            }
        }
        return rei; // Rei não encontrado (não deveria acontecer)
    }

    private boolean reiEmXeque() {

        Peça rei = getRei(true);

        if(pecaSelecionada.podeMovimentar(rei.coluna, rei.linha)){
            pecaXeque = pecaSelecionada;
            return true;
        }else{
            pecaXeque = null;
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

    //isso faz com que outras pecas nao possam se mexer antes do ei sair do xeque
    private boolean oponentePodematarORei(){
        Peça rei = getRei(false);

        for(Peça peça : copiaPecas){
            if(peça.cor != rei.cor && peça.podeMovimentar(rei.coluna, rei.linha)){
                return true;
            }
        }

        return false;
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

//        pecas.add(new Rei(branco,3,7));
//        pecas.add(new Rei(preto,0,3));
//        pecas.add(new Rainha(preto,4,5));


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