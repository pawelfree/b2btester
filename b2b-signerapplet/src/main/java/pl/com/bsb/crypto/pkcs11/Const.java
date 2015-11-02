package pl.com.bsb.crypto.pkcs11;

/**
 * @author bkubacki
 *	Klasa zawiera stałe PKCS#11
 */
public class Const {
	
		//user
		public static final long CKU_SO				= 0;
		public static final long CKU_USER				= 1;
		public static final long CKU_CONTEXT_SPECIFIC	= 2;
		
		
		//session
		public static final long CKS_RO_PUBLIC_SESSION	= 0;
		public static final long CKS_RO_USER_FUNCTIONS	= 1;
		public static final long CKS_RW_PUBLIC_SESSION	= 2;
		public static final long CKS_RW_USER_FUNCTIONS	= 3;
		public static final long CKS_RW_SO_FUNCTIONS	= 4;
		
		public static final long CKF_RW_SESSION		= 2;
		public static final long CKF_SERIAL_SESSION 	= 4;
		
		//objects
		public static final long CKO_DATA              = 0x00000000;
		public static final long CKO_CERTIFICATE       = 0x00000001;
		public static final long CKO_PUBLIC_KEY        = 0x00000002;
		public static final long CKO_PRIVATE_KEY       = 0x00000003;
		public static final long CKO_SECRET_KEY        = 0x00000004;
		//public static final long CKO_HW_FEATURE        = 0x00000005;
		//public static final long CKO_DOMAIN_PARAMETERS = 0x00000006;
		//public static final long CKO_MECHANISM         = 0x00000007;
		//public static final long CKO_VENDOR_DEFINED    = 0x80000000;
		
		
		public static final long CKF_ARRAY_ATTRIBUTE    		  = 0x40000000;
		
		//attribute
		public static final long CKA_CLASS                       = 0x00000000;
		public static final long CKA_TOKEN                       = 0x00000001;
		public static final long CKA_PRIVATE                     = 0x00000002;
		public static final long CKA_LABEL                       = 0x00000003;
		public static final long CKA_APPLICATION                 = 0x00000010;
		public static final long CKA_VALUE                       = 0x00000011;
		public static final long CKA_OBJECT_ID                   = 0x00000012;
		public static final long CKA_CERTIFICATE_TYPE            = 0x00000080;
		public static final long CKA_ISSUER                      = 0x00000081;
		public static final long CKA_SERIAL_NUMBER               = 0x00000082;
		public static final long CKA_AC_ISSUER                   = 0x00000083;
		public static final long CKA_OWNER                       = 0x00000084;
		public static final long CKA_ATTR_TYPES                  = 0x00000085;
		public static final long CKA_TRUSTED                     = 0x00000086;
		public static final long CKA_CERTIFICATE_CATEGORY        = 0x00000087;
		public static final long CKA_JAVA_MIDP_SECURITY_DOMAIN   = 0x00000088;
		public static final long CKA_URL                         = 0x00000089;
		public static final long CKA_HASH_OF_SUBJECT_PUBLIC_KEY  = 0x0000008A;
		public static final long CKA_HASH_OF_ISSUER_PUBLIC_KEY   = 0x0000008B;
		public static final long CKA_CHECK_VALUE                 = 0x00000090;
		public static final long CKA_KEY_TYPE                    = 0x00000100;
		public static final long CKA_SUBJECT                     = 0x00000101;
		public static final long CKA_ID                          = 0x00000102;
		public static final long CKA_SENSITIVE                   = 0x00000103;
		public static final long CKA_ENCRYPT                     = 0x00000104;
		public static final long CKA_DECRYPT                     = 0x00000105;
		public static final long CKA_WRAP                        = 0x00000106;
		public static final long CKA_UNWRAP                      = 0x00000107;
		public static final long CKA_SIGN                        = 0x00000108;
		public static final long CKA_SIGN_RECOVER                = 0x00000109;
		public static final long CKA_VERIFY                      = 0x0000010A;
		public static final long CKA_VERIFY_RECOVER              = 0x0000010B;
		public static final long CKA_DERIVE                      = 0x0000010C;
		public static final long CKA_START_DATE                  = 0x00000110;
		public static final long CKA_END_DATE                    = 0x00000111;
		public static final long CKA_MODULUS                     = 0x00000120;
		public static final long CKA_MODULUS_BITS                = 0x00000121;
		public static final long CKA_PUBLIC_EXPONENT             = 0x00000122;
		public static final long CKA_PRIVATE_EXPONENT            = 0x00000123;
		public static final long CKA_PRIME_1                     = 0x00000124;
		public static final long CKA_PRIME_2                     = 0x00000125;
		public static final long CKA_EXPONENT_1                  = 0x00000126;
		public static final long CKA_EXPONENT_2                  = 0x00000127;
		public static final long CKA_COEFFICIENT                 = 0x00000128;
		public static final long CKA_PRIME                       = 0x00000130;
		public static final long CKA_SUBPRIME                    = 0x00000131;
		public static final long CKA_BASE                        = 0x00000132;
		public static final long CKA_PRIME_BITS                  = 0x00000133;
		public static final long CKA_SUBPRIME_BITS               = 0x00000134;
		public static final long CKA_VALUE_BITS                  = 0x00000160;
		public static final long CKA_VALUE_LEN                   = 0x00000161;
		public static final long CKA_EXTRACTABLE                 = 0x00000162;
		public static final long CKA_LOCAL                       = 0x00000163;
		public static final long CKA_NEVER_EXTRACTABLE           = 0x00000164;
		public static final long CKA_ALWAYS_SENSITIVE            = 0x00000165;
		public static final long CKA_KEY_GEN_MECHANISM           = 0x00000166;
		public static final long CKA_MODIFIABLE                  = 0x00000170;
		public static final long CKA_ECDSA_PARAMS                = 0x00000180;
		public static final long CKA_EC_PARAMS                   = 0x00000180;
		public static final long CKA_EC_POINT                    = 0x00000181;
		public static final long CKA_SECONDARY_AUTH              = 0x00000200;/* Deprecated */
		public static final long CKA_AUTH_PIN_FLAGS              = 0x00000201; /* Deprecated */
		public static final long CKA_ALWAYS_AUTHENTICATE         = 0x00000202;
		public static final long CKA_WRAP_WITH_TRUSTED           = 0x00000210;
		public static final long CKA_WRAP_TEMPLATE      		  = (CKF_ARRAY_ATTRIBUTE|0x00000211);
		public static final long CKA_UNWRAP_TEMPLATE    		  = (CKF_ARRAY_ATTRIBUTE|0x00000212);
		public static final long CKA_HW_FEATURE_TYPE             = 0x00000300;
		public static final long CKA_RESET_ON_INIT               = 0x00000301;
		public static final long CKA_HAS_RESET                   = 0x00000302;
		public static final long CKA_PIXEL_X                     = 0x00000400;
		public static final long CKA_PIXEL_Y                     = 0x00000401;
		public static final long CKA_RESOLUTION                  = 0x00000402;
		public static final long CKA_CHAR_ROWS                   = 0x00000403;
		public static final long CKA_CHAR_COLUMNS                = 0x00000404;
		public static final long CKA_COLOR                       = 0x00000405;
		public static final long CKA_BITS_PER_PIXEL              = 0x00000406;
		public static final long CKA_CHAR_SETS                   = 0x00000480;
		public static final long CKA_ENCODING_METHODS            = 0x00000481;
		public static final long CKA_MIME_TYPES                  = 0x00000482;
		public static final long CKA_MECHANISM_TYPE              = 0x00000500;
		public static final long CKA_REQUIRED_CMS_ATTRIBUTES     = 0x00000501;
		public static final long CKA_DEFAULT_CMS_ATTRIBUTES      = 0x00000502;
		public static final long CKA_SUPPORTED_CMS_ATTRIBUTES    = 0x00000503;
		public static final long CKA_ALLOWED_MECHANISMS 		  = (CKF_ARRAY_ATTRIBUTE|0x00000600);
		public static final long CKA_VENDOR_DEFINED              = 0x80000000;
		
