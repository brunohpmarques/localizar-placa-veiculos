import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.activation.MimetypesFileTypeMap;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

// http://www.w3ii.com/pt/java_dip/default.html

public class Main {
	
	private static final String DIRECT_PROJECT = System.getProperty("user.dir");
	private static final String DIRECT_ENTRADA = DIRECT_PROJECT +"\\entrada";
	private static final String DIRECT_PREPROCESSAMENTO = DIRECT_PROJECT +"\\preprocessamento";
	private static final String DIRECT_SEGMENTACAO = DIRECT_PROJECT +"\\segmentacao";
	
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
		}
	}
	
	public static void main(String[] args) {  
		try{
			System.out.println("Carregando imagens de entrada."); 
			ArrayList<Imagem> listaImagensEntrada = getListaImagens(DIRECT_ENTRADA);
			System.out.println(listaImagensEntrada.size() +" imagens de entrada carregadas."); 
			
			System.out.println("Iniciando pre-processamento."); 
			PreProcessamento pp = new PreProcessamento(DIRECT_PREPROCESSAMENTO);
			
			// EXEMPLO
			Imagem imgTemp = pp.paraTonsDeCinza(listaImagensEntrada.get(2));
			pp.paraPretoEBrancoGlobal(imgTemp, 127).gravar();
			pp.paraPretoEBrancoLocal(imgTemp, 15, 40).gravar();
			
			System.out.println("Fim do pre-processamento."); 
			
			System.out.println("Fim com sucesso.");
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fim com erro.");
		}
	}
	
	/** Instancia lista com todas as imagens de um diretorio **/
	public static ArrayList<Imagem> getListaImagens(String diretorio) throws FileNotFoundException{
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
					matriz = Imgcodecs.imread(arquivo.getAbsolutePath(), Imgcodecs.CV_LOAD_IMAGE_COLOR);
					listaImagens.add(new Imagem(mimetype, type, arquivo.getAbsolutePath(), matriz));
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
