package utils;
import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import model.Imagem;
/**
 * @author Bruno Marques
 * @author Danny Queiroz
 */
public class PreProcessamento {
	public enum Orientacao{
		TODOS('T'), HORIZONTAL('H'), VERTICAL('V');
		public char label;
		Orientacao(char label) {
			this.label = label;
		}
	}
	public enum Sinal{
		POSITIVO('P'), NEGATIVO('N');
		public char label;
		Sinal(char label) {
			this.label = label;
		}
	}
	
	static{
		File fp = new File(ConstantesUtil.PATH_PREPROCESSAMENTO);		
		if(!fp.exists()){
			fp.mkdirs();
		}		
	}
	
	/** Normaliza imagem de entrada **/
	public static Imagem normalizar(Imagem imagem) {
		Mat saida = imagem.getMatriz().clone();
        Imgproc.equalizeHist(imagem.getMatriz(), saida);
		return new Imagem(imagem.getNome() +"_norm", imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}

	/** Converte imagem para escala de tons de cinza **/
	public static Imagem paraTonsDeCinza(Imagem imagem) {
		Mat saida = imagem.getMatriz().clone();
        saida.put(0, 0);
        
		Imgproc.cvtColor(imagem.getMatriz(), saida, Imgproc.COLOR_RGB2GRAY);
		return new Imagem(imagem.getNome() +"_gray", imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}

	/** Converte imagem para preto e branco **/
	public static Imagem paraPretoEBrancoGlobal(Imagem imagem, double thresh) { // 127
		Mat saida = imagem.getMatriz().clone();
        
		Imgproc.threshold(imagem.getMatriz(), saida, thresh, 255, Imgproc.THRESH_BINARY);
		return new Imagem(imagem.getNome() +"_limiG", imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}
	
	/** Converte imagem para preto e branco usando limiar local 
	 * @param blockSize: Tamanho da area de vizinhança (impar)
	 * @param C: Apenas uma constante que eh subtraida da media ou media ponderada.
	 * **/
	public static Imagem paraPretoEBrancoLocal(Imagem imagem, int blockSize, double C) { // 15, 40
		Mat saida = imagem.getMatriz().clone();
        
		Imgproc.adaptiveThreshold(imagem.getMatriz(), saida, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, blockSize, C);
		return new Imagem(imagem.getNome() +"_limiL", imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}

	/** Converte imagem para preto e branco usando algoritmo de OTSU **/
	public static Imagem paraPretoEBrancoOTSU(Imagem imagem){
		Mat saida = imagem.getMatriz().clone();
		
		Imgproc.threshold(imagem.getMatriz(), saida, 0, 255, Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY);
		return new Imagem(imagem.getNome() +"_otsu", imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}
	
	/** Adiciona contraste na imagem **/
	public static Imagem filtroContraste(Imagem imagem, int value) { //10
        Mat saida = new Mat(imagem.getMatriz().rows(), imagem.getMatriz().cols(), imagem.getMatriz().type());
        imagem.getMatriz().convertTo(saida, -1, 10d * value / 100, 0);
        return new Imagem(imagem.getNome() +"_cont", imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
    }


	/** Adiciona birlho na imagem **/
	public static Imagem ajustarBrilho(Imagem imagem, double alpha, double beta) { // 10, 50
		Mat saida = imagem.getMatriz().clone();

		imagem.getMatriz().convertTo(saida, -1, alpha, beta);
		return new Imagem(imagem.getNome() +"_bril", imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}

	/** Adiciona nitidez na imagem **/
	public static Imagem filtroNitidez(Imagem imagem, double sigmaX, double alpha, double beta, double gamma) { // 10, 1.5, -0.5, 0
		Mat saida = imagem.getMatriz().clone();
		Imgproc.GaussianBlur(imagem.getMatriz(), saida, new Size(0, 0), sigmaX);

		Core.addWeighted(imagem.getMatriz(), alpha, saida, alpha, gamma, saida); 
		return new Imagem(imagem.getNome() +"_niti", imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}

	/** Aplica filtro box na imagem **/
	public static Imagem filtroBox(Imagem imagem, int maskOrder) { // 4
		Mat saida = imagem.getMatriz().clone();
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
		return new Imagem(imagem.getNome() +"_box", imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}
	
	/** Aplica filtro mediana na imagem **/
	public static Imagem filtroMediana(Imagem imagem, int maskOrder) { // 4
		Mat saida = imagem.getMatriz().clone();
		Imgproc.medianBlur(imagem.getMatriz(), saida, maskOrder);
		return new Imagem(imagem.getNome() +"_medi", imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}

	/** Aplica filtro gaussiano na imagem **/
	public static Imagem filtroGaussiano(Imagem imagem, int maskOrder, double sigmaX) { // 4, 9
		Mat saida = imagem.getMatriz().clone();
		Imgproc.GaussianBlur(imagem.getMatriz(), saida, new Size(maskOrder, maskOrder), sigmaX);
		
		return new Imagem(imagem.getNome() +"_gaus", imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}

	/** Aplica erosao na imagem **/
	public static Imagem morfoErosao(Imagem imagem, int tamanhoErosao) { // 3
		Mat saida = imagem.getMatriz().clone();

		Mat elemento = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(tamanhoErosao, tamanhoErosao));
		Imgproc.erode(imagem.getMatriz(), saida, elemento);
		return new Imagem(imagem.getNome() +"_eros", imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}
	
	/** Aplica operacao de erosao horizontal ou vertical na imagem **/
	public static Imagem morfoErosaoOrientacao(Imagem imagem, Orientacao orientacao, int fator){		
		Mat saida = imagem.getMatriz().clone();
		int saidaSize;
		Size sizeEstrutura;
		
		if(orientacao == Orientacao.HORIZONTAL){
			saidaSize = saida.cols() / fator;
			sizeEstrutura = new Size(saidaSize, 1);
		}else{
			saidaSize = saida.rows() / fator;
			sizeEstrutura = new Size(1, saidaSize);
			orientacao = Orientacao.VERTICAL;
		}
		
		Mat horizontalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, sizeEstrutura);
		Imgproc.erode(saida, saida, horizontalStructure, new Point(-1, -1), 1);	    
	    return new Imagem(imagem.getNome() +"_eros"+orientacao, imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}

	/** Aplica dilatacao na imagem **/
	public static Imagem morfoDilatacao(Imagem imagem, int tamanhoDilatacao) { // 3
		Mat saida = imagem.getMatriz().clone();

		Mat elemento = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(tamanhoDilatacao, tamanhoDilatacao));
		Imgproc.dilate(imagem.getMatriz(), saida, elemento);
		return new Imagem(imagem.getNome() +"_dila", imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}
	
	/** Aplica operacao de fechamento na imagem **/
	public static Imagem morfoFechamento(Imagem imagem, int largura, int altura){// 17, 3
		Mat saida = imagem.getMatriz().clone();
		Mat elemento = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(largura, altura) );
	    Imgproc.morphologyEx(imagem.getMatriz(), saida, Imgproc.MORPH_CLOSE, elemento);
	    
	    return new Imagem(imagem.getNome() +"_fech", imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}
	
	/** Aplica operacao de abertura na imagem **/
	public static Imagem morfoAbertura(Imagem imagem, int largura, int altura){// 17, 3
		Mat saida = imagem.getMatriz().clone();
		Mat elemento = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(largura, altura) );
	    Imgproc.morphologyEx(imagem.getMatriz(), saida, Imgproc.MORPH_OPEN, elemento);
	    
	    return new Imagem(imagem.getNome() +"_aber", imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}
	
	/** Aplica operacao de fechamento horizontal ou vertical na imagem **/
	public static Imagem morfoFechamentoOrientacao(Imagem imagem, Orientacao orientacao, int fator){// 20		
		Mat saida = imagem.getMatriz().clone();
		int saidaSize;
		Size sizeEstrutura;
		
		if(orientacao == Orientacao.HORIZONTAL){
			saidaSize = saida.cols() / fator;
			sizeEstrutura = new Size(saidaSize, 1);
		}else{
			saidaSize = saida.rows() / fator;
			sizeEstrutura = new Size(1, saidaSize);
		}
		
		Mat horizontalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, sizeEstrutura);
		Imgproc.dilate(saida, saida, horizontalStructure, new Point(-1, -1), 1);
	    Imgproc.erode(saida, saida, horizontalStructure, new Point(-1, -1), 1);
	    
	    return new Imagem(imagem.getNome() +"_fech"+orientacao, imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}
	
	/** Aplica operacao de dilatacao horizontal ou vertical na imagem **/
	public static Imagem morfoDilatacaoOrientacao(Imagem imagem, Orientacao orientacao, int fator){// 20		
		Mat saida = imagem.getMatriz().clone();
		int saidaSize;
		Size sizeEstrutura;
		
		if(orientacao == Orientacao.HORIZONTAL){
			saidaSize = saida.cols() / fator;
			sizeEstrutura = new Size(saidaSize, 1);
		}else{
			saidaSize = saida.rows() / fator;
			sizeEstrutura = new Size(1, saidaSize);
			orientacao = Orientacao.VERTICAL;
		}
		
		Mat horizontalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, sizeEstrutura);
		Imgproc.dilate(saida, saida, horizontalStructure, new Point(-1, -1), 1);	    
	    return new Imagem(imagem.getNome() +"_dila"+orientacao, imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}
	
	/** Aplica operacao de intersecao em duas imagens **/
	public static Imagem intersecao(Imagem imagem1, Imagem imagem2){	
		Mat saida = new Mat(imagem1.getMatriz().width(), imagem1.getMatriz().height(), imagem1.getMatriz().type());
		
		Core.bitwise_and(imagem1.getMatriz(), imagem2.getMatriz(), saida);
	    return new Imagem(imagem1.getNome() +"_inte", imagem1.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}

	/** Aplica Prewitt na imagem **/
	public static Imagem filtroPrewitt(Imagem imagem, Orientacao orientation) {
		Mat saida = imagem.getMatriz().clone();
		Mat mask = new Mat(3, 3, CvType.CV_32F);
		
		if(orientation == Orientacao.HORIZONTAL){
			mask.put(0, 0, -1);
			mask.put(0, 1, -1);
			mask.put(0, 2, -1);

			mask.put(1, 0, 0);
			mask.put(1, 1, 0);
			mask.put(1, 2, 0);

			mask.put(2, 0, 1);
			mask.put(2, 1, 1);
			mask.put(2, 2, 1);
		}else if(orientation == Orientacao.VERTICAL){
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
			orientation = Orientacao.TODOS;
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
		return new Imagem(imagem.getNome() +"_prew"+orientation, imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}

	/** Aplica Sobel na imagem **/
	public static Imagem filtroSobel(Imagem imagem, Orientacao orientation) {
		Mat saida = imagem.getMatriz().clone();
		Mat mask = new Mat(3, 3, CvType.CV_32F);
		
		if(orientation == Orientacao.HORIZONTAL){
			mask.put(0, 0, -1);
			mask.put(0, 1, -2);
			mask.put(0, 2, -1);

			mask.put(1, 0, 0);
			mask.put(1, 1, 0);
			mask.put(1, 2, 0);

			mask.put(2, 0, 1);
			mask.put(2, 1, 2);
			mask.put(2, 2, 1);
		}else if(orientation == Orientacao.VERTICAL){
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
			orientation = Orientacao.TODOS;
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
		return new Imagem(imagem.getNome() +"_sobe"+orientation, imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}
	
	/** Aplica filtro canny na imagem **/
	public static Imagem filtroCanny(Imagem imagem, double thresh1, double thresh2) {
		Mat saida = imagem.getMatriz().clone();
		Imgproc.Canny(imagem.getMatriz(), saida, thresh1, thresh2);
		return new Imagem(imagem.getNome() +"_cann", imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
  }

  /** Aplica filtro canny na imagem **/
	public static Imagem filtroCanny(Imagem imagem, double thresh1, double thresh2, int aperture) { // 10, 100, 5
		Mat saida = imagem.getMatriz().clone();
		Imgproc.Canny(imagem.getMatriz(), saida, thresh1, thresh2, aperture, true);
		return new Imagem(imagem.getNome() +"_cann", imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}
	
	/** Aplica filtro canny automatico na imagem **/
	public static Imagem filtroAutoCanny(Imagem imagem, double sigma) {
		if(sigma <= 0){
			sigma = 0.33;
		}
		
		imagem = filtroGaussiano(imagem, 3, 0);
		
		Mat saida = imagem.getMatriz().clone();
		MatOfDouble mu = new MatOfDouble();
		MatOfDouble sigma2 = new MatOfDouble();
		Core.meanStdDev(imagem.getMatriz(), mu, sigma2);
		double media = mu.get(0, 0)[0];
		
		int min = (int)Math.max(0, (1.0 - sigma) * media);
		int max = (int)Math.min(255, (1.0 + sigma) * media);
		
		Imgproc.Canny(imagem.getMatriz(), saida, min, max);
		return new Imagem(imagem.getNome() +"_cannA", imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}
	
	/** Aplica filtro laplaciano na imagem **/
	public static Imagem filtroLaplaciano(Imagem imagem, Sinal valor) {
		Mat saida = imagem.getMatriz().clone();
		Mat mask = new Mat(3, 3, CvType.CV_32F);
		
		if(valor == Sinal.NEGATIVO){
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
			valor = Sinal.POSITIVO;
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
		return new Imagem(imagem.getNome() +"_lapl"+valor, imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}
	
	/** Redimensiona a imagem **/
	public static Imagem redimensionar(Imagem imagem, int width, int heigth) {
		if(width < 0){width = 0;}
		if(heigth < 0){heigth = 0;}
		Mat saida = imagem.getMatriz().clone();
		Imgproc.resize(saida, saida, new Size(width, heigth));

		return new Imagem(imagem.getNome() +"_redim", imagem.getFormato(), ConstantesUtil.PATH_PREPROCESSAMENTO, saida);
	}
}
