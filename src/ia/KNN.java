package ia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.activation.MimetypesFileTypeMap;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import model.Imagem;
import model.PreProcessamento;
import model.Segmentacao;

public class KNN {
	private static final String DIRECT_PROJECT = System.getProperty("user.dir");
	private static final String diretorioBasePlacas = DIRECT_PROJECT+"/basePlacas";
	private static final String ls = System.lineSeparator();
	private static final int K = 5;
	private static ArrayList<Imagem> basePlacas;
	
	public static Imagem run(ArrayList<Imagem> imagensEntrada, ArrayList<Imagem> regioesCandidatas){
		try {
			if(regioesCandidatas != null && !regioesCandidatas.isEmpty()){				
				if(basePlacas == null || basePlacas.isEmpty()){
					basePlacas = Extractor.lerVetorARFF("baseVetor");
				}
				System.out.println("Base de placas com "+basePlacas.size()+" histogramas carregada");
				
				Date dateIni = new Date();
				Date dateFim;
				System.out.println("CALCULANDO DISTANCIA AS: "+ dateIni.toString());
				
				List<Imagem> vizinhos = new ArrayList<Imagem>();
				Imagem iMaior = null;
				int cont = 0;
				for (int i = 0; i < basePlacas.size(); i++) {
					if(!imagensEntrada.contains(basePlacas.get(i))){ // garante que nao vai comparar com a base de teste
						
						for (Imagem imagem : regioesCandidatas) {
							imagem.setDistancia(Distancia.euclidianaVetor(imagem, basePlacas.get(i)));
						}
						
						iMaior = regioesCandidatas.get(0);
						for (int j = 1; j < regioesCandidatas.size(); j++) {
							if(regioesCandidatas.get(j).getDistancia() < iMaior.getDistancia()){
								iMaior = regioesCandidatas.get(j);
							}
						}
						vizinhos.add(iMaior.clone());
						
					}
					if(++cont % 50 == 0){
						System.out.println(i +" calculados");
					}
				}
				
				Collections.sort(vizinhos, new Comparator<Imagem>() {
				        @Override
				        public int compare(Imagem i1, Imagem i2){
				        	if(i1.getDistancia() > i1.getDistancia()) return 1;
				        	if(i1.getDistancia() < i1.getDistancia()) return -1;
				        	return 0;
				        }
				    });
				
				vizinhos = vizinhos.subList(0, K-1);
				
				HashMap<Imagem, Integer> map = new HashMap<>();
				for (int i = 0; i < vizinhos.size(); i++) {
					if(map.containsKey(vizinhos.get(i))){
						map.put(vizinhos.get(i), map.get(vizinhos.get(i)) + 1);
					}else{
						map.put(vizinhos.get(i), 1);
					}
				}
				
				int temp = -1;
				for (Entry<Imagem, Integer> imagem : map.entrySet()) {
					if(temp == -1 || imagem.getValue() < temp){
						iMaior = imagem.getKey();
					}
				}
				
				dateFim = new Date();
				System.out.println("TERMINOU DE CALCULAR AS: "+ dateFim.toString());
				dateFim.setTime(dateFim.getTime()-dateIni.getTime());
				System.out.println("DURACAO: "+ dateFim.getTime()/1000 +" SEGUNDOS\n");
				
				return iMaior;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static class Distancia{
		
		public static double euclidianaHist(Imagem i1, Imagem i2){
			double soma = 0.0;
			int ih1[] = i1.getHistograma();
			int ih2[] = i2.getHistograma();
			for (int i = 0; i < ih1.length; i++) {
				soma += Math.pow((ih1[i] - ih2[i]), 2);
			}
			return (double)Math.sqrt(soma);
		}
		
		public static double manhattanHist(Imagem i1, Imagem i2){
			int soma = 0;
			int ih1[] = i1.getHistograma();
			int ih2[] = i2.getHistograma();
			for (int i = 0; i < ih1.length; i++) {
				soma += Math.abs((ih1[i] - ih2[i]));
			}
			return soma;
		}
		
		public static double euclidianaVetor(Imagem i1, Imagem i2){
			double soma = 0.0;
			soma += Math.pow((i1.getAspect() - i2.getAspect()), 2);
			soma += Math.pow((i1.getNorm() - i2.getNorm()), 2);
			soma += Math.pow((i1.getMean() - i2.getMean()), 2);
			soma += Math.pow((i1.getSum() - i2.getSum()), 2);
			soma += Math.pow((i1.getTrace() - i2.getTrace()), 2);
			soma += Math.pow((i1.getQuantidadePixelsClaros() - i2.getQuantidadePixelsClaros()), 2);
			soma += Math.pow((i1.getQuantidadePixelsEscuros() - i2.getQuantidadePixelsEscuros()), 2);
			return (double)Math.sqrt(soma);
		}
	
		public static double manhattanVetor(Imagem i1, Imagem i2){
			int soma = 0;
			soma += Math.abs((i1.getAspect() - i2.getAspect()));
			soma += Math.abs((i1.getNorm() - i2.getNorm()));
			soma += Math.abs((i1.getMean() - i2.getMean()));
			soma += Math.abs((i1.getSum() - i2.getSum()));
			soma += Math.abs((i1.getTrace() - i2.getTrace()));
			soma += Math.abs((i1.getQuantidadePixelsClaros() - i2.getQuantidadePixelsClaros()));
			soma += Math.abs((i1.getQuantidadePixelsEscuros() - i2.getQuantidadePixelsEscuros()));
			return soma;
		}
	
	}
	
	public static class Extractor{
		
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
			
			Date dateIni = new Date();
			Date dateFim;
			System.out.println("LENDO IMAGENS DA BASE AS: "+ dateIni.toString());

			String mimetype;
	        String type;
	        Mat matriz;
	        int cont = 0;
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
				if(++cont % 50 == 0){
					System.out.println(cont +" lidos");
				}
			}
			
			dateFim = new Date();
			System.out.println("TERMINOU DE LER AS IMAGENS DA BASE AS: "+ dateFim.toString());
			dateFim.setTime(dateFim.getTime()-dateIni.getTime());
			System.out.println("DURACAO: "+ dateFim.getTime()/1000 +" SEGUNDOS\n");
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
			Date dateIni = new Date();
			Date dateFim;
			System.out.println("LENDO BASE ARFF AS: "+ dateIni.toString());
			
			ArrayList<Imagem> lHist = new ArrayList<>();
			File arquivo = new File(diretorioBasePlacas+"/"+nomeArquivo+".arff");
			BufferedReader conteudo = new BufferedReader(new FileReader(arquivo));
			int offset = 266; // numero de linhas que nao importam
			String arrays[];
			int arrayi[];
			
			int cont = 0;
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
		    	}else{
		    		System.err.println("Linha "+ cont +" com tamanho "+ arrays.length +" "+arrays[0]);
		    	}
		    	
		    	if(++cont %50 == 0){
		    		System.out.println(cont +" lidos");
		    	}
		    }
		    
		    dateFim = new Date();
			System.out.println("TERMINOU DE LER A BASE AS: "+ dateFim.toString());
			dateFim.setTime(dateFim.getTime()-dateIni.getTime());
			System.out.println("DURACAO: "+ dateFim.getTime()/1000 +" SEGUNDOS\n");
		    return lHist;
		}
		
