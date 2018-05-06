package model;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class Imagem {
	private String nome;
	private String formato;
	private String caminho;
	private Mat matriz;
	
	private double aspect;
	private double norm;
	private double mean;
	private double sum;
	private double trace;
	private float quantidadePixelsClaros;
	private float quantidadePixelsEscuros;
	private int quantidadeCompInternos;
	
	private int[] histograma;
	private double distancia;
	
	public Imagem(){}
	
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
	
	public Imagem(String nome, double aspect, double norm, double mean, double sum,
			double trace, float quantidadePixelsClaros,
			float quantidadePixelsEscuros) {
		this.aspect = aspect;
		this.nome = nome;
		this.norm = norm;
		this.mean = mean;
		this.sum = sum;
		this.trace = trace;
		this.quantidadePixelsClaros = quantidadePixelsClaros;
		this.quantidadePixelsEscuros = quantidadePixelsEscuros;
	}

	public Imagem(String nome, String formato, String caminho, Mat matriz, float quantidadePixelsClaros,
			float quantidadePixelsEscuros, int[] histograma, double distancia) {
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

	public double getAspect() {
		return aspect;
	}

	public void setAspect(double aspect) {
		this.aspect = aspect;
	}

	public double getNorm() {
		return norm;
	}

	public void setNorm(double norm) {
		this.norm = norm;
	}

	public double getMean() {
		return mean;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}

	public double getSum() {
		return sum;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}

	public double getTrace() {
		return trace;
	}

	public void setTrace(double trace) {
		this.trace = trace;
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
