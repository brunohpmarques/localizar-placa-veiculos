package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;

import model.Imagem;

public class Descritores {
	public static final double MARGEM_COR = 20;
	public static final double LIMIAR_COR = 128;
	private static final String DIRECT_DESCRITORES = System.getProperty("user.dir") +"/resultados/descritores";
	
	static{
		File fd = new File(DIRECT_DESCRITORES);		
		if(!fd.exists()){
			fd.mkdirs();
		}		
	}
	
	/** Retorna array do histograma de uma imagem **/
	public static int[] getHistograma(Imagem img) {
		int histograma[] = new int[256];
	    
	    Mat b_hist = getMatHistograma(img);
	    
	    for (int j = 0; j < b_hist.height(); j++) {
	    	histograma[j] = (int)Math.round(b_hist.get(j, 0)[0]);
		}	    
	    return histograma;
	}
	
	/** Retorna Mat do histograma de uma imagem **/
	public static Mat getMatHistograma(Imagem img) {
	    Vector<Mat> bgr_planes = new Vector<>();
	    Core.split(img.getMatriz(), bgr_planes);
	    
	    MatOfInt histSize = new MatOfInt(256);
	    MatOfFloat histRange = new MatOfFloat(0f, 256f);
	    Mat b_hist = new  Mat();
	    Imgproc.calcHist(bgr_planes, new MatOfInt(0), new Mat(), b_hist, histSize, histRange, false);
	    return b_hist;
	}

	/** Retorna a normal de uma imagem **/
	public static double getNorm(Imagem img){
		return Core.norm(img.getMatriz());
	}
	
	/** Retorna a media de uma imagem (double[] Scalar.val) **/
	public static Scalar getMean(Imagem img){
		return Core.mean(img.getMatriz());
	}
	
	/** Retorna a soma dos elementos de uma imagem (double[] Scalar.val) **/
	public static Scalar getSum(Imagem img){
		return Core.sumElems(img.getMatriz());
	}
	
	/** Retorna a soma dos elementos da diagonal de uma imagem (double[] Scalar.val) **/
	public static Scalar getTrace(Imagem img){
		return Core.trace(img.getMatriz());
	}
	
	/** Retorna a soma dos elementos da diagonal de uma imagem **/
	public static double getKMeans(Imagem img, int k){
		Mat out = img.getMatriz().clone();
		Mat samples = out.reshape(1, out.cols() * out.rows());
		Mat samples32f = new Mat();
		samples.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0);
		
