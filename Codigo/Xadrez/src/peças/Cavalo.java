package peças;

import principal.Tela;
import principal.TipoPeca;

public class Cavalo extends Peça{
    public Cavalo(int cor, int coluna, int linha) {
        super(cor, coluna, linha);

        tipo = TipoPeca.CAVALO;

        if(cor == Tela.branco){
            png = getPng("/peça/cavalo-white");
        }else{
            png = getPng("/peça/cavalo-black");
        }
    }

    @Override
    public boolean podeMovimentar(int colunaAlvo, int linhaAlvo){
        if(isNaLinha(colunaAlvo, linhaAlvo)){
            if(Math.abs(colunaAlvo - preColuna) * Math.abs(linhaAlvo - preLinha) == 2 ){

                if(estaNoQuadranteValido(colunaAlvo, linhaAlvo)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isNaLinha(int colunaAlvo, int linhaAlvo){
        if(colunaAlvo >= 0 && colunaAlvo <= 7 && linhaAlvo >= 0 && linhaAlvo <= 7){
            return true;
        }
        return false;
    }
}
