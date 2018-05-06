import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import org.opencv.core.Core;

import ia.MySVM;
import model.Imagem;
import utils.ArffUtil;
import utils.ConstantesUtil;
import utils.FileUtil;
import utils.Segmentacao;

// Instalar OpenCV: https://docs.opencv.org/3.0-beta/doc/tutorials/introduction/java_eclipse/java_eclipse.html
// Documentacao http://www.w3ii.com/pt/java_dip/default.html
// Referencia http://wiki.ifba.edu.br/ads/tiki-download_file.php?fileId=827
public class Main {
		
	static{
		// Carrega OPENCV
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		File fe = new File(ConstantesUtil.PATH_INPUT);
		if(!fe.exists()){
			fe.mkdirs();
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
			ArrayList<Imagem> listaImagensEntrada = FileUtil.getListaImagens(ConstantesUtil.PATH_INPUT, 0);
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
				imgTemp = AlgoritmosPreProc.pcc(imagem);
				imgTemp.gravar();
				
				regioesCandidatas = Segmentacao.getRegioesCandidatas(imagem, imgTemp, 2);
				System.out.println(regioesCandidatas.size()+" regioes candidatas para "+imagem.getNome());
//				for (Imagem img : regioesCandidatas) {
//					img.gravar();
//				}
				
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
			ArffUtil.gerarARFF("baseVetor");
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
			ArrayList<Imagem> listaImagensEntrada = FileUtil.getListaImagens(ConstantesUtil.PATH_INPUT, 0);
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
