package ia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import model.Imagem;
import utils.ArffUtil;

public class MyKNN {
	private static final int K = 5;
	private static ArrayList<Imagem> basePlacas;
	
	public static Imagem run(ArrayList<Imagem> imagensEntrada, ArrayList<Imagem> regioesCandidatas){
		try {
			if(regioesCandidatas != null && !regioesCandidatas.isEmpty()){				
				if(basePlacas == null || basePlacas.isEmpty()){
					basePlacas = ArffUtil.lerARFF("baseVetor");
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
}
