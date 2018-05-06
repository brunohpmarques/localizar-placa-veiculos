package utils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import model.Imagem;

//outro metodo para segmentacao: https://repositorio.ufba.br/ri/bitstream/ri/20966/1/mono_tiagoaraujo_bsi_2016.1%5BLAPV%5D.pdf	
public class Segmentacao {

	private static final String DIRECT_SEGMENTACAO = System.getProperty("user.dir") +"/resultados/segmentacao";

	static{
		File fs = new File(DIRECT_SEGMENTACAO);		
		if(!fs.exists()){
			fs.mkdirs();
		}		
	}
	
	public static ArrayList<Imagem> getRegioesCandidatas(Imagem imagemOriginal, Imagem processada, double margemTamanho){
		ArrayList<Imagem> regioesCandidatas = new ArrayList<Imagem>();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	    Mat hierarchy = new Mat();
	    Imgproc.findContours(processada.getMatriz(), contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
	    List<MatOfPoint> contours_poly = new ArrayList<MatOfPoint>(contours.size());
	    contours_poly.addAll(contours);

	    MatOfPoint2f mMOP2f1,mMOP2f2;
	    mMOP2f1 = new MatOfPoint2f();
	    mMOP2f2 = new MatOfPoint2f();

	    for(int i = 0; i < contours.size(); i++){
	        if (contours.get(i).toList().size() > 50){ // 100 alterado
	            contours.get(i).convertTo(mMOP2f1, CvType.CV_32FC2);
	            mMOP2f2.convertTo(contours_poly.get(i), CvType.CV_32S);
	            Imgproc.approxPolyDP(mMOP2f1, mMOP2f2, 8, true);

	            if(contours_poly.get(i).toList().size() >= 4 && contours_poly.get(i).toList().size() <= 20){//20
	            	
	            	Rect appRect = Imgproc.boundingRect(contours_poly.get(i));	            		
	            	
	            	double razao = appRect.width / appRect.height;
	            	if(razao <= (6 + margemTamanho) && razao >= (3 - margemTamanho)){
	            		Rect roi = new Rect(appRect.x, appRect.y, appRect.width, appRect.height);
		                Mat cropped = new Mat(imagemOriginal.getMatriz(), roi);
		                
		                if(cropped.width() <= 50){
		                	continue;
		                }
		                
//		                Imgproc.drawContours(imagemOriginal.getMatriz(), contours_poly, i, new Scalar(255, 0, 255));
//		                imagemOriginal.setNome(imagemOriginal.getNome()+"_DRAW");
//		                imagemOriginal.gravar();
		                
		                Imagem imgCandidata = new Imagem(imagemOriginal.getNome() +"_cand_"+i, imagemOriginal.getFormato(), DIRECT_SEGMENTACAO, cropped);
		                
		                //////////////////////////////////////////
		                // Segmentacao pelo KNN com vetor de caracteristicas estatisticas
//		                PreProcessamento.getNorm(imgCandidata);
//		                PreProcessamento.getMean(imgCandidata);
//		                PreProcessamento.getSum(imgCandidata);
//		                PreProcessamento.getTrace(imgCandidata);
//		                PreProcessamento.getKMeans(imgCandidata, 4);
		                // TODO entropia, contraste ou variancia
		                //////////////////////////////////////////		
		                
		                //////////////////////////////////////////
		                // Segmentacao pelo KNN com vetor de caracteristicas usando histograma
//		                imgCandidata = PreProcessamento.paraTonsDeCinza(imgCandidata);
//		                imgCandidata.setHistograma(PreProcessamento.getHistograma(imgCandidata));
//		                imgCandidata = PreProcessamento.paraPretoEBrancoOTSU(imgCandidata);
		                //////////////////////////////////////////
		                
		                //////////////////////////////////////////
		                // Segmentacao usada na disciplina de PI pela conta de densidade de pixels claros e escuros e componentes internos
//		                float count = Descritores.getQuantidadePixelsClaros(cropped, Descritores.LIMIAR_COR);
//		                imgCandidata.setQuantidadePixelsClaros(count);
//		                count = Descritores.getQuantidadePixelsEscuros(cropped, Descritores.LIMIAR_COR);
//		                imgCandidata.setQuantidadePixelsEscuros(count);
//		                imgCandidata.setTrace(Descritores.getQuantidadesComponentesInternos(imgCandidata));
		                //////////////////////////////////////////
		                
		                imgCandidata.setCaminho(DIRECT_SEGMENTACAO);
		                regioesCandidatas.add(imgCandidata);
	            	}	
	            }
	        }   
	    }
	    return regioesCandidatas;
	}
	
	// escolher candidata
	public static Imagem getPlaca(ArrayList<Imagem> listaCandidatas){   
		if(listaCandidatas == null || listaCandidatas.isEmpty()){
			return null;
		}
        Imagem max = listaCandidatas.get(0);
        for (int i=1; i<listaCandidatas.size(); i++) {
        	if((listaCandidatas.get(i).getTrace() >= max.getTrace() &&
        		listaCandidatas.get(i).getTrace() >= 5 && listaCandidatas.get(i).getTrace() <= 7) &&
        			listaCandidatas.get(i).getQuantidadePixelsClaros() > listaCandidatas.get(i).getQuantidadePixelsEscuros()
        			&& listaCandidatas.get(i).getQuantidadePixelsClaros() > max.getQuantidadePixelsClaros() 
        			&& listaCandidatas.get(i).getQuantidadePixelsEscuros() > max.getQuantidadePixelsEscuros()){
        		max = listaCandidatas.get(i);
//                System.out.println("Pixels claros para "+ listaCandidatas.get(i).getNome()+": "+listaCandidatas.get(i).getQuantidadePixelsClaros());
//                System.out.println("Pixels escuros para "+ listaCandidatas.get(i).getNome()+": "+listaCandidatas.get(i).getQuantidadePixelsEscuros());
//                System.out.println();
        	}
		}
		return max;
	}
	
}
