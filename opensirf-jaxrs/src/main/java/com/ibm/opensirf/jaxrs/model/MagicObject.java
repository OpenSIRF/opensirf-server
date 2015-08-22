/*
 * OpenSIRF JAX-RS
 * 
 * Copyright IBM Corporation 2015.
 * All Rights Reserved.
 * 
 * MIT License:
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 * Except as contained in this notice, the name of a copyright holder shall not
 * be used in advertising or otherwise to promote the sale, use or other
 * dealings in this Software without prior written authorization of the
 * copyright holder.
 */
package com.ibm.opensirf.jaxrs.model;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MagicObject {
	public MagicObject() { }
	
	public MagicObject(Map<String,String> metadata)
	{
		sirfCatalogId = metadata.get("sirfcatalogid");
		sirfLevel = metadata.get("sirflevel");
		containerSpecification = metadata.get("containerspecification");
	}
	
	public String getSirfLevel() {
		return sirfLevel;
	}

	public void setSirfLevel(String sirfLevel) {
		this.sirfLevel = sirfLevel;
	}

	public String getContainerSpecification() {
		return containerSpecification;
	}

	public void setContainerSpecification(String containerSpecification) {
		this.containerSpecification = containerSpecification;
	}

	public String getSirfCatalogId() {
		return sirfCatalogId;
	}

	public void setSirfCatalogId(String sirfCatalogId) {
		this.sirfCatalogId = sirfCatalogId;
	}

	@XmlElement
	private String sirfLevel;

	@XmlElement
	private String containerSpecification;

	@XmlElement
	private String sirfCatalogId;
}