		Mat labels = new Mat();
		TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, Integer.parseInt(out.total()+""), 1);
		Mat centers = new Mat();
		return Core.kmeans(samples32f, k, labels, criteria, 1, Core.KMEANS_PP_CENTERS, centers);
	}
	
	/**Numero de pixels claros do segmento / Numero de pixels do segmento*/
	public static float getQuantidadePixelsClaros(Imagem imagem, double limiar){
		return getQuantidadePixelsClaros(imagem.getMatriz(), limiar);
	}
	
	/**Numero de pixels claros do segmento / Numero de pixels do segmento*/
	public static float getQuantidadePixelsClaros(Mat imagem, double limiar){
		double[] cor;
		float countGray = 0;
		for (int row = 0; row < imagem.width(); row++) {
        	for (int col = 0; col < imagem.height(); col++) {
        		cor = imagem.get(row, col);
        		if(cor != null){        			
        			if(cor[0] >= limiar){
        				countGray++;
                	}
        		}
			}
		}
		return (countGray / (imagem.width()*imagem.height()));
	}
	
	/**Numero de pixels escuros do segmento / Numero de pixels do segmento*/
	public static float getQuantidadePixelsEscuros(Imagem imagem, double limiar){
		return getQuantidadePixelsEscuros(imagem.getMatriz(), limiar);
	}
	
	/**Numero de pixels escuros do segmento / Numero de pixels do segmento*/
	public static float getQuantidadePixelsEscuros(Mat imagem, double limiar){
		double[] cor;
		float countGray = 0;
		for (int row = 0; row < imagem.width(); row++) {
        	for (int col = 0; col < imagem.height(); col++) {
        		cor = imagem.get(row, col);
        		if(cor != null){        			        			
        			if(cor[0] < limiar){
        				countGray++;
                	}
        		}
			}
		}
		return (countGray / (imagem.width()*imagem.height()));
	}
	
	public static int getQuantidadesComponentesInternos(Imagem img){
		Imagem temp = img.clone();
		// OP1
//		temp = PreProcessamento.paraTonsDeCinza(temp);
//		temp = PreProcessamento.normalizar(temp);
//		temp = PreProcessamento.filtroMediana(temp, 5);
//		Scalar scalar = Descritores.getMean(temp);
//		temp = PreProcessamento.paraPretoEBrancoGlobal(temp, scalar.val[0]);//90
//		temp = PreProcessamento.filtroAutoCanny(temp, 3);
		
		// OP2
//		temp = PreProcessamento.paraTonsDeCinza(temp);
//		temp = PreProcessamento.filtroMediana(temp, 3);
//		temp = PreProcessamento.filtroNitidez(temp, 3, 1, 101, -101);
//		temp = PreProcessamento.filtroContraste(temp, 10);
//		Scalar scalar = Descritores.getMean(temp);
//		temp = PreProcessamento.paraPretoEBrancoGlobal(temp, scalar.val[0]);
//		temp = PreProcessamento.filtroAutoCanny(temp, 3);
		
		// OP3
//		temp = PreProcessamento.paraTonsDeCinza(temp);
		temp = PreProcessamento.filtroMediana(temp, 3);
		temp = PreProcessamento.filtroAutoCanny(temp, 3);
		
		temp.setCaminho(DIRECT_DESCRITORES);
		temp.gravar();
				
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
	
	/*
	 * Se der -> OpenCV Error: Bad argument (Specified feature detector type is not supported.) in cv::javaFeatureDetector::create
	 * Solucao: http://stackoverflow.com/questions/30657774/surf-and-sift-algorithms-doesnt-work-in-opencv-3-0-java
	 * */
	public static MatOfKeyPoint sift(Imagem imagem){
        return sift(imagem.getMatriz());
	}
	
	private static int id = 0;
	private static MatOfKeyPoint sift(Mat imagem){
        Mat blurredImage = new Mat();
        Mat output = new Mat();

        // remove some noise
        //Imgproc.blur(imagem, blurredImage, new Size(7, 7));

        //convert to gray
        //Mat gray = new Mat(imagem.width(), imagem.height(), CvType.CV_8U, new Scalar(4));
        Mat gray = new Mat(imagem.width(), imagem.height(), CvType.CV_8U);
        Imgproc.cvtColor(imagem, gray, Imgproc.COLOR_BGR2GRAY);

		FeatureDetector fd = FeatureDetector.create(FeatureDetector.BRISK); //ORB, MSER, GFTT, HARRIS, SIMPLEBLOB, BRISK, AKAZE
        MatOfKeyPoint regions = new MatOfKeyPoint();
        fd.detect(gray, regions);

        Features2d.drawKeypoints(gray, regions, output);
        new Imagem("sift"+(++id), ".jpg", "data/", output).gravar();
        
        
        //DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
        //MatOfDMatch matches = new MatOfDMatch();
        //matcher.match(output, matches);
        //img.setMatriz(output);
        System.out.println("KeyPoints: "+regions.rows());
        return regions;
	}
	
	/**Harris Corner Detection
	 * @see https://docs.opencv.org/2.4/doc/tutorials/features2d/trackingmotion/harris_detector/harris_detector.html*/
	public static Mat getHarrisCorner(Mat imagem, int thresh) {

	    // This function implements the Harris Corner detection. The corners at intensity > thresh
	    // are drawn.
	    Mat Harris_scene = new Mat();

	    Mat harris_scene_norm = new Mat(), harris_scene_scaled = new Mat();
	    int blockSize = 9;
	    int apertureSize = 5;
	    double k = 0.1;
	    Imgproc.cornerHarris(imagem, Harris_scene, blockSize, apertureSize,k);
	    Core.normalize(Harris_scene, harris_scene_norm, 0, 255, Core.NORM_MINMAX, CvType.CV_32FC1, new Mat());
	    Core.convertScaleAbs(harris_scene_norm, harris_scene_scaled);
	    return harris_scene_scaled;
	}
	
	public static int getCntHarrisCorner(Mat Scene, int thresh) {
		Mat hc = getHarrisCorner(Scene, thresh);
		int cnt = 0;
	    for( int j = 0; j < hc.rows() ; j++){
	        for( int i = 0; i < hc.cols(); i++){
	            if ((int) hc.get(j,i)[0] > thresh){
	                //Imgproc.circle(hc, new Point(i,j), 1 , new Scalar(255), 2 ,8 , 0);
	            	cnt++;
	            }
	        }
	    }
	   //new Imagem("harrisCorner"+(++id), ".jpg", "data/", harris_scene_scaled).gravar();
	    return cnt;
	}
	
	public static MatOfRect getHOG(Mat imagem) {
		HOGDescriptor hog = new HOGDescriptor();
		MatOfRect foundLocations = new MatOfRect();
        MatOfDouble foundWeights = new MatOfDouble();
        Size winStride = new Size(8, 8);
        Size padding = new Size(32, 32);
		hog.detectMultiScale(imagem, foundLocations, foundWeights, 0, winStride, padding, 1, 255, false);
		return foundLocations;
	}
}
