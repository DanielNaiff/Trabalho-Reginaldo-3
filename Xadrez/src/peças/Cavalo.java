package peças;

import principal.Tela;

public class Cavalo extends Peça{
    public Cavalo(int cor, int coluna, int linha) {
        super(cor, coluna, linha);

        if(cor == Tela.branco){
            png = getPng("/peça/cavalo-white");
        }else{
            png = getPng("/peça/cavalo-black");
        }
    }

    public boolean movimento(int colunaAlvo, int linhaAlvo){
        if(isNaLinha(colunaAlvo, linhaAlvo)){
            if(Math.abs(colunaAlvo - preColuna) * Math.abs(linhaAlvo - preLinha) == 2 ){

                if(estaNoQuadranteValido(colunaAlvo, linhaAlvo)){
                    return true;
                }
            }
        }
        return false;
    }
}
