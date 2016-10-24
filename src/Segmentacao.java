import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


public class Segmentacao {
	
private String diretorioSaida;
	
	public Segmentacao(String diretorioSaida){
		this.diretorioSaida = diretorioSaida;
	}

	
	public ArrayList<Imagem> getRegioesCandidatas(Imagem imagemOriginal, Imagem processada, int largura){
		ArrayList<Imagem> regioesCandidatas = new ArrayList<Imagem>();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	    Mat hierarchy = new Mat();
	    Mat saida = processada.getMatriz().clone();
	    Imgproc.findContours(processada.getMatriz(), contours, hierarchy, 0, 1);
	    List<MatOfPoint> contours_poly = new ArrayList<MatOfPoint>(contours.size());
	    contours_poly.addAll(contours);

	    MatOfPoint2f mMOP2f1, mMOP2f2;
	    mMOP2f1 = new MatOfPoint2f();
	    mMOP2f2 = new MatOfPoint2f();

	    for(int i = 0; i < contours.size(); i++){
	        if (contours.get(i).toList().size() > 100){ 
	            contours.get(i).convertTo(mMOP2f1, CvType.CV_32FC2);
	            Imgproc.approxPolyDP(mMOP2f1, mMOP2f2, 5, true);
	            mMOP2f2.convertTo(contours_poly.get(i), CvType.CV_32S);
	            Rect appRect = Imgproc.boundingRect(contours_poly.get(i));
	            
//	            No. of White Pixels in the Rectangle/Total no. of Pixels in the rectangle
	            
	            if (appRect.width > appRect.height && appRect.width - appRect.height >= largura) {
	                Imgproc.rectangle(saida, new Point(appRect.x, appRect.y) ,new Point(appRect.x + appRect.width, appRect.y + appRect.height), new Scalar(255, 255, 0));
	                
	                Rect roi = new Rect(appRect.x, appRect.y, appRect.width, appRect.height);
	                Mat cropped = new Mat(imagemOriginal.getMatriz(), roi);
	                
	                Imgproc.drawContours(imagemOriginal.getMatriz(), contours_poly, i, new Scalar(255, 0, 255));
	                regioesCandidatas.add(new Imagem(imagemOriginal.getNome() +"_cand_"+i, imagemOriginal.getFormato(), diretorioSaida, cropped));
	            }
	        }   
	    }
	    
	    return regioesCandidatas;
	}
	
	// teste 2
	public ArrayList<Imagem> getRegioesCandidatas2(Imagem imagemOriginal, Imagem processada, int largura){
		ArrayList<Imagem> regioesCandidatas = new ArrayList<Imagem>();
		ArrayList<RotatedRect> rects = new  ArrayList<RotatedRect>();
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(processada.getMatriz(), contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
			
		int c = 0;
		for (MatOfPoint con : contours) {
			if (con.toList().size() > 100){
				Rect roi = Imgproc.boundingRect(con);
				// we only work with a submat, not the whole image:
				Mat mat = imagemOriginal.getMatriz().submat(roi); 
				RotatedRect rotatedRect = Imgproc.minAreaRect(new MatOfPoint2f(con.toArray()));
				Mat rot = Imgproc.getRotationMatrix2D(rotatedRect.center, rotatedRect.angle, 1.0);
				// rotate using the center of the roi
				double[] rot_0_2 = rot.get(0, 2);
				for (int i = 0; i < rot_0_2.length; i++) {
				    rot_0_2[i] += rotatedRect.size.width / 2 - rotatedRect.center.x;
				}
				rot.put(0, 2, rot_0_2);
				double[] rot_1_2 = rot.get(1, 2);
				for (int i = 0; i < rot_1_2.length; i++) {
				    rot_1_2[i] += rotatedRect.size.height / 2 - rotatedRect.center.y;
				}
				rot.put(1, 2, rot_1_2);
				// final rotated and cropped image:
				Mat rotated = new Mat();
				Imgproc.warpAffine(mat, rotated, rot, rotatedRect.size);
	            regioesCandidatas.add(new Imagem(imagemOriginal.getNome() +"_cand_"+c++, imagemOriginal.getFormato(), diretorioSaida, rotated));
			}
		}
		
		
//		i = 0;
//		for (RotatedRect appRect : rects) {
//			if (appRect.size.width > appRect.size.height && appRect.size.width - appRect.size.height >= largura) {
//                Point[] points = new Point[4];
//                appRect.points(points);
//                
//                Rect roi = new Rect(points[0], points[3]);
//                Mat cropped = new Mat(imagemOriginal.getMatriz(), roi);
//                regioesCandidatas.add(new Imagem(imagemOriginal.getNome() +"_cand_"+i++, imagemOriginal.getFormato(), diretorioSaida, cropped));
//            }
//		}
		
		return regioesCandidatas;
	}
	

	// teste 3
	public ArrayList<Imagem> getRegioesCandidatas3(Imagem imagemOriginal, Imagem processada, double margem){
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
	        if (contours.get(i).toList().size() > 100){ 
	            contours.get(i).convertTo(mMOP2f1, CvType.CV_32FC2);
	            mMOP2f2.convertTo(contours_poly.get(i), CvType.CV_32S);
	            Imgproc.approxPolyDP(mMOP2f1, mMOP2f2, 10, true);

	            if(contours_poly.get(i).toList().size() >= 4 && contours_poly.get(i).toList().size() <= 10){
	            	
	            	Rect appRect = Imgproc.boundingRect(contours_poly.get(i));
	            	double aspect = appRect.width / appRect.height;
	            	if(aspect <= (2.66 + margem) && aspect >= (2 - margem)){
	            		Rect roi = new Rect(appRect.x, appRect.y, appRect.width, appRect.height);
		                Mat cropped = new Mat(imagemOriginal.getMatriz(), roi);
		                
		                Imgproc.drawContours(imagemOriginal.getMatriz(), contours_poly, i, new Scalar(255, 0, 255));
		                regioesCandidatas.add(new Imagem(imagemOriginal.getNome() +"_cand_"+i, imagemOriginal.getFormato(), diretorioSaida, cropped));
	            	}	            	
	            }
	        }   
	    }
	    
	    return regioesCandidatas;
	}
	
}
