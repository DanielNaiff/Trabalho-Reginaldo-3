public class Retangulo {
    private int base;
    private int altura;

    public Retangulo(int base, int altura) {
        this.base = base;
        this.altura = altura;
    }

    int calcularArea(){
        return base*altura;
    }

    int calcularPerimetro(){
        return 2*base + 2*altura;
    }
}
