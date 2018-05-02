package ia;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.ml.Ml;
import org.opencv.ml.SVM;

import model.Imagem;
import utils.Descritores;
import utils.FileUtil;

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
	}
	
	private Mat getMat(Mat img){
		img = img.reshape(img.cols(), 1);
		img.convertTo(img, CvType.CV_32F);
		return img;
	}
	
	private Mat getMat(float normal, float mean, float sum, float trace, float pixelsClaros, float pixelsEscuros, float compInternos){
		Mat mat = new Mat(new Size(7, 1), CvType.CV_32F);
		mat.put(0, 0, normal);
		mat.put(1, 0, mean);
		mat.put(2, 0, sum);
		mat.put(3, 0, trace);
		mat.put(4, 0, pixelsClaros);
		mat.put(5, 0, pixelsEscuros);
		mat.put(6, 0, compInternos);
		return mat;
	}
	
	public void toTrain() throws FileNotFoundException {
		if(!this.cvSVM.isTrained()) {
			int maxData = 10;
	        int count = 0;
	        float normal, mean, sum, trace, kmeans, pixelsClaros, pixelsEscuros, compInternos;
	        Mat trainingData = new Mat(new Size(7, maxData*2), CvType.CV_32F);
	        Mat trainingLabels = new Mat(new Size(1, maxData*2), CvType.CV_32SC1);
	        Mat mat;
	        Imagem img;
	        ArrayList<Imagem> imagens;
	        	        
	        imagens = FileUtil.getListaImagens(PATH_POSITIVE, maxData);
	        for (int i = 0; i < imagens.size(); i++) {
	        	img = imagens.get(i);
	        	mat = img.getMatriz();
	        	
	        	Descritores.sift(mat);
	        	
	        	normal = (float) Descritores.getNorm(img);
	        	mean = (float) Descritores.getMean(img).val[0];
	        	sum = (float) Descritores.getSum(img).val[0];
	        	trace = (float) Descritores.getTrace(img).val[0];
	        	pixelsClaros = Descritores.getQuantidadePixelsClaros(mat, Descritores.LIMIAR_COR);
	        	pixelsEscuros = Descritores.getQuantidadePixelsEscuros(mat, Descritores.LIMIAR_COR);
	        	compInternos = Descritores.getQuantidadesComponentesInternos(img);
	        	System.out.println(img.getNome()+" - 1 - "+normal+" - "+mean+" - "+sum+" - "+trace+" - "+pixelsClaros+" - "+pixelsEscuros+" - "+compInternos);
	        		        	
	        	mat = getMat(normal, mean, sum, trace, pixelsClaros, pixelsEscuros, compInternos);
	        	trainingData.row(count).push_back(mat);
	        	trainingLabels.put(count, 0, 1);
	        	count++;
			}
	        
	        imagens = FileUtil.getListaImagens(PATH_NEGATIVE, maxData);
	        for (int i = 0; i < imagens.size(); i++) {
	        	img = imagens.get(i);
	        	mat = img.getMatriz();
	        	
	        	normal = (float) Descritores.getNorm(img);
	        	mean = (float) Descritores.getMean(img).val[0];
	        	sum = (float) Descritores.getSum(img).val[0];
	        	trace = (float) Descritores.getTrace(img).val[0];
	        	kmeans = (float) Descritores.getKMeans(img, 4);
	        	pixelsClaros = Descritores.getQuantidadePixelsClaros(mat, Descritores.LIMIAR_COR);
	        	pixelsEscuros = Descritores.getQuantidadePixelsEscuros(mat, Descritores.LIMIAR_COR);
	        	compInternos = Descritores.getQuantidadesComponentesInternos(img);
	        	System.out.println(img.getNome()+" - 0 - "+normal+" - "+mean+" - "+sum+" - "+trace+" - "+pixelsClaros+" - "+pixelsEscuros+" - "+compInternos);
	        	
	        	mat = getMat(normal, mean, sum, trace, pixelsClaros, pixelsEscuros, compInternos);
	        	trainingData.row(count).push_back(mat);
	        	trainingLabels.put(count, 0, 0);
	        	count++;
			}
            	        
	        this.cvSVM.train(trainingData, Ml.ROW_SAMPLE, trainingLabels);
	        //this.cvSVM.save(TRAIN_XML);	        
		}
	}
	
	public ArrayList<Imagem> toTest(ArrayList<Imagem> imgInputs) throws Exception {
		if(this.cvSVM.isTrained()) {
			ArrayList<Imagem> imgOutputs = new ArrayList<>();
			float result = -1;
			float normal, mean, sum, trace, pixelsClaros, pixelsEscuros, compInternos;
	        Mat mat;
			
			for (Imagem imagem : imgInputs) {
	        	mat = imagem.getMatriz();
	        	
	        	normal = (float) Descritores.getNorm(imagem);
	        	mean = (float) Descritores.getMean(imagem).val[0];
	        	sum = (float) Descritores.getSum(imagem).val[0];
	        	trace = (float) Descritores.getTrace(imagem).val[0];
	        	pixelsClaros = Descritores.getQuantidadePixelsClaros(mat, Descritores.LIMIAR_COR);
	        	pixelsEscuros = Descritores.getQuantidadePixelsEscuros(mat, Descritores.LIMIAR_COR);
	        	compInternos = Descritores.getQuantidadesComponentesInternos(imagem);
	        	System.err.println(imagem.getNome()+"? "+normal+" - "+mean+" - "+sum+" - "+trace+" - "+pixelsClaros+" - "+pixelsEscuros+" - "+compInternos);
	        	
	        	mat = getMat(normal, mean, sum, trace, pixelsClaros, pixelsEscuros, compInternos);
				result = this.cvSVM.predict(mat);
				if(result == 1) {
					imgOutputs.add(imagem);
					System.err.println("1: "+imagem.getNome());
				}else {
					System.err.println("0: "+imagem.getNome());
				}
			}
			
			mat = getMat(4737.75f, 67.63452f, 70137.0f, 1046.0f, 0.029893925f, 0.2487946f, 2.0f);
			result = this.cvSVM.predict(mat);
			if(result == 1) {
				System.err.println("1: FAKE");
			}else {
				System.err.println("0: FAKE");
			}
			
			
	        //this.cvSVM.save(TEST_XML);
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
	        //this.cvSVM.save(TRAIN_XML);	        
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
	        //this.cvSVM.save(TEST_XML);
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
