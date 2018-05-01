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

import ia.MyKNN;
import ia.MySVM;
import model.Imagem;
import utils.FileUtil;
import utils.PreProcessamento;
import utils.Segmentacao;

// documentacao http://www.w3ii.com/pt/java_dip/default.html
// Referencia http://wiki.ifba.edu.br/ads/tiki-download_file.php?fileId=827
public class Main {
	
	private static final String DIRECT_PROJECT = System.getProperty("user.dir");
	private static final String DIRECT_ENTRADA = DIRECT_PROJECT +"/baseFaceis";
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
	
	/**SVM*/
	public static void main(String[] args) {
		Date dateIni = new Date();
		Date dateTemp;
		Date dateFim;
		System.out.println("INICIOU AS: "+ dateIni.toString() +"\n");
		
		try {
			MySVM svm = new MySVM();
			svm.toTrain();
			
			System.out.println("Carregando imagens de entrada."); 
			ArrayList<Imagem> listaImagensEntrada = FileUtil.getListaImagens(DIRECT_ENTRADA, 0);
			dateFim = new Date();
			dateFim.setTime(dateFim.getTime()-dateIni.getTime());
			System.out.println(listaImagensEntrada.size() +" imagens de entrada carregadas em "+ (dateFim.getTime()/1000) +" segundos.\n");
			
			dateTemp = new Date();
			System.out.println("Iniciando pre-processamento as "+ dateTemp.toString()); 
			
			Imagem imgTemp;
			ArrayList<Imagem> regioesCandidatas;
			ArrayList<Imagem> regioesSelecionadas;
			int proc = 0;
			for (Imagem imagem : listaImagensEntrada) {
				imgTemp = AlgoritmosPreProc.clear(imagem);
				imgTemp.gravar();
				
				regioesCandidatas = Segmentacao.getRegioesCandidatas(imagem, imgTemp, 2);
				regioesSelecionadas = svm.toTest(regioesCandidatas);
				
				for (Imagem img : regioesSelecionadas) {
					img.gravar();
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
	
	public static void main2(String[] args) {
		try {
			MyKNN.Extractor.gerarVetorARFF("baseVetor");
			ArrayList<Imagem> al = MyKNN.Extractor.lerVetorARFF("baseVetor");
			System.err.println(al.size() +" imagens lidas");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main3(String[] args) {
		
		Date dateIni = new Date();
		Date dateTemp;
		Date dateFim;
		System.out.println("INICIOU AS: "+ dateIni.toString() +"\n");
		try{
			
			System.out.println("Carregando imagens de entrada."); 
			ArrayList<Imagem> listaImagensEntrada = FileUtil.getListaImagens(DIRECT_ENTRADA, 0);
			dateFim = new Date();
			dateFim.setTime(dateFim.getTime()-dateIni.getTime());
			System.out.println(listaImagensEntrada.size() +" imagens de entrada carregadas em "+ (dateFim.getTime()/1000) +" segundos.\n");
			
//			Collections.shuffle(listaImagensEntrada);
//			listaImagensEntrada = new ArrayList<Imagem>(listaImagensEntrada.subList(0, 10));

			System.out.println(listaImagensEntrada.size() +" imagens separadas para teste");
						
			dateTemp = new Date();
			System.out.println("Iniciando pre-processamento as "+ dateTemp.toString()); 
			
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
				imgTemp = AlgoritmosPreProc.clear(imagem); // NOSSO
//				imgTemp = AlgoritmosPreProc.Existentes.ufmgDiegoEAndres(imagem, pp);
				imgTemp.gravar();

				/** ALGORITMO DE DETECCAO DE REGIOES CANDIDATAS **/
				regioesCandidatas = Segmentacao.getRegioesCandidatas(imagem, imgTemp, 2);//0.25
//				for (Imagem candidata : regioesCandidatas) {
//					candidata.gravar();
//				}
				
				/** ALGORITMO DE DETECÇÃO DE SEGMENTACAO **/
				// MANUAL
				imgTemp = Segmentacao.getPlaca(regioesCandidatas); // na melhor base 22 de 52
				
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
}  
