package peças;

import principal.Tela;

public class Rei extends Peça{
    public Rei(int corr, int coluna, int linha) {
        super(corr, coluna, linha);
        if(cor == Tela.branco){
            png = getPng("/peça/rei-white");
        }else{
            png = getPng("/peça/rei-black");
        }
    }
}
