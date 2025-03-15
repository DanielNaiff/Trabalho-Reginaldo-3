package peças;

import principal.Tabuleiro;
import principal.Tela;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Peça {

    public BufferedImage png;
    public int x, y;
    public int coluna, linha, preColuna, preLinha;
    public int cor;
    public Peça peçaColidida;

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

    public boolean isNaLinha(int colunaAlvo, int linhaAlvo){
        if(colunaAlvo >= 0 && colunaAlvo <= 7 && linhaAlvo >= 0 && linhaAlvo <= 7){
            return true;
        }
        return false;
    }

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
        x = getX(coluna);
        y = getY(linha);
        preColuna = getColuna(x);
        preLinha = getLinha(y);

    }

    public boolean movimento(int colunaAlvo, int linhaAlvo){
        return false;
    }

    public Peça getColisao(int colunaAlvo, int linhaAlvo){
        for(Peça peça: Tela.copiaPecas){
            if(peça.coluna == colunaAlvo && peça.linha == linhaAlvo && peça != this){
                return peça;
            }
        }

        return null;
    }

    public int getX(int col){
        return coluna * Tabuleiro.tamanho;
    }

    public int getY(int linha){
        return linha * Tabuleiro.tamanho;
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
