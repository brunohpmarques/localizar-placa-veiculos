import org.opencv.core.Core;

import model.Imagem;
import model.PreProcessamento;

public class AlgoritmosPreProc {
	
	// Ent		Segm	Acer	Erro	NaoEnc
	// 382	->	382		90		292		0
	public static Imagem advanced(Imagem imagem, PreProcessamento pp){
		Imagem imgTemp1 = null, imgTemp2 = null;
				
		imagem = pp.paraTonsDeCinza(imagem);
		imagem = pp.normalizar(imagem);
		imagem = pp.filtroNitidez(imagem, 7, 0.75, -0.5, 0);
		imagem = pp.filtroGaussiano(imagem, 3, -3);
		imagem = pp.normalizar(imagem);
		imagem = pp.filtroAutoCanny(imagem, 0);
		imgTemp1 = pp.morfoFechamentoOrientacao(imagem, PreProcessamento.HORIZONTAL, 30);
		imgTemp2 = pp.morfoFechamentoOrientacao(imagem, PreProcessamento.VERTICAL, 30);
		imagem = pp.intersecao(imgTemp1, imgTemp2);
		imagem = pp.morfoErosao(imagem, 3);
		imagem = pp.morfoDilatacaoOrientacao(imagem, PreProcessamento.HORIZONTAL, 30);
		imagem = pp.morfoDilatacao(imagem, 9);
		return imagem;
	}
	
	// MELHOR
	// Ent		Segm	Acer	Erro	NaoEnc
	// 382	->	365		232		133		17
	public static Imagem clear(Imagem imagem, PreProcessamento pp){
		Imagem imgTemp1 = null, imgTemp2 = null;
				
		imagem = pp.paraTonsDeCinza(imagem);
		imagem = pp.ajustarBrilho(imagem, 0.30, 0.30);
		imagem = pp.filtroMediana(imagem, 5);
		
		imagem = pp.paraPretoEBrancoLocal(imagem, 9, 5);//15, 5
		Core.bitwise_not(imagem.getMatriz(), imagem.getMatriz());
		
		imgTemp1 = pp.morfoFechamentoOrientacao(imagem, PreProcessamento.HORIZONTAL, 30);
		imgTemp2 = pp.morfoFechamentoOrientacao(imagem, PreProcessamento.VERTICAL, 15);
		imagem = pp.intersecao(imgTemp1, imgTemp2);
		
		imagem = pp.morfoErosao(imagem, 3);
		imagem = pp.morfoDilatacaoOrientacao(imagem, PreProcessamento.HORIZONTAL, 30);
		imagem = pp.morfoDilatacao(imagem, 9);
		return imagem;
	}

	public static class Existentes{
		
		//http://www.prp.rei.unicamp.br/pibic/congressos/xviicongresso/paineis/059834.pdf
		// Ent		Segm	Acer	Erro	NaoEnc
		// 382	->	351		130		221		31
		public static Imagem unicampCristiane(Imagem imagem, PreProcessamento pp){
			imagem = pp.paraTonsDeCinza(imagem);
			imagem = pp.paraPretoEBrancoGlobal(imagem, 128);
			return imagem;
		}
		
		//http://www.lbd.dcc.ufmg.br/colecoes/wvc/2006/0065.pdf
		// Ent		Segm	Acer	Erro	NaoEnc
		// 382	->	95		14		81		287		
		public static Imagem ufmgDiegoEAndres(Imagem imagem, PreProcessamento pp){
			imagem = pp.paraTonsDeCinza(imagem);
			imagem = pp.filtroSobel(imagem, PreProcessamento.VERTICAL);
			imagem = pp.paraPretoEBrancoOTSU(imagem);
			return imagem;
		}
		
		//http://www.lbd.dcc.ufmg.br/colecoes/wvc/2010/0047.pdf
		// Ent		Segm	Acer	Erro	NaoEnc	
		// 382	->	382		100		282		0		
		public static Imagem ufmgViniciusELucasEAparecido(Imagem imagem, PreProcessamento pp){
			imagem = pp.paraTonsDeCinza(imagem);
			imagem = pp.filtroMediana(imagem, 3);
			imagem = pp.filtroAutoCanny(imagem, 0);
			return imagem;
		}
		
	}
	
}
