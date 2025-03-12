package peças;

import principal.Tela;

public class Bispo extends Peça{
    public Bispo(int corr, int coluna, int linha) {
        super(corr, coluna, linha);

        if(cor == Tela.branco){
            png = getPng("/peça/bispo-white");
        }else{
            png = getPng("/peça/bispo-black");
        }
    }
}
