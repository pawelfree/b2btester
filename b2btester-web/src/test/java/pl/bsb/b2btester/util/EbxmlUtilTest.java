package pl.bsb.b2btester.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author aszatkowski
 */
public class EbxmlUtilTest {

    private String balanceRequest3 = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/Pgo8QmFsYW5jZVJlcXVlc3QgeG1sbnM6eHNpPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYS1pbnN0YW5jZSIgeHNpOm5vTmFtZXNwYWNlU2NoZW1hTG9jYXRpb249IjMuMC9CYWxhbmNlUmVxdWVzdC54c2QiID4KICAgIDxIZWFkZXI+CiAgICAgICAgPElzc3VlRGF0ZVRpbWU+MjAxMi0wMS0xMVQwNDozNTo0MzwvSXNzdWVEYXRlVGltZT4KICAgICAgICA8RG9jdW1lbnRJZGVudGlmaWVyPkRPS19NS3gwMV9CUl8wMDE8L0RvY3VtZW50SWRlbnRpZmllcj4KICAgICAgICA8U2VuZGVySWRlbnRpZmllcj5BUk08L1NlbmRlcklkZW50aWZpZXI+CiAgICAgICAgPFJlY2VpdmVySWRlbnRpZmllcj4xMTM8L1JlY2VpdmVySWRlbnRpZmllcj4KICAgIDwvSGVhZGVyPgogICAgPEJhbGFuY2VDcml0ZXJpYT4KICAgICAgICA8Q2FsY3VsYXRpb25EYXRlPjIwMTItMDEtMTE8L0NhbGN1bGF0aW9uRGF0ZT4KICAgICAgICA8Q3VzdG9tZXJBY2NvdW50PgogICAgICAgICAgICA8QWNjb3VudElkZW50aWZpZXIgSWRlbnRpZmljYXRpb25TY2hlbWVOYW1lPSJOUkIiPjAyMTEzMDEwNjI4MDAwMDAwMDAwMDI4MTczPC9BY2NvdW50SWRlbnRpZmllcj4KICAgICAgICA8L0N1c3RvbWVyQWNjb3VudD4KICAgIDwvQmFsYW5jZUNyaXRlcmlhPgo8L0JhbGFuY2VSZXF1ZXN0Pg==";
    private String balanceRequest3docId = "DOK_MKx01_BR_001";
    private String balanceRequest3MsgVer = "3.0";
    private String balanceRequest3MsgType = "BalanceRequest";
    private String balanceRequest1 = "PEJhbGFuY2VSZXF1ZXN0IHhtbG5zOnhzaT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEtaW5zdGFuY2UiIHhzaTpub05hbWVzcGFjZVNjaGVtYUxvY2F0aW9uPSJCYWxhbmNlUmVxdWVzdC54c2QiPgogIDxIZWFkZXI+CiAgICA8SXNzdWVEYXRlVGltZT4yMDEzLTA1LTE0VDEwOjAxOjQzPC9Jc3N1ZURhdGVUaW1lPgogICAgPERvY3VtZW50SWRlbnRpZmllcj4zNDA4NzE5PC9Eb2N1bWVudElkZW50aWZpZXI+CiAgICA8U2VuZGVySWRlbnRpZmllcj5UUkVaT1I8L1NlbmRlcklkZW50aWZpZXI+CiAgICA8UmVjZWl2ZXJJZGVudGlmaWVyPlpTSy1DPC9SZWNlaXZlcklkZW50aWZpZXI+CiAgPC9IZWFkZXI+CiAgPEJhbGFuY2VDcml0ZXJpYT4KICAgIDxDYWxjdWxhdGlvbkRhdGU+MjAxMy0wNS0xNDwvQ2FsY3VsYXRpb25EYXRlPgogICAgPEN1c3RvbWVyQWNjb3VudD4KICAgICAgPEFjY291bnRJZGVudGlmaWVyIElkZW50aWZpY2F0aW9uU2NoZW1lTmFtZT0iTlJCIj40ODEwMTAxMDEwMDA3NTkxMjIyNjEwMDAwMDwvQWNjb3VudElkZW50aWZpZXI+CiAgICA8L0N1c3RvbWVyQWNjb3VudD4KICA8L0JhbGFuY2VDcml0ZXJpYT4KPC9CYWxhbmNlUmVxdWVzdD4K";
    private String balanceRequest1MsgVer = "1.0";
    private String balanceRequest1MsgReceiver = "113";
    private String balanceRequest1MsgSender = "ARM";
    
    public EbxmlUtilTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws FileNotFoundException, IOException {
// jesli chcesz wyprodukować plik w postaci base64 do załączenia w teście to wyłacz komentarz i skopiuj napis z konsoli
//        File file = new File("src/test/resources/BalanceREquest.xml");
//        FileInputStream fin = new FileInputStream(file.getAbsolutePath());
//        content = new byte[(int) file.length()];
//        try {
//            fin.read(content);
//        } finally {
//            fin.close();
//        }
//        System.err.println(Base64.encode(content));
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetMsgVer1() throws Exception {
        String result = EbxmlUtil.getMessageVersion(Base64.decode(balanceRequest1));
        assertEquals(balanceRequest1MsgVer, result);
    }

    /**
     * Test of getDocumentIdentifier method, of class EbxmlUtil.
     */
    @Test
    public void testGetDocumentIdentifier() throws Exception {
        String result = EbxmlUtil.getDocumentIdentifier(Base64.decode(balanceRequest3));
        assertEquals(balanceRequest3docId, result);
    }

    /**
     * Test of getMessgeType method, of class EbxmlUtil.
     */
    @Test
    public void testGetMessgeType() throws Exception {
        String result = EbxmlUtil.getMessgeType(Base64.decode(balanceRequest3));
        assertEquals(balanceRequest3MsgType, result);
    }

    /**
     * Test of getMessageVersion method, of class EbxmlUtil.
     */
    @Test
    public void testGetMessageVersion() throws Exception {
        String result = EbxmlUtil.getMessageVersion(Base64.decode(balanceRequest3));
        assertEquals(balanceRequest3MsgVer, result);
    }

    @Test
    public void testGetReceiverIdentifier() throws Exception {
        String result = EbxmlUtil.getMessageReceiver(Base64.decode(balanceRequest3));
        assertEquals(balanceRequest1MsgReceiver, result);
    }
    
    @Test
    public void testGetSenderIdentifier() throws Exception {
        String result = EbxmlUtil.getMessageSender(Base64.decode(balanceRequest3));
        assertEquals(balanceRequest1MsgSender, result);
    }    
}
