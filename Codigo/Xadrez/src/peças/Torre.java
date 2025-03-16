package peças;

import principal.Tela;
import principal.TipoPeca;

public class Torre extends Peça{
    public Torre(int cor, int coluna, int linha) {
        super(cor, coluna, linha);

         tipo = TipoPeca.TORRE;

        if(cor == Tela.branco){
            png = getPng("/peça/torre-white");
        }else{
            png = getPng("/peça/torre-black");
        }
    }

    @Override
    public boolean podeMovimentar(int colunaAlvo, int linhaAlvo){
        if(isNaLinha(colunaAlvo, linhaAlvo) && !mesmoQuadrante(colunaAlvo, linhaAlvo)){
            if(colunaAlvo == preColuna || linhaAlvo == preLinha){
                if(estaNoQuadranteValido(colunaAlvo, linhaAlvo) && !estaEmLinhaReta(colunaAlvo, linhaAlvo)){
                    return true;
                }
            }
        }
        return false;
    }
}
