package peças;

import principal.Tela;
import principal.TipoPeca;

public class Rainha extends Peça{
    public Rainha(int cor, int coluna, int linha) {
        super(cor, coluna, linha);

        tipo = TipoPeca.RAINHA;

        if(cor == Tela.branco){
            png = getPng("/peça/rainha-white");
        }else{
            png = getPng("/peça/rainha-black");
        }
    }

    @Override
    public boolean podeMovimentar(int colunaAlvo, int linhaAlvo){
        if(isNaLinha(colunaAlvo, linhaAlvo) && !mesmoQuadrante(colunaAlvo, linhaAlvo)){
            if(Math.abs(colunaAlvo - preColuna) == Math.abs(linhaAlvo - preLinha)){
                if(estaNoQuadranteValido(colunaAlvo, linhaAlvo) && !estaNaDiagonal(colunaAlvo, linhaAlvo) ){
                    return true;
                }
            }
        }

        if(isNaLinha(colunaAlvo, linhaAlvo) && !mesmoQuadrante(colunaAlvo, linhaAlvo)){
            if(colunaAlvo == preColuna || linhaAlvo == preLinha){
                if(estaNoQuadranteValido(colunaAlvo, linhaAlvo) && !estaEmLinhaReta(colunaAlvo, linhaAlvo)){
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
