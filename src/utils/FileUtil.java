package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import model.Imagem;

public class FileUtil {
	private static final List<String> FILE_TYPES;
	
	static {
		FILE_TYPES = new ArrayList<>();
		FILE_TYPES.add(".jpeg");
		FILE_TYPES.add(".jpg");
		FILE_TYPES.add(".png");
	}
	
	/** Recupera extensao de um arquivo **/
	public static String getFileExtension(File file) {
	    String name = file.getName();
	    try {
	        return "."+ name.substring(name.lastIndexOf(".") + 1);
	    } catch (Exception e) {
	        return "";
	    }
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
		
		//if(arrayArquivos.length*0.66 < max){
		//	throw new NumberFormatException("Numero maximo: "+((int)arrayArquivos.length*0.66));
		//}
		
		ArrayList<Imagem> listaImagens = new ArrayList<>();

		String mimetype;
        String type;
        Mat matriz;
		for (File arquivo : arrayArquivos) {
			if(arquivo.isFile() && arquivo.canRead()){
				mimetype = new MimetypesFileTypeMap().getContentType(arquivo);
				//type = mimetype.split("/")[0];
				type = getFileExtension(arquivo);
				if(FILE_TYPES.contains(type.toLowerCase())){
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
	
	public static void gravarArquivo(String caminho, String conteudo, boolean append) {
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
	
	//renomearImagens("D:\\Documentos\\python\\DLLPD\\base\\placas\\", "placas-", ".jpg");
    public static void renomearImagens(String diretorio, String prefixo, String formato) throws FileNotFoundException{
		File arquivos = new File(diretorio);
		
		if(!arquivos.isDirectory()){
			throw new FileNotFoundException(diretorio);
		}
		File[] arrayArquivos = arquivos.listFiles();
		
		if(arrayArquivos == null){
			throw new FileNotFoundException(diretorio);
		}
        int count = 1;
		for (File arquivo : arrayArquivos) {
			if(arquivo.isFile() && arquivo.canRead()){
				arquivo.renameTo(new File(diretorio+prefixo+count+formato));
				count++;
			}
		}
	}
}
