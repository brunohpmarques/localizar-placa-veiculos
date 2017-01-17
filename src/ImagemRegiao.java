
public class ImagemRegiao {
	private Imagem imagem;
	private int qntVertical[];
	private int qntHorizontal[];
	
	public ImagemRegiao(Imagem i){
		this.imagem = i;
		this.qntVertical = null;
		this.qntHorizontal = null;
	}

	public Imagem getImagem() {
		return imagem;
	}

	public void setImagem(Imagem imagem) {
		this.imagem = imagem;
	}

	public int[] getQntVertical() {
		return qntVertical;
	}

	public void setQntVertical(int[] qntVertical) {
		this.qntVertical = qntVertical;
	}

	public int[] getQntHorizontal() {
		return qntHorizontal;
	}

	public void setQntHorizontal(int[] qntHorizontal) {
		this.qntHorizontal = qntHorizontal;
	}
}
