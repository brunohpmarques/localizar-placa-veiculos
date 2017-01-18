package ia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.activation.MimetypesFileTypeMap;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import model.Imagem;
import model.PreProcessamento;

public class KNN {
	private static final String DIRECT_PROJECT = System.getProperty("user.dir");
	private static final String diretorioBasePlacas = DIRECT_PROJECT+"/basePlacas";
	private static final String ls = System.lineSeparator();
	
	public static void run(ArrayList<Imagem> listaImagens){
		try {
			Extractor.gerarHistogramaARFF("base");
			ArrayList<Imagem> basePlacas = Extractor.lerHistogramaARFF("base");
			System.out.println("Base de placas com "+basePlacas.size()+" histogramas");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static class Extractor{
		
		private static String getFileExtension(File file) {
		    String name = file.getName();
		    try {
		        return "."+ name.substring(name.lastIndexOf(".") + 1);
		    } catch (Exception e) {
		        return "";
		    }
		}
		
		private static ArrayList<Imagem> lerBasePlacas(String diretorio) throws FileNotFoundException{
			File arquivos = new File(diretorio);
			
			if(!arquivos.isDirectory()){
				throw new FileNotFoundException(diretorio);
			}
			File[] arrayArquivos = arquivos.listFiles();
			
			if(arrayArquivos == null){
				throw new FileNotFoundException(diretorio);
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
						matriz = Imgcodecs.imread(arquivo.getAbsolutePath(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
						listaImagens.add(new Imagem(mimetype, type, arquivo.getAbsolutePath(), matriz));
					}		
				}
			}
			return listaImagens;
		}

		public static void gerarHistogramaARFF(String nomeArquivo) throws FileNotFoundException{
			ArrayList<Imagem> listaImagens = lerBasePlacas(diretorioBasePlacas);
			int hist[];
			String texto = 
					  "% DETECCAO DE PLACAS VEICULARES"+ls
					+ "% BRUNO MARQUES"+ls
					+ "% DANNY QUEIROZ"+ls
					+ "% PROCESSAMENTO DE IMAGENS 2016.2 - UFRPE"+ls+ls
					+ "@RELATION deteccaoDePlacasVeiculares"+ls+ls;
			
			for (int i = 0; i <= 255; i++) {
				texto += "@ATTRIBUTE cor-"+i+" NUMERIC"+ls;
			}
			texto += "@ATTRIBUTE nome STRING"+ls;
			texto += ls+"@DATA"+ls;
			
			String temp;
			for (Imagem imagem : listaImagens) {
				temp = "";
				hist = PreProcessamento.getHistograma(imagem);
				for (int i = 0; i < hist.length; i++) {
					temp += hist[i]+",";
				}
				texto += temp + imagem.getNome() + ls;
			}
			
			gravarArquivo(diretorioBasePlacas+"/"+nomeArquivo+".arff", texto, false);
		}
		
		public static ArrayList<Imagem> lerHistogramaARFF(String nomeArquivo) throws IOException{
			ArrayList<Imagem> lHist = new ArrayList<>();
			File arquivo = new File(diretorioBasePlacas+"/"+nomeArquivo+".arff");
			StringBuilder retorno = new StringBuilder();
			BufferedReader conteudo = new BufferedReader(new FileReader(arquivo));
			int offset = 266; // numero de linhas que nao importam
			String arrays[];
			int arrayi[];
		    while (conteudo.ready()) {
		    	if(offset > 0){
		    		offset--;
		    		conteudo.readLine();
		    		continue;
		    	}
		    	arrays = conteudo.readLine().trim().replace(ls, "").split(",");
		    	if(arrays.length == 257){ // 256 cores e o nome da placa
			    	arrayi = new int[256];
			    	for (int i=0; i<arrayi.length; i++) {
			    		if(arrays[i].isEmpty()) continue;
			    		arrayi[i] = Integer.parseInt(arrays[i]);
					}
			    	lHist.add(new Imagem(arrays[arrays.length-1], arrayi));
		    	}
		    }
		    return lHist;
		}
		
		private static void gravarArquivo(String caminho, String conteudo, boolean append) {
	        File arquivo = new File(caminho);
	        try {
	            FileWriter grava = new FileWriter(arquivo, append);
	            PrintWriter escreve = new PrintWriter(grava);
	            escreve.println(conteudo);
	            escreve.close();
	            grava.close();
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }
		
	}
	
	

}
