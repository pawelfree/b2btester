package pl.com.bsb.crypto.pkcs11;

//import java.io.IOException;
import java.lang.reflect.Method;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


//import sun.security.pkcs11.SunPKCS11;
import sun.security.pkcs11.wrapper.CK_INFO;
import sun.security.pkcs11.wrapper.PKCS11;

/**
 * @author bkubacki
 *	Klasa reprezentuje biblioekę PKCS#11
 */
public class Library {
	PKCS11 function = null;
	
	
	/**
	 * Konstruktor
	 * @param libName Nazwa biblioteki PKCS#11
	 * @throws P11Exception Błędy PKCS#11
	 */
	public Library(String libName) throws P11Exception{		
		Method method = null;
		try{
			//dla javy 5 i 6 funkcja getInstance ma inne wywoania! 
			//function = PKCS11.getInstance(libName, "C_GetFunctionList", null, false);			
			Method[] ms = PKCS11.class.getMethods();
			for (int i = 0; i < ms.length; i++){
				if (ms[i].getName().equals("getInstance")){
					method = ms[i];
					break;
				}
			}
			if (method == null){
				throw new P11Exception("Nie odnaleziono funkcji: \"PKCS11.getInstance\".");
			}
			int paramCount = method.getParameterTypes().length;
			Object[] params = null;
			if (paramCount == 4){
				params = new Object[4];
				params[0] = libName;
				params[1] = "C_GetFunctionList";
				params[2] = null;
				params[3] = Boolean.FALSE;
			}
			else if (paramCount == 3) {
				params = new Object[3];
				params[0] = libName;
				params[1] = null;
				params[2] = Boolean.FALSE;
			}
			else{
				throw new P11Exception("Nierozpoznana wersja biblioteki: \"sun.security.pkcs11.wrapper.PKCS11\".");
			}			
			function = (PKCS11)method.invoke(null, params);
			System.out.println("Załadowano bibliotekę: " + libName);
		}
		//catch (IOException e) {
		//	throw new P11Exception("Nie można odczytać pliku: \"" + libName + "\".");
		//}
		catch (Exception e) {
			//e.printStackTrace();
			throw new P11Exception("Nie można załadować biblioteki PKCS#11: \"" + libName + "\".");
		}		
	}
	
	
	
	/**
	 * Funkcja zwraca obiekt reprezentujący informacje o bibliotece
	 * @return Obiekt reprezentujący informacje o bibliotece
	 * @throws P11Exception Błędy PKCS#11
	 */
	public LibraryInfo getLibInfo() throws P11Exception{
		try{
			CK_INFO info = function.C_GetInfo();
			return new LibraryInfo(info);
			
		}catch (Exception e) {
			throw new P11Exception("Nie można pobrać informacji o bibliotece.");
		}		
	}
	
	
	/**
	 * Funkcja zwraca listę urządzeń wykrytych przez bibliotekę
	 * @return Lista urządzeń wykrytych przez bibliotekę
	 * @throws P11Exception Błędy PKCS#11
	 */
	public List<Token> getTokens() throws P11Exception{
		try{
			long slots[] = function.C_GetSlotList(true);
			List<Token> tokens = new ArrayList<Token>();			
			for (int i = 0; i < slots.length; i++){
				Token token = new Token(this, slots[i]);
				tokens.add(token);					
			}			
			return tokens;			
		}
		catch (P11Exception e) {
			throw e;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new P11Exception("Nie można odczytać informacji o slotach.");
		}
		
	}	
	
	
	//dla testów
	public static void main(String[] args) {
		try{	
					
			Library lib = new Library("aetpkss1.dll");
						
			LibraryInfo info = lib.getLibInfo();
			System.out.println(info);
			
			List<Token> tokens = lib.getTokens();
			for (Token token : tokens){
				System.out.println(token.getTokenInfo().toString());
				List<P11RsaPubKey> keys = token.getRsaPublicKeys();				
				for (P11RsaPubKey key : keys){
					System.out.println(key); 
				}
				
				Map<X509Certificate, String> certs = token.getCertificates();
				for (X509Certificate cert : certs.keySet()){
					System.out.println(cert);
					token.login("1111", false);
					try{
						P11X509Certificate p11Cert = token.findCertificate(cert.getEncoded());
						P11RsaPrivKey privKey = token.findRsaPrivateKey(p11Cert);
						privKey.sign("ala ma kota".getBytes());						
						
					}
					finally{
						token.logout();
					}
					
				}
				
				
				//P11X509Certificate[] certs = tokens[i].getX509Certificates();
				//for (int j = 0; j < certs.length; j++){
				//	System.out.println(certs[j].toString());
				//	tokens[i].login("1111", false);
				//	P11RsaPrivKey key = tokens[i].findRsaPrivateKey(certs[j]);
				//	if (key != null){
				//		byte[] toSign = new byte[128 - 11];
				//		
				//		Random rnd = new Random();
				//		rnd.nextBytes(toSign);						
				//		byte[] signature = key.sign(toSign);
				//	}
				//	tokens[i].logout();
				//}	
			
			}
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}		
	}

}
