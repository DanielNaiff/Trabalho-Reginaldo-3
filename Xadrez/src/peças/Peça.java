package peças;

import principal.Tabuleiro;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Peça {

    public BufferedImage png;
    public int x, y;
    public int coluna, linha, preColuna, preLinha;
    public int cor;

    public Peça(int corr, int coluna, int linha){
        this.cor = cor;
        this.coluna = coluna;
        this.linha = linha;
        x = getX(coluna);
        y = getY(linha);
        preColuna = coluna;
        preLinha = linha;
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

    public int getX(int col){
        return coluna * Tabuleiro.tamanho;
    }

    public int getY(int linha){
        return linha * Tabuleiro.tamanho;
    }

    public void desenhar(Graphics2D graphics2D){
        graphics2D.drawImage(png, x, y, Tabuleiro.tamanho, Tabuleiro.tamanho, null);
    }
}
