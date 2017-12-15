package com.ufu.dup.to;

public class SimScore {
	private double titleSim;
	private double desSim;
	private double topicSim;
	private double tagSim;
	
	
	public double getTitleSim() {
		return titleSim;
	}
	public void setTitleSim(double titleSim) {
		this.titleSim = titleSim;
	}
	public double getDesSim() {
		return desSim;
	}
	public void setDesSim(double desSim) {
		this.desSim = desSim;
	}

	public SimScore() {
		super();
	}
	
	
	public double getTopicSim() {
		return topicSim;
	}
	public void setTopicSim(double topicSim) {
		this.topicSim = topicSim;
	}
	public double getTagSim() {
		return tagSim;
	}
	public void setTagSim(double tagSim) {
		this.tagSim = tagSim;
	}
	@Override
	public String toString() {
		return "SimScore [titleSim=" + titleSim + ", desSim=" + desSim + ", topicSim=" + topicSim + ", tagSim=" + tagSim + "]";
	}
	public SimScore(double titleSim, double desSim, double topicSim, double tagSim) {
		super();
		this.titleSim = titleSim;
		this.desSim = desSim;
		this.topicSim = topicSim;
		this.tagSim = tagSim;
	}
		

	

}
