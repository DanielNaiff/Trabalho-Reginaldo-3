package peças;

import principal.Tela;

public class Peao extends Peça{
    public Peao(int cor, int coluna, int linha) {
        super(cor, coluna, linha);

        if(cor == Tela.branco){
            png = getPng("/peça/peao-white");
        }else{
            png = getPng("/peça/peao-black");
        }
    }
}
