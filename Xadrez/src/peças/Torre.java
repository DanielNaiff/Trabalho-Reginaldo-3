package peças;

import principal.Tela;

public class Torre extends Peça{
    public Torre(int cor, int coluna, int linha) {
        super(cor, coluna, linha);

        if(cor == Tela.branco){
            png = getPng("/peça/torre-white");
        }else{
            png = getPng("/peça/torre-black");
        }
    }

    public boolean movimento(int colunaAlvo, int linhaAlvo){
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
