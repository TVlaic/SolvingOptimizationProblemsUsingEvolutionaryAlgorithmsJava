package hr.fer.zemris.optjava.dz3;

import java.util.Random;

public class BitVectorSolution extends SingleObjectiveSolution{
	byte[] bits;
	
	public BitVectorSolution(int size){
		this.bits = new byte[size];
	}
	
	public BitVectorSolution newLikeThis(){
		return new BitVectorSolution(bits.length);
	}
	
	public BitVectorSolution duplicate(){
		BitVectorSolution clone = new BitVectorSolution(bits.length);
		clone.bits = this.bits.clone();
		return clone;
	}
	
	public void Randomize(Random rand){
		//implementiraj
	}
}