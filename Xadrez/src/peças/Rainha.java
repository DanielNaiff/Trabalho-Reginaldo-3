package peças;

import principal.Tela;

public class Rainha extends Peça{
    public Rainha(int cor, int coluna, int linha) {
        super(cor, coluna, linha);

        if(cor == Tela.branco){
            png = getPng("/peça/rainha-white");
        }else{
            png = getPng("/peça/rainha-black");
        }
    }
}
