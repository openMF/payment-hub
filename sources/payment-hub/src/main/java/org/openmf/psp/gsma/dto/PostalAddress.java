/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */

/**
@Author Sidhant Gupta
*/
package org.openmf.psp.gsma.dto;

/*
"postalAddress": {
  "addressLine1": "string",
  "addressLine2": "string",
  "addressLine3": "string",
  "city": "string",
  "stateProvince": "string",
  "postalCode": "string",
  "country": "AD"
}
*/

public class PostalAddress {

	String addressLine1;
	String addressLine2;
	String addressLine3;
	String city;
	String stateProvince;
	String postalCode;
	String country;

	public String getAddressLine1() {
	    return addressLine1;
	}
	
	public void setAddressLine1(String addressLine1) {
	    this.addressLine1 = addressLine1;
	}
	
	public String getAddressLine2() {
	    return addressLine2;
	}
	
	public void setAddressLine2(String addressLine2) {
	    this.addressLine2 = addressLine2;
	}
	
	public String getAddressLine3() {
	    return addressLine3;
	}
	
	public void setAddressLine3(String addressLine3) {
	    this.addressLine3 = addressLine3;
	}
	
	public String getCity() {
	    return city;
	}
	
	public void setCity(String city) {
	    this.city = city;
	}
	
	public String getStateProvince() {
	    return stateProvince;
	}
	
	public void setStateProvince(String stateProvince) {
	    this.stateProvince = stateProvince;
	}
	
	public String getPostalCode() {
	    return postalCode;
	}
	
	public void setPostalCode(String postalCode) {
	    this.postalCode = postalCode;
	}
	
	public String getCountry() {
	    return country;
	}
	
	public void setCountry(String country) {
	    this.country = country;
	}
}