package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.opencv.core.Mat;

import model.Imagem;

public class ArffUtil {
	
	public static void gerarARFF(String nomeArquivo) throws FileNotFoundException{
		Date dateIni = new Date();
		Date dateFim;
		System.out.println("GERANDO BASE ARFF AS: "+ dateIni.toString());
		
		int max = 300;
		String texto = 
				  "% DETECCAO DE PLACAS VEICULARES"+ConstantesUtil.ls
				+ "% BRUNO MARQUES"+ConstantesUtil.ls
				+ "% DANNY QUEIROZ"+ConstantesUtil.ls
				+ "% PROCESSAMENTO DE IMAGENS 2016.2 - UFRPE"+ConstantesUtil.ls+ConstantesUtil.ls
				+ "@RELATION deteccaoDePlacasVeiculares"+ConstantesUtil.ls+ConstantesUtil.ls;

    	texto += "@ATTRIBUTE width NUMERIC"+ConstantesUtil.ls;
		texto += "@ATTRIBUTE heigth NUMERIC"+ConstantesUtil.ls;
    	texto += "@ATTRIBUTE aspect NUMERIC"+ConstantesUtil.ls;
    	texto += "@ATTRIBUTE hcorner NUMERIC"+ConstantesUtil.ls;
		texto += "@ATTRIBUTE norm NUMERIC"+ConstantesUtil.ls;
		texto += "@ATTRIBUTE mean NUMERIC"+ConstantesUtil.ls;
		texto += "@ATTRIBUTE sum NUMERIC"+ConstantesUtil.ls;
		texto += "@ATTRIBUTE trace NUMERIC"+ConstantesUtil.ls;
		texto += "@ATTRIBUTE kmeans NUMERIC"+ConstantesUtil.ls;
		texto += "@ATTRIBUTE pixelsClaros NUMERIC"+ConstantesUtil.ls;
		texto += "@ATTRIBUTE pixelsEscuros NUMERIC"+ConstantesUtil.ls;
		texto += "@ATTRIBUTE compInternos NUMERIC"+ConstantesUtil.ls;
		texto += "@ATTRIBUTE nome STRING"+ConstantesUtil.ls;
		texto += "@ATTRIBUTE class {0,1}"+ConstantesUtil.ls;
		texto += ConstantesUtil.ls+"@DATA"+ConstantesUtil.ls;
		
		Mat mat;
		String temp = ConstantesUtil.EMPTY;
		String fname;
		ArrayList<Imagem> imagensData = new ArrayList<>();
		
		ArrayList<Imagem> listaImagensP = FileUtil.getListaImagens(ConstantesUtil.PATH_DATA_POSITIVE, max);
		for (Imagem imagem : listaImagensP) {
			imagem.setNome("1_"+imagem.getNome());
		}
		imagensData.addAll(listaImagensP);
		
		ArrayList<Imagem> listaImagensN = FileUtil.getListaImagens(ConstantesUtil.PATH_DATA_POSITIVE, max);
		for (Imagem imagem : listaImagensN) {
			imagem.setNome("0_"+imagem.getNome());
		}
		imagensData.addAll(listaImagensN);
		System.out.println(imagensData.size());
		
		Collections.shuffle(imagensData);
		
		for (Imagem imagem : imagensData) {
			imagem = PreProcessamento.paraTonsDeCinza(imagem);
			mat = imagem.getMatriz();
			fname = imagem.getNome();
			
			temp = mat.width()+ConstantesUtil.COMMA;
			temp += mat.height()+ConstantesUtil.COMMA;
			temp += (mat.width()/mat.height())+ConstantesUtil.COMMA;
			temp += Descritores.getCntHarrisCorner(mat, 128)+ConstantesUtil.COMMA;
			temp += (float) Descritores.getNorm(imagem)+ConstantesUtil.COMMA;
			temp += (float) Descritores.getMean(imagem).val[0]+ConstantesUtil.COMMA;
			temp += (float) Descritores.getSum(imagem).val[0]+ConstantesUtil.COMMA;
			temp += (float) Descritores.getTrace(imagem).val[0]+ConstantesUtil.COMMA;
			temp += (float) Descritores.getKMeans(imagem, 4)+ConstantesUtil.COMMA;
			temp += Descritores.getQuantidadePixelsClaros(mat, Descritores.LIMIAR_COR)+ConstantesUtil.COMMA;
			temp += Descritores.getQuantidadePixelsEscuros(mat, Descritores.LIMIAR_COR)+ConstantesUtil.COMMA;
			temp += Descritores.getQuantidadesComponentesInternos(imagem)+ConstantesUtil.COMMA;
			temp += fname.substring(2, fname.length())+ConstantesUtil.COMMA;
			temp += fname.charAt(0)+ConstantesUtil.ls;
			texto += temp;
		}
		
		FileUtil.gravarArquivo(ConstantesUtil.PATH_DATA+nomeArquivo+".arff", texto, false);
		
		dateFim = new Date();
		System.out.println("TERMINOU DE GRAVAR A BASE AS: "+ dateFim.toString());
		dateFim.setTime(dateFim.getTime()-dateIni.getTime());
		System.out.println("DURACAO: "+ dateFim.getTime()/1000 +" SEGUNDOS\n");
	}

	public static ArrayList<Imagem> lerARFF(String nomeArquivo) throws IOException{
		Date dateIni = new Date();
		Date dateFim;
		System.out.println("LENDO BASE ARFF AS: "+ dateIni.toString());
		
		ArrayList<Imagem> lVetor = new ArrayList<>();
		File arquivo = new File(ConstantesUtil.PATH_INPUT+nomeArquivo+".arff");
		BufferedReader conteudo = new BufferedReader(new FileReader(arquivo));
		int offset = 23; // numero de linhas que nao importam
		String arrays[];
		Imagem imagem;
		
		int cont = 0;
	    while (conteudo.ready()) {
	    	if(offset > 0){
	    		offset--;
	    		conteudo.readLine();
	    		continue;
	    	}
	    	arrays = conteudo.readLine().trim().replace(ConstantesUtil.ls, "").split(",");
	    	
	    	if(arrays.length == 14){ // 12 valores, o nome da placa e a classe
	    		//TODO
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
}
