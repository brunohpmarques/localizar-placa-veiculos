import org.opencv.core.Core;

import model.Imagem;
import utils.PreProcessamento;
import utils.PreProcessamento.Orientacao;

public class AlgoritmosPreProc {
	
	// Ent		Segm	Acer	Erro	NaoEnc
	// 382	->	382		90		292		0
	public static Imagem advanced(Imagem imagem){
		Imagem imgTemp1 = null, imgTemp2 = null;
				
		imagem = PreProcessamento.paraTonsDeCinza(imagem);
		imagem = PreProcessamento.normalizar(imagem);
		imagem = PreProcessamento.filtroNitidez(imagem, 7, 0.75, -0.5, 0);
		imagem = PreProcessamento.filtroGaussiano(imagem, 3, -3);
		imagem = PreProcessamento.normalizar(imagem);
		imagem = PreProcessamento.filtroAutoCanny(imagem, 0);
		imgTemp1 = PreProcessamento.morfoFechamentoOrientacao(imagem, Orientacao.HORIZONTAL, 30);
		imgTemp2 = PreProcessamento.morfoFechamentoOrientacao(imagem, Orientacao.VERTICAL, 30);
		imagem = PreProcessamento.intersecao(imgTemp1, imgTemp2);
		imagem = PreProcessamento.morfoErosao(imagem, 3);
		imagem = PreProcessamento.morfoDilatacaoOrientacao(imagem, Orientacao.HORIZONTAL, 30);
		imagem = PreProcessamento.morfoDilatacao(imagem, 9);
		return imagem;
	}
	
	// MELHOR
	// Ent		Segm	Acer	Erro	NaoEnc
	// 382	->	365		232		133		17
	public static Imagem clear(Imagem imagem){
		Imagem imgTemp1 = null, imgTemp2 = null;
				
		imagem = PreProcessamento.paraTonsDeCinza(imagem);
		imagem = PreProcessamento.normalizar(imagem);
		imagem = PreProcessamento.ajustarBrilho(imagem, 0.30, 0.30);
		imagem = PreProcessamento.filtroMediana(imagem, 5);
		
		imagem = PreProcessamento.paraPretoEBrancoLocal(imagem, 9, 5);//15, 5
		Core.bitwise_not(imagem.getMatriz(), imagem.getMatriz());
		
		imgTemp1 = PreProcessamento.morfoFechamentoOrientacao(imagem, Orientacao.HORIZONTAL, 30);
		imgTemp2 = PreProcessamento.morfoFechamentoOrientacao(imagem, Orientacao.VERTICAL, 15);
		imagem = PreProcessamento.intersecao(imgTemp1, imgTemp2);
		
		imagem = PreProcessamento.morfoErosao(imagem, 3);
		imagem = PreProcessamento.morfoDilatacaoOrientacao(imagem, Orientacao.HORIZONTAL, 30);
		imagem = PreProcessamento.morfoDilatacao(imagem, 9);
		return imagem;
	}
	
	public static Imagem pcc(Imagem imagem){
		imagem = PreProcessamento.paraTonsDeCinza(imagem);
		imagem = PreProcessamento.normalizar(imagem);
				
		imagem = PreProcessamento.filtroMediana(imagem, 5);
		imagem = PreProcessamento.filtroNitidez(imagem, 0.1, 1, 1, -75);
		
		imagem = PreProcessamento.paraPretoEBrancoLocal(imagem, 21, 25);
		Core.bitwise_not(imagem.getMatriz(), imagem.getMatriz());
		return imagem;
	}

	public static class Existentes{
		
		//http://www.prp.rei.unicamp.br/pibic/congressos/xviicongresso/paineis/059834.pdf
		// Ent		Segm	Acer	Erro	NaoEnc
		// 382	->	351		130		221		31
		public static Imagem unicampCristiane(Imagem imagem){
			imagem = PreProcessamento.paraTonsDeCinza(imagem);
			imagem = PreProcessamento.paraPretoEBrancoGlobal(imagem, 128);
			return imagem;
		}
		
		//http://www.lbd.dcc.ufmg.br/colecoes/wvc/2006/0065.pdf
		// Ent		Segm	Acer	Erro	NaoEnc
		// 382	->	95		14		81		287		
		public static Imagem ufmgDiegoEAndres(Imagem imagem){
			imagem = PreProcessamento.paraTonsDeCinza(imagem);
			imagem = PreProcessamento.filtroSobel(imagem, Orientacao.VERTICAL);
			imagem = PreProcessamento.paraPretoEBrancoOTSU(imagem);
			return imagem;
		}
		
		//http://www.lbd.dcc.ufmg.br/colecoes/wvc/2010/0047.pdf
		// Ent		Segm	Acer	Erro	NaoEnc	
		// 382	->	382		100		282		0		
		public static Imagem ufmgViniciusELucasEAparecido(Imagem imagem){
			imagem = PreProcessamento.paraTonsDeCinza(imagem);
			imagem = PreProcessamento.filtroMediana(imagem, 3);
			imagem = PreProcessamento.filtroAutoCanny(imagem, 0);
			return imagem;
		}
		
	}
	
}
