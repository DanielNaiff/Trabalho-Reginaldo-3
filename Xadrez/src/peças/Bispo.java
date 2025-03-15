package peças;

import principal.Tela;

public class Bispo extends Peça{
    public Bispo(int cor, int coluna, int linha) {
        super(cor, coluna, linha);

        if(cor == Tela.branco){
            png = getPng("/peça/bispo-white");
        }else{
            png = getPng("/peça/bispo-black");
        }
    }

}
