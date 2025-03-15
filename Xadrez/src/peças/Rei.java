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

    @Override
    public boolean podeMovimentar(int colunaAlvo, int linhaAlvo){
        if(isNaLinha(colunaAlvo, linhaAlvo)){
            if(Math.abs(colunaAlvo - preColuna) + Math.abs(linhaAlvo - preLinha) == 1 || Math.abs(colunaAlvo - preColuna)*Math.abs(linhaAlvo - preLinha) == 1){
                if(estaNoQuadranteValido(colunaAlvo, linhaAlvo)){
                    return true;
                }
            }
            if(!moveu){
                //roque direita
                if (colunaAlvo == preColuna+2 && linhaAlvo == preLinha && !estaEmLinhaReta(colunaAlvo, linhaAlvo)){
                    for(Peça peça : Tela.copiaPecas){
                        if(peça.coluna == preColuna+3 && peça.linha == preLinha && !peça.moveu){
                            Tela.roque = peça;
                            return true;
                        }
                    }
                }

                //roque esquerdo
                if (colunaAlvo == preColuna-2 && linhaAlvo == preLinha && !estaEmLinhaReta(colunaAlvo, linhaAlvo)){
                    Peça p[] = new Peça[2];
                    for(Peça peça : Tela.copiaPecas){
                        if(peça.coluna == preColuna-3 && peça.linha == linhaAlvo){
                            p[0] = peça;
                        }
                        if(peça.coluna == preColuna-4 && peça.linha == linhaAlvo){
                            p[1] = peça;
                        }
                        if(p[0] == null && p[1] != null && p[1].moveu == false){
                            Tela.roque = p[1];
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
