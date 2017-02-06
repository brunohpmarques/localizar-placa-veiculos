import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.activation.MimetypesFileTypeMap;
import javax.print.attribute.standard.NumberOfDocuments;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import ia.KNN;
import model.Imagem;
import model.PreProcessamento;
import model.Segmentacao;

// documentacao http://www.w3ii.com/pt/java_dip/default.html
// Referencia http://wiki.ifba.edu.br/ads/tiki-download_file.php?fileId=827
public class Main {
	
	private static final String DIRECT_PROJECT = System.getProperty("user.dir");
	private static final String DIRECT_ENTRADA = DIRECT_PROJECT +"/entrada";
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
		
//		try {
//			KNN.Extractor.gerarHistogramaARFF("base");
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
	}
	
	public static void main(String[] args) {
		
		Date dateIni = new Date();
		Date dateTemp;
		Date dateFim;
		System.out.println("INICIOU AS: "+ dateIni.toString() +"\n");
		try{
			
			System.out.println("Carregando imagens de entrada."); 
			ArrayList<Imagem> listaImagensEntrada = getListaImagens(DIRECT_ENTRADA, 0);
			dateFim = new Date();
			dateFim.setTime(dateFim.getTime()-dateIni.getTime());
			System.out.println(listaImagensEntrada.size() +" imagens de entrada carregadas em "+ (dateFim.getTime()/1000) +" segundos.\n");
			
			Collections.shuffle(listaImagensEntrada);
			listaImagensEntrada = new ArrayList<Imagem>(listaImagensEntrada.subList(0, 10));
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

				imgTemp = AlgoritmosPreProc.clear(imagem, pp);
				
				if(imgTemp == null){
					continue;
				}
				
				imgTemp.gravar();

				regioesCandidatas = s.getRegioesCandidatas3(pp, imagem, imgTemp, 2);//0.25
//				for (Imagem candidata : regioesCandidatas) {
//					candidata.gravar();
//				}
				
				// MANUAL
				imgTemp = s.getPlaca(regioesCandidatas);
				
				// KNN
//				imgTemp = KNN.run(listaImagensEntrada, regioesCandidatas);
				
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
}  
