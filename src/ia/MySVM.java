package ia;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.Ml;
import org.opencv.ml.SVM;
import org.opencv.objdetect.HOGDescriptor;

import model.Imagem;
import utils.ConstantesUtil;
import utils.Descritores;
import utils.FileUtil;
import utils.PreProcessamento;

//https://docs.opencv.org/3.0-beta/doc/tutorials/ml/introduction_to_svm/introduction_to_svm.html
public class MySVM {
	private SVM cvSVM;
	
	static{
		File fd = new File(ConstantesUtil.PATH_DATA_POSITIVE);		
		if(!fd.exists()){
			fd.mkdirs();
		}	
		
		fd = new File(ConstantesUtil.PATH_DATA_NEGATIVE);
		if(!fd.exists()){
			fd.mkdirs();
		}
	}
	
	public MySVM () {
		this.cvSVM = SVM.create();
		this.cvSVM.setKernel(SVM.LINEAR);
        this.cvSVM.setType(SVM.C_SVC);
	}
	
	private Mat getMat(Mat img){
		img = img.reshape(img.cols(), 1);
		img.convertTo(img, CvType.CV_32F);
		return img;
	}
	
	private void showMat(Mat mat){
		for (int i = 0; i < mat.rows(); i++) {
        	for (int j = 0; j < mat.cols(); j++) {
				System.out.printf(mat.get(i, j)[0]+"f, ");
			}
        	System.out.println();
		}
    	System.out.println();
	}
	
	private Mat getMat(float width, float heigth, float aspect, float hcorner, float normal, float mean, float sum, float trace, float kmeans, float pixelsClaros, float pixelsEscuros, float compInternos){
		Mat mat = new Mat(new Size(12, 1), CvType.CV_32F);
		mat.put(0, 0, width);
		mat.put(0, 1, heigth);
		mat.put(0, 2, aspect);
		mat.put(0, 3, hcorner);
		mat.put(0, 4, normal);
		mat.put(0, 5, mean);
		mat.put(0, 6, sum);
		mat.put(0, 7, trace);
		mat.put(0, 8, kmeans);
		mat.put(0, 9, pixelsClaros);
		mat.put(0, 10, pixelsEscuros);
		mat.put(0, 11, compInternos);
		return mat;
	}
	
	public void toTrain() throws FileNotFoundException {
		if(!this.cvSVM.isTrained()) {
			int maxData = 300;
	        int count = 0;
	        float width, heigth, aspect, hcorner, normal, mean, sum, trace, kmeans, pixelsClaros, pixelsEscuros, compInternos;
	        Mat trainingData = new Mat(new Size(0, 0), CvType.CV_32F);
	        Mat trainingLabels = new Mat(new Size(1, maxData*2), CvType.CV_32SC1);
	        Mat mat;
	        Imagem img;
	        ArrayList<Imagem> imagens;
	        	        
	        imagens = FileUtil.getListaImagens(ConstantesUtil.PATH_DATA_POSITIVE, maxData);
	        for (int i = 0; i < imagens.size(); i++) {
	        	img = imagens.get(i);
	        	img = PreProcessamento.paraTonsDeCinza(img);
//	        	img = Segmentacao.redimensionar(img);
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
	        	trainingData.push_back(mat);
	        	trainingLabels.put(count, 0, 1);
	        	count++;
			}
	        
	        imagens = FileUtil.getListaImagens(ConstantesUtil.PATH_DATA_NEGATIVE, maxData);
	        for (int i = 0; i < imagens.size(); i++) {
	        	img = imagens.get(i);
	        	img = PreProcessamento.paraTonsDeCinza(img);
//	        	img = Segmentacao.redimensionar(img);
	        	mat = img.getMatriz();
	        		        	
	        	width = Float.parseFloat(ConstantesUtil.EMPTY+mat.width());
	        	heigth = Float.parseFloat(ConstantesUtil.EMPTY+mat.height());
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
	        	trainingData.push_back(mat);
	        	trainingLabels.put(count, 0, 0);
	        	count++;
			}
	        
	        showMat(trainingData);
	                   	        
	        this.cvSVM.train(trainingData, Ml.ROW_SAMPLE, trainingLabels);	        
	        this.cvSVM.save(new File(ConstantesUtil.PATH_TRAIN_XML).getAbsolutePath());	        
		}
	}
	
