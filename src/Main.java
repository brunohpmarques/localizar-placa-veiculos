import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.activation.MimetypesFileTypeMap;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

// tutorial http://www.w3ii.com/pt/java_dip/default.html

/*
http://stackoverflow.com/questions/37302098/image-preprocessing-with-opencv-before-doing-character-recognition-tesseract
1. Convert to Grayscale.
2. Gaussian Blur with 3x3 or 5x5 filter.
3. Apply Sobel Filter to find vertical edges.
	Sobel(gray, dst, -1, 1, 0)
4. Threshold the resultant image to get a binary image.
5. Apply a morphological close operation using suitable structuring element.
6. Find contours of the resulting image.
7. Find minAreaRect of each contour. Select rectangles based on aspect ratio and minimum and maximum area.
8. For each selected contour, find edge density. Set a threshold for edge density and choose the rectangles breaching that threshold as possible plate regions.
9. Few rectangles will remain after this. You can filter them based on orientation or any criteria you deem suitable.
10. Clip these detected rectangular portions from the image after adaptiveThreshold and apply OCR.
*/

/*
http://stackoverflow.com/questions/20276209/find-the-plate-rectangle-in-a-given-picture
1. You will want to perform a smooth/blur of some kind before thresholding the image, in order to eliminate unwanted noise;
2. Apply threshold to the image like you are doing now;
3. Use the openCv library's dilate function to slightly dilate your detected edges. This is useful because once the license 
   plate's characters are dilated, they will sort of fill the rectangle in which they are contained;
4. openCV has a function called cvRectangle that searches for rectanglular shapes in the image. There's plenty of documenation 
   online to assist you in using it;
5. Finally, you'll want to filter the rectangular shapes found that are license plate candidates based on ratio (width / height) 
   and area (width * height). For example, portuguese license plates, at least in my test images, had a ratio between 2 and 3.5 
   and an area of around 5000-7000 pixels. Obviously this depends on the license plate's shape, the image size, etc..
*/

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
		}else{
			fs.delete();
			fs.mkdirs();
		}
	}
	
	public static void main(String[] args) { 
		Date dateIni = new Date();
		Date dateTemp;
		Date dateFim;
		System.out.println("INICIOU AS: "+ dateIni.toString() +"\n");
		try{
			
			System.out.println("Carregando imagens de entrada."); 
			ArrayList<Imagem> listaImagensEntrada = getListaImagens(DIRECT_ENTRADA, 50);
//			Collections.shuffle(listaImagensEntrada);
			
			dateFim = new Date();
			dateFim.setTime(dateFim.getTime()-dateIni.getTime());
			System.out.println(listaImagensEntrada.size() +" imagens de entrada carregadas em "+ (dateFim.getTime()/1000) +" segundos.\n");
						
			dateTemp = new Date();
			System.out.println("Iniciando pre-processamento as "+ dateTemp.toString()); 
			PreProcessamento pp = new PreProcessamento(DIRECT_PREPROCESSAMENTO);
			Segmentacao s = new Segmentacao(DIRECT_SEGMENTACAO);
			
			// PRE-PROCESSAMENTO
			Imagem imgTemp;
			ArrayList<Imagem> regioesCandidatas;
			for (Imagem imagem : listaImagensEntrada) {			
				
//        Gaussiano, tons de cinza, binarizar, dilatacao e erosao, canny
//        cvSmooth(orgImg,orgImg,CV_GAUSSIAN,7);
//        cvCvtColor(orgImg,grayImg,CV_BGR2GRAY);
//        cvThreshold(grayImg,grayImg,230,255,CV_THRESH_BINARY_INV);  
//        cvErode(grayImg,grayImg,null,3);
//        cvDilate(grayImg,grayImg,null,2);
				
				// SEGUNDA OPCAO
//				imgTemp = pp.filtroGaussiano(imagem, 7, 0);	
//				imgTemp = pp.paraTonsDeCinza(imgTemp);
//				imgTemp = pp.filtroLaplaciano(imgTemp, PreProcessamento.NEGATIVA);
//				imgTemp = pp.paraPretoEBrancoOTSU(imgTemp);
//				imgTemp = pp.morfoErosao(imgTemp, 3);
//				imgTemp = pp.morfoDilatacao(imgTemp, 2);
//				imgTemp = pp.morfoFechamento(imgTemp, 27, 3);
//				imgTemp = pp.filtroAutoCanny(imgTemp, 0);
				
				// MELHOR
				imgTemp = pp.paraTonsDeCinza(imagem);
				imgTemp = pp.normalizar(imgTemp);			
				imgTemp = pp.filtroGaussiano(imagem, 3, 0);	
				imgTemp = pp.filtroAutoCanny(imgTemp, 0);
				
				imgTemp.gravar();

				regioesCandidatas = s.getRegioesCandidatas3(imagem, imgTemp, 0.5);
				imgTemp = s.getPlaca(regioesCandidatas);
				if(imgTemp == null){
					System.err.println(imagem.getNome() +" é difícil");
				}else{
					imgTemp.gravar();
				}
//				for (Imagem candidata : regioesCandidatas) {
//					candidata.gravar();
//				}
			}
			
			dateFim = new Date();
			dateFim.setTime(dateFim.getTime()-dateIni.getTime());
			System.out.println("Fim do pre-processamento em "+ (dateFim.getTime()/1000) +" segundos.\n"); 
			
			System.out.println("Fim com sucesso.");
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fim com erro.");
		}
		dateFim = new Date();
		System.out.println("\nTERMINOU AS: "+ dateFim.toString());
		dateFim.setTime(dateFim.getTime()-dateIni.getTime());
		System.out.println("DURACAO: "+ dateFim.getTime()/1000 +" SEGUNDOS");
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
					if(max > 0 && listaImagens.size() == max){
						break;
					}
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
