import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.print.attribute.standard.NumberOfDocuments;
import javax.swing.plaf.basic.BasicFormattedTextFieldUI;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import ia.KNN;
import model.Imagem;
import model.PreProcessamento;
import model.Segmentacao;

// documentacao http://www.w3ii.com/pt/java_dip/default.html
// Referencia http://wiki.ifba.edu.br/ads/tiki-download_file.php?fileId=827
public class Main {
	
	private static final String DIRECT_PROJECT = System.getProperty("user.dir");
	private static final String DIRECT_ENTRADA = DIRECT_PROJECT +"/entrada3";
	private static final String DIRECT_PREPROCESSAMENTO = DIRECT_PROJECT +"/preprocessamento";
	private static final String DIRECT_SEGMENTACAO = DIRECT_PROJECT +"/segmentacao";
	
	static{
		// Carrega OPENCV 3.1
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		// Cria as pastas se nao existir
		File fe = new File(DIRECT_ENTRADA);
		File fp = new File(DIRECT_PREPROCESSAMENTO);
		File fs = new File(DIRECT_SEGMENTACAO);
		
		if(!fe.exists()){
			fe.mkdirs();
		}
		
		if(!fp.exists()){
			fp.mkdirs();
		}
		
		if(!fs.exists()){
			fs.mkdirs();
		}else{
			fs.mkdirs();
		}
		
	}
	
