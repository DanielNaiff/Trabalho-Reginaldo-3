package peças;

import principal.Tela;
import principal.TipoPeca;

public class Rainha extends Peça{
    public Rainha(int cor, int coluna, int linha) {
        super(cor, coluna, linha);

        tipo = TipoPeca.Rainha;

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
}