		//key type
		public static final long CKK_RSA            = 0x00000000;
		public static final long CKK_DSA            = 0x00000001;
		public static final long CKK_DH             = 0x00000002;
		public static final long CKK_ECDSA  		 = 0x00000003;
		public static final long CKK_EC             = 0x00000003;
		public static final long CKK_X9_42_DH       = 0x00000004;
		public static final long CKK_KEA            = 0x00000005;
		public static final long CKK_GENERIC_SECRET = 0x00000010;
		public static final long CKK_RC2            = 0x00000011;
		public static final long CKK_RC4            = 0x00000012;
		public static final long CKK_DES            = 0x00000013;
		public static final long CKK_DES2           = 0x00000014;
		public static final long CKK_DES3           = 0x00000015;
		public static final long CKK_CAST           = 0x00000016;
		public static final long CKK_CAST3          = 0x00000017;
		public static final long CKK_CAST5          = 0x00000018;
		public static final long CKK_CAST128        = 0x00000018;
		public static final long CKK_RC5            = 0x00000019;
		public static final long CKK_IDEA           = 0x0000001A;
		public static final long CKK_SKIPJACK       = 0x0000001B;
		public static final long CKK_BATON          = 0x0000001C;
		public static final long CKK_JUNIPER        = 0x0000001D;
		public static final long CKK_CDMF           = 0x0000001E;
		public static final long CKK_AES            = 0x0000001F;
		public static final long CKK_BLOWFISH       = 0x00000020;
		public static final long CKK_TWOFISH        = 0x00000021;
		public static final long CKK_VENDOR_DEFINED = 0x80000000;
		
		//mechanism
		public static final long CKM_RSA_PKCS_KEY_PAIR_GEN          = 0x00000000;
		public static final long CKM_RSA_PKCS                       = 0x00000001;
		public static final long CKM_RSA_9796                       = 0x00000002;
		public static final long CKM_RSA_X_509                      = 0x00000003;
		public static final long CKM_MD2_RSA_PKCS                   = 0x00000004;
		public static final long CKM_MD5_RSA_PKCS                   = 0x00000005;
		public static final long CKM_SHA1_RSA_PKCS                  = 0x00000006;
		public static final long CKM_RIPEMD128_RSA_PKCS             = 0x00000007;
		public static final long CKM_RIPEMD160_RSA_PKCS             = 0x00000008;
		public static final long CKM_RSA_PKCS_OAEP                  = 0x00000009;
		public static final long CKM_RSA_X9_31_KEY_PAIR_GEN         = 0x0000000A;
		public static final long CKM_RSA_X9_31                      = 0x0000000B;
		public static final long CKM_SHA1_RSA_X9_31                 = 0x0000000C;
		public static final long CKM_RSA_PKCS_PSS                   = 0x0000000D;
		public static final long CKM_SHA1_RSA_PKCS_PSS              = 0x0000000E;
		public static final long CKM_DSA_KEY_PAIR_GEN               = 0x00000010;
		public static final long CKM_DSA                            = 0x00000011;
}