	public static void main2(String[] args) {
		try {
			KNN.Extractor.gerarVetorARFF("baseVetor");
			ArrayList<Imagem> al = KNN.Extractor.lerVetorARFF("baseVetor");
			System.err.println(al.size() +" imagens lidas");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		Date dateIni = new Date();
		Date dateTemp;
		Date dateFim;
		System.out.println("INICIOU AS: "+ dateIni.toString() +"\n");
		try{
			
			System.out.println("Carregando imagens de entrada."); 
			ArrayList<Imagem> listaImagensEntrada = getListaImagens(DIRECT_ENTRADA, 10);
			dateFim = new Date();
			dateFim.setTime(dateFim.getTime()-dateIni.getTime());
			System.out.println(listaImagensEntrada.size() +" imagens de entrada carregadas em "+ (dateFim.getTime()/1000) +" segundos.\n");
			
//			Collections.shuffle(listaImagensEntrada);
//			listaImagensEntrada = new ArrayList<Imagem>(listaImagensEntrada.subList(0, 10));

			System.out.println(listaImagensEntrada.size() +" imagens separadas para teste");
						
			dateTemp = new Date();
			System.out.println("Iniciando pre-processamento as "+ dateTemp.toString()); 
			PreProcessamento pp = new PreProcessamento(DIRECT_PREPROCESSAMENTO);
			Segmentacao s = new Segmentacao(DIRECT_SEGMENTACAO);
			
			// PRE-PROCESSAMENTO
			Imagem imgTemp;
			Imagem imgTemp1;
			Imagem imgTemp2;
			ArrayList<Imagem> regioesCandidatas;
			int proc = 0;
			for (Imagem imagem : listaImagensEntrada) {
				
//				imgTemp = sift(imagem);
//				match(imagem);
//				if(1+2==3) return;
				
				/** ALGORITMO DE PREPROCESSAMENTO **/
				imgTemp = AlgoritmosPreProc.clear(imagem, pp); // NOSSO
//				imgTemp = AlgoritmosPreProc.Existentes.ufmgDiegoEAndres(imagem, pp);
				imgTemp.gravar();

				/** ALGORITMO DE DETECCAO DE REGIOES CANDIDATAS **/
				regioesCandidatas = s.getRegioesCandidatas(pp, imagem, imgTemp, 2);//0.25
//				for (Imagem candidata : regioesCandidatas) {
//					candidata.gravar();
//				}
				
				/** ALGORITMO DE DETECÇÃO DE SEGMENTACAO **/
				// MANUAL
				imgTemp = s.getPlaca(regioesCandidatas); // na melhor base 22 de 52
				
				// KNN
//				imgTemp = KNN.run(listaImagensEntrada, regioesCandidatas);  // na melhor base 9 de 52
				
				if(imgTemp == null){
					System.err.println(imagem.getNome() +" eh dificil");
				}else{
					imgTemp.gravar();
				}
				
				if(++proc % 50 == 0){
					System.out.println("Imagens processadas: "+proc);
				}
			}
			
			dateFim = new Date();
			dateFim.setTime(dateFim.getTime()-dateIni.getTime());
			System.out.println("Fim do pre-processamento em "+ (dateFim.getTime()/1000) +" segundos.\n"); 
			
			System.out.println("Fim com sucesso.");
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fim com erro.");
		}
		
		dateFim = new Date();
		System.out.println("\nTERMINOU AS: "+ dateFim.toString());
		dateFim.setTime(dateFim.getTime()-dateIni.getTime());
		System.out.println("DURACAO: "+ dateFim.getTime()/1000 +" SEGUNDOS");
	}
	
	/** Instancia lista com todas as imagens de um diretorio **/
	public static ArrayList<Imagem> getListaImagens(String diretorio, int max) throws FileNotFoundException{
		File arquivos = new File(diretorio);
		
		if(!arquivos.isDirectory()){
			throw new FileNotFoundException(diretorio);
		}
		File[] arrayArquivos = arquivos.listFiles();
		
		if(arrayArquivos == null){
			throw new FileNotFoundException(diretorio);
		}
		
		if(arrayArquivos.length*0.66 < max){
			throw new NumberFormatException("Numero maximo: "+((int)arrayArquivos.length*0.66));
		}
		
		ArrayList<Imagem> listaImagens = new ArrayList<>();

		String mimetype;
        String type;
        Mat matriz;
		for (File arquivo : arrayArquivos) {
			if(!arquivo.isDirectory() && arquivo.canRead()){
				mimetype = new MimetypesFileTypeMap().getContentType(arquivo);
				type = mimetype.split("/")[0];
				if(type.equals("image")){
					type = getFileExtension(arquivo);
					mimetype = arquivo.getName().replaceAll(type, "");
					matriz = Imgcodecs.imread(arquivo.getAbsolutePath(), Imgcodecs.CV_LOAD_IMAGE_COLOR);
					listaImagens.add(new Imagem(mimetype, type, arquivo.getAbsolutePath(), matriz));
					if(max > 0 && listaImagens.size() == max){
						break;
					}
				}		
			}
		}
		return listaImagens;
	}
	
	/** Recupera extensao de um arquivo **/
	private static String getFileExtension(File file) {
	    String name = file.getName();
	    try {
	        return "."+ name.substring(name.lastIndexOf(".") + 1);
	    } catch (Exception e) {
	        return "";
	    }
	}

	
	/*
	 * Se der -> OpenCV Error: Bad argument (Specified feature detector type is not supported.) in cv::javaFeatureDetector::create
	 * Solucao: http://stackoverflow.com/questions/30657774/surf-and-sift-algorithms-doesnt-work-in-opencv-3-0-java
	 * */
	private static Imagem sift(Imagem imagem){
        Mat blurredImage = new Mat();
        Mat output = new Mat();
        Imagem img = new Imagem(imagem.getNome()+"_sift", imagem.getFormato(), DIRECT_PREPROCESSAMENTO, imagem.getMatriz().clone());

        // remove some noise
        Imgproc.blur(img.getMatriz(), blurredImage, new Size(7, 7));

        //convert to gray
        //Mat mat = new Mat(img.width(), img.height(), CvType.CV_8U, new Scalar(4));
        Mat gray = new Mat(img.getMatriz().width(), img.getMatriz().height(), CvType.CV_8U, new Scalar(4));
        Imgproc.cvtColor(img.getMatriz(), gray, Imgproc.COLOR_BGR2GRAY);

		FeatureDetector fd = FeatureDetector.create(FeatureDetector.BRISK); //ORB, MSER, GFTT, HARRIS, SIMPLEBLOB, BRISK, AKAZE
        MatOfKeyPoint regions = new MatOfKeyPoint();
        fd.detect(gray, regions);

//        System.out.println("REGIONS ARE: " + regions.rows());
        Features2d.drawKeypoints(gray, regions, output);
        
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
        MatOfDMatch matches = new MatOfDMatch();
        matcher.match(output, matches);
        
        img.setMatriz(output);
        return img;
	}
	
	//http://stackoverflow.com/questions/35428440/java-opencv-extracting-good-matches-from-knnmatch
	private static void match(Imagem imagem){
		FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
		DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
		DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
		
		// DETECTION
		// first image
		Mat img1 = Imgcodecs.imread(imagem.getCaminhoCompleto(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		Mat descriptors1 = new Mat();
		MatOfKeyPoint keypoints1 = new MatOfKeyPoint();

		detector.detect(img1, keypoints1);
		descriptor.compute(img1, keypoints1, descriptors1);

		// second image
		Mat img2 = Imgcodecs.imread("/basePlacas/PLACA_BASE.png", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
//		img2 = new Mat(img2, new Rect(0, 0, img1.width(), img1.height()));
		Mat descriptors2 = new Mat();
		MatOfKeyPoint keypoints2 = new MatOfKeyPoint();

		detector.detect(img2, keypoints2);
		descriptor.compute(img2, keypoints2, descriptors2);
		
		/*
		// MATCHING
		// match these two keypoints sets
		List<MatOfDMatch> matchesKNN = new ArrayList<MatOfDMatch>();
		matcher.knnMatch(descriptors1, descriptors2, matchesKNN, 5);
		
		// DRAWING OUTPUT
		Mat outputImg = new Mat();
		// this will draw all matches, works fine
		Features2d.drawMatches2(img1, keypoints1, img2, keypoints2, matchesKNN, outputImg);
		*/
		
		MatOfDMatch matches = new MatOfDMatch();
	    matcher.match(descriptors1, descriptors2, matches);
	    Mat outputImg2 = new Mat();
		Features2d.drawMatches(img1, keypoints1, img2, keypoints2, matches, outputImg2);
		
		// save image
		Imgcodecs.imwrite("result.jpg", outputImg2);
	}
}  
