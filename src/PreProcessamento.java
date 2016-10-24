import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class PreProcessamento {
	public static final char HORIZONTAL = 'H';
	public static final char VERTICAL = 'V';
	public static final char TODOS = 'T';
	public static final char POSITIVA = 'P';
	public static final char NEGATIVA = 'N';

	private String diretorioSaida;
	
	public PreProcessamento(String diretorioSaida){
		this.diretorioSaida = diretorioSaida;
	}

	/** Converte imagem para escala de tons de cinza **/
	public Imagem paraTonsDeCinza(Imagem imagem) {
        Mat saida = new Mat(imagem.getMatriz().height(), imagem.getMatriz().width(), imagem.getMatriz().type());
        saida.put(0, 0);
        
		Imgproc.cvtColor(imagem.getMatriz(), saida, Imgproc.COLOR_RGB2GRAY);
		return new Imagem(imagem.getNome() +"_gray", imagem.getFormato(), diretorioSaida, saida);
	}

	/** Converte imagem para preto e branco **/
	public Imagem paraPretoEBranco(Imagem imagem, double thresh) {
        Mat saida = new Mat(imagem.getMatriz().height(), imagem.getMatriz().width(), imagem.getMatriz().type());
        
		Imgproc.threshold(imagem.getMatriz(), saida, thresh, 255, Imgproc.THRESH_BINARY); // 127
		return new Imagem(imagem.getNome() +"_limi", imagem.getFormato(), diretorioSaida, saida);
	}

	/** Adiciona contraste na imagem **/
	public Imagem filtroContraste(Imagem imagem) {
		Mat saida = new Mat(imagem.getMatriz().height(), imagem.getMatriz().width(), imagem.getMatriz().type());

		Imgproc.equalizeHist(imagem.getMatriz(), saida);
		return new Imagem(imagem.getNome() +"_cont", imagem.getFormato(), diretorioSaida, saida);
	}

	/** Adiciona birlho na imagem **/
	public Imagem filtroBrilho(Imagem imagem, double alpha, double beta) { // 10, 50
		Mat saida = new Mat(imagem.getMatriz().height(), imagem.getMatriz().width(), imagem.getMatriz().type());

		imagem.getMatriz().convertTo(saida, -1, alpha, beta);
		return new Imagem(imagem.getNome() +"_bril", imagem.getFormato(), diretorioSaida, saida);
	}

	/** Adiciona nitidez na imagem **/
	public Imagem filtroNitidez(Imagem imagem, double sigmaX, double alpha, double beta, double gamma) { // 10, 1.5, -0.5, 0
		Mat saida = new Mat(imagem.getMatriz().height(), imagem.getMatriz().width(), imagem.getMatriz().type());
		Imgproc.GaussianBlur(imagem.getMatriz(), saida, new Size(0, 0), sigmaX);

		Core.addWeighted(imagem.getMatriz(), alpha, saida, alpha, gamma, saida); 
		return new Imagem(imagem.getNome() +"_niti", imagem.getFormato(), diretorioSaida, saida);
	}

	/** Aplica filtro box na imagem **/
	public Imagem filtroBox(Imagem imagem, int maskOrder) { // 4
		Mat saida = new Mat(imagem.getMatriz().height(), imagem.getMatriz().width(), imagem.getMatriz().type());
		Mat mask = Mat.ones(maskOrder, maskOrder, CvType.CV_32F);

		for (int i = 0; i < mask.rows(); i++) {
			for (int j = 0; j < mask.cols(); j++) {

				double[] m = mask.get(i, j);

				for (int k = 0; k < m.length; k++) {
					m[k] = m[k] / (maskOrder * maskOrder);
				}
				mask.put(i, j, m);
			}
		}

		Imgproc.filter2D(imagem.getMatriz(), saida, -1, mask);
		return new Imagem(imagem.getNome() +"_box", imagem.getFormato(), diretorioSaida, saida);
	}

	/** Aplica filtro gaussiano na imagem **/
	public Imagem filtroGaussiano(Imagem imagem, int maskOrder, double sigmaX) { // 4, 9
		Mat saida = new Mat(imagem.getMatriz().height(), imagem.getMatriz().width(), imagem.getMatriz().type());
		Imgproc.GaussianBlur(imagem.getMatriz(), saida, new Size(maskOrder, maskOrder), sigmaX);
		
		return new Imagem(imagem.getNome() +"_gaus", imagem.getFormato(), diretorioSaida, saida);
	}

	/** Aplica erosao na imagem **/
	public Imagem morfoErosao(Imagem imagem, int tamanhoErosao) { // 3
		Mat saida = new Mat(imagem.getMatriz().height(), imagem.getMatriz().width(), imagem.getMatriz().type());
		Mat elemento = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * tamanhoErosao + 1, 2 * tamanhoErosao + 1));
		Imgproc.erode(imagem.getMatriz(), saida, elemento);
		
		return new Imagem(imagem.getNome() +"_eros", imagem.getFormato(), diretorioSaida, saida);
	}

	/** Aplica dilatacao na imagem **/
	public Imagem morfoDilatacao(Imagem imagem, int tamanhoDilatacao) { // 3
		Mat saida = new Mat(imagem.getMatriz().height(), imagem.getMatriz().width(), imagem.getMatriz().type());
		Mat elemento = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * tamanhoDilatacao + 1, 2 * tamanhoDilatacao + 1));
		Imgproc.dilate(imagem.getMatriz(), saida, elemento);
		
		return new Imagem(imagem.getNome() +"_dila", imagem.getFormato(), diretorioSaida, saida);
	}

	/** Aplica Prewitt na imagem **/
	public Imagem filtroPrewitt(Imagem imagem, char orientation) {
		Mat saida = new Mat(imagem.getMatriz().height(), imagem.getMatriz().width(), imagem.getMatriz().type());
		Mat mask = new Mat(3, 3, CvType.CV_32F);
		
		if(orientation == HORIZONTAL){
			mask.put(0, 0, -1);
			mask.put(0, 1, -1);
			mask.put(0, 2, -1);

			mask.put(1, 0, 0);
			mask.put(1, 1, 0);
			mask.put(1, 2, 0);

			mask.put(2, 0, 1);
			mask.put(2, 1, 1);
			mask.put(2, 2, 1);
		}else if(orientation == VERTICAL){
			mask.put(0, 0, -1);
			mask.put(0, 1, 0);
			mask.put(0, 2, 1);

			mask.put(1, 0, -1);
			mask.put(1, 1, 0);
			mask.put(1, 2, 1);

			mask.put(2, 0, -1);
			mask.put(2, 1, 0);
			mask.put(2, 2, 1);
		}else{
			orientation = TODOS;
			mask.put(0, 0, 0);
			mask.put(0, 1, 2);
			mask.put(0, 2, 2);

			mask.put(1, 0, 0);
			mask.put(1, 1, -4);
			mask.put(1, 2, 2);

			mask.put(2, 0, -2);
			mask.put(2, 1, 0);
			mask.put(2, 2, 0);
		}
		
		Imgproc.filter2D(imagem.getMatriz(), saida, -1, mask);
		return new Imagem(imagem.getNome() +"_prew"+orientation, imagem.getFormato(), diretorioSaida, saida);
	}

	/** Aplica Sobel na imagem **/
	public Imagem filtroSobel(Imagem imagem, char orientation) {
		Mat saida = new Mat(imagem.getMatriz().height(), imagem.getMatriz().width(), imagem.getMatriz().type());
		Mat mask = new Mat(3, 3, CvType.CV_32F);
		
		if(orientation == HORIZONTAL){
			mask.put(0, 0, -1);
			mask.put(0, 1, -2);
			mask.put(0, 2, -1);

			mask.put(1, 0, 0);
			mask.put(1, 1, 0);
			mask.put(1, 2, 0);

			mask.put(2, 0, 1);
			mask.put(2, 1, 2);
			mask.put(2, 2, 1);
		}else if(orientation == VERTICAL){
			mask.put(0, 0, -1);
			mask.put(0, 1, 0);
			mask.put(0, 2, 1);

			mask.put(1, 0, -2);
			mask.put(1, 1, 0);
			mask.put(1, 2, 2);

			mask.put(2, 0, -1);
			mask.put(2, 1, 0);
			mask.put(2, 2, 1);
		}else{
			orientation = TODOS;
			mask.put(0, 0, 0);
			mask.put(0, 1, 2);
			mask.put(0, 2, 2);

			mask.put(1, 0, -2);
			mask.put(1, 1, 0);
			mask.put(1, 2, 2);

			mask.put(2, 0, -2);
			mask.put(2, 1, -2);
			mask.put(2, 2, 0);
		}
		
		Imgproc.filter2D(imagem.getMatriz(), saida, -1, mask);
		return new Imagem(imagem.getNome() +"_sobe"+orientation, imagem.getFormato(), diretorioSaida, saida);
	}
	
	/** Aplica filtro laplaciano na imagem **/
	public Imagem filtroLaplaciano(Imagem imagem, char valor) {
		Mat saida = new Mat(imagem.getMatriz().height(), imagem.getMatriz().width(), imagem.getMatriz().type());
		Mat mask = new Mat(3, 3, CvType.CV_32F);
		
		if(valor == NEGATIVA){
			mask.put(0, 0, 0);
			mask.put(0, 1, -1);
			mask.put(0, 2, 0);

			mask.put(1, 0, -1);
			mask.put(1, 1, 4);
			mask.put(1, 2, -1);

			mask.put(2, 0, 0);
			mask.put(2, 1, -1);
			mask.put(2, 2, 0);
		}else{
			valor = POSITIVA;
			mask.put(0, 0, 0);
			mask.put(0, 1, 1);
			mask.put(0, 2, 0);

			mask.put(1, 0, 1);
			mask.put(1, 1, -4);
			mask.put(1, 2, 1);

			mask.put(2, 0, 0);
			mask.put(2, 1, 1);
			mask.put(2, 2, 0);
		}	

		Imgproc.filter2D(imagem.getMatriz(), saida, -1, mask);
		return new Imagem(imagem.getNome() +"_lapl"+valor, imagem.getFormato(), diretorioSaida, saida);
	}

}
