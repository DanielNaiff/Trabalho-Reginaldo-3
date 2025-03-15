package principal;

import peças.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Tela extends JPanel implements Runnable {

    boolean isMover;
    boolean isQuadrante;

    Interagir interagir = new Interagir();
    static Thread threadJogo;
    private boolean executando;
    Peça pecaSelecionada;
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
        addMouseMotionListener(interagir)  ;
        addMouseListener(interagir);


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

        // Verifica se existe uma peça selecionada
        if(pecaSelecionada != null) {
            if(isMover){
                // Define a cor de desenho para branco (geralmente, isso seria para desenhar um contorno ou um destaque)
                graphics2D.setColor(Color.pink);

                // Define o nível de transparência para 0, ou seja, 100% de transparência (totalmente invisível)
                // Isso provavelmente é feito para apagar qualquer forma anterior da peça antes de desenhá-la na nova posição
                graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.f));

                // Desenha um retângulo na posição da peça selecionada
                // O retângulo é desenhado nas coordenadas relativas ao tabuleiro, com o tamanho definido pela constante 'Tabuleiro.tamanho'
                // Isso apagaria qualquer forma anterior que tivesse sido desenhada na posição da peça
                graphics2D.fillRect(pecaSelecionada.coluna * Tabuleiro.tamanho, pecaSelecionada.linha * Tabuleiro.tamanho, Tabuleiro.tamanho, Tabuleiro.tamanho);

                // Restaura a transparência para 100% (opacidade total), garantindo que os próximos desenhos não sejam transparentes
                graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
            //Desenha a peca selecionada
            pecaSelecionada.desenhar(graphics2D);
        }

    }

    //se uma peca esta sendo segurada, mantem a posicao
    private void simular(){

        //Resetar a lista de pecas
        copiarPecas(pecas, copiaPecas);

        pecaSelecionada.x = interagir.x - Tabuleiro.tamanho/2;
        pecaSelecionada.y = interagir.y - Tabuleiro.tamanho/2;

        pecaSelecionada.coluna = pecaSelecionada.getColuna(pecaSelecionada.x);
        pecaSelecionada.linha = pecaSelecionada.getLinha(pecaSelecionada.y);

        isMover = false;
        isQuadrante = false;

        if(pecaSelecionada.podeMovimentar(pecaSelecionada.coluna, pecaSelecionada.linha)){
            isQuadrante = true;

            if(pecaSelecionada.peçaColidida != null){
                copiaPecas.remove(pecaSelecionada.peçaColidida.getIndex());
            }
            isMover = true;
        }
    }

    private void atualizar() {
        //Funcao para detectar movimento do mouse
        if(interagir.clicou){
            if (pecaSelecionada == null){
                // se pecaSelecionada e null, checha se voce pode escolher uma peca
                for(Peça peça: copiaPecas){
                    //se o mouse esta em cima de uma peca aliada, entao escolhe a peca ativada
                    if(peça.cor == corAtual && peça.coluna == interagir.x/Tabuleiro.tamanho && peça.linha == interagir.y/Tabuleiro.tamanho){
                        pecaSelecionada = peça;
                        break;
                    }
                }
            }
            else{

                // se o jogador esta segurando a peca, entao ele simula o movimento
                simular();
            }
        }

        if(interagir.clicou == false){
            if(pecaSelecionada != null){
                if(isQuadrante){
                    //atualiza a peca caso dela ter sido capturada e removida durante a simulacao
                    copiarPecas(copiaPecas,pecas);
                    pecaSelecionada.atualizarPosicao();
                }else {
                    copiarPecas(pecas, copiaPecas);
                    pecaSelecionada.resetarPosicao();
                    pecaSelecionada = null;
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
        pecas.add(new Rainha(branco, 3, 3));
        pecas.add(new Rei(branco, 4, 7));


    }

    private void copiarPecas(ArrayList<Peça> atuais, ArrayList<Peça> copias){
        copias.clear();
        for(int i = 0; i< atuais.size(); i++){
            copias.add(atuais.get(i));
        }
    }


}
