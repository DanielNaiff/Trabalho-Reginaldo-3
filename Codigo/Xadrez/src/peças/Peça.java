package peças;

import principal.Tabuleiro;
import principal.Tela;
import principal.TipoPeca;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class Peça {

    public boolean moveu;
    public BufferedImage png;
    public int x, y;
    public TipoPeca tipo;
    public int coluna, linha, preColuna, preLinha;
    public int cor;
    public Peça peçaColidida;
    public boolean pecaMoveuDoisPassos;

    public Peça(int cor, int coluna, int linha){
        this.cor = cor;
        this.coluna = coluna;
        this.linha = linha;
        x = getX(coluna);
        y = getY(linha);
        preColuna = coluna;
        preLinha = linha;
    }

    public Peça(){}

    public boolean mesmoQuadrante(int colunaAlvo, int linhaAlvo){
        if(colunaAlvo == preColuna && linhaAlvo == preLinha){
            return true;
        }
        return false;
    }

    public boolean estaNaDiagonal(int colunaAlvo, int linhaALvo){
        if(linhaALvo < preLinha){
            //esquerda
            for(int c = preColuna - 1; c > colunaAlvo; c-- ){
                int diferenca = Math.abs(c - preColuna);
                for (Peça peça : Tela.copiaPecas){
                    if(peça.coluna == c && peça.linha == preLinha - diferenca){
                        peçaColidida = peça;
                        return true;
                    }
                }
            }

            //direita
            for(int c = preColuna + 1; c < colunaAlvo; c++ ){
                int diferenca = Math.abs(c - preColuna);
                for (Peça peça : Tela.copiaPecas){
                    if(peça.coluna == c && peça.linha == preLinha - diferenca){
                        peçaColidida = peça;
                        return true;
                    }
                }
            }
        }

        if(linhaALvo > preLinha) {
            // para baixo na esquerda
            for (int c = preColuna - 1; c > colunaAlvo; c--) {
                int diferenca = Math.abs(c - preColuna);
                for (Peça peça : Tela.copiaPecas) {
                    if (peça.coluna == c && peça.linha == preLinha - diferenca) {
                        peçaColidida = peça;
                        return true;
                    }
                }
            }

            // para cima na esquerda
            for (int c = preColuna + 1; c < colunaAlvo; c++) {
                int diferenca = Math.abs(c - preColuna);
                for (Peça peça : Tela.copiaPecas) {
                    if (peça.coluna == c && peça.linha == preLinha - diferenca) {
                        peçaColidida = peça;
                        return true;
                    }
                }
            }
        }

        return false;

    }

    public boolean estaEmLinhaReta(int colunaALvo, int linhaAlvo){
        //esquerda
        for(int c = preColuna - 1; c> colunaALvo; c--){
            for (Peça peça : Tela.copiaPecas){
                if(peça.coluna == c && peça.linha == linhaAlvo){
                    peçaColidida = peça;
                    return true;
                }
            }
        }

        //direita

        for(int c = preColuna + 1; c < colunaALvo; c++){
            for (Peça peça : Tela.copiaPecas){
                if(peça.coluna == c && peça.linha == linhaAlvo){
                    peçaColidida = peça;
                    return true;
                }
            }
        }

        for(int r = preLinha - 1; r > linhaAlvo; r--){
            for (Peça peça : Tela.copiaPecas){
                if(peça.coluna == colunaALvo && peça.linha == r){
                    peçaColidida = peça;
                    return true;
                }
            }
        }

        for(int r = preLinha + 1; r < linhaAlvo; r++){
            for (Peça peça : Tela.copiaPecas){
                if(peça.coluna == colunaALvo && peça.linha == r){
                    peçaColidida = peça;
                    return true;
                }
            }
        }

        return false;
    }

    public BufferedImage getPng(String imagePath){
        BufferedImage png = null;

        try{
            png = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));

        }catch (IOException e){
            e.printStackTrace();
        }

        return png;
    }

    public void resetarPosicao(){
        coluna = preColuna;
        linha = preLinha;
        x = getX(coluna);
        y = getY(linha);
    }

    //requisito do projeto
    public static int getX(int col) {
        return col * Tabuleiro.tamanho;
    }

    //requisito do projeto
    public static int getY(int linha) {
        return linha * Tabuleiro.tamanho;
    }

    //requisito do projeto
    public abstract boolean podeMovimentar(int colunaAlvo, int linhaAlvo);

    //requisito do projeto
    public abstract boolean isNaLinha(int colunaAlvo, int linhaAlvo);

    public boolean estaNoQuadranteValido(int colunaAlvo, int linhaAlvo){
        peçaColidida = getColisao(colunaAlvo,linhaAlvo);

        if(peçaColidida == null){
            return true;
        }else{
            if(peçaColidida.cor != this.cor){
                return true;
            }else{
                peçaColidida = null;
            }
        }

        return false;
    }

    public int getIndex(){
        for(int index = 0; index < Tela.copiaPecas.size(); index++){
            if(Tela.copiaPecas.get(index) == this){
                return index;
            }
        }

        return 0;
    }

    public void atualizarPosicao(){

        if(tipo == TipoPeca.PEAO){
            if(Math.abs(linha - preLinha) == 2){
                pecaMoveuDoisPassos = true;
            }
        }

        moveu = true;
        x = getX(coluna);
        y = getY(linha);
        preColuna = getColuna(x);
        preLinha = getLinha(y);

    }

    public Peça getColisao(int colunaAlvo, int linhaAlvo){
        for(Peça peça: Tela.copiaPecas){
            if(peça.coluna == colunaAlvo && peça.linha == linhaAlvo && peça != this){
                return peça;
            }
        }

        return null;
    }

    public int getColuna(int x){
        return (x + (Tabuleiro.tamanho/2))/Tabuleiro.tamanho;
    }

    public int getLinha(int y){
        return( y + (Tabuleiro.tamanho/2))/Tabuleiro.tamanho;
    }

    public void desenhar(Graphics2D graphics2D){
        graphics2D.drawImage(png, x, y, Tabuleiro.tamanho, Tabuleiro.tamanho, null);
    }

}
