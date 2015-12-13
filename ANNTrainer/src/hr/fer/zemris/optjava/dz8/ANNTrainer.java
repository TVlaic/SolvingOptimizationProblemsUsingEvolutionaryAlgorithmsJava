package hr.fer.zemris.optjava.dz8;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

import hr.fer.zemris.optjava.dz8.EvaluateSolution;
import hr.fer.zemris.optjava.dz8.FFANN;
import hr.fer.zemris.optjava.dz8.IFunction;

public class ANNTrainer {
	
	public static void main(String[] args) {
		Dataset data =null;
		int firstNetParameter=0;
		boolean elman = false;
		int net[]=null;
		int n =0;
		double merr=0;
		int maxIter = 0;
		try{
			if(args[1].contains("tdnn")){
				
			}else if(args[1].contains("elman")){
				elman=true;
			}else{
				System.out.println("Invalid net definition");
				return;
			}
			String arh[] = args[1].split("-");
			if(arh.length!= 2){

				System.out.println("Invalid net definition");
				return;
			}else{
				String arhValue[] = arh[1].split("x");
				net = new int[arhValue.length];
				if(elman && Integer.parseInt(arhValue[0]) != 1){
					System.out.println("net must have 1 input");
					return;
				}
				for(int i=0;i<net.length;i++){
					
					net[i] = Integer.parseInt(arhValue[i]);
					
				}
				if(net[net.length-1] != 1){
					System.out.println("net must have 1 output");
					return;
				}
				
			}
		}catch(Exception e){
			System.out.println("Invalid net definition, must be of valid format tdnn-8x10x1 for example");
			return;
		}
		
		try{
			double normalizationFactor = getNormalizationFactor(args[0]);
			double min = getMin(args[0]);
			data = loadDataset(args[0], net[0], 100, elman, normalizationFactor,min);
			
		}catch(Exception e){
			System.out.println("wrong file path");
			return;
		}
		
		try{
			n=Integer.parseInt(args[2]);
			
		}catch(Exception e){
			System.out.println("Parameter not numeric, defines number of solutions in pop");
			return;
		}
		
		try{
			merr=Double.parseDouble(args[3]);
			
		}catch(Exception e){
			System.out.println("Defines max error");
			return;
		}
		
		try{
			maxIter=Integer.parseInt(args[4]);
			
		}catch(Exception e){
			System.out.println("Defines max iterations");
			return;
		}

		
		
//		for(Data dat : data.data){
//			System.out.println("input");
//			for(Double value : dat.input){
//				System.out.print(value);
//			}
//			System.out.println();
//			System.out.println("output");
//			for(Double value : dat.output){
//				System.out.print(value);
//			}
//			System.out.println();
//		}
		IFunction fun[] = new IFunction[net.length-1];
		for(int i=0;i<fun.length;i++){
			fun[i] = new HiperbolicTan();
		}
		FFANN neural = new FFANN(net, fun, data, elman);
		
//		for(int i=0;i<data.getSizeOfSample();i++){
//			Data dat = data.getIthSample(i);
//			for(int j=0;j<dat.input.size();j++){
//				if(!(dat.input.get(j)<1 || dat.input.get(j)>1)){
//					System.out.println("zakej" + dat.input.get(j));
//				}
//			}
//			for(int j=0;j<dat.output.size();j++){
//				if(!(dat.output.get(j)>1 || dat.output.get(j)<1)){
//					System.out.println("zakej" + dat.output.get(j));
//				}
//			}
//		}
		EvaluateSolution evaluator = new EvaluateSolution(neural);
		DifferentialEvolution  alg = new DifferentialEvolution(n, neural.weightCount, evaluator, merr, maxIter);
		alg.run();
	}
	
	public static double getMin(String pathToFile){
		File dataFile = new File(pathToFile);
		Scanner scan = null;
		double min=0;
		
		try {
			scan = new Scanner(dataFile);
		} catch (FileNotFoundException e) {
			System.out.println("Error opening a file reader");
			System.exit(0);
		}
		if(scan.hasNextLine()){
			double temp = scan.nextDouble();
			min = temp;
		}
		while(scan.hasNextLine()){
			double temp = scan.nextDouble();
			if(temp < min){
				min = temp;
			}
		}
		return min;
	}
	
	private static double getNormalizationFactor(String pathToFile) {
		File dataFile = new File(pathToFile);
		Scanner scan = null;
		double normalizationFactor =0;
		double min=0;
		double max=0;
		
		try {
			scan = new Scanner(dataFile);
		} catch (FileNotFoundException e) {
			System.out.println("Error opening a file reader");
			System.exit(0);
		}
		if(scan.hasNextLine()){
			double temp = scan.nextDouble();
			min = temp;
			max = temp;
		}
		while(scan.hasNextLine()){
			double temp = scan.nextDouble();
			if(temp < min){
				min = temp;
			}
			if(temp > max){
				max = temp;
			}
		}
		normalizationFactor = (max-min);
		return normalizationFactor;
		
	}

	public static Dataset loadDataset(String pathToFile, int bufferSize, int numberOfSamples, boolean elman, double normalizationFactor, double minElem){
		File dataFile = new File(pathToFile);
		Scanner scan = null;
		if(elman){
			bufferSize = 1;
		}
		try {
			scan = new Scanner(dataFile);
		} catch (FileNotFoundException e) {
			System.out.println("Error opening a file reader");
			System.exit(0);
		}
		LinkedList<Double> buffer = new LinkedList<>();
		LinkedList<Double> outputs = new LinkedList<>();
		LinkedList<Data> dataList = new LinkedList<>();
		
		
		while(scan.hasNextLine() && dataList.size() < numberOfSamples){
			if(buffer.size()<bufferSize){
				buffer.add(2*(scan.nextDouble()-minElem)/normalizationFactor -1);
			}else{
				outputs.add(2*(scan.nextDouble()-minElem)/normalizationFactor -1);
				dataList.add(new Data(new LinkedList<>(buffer), new LinkedList<>(outputs)));
				buffer.removeFirst();
				buffer.addLast(outputs.getFirst());
				outputs.clear();
			}
		}
		Dataset myDataset = new Dataset(dataList);
		if(myDataset == null || myDataset.getSizeOfSample() <0){
			System.out.println("No data samples");
			System.exit(0);
		}
		return myDataset;
			
	}

}
