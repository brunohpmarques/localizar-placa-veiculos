package model;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class Imagem {
	private String nome;
	private String formato;
	private String caminho;
	private Mat matriz;
	private float quantidadePixelsClaros;
	private float quantidadePixelsEscuros;
	private int[] histograma;
	private double distancia;
	
	public Imagem(String nome, int[] histograma){
		this.nome = nome;
		this.histograma = histograma;
	}

	public Imagem(String nome, String formato, String caminho, Mat matriz) {
		this.nome = nome;
		this.formato = formato;
		this.caminho = caminho.replace(nome + formato, "");
		this.matriz = matriz;
	}

	public Imagem(String nome, String formato, String caminho, Mat matriz, float quantidadePixelsClaros,
			float quantidadePixelsEscuros, int[] histograma, double distancia) {
		super();
		this.nome = nome;
		this.formato = formato;
		this.caminho = caminho;
		this.matriz = matriz;
		this.quantidadePixelsClaros = quantidadePixelsClaros;
		this.quantidadePixelsEscuros = quantidadePixelsEscuros;
		this.histograma = histograma;
		this.distancia = distancia;
	}

	public String getNome() {
		return nome;
	}

	public Imagem setNome(String nome) {
		this.nome = nome;
		return this;
	}

	public String getFormato() {
		return formato;
	}

	public Imagem setFormato(String formato) {
		this.formato = formato;
		return this;
	}

	public String getCaminho() {
		return caminho;
	}
	
	public String getCaminhoCompleto() {
		return caminho + nome + formato;
	}

	public Imagem setCaminho(String caminho) {
		this.caminho = caminho;
		return this;
	}

	public Mat getMatriz() {
		return matriz;
	}

	public Imagem setMatriz(Mat matriz) {
		this.matriz = matriz;
		return this;		
	}
	
	public float getQuantidadePixelsClaros() {
		return quantidadePixelsClaros;
	}

	public void setQuantidadePixelsClaros(float quantidade) {
		this.quantidadePixelsClaros = quantidade;
	}
	
	public float getQuantidadePixelsEscuros() {
		return quantidadePixelsEscuros;
	}

	public void setQuantidadePixelsEscuros(float quantidade) {
		this.quantidadePixelsEscuros = quantidade;
	}
	
	/** Grava arquivo de imagem com nome e formato no caminho setado **/
	public Imagem gravar(){
		Imgcodecs.imwrite(caminho +"\\"+ nome + formato, matriz);
		return this;
	}

	public int[] getHistograma() {
		return this.histograma;
	}

	public void setHistograma(int[] histograma) {
		this.histograma = histograma;
	}

	public double getDistancia() {
		return distancia;
	}

	public void setDistancia(double distancia) {
		this.distancia = distancia;
	}
	
	public Imagem clone(){
		return new Imagem(nome, formato, caminho, matriz, quantidadePixelsClaros,
				quantidadePixelsEscuros, histograma, distancia);
	}
	
	@Override
    public boolean equals(Object o){
        if (o instanceof Imagem){
        	Imagem i = (Imagem) o;
        	String n1 = i.getNome().split("_")[0];
        	String n2 = this.nome.split("_")[1];
        	if(n1.equalsIgnoreCase(n2)){
        		return true;
        	}
        }
        return false;
    }
	
}
