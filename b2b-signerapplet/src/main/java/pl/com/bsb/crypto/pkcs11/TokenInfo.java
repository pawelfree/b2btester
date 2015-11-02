package pl.com.bsb.crypto.pkcs11;

import sun.security.pkcs11.wrapper.CK_TOKEN_INFO;

/**
 * @author bkubacki
 *	Informacje o urządzeniu PKCS#11
 */
public class TokenInfo {
	TokenInfo(CK_TOKEN_INFO info){
		firmwareVersion = info.firmwareVersion.major + "." + info.firmwareVersion.minor;
		hardwareVersion = info.hardwareVersion.major + "." + info.hardwareVersion.minor;
		label = new String(info.label).trim();
		manufacturerID = new String(info.manufacturerID).trim();
		model = new String(info.model).trim();
		serialNumber = new String(info.serialNumber).trim();
		
	} 
	private String firmwareVersion;
	private String hardwareVersion;
	private String label;
	private String manufacturerID;
	private String model;
	private String serialNumber;
	public String getFirmwareVersion() {
		return firmwareVersion;
	}
	public String getHardwareVersion() {
		return hardwareVersion;
	}
	public String getLabel() {
		return label;
	}
	public String getManufacturerID() {
		return manufacturerID;
	}
	public String getModel() {
		return model;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	
	public String toString() {
		String info = "firmwareVersion: " + firmwareVersion + ", " + 
					  "hardwareVersion: " + hardwareVersion + ", " + 
					  "label: " + label + ", " + 
					  "model: " + model + ", " + 
					  "serialNumber: " + serialNumber;
		return info;
	}
	
}
