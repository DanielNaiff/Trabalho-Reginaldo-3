package peças;

import principal.Tela;
import principal.TipoPeca;

public class Peao extends Peça{
    public Peao(int cor, int coluna, int linha) {
        super(cor, coluna, linha);

        tipo = TipoPeca.PEAO;

        if(cor == Tela.branco){
            png = getPng("/peça/peao-white");
        }else{
            png = getPng("/peça/peao-black");
        }
    }



    @Override
    public boolean podeMovimentar(int colunaAlvo, int linhaAlvo){
        if(isNaLinha(colunaAlvo, linhaAlvo) && !mesmoQuadrante(colunaAlvo, linhaAlvo)){
            int valor;
            if(cor == Tela.branco){
                valor = -1;
            }else{
                valor = 1;
            }
            peçaColidida = getColisao(colunaAlvo, linhaAlvo);

            // 1 quadrado de movimento
            if(colunaAlvo == preColuna && linhaAlvo == preLinha + valor && peçaColidida == null){
                return true;
            }

            if(colunaAlvo == preColuna && linhaAlvo == preLinha + valor*2 && peçaColidida == null && moveu == false && !estaEmLinhaReta(colunaAlvo, linhaAlvo)){
                return true;
            }

            //captura de movimento
            if(Math.abs(colunaAlvo - preColuna) == 1 && linhaAlvo == preLinha + valor && peçaColidida != null && peçaColidida.cor != cor){
                return true;
            }

            if(Math.abs(colunaAlvo - preColuna) == 1 && linhaAlvo == preLinha + valor){
                for(Peça peça : Tela.copiaPecas){
                    if(peça.coluna == colunaAlvo && peça.linha == preLinha && peça.pecaMoveuDoisPassos == true){
                        peçaColidida = peça;
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