		public static void gerarVetorARFF(String nomeArquivo) throws FileNotFoundException{
			ArrayList<Imagem> listaImagens = lerBasePlacas(diretorioBasePlacas);
			int hist[];
			String texto = 
					  "% DETECCAO DE PLACAS VEICULARES"+ls
					+ "% BRUNO MARQUES"+ls
					+ "% DANNY QUEIROZ"+ls
					+ "% PROCESSAMENTO DE IMAGENS 2016.2 - UFRPE"+ls+ls
					+ "@RELATION deteccaoDePlacasVeiculares"+ls+ls;
			
			texto += "@ATTRIBUTE aspect NUMERIC"+ls;
			texto += "@ATTRIBUTE norm NUMERIC"+ls;
			texto += "@ATTRIBUTE mean NUMERIC"+ls;
			texto += "@ATTRIBUTE sum NUMERIC"+ls;
			texto += "@ATTRIBUTE trace NUMERIC"+ls;
			texto += "@ATTRIBUTE whitePixels NUMERIC"+ls;
			texto += "@ATTRIBUTE blackPixels NUMERIC"+ls;
			
			texto += "@ATTRIBUTE nome STRING"+ls;
			texto += ls+"@DATA"+ls;
			
			String temp;
			for (Imagem imagem : listaImagens) {
				temp = (imagem.getMatriz().width() / imagem.getMatriz().height())+",";
				temp += PreProcessamento.getNorm(imagem)+",";
				temp += PreProcessamento.getMean(imagem).val[0]+",";
				temp += PreProcessamento.getSum(imagem).val[0]+",";
				temp += PreProcessamento.getTrace(imagem).val[0]+",";
				temp += Segmentacao.getQuantidadePixelsClaros(imagem.getMatriz())+",";
				temp += Segmentacao.getQuantidadePixelsEscuros(imagem.getMatriz())+",";
				
				texto += temp + imagem.getNome() + ls;
			}
			
			gravarArquivo(diretorioBasePlacas+"/"+nomeArquivo+".arff", texto, false);
		}
		
