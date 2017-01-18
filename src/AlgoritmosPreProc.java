import org.opencv.core.Core;

// outro metodo: https://repositorio.ufba.br/ri/bitstream/ri/20966/1/mono_tiagoaraujo_bsi_2016.1%5BLAPV%5D.pdf		
public class AlgoritmosPreProc {
	
	// (185 de 383) (32 de 50)
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
		
		imagem.gravar();
		return imagem;
	}
	
	// MELHOR (37 de 50)
	public static Imagem clear(Imagem imagem, PreProcessamento pp){
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
