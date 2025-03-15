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

    @Override
    public boolean podeMovimentar(int colunaAlvo, int linhaAlvo){
        if(isNaLinha(colunaAlvo, linhaAlvo) && !mesmoQuadrante(colunaAlvo, linhaAlvo)){
            if(Math.abs(colunaAlvo - preColuna) == Math.abs(linhaAlvo - preLinha)){
                if(estaNoQuadranteValido(colunaAlvo, linhaAlvo) && !estaNaDiagonal(colunaAlvo, linhaAlvo) ){
                    return true;
                }
            }
        }
        return false;
    }

}
