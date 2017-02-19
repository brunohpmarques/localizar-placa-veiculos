package model;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

//outro metodo para segmentacao: https://repositorio.ufba.br/ri/bitstream/ri/20966/1/mono_tiagoaraujo_bsi_2016.1%5BLAPV%5D.pdf	
public class Segmentacao {

	private String diretorioSaida;
	private static double margemCor = 20; //20
	private static double minimoCor = 128; //500

	public Segmentacao(String diretorioSaida){
		this.diretorioSaida = diretorioSaida;
	}

	public ArrayList<Imagem> getRegioesCandidatas(PreProcessamento pp, Imagem imagemOriginal, Imagem processada, double margemTamanho){
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
		                
		                Imagem imgCandidata = new Imagem(imagemOriginal.getNome() +"_cand_"+i, imagemOriginal.getFormato(), diretorioSaida, cropped);
		                
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
//		                imgCandidata = pp.paraTonsDeCinza(imgCandidata);
//		                imgCandidata.setHistograma(PreProcessamento.getHistograma(imgCandidata));
//		                imgCandidata = pp.paraPretoEBrancoOTSU(imgCandidata);
		                //////////////////////////////////////////
		                
		                //////////////////////////////////////////
		                // Segmentacao pela conta de densidade de pixels claros e escuros e componentes internos
		                float count = getQuantidadePixelsClaros(cropped);
		                imgCandidata.setQuantidadePixelsClaros(count);
		                count = getQuantidadePixelsEscuros(cropped);
		                imgCandidata.setQuantidadePixelsEscuros(count);
		                imgCandidata.setTrace(getQuantidadesComponentesInternos(imgCandidata, pp));
		                //////////////////////////////////////////
		                
		                imgCandidata.setCaminho(diretorioSaida);
		                regioesCandidatas.add(imgCandidata);
	            	}	
	            }
	        }   
	    }
	    return regioesCandidatas;
	}
	
	// Com este metodo aumentou para 232 acertos
	private int getQuantidadesComponentesInternos(Imagem img, PreProcessamento pp){
		Imagem temp = img.clone();
		temp = pp.paraTonsDeCinza(temp);
		temp = pp.normalizar(temp);
		temp = pp.filtroMediana(temp, 3);
		temp = pp.filtroMediana(temp, 3);
		temp = pp.paraPretoEBrancoGlobal(temp, 90);
		temp = pp.filtroAutoCanny(temp, 0);
		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	    Mat hierarchy = new Mat();
	    Imgproc.findContours(temp.getMatriz(), contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
	    List<MatOfPoint> contours_poly = new ArrayList<MatOfPoint>(contours.size());
	    contours_poly.addAll(contours);

	    MatOfPoint2f mMOP2f1,mMOP2f2;
	    mMOP2f1 = new MatOfPoint2f();
	    mMOP2f2 = new MatOfPoint2f();
	    
	    int cont = 0;
	    for(int i = 0; i < contours.size(); i++){
	            contours.get(i).convertTo(mMOP2f1, CvType.CV_32FC2);
	            mMOP2f2.convertTo(contours_poly.get(i), CvType.CV_32S);
	            Imgproc.approxPolyDP(mMOP2f1, mMOP2f2, 8, false);
	            
	            if(contours_poly.get(i).toList().size() >= 3 && contours_poly.get(i).toList().size() <= 6){	            	
//	                Imgproc.drawContours(temp.getMatriz(), contours_poly, i, new Scalar(255, 0, 255));
//	                temp.setNome(temp.getNome()+"_DRAW");
//	                temp.gravar();
	            	cont++;
	            }
	        
	    }
		return cont;
	}
	
	public static float getQuantidadePixelsClaros(Mat imagem){
		double[] cor;
		float countGray = 0;
		for (int row = 0; row < imagem.width(); row++) {
        	for (int col = 0; col < imagem.height(); col++) {
        		cor = imagem.get(row, col);
        		if(cor != null){
        			// Se for aproximadamente cinza
//        			if((cor[0] <= (cor[1]+margemCor) && cor[0] >= (cor[1]-margemCor) 
//        			&& cor[1] <= (cor[2]+margemCor) && cor[1] >= (cor[2]-margemCor)
//        			&& cor[0] <= (cor[2]+margemCor) && cor[0] >= (cor[2]-margemCor))
//        			&& (cor[0]+cor[1]+cor[2]) >= minimoCor){
//        				countGray++;
//        			}
        			
        			if(cor[0] >= minimoCor){
        				countGray++;
                	}
        		}
			}
		}
        //Numero de pixels claros do segmento / Numero de pixels do segmento
		return (countGray / (imagem.width()*imagem.height()));
	}
	
	public static float getQuantidadePixelsEscuros(Mat imagem){
		double[] cor;
		float countBlack = 0;
		for (int row = 0; row < imagem.width(); row++) {
        	for (int col = 0; col < imagem.height(); col++) {
        		cor = imagem.get(row, col);
        		if(cor != null){        			
        			// Se for aproximadamente preto
//        			if((cor[0] <= (cor[1]+margemCor) && cor[0] >= (cor[1]-margemCor) 
//                	&& cor[1] <= (cor[2]+margemCor) && cor[1] >= (cor[2]-margemCor)
//                	&& cor[0] <= (cor[2]+margemCor) && cor[0] >= (cor[2]-margemCor))
//                	&& (cor[0]+cor[1]+cor[2]) < minimoCor){
//        				countBlack++;
//                	}
        			
        			if(cor[0] < minimoCor){
        				countBlack++;
                	}
        		}
			}
		}
        //Numero de pixels escuros do segmento / Numero de pixels do segmento
		return (countBlack / (imagem.width()*imagem.height()));
	}
	
	// escolher candidata
	public Imagem getPlaca(ArrayList<Imagem> listaCandidatas){   
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
