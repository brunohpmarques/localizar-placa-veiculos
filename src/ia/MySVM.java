package ia;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.Ml;
import org.opencv.ml.SVM;

import model.Imagem;
import utils.Descritores;
import utils.FileUtil;
import utils.PreProcessamento;

//https://docs.opencv.org/3.0-beta/doc/tutorials/ml/introduction_to_svm/introduction_to_svm.html
public class MySVM {
	private static final String PATH_POSITIVE = "data/positivo/";
	private static final String PATH_NEGATIVE = "data/negativo/";
	private static final String TRAIN_XML = "data/train.xml";
	private static final String TEST_XML = "data/test.xml";
	private SVM cvSVM;
	
	static{
		File fd = new File(PATH_POSITIVE);		
		if(!fd.exists()){
			fd.mkdirs();
		}	
		
		fd = new File(PATH_NEGATIVE);
		if(!fd.exists()){
			fd.mkdirs();
		}
	}
	
	public MySVM () {
		this.cvSVM = SVM.create();
		this.cvSVM.setKernel(SVM.LINEAR);
        this.cvSVM.setType(SVM.C_SVC);
        this.cvSVM.setC(100000);
        
        TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER,100,0.1);
        this.cvSVM.setKernel(SVM.LINEAR);
        this.cvSVM.setType(SVM.C_SVC);
        this.cvSVM.setGamma(0.5);
        this.cvSVM.setNu(0.5);
        this.cvSVM.setC(1);
        this.cvSVM.setTermCriteria(criteria);
	}
	
	private Mat getMat(Mat img){
		img = img.reshape(img.cols(), 1);
		img.convertTo(img, CvType.CV_32F);
		return img;
	}
	
	private Mat getMat(float width, float heigth, float aspect, float hcorner, float normal, float mean, float sum, float trace, float kmeans, float pixelsClaros, float pixelsEscuros, float compInternos){
		Mat mat = new Mat(new Size(12, 1), CvType.CV_32FC1);
		mat.put(0, 0, width);
		mat.put(1, 0, heigth);
		mat.put(2, 0, aspect);
		mat.put(3, 0, hcorner);
		mat.put(4, 0, normal);
		mat.put(5, 0, mean);
		mat.put(6, 0, sum);
		mat.put(7, 0, trace);
		mat.put(8, 0, kmeans);
		mat.put(9, 0, pixelsClaros);
		mat.put(10, 0, pixelsEscuros);
		mat.put(11, 0, compInternos);
		return mat;
	}
	
	public void toTrain() throws FileNotFoundException {
		if(!this.cvSVM.isTrained()) {
			int maxData = 100;
	        int count = 0;
	        float width, heigth, aspect, hcorner, normal, mean, sum, trace, kmeans, pixelsClaros, pixelsEscuros, compInternos;
	        Mat trainingData = new Mat(new Size(12, maxData*2), CvType.CV_32FC1);
	        Mat trainingLabels = new Mat(new Size(1, maxData*2), CvType.CV_32SC1);
	        Mat mat;
	        Imagem img;
	        ArrayList<Imagem> imagens;
	        	        
	        imagens = FileUtil.getListaImagens(PATH_POSITIVE, maxData);
	        for (int i = 0; i < imagens.size(); i++) {
	        	img = imagens.get(i);
	        	img = PreProcessamento.paraTonsDeCinza(img);
	        	mat = img.getMatriz();
	        		        	
	        	width = mat.width();
	        	heigth = mat.height();
	        	aspect = width/heigth;
	        	hcorner = Descritores.getCntHarrisCorner(mat, 128);
	        	normal = (float) Descritores.getNorm(img);
	        	mean = (float) Descritores.getMean(img).val[0];
	        	sum = (float) Descritores.getSum(img).val[0];
	        	trace = (float) Descritores.getTrace(img).val[0];
	        	kmeans = (float) Descritores.getKMeans(img, 4);
	        	pixelsClaros = Descritores.getQuantidadePixelsClaros(mat, Descritores.LIMIAR_COR);
	        	pixelsEscuros = Descritores.getQuantidadePixelsEscuros(mat, Descritores.LIMIAR_COR);
	        	compInternos = Descritores.getQuantidadesComponentesInternos(img);
	        	//System.out.println(img.getNome()+" - 1 - "+width+" - "+heigth+" - "+aspect+" - "+hcorner+" - "+normal+" - "+mean+" - "+sum+" - "+trace+" - "+kmeans+" - "+pixelsClaros+" - "+pixelsEscuros+" - "+compInternos);
	        		        	
	        	mat = getMat(width, heigth, aspect, hcorner, normal, mean, sum, trace, kmeans, pixelsClaros, pixelsEscuros, compInternos);
	        	trainingData.row(count).push_back(mat);
	        	trainingLabels.put(count, 0, 1);
	        	count++;
			}
	        
	        imagens = FileUtil.getListaImagens(PATH_NEGATIVE, maxData);
	        for (int i = 0; i < imagens.size(); i++) {
	        	img = imagens.get(i);
	        	img = PreProcessamento.paraTonsDeCinza(img);
	        	mat = img.getMatriz();
	        		        	
	        	width = mat.width();
	        	heigth = mat.height();
	        	aspect = width/heigth;
	        	hcorner = Descritores.getCntHarrisCorner(mat, 128);
	        	normal = (float) Descritores.getNorm(img);
	        	mean = (float) Descritores.getMean(img).val[0];
	        	sum = (float) Descritores.getSum(img).val[0];
	        	trace = (float) Descritores.getTrace(img).val[0];
	        	kmeans = (float) Descritores.getKMeans(img, 4);
	        	pixelsClaros = Descritores.getQuantidadePixelsClaros(mat, Descritores.LIMIAR_COR);
	        	pixelsEscuros = Descritores.getQuantidadePixelsEscuros(mat, Descritores.LIMIAR_COR);
	        	compInternos = Descritores.getQuantidadesComponentesInternos(img);
	        	//System.out.println(img.getNome()+" - 0 - "+width+" - "+heigth+" - "+aspect+" - "+hcorner+" - "+normal+" - "+mean+" - "+sum+" - "+trace+" - "+kmeans+" - "+pixelsClaros+" - "+pixelsEscuros+" - "+compInternos);
	        	
	        	mat = getMat(width, heigth, aspect, hcorner, normal, mean, sum, trace, kmeans, pixelsClaros, pixelsEscuros, compInternos);
	        	trainingData.row(count).push_back(mat);
	        	trainingLabels.put(count, 0, 0);
	        	count++;
			}
	        
	        for (int i = 0; i < trainingData.rows(); i++) {
	        	for (int j = 0; j < trainingData.cols(); j++) {
					System.out.printf(trainingData.get(i, j)[0]+", ");
				}
	        	System.out.println();
			}
            	        
	        this.cvSVM.train(trainingData, Ml.ROW_SAMPLE, trainingLabels);	        
	        ////this.cvSVM.save(new File(TRAIN_XML).getAbsolutePath());	        
		}
	}
	
	public ArrayList<Imagem> toTest(ArrayList<Imagem> imgInputs) throws Exception {
		if(this.cvSVM.isTrained()) {
			ArrayList<Imagem> imgOutputs = new ArrayList<>();
			float result = -1;
			float width, heigth, aspect, hcorner, normal, mean, sum, trace, kmeans, pixelsClaros, pixelsEscuros, compInternos;
	        Mat mat;
			
			for (Imagem imagem : imgInputs) {
				mat = imagem.getMatriz().clone();
				mat.put(0, 0);
				Imgproc.cvtColor(imagem.getMatriz(), mat, Imgproc.COLOR_RGB2GRAY);
	        	
	        	width = mat.width();
	        	heigth = mat.height();
	        	aspect = width/heigth;
	        	hcorner = Descritores.getCntHarrisCorner(mat, 128);
	        	normal = (float) Descritores.getNorm(imagem);
	        	mean = (float) Descritores.getMean(imagem).val[0];
	        	sum = (float) Descritores.getSum(imagem).val[0];
	        	trace = (float) Descritores.getTrace(imagem).val[0];
	        	kmeans = (float) Descritores.getKMeans(imagem, 4);
	        	pixelsClaros = Descritores.getQuantidadePixelsClaros(mat, Descritores.LIMIAR_COR);
	        	pixelsEscuros = Descritores.getQuantidadePixelsEscuros(mat, Descritores.LIMIAR_COR);
	        	compInternos = Descritores.getQuantidadesComponentesInternos(imagem);
	        	System.err.println(imagem.getNome()+"? "+normal+" - "+mean+" - "+sum+" - "+trace+" - "+pixelsClaros+" - "+pixelsEscuros+" - "+compInternos);
	        	
	        	mat = getMat(width, heigth, aspect, hcorner, normal, mean, sum, trace, kmeans, pixelsClaros, pixelsEscuros, compInternos);
				result = this.cvSVM.predict(mat);
				
				if(result == 1) {
					imgOutputs.add(imagem);
					System.err.println("1: "+imagem.getNome());
				}else {
					System.err.println("0: "+imagem.getNome());
				}
			}
			
//			mat = getMat(64.0f, 22.0f, 2.909091f, 1319.0f, 5917.45f, 147.67897f, 207932.0f, 3109.0f, 4.8876977f, 0.2393466f, 0.10440341f, 2.0f);
//			result = this.cvSVM.predict(mat);
//			if(result == 1) {
//				System.err.println("1: FAKE");
//			}else {
//				System.err.println("0: FAKE");
//			}
			
	        //this.cvSVM.save(new File(TEST_XML).getAbsolutePath());
	        return imgOutputs;
		}else {
			throw new Exception("SVM não treinada");
		}
	}
	
	public void toTrainHistograma() throws FileNotFoundException {
		if(!this.cvSVM.isTrained()) {
			int maxData = 10;
	        int count = 0;
	        Mat trainingData = new Mat(new Size(256, maxData*2), CvType.CV_32F);
	        Mat trainingLabels = new Mat(new Size(1, maxData*2), CvType.CV_32SC1);
	        Mat img;
	        ArrayList<Imagem> imagens;
	        	        
	        imagens = FileUtil.getListaImagens(PATH_POSITIVE, maxData);
	        for (int i = 0; i < imagens.size(); i++) {
	        	Descritores.getQuantidadesComponentesInternos(imagens.get(i));
	        	img = Descritores.getMatHistograma(imagens.get(i));
	        	img = getMat(img);
	        	trainingData.row(count).push_back(img);
	        	trainingLabels.put(count, 0, 1);
	        	count++;
			}
	        
	        imagens = FileUtil.getListaImagens(PATH_NEGATIVE, maxData);
	        for (int i = 0; i < imagens.size(); i++) {
	        	img = Descritores.getMatHistograma(imagens.get(i));
	        	img = getMat(img);
	        	trainingData.row(count).push_back(img);
	        	trainingLabels.put(count, 0, 0);
	        	count++;
			}
                        
            trainingData = trainingData.rowRange(0, trainingData.rows()-1);
            trainingLabels = trainingLabels.rowRange(0, trainingLabels.rows()-1);
            	        
	        this.cvSVM.train(trainingData, Ml.ROW_SAMPLE, trainingLabels);
	        //this.cvSVM.save(new File(TRAIN_XML).getAbsolutePath());	        
		}
	}
	
	public ArrayList<Imagem> toTestHistograma(ArrayList<Imagem> imgInputs) throws Exception {
		if(this.cvSVM.isTrained()) {
			ArrayList<Imagem> imgOutputs = new ArrayList<>();
			float result = -1;
	        Mat img;
			
			for (Imagem imagem : imgInputs) {
				img = Descritores.getMatHistograma(imagem);
				img = getMat(img);
				result = this.cvSVM.predict(img);
				if(result == 1) {
					imgOutputs.add(imagem);
				}else {
					System.out.println("0: "+imagem.getNome());
				}
			}
			//this.cvSVM.save(new File(TEST_XML).getAbsolutePath());
	        return imgOutputs;
		}else {
			throw new Exception("SVM não treinada");
		}
	}
	
	public void testar() {
		Mat labels = new Mat(new Size(1,4),CvType.CV_32SC1);
	    labels.put(0, 0, 1);
	    labels.put(1, 0, 1);
	    labels.put(2, 0, 1);
	    labels.put(3, 0, 0);

	    Mat data = new Mat(new Size(1,4),CvType.CV_32FC1);
	    data.put(0, 0, 5);
	    data.put(1, 0, 2);
	    data.put(2, 0, 3);
	    data.put(3, 0, 8);

	    Mat testSamples = new Mat(new Size(1,5),CvType.CV_32FC1);
	    testSamples.put(0,0,8);
	    testSamples.put(1,0,3);
	    testSamples.put(2,0,6);
	    testSamples.put(3,0,12);
	    testSamples.put(4,0,15);

	    SVM svm = SVM.create();
	    TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER,100,0.1);
	    svm.setKernel(SVM.LINEAR);
	    svm.setType(SVM.C_SVC);
	    svm.setGamma(0.5);
	    svm.setNu(0.5);
	    svm.setC(1);
	    svm.setTermCriteria(criteria);

	    //data is N x 64 trained data Mat , labels is N x 1 label Mat with integer values;
	    svm.train(data, Ml.ROW_SAMPLE, labels);

	    float predictedClass;
	    System.out.println(testSamples.size());
	    for (int i = 0; i < testSamples.rows(); i++) {
	    	predictedClass = svm.predict(testSamples.row(i));
	    	System.out.println(predictedClass);
		}
	    //System.out.println(predictedClass);
	}
}
