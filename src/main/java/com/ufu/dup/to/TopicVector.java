package com.ufu.dup.to;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@Table(name = "topicvector")
public class TopicVector {
	private static final long serialVersionUID = -111652190121815641L;
	@Id
    private Integer postid;
	
	private String vectors;
   		
	

	public TopicVector() {
	}
	
	public TopicVector(Integer id, String vectors) {
		this.postid = id;
		this.vectors=vectors;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((postid == null) ? 0 : postid.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TopicVector other = (TopicVector) obj;
		if (postid == null) {
			if (other.postid != null)
				return false;
		} else if (!postid.equals(other.postid))
			return false;
		return true;
	}



	public Integer getPostid() {
		return postid;
	}

	public void setPostid(Integer postid) {
		this.postid = postid;
	}

	public String getVectors() {
		return vectors;
	}

	public void setVectors(String vectors) {
		this.vectors = vectors;
	}

	@Override
	public String toString() {
		return "TopicVector [postid=" + postid + ", vectors=" + vectors + "]";
	}

	
	

	
	
    
}