		public static ArrayList<Imagem> lerVetorARFF(String nomeArquivo) throws IOException{
			Date dateIni = new Date();
			Date dateFim;
			System.out.println("LENDO BASE ARFF AS: "+ dateIni.toString());
			
			ArrayList<Imagem> lVetor = new ArrayList<>();
			File arquivo = new File(diretorioBasePlacas+"/"+nomeArquivo+".arff");
			BufferedReader conteudo = new BufferedReader(new FileReader(arquivo));
			int offset = 17; // numero de linhas que nao importam
			String arrays[], arrayi[];
			double mean, sum, trace;
			
			int cont = 0;
		    while (conteudo.ready()) {
		    	if(offset > 0){
		    		offset--;
		    		conteudo.readLine();
		    		continue;
		    	}
		    	arrays = conteudo.readLine().trim().replace(ls, "").split(",");
		    	
		    	if(arrays.length == 8){ // 7 valores e o nome da placa
		    		
		    		arrayi = arrays[2].split("#");		    		
		    		mean = Double.parseDouble(arrayi[0]);
		    		
		    		arrayi = arrays[3].split("#");
		    		sum = Double.parseDouble(arrayi[0]);
		    		
		    		arrayi = arrays[4].split("#");
		    		trace = Double.parseDouble(arrayi[0]);
		    		
			    	lVetor.add(new Imagem(
			    			arrays[7],
			    			Double.parseDouble(arrays[0]+""),
			    			Double.parseDouble(arrays[1]+""), 
			    			mean,
			    			sum,
			    			trace,
			    			Float.parseFloat(arrays[5]+""),
			    			Float.parseFloat(arrays[6]+"")
			    			));
		    	}else{
		    		System.err.println("Linha "+ cont +" com tamanho "+ arrays.length +" "+arrays[0]);
		    	}
		    	
		    	if(++cont %50 == 0){
		    		System.out.println(cont +" lidos");
		    	}
		    }
		    
		    dateFim = new Date();
			System.out.println("TERMINOU DE LER A BASE AS: "+ dateFim.toString());
			dateFim.setTime(dateFim.getTime()-dateIni.getTime());
			System.out.println("DURACAO: "+ dateFim.getTime()/1000 +" SEGUNDOS\n");
		    return lVetor;
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
