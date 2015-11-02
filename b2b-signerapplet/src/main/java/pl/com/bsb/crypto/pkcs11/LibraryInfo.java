package pl.com.bsb.crypto.pkcs11;

import sun.security.pkcs11.wrapper.CK_INFO;

/**
 * @author bkubacki
 * Obiekt reprezentuje informacje o bibliotece PKCS#11
 */
public class LibraryInfo {
	LibraryInfo(CK_INFO info) {		
		cryptokiVersion = info.cryptokiVersion.major + "." + info.cryptokiVersion.minor;
		version = info.libraryVersion.major + "." + info.libraryVersion.minor;
		description = new String(info.libraryDescription).trim();
		manufacturedID = new String(info.manufacturerID).trim();		
	}
	private String cryptokiVersion;
	private String version;
	private String description;
	private String manufacturedID;
	
	/**
	 * @return Zwraca wersję CRYPTOKI
	 */
	public String getCryptokiVersion() {
		return cryptokiVersion;
	}
	
	/**
	 * @return Zwraca opis biblioteki
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @return Zwraca producenta biblioteki
	 */
	public String getManufacturedID() {
		return manufacturedID;
	}
	
	/**
	 * @return Zwraca wersję biblioteki
	 */
	public String getVersion() {
		return version;
	} 
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String info = "Cryptoki version: " + cryptokiVersion + 
					  ", library version: " + version + 
					  ", description: " + description +
					  ", manufacturedId: " + manufacturedID;
		return info;
	}

}