	public ArrayList<Imagem> toTest(ArrayList<Imagem> imgInputs) throws Exception {
		if(this.cvSVM.isTrained()) {
			ArrayList<Imagem> imgOutputs = new ArrayList<>();
			float result = -1;
			float width, heigth, aspect, hcorner, normal, mean, sum, trace, kmeans, pixelsClaros, pixelsEscuros, compInternos;
	        Mat mat;
			
			for (Imagem imagem : imgInputs) {
//				imagem = Segmentacao.redimensionar(imagem);
				mat = imagem.getMatriz().clone();
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
	        		        	
	        	mat = getMat(width, heigth, aspect, hcorner, normal, mean, sum, trace, kmeans, pixelsClaros, pixelsEscuros, compInternos);
				result = this.cvSVM.predict(mat);
				
				if(result == 1) {
					imgOutputs.add(imagem);
					System.err.println("1: "+imagem.getNome());
				}
//				else {
//					System.err.println("0: "+imagem.getNome());
//				}
			}
	        System.err.println(imgOutputs.size()+" podem ser placa");
	        this.cvSVM.save(new File(ConstantesUtil.PATH_TEST_XML).getAbsolutePath());
	        return imgOutputs;
		}else {
			throw new Exception("SVM não treinada");
		}
	}
	
	public void toTrainHistograma() throws FileNotFoundException {
		if(!this.cvSVM.isTrained()) {
			int maxData = 300;
	        int count = 0;
	        Mat trainingData = new Mat(new Size(0, maxData*2), CvType.CV_32F);
	        Mat trainingLabels = new Mat(new Size(1, maxData*2), CvType.CV_32SC1);
	        Mat img;
	        ArrayList<Imagem> imagens;
	        	        
	        imagens = FileUtil.getListaImagens(ConstantesUtil.PATH_DATA_POSITIVE, maxData);
	        for (int i = 0; i < imagens.size(); i++) {
	        	Descritores.getQuantidadesComponentesInternos(imagens.get(i));
	        	img = Descritores.getMatHistograma(imagens.get(i));
	        	img = getMat(img);
	        	trainingData.push_back(img);
	        	trainingLabels.put(count, 0, 1);
	        	count++;
			}
	        
	        imagens = FileUtil.getListaImagens(ConstantesUtil.PATH_DATA_NEGATIVE, maxData);
	        for (int i = 0; i < imagens.size(); i++) {
	        	img = Descritores.getMatHistograma(imagens.get(i));
	        	img = getMat(img);
	        	trainingData.push_back(img);
	        	trainingLabels.put(count, 0, 0);
	        	count++;
			}
	        
	        
	        showMat(trainingData);
	        System.out.println(trainingData.size());
            	        
	        this.cvSVM.train(trainingData, Ml.ROW_SAMPLE, trainingLabels);
	        this.cvSVM.save(new File(ConstantesUtil.PATH_TRAIN_XML).getAbsolutePath());	        
		}
	}
	
	public ArrayList<Imagem> toTestHistograma(ArrayList<Imagem> imgInputs) throws Exception {
		if(this.cvSVM.isTrained()) {
			ArrayList<Imagem> imgOutputs = new ArrayList<>();
			float result = -1;
	        Mat img;
			
			for (Imagem imagem : imgInputs) {
				img = Descritores.getMatHistograma(imagem);
	        	img = imagem.getMatriz().clone();
				img = getMat(img);
				result = this.cvSVM.predict(img);
				if(result == 1) {
					imgOutputs.add(imagem);
					System.err.println("1: "+imagem.getNome());
				}
//				else {
//					System.err.println("0: "+imagem.getNome());
//				}
			}
	        System.err.println(imgOutputs.size()+" podem ser placa");
			this.cvSVM.save(new File(ConstantesUtil.PATH_TEST_XML).getAbsolutePath());
	        return imgOutputs;
		}else {
			throw new Exception("SVM não treinada");
		}
	}
	
