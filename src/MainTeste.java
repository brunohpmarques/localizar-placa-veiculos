import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.imgproc.Imgproc;

public class MainTeste {
	
	//https://repositorio.ufba.br/ri/bitstream/ri/20966/1/mono_tiagoaraujo_bsi_2016.1%5BLAPV%5D.pdf	
	public static void segmentarPorVetores(ArrayList<Imagem> listaImagensEntrada, PreProcessamento pp){	
		ImagemRegiao ir;
		for (Imagem imagem : listaImagensEntrada) {
			imagem = pp.paraTonsDeCinza(imagem);
			ir = new ImagemRegiao(imagem);
			imagem = pp.normalizar(imagem);
			imagem = pp.filtroGaussiano(imagem, 3, -3);
			imagem = pp.filtroSobel(imagem, PreProcessamento.VERTICAL);
			imagem.gravar();
			
			calcVerticalEdges(imagem, ir);
			calcHorizontalEdges(imagem, ir);
			
			break;
		}
		
	}
	
	public static void calcVerticalEdges(Imagem imagem, ImagemRegiao ir){
		double cor[];
		int qntVertical[] = new int[imagem.getMatriz().height()];
		for (int i = 0; i < imagem.getMatriz().height(); i++) {
			qntVertical[i] = 0;
			for (int j = 0; j < imagem.getMatriz().width(); j++) {
				cor = imagem.getMatriz().get(i, j);
				if(cor != null && cor[0] > 0){
					qntVertical[i]++;
				}
			}
			System.out.println(qntVertical[i]);
		}
		ir.setQntVertical(qntVertical);
	}
	
	public static void calcHorizontalEdges(Imagem imagem, ImagemRegiao ir){
		double cor[];
		int qntHorizontal[] = new int[imagem.getMatriz().width()];
		for (int i = 0; i < imagem.getMatriz().width(); i++) {
			qntHorizontal[i] = 0;
			for (int j = 0; j < imagem.getMatriz().height(); j++) {
				cor = imagem.getMatriz().get(i, j);
				if(cor != null && cor[0] > 0){
					qntHorizontal[i]++;
				}
			}
			//System.out.println(qntHorizontal[i]);
		}
		ir.setQntHorizontal(qntHorizontal);
	}
	
	

	//
	public static Imagem clearAlgorithm(Imagem imagem, PreProcessamento pp){
		Imagem imgTemp1 = null, imgTemp2 = null;
				
		imagem = pp.paraTonsDeCinza(imagem);
		imagem = pp.filtroBrilho(imagem, 0.30, 0.30);
		imagem = pp.filtroMediana(imagem, 5);
		
		imagem = pp.paraPretoEBrancoLocal(imagem, 9, 5);//15, 5
		Core.bitwise_not(imagem.getMatriz(), imagem.getMatriz());
		
		imgTemp1 = pp.morfoFechamentoOrientacao(imagem, PreProcessamento.HORIZONTAL, 30);
		imgTemp2 = pp.morfoFechamentoOrientacao(imagem, PreProcessamento.VERTICAL, 15);
		imagem = pp.intersecao(imgTemp1, imgTemp2);
		
		imagem = pp.morfoErosao(imagem, 3);
		imagem = pp.morfoDilatacaoOrientacao(imagem, PreProcessamento.HORIZONTAL, 30);
		imagem = pp.morfoDilatacao(imagem, 9);
		
		imagem.gravar();
		return imagem;
	}
}
