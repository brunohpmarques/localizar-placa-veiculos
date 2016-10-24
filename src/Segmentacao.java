import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
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

	    MatOfPoint2f mMOP2f1,mMOP2f2;
	    mMOP2f1=new MatOfPoint2f();
	    mMOP2f2=new MatOfPoint2f();

	    for(int i = 0; i < contours.size(); i++){
	        if (contours.get(i).toList().size() > 100){ 
	            contours.get(i).convertTo(mMOP2f1, CvType.CV_32FC2);
	            Imgproc.approxPolyDP(mMOP2f1,mMOP2f2, 3, true);
	            mMOP2f2.convertTo(contours_poly.get(i), CvType.CV_32S);
	            Rect appRect = Imgproc.boundingRect(contours_poly.get(i));
	            
//	            No. of White Pixels in the Rectangle/Total no. of Pixels in the rectangle
	            
	            if (appRect.width > appRect.height && appRect.width - appRect.height >= largura) {
	                Imgproc.rectangle(saida, new Point(appRect.x, appRect.y) ,new Point(appRect.x + appRect.width, appRect.y + appRect.height), new Scalar(255, 255, 0));
	                
	                Rect roi = new Rect(appRect.x, appRect.y, appRect.width, appRect.height);
	                Mat cropped = new Mat(imagemOriginal.getMatriz(), roi);
	                regioesCandidatas.add(new Imagem(imagemOriginal.getNome() +"_cand_"+i, imagemOriginal.getFormato(), diretorioSaida, cropped));
	            }
	        }   
	    }
	    
	    return regioesCandidatas;
	}

}
