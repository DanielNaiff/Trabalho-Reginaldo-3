package peças;

import principal.Tela;

public class Rei extends Peça{
    public Rei(int cor, int coluna, int linha) {
        super(cor, coluna, linha);
        if(cor == Tela.branco){
            png = getPng("/peça/rei-white");
        }else{
            png = getPng("/peça/rei-black");
        }
    }

    public boolean movimento(int colunaAlvo, int linhaAlvo){
        if(isNaLinha(colunaAlvo, linhaAlvo)){
            if(Math.abs(colunaAlvo - preColuna) + Math.abs(linhaAlvo - preLinha) == 1 || Math.abs(colunaAlvo - preColuna)*Math.abs(linhaAlvo - preLinha) == 1){

                if(estaNoQuadranteValido(colunaAlvo, linhaAlvo)){
                    return true;
                }
            }
        }
        return false;
    }
}
