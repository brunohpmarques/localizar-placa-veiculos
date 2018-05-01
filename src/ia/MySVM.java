package ia;

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
	
	public void toTrain() throws FileNotFoundException {
		if(!this.cvSVM.isTrained()) {
			int maxData = 90;
	        int count = 0;
	        Mat trainingData = new Mat(new Size(256, maxData*2), CvType.CV_32F);
	        Mat trainingLabels = new Mat(new Size(1, maxData*2), CvType.CV_32SC1);
	        Mat img;
	        ArrayList<Imagem> imagens;
	        	        
	        imagens = FileUtil.getListaImagens(PATH_POSITIVE, maxData);
	        for (int i = 0; i < imagens.size(); i++) {
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
	
	public ArrayList<Imagem> toTest(ArrayList<Imagem> imgInputs) throws Exception {
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