	/**@see http://answers.opencv.org/question/128170/training-svm-using-hog-features-using-java/
	 * @see https://stackoverflow.com/questions/38233753/android-opencv-why-hog-descriptors-are-always-zero*/
	public void toTrainHOG() throws FileNotFoundException {
		if(!this.cvSVM.isTrained()) {
			int maxData = 350;
			Mat trainingData = new Mat();
	        Mat trainingLabels = new Mat();
	        Mat img = null;
	        Size sz = new Size(384,192);
	        Size szone = new Size(1,1);
	        Size szzer = new Size(0,0);
	        ArrayList<Imagem> imagens;
	        
	        imagens = FileUtil.getListaImagens(ConstantesUtil.PATH_DATA_POSITIVE, maxData);
	        for (int i = 0; i < imagens.size(); i++) {
	        	img = imagens.get(i).getMatriz();
		        Imgproc.resize(img, img, sz);
		        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2GRAY);
		        trainingData.push_back(img);
		        trainingLabels.push_back(Mat.ones(szone, CvType.CV_32SC1));
	        }
	        
	        imagens = FileUtil.getListaImagens(ConstantesUtil.PATH_DATA_NEGATIVE, maxData);
	        for (int i = 0; i < imagens.size(); i++) {
	        	img = imagens.get(i).getMatriz();
		        Imgproc.resize(img, img, sz);
		        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2GRAY);
		        trainingData.push_back(img);
		        trainingLabels.push_back(Mat.zeros(szone, CvType.CV_32SC1));
	        }  
	        
	        Size size = new Size(sz.width, sz.height);
	        Size block_size = new Size(size.width / 4, size.height / 4);
	        Size block_stride = new Size(size.width / 8, size.height / 8);
	        Size cell_size = block_stride;
	        int num_bins = 9;
	        HOGDescriptor hog = new HOGDescriptor(size, block_size, block_stride, cell_size, num_bins);
	        Mat inputHOG;
	        Mat gradients = new Mat();
	        MatOfPoint locations = new MatOfPoint();
	        MatOfFloat descriptors = new MatOfFloat();
	        
	        for (int i = 0; i < trainingData.rows(); i+=sz.height){
	            inputHOG = new Mat();
	            for (int j = 0; j < sz.height; j++) {
	            	inputHOG.push_back(trainingData.row(j));
				}
	            hog.compute(inputHOG, descriptors, szzer, szzer, locations);
	            img = descriptors.reshape(descriptors.cols(), 1);
	            gradients.push_back(img.clone());
	        }
	        
	        trainingData.convertTo(trainingData, CvType.CV_32FC1);
	        this.cvSVM.trainAuto(gradients, Ml.ROW_SAMPLE, trainingLabels);
	        this.cvSVM.save(new File(ConstantesUtil.PATH_TRAIN_XML).getAbsolutePath());
		}
	}

	public ArrayList<Imagem> toTestHOG(ArrayList<Imagem> imagens) throws Exception {
		if(this.cvSVM.isTrained()) {
			Size sz = new Size(384,192);
	        Size szzer = new Size(0,0);
	        Mat img = null;
	        double clazz;
	        Imagem imagem;
	        ArrayList<Imagem> imgOutputs = new ArrayList<>();
	        Size size = new Size(sz.width, sz.height);
	        Size block_size = new Size(size.width / 4, size.height / 4);
	        Size block_stride = new Size(size.width / 8, size.height / 8);
	        Size cell_size = block_stride;
	        int num_bins = 9;
	        HOGDescriptor hog = new HOGDescriptor(size, block_size, block_stride, cell_size, num_bins);
	        MatOfPoint locations = new MatOfPoint();
	        MatOfFloat descriptors = new MatOfFloat();
	        
	        for (int i = 0; i < imagens.size(); i++) {
	        	imagem = imagens.get(i);
	        	img = imagem.getMatriz().clone();
		        Imgproc.resize(img, img, sz);
		        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2GRAY);
		        hog.compute(img, descriptors, szzer, szzer, locations);
		        img = descriptors.reshape(descriptors.cols(), 1);
		        
		        clazz = this.cvSVM.predict(img);
		        if(clazz == 1) {
		        	imgOutputs.add(imagem);
		        }
	        }
	        System.err.println(imgOutputs.size()+" podem ser placa");
	        this.cvSVM.save(new File(ConstantesUtil.PATH_TEST_XML).getAbsolutePath());
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
