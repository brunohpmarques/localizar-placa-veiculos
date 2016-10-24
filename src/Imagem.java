import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class Imagem {
	private String nome;
	private String formato;
	private String caminho;
	private Mat matriz;
	
	public Imagem(String nome, String formato, String caminho) {
		this.nome = nome;
		this.formato = formato;
		this.caminho = caminho.replace(nome + formato, "");
		this.ler();
	}
	
	public Imagem(String nome, String formato, String caminho, Mat matriz) {
		this.nome = nome;
		this.formato = formato;
		this.caminho = caminho.replace(nome + formato, "");
		this.matriz = matriz;
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
	
	/** Le arquivo de imagem com nome e formato no caminho setado **/
	public Imagem ler(){
		this.matriz = Imgcodecs.imread(this.caminho, Imgcodecs.CV_LOAD_IMAGE_COLOR);
		return this;
	}
	
	/** Grava arquivo de imagem com nome e formato no caminho setado **/
	public Imagem gravar(){
		Imgcodecs.imwrite(caminho +"\\"+ nome + formato, matriz);
		return this;
	}
	
}