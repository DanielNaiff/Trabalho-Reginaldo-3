package peças;

import principal.Tela;

public class Cavalo extends Peça{
    public Cavalo(int corr, int coluna, int linha) {
        super(corr, coluna, linha);

        if(cor == Tela.branco){
            png = getPng("/peça/cavalo-white");
        }else{
            png = getPng("/peça/cavalo-black");
        }
    }
}
