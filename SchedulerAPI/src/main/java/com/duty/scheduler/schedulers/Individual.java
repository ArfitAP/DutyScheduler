package com.duty.scheduler.schedulers;

public class Individual {
	
	protected int defaultGeneLength = 31*5;
    private byte[] genes = new byte[defaultGeneLength];
    private int fitness = 0;

    public Individual() {
        for (int i = 0; i < genes.length; i++) {
            byte gene = (byte) Math.round(Math.random());
            genes[i] = gene;
        }
    }

    protected byte getSingleGene(int index) {
        return genes[index];
    }

    protected void setSingleGene(int index, byte value) {
        genes[index] = value;
        fitness = 0;
    }

    public int getFitness() {
        if (fitness == 0) {
            fitness = GeneticAlgorithmScheduler.getFitness(this);
        }
        return fitness;
    }  
    
    public void setFitness(int fitness) {
		this.fitness = fitness;
	}

	public int getDefaultGeneLength() {
		return defaultGeneLength;
	}

	@Override
    public String toString() {
        String geneString = "";
        for (int i = 0; i < genes.length; i++) {
            geneString += getSingleGene(i);
        }
        return geneString;
    }
	
}
