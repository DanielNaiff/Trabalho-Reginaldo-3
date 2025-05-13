public class Main {
    public static void main(String[] args) {
        RetanguloTest teste = new RetanguloTest();
        boolean resultado;

        resultado = teste.testCalcularArea();
        System.out.println("Teste calcular area: " + resultado);

        resultado = teste.testCalcularPerimetro();
        System.out.println("Teste calcular perimetro: " + resultado);
    }
}