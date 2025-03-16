package principal;

import java.awt.*;

public class Tabuleiro {

    public static final int tamanho = 64;

    public void desenhar(Graphics2D graphics2D){
        int a = 0;
        for(int linha = 0; linha < 8; linha++){
            for(int coluna = 0; coluna < 8; coluna++){
                if(a==0){
                    graphics2D.setColor(new Color(240,240,240));
                    a = 1;
                }else{
                    graphics2D.setColor(new Color(40,40,40));
                    a = 0;
                }
                graphics2D.fillRect(coluna*tamanho , linha*tamanho , tamanho , tamanho );
            }

            if(a == 0){
                a = 1;
            }else {
                a = 0;
            }
        }
    }
}
