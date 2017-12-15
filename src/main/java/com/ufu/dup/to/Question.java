package com.ufu.dup.to;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@Table(name = "posts")
public class Question {
	private static final long serialVersionUID = -111652190111812641L;
	@Id
    private Integer id;
	
	private String body;
	
	private String title;
	
	private String tags;
    
	@Transient
	private String topicVectors, tagVectors;
	
	
	public Question() {
	}
	
	public Question(Integer id) {
		this.id = id;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Question other = (Question) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getTopicVectors() {
		return topicVectors;
	}

	public void setTopicVectors(String topicVectors) {
		this.topicVectors = topicVectors;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getTagVectors() {
		return tagVectors;
	}

	public void setTagVectors(String tagVectors) {
		this.tagVectors = tagVectors;
	}

	
	
	

	
	
    
